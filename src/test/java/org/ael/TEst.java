package org.ael;

public class TEst {

    public static void main(String[] args) {

        Ael ael = new Ael();

        ael.get("/a", webContent -> {
            webContent.getResponse().text("F大大");
        })
                .start(TEst.class);

    }

}
