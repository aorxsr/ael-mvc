package org.ael.route.asm;

/**
 * 封装方法描述和参数个数
 * @Author aorxsr
 * @Date 2020/2/15
 */
class EnclosingMetadata {
    //method name
    String name;
    // method description
    String desc;
    // params size
    int size;

    public EnclosingMetadata(String name, String desc, int size) {
        this.name = name;
        this.desc = desc;
        this.size = size;
    }
}