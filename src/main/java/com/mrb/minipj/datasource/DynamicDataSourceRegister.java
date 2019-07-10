package com.mrb.minipj.datasource;

import com.mrb.minipj.constants.DynamicDbConstant;
import com.mrb.minipj.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.*;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 动态数据源注册<br/>
 * 启动动态数据源请在启动类中（如SpringBootSampleApplication）
 * 添加 @Import(DynamicDataSourceRegister.class)
 * @author Administrator
 *
 */
@Slf4j
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    // 数据源
    private DataSource defaultDataSource;
    public static Map<Object, Object> customDataSources = new HashMap<>();
    /**
     * 别名
     */
    private final   static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();

    static{
        aliases.addAliases("url", "jdbc-url");
        aliases.addAliases("username", "user");
    }
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        log.info("start init dynamic dataSource Registry");
        for (Object key : customDataSources.keySet()) {
            DynamicDataSourceContextHolder.dataSourceIds.add((String) key);
        }

        // 创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", customDataSources);
        registry.registerBeanDefinition(DynamicDbConstant.DS_DEFAULT_NAME, beanDefinition);

        log.info("end init dynamic dataSource Registry");
    }

    /**
     * 初始化数据源配置
     */
    @Override
    public void setEnvironment(Environment env) {
        initDefaultDataSource(env);
    }

    public static synchronized void addNewDataSource(Map<String,Object> dsMap,String key){

        customDataSources.put(key,bind(DynamicDbConstant.DATASOURCE_TYPE_CLASS,dsMap));
        DynamicDataSourceContextHolder.dataSourceIds.add(key);
        DynamicDataSource dynamicDataSource =SpringUtil
                .getApplicationContext().getBean(DynamicDataSource.class);
        dynamicDataSource.setTargetDataSources(customDataSources);
    }

    /**
     * 初始化主数据源
     *
     */
    private void initDefaultDataSource(Environment env) {
        Map<String, Object> dsMap = (Map) Binder.get(env)
                .bind("spring.datasource.primary", Map.class).orElse(null);
        defaultDataSource = bind(DynamicDbConstant.DATASOURCE_TYPE_CLASS,dsMap);
        customDataSources.put(DynamicDbConstant.HEAD_QUARTERS_KEY, defaultDataSource);
    }

    //绑定数据源
    public static <T extends DataSource> T bind(Class<T> clazz, Map properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(new ConfigurationPropertySource[]{source.withAliases(aliases)});
        // 通过类型绑定参数并获得实例对象
        return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz)).get();
    }

}
