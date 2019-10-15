package org.ael.mvc.controller;

import org.ael.mvc.annotation.Controller;
import org.ael.mvc.annotation.GetMapping;
import org.ael.mvc.annotation.RequestParam;
import org.ael.mvc.http.Response;
import org.ael.mvc.http.WebContent;

@Controller
@GetMapping(value = "/test")
public class TestController {

    @GetMapping(value = "/test")
    public void test(WebContent webContent) {
        webContent.getResponse().text("test!");
    }

    @GetMapping(value = "/p")
    public void testA(@RequestParam(value = "a", required = true) String a,
                      Response response) {
        response.json("aaaa");
    }

    @GetMapping(value = "/index")
    public String index() {

        return "index";
    }

}
