package org.ael.template.give;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.ael.Ael;
import org.ael.Environment;
import org.ael.commons.StreamUtils;
import org.ael.constant.ContentType;
import org.ael.exception.ViewNotFoundException;
import org.ael.http.WebContent;
import org.ael.http.body.ByteBufBody;
import org.ael.constant.EnvironmentConstant;
import org.ael.template.AelTemplate;
import org.ael.template.ModelAndView;

import java.io.*;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * default impl template
 *
 * @Author: aorxsr
 * @Date: 2019/8/22 19:51
 */
public class DefaultTemplate implements AelTemplate {

    @Override
    public WebContent render(ModelAndView modelAndView, WebContent webContent) {
        String view = modelAndView.getView();
        InputStream resourceAsStream = StreamUtils.getClassPathFile(view);
        if (!new File(view).exists() || null == resourceAsStream) {
            throw new ViewNotFoundException(view + " view not found ... ");
        } else {
            try {
                webContent.getRequest().setASESSION(false);
                InputStream inputStream = StreamUtils.convertToByteArrayInputStream(resourceAsStream);
                webContent.getResponse().addHeader("Content-Length", inputStream.available());
                ByteBuf buffer = Unpooled.buffer();
                buffer.writeBytes(inputStream, inputStream.available());
                webContent.getResponse().write(new ByteBufBody(buffer));
                String suffix = view.substring(view.lastIndexOf(".") + 1);
                webContent.getResponse().setContentType(ContentType.get(suffix));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != resourceAsStream) {
                    try {
                        resourceAsStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return webContent;
        }
    }

    @Override
    public FullHttpResponse renderResponse(ModelAndView modelAndView, WebContent webContent) throws ViewNotFoundException {
        // 笑死了,竟然有人从这里访问,我操,我之前写的都没了,还得重新写
        DefaultFullHttpResponse defaultFullHttpResponse = null;
        try {
            ReadStaticResources fileContext = readFileContext(modelAndView.getView());
            ByteBuf byteBuf = fileContext.getByteBuf();
            if (null != byteBuf) {
                defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1,HttpResponseStatus.OK, byteBuf);
            }
        } catch (IOException e) {
            throw new ViewNotFoundException(modelAndView.getView() + " view not found ... ");
        }
        return defaultFullHttpResponse;
    }

    private static String PREFIX = "";
    private static String SUFFIX = "";

    private final static ClassLoader DEFAULT_CLASS_LOADER = DefaultTemplate.class.getClassLoader();

    @Override
    public void init(Ael ael) {
        Environment environment = ael.getEnvironment();

        PREFIX = environment.getString(EnvironmentConstant.TEMPLATE_PRIFIX, EnvironmentConstant.DEFAULT_TEMPLATE_PREFIX);
        SUFFIX = environment.getString(EnvironmentConstant.TEMPLATE_SUFFIX, EnvironmentConstant.DEFAULT_TEMPLATE_SUFFIX);
    }

    @Override
    public ReadStaticResources readFileContext(String view) throws ViewNotFoundException, IOException {
        InputStream resourceAsStream = StreamUtils.getClassPathFile(PREFIX + view + SUFFIX);
        if (null == resourceAsStream) {
            throw new ViewNotFoundException(view + " view not found ... ");
        } else {
            return new ReadStaticResources(resourceAsStream);
        }
    }

    private String readFile(InputStream inStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }


}
