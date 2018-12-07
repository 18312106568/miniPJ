package com.mrb.miniPj;
import com.mrb.minipj.repository.UserGoodRepository;
import java.io.Serializable;
import java.util.Optional;
import lombok.Data;
import org.elasticsearch.index.settings.IndexDynamicSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
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
    
    
    @RestController
    static class DefaultController{
        @Autowired
        UserGoodRepository userGoodRepository;
        
        @RequestMapping(value = "saveGood")
        public String saveUserGood(Long id){
            System.out.println("保存商品");
            UserGoods userGoods = new UserGoods();
            userGoods.setId(new Long(id));
            userGoods.setGoodDesc("奶茶真好喝");
            userGoods.setGoodName("奶茶");
            if(userGoodRepository.save(userGoods)!=null){
                return "success";
            }
            return  "fail";
        }
        
        @RequestMapping(value = "getGood")
        public String getUserGood(Long id){
            Optional<UserGoods> optional = userGoodRepository.findById(id);
            return optional.get().toString();
        }
        
    }
    
    @Data
    @Document(indexName = "es-good",type = "user")
    public static class UserGoods implements Serializable{
        private Long id;
        private String goodName;
        private String goodDesc;
    }
}
