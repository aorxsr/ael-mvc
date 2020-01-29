package org.ael.mvc.template.give;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.ael.mvc.Ael;
import org.ael.mvc.Environment;
import org.ael.mvc.commons.StreamUtils;
import org.ael.mvc.constant.ContentType;
import org.ael.mvc.constant.EnvironmentConstant;
import org.ael.mvc.exception.ViewNotFoundException;
import org.ael.mvc.http.WebContent;
import org.ael.mvc.http.body.ByteBufBody;
import org.ael.mvc.template.AelTemplate;
import org.ael.mvc.template.ModelAndView;

import java.io.*;

/**
 * default impl template
 *
 * @Author: aorxsr
 * @Date: 2019/8/22 19:51
 */
public class DefaultTemplate implements AelTemplate {

    @Override
    public WebContent render(ModelAndView modelAndView, WebContent webContent) {
        String view = modelAndView.getView();
        InputStream resourceAsStream = StreamUtils.getClassPathFile(view);
        if (null == resourceAsStream) {
            throw new ViewNotFoundException(view + " view not found ... ");
        } else {
            try {
                webContent.getRequest().setASESSION(false);
                InputStream inputStream = StreamUtils.convertToByteArrayInputStream(resourceAsStream);
                ByteBuf buffer = Unpooled.buffer();
                buffer.writeBytes(inputStream, inputStream.available());
                webContent.getResponse().write(new ByteBufBody(buffer));
                webContent.getResponse().addHeader("Content-Length", inputStream.available());
                String suffix = view.substring(view.lastIndexOf(".") + 1);
                webContent.getResponse().setContentType(ContentType.get(suffix));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != resourceAsStream) {
                    try {
                        resourceAsStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return webContent;
        }
    }

    private static String PREFIX = "";
    private static String SUFFIX = "";

    private final static ClassLoader DEFAULT_CLASS_LOADER = DefaultTemplate.class.getClassLoader();

    @Override
    public void init(Ael ael) {
        Environment environment = ael.getEnvironment();

        PREFIX = environment.getString(EnvironmentConstant.TEMPLATE_PRIFIX, EnvironmentConstant.DEFAULT_TEMPLATE_PREFIX);
        SUFFIX = environment.getString(EnvironmentConstant.TEMPLATE_SUFFIX, EnvironmentConstant.DEFAULT_TEMPLATE_SUFFIX);
    }

    @Override
    public ReadStaticResources readFileContext(String view) throws ViewNotFoundException, IOException {
        InputStream resourceAsStream = StreamUtils.getClassPathFile(PREFIX + view + SUFFIX);
        if (null == resourceAsStream) {
            throw new ViewNotFoundException(view + " view not found ... ");
        } else {
            return new ReadStaticResources(resourceAsStream);
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
