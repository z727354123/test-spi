/*
 * Copyright(c) Kingdee Software (China) Co., Ltd. 1993-2020, All rights reserved.
 */

package com.fizz.api;

/**
 * 功能描述
 *
 * @since 2022-04-24
 */
public class Srv2Calc extends Calc{
    @Override
    public int innerAdd(int left, int right) {
        System.out.println("-------------------Srv2Calc----------------------");
        return left + right + 20;
    }
}
