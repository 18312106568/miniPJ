package com.mrb.minipj.entity;

import com.mrb.minipj.entity.base.BaseEntity;
import com.mrb.minipj.entity.base.IdEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 药品监控码上传配置
 */
@Entity
@Table(name = "ZX_BMS_DRUG_CODE_DB_CONFIG")
@Data
public class DrugCodeDbConfig extends BaseEntity {

    /**
     * 唯一标识
     */
    @Column( name = "KEY")
    private String key;

    /**
     * 数据库名
     */
    @Column( name = "NAME")
    private String name;

    /**
     * 数据库驱动
     */
    @Column( name = "DRIVER_CLASS")
    private String driverClassName;

    /**
     * 数据库连接
     */
    @Column( name ="URL")
    private String url;

    /**
     * 用户名
     */
    @Column( name ="USERNAME")
    private String username;

    /**
     * 密码
     */
    @Column( name ="PASSWORD")
    private String password;

}
