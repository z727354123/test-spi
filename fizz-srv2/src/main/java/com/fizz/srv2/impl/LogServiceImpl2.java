package com.fizz.srv2.impl;

import com.fizz.spi.ILogService;

public class LogServiceImpl2 implements ILogService {
    @Override
    public void log(String msg) {
        System.out.println("-------------------srv222-impl22---------------------:" + msg);
    }
}
