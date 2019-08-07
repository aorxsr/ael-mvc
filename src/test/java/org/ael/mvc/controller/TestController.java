package org.ael.mvc.controller;

import org.ael.mvc.annotation.Controller;
import org.ael.mvc.annotation.GetMapping;
import org.ael.mvc.http.WebContent;

@Controller
@GetMapping(value = "/test")
public class TestController {

    @GetMapping(value = "/test")
    public void test(WebContent webContent) {
        webContent.getResponse().text("test!");
    }

}
