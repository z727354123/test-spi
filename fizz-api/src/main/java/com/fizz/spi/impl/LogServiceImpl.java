package com.fizz.spi.impl;

import com.fizz.spi.ILogService;

public class LogServiceImpl implements ILogService {
    @Override
    public void log(String msg) {
        System.out.println("-------------------api----------------------:" + msg);
    }
}
