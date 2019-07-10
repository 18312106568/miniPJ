package com.mrb.minipj.entity;

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
@Table(name = "ZX_BMS_DRUG_CODE_UP_SETTING")
@Data
public class DrugCodeUpSetting implements Serializable {

    /**
     * 货品ID
     */
    @Id
    @Column(name = "GOODSID")
    private Long goodsId;

    /**
     * 状态 0-作废，1-正常
     */
    @Column( name = "STATE")
    private Boolean state;

    /**
     * 上传频率 1-每天，2-每周（周一），3-每月（第一天）
     */
    @Column( name = "UPLOAD_FREQUENCY")
    private Integer uploadFrequency;

    /**
     * 接收邮箱
     */
    @Column( name = "RECEIVED_MAIL")
    private String receivedMail;

    /**
     * 修改人ID
     */
    @Column( name = "MODIFIERID")
    private Long modifierId;

    /**
     * 修改日期
     */
    @Column( name = "MODIFIER_DATE")
    private Date modifierDate;

    /**
     * 上次上传时间
     */
    @Column( name = "LASTUPLOAD_TIME")
    private Date lastuploadTime;

    /**
     * 备注
     */
    @Column( name ="REMARKS")
    private String remarks;
}
