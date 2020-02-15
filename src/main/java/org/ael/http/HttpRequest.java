package org.ael.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import org.ael.constant.HttpConstant;
import org.ael.http.inter.Request;
import org.ael.http.inter.Session;
import org.ael.http.session.SessionHandler;
import org.ael.constant.EnvironmentConstant;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: aorxsr
 * @Date: 2019/7/18 18:26
 */
public class HttpRequest implements Request {

    private final static SessionHandler SESSION_HANDLER = new SessionHandler(WebContent.ael.getSessionManager());

    // Disk if size exceed
    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private final static String GZIP = "gzip";
    private final static String WEN = "?";

    private String uri;
    private String url;
    private String host;
    private String method;
    private boolean multipart;
    private ByteBuf body = null;

    private boolean asession = true;
    private boolean keepAlive;
    private String remoteAddress;

    private Session session;

    private Map<String, String> headers = new HashMap<>(8);
    private Map<String, List<String>> parameters = new HashMap<>(8);
    private Map<String, Cookie> cookies = new HashMap<>(8);

    private io.netty.handler.codec.http.HttpRequest nettyRequest;
    private Queue<HttpContent> contents = new LinkedList<>();

    private Map<String, MultiPartFile> multiPartFileMap = new ConcurrentHashMap<>(8);

    public void setNettyRequest(io.netty.handler.codec.http.HttpRequest httpRequest) {
        this.nettyRequest = httpRequest;
    }

    public void appendHttpContent(HttpContent content) {
        this.contents.add(content.retain());
    }

    /**
     * init request
     *
     * @param remoteAddress
     */
    public void initRequest(String remoteAddress) {
        // url
        url = nettyRequest.uri();
        // uri init
        int wenIndex = this.url.indexOf('?');
        this.uri = wenIndex < 0 ? this.url : this.url.substring(0, wenIndex);
        // remoteAddress
        this.remoteAddress = remoteAddress;
        // headers
        nettyRequest.headers().forEach(header -> headers.put(header.getKey(), header.getValue()));
        // Method Type
        method = nettyRequest.method().name();
        // keepAlive
        keepAlive = HttpUtil.isKeepAlive(nettyRequest);
        // cookie init
        String cookieString = headers.get(HttpConstant.COOKIE);
        if (!StringUtil.isNullOrEmpty(cookieString)) {
            ServerCookieDecoder.LAX.decode(cookieString).forEach(cookie -> cookies.put(cookie.name(), cookie));
        }
        // parameter init
        if (url.contains(WEN)) {
            Map<String, List<String>> parameters =
                    new QueryStringDecoder(url, CharsetUtil.UTF_8).parameters();
            if (null != parameters) {
                this.parameters.putAll(parameters);
            }
        }

        // session
        session = SESSION_HANDLER.getSession(getCookieValue(WebContent.ael.getEnvironment().getString(EnvironmentConstant.SESSION_KEY, EnvironmentConstant.DEFAULT_SESSION_KEY)), remoteAddress);

        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            return;
        }

        // Not Get Method
        if (!contents.isEmpty()) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, nettyRequest);
            if (decoder.isMultipart()) {
                this.multipart = true;
                for (HttpContent content : contents) {
                    decoder.offer(content);
                    //
                    readHttpDataChunkByChunk(decoder);
                    // -1 释放
                    content.release();
                }
            } else {
                notMultipart();
            }
        }
    }

    /**
     * 从decoder中读出数据，写入临时对象，然后写入...哪里？
     * 这个封装主要是为了释放临时对象
     * https://blog.csdn.net/arctan90/article/details/51292120
     */
    private void readHttpDataChunkByChunk(HttpPostRequestDecoder decoder) {
        try {
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    try {
                        // new value
                        // Attribute就是form表单里带的各种 name= 的属性
                        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                            Attribute attribute = (Attribute) data;
                            String name = attribute.getName();
                            String value = attribute.getValue();
                            List<String> values = new ArrayList<>();
                            if (parameters.containsKey(name)) {
                                values = parameters.get(name);
                                values.add(value);
                            }
                            values.add(value);
                            this.parameters.put(name, values);
                        } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                            FileUpload fileUpload = (FileUpload) data;
                            // Determine whether all uploads are completed
                            if (fileUpload.isCompleted()) {
                                // fileUpload.isInMemory();// tells if the file is in Memory
                                // or on File
                                // fileUpload.renameTo(dest); // enable to move into another
                                // File dest
                                // decoder.removeFileUploadFromClean(fileUpload); //remove
                                // the File of to delete file
                                multiPartFileMap.put(fileUpload.getName(), MultiPartFile.builder().file(fileUpload).build());
                            }
                        }
                    } finally {
                        data.release();
                    }
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException | IOException e1) {
            // end
        }
    }

    private void notMultipart() {
        ArrayList<ByteBuf> byteBufs = new ArrayList<>(contents.size());
        this.contents.forEach(httpContent -> byteBufs.add(httpContent.content().copy()));
        if (!byteBufs.isEmpty())
            this.body = Unpooled.copiedBuffer(byteBufs.toArray(new ByteBuf[0]));
    }


    @Override
    public String getMethod() {
        if (StringUtil.isNullOrEmpty(method)) {
            this.method = nettyRequest.method().name();
        }
        return method;
    }

    @Override
    public String getUri() {
        if (StringUtil.isNullOrEmpty(uri)) {
            uri = nettyRequest.uri();
        }
        return uri;
    }

    @Override
    public String getHost() {
        if (StringUtil.isNullOrEmpty(host)) {
            if (headers.containsKey(HttpConstant.HOST)) {
                host = headers.get(HttpConstant.HOST);
            }
        }
        return host;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Object getParameter(String name) {
        if (parameters.containsKey(name)) {
            return parameters.get(name);
        } else {
            return null;
        }
    }

    @Override
    public Map<String, List<String>> getParameters() {
        return null;
    }

    @Override
    public Object getPathParam(String name) {
        if (parameters.containsKey(name)) {
            return parameters.get(name);
        } else {
            return null;
        }
    }

    @Override
    public MultiPartFile getMultiPartFile(String name) {
        return multiPartFileMap.containsKey(name) ? multiPartFileMap.get(name) : null;
    }

    @Override
    public boolean isUseGZIP() {
        if (WebContent.ael.getEnvironment().getBoolean(EnvironmentConstant.HTTP_ZIP, false)) {
            return false;
        }
        // 判断是否有 gzip 标识
        String acceptEncoding = headers.get(HttpConstant.ACCEPT_ENCODING);
        if (StringUtil.isNullOrEmpty(acceptEncoding)) {
            return false;
        }
        return acceptEncoding.contains(GZIP);
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public boolean isASESSION() {
        return asession;
    }

    @Override
    public void setASESSION(boolean asession) {
        this.asession = asession;
    }

    @Override
    public Map<String, Cookie> cookies() {
        return cookies;
    }

    @Override
    public Request setCookie(Cookie cookie) {
        cookies.put(cookie.name(), cookie);
        return this;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public boolean isKeepAlive() {
        return keepAlive;
    }

    @Override
    public boolean isMultipart() {
        return this.multipart;
    }

    @Override
    public ByteBuf body() {
        return this.body;
    }

}
