package com.mrb.miniPj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

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


    @RestController
    static class DefaultController{


        @RequestMapping(value = "upload",method = RequestMethod.POST)
        @ResponseBody
        public String upload(HttpServletRequest request) throws IOException {
            InputStream inputStream = request.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            StringBuilder sb = new StringBuilder();
            while(scanner.hasNextLine()){
                sb.append(scanner.nextLine()).append("\r\n");
            }
            System.out.println(sb.toString());
            return sb.toString();
        }


    }

}
