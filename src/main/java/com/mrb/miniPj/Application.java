package com.mrb.miniPj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public static void main(String args[]){
        SpringApplication.run(Application.class, args);
    }


    @RestController
    @RequestMapping("/")
    static class DefaultController{

        @RequestMapping("index")
        public String index(){
            return "hello world";
        }
    }

    @EnableWebSecurity
    public class MySecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            //super.configure(http);
            //定制请求的授权规则
            http.authorizeRequests().antMatchers("/").permitAll()
                    .antMatchers("/level1/**").hasRole("VIP1")
                    .antMatchers("/level2/**").hasRole("VIP2")
                    .antMatchers("/level3/**").hasRole("VIP3");

            //开启自动配置的登陆功能，效果，如果没有登陆，没有权限就会来到登陆页面
            http.formLogin().usernameParameter("user").passwordParameter("pwd").loginPage("/userlogin");
            //1、 /login 来到登陆页
            //2、重定向到/login?error表示登陆失败
            //3、更多详细功能
            //4、默认post形式的 /login 代表处理登陆
            //5、一旦定制loginPage  那么 loginPage的post请求就是登陆


            //开启自动配置的注销功能
            http.logout().logoutSuccessUrl("/"); //注销成功以后来到首页
            //1、访问/logout 表示用户注销。清空 session
            //2、注销成功会返回 /login?logout 页面
            //3、默认post形式的 /login代表处理登陆


            //开启记住我功能
            http.rememberMe().rememberMeParameter("remeber");
            //登陆成功以后，将cookie发给浏览器保存，以后访问页面带上这个cookie，只要通过检查就可以免登陆
            //点击注销会删除cookie
        }

        //定义认证规则
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            //super.configure(auth);

            //auth.jdbcAuthentication()...
            auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())   //在Spring Security 5.0中新增了多种加密方式，页改变了密码的格式
                    .withUser("zhangsan").password(new BCryptPasswordEncoder().encode("123456")).roles("VIP1", "VIP2")
                    .and()
                    .withUser("lisi").password(new BCryptPasswordEncoder().encode("123456")).roles("VIP2", "VIP3")
                    .and()
                    .withUser("wangwu").password(new BCryptPasswordEncoder().encode("123456")).roles("VIP1", "VIP3");
        }
    }
}
