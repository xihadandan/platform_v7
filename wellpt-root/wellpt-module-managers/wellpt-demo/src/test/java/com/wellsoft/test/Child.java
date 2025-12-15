package com.wellsoft.test;

//定义一个类实现范型接口
public class Child implements Parent<String> {
    public String bridgeMethod(String param) {
        return param;
    }
}
