package com.mrb.minipj;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.*;

import freemarker.template.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this templates file, choose Tools | Templates
 * and open the templates in the editor.
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
    
    
    @Controller
    static class DefaultController{

        @Autowired
        Configuration cfg;

        final String TEMP_NAME = "article.ftl";

        @RequestMapping(value = "article/get")
        public void getArticle(HttpServletResponse response){
            try {
                Template template = cfg.getTemplate(TEMP_NAME);

                Article article = new Article();
                List<String> segments = new ArrayList<>();
                article.setTitle("奶茶");
                segments.add("这家店奶茶真好喝，下周一起去喝奶茶");
                segments.add("好久没喝奶茶了，超想去喝奶茶");
                article.setSegments(segments);

                Map data = new HashMap();
                data.put("bean",article);

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;fileName=article.txt" );
                Writer writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8");
                template.process(data, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
        }
        
    }
    

    @Data
    public static class Article {
        private String title;
        private List<String> segments;
    }
}
