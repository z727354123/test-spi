/*
 * Copyright(c) Kingdee Software (China) Co., Ltd. 1993-2020, All rights reserved.
 */

package com.fizz.entity;

import java.util.function.Supplier;

/**
 * 功能描述
 *
 * @since 2022-04-24
 */
public class Holder<T> {
    private volatile T val;

    public T get() {
        return val;
    }

    public void set(T val) {
        this.val = val;
    }

    /**
     * 同步获取
     *
     * @param supplier 提供方法
     * @return 实例
     */
    public T get(Supplier<T> supplier) {
        T ins = this.get();
        if (ins == null) {
            synchronized (this) {
                if (this.get() == null) {
                    this.set(supplier.get());
                }
                ins = this.get();
            }
        }
        return ins;
    }
}
