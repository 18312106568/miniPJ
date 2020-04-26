package com.mrb.miniPj;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cat implements IShout {
    @Override
    public void shout() {
        log.info("miao miao");
    }
}
