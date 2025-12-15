/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-3.1	ruanhg		2014-10-3		Create
 * </pre>
 * @date 2014-10-3
 */
@Entity
@Table(name = "is_data_operation_log")
@DynamicUpdate
@DynamicInsert
public class DataOperationLog extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2553037356477986505L;

    private Integer stableType;//1实体表/2非实体表/3关系表/4文件夹

    private String stableName;//实体名/表名

    private String suuid;//数据uuid

    private Integer srecVer;

    private String screator;

    private Date screateTime;

    private String smodifier;

    private Date smodifyTime;
    @UnCloneable
    private Clob content;

    private Date synTime;

    private Integer status;//1未同步/2同步中/3同步失败/4同步成功/5免同步/6反馈

    private Integer direction;//1内/2外

    private Integer action;//1变更/2删/3添加/4更新

    private String byzd0;//用于反馈的状态

    private String byzd1;//用于反馈的状态

    private String byzd2;

    private String byzd3;

    private String byzd4;

    private String byzd5;

    private String byzd6;

    private String byzd7;
    @UnCloneable
    private Clob byzd8;
    @UnCloneable
    private Clob byzd9;

    private String remark;

    public Integer getStableType() {
        return stableType;
    }

    public void setStableType(Integer stableType) {
        this.stableType = stableType;
    }

    public String getStableName() {
        return stableName;
    }

    public void setStableName(String stableName) {
        this.stableName = stableName;
    }

    public String getSuuid() {
        return suuid;
    }

    public void setSuuid(String suuid) {
        this.suuid = suuid;
    }

    public Integer getSrecVer() {
        return srecVer;
    }

    public void setSrecVer(Integer srecVer) {
        this.srecVer = srecVer;
    }

    public String getScreator() {
        return screator;
    }

    public void setScreator(String screator) {
        this.screator = screator;
    }

    public Date getScreateTime() {
        return screateTime;
    }

    public void setScreateTime(Date screateTime) {
        this.screateTime = screateTime;
    }

    public String getSmodifier() {
        return smodifier;
    }

    public void setSmodifier(String smodifier) {
        this.smodifier = smodifier;
    }

    public Date getSmodifyTime() {
        return smodifyTime;
    }

    public void setSmodifyTime(Date smodifyTime) {
        this.smodifyTime = smodifyTime;
    }

    public Clob getContent() {
        return content;
    }

    public void setContent(Clob content) {
        this.content = content;
    }

    public Date getSynTime() {
        return synTime;
    }

    public void setSynTime(Date synTime) {
        this.synTime = synTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public String getByzd0() {
        return byzd0;
    }

    public void setByzd0(String byzd0) {
        this.byzd0 = byzd0;
    }

    public String getByzd2() {
        return byzd2;
    }

    public void setByzd2(String byzd2) {
        this.byzd2 = byzd2;
    }

    public String getByzd3() {
        return byzd3;
    }

    public void setByzd3(String byzd3) {
        this.byzd3 = byzd3;
    }

    public String getByzd4() {
        return byzd4;
    }

    public void setByzd4(String byzd4) {
        this.byzd4 = byzd4;
    }

    public String getByzd5() {
        return byzd5;
    }

    public void setByzd5(String byzd5) {
        this.byzd5 = byzd5;
    }

    public String getByzd6() {
        return byzd6;
    }

    public void setByzd6(String byzd6) {
        this.byzd6 = byzd6;
    }

    public String getByzd7() {
        return byzd7;
    }

    public void setByzd7(String byzd7) {
        this.byzd7 = byzd7;
    }

    public Clob getByzd8() {
        return byzd8;
    }

    public void setByzd8(Clob byzd8) {
        this.byzd8 = byzd8;
    }

    public Clob getByzd9() {
        return byzd9;
    }

    public void setByzd9(Clob byzd9) {
        this.byzd9 = byzd9;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getByzd1() {
        return byzd1;
    }

    public void setByzd1(String byzd1) {
        this.byzd1 = byzd1;
    }

}
