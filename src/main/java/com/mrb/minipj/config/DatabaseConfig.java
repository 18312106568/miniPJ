package com.mrb.minipj.config;

import com.mrb.minipj.datasource.DynamicDataSource;
import com.mrb.minipj.datasource.DynamicDataSourceContextHolder;
import com.mrb.minipj.datasource.DynamicDataSourceRegister;
import com.mrb.minipj.entity.DrugCodeDbConfig;
import com.mrb.minipj.repository.DrugCodeDbConfigRepository;
import com.mrb.minipj.service.DataBaseService;
import com.mrb.minipj.utils.ConverUtils;
import com.mrb.minipj.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class DatabaseConfig implements ApplicationRunner {


    @Autowired
    DrugCodeDbConfigRepository dbConfigRepository;

    @Autowired
    ApplicationContext applicationContext;



    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(SpringUtil.getApplicationContext()==null){
            SpringUtil.setApplicationContext(applicationContext);
        }
        List<DrugCodeDbConfig> drugCodeDbConfigList = dbConfigRepository.findAll();
        if(drugCodeDbConfigList==null || drugCodeDbConfigList.isEmpty()){
            log.info("no more dbconfig!");
            return;
        }
        //初始化各公司药监码数据源
        DataBaseService.initDataBaseSources(drugCodeDbConfigList);
    }

}
