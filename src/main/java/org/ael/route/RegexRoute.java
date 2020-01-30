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
package org.ael.route;

import lombok.Builder;
import lombok.Data;

import java.util.regex.Pattern;

/**
 * @Author aorxsr
 * @Date 2020/1/29
 */
@Data
@Builder
public class RegexRoute {

    private Pattern pattern;
    private int atomInt;

    public boolean matching(String url) {
        return pattern.matcher(url).matches();
    }

}
