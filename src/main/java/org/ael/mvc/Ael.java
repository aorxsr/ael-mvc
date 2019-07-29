package org.ael.mvc;

import lombok.Getter;
import org.ael.mvc.route.RouteFunctionHandler;
import org.ael.mvc.route.RouteHandler;
import org.ael.mvc.server.Server;
import org.ael.mvc.server.netty.NettyServer;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
public class Ael {

	private Server server = new NettyServer();
	@Getter
	private Environment environment = new Environment();
	@Getter
	private RouteHandler routeHandler = new RouteHandler();

	public Ael start() {
		server.start(this);
		return this;
	}

	public void stop() {
		server.stop();
	}

	public Ael get(String url, RouteFunctionHandler handler) {
		routeHandler.addHandler("GET", url, handler);
		return this;
	}

}
