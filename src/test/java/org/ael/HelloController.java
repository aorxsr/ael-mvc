package org.ael;

import org.ael.c.annotation.*;

@Controller
@RequestMapping(value = "")
public class HelloController {

    @ResponseBody
    @GetMapping(value = "/helloWord")
    public String helloWord() {
        return "helloWord";
    }

    @ResponseBody
    @PostMapping(value = "/post")
    public Object post(@RequestBody User user) {
        System.out.println(user.getName());
        return user;
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
