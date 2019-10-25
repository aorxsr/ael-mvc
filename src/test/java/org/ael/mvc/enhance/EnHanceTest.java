package org.ael.mvc.enhance;

import org.ael.mvc.enhance.annotation.Enhance;
import org.ael.mvc.enhance.annotation.EnhanceEnable;
import org.ael.mvc.http.WebContent;

@Enhance(url = "/")
public class EnHanceTest extends AbstractEnhance {


    @EnhanceEnable
    public boolean before(WebContent webContent) {
        webContent.getResponse().json("嘻嘻嘻");
        return false;
    }

    @EnhanceEnable
    public boolean after(WebContent webContent) {
        System.out.println("after");
        return true;
    }

}
