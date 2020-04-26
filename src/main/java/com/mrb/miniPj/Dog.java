package com.mrb.miniPj;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Dog implements IShout {
    @Override
    public void shout() {
        log.info("wang wang");
    }
}
