package com.mrb.miniPj;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    @Slf4j
    @Controller
    @RequestMapping("/")
    static class DefaultController{


        @RequestMapping("/")
        public String index(Model model){
            model.addAttribute("name","hello pillar");
            System.out.println(new BCryptPasswordEncoder().encode("123456"));
            return "index";
        }

        @RequestMapping("/loginPage")
        public String loginPage(Model model){
            return "userlogin";
        }

        @RequestMapping("/level1/1")
        public String level1(Model model){
            return "level1";
        }

        @RequestMapping("/level2/1")

        public String level2(Model model){
            return "level2";
        }

        @RequestMapping("/test")
        @ResponseBody
        public String test(HttpServletResponse  response){
            response.setStatus(302);
            response.setHeader("Location","http://www.baidu.com");
            return "test302";
        }

    }

    @EnableWebSecurity
    public class MySecurityConfig extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.formLogin().and().httpBasic();
           // http.formLogin().usernameParameter("user").passwordParameter("pwd").loginPage("/loginPage");
            http.authorizeRequests().antMatchers("/").permitAll()
                    .antMatchers("/test").permitAll()
                    .antMatchers("/upload").permitAll()
                    //.antMatchers("/login").permitAll()
                    .antMatchers("/level1/**").hasAnyAuthority("VIP1")
                    .antMatchers("/level2/**").hasRole("VIP2")
                    .antMatchers("/level3/**").hasRole("VIP3");
            //添加自定义拦截器到httpSecurity
//            OpenIdAuthenticationFilter openIdAuthenticationFilter = new OpenIdAuthenticationFilter();
//
//            //此处可以添加认证处理对象
//            openIdAuthenticationFilter.setAuthenticationManager(null);
//            openIdAuthenticationFilter.setRequiresAuthenticationRequestMatcher(
//                    new AntPathRequestMatcher("/login", "POST"));
//            http.addFilter(openIdAuthenticationFilter);

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




    public static class OpenIdAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
        //仅处理post
        private boolean postOnly = true;
        /***
         * 用于拦截封装token具体验证交由anthenticationManager属性完成，可以在创建时自己设置
         */
        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
                throws AuthenticationException {
            if (postOnly && !request.getMethod().equals("POST")) {
                throw new AuthenticationServiceException(
                        "Authentication method not supported: " + request.getMethod());
            }
            String username = request.getParameter("username"); //默认
            String password = request.getParameter("password");
            username = username == null?"":username.trim();
            password = password == null?"":password;
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    username, new BCryptPasswordEncoder().encode(password));
            authRequest.setDetails(request);//放入token 的detials中
            //默认认证成功
            final List<GrantedAuthority> AUTHORITIES = new ArrayList<>();
            AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
            AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_VIP2"));
            return new UsernamePasswordAuthenticationToken(authRequest.getPrincipal()
                    , authRequest.getCredentials(), AUTHORITIES);
        }
    }

}
