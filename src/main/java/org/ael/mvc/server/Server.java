package org.ael.mvc.server;

import org.ael.mvc.Ael;

/**
 * @Author: aorxsr
 * @Date: 2019/7/16 18:19
 */
public interface Server {

	void start(Ael ael) throws InterruptedException;

	void stop();

}
