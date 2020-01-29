package org.ael;

import org.ael.c.annotation.Controller;
import org.ael.c.annotation.GetMapping;

@Controller
@GetMapping(value = "")
public class HelloController {

    @GetMapping(value = "/helloWord")
    public String helloWord() {
        return "helloWord";
    }

}
