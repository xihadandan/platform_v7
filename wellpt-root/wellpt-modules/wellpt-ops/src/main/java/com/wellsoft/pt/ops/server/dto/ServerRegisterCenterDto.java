/*
 * @(#)2019-07-30 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.ops.server.dto;

import java.io.Serializable;


/**
 * Description: 数据库表SERVER_REGISTER_CENTER的对应的DTO类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-30.1	chenq		2019-07-30		Create
 * </pre>
 * @date 2019-07-30
 */
public class ServerRegisterCenterDto implements Serializable {

    private static final long serialVersionUID = 1564473747305L;

    // 应用端口
    private Integer port;
    // 应用名称
    private String name;
    // 应用IP
    private String ip;
    // 描述信息
    private String remark;

    private String machine;

    /**
     * @return the port
     */
    public Integer getPort() {
        return this.port;
    }

    /**
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return this.remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }
}
