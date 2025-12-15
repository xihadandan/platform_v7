package com.wellsoft.test;

import java.lang.reflect.Method;

//测试方法
public class BridgeMethodTest {
    public static void main(String[] args) throws Exception {
        // 使用java的多态
        Parent parent = new Child();
        System.out.println(parent.bridgeMethod("abc123"));// 调用的是实际的方法
        Class<? extends Parent> clz = parent.getClass();
        System.out.println(clz.getName());
        Method method = clz.getMethod("bridgeMethod", Object.class); // 获取桥接方法
        System.out.println(method.isBridge()); // true
        System.out.println(method.invoke(parent, "hello")); // 调用的是桥接方法
        // System.out.println(parent.bridgeMethod(new Object()));// 调用的是桥接方法, 会报ClassCastException: java.lang.Object cannot be cast to java.lang.String`错误`


        Class<Child> clazz = Child.class;
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            System.out.println(getMethodInfo(m) + " is Bridge Method? " + m.isBridge());
        }
    }

    public static String getMethodInfo(Method m) {
        StringBuilder sb = new StringBuilder();
        sb.append(m.getReturnType()).append(" ");
        sb.append(m.getName());
        Class[] params = m.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i].getName()).append(" ");
        }
        return sb.toString();
    }
}
