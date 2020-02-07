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
