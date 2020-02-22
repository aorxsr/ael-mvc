package org.ael.route.hook;

import org.ael.http.WebContent;

public class HookContext {

    private WebContent webContent;

    private HookReturnENUM hookReturnENUM = HookReturnENUM.SUCCESS;

    private String failedMessage;

    public WebContent getWebContent() {
        return webContent;
    }

    public void setWebContent(WebContent webContent) {
        this.webContent = webContent;
    }

    public HookReturnENUM getHookReturnENUM() {
        return hookReturnENUM;
    }

    public void setHookReturnENUM(HookReturnENUM hookReturnENUM) {
        this.hookReturnENUM = hookReturnENUM;
    }

    public String getFailedMessage() {
        return failedMessage;
    }

    public void setFailedMessage(String failedMessage) {
        this.hookReturnENUM = HookReturnENUM.FAILED;
        this.failedMessage = failedMessage;
    }

    public static enum HookReturnENUM {
        SUCCESS, FAILED
    }

}
