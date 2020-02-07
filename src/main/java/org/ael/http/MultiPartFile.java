/* Copyright (c) 2019, aorxsr (aorxsr@163.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ael.http;

import io.netty.handler.codec.http.multipart.FileUpload;
import lombok.Builder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Author aorxsr
 * @Date 2020/2/7
 */
@Builder
public class MultiPartFile {

    private FileUpload file;

    public String getContentTransferEncoding() {
        return file.getContentTransferEncoding();
    }

    public boolean renameTo(File toFile) throws IOException {
        return file.renameTo(toFile);
    }

    public File getFile() throws IOException {
        return file.getFile();
    }

    public Charset getCharSet() {
        return file.getCharset();
    }

    public String getFileContentType() {
        return file.getContentType();
    }

    public String getFormName() {
        return file.getName();
    }

    public String getFileName() {
        return file.getFilename();
    }

}
