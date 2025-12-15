/*
 * @(#)2019年5月24日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.support;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月24日.1	zhulh		2019年5月24日		Create
 * </pre>
 * @date 2019年5月24日
 */
public class LicenseContent {
    // 项目的唯一识别码，例如机器码，从平台的系统设置->许可管理页面中获取
    private String subject;
    // 发布日期
    private Date issued;
    // 有效开始日期
    private Date notBefore;
    // 有效截止日期
    private Date notAfter;
    // 序列号
    private String serialNumber;
    // 许可用户类型
    private String consumerType;
    // 许可用户数量
    private int consumerAmount = 1;
    // 说明信息
    private String info;
    // 附加数据
    private Object extra;

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject 要设置的subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the issued
     */
    public Date getIssued() {
        return issued;
    }

    /**
     * @param issued 要设置的issued
     */
    public void setIssued(Date issued) {
        this.issued = issued;
    }

    /**
     * @return the notBefore
     */
    public Date getNotBefore() {
        return notBefore;
    }

    /**
     * @param notBefore 要设置的notBefore
     */
    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    /**
     * @return the notAfter
     */
    public Date getNotAfter() {
        return notAfter;
    }

    /**
     * @param notAfter 要设置的notAfter
     */
    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    /**
     * @return the serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @param serialNumber 要设置的serialNumber
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * @return the consumerType
     */
    public String getConsumerType() {
        return consumerType;
    }

    /**
     * @param consumerType 要设置的consumerType
     */
    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    /**
     * @return the consumerAmount
     */
    public int getConsumerAmount() {
        return consumerAmount;
    }

    /**
     * @param consumerAmount 要设置的consumerAmount
     */
    public void setConsumerAmount(int consumerAmount) {
        this.consumerAmount = consumerAmount;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info 要设置的info
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the extra
     */
    public Object getExtra() {
        return extra;
    }

    /**
     * @param extra 要设置的extra
     */
    public void setExtra(Object extra) {
        this.extra = extra;
    }

}
