package com.mrb.miniPj;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
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

    public static final String QUEUE_NAME = "topic.test";

    public static final String EXCHANGE_NAME = "exchange";

    public static final String FANOUT_NAME = "fanout";

    public static void main(String args[]){
        SpringApplication.run(Application.class, args);
    }


    @RestController
    static class DefaultController{

        @Autowired
        TestSender sender;

        @RequestMapping("/send-msg")
        public String saveUserGood(String msg){
            sender.send(msg);
            return "success";
        }


    }

    @Configuration
    public class SenderConf {
        @Bean
        public Queue queue() {
            return new Queue(QUEUE_NAME);
        }
    }

    @Component
    public class TestSender {
        @Autowired
        private AmqpTemplate template;

        public void send(Object value) {
            //1.主题交换机模式
            template.convertAndSend(EXCHANGE_NAME,QUEUE_NAME,value);
        }
    }
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

//    @Bean
//    Binding bindingExchangeMessage(Queue queueMessage, TopicExchange topicExchange) {
//        return BindingBuilder.bind(queueMessage).to(topicExchange).with(QUEUE_NAME);
//    }

    @Component
    public static class TestReceive {
        @RabbitListener(queues = QUEUE_NAME)    //监听器监听指定的Queue
//        @RabbitListener(bindings = {@QueueBinding(
//                value=@org.springframework.amqp.rabbit.annotation.Queue(QUEUE_NAME),
//                exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = EXCHANGE_NAME,type = "topic"),
//                key = QUEUE_NAME)})
        public void processC(String str) {
            System.out.println("ReceiveC:" + str);
        }

        @RabbitListener(queues = QUEUE_NAME)    //监听器监听指定的Queue
        public void processA(String str) {
            System.out.println("ReceiveA:" + str);
        }
    }

}
