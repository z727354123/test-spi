package com.fizz.main;

import java.lang.reflect.Method;

public class TestWrapper {
    public static void main(String[] args) throws Exception {
        call();

    }

    private static void call() throws Exception {
        Class<?> aClass = Class.forName("com.fizz.api.CalcSerivce");
        Object ins = aClass.newInstance();
        Method add = aClass.getDeclaredMethod("add", int.class, int.class);
        System.out.println(add.invoke(ins, 1, 1));
        System.out.println(add.invoke(ins, 2, 2));
    }
}
