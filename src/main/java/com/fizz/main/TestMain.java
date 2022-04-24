/*
 * Copyright(c) Kingdee Software (China) Co., Ltd. 1993-2020, All rights reserved.
 */

package com.fizz.main;

import com.fizz.api.Calc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 功能描述
 *
 * @since 2022-04-24
 */
public class TestMain {
    public static void main(String[] args) throws Exception {
        // System.setProperty("calcService.ins", "srv2");
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
