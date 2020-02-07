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
package org.ael.plugin.ioc;

import org.ael.Ael;
import org.ael.c.annotation.Controller;
import org.ael.ioc.core.DefaultIOC;
import org.ael.ioc.core.annotation.Bean;
import org.ael.ioc.core.annotation.Service;

/**
 * @author aorxsr
 * @Data 2020/2/6
 */
public class IOCPlugin {

    private DefaultIOC ioc = new DefaultIOC();

    public void initIoc(Ael ael) {
        ioc.addBeanClss(Bean.class);
        ioc.addBeanClss(Service.class);
        ioc.addBeanClss(Controller.class);

        ioc.init(ael.getScanClass());
    }

    public Object getBean(Class<?> tClass) {
        return ioc.getBean(tClass);
    }

}
