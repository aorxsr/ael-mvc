package org.ael;

import org.ael.c.annotation.*;
import org.ael.http.MultiPartFile;
import org.ael.ioc.core.annotation.Injection;

@Controller
@RequestMapping(value = "")
public class HelloController {

    @Injection
    private TestBean testBean;

    @ResponseBody
    @GetMapping(value = "/helloWord")
    public String helloWord() {
        testBean.testConsole();

        return "helloWord";
    }

    @ResponseBody
    @PostMapping(value = "/post")
    public Object post(@RequestBody User user) {
        System.out.println(user.getName());
        return user;
    }

    @GetMapping(value = "index")
    public String index(@PathParam(required = true) String pathParam) {
        System.out.println(pathParam);
        return "index";
    }

    @ResponseBody
    @PostMapping(value = "/upload")
    public String uploadFile(@MultiPartFileParam(name = "wenjian") MultiPartFile file) {
        System.out.println(file);
        return file.getFileName();
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
