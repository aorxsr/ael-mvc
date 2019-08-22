package org.ael.mvc;

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
	public void test1() throws InstantiationException, IllegalAccessException {
		new Ael()
				.get("/", handler -> handler.getResponse().text(handler.getRequest().cookies().toString()))
				.get("/get", handler -> handler.getResponse().text(handler.getRequest().cookies().toString()))
				.get("/a", handler -> handler.getResponse().text(handler.getRequest().cookies().toString()))
				.start(ServerTest.class);
	}

}
