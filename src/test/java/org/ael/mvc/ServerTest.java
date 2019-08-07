package org.ael.mvc;

import org.ael.mvc.http.Response;
import org.ael.mvc.http.body.StringBody;
import org.ael.mvc.server.Server;
import org.ael.mvc.server.netty.NettyServer;
import org.junit.Test;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 17:02
 */
public class ServerTest {

	public static void main(String[] args) throws InterruptedException {
		Ael ael = new Ael();

		Server server = new NettyServer();

		server.start(ael);
	}


	@Test
	public void test1() {
		new Ael()
				.get("/", handler -> {
					handler.getResponse().text("ABCD");
					Response response = handler.getResponse();
					response.setCookie("cookieTest", "ffh");
				})
				.post("/post", handler -> handler.getResponse().json("{\"a\":\"aaa\"}"))
				.get("/a", handler -> {
					StringBuffer sub = new StringBuffer();
					handler.getRequest().getHeaders().forEach((k, v) -> sub.append(k + " " + v));
					handler.getResponse().text(sub.toString());
				})
				.start(ServerTest.class);
	}

}
