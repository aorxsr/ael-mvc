/* Copyright (c) 2019, aorxsr (aorxsr@163.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ael.http.body;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import lombok.extern.slf4j.Slf4j;
import org.ael.commons.StringUtils;
import org.ael.constant.ContentType;
import org.ael.constant.HttpConstant;
import org.ael.http.inter.Request;
import org.ael.http.inter.Response;
import org.ael.http.inter.Session;
import org.ael.http.WebContent;
import org.ael.constant.EnvironmentConstant;
import org.ael.template.ModelAndView;
import org.ael.template.give.ReadStaticResources;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @Author: aorxsr
 * @Date: 2020/1/29
 */
@Slf4j
public class SimpleBodyWrite implements BodyWrite {

    private WebContent webContent;
    private Response response;
    private Request request;

    public SimpleBodyWrite(WebContent webContent) {
        this.webContent = webContent;
        this.response = webContent.getResponse();
        this.request = webContent.getRequest();

        Session session = request.getSession();
        if (null != session) {
            // 获取Cookie
            if (request.isASESSION()) {
                Cookie cookie = request.getCookie(WebContent.ael.getEnvironment().getString(EnvironmentConstant.SESSION_KEY, EnvironmentConstant.DEFAULT_SESSION_KEY));
                if (null == cookie) {
                    response.setCookie(new DefaultCookie(EnvironmentConstant.DEFAULT_SESSION_KEY, session.getId()));
                } else {
                    response.setCookie(cookie);
                }
            }
        }
    }

    @Override
    public FullHttpResponse onView(ViewBody body) throws IOException {
        FullHttpResponse defaultFullHttpResponse = WebContent.ael.getAelTemplate().renderResponse(body.getModelAndView(), this.webContent);
        appendResponseCookie(response.getNettyCookies(), defaultFullHttpResponse);
        if (StringUtils.isEmpty(response.getContentType())) {
            response.setContentType(HttpConstant.TEXT_HTML);
        }

        response.getHeaders().forEach((k, v) -> defaultFullHttpResponse.headers().set(k, v));
        defaultFullHttpResponse.headers().set(HttpConstant.DATE, new Date());
        return defaultFullHttpResponse;
    }

    @Override
    public FullHttpResponse onByteBuf(Object byteBuf) {
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus()));
        response.getHeaders().forEach((k, v) -> defaultFullHttpResponse.headers().set(k, v));
        ChannelHandlerContext ctx = webContent.getCtx();
        // Promise是做数据过程中的数据保证
        ctx.write(defaultFullHttpResponse, ctx.voidPromise());
        ChannelFuture future = ctx.writeAndFlush(byteBuf);
        if (!request.isKeepAlive()) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        return null;
    }

    @Override
    public FullHttpResponse onByteBuf(ByteBuf byteBuf) {
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(response.getStatus()), byteBuf);
        appendResponseCookie(response.getNettyCookies(), defaultFullHttpResponse);
        response.getHeaders().forEach((k, v) -> defaultFullHttpResponse.headers().set(k, v));
        defaultFullHttpResponse.headers().set(HttpConstant.DATE, new Date());
        return defaultFullHttpResponse;
    }

    @Override
    public FullHttpResponse onStatics(ByteBuf byteBuf) {
        return onByteBuf(byteBuf);
    }

    private void appendResponseCookie(Set<Cookie> cookieSet, FullHttpResponse response) {
        cookieSet.forEach(cookie -> response.headers().add(HttpConstant.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie)));
    }
}
