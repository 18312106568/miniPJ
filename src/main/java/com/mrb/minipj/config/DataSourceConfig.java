package com.mrb.minipj.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    /**
     * 公共库
     * @return
     */
    @Primary
    @Bean(name = "primaryDataSourceProperties")
    @Qualifier("primaryDataSourceProperties")
    @ConfigurationProperties(prefix="spring.datasource.primary")
    public DataSourceProperties  primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "primaryDataSource")
    public HikariDataSource dataSourcePrimary() {
        //return DruidDataSourceBuilder.create().build(); //使用druidCP时打开这个注释，同时注释掉下面一行
        return primaryDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }



    /**
     * 考试库
     * @return
     */
    @Bean(name = "examDataSourceProperties")
    @Qualifier("examDataSourceProperties")
    @ConfigurationProperties(prefix="spring.datasource.exam")
    public DataSourceProperties examDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "examDataSource")
    public HikariDataSource dataSourceExam() {
        return examDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();

    }




}
