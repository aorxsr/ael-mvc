package org.ael;

import org.ael.http.WebContent;
import org.ael.route.hook.HookContext;
import org.ael.route.hook.annotation.Hook;
import org.ael.route.hook.annotation.HookController;

@HookController
public class IndexHook {

    @Hook(url = "/*")
    public HookContext all(WebContent webContent) {

        webContent.getResponse().text("错误!");
        webContent.getResponse().setStatus(500);
        HookContext context = new HookContext();

        context.setFailedMessage("失败!");
        context.setWebContent(webContent);

        return context;
    }

}
