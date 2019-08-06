package com.mrb.minipj;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

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

    @Configuration
    public static  class CxfConfig {

        @Autowired
        private Bus bus;

        @Autowired
        public DemoService demoService;



        @Bean
        public Endpoint endpoint() {
            EndpointImpl endpoint = new EndpointImpl(bus, demoService);
            WebService serviceAnn = demoService.getClass().getDeclaredAnnotation(WebService.class);
            endpoint.publish("/"+serviceAnn.name());
            return endpoint;
        }
    }



    @Component
    @WebService(serviceName = "demo",
            targetNamespace = "http://service.minipj.mrb.com"

    )
    public static class DemoService  {

        public String helloWorld(String param) {
            return "hello"+param;
        }

    }




}
