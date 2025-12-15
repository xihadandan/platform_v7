/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: JsonData.java
 *
 * @author zhulh
 * @date 2013-1-21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-21.1	zhulh		2013-1-21		Create
 * </pre>
 */
@ApiModel("JDS服务请求参数")
public class JsonData {

    @ApiModelProperty("租户ID")
    private String tenantId;
    @ApiModelProperty("用户Id")
    private String userId;
    @ApiModelProperty("服务名称")
    private String serviceName;
    @ApiModelProperty("方法名称")
    private String methodName;
    @ApiModelProperty("是否验证（默认值false）")
    private Boolean validate = false;
    @ApiModelProperty("版本号")
    private String version;
    @ApiModelProperty("是否认证（默认值false）")
    private Boolean authenticate = false;
    @ApiModelProperty("参数字符串jsonArray")
    private String args;
    @ApiModelProperty("参数类型集合")
    private List<String> argTypes;

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId 要设置的tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName 要设置的serviceName
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * @param methodName 要设置的methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return the validate
     */
    public Boolean getValidate() {
        return validate;
    }

    /**
     * @param validate 要设置的validate
     */
    public void setValidate(Boolean validate) {
        this.validate = validate;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the args
     */
    public String getArgs() {
        return args;
    }

    /**
     * @param args 要设置的args
     */
    public void setArgs(String args) {
        this.args = args;
    }

    /**
     * @return the argTypes
     */
    public List<String> getArgTypes() {
        return argTypes;
    }

    /**
     * @param argTypes 要设置的argTypes
     */
    public void setArgTypes(List<String> argTypes) {
        this.argTypes = argTypes;
    }

    public Boolean getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(Boolean authenticate) {
        this.authenticate = authenticate;
    }
}
