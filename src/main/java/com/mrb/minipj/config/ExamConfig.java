package com.mrb.minipj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef="entityManagerFactoryExam",
        transactionManagerRef="transactionManagerExam",
        basePackages= { "com.mrb.minipj.repository.exam" }) //设置Repository所在位置
public class ExamConfig {

    @Autowired
    @Qualifier("examDataSource")
    private DataSource examDataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Bean(name = "entityManagerExam")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryExam(builder).getObject().createEntityManager();
    }
    @Bean(name = "entityManagerFactoryExam")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryExam (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(examDataSource)
                .properties(getVendorProperties())
                .packages("com.mrb.minipj") //设置实体类所在位置
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }

    private Map getVendorProperties() {
        return jpaProperties.getHibernateProperties(new HibernateSettings());
    }


    @Bean(name = "transactionManagerExam")
    PlatformTransactionManager transactionManagerExam(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryExam(builder).getObject());
    }


}
