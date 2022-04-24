package com.fizz.srv.impl;

import com.fizz.spi.ILogService;

public class LogServiceImpl implements ILogService {
    @Override
    public void log(String msg) {
        System.out.println("-------------------srv----------------------:" + msg);
    }
}
