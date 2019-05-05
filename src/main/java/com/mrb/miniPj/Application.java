package com.mrb.miniPj;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

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
        
        @RequestMapping("/test")
        public String test(Long id){
            String result = null;
            try{
                result = helloSelenium();
                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fail";
        }

        private String helloSelenium() throws InterruptedException, IOException {
            /**
             * 本地需要安装chromedriver https://npm.taobao.org/mirrors/chromedriver/
             */
            ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File("E:\\softface\\chromedriver\\chromedriver.exe"))
                    .usingAnyFreePort()
                    .build();
            service.start();

            ChromeOptions options = new ChromeOptions();
            DesiredCapabilities cap = DesiredCapabilities.chrome();
            cap.setCapability(ChromeOptions.CAPABILITY, options);
            System.out.println(service.getUrl());
            WebDriver driver = new RemoteWebDriver(service.getUrl(),cap);
            driver.get("https://www.baidu.com");
            Thread.sleep(1000);


            WebElement kw = driver.findElement(By.id("kw"));
            WebElement su = driver.findElement(By.id("su"));
            kw.sendKeys("selenium");
            su.click();
            Thread.sleep(1000);
            WebElement element = driver.findElement(By.tagName("html"));
            System.out.println(driver.manage().getCookies());
            String result = element.getAttribute("innerHTML");
            //System.out.println(element.getText());
            driver.quit();
            service.stop();
            return result;
        }

        
    }

}
