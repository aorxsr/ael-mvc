package org.ael.mvc.commons;

import io.netty.buffer.Unpooled;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {


    public static InputStream getClassPathFile(String view) {
        InputStream stream = StreamUtils.class.getResourceAsStream(view);
        if (null == stream) {
            stream = StreamUtils.class.getClassLoader().getResourceAsStream(view);
        }
        return stream;
    }

    public static  InputStream convertToByteArrayInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

}
