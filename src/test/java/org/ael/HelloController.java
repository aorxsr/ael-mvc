package org.ael;

import org.ael.c.annotation.*;

@Controller
@RequestMapping(value = "")
public class HelloController {

    @GetMapping(value = "/helloWord")
    public String helloWord() {
        return "helloWord";
    }

    @PostMapping(value = "/post")
    public String post(@RequestBody User user) {
        System.out.println(user.getName());
        return "asdf";
    }

}

class User {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
