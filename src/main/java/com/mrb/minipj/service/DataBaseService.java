package com.mrb.minipj.service;

import com.mrb.minipj.constants.DynamicDbConstant;
import com.mrb.minipj.datasource.DynamicDataSource;
import com.mrb.minipj.datasource.DynamicDataSourceContextHolder;
import com.mrb.minipj.datasource.DynamicDataSourceRegister;
import com.mrb.minipj.entity.DrugCodeDbConfig;
import com.mrb.minipj.utils.ConverUtils;
import com.mrb.minipj.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class DataBaseService {

    private static String[] dsProperties = {"driverClassName","url","username","password"};

    public  static void addNewDataBaseSource(Map<String,Object> dsMap, String key){

        DynamicDataSourceRegister.customDataSources.put(
                key,DynamicDataSourceRegister.bind(DynamicDbConstant.DATASOURCE_TYPE_CLASS,dsMap));

        //获取动态数据源
        DynamicDataSource dynamicDataSource =SpringUtil
                .getApplicationContext().getBean(DynamicDataSource.class);
        dynamicDataSource.setTargetDataSources( DynamicDataSourceRegister.customDataSources);
    }

    public static void initDataBaseSources(List<DrugCodeDbConfig> drugCodeDbConfigList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = DrugCodeDbConfig.class;
        for(DrugCodeDbConfig dbConfig : drugCodeDbConfigList){
            Map<String,Object> dsMap = new HashMap<>();
            for(String dsProperty : dsProperties){
                Method method = clazz.getDeclaredMethod(ConverUtils.toGetName(dsProperty));
                Object obj = method.invoke(dbConfig);
                dsMap.put(dsProperty,obj);
            }
            addNewDataBaseSource(dsMap,dbConfig.getKey());
            log.info("init new datasource {}",dbConfig.getKey());
        }
    }
}
