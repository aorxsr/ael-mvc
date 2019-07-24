package org.ael.mvc;

import org.ael.mvc.server.Server;
import org.ael.mvc.server.netty.NettyServer;

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

}
