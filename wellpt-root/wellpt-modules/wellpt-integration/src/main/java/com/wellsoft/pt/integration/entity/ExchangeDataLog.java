/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 交换数日志
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
@Table(name = "is_exchange_data_log")
@DynamicUpdate
@DynamicInsert
public class ExchangeDataLog extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4458233843524335051L;

    private String batchId;

    private String dataId;

    private Integer dataRecVer;
    //数据节点
    //  0、验证数据是否存在
    //	1、调问平台接收数据接口日志（调用接口）
    //	2、保存数据到交换平台（保存数据）
    //	3、调问终端上传结果回调接口日志（平台告诉终端上传结果）
    //	4、发送到无系统单位成功（发送到无系统单位成功）
    //	41、将请求终端接收数据接口请求加入队列
    //	5、调用终端接收数据接口日志
    //	6、调用平台抄送结果回调接口日志
    //	7、调用终端路由结果接口日志
    //	8、调用平台签收结果接口日志
    //	9、调用终端签收结果接口日志
    // 10、成功撤回无系统单位日志
    // 11、请求撤回
    // 12、调用系统撤回
    // 13、补发到平台单位成功
    // 14、消息补发到单位系统
    // 15、终端撤销回调平台
    // 16、调用系统撤回回调接口
    // 17、重发任务
    // 18、验证数据
    // 19、调用自定义模块方法
    // 20、请求历史数据
    // 21、调用查询接口
    private Integer node;

    private String fromUnitId;

    private String toUnitId;

    //业务处理情况
    private Integer status;
    //返回的结果对应respone
    private Integer code;
    //返回结果的说明对应respone
    private String msg;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getNode() {
        return node;
    }

    public void setNode(Integer node) {
        this.node = node;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getFromUnitId() {
        return fromUnitId;
    }

    public void setFromUnitId(String fromUnitId) {
        this.fromUnitId = fromUnitId;
    }

    public String getToUnitId() {
        return toUnitId;
    }

    public void setToUnitId(String toUnitId) {
        this.toUnitId = toUnitId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getDataRecVer() {
        return dataRecVer;
    }

    public void setDataRecVer(Integer dataRecVer) {
        this.dataRecVer = dataRecVer;
    }

}
