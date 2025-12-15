/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 交换数据路由规则
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-15.1	ruanhg		2013-11-15		Create
 * </pre>
 * @date 2013-11-15
 */
@Entity
@Table(name = "is_exchange_route")
@DynamicUpdate
@DynamicInsert
public class ExchangeRoute extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7594109189876695761L;
    //唯一识别号
    private String id;
    //编号用于排序
    private String code;
    //名称
    private String name;
    //
    private String restrain;
    //源数据类型
    private String fromTypeId;
    //目的类型
    private String toType;//type3仅由发件人指定的收件单位,type1直接选择收件单位,type2表单数据项指定收件单位
    //目的
    private String toId;
    //目的(系统)
    private String toField;
    //转换规则
    private String transformId;
    //重发次数
    private Integer retransmissionNum;
    //间隔秒数
    private Integer interval;
    //允许收件单位为空
    private String toEmpty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestrain() {
        return restrain;
    }

    public void setRestrain(String restrain) {
        this.restrain = restrain;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToField() {
        return toField;
    }

    public void setToField(String toField) {
        this.toField = toField;
    }

    public String getTransformId() {
        return transformId;
    }

    public void setTransformId(String transformId) {
        this.transformId = transformId;
    }

    public Integer getRetransmissionNum() {
        return retransmissionNum;
    }

    public void setRetransmissionNum(Integer retransmissionNum) {
        this.retransmissionNum = retransmissionNum;
    }

    @Column(name = "\"INTERVAL\"")
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getFromTypeId() {
        return fromTypeId;
    }

    public void setFromTypeId(String fromTypeId) {
        this.fromTypeId = fromTypeId;
    }

    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    public String getToEmpty() {
        return toEmpty;
    }

    public void setToEmpty(String toEmpty) {
        this.toEmpty = toEmpty;
    }

}
