package com.mrb.miniPj;

import com.mrb.miniPj.service.DemoService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.Date;

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



    @Component
    @WebService(serviceName = "DemoService", // 与接口中指定的name一致
            targetNamespace = "http://service.minipj.mrb.com", // 与接口中的命名空间一致,一般是接口的包名倒
            endpointInterface = "com.mrb.miniPj.service.DemoService"// 接口地址
    )
    public static class DemoServiceImpl implements DemoService {

        @Override
        public String sayHello(String user) {
            return user+"，现在时间："+"("+new Date()+")";
        }

    }




}
