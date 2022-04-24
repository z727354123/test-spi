package com.fizz.main;

import com.fizz.spi.ILogService;

import java.util.ServiceLoader;

public class TestJavaSPI {
    public static void main(String[] args) {
        ServiceLoader<ILogService> load = ServiceLoader.load(ILogService.class);
        load.iterator();
        for (ILogService service : load) {
            service.log("msg");
        }
    }
}
