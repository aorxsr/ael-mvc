package org.ael.mvc.enhance;

import lombok.Data;
import org.ael.mvc.constant.HttpMethod;

import java.lang.reflect.Method;

/**
 * @author aorxsr
 * @date 2019/10/19
 */
@Data
public class EnhanceInfo {

    private HttpMethod httpMethod;
    private Object target;

    private Method beforeMethod;
    private Method afterMethod;

}
