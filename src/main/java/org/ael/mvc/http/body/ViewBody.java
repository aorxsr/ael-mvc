package org.ael.mvc.http.body;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Getter;

import java.io.IOException;

/**
 * @Author: aorxsr
 * @Date: 2019/7/29 13:54
 */
public class ViewBody implements Body {

	@Getter
	private String url;

	public static ViewBody of(String htmlUrl) {
		ViewBody viewBody = new ViewBody();
		viewBody.url = htmlUrl;
		return viewBody;
	}

	@Override
	public FullHttpResponse body(BodyWrite write) throws IOException {
		return write.onView(this);
	}

}
