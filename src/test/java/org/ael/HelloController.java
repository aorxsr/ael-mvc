package org.ael;

import org.ael.c.annotation.*;
import org.ael.dao.UserDao;
import org.ael.http.MultiPartFile;
import org.ael.http.WebContent;
import org.ael.ioc.core.annotation.Injection;
import org.ael.orm.annotation.Value;
import org.ael.plugin.aop.annotation.BeforeEnhance;

@BeforeEnhance(enhanceMethodName = "org.ael.HelloController.before")
@Controller
@RequestMapping(value = "")
public class HelloController {

    @Value(name = "server.port")
    private String port;

    @Injection
    private WebContent webContent;

    public void before() {
        System.out.println("端口:" + port);
        System.out.println("前置增强...");
        testBean.testConsole();
        webContent.getResponse().text("失败!");
    }

    @Injection
    private TestBean testBean;

    @Injection
    private UserDao userDao;

    @ResponseBody
    @GetMapping(value = "/helloWord")
    public String helloWord() {
//        testBean.testConsole();
        userDao.q();
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
