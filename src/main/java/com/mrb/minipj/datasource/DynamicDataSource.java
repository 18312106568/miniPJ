package com.zhuowang.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 * @author Administrator
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource  {

	@Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }
}
