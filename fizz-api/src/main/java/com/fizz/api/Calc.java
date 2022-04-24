/*
 * Copyright(c) Kingdee Software (China) Co., Ltd. 1993-2020, All rights reserved.
 */

package com.fizz.api;

import com.fizz.extension.ExtensionFactory;

/**
 * 计算服务
 *
 * @since 2022-04-24
 */
public abstract class Calc {
    // 创建工厂
    private static final ExtensionFactory<Calc> FACTORY = ExtensionFactory.getExtensionFactory(Calc.class);

    // 单例, 初始化实例
    private static Calc ins;

    /**
     * 加锁 获取实例
     *
     * @return 实例
     */
    public static Calc getIns() {
        if (ins == null) {
            synchronized (Calc.class) {
                if (ins == null) {
                    initData();
                }
            }
        }
        return ins;
    }

    /**
     * 加锁后的真实初始化
     */
    private static void initData() {
        boolean isLocal = false;
        if (isLocal) {
            // 本地调用, 创建 Mork 实例
            ins = null; // xxx
            return;
        }
        String name = System.getProperty("calcService.ins", "def");
        ins = FACTORY.getExtension(name);
    }

    /**
     * 抽象加法
     *
     * @param left  数字1
     * @param right 数字2
     * @return 求和
     */
    public abstract int innerAdd(int left, int right);

}
