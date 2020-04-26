package com.mrb.miniPj;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ServiceLoader;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MRB
 */
@SpringBootApplication
public class Application {
    public static void main(String args[]){

        SpringApplication.run(Application.class, args);
    }

    @Slf4j
    @RestController
    static public class IndexController{
        @RequestMapping("/")
        public String index(){
            ServiceLoader<IShout> shouts = ServiceLoader.load(IShout.class);
            log.info(shouts.toString());
            for (IShout s : shouts) {
                s.shout();
            }
            return "hello";
        }
    }
}
