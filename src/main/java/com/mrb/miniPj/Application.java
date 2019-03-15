package com.mrb.miniPj;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author MRB
 */
/**
 * Document: https://nacos.io/zh-cn/docs/quick-start-spring-boot.html
 * <p>
 * Nacos 控制台添加配置：
 * <p>
 * Data ID：mini-nacos
 * <p>
 * Group：DEFAULT_GROUP
 * <p>
 * 配置内容：useLocalCache=true
 */
@SpringBootApplication
@NacosPropertySource(dataId = "mini-nacos", autoRefreshed = true)
public class Application {
    public static void main(String args[]){
        SpringApplication.run(Application.class, args);
    }

    @Controller
    @RequestMapping(value = "/config")
    static class ConfigController {

        @NacosValue(value = "${useLocalCache:124}", autoRefreshed = true)
        private String useLocalCache;

        @RequestMapping(value = "/test-get")
        @ResponseBody
        public String testGet() {
            return this.useLocalCache;
        }
    }

    @RestController
    @RequestMapping("/cache")
    static class CacheController {

        @Autowired
        StringRedisTemplate stringRedisTemplate;

        @RequestMapping("/get-key")
        public String getKey(@RequestParam("name")String name){
            String result = stringRedisTemplate.opsForValue().get(name);
            if(StringUtils.isEmpty(result)){
                return "no exit!";
            }
            return result;
        }
    }

}
