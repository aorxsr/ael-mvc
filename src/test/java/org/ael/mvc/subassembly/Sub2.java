package org.ael.mvc.subassembly;

import org.ael.mvc.container.annotation.Injection;
import org.ael.mvc.container.annotation.Subassembly;

@Subassembly
public class Sub2 {

    @Injection
    private Sub1 sub1;

}
