package org.ael.log;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class LogFile implements Runnable, Closeable {
	Queue<String> querys = new ConcurrentLinkedQueue();
	private BufferedWriter out;
	private File file;
	private final String name;
	private final String dir;
	private final int maxSize;
	private volatile boolean isRuning;

	public LogFile(String name, String dir, int maxSize) {
		this.name = name;
		this.dir = dir;
		this.maxSize = maxSize;
		this.isRuning = false;
		this.file = new File(dir, name + ".log");
		if (!this.file.getParentFile().exists()) {
			this.file.getParentFile().mkdir();
		}

		try {
			this.file.createNewFile();
			this.out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file, true), "UTF-8"));
			this.isRuning = true;
		} catch (IOException var5) {
			var5.printStackTrace();
		}

	}

	public void close() throws IOException {
	}

	public void run() {
		while (true) {
			if (this.isRuning && !this.querys.isEmpty()) {
				this.write();
			} else {
				try {
					TimeUnit.MILLISECONDS.sleep(100L);
				} catch (InterruptedException var2) {
					var2.printStackTrace();
				}
			}
		}
	}

	private void write() {
		if (!this.querys.isEmpty()) {
			;
		}
	}
}
