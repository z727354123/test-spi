package com.fizz.api;

public class SrvCalcWrapper extends Calc {

    private final Calc ins;
    public SrvCalcWrapper(Calc ins) {
        this.ins = ins;
    }

    @Override
    public int innerAdd(int left, int right) {
        System.out.println("-------------------SrvCalcWrapper----------------------");
        return ins.innerAdd(left, right);
    }
}
