package org.ael.mvc.template.give;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Data
public class ReadStaticResources {

    int size;
    ByteBuf byteBuf;

    public ReadStaticResources(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteBuf = Unpooled.buffer();
        size = 0;

        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1) {
            size += len;
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();

        inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        byteBuf.writeBytes(inputStream, size);
    }

    public String getSizeString() {
        return String.valueOf(size);
    }

}
