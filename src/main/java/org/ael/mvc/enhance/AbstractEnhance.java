package org.ael.mvc.enhance;

import org.ael.mvc.http.WebContent;

/**
 * @author aorxsr
 * @date 2019/10/19
 * 增强
 */
public abstract class AbstractEnhance {

    /**
     * 前置增强
     *
     * @param webContent
     */
    public abstract void before(WebContent webContent);

    /**
     * 后置增强
     *
     * @param webContent
     */
    public abstract void after(WebContent webContent);

}
