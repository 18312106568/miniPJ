package com.mrb.miniPj;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;
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
@Slf4j
@SpringBootApplication
public class Application {
    public static void main(String args[]){
        SpringApplication.run(Application.class, args);
    }

    public static class HiJob extends QuartzJobBean {
        @Override
        protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("    Hi! :" + jobExecutionContext.getJobDetail().getKey());
        }
    }

    @Configuration
    public static class QuartzConfig {
        @Bean
        public JobDetail myJobDetail(){
            JobDetail jobDetail = JobBuilder.newJob(HiJob.class)
                    .withIdentity("myJob1","myJobGroup1")
                    //JobDataMap可以给任务execute传递参数
                    .usingJobData("job_param","job_param1")
                    .storeDurably()
                    .build();
            return jobDetail;
        }
        @Bean
        public Trigger myTrigger(){
            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(myJobDetail())
                    .withIdentity("myTrigger1","myTriggerGroup1")
                    .usingJobData("job_trigger_param","job_trigger_param1")
                    .startNow()
                    //.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever())
                    .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
                    .build();
            return trigger;
        }
    }


}
