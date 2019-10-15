package org.ael.mvc;

import org.ael.mvc.container.annotation.Injection;
import org.ael.mvc.exception.NotFoundException;
import org.ael.mvc.server.Server;
import org.ael.mvc.server.netty.NettyServer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * @Author: aorxsr
 * @Date: 2019/7/24 17:02
 */
public class ServerTest {

	private String a;

	public static void main(String[] args) throws InterruptedException {
		Ael ael = new Ael();

		Server server = new NettyServer();

		server.start(ael);
	}


	@Test
	public void test1() throws InstantiationException, IllegalAccessException {
		new Ael()
				.get("/param", handler -> handler.getRequest().getParameters().forEach((k, v) -> System.out.println(k + "\t" + v)))
				.get("/get", handler -> handler.getResponse().text(handler.getRequest().cookies().toString()))
				.get("/a", handler -> handler.getResponse().text(handler.getRequest().cookies().toString()))
				.get("/download", handler -> {
					try {
						handler.getResponse().download(new File("F:\\back.sql"), "back.sql");
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				})
//				.get("/index.html", handler -> handler.getResponse().html("/template/index.html"))
				.addResourcesMapping("/statics/", "/statics/")
				.addResourcesMapping("/view/", "/template/")
				.start(ServerTest.class);
	}

	@Test
	public void T() {
		Class<ServerTest> serverTestClass = ServerTest.class;

		Field[] fields = serverTestClass.getDeclaredFields();
		for (Field field : fields) {
			Injection injection = field.getDeclaredAnnotation(Injection.class);
			if (injection == null) {
				continue;
			}
			
			System.out.println(field.getName());
		}

	}

}
