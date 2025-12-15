/*
 * @(#)Mar 14, 2022 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.support;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 14, 2022.1	zhulh		Mar 14, 2022		Create
 * </pre>
 * @date Mar 14, 2022
 */
public class DocumentLinkInfo extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    // 业务类型
    private String businessType;

    // 访问策略, 0不检验，1检验源数据，2检验目标数据，3任意数据，4全部
    private String accessStrategy;

    // 源数据UUID
    private String sourceDataUuid;

    // 源数据检验器
    private String sourceDataChecker;

    // 目标数据UUID
    private String targetDataUuid;

    // 目标数据检验器
    private String targetDataChecker;

    // 目标地址
    private String targetUrl;

    /**
     * @return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * @param businessType 要设置的businessType
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * @return the accessStrategy
     */
    public String getAccessStrategy() {
        return accessStrategy;
    }

    /**
     * @param accessStrategy 要设置的accessStrategy
     */
    public void setAccessStrategy(String accessStrategy) {
        this.accessStrategy = accessStrategy;
    }

    /**
     * @return the sourceDataUuid
     */
    public String getSourceDataUuid() {
        return sourceDataUuid;
    }

    /**
     * @param sourceDataUuid 要设置的sourceDataUuid
     */
    public void setSourceDataUuid(String sourceDataUuid) {
        this.sourceDataUuid = sourceDataUuid;
    }

    /**
     * @return the sourceDataChecker
     */
    public String getSourceDataChecker() {
        return sourceDataChecker;
    }

    /**
     * @param sourceDataChecker 要设置的sourceDataChecker
     */
    public void setSourceDataChecker(String sourceDataChecker) {
        this.sourceDataChecker = sourceDataChecker;
    }

    /**
     * @return the targetDataUuid
     */
    public String getTargetDataUuid() {
        return targetDataUuid;
    }

    /**
     * @param targetDataUuid 要设置的targetDataUuid
     */
    public void setTargetDataUuid(String targetDataUuid) {
        this.targetDataUuid = targetDataUuid;
    }

    /**
     * @return the targetDataChecker
     */
    public String getTargetDataChecker() {
        return targetDataChecker;
    }

    /**
     * @param targetDataChecker 要设置的targetDataChecker
     */
    public void setTargetDataChecker(String targetDataChecker) {
        this.targetDataChecker = targetDataChecker;
    }

    /**
     * @return the targetUrl
     */
    public String getTargetUrl() {
        return targetUrl;
    }

    /**
     * @param targetUrl 要设置的targetUrl
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

}
