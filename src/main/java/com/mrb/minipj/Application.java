package com.mrb.minipj;


import com.google.gson.Gson;
import com.mrb.minipj.repository.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


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

        @Autowired
        UserService userService;

        @Autowired
        DataSource dataSource;

        ExecutorService executorService = Executors.newFixedThreadPool(32);

        @RequestMapping(value = "dataSource/get")
        public String getDataSource(){
            Gson gson = new Gson();
            return dataSource.toString();
        }

        @RequestMapping(value = "user/autoAdd")
        public String autoAddUsers() throws ExecutionException, InterruptedException {
            List<FutureTask> taskList = new ArrayList<>();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 50; i++) {
                Callable<Long> callable = new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        try {
                            long cost = userService.saveUser(Long.toString(System.currentTimeMillis()));
                            return cost;
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                        return -1l;
                    }
                };
                FutureTask<Long> task = new FutureTask(callable);
                taskList.add(task);
                executorService.submit(task);
            }
            int count=0;
            long maxCost=0;
            for(Future<Long> task : taskList){
                long cost = task.get();
                if(cost>0){
                    count++;
                    if(cost>maxCost){
                        maxCost=cost;
                    }
                }
                continue;
            }
            long end = System.currentTimeMillis();
            return String.format("成功添加条数:%d,花费时间：%d 毫秒,其中最大耗时是：%d 毫秒",count,(end-start),maxCost);
        }

        @RequestMapping(value = "user/save")
        public String saveUser(String userName){
            try {
                userService.saveUser(userName);
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

    @Slf4j
    @Service
    public static class UserService{
        @Autowired
        UserRepository userRepository;

        //@Transactional(rollbackFor = Exception.class)
        public long saveUser(String name){
            long start = System.currentTimeMillis();
            User user = new User();
            user.setName(name);
            user = userRepository.save(user);
            long end = System.currentTimeMillis();
            long cost = end-start;
            log.info("save user:{} cost time {}",user.getId(),cost);
            return cost;
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
