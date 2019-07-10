package com.mrb.minipj;



import com.mrb.minipj.datasource.DynamicDataSourceRegister;
import com.mrb.minipj.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 *
 * @author MRB
 */
@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@Import({DynamicDataSourceRegister.class})
public class Application {
    public static void main(String args[]){
        SpringUtil.setApplicationContext(SpringApplication.run(Application.class, args));
    }

}
