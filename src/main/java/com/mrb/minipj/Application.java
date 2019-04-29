package com.mrb.minipj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    static class DefaultController{

        @RequestMapping(value = "dolog")
        public String doLog(Long id){
            log.info("hi,man how are you?");
            log.warn("warning....!!!");
            log.error("i can not do it");
            return "we had done log";
        }
        
    }

}
