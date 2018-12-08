package com.mrb.minipj;


import com.mrb.minipj.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.List;


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
        UserRepository userRepository;

        @RequestMapping(value = "user/save")
        public String saveUser(String userName){
            try {
                User user = new User();
                user.setName(userName);
                userRepository.save(user);
                return "success";
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return "fail";
        }

        @RequestMapping(value = "user/get")
        public List<User> getUser(String userName){
           List<User> userList = userRepository.findAllByName(userName);
           return userList;
        }
        
    }

    @Data
    @Entity
    @Table(name = "tb_user")
    public  static class User{
        @Id
        @Column(name="id")
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        private Long id;
        @Column(name="name")
        private String name;

    }



}
