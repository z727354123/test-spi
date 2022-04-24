/*
 * Copyright(c) Kingdee Software (China) Co., Ltd. 1993-2020, All rights reserved.
 */

package com.fizz.api;

/**
 * 测试
 *
 * @since 2022-04-24
 */
public class CalcSerivce {

    public static int add(int left, int right) {
        int res = Calc.getIns().innerAdd(left, right);
        return res;
    }
}
