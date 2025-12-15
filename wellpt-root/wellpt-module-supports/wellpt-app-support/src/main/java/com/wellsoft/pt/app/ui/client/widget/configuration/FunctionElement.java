/*
 * @(#)2019年6月18日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月18日.1	zhulh		2019年6月18日		Create
 * </pre>
 * @date 2019年6月18日
 */
@ApiModel
public class FunctionElement extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4575295280373771703L;
    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("编码")
    private String code;

    // 组件元素类型type与functionType不能同时有值
    @ApiModelProperty("类型")
    private String type;

    // 功能类型functionType与type不能同时有值
    @ApiModelProperty("功能类型")
    private String functionType;

    @ApiModelProperty("功能元素导出类型")
    private String exportType;

    @ApiModelProperty("是否引用")
    private Boolean ref;

    private Boolean isProtected;


    private String resourceId;
    private String resourceType;


    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
        if (StringUtils.isNotBlank(this.type) && StringUtils.isNotBlank(this.functionType)) {
            throw new RuntimeException("组件元素类型type与functionType不能同时有值");
        }
    }

    /**
     * @return the functionType
     */
    public String getFunctionType() {
        return functionType;
    }

    /**
     * @param functionType 要设置的functionType
     */
    public void setFunctionType(String functionType) {
        this.functionType = functionType;
        if (StringUtils.isNotBlank(this.functionType) && StringUtils.isNotBlank(this.type)) {
            throw new RuntimeException("功能类型functionType与type不能同时有值");
        }
    }

    /**
     * @return the exportType
     */
    public String getExportType() {
        return exportType;
    }

    /**
     * @param exportType 要设置的exportType
     */
    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    /**
     * @return
     */
    public Boolean getRef() {
        return ref;
    }

    /**
     * @param ref
     */
    public void setRef(Boolean ref) {
        this.ref = ref;
    }

    /**
     * @return
     */
    public boolean isRef() {
        return BooleanUtils.isTrue(ref);
    }

    public Boolean getIsProtected() {
        return isProtected;
    }

    public void setIsProtected(Boolean aProtected) {
        isProtected = aProtected;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
