package org.ael.mvc.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: aorxsr
 * @Date: 2019/8/24 11:05
 */
public interface ContentType {

	String APPLICATION = "application/octet-stream";

	Map<String, String> contentTypes = new HashMap<String, String>() {{
		put("mp4", "audio/mp4");
		put("mp3", "audio/mpeg");

		put("bmp", "image/bmp");
		put("gif", "image/gif");
		put("jpeg", "image/jpeg");
		put("jpg", "image/jpeg");

		put("png", "image/png");
		put("svg", "image/svg+xml");

		put("tif", "image/tiff");
		put("ico", "image/x-icon");

		put("js", "application/javascript");
		put("json", "application/json");

		put("doc", "application/msword");
		put("xls", "application/vnd.ms-excel");
		put("ppt", "application/vnd.ms-powerpoint");
		put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");

		put("mpeg", "video/mpeg");
		put("mpg", "video/mpeg");

		put("wmv", "video/x-ms-wmv");
		put("avi", "video/x-msvideo");

		put("atom", "application/xml");
		put("rdf", "application/xml");
		put("rss", "application/xml");
		put("xml", "application/xml");

		put("pdf", "application/pdf");
		put("ps", "application/postscript");
		put("zip", "application/zip");

		put("css", "text/css");
		put("html", "text/html");
		put("htm", "text/html");
		put("shtml", "text/html");
	}};

	/**
	 * @param ext
	 * @return
	 */
	static String get(String ext) {
		if (contentTypes.containsKey(ext)) {
			return contentTypes.get(ext);
		} else {
			return APPLICATION;
		}
	}

}
