package org.ael.mvc.template.give;

import org.ael.mvc.Ael;
import org.ael.mvc.Environment;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.constant.HttpConstant;
import org.ael.mvc.exception.ViewNotFoundException;
import org.ael.mvc.http.WebContent;
import org.ael.mvc.template.AelTemplate;
import org.ael.mvc.template.ModelAndView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * default impl template
 *
 * @Author: aorxsr
 * @Date: 2019/8/22 19:51
 */
public class DefaultTemplate implements AelTemplate {

	private static final ClassLoader classLoader = DefaultTemplate.class.getClassLoader();

	private static String PREFIX = "";
	private static String SUFFIX = "";

	@Override
	public WebContent render(ModelAndView modelAndView, WebContent webContent) throws ViewNotFoundException {
		String view = modelAndView.getView();
		InputStream resourceAsStream = classLoader.getResourceAsStream(PREFIX + view + SUFFIX);
		if (null == resourceAsStream) {
			resourceAsStream = this.getClass().getResourceAsStream(PREFIX + view + SUFFIX);
		}
		if (null == resourceAsStream) {
			throw new ViewNotFoundException(view + " view not found ... ");
		} else {
			String readFile = readFile(resourceAsStream);
			webContent.getResponse().text(readFile);
			webContent.getResponse().setContentType(HttpConstant.TEXT_HTML);
			return webContent;
		}
	}

	@Override
	public void init(Ael ael) {
		Environment environment = ael.getEnvironment();

		PREFIX = environment.getString(EnvironmentConstant.TEMPLATE_PRIFIX, EnvironmentConstant.DEFAULT_TEMPLATE_PREFIX);
		SUFFIX = environment.getString(EnvironmentConstant.TEMPLATE_SUFFIX, EnvironmentConstant.DEFAULT_TEMPLATE_SUFFIX);
	}

	@Override
	public String readFileContext(String view) throws ViewNotFoundException {
		InputStream resourceAsStream = classLoader.getResourceAsStream(PREFIX + view + SUFFIX);
		if (null == resourceAsStream) {
			resourceAsStream = this.getClass().getResourceAsStream(PREFIX + view + SUFFIX);
		}
		if (null == resourceAsStream) {
			throw new ViewNotFoundException(view + " view not found ... ");
		} else {
			return readFile(resourceAsStream);
		}
	}

	private String readFile(InputStream inStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		StringBuilder builder = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return builder.toString();
	}


}
