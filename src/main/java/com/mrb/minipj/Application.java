package com.mrb.minipj;


import com.google.gson.Gson;
import com.mrb.minipj.repository.exam.CdbItsPdetailsRepository;
import com.mrb.minipj.repository.primary.UserRepository;
import com.mrb.minipj.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;


/**
 *
 * @author MRB
 */
@Slf4j
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

        @Autowired
        CdbItsPdetailsRepository cdbItsPdetailsRepository;

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

        @RequestMapping(value = "tran/submit")
        public String geCdbItsPdetails(Integer pid,String loginSign) throws IOException {
            log.info("======>geCdbItsPdetails request param:pid:{},loginSign",pid,loginSign);
            String REGEX = ".*alert\\('(.*)'\\);window.location.href.*";
            List<CdbItsPdetails> cdbItsPdetailsList = cdbItsPdetailsRepository.findAllByPid(pid);
            Map<String,List<CdbItsPdetails>> cdbItsPdetailsMap=
                    cdbItsPdetailsList.stream().collect(Collectors.groupingBy(CdbItsPdetails::getType));

            StringBuilder sb = new StringBuilder("&subVal=%CC%E1%BD%BB");
            for(String key:cdbItsPdetailsMap.keySet()){
               if("checkbox".equals(key)) {
                   continue;
               }
               List<CdbItsPdetails> pdetailsList = cdbItsPdetailsMap.get(key);
               for(CdbItsPdetails pdetails:pdetailsList){
                   sb.append("&").append(String.format("%s-%d=%s",pdetails.getType(),pdetails.getQid(),pdetails.getRAns()));
               }
            }
            List<CdbItsPdetails> checkBoxList = cdbItsPdetailsMap.get("checkbox");
            for(CdbItsPdetails checkBox :checkBoxList){
                char[] ransChars = checkBox.getRAns().toCharArray();
                for(char ransChar : ransChars) {
                    sb.append("&").append(String.format("%s-%d-%s=%s", checkBox.getType(), checkBox.getQid(), ransChar,ransChar));
                }
            }
            String url = String.format("http://173.1.1.2/train/its_exam.php?action=submit&id=%d",pid);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, sb.substring(1).toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8")
                    .addHeader("Referer",String.format("http://173.1.1.2/train/its_exam.php?action=enter&id=%d",pid))
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
                    .addHeader("Cookie", loginSign)
                    //"PHPSESSID=og0n6eovq3qr8br6955gmn3hm7;cdb_auth=2316elvypnl6%2FJ913%2BXo3SSPhW0SP8PL7iAyGOCODx9apIwQskN9TJH93DsYScQ4sJguachOsvW6wC0Kvce7EaJQ4EU;uchome_loginuser=20233;cdb_sid=E80NhA;"
                    .build();
            Response response = client.newCall(request).execute();
            byte[] data = response.body().bytes();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, num);
            }
            gzip.close();
            bis.close();
            byte[] ret = bos.toByteArray();
            bos.flush();
            bos.close();
            String result = new String(new String(ret,"GB2312").getBytes(),"UTF-8");
            log.info(result);
            return result.replaceAll("\\r\\n","").replaceAll(REGEX,"$1");
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

    @Data
    @Entity
    @Table(name = "cdb_its_pdetails")
    public  static class CdbItsPdetails{
        @Id
        @Column(name="id")
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        private Long id;

        @Column(name="pid")
        private Integer pid;

        @Column(name="qid")
        private Integer qid;

        @Column(name="type")
        private String type;

        @Column(name="question")
        private String question;

        @Column(name="ans")
        private String ans;

        @Column(name="r_ans")
        private String rAns;

        @Column(name="value")
        private Integer value;

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
