/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.documentlink.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
@ApiModel("文档链接关系")
@Entity
@Table(name = "cd_document_link")
@DynamicUpdate
@DynamicInsert
public class CdDocumentLinkEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8257053637259915475L;

    // 业务类型
    @ApiModelProperty("业务类型")
    private String businessType;

    // 访问策略, 0不检验，1检验源数据，2检验目标数据，3任意数据，4全部
    @ApiModelProperty("访问策略, 0不检验，1检验源数据，2检验目标数据，3任意数据，4全部")
    private String accessStrategy;

    // 源数据UUID
    @ApiModelProperty("源数据UUID")
    private String sourceDataUuid;

    // 源数据检验器
    @ApiModelProperty("源数据检验器")
    private String sourceDataChecker;

    // 目标数据UUID
    @ApiModelProperty("目标数据UUID")
    private String targetDataUuid;

    // 目标数据检验器
    @ApiModelProperty("目标数据检验器")
    private String targetDataChecker;

    // 目标地址
    @ApiModelProperty("目标地址")
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
