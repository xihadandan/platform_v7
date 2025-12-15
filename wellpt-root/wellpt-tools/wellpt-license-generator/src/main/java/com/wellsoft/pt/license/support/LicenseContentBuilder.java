/*
 * @(#)3/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.support;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 3/29/24.1	zhulh		3/29/24		Create
 * </pre>
 * @date 3/29/24
 */
public class LicenseContentBuilder {
    // 项目的唯一识别码，例如机器码，从平台的系统设置->许可管理页面中获取
    private String subject;
    // 发布日期
    private Date issued;
    // 有效开始日期
    private Date notBefore;
    // 有效截止日期
    private Date notAfter;
    // 序列号
    private String serialNumber=UUID.randomUUID().toString();
    // 许可用户类型
    private String consumerType = "user";
    // 许可用户数量
    private int consumerAmount = Integer.MAX_VALUE;
    // 说明信息
    private String info;
    // 附加数据
    private Map<String,Object> extra = new HashMap<>();

    private LicenseContentBuilder() {}

    public static  LicenseContentBuilder create() {
        return new LicenseContentBuilder();
    }

    public LicenseContentBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }

    public LicenseContentBuilder issued(Date issued) {
        this.issued = issued;
        return this;
    }

    public LicenseContentBuilder notBefore(Date notBefore) {
        this.notBefore = notBefore;
        return this;
    }

    public LicenseContentBuilder notAfter(Date notAfter) {
        this.notAfter = notAfter;
        return this;
    }

    public LicenseContentBuilder consumerAmount(int consumerAmount) {
        this.consumerAmount = consumerAmount;
        return this;
    }

    public LicenseContentBuilder info(String info) {
        this.info = info;
        return this;
    }

    public LicenseContentBuilder extra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }

    public LicenseContent build() {
        LicenseContent content = new LicenseContent();
        content.setSubject(this.subject);
        content.setIssued(this.issued);
        content.setNotBefore(this.notBefore);
        content.setNotAfter(this.notAfter);
        content.setSerialNumber(this.serialNumber);
        content.setConsumerType(consumerType);
        content.setConsumerAmount(consumerAmount);
        content.setInfo(this.info);
        content.setExtra(this.extra);
        return content;
    }

}
