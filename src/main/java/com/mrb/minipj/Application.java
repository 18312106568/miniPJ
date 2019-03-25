package com.mrb.minipj;


import com.google.gson.Gson;
import com.mrb.minipj.repository.UserRepository;
import com.mrb.minipj.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.sql.DataSource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;


/**
 *
 * @author MRB
 */
@EnableScheduling
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
                            Thread.sleep((int)Math.random()*1000);
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

        @CostLog
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
    @Component
    public static class CleanTask {

        @Autowired
        UserService userService;

        ExecutorService executorService = Executors.newFixedThreadPool(16);

        public static int taskCount=0;


        /**
         * 每秒执行一次
         */
        //@Scheduled(cron = "0/1 * * * * ?")
        public void batchAddUser() throws ExecutionException, InterruptedException {
            log.info("======>start the task：{}",taskCount++);
            List<FutureTask> taskList = new ArrayList<>();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 50; i++) {
                Callable<Long> callable = new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        try {
                            Thread.sleep((int)Math.random()*10);
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
            log.info("成功添加条数:{},花费时间：{} 毫秒,其中最大耗时是：{} 毫秒",count,(end-start),maxCost);
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
            //log.info("{} save user:{} cost time {}",Thread.currentThread().getName(),user.getId(),cost);
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

    @Slf4j
    @Aspect
    @Component
    public static class ControllerAspect {
        @Pointcut(value = " @annotation(com.mrb.minipj.Application.CostLog)")
        public void addCostPoint(){}

        @Around("addCostPoint()")
        public Object doLog(ProceedingJoinPoint jp) throws Throwable {
            long start = System.currentTimeMillis();
            log.info("=====>{} receiced the request,now time : {} ",Thread.currentThread().getName(), DateUtils.format(new Date(start)));
            Object result = jp.proceed();
            long end = System.currentTimeMillis();
            log.info("======>{} return the request,cost time :{}",Thread.currentThread().getName(),(end-start));
            return result;
        }
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CostLog {
    }

}
