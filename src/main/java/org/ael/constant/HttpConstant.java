package org.ael.constant;

import io.netty.util.AsciiString;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 13:37
 */
public interface HttpConstant {

    String WELL = "#";

    String ACCEPT = "Accept";
    String USER_AGENT = "User-Agent";
    String ACCEPT_ENCODING = "Accept-Encoding";
    String COOKIE = "Cookie";
    String HOST = "Host";

    AsciiString SET_COOKIE = AsciiString.cached("Set-Cookie");

    /*****response********/

    String CONTENT_TYPE = "Content-Type";
    String DATE = "Date";

    String TEXT_PLAIN = "text/plain;charset=utf-8";
    String TEXT_XML = "text/xml";
    String TEXT_HTML = "text/html";
    String APPLICATION_JSON = "application/json;charset=utf-8";

}
