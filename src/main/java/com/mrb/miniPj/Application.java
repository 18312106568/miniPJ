package com.mrb.miniPj;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

    public static final String TOPIC = "KAFKA_LOG";


    @RestController
    static class DefaultController{

        @Autowired
        private KafkaTemplate kafkaTemplate;

        @RequestMapping("send-log")
        public String sendLog(@RequestParam("value") String value) throws ExecutionException, InterruptedException {
            ListenableFuture future = kafkaTemplate.send(TOPIC,value);
            return  future.get().toString();
        }
    }


    @Component
    @Slf4j
    public  static class LogConsumer {
        @KafkaListener(topics = {TOPIC})
        public void consumer(ConsumerRecord<?, ?> consumerRecord) {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            log.info(">>>>>>>>>> record =" + kafkaMessage);
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                Object message = kafkaMessage.get();
                System.err.println("消费消息:" + message);
            }
        }
    }

}
