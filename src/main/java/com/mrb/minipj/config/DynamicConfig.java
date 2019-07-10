package com.mrb.minipj.config;

import com.mrb.minipj.constants.DynamicDbConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryDynamic",
        transactionManagerRef = "transactionManagerDynamic",
        basePackages = {"com.mrb.minipj.repository"}//设置Repository所在位置
)
public class DynamicConfig {

    @Autowired
    @Qualifier(value = "dynamicDataSource") //配置中定义的名字
    private DataSource dynamicDataSource;

    @Bean(name = "entityManagerFactoryDynamic")
    @Primary
    public EntityManagerFactory entityManagerFactoryDynamic() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.mrb.minipj..entity");
        factory.setDataSource(dynamicDataSource);//数据源
        factory.setPersistenceUnitName("dynamicPersistenceUnit");
//        Properties properties = new Properties();
//        properties.put("hibernate.show_sql", true);
//        properties.put("hibernate.dialect","org.hibernate.dialect.Oracle10gDialect");
//        factory.setJpaProperties(properties);
        factory.afterPropertiesSet();//在完成了其它所有相关的配置加载以及属性设置后,才初始化
        return factory.getObject();
    }

    @Bean(name = "transactionManagerDynamic")
    @Primary
    PlatformTransactionManager transactionManagerDynamic() {
        return new JpaTransactionManager(entityManagerFactoryDynamic());
    }
}
