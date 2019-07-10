package com.mrb.test;

import com.mrb.minipj.Application;
import com.mrb.minipj.datasource.DynamicDataSourceContextHolder;
import com.mrb.minipj.datasource.DynamicDataSourceRegister;
import com.mrb.minipj.entity.DrugCodeUpSetting;
import com.mrb.minipj.repository.DrugCodeUpSettingRepository;
import com.mrb.minipj.utils.SpringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)

@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class TestBinder {


    @Autowired
    private Environment environment;

    @Autowired
    private DrugCodeUpSettingRepository drugCodeUpSettingRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testDrugCodeRepository() throws InterruptedException {

//        Map<String, Object> dsMap = new HashMap<>();
//        dsMap.put("driverClassName","oracle.jdbc.driver.OracleDriver");
//        dsMap.put("url","jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.11.152)(PORT = 1521)))(CONNECT_DATA =(SERVICE_NAME = dbtest)))");
//        dsMap.put("username","gydzmis");
//        dsMap.put("password","gydzmis");
//        DynamicDataSourceRegister.addNewDataSource(dsMap,"dz");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<DrugCodeUpSetting> drugCodeUpSettingList=  drugCodeUpSettingRepository.findAll();
                System.out.println(drugCodeUpSettingList);

                DynamicDataSourceContextHolder.setDataSourceType("dz");
                drugCodeUpSettingList=  drugCodeUpSettingRepository.findAll();
                System.out.println(drugCodeUpSettingList);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DynamicDataSourceContextHolder.setDataSourceType("xt");
                List<DrugCodeUpSetting> drugCodeUpSettingList=  drugCodeUpSettingRepository.findAll();
                System.out.println(drugCodeUpSettingList);
            }
        }).start();
        Thread.sleep(1000);

    }

    @Test
    public void testEnv(){
        Map<String, Object> dataSourceMap = (Map)Binder.get(environment)
                .bind("datasource.hikari", Map.class).orElse(null);
        System.out.println(dataSourceMap);
    }
}
