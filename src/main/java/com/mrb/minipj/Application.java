package com.mrb.minipj;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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


        @Autowired
        StringRedisTemplate stringRedisTemplate;

        @RequestMapping(value = "redis/get")
        public String getKeyValue(String key){
            return stringRedisTemplate.opsForValue().get(key);
        }
        
        @RequestMapping(value = "redis/set")
        public String setKeyValue(String key,String value){
            try {
                stringRedisTemplate.opsForValue().set(key, value);
                return "success";
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return "fail";
        }
        
    }



}
