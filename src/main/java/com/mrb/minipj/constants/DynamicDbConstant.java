package com.mrb.minipj.constants;

public class DynamicDbConstant {
    /**
     * 默认数据库驱动类型
     */
    public static final String DATASOURCE_TYPE_DEFAULT = "com.zaxxer.hikari.HikariDataSource";
    public static final Class DATASOURCE_TYPE_CLASS = com.zaxxer.hikari.HikariDataSource.class;

    /**
     * 默认动态数据源名
     */
    public static final String DS_DEFAULT_NAME = "dynamicDataSource";

    /**
     * 本部BMS数据库 key
     */
    public static final String HEAD_QUARTERS_KEY = "bms";

}
