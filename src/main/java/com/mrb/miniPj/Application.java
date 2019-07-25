package com.mrb.miniPj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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


    @RestController
    static class DefaultController{

        @Resource
        private MongoTemplate mongoTemplate;

        @RequestMapping("/test/mongodb")
        public String testMongdb(String value){
            Map<String,Object> result = new HashMap<>();
            result.put("x",100);
            result.put("y",200);
            mongoTemplate.insert(result,"runoob");
            return mongoTemplate.findAll(Map.class,"runoob").toString();
        }


    }

}
