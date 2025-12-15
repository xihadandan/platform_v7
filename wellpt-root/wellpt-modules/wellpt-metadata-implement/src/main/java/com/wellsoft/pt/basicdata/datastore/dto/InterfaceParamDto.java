package com.wellsoft.pt.basicdata.datastore.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 * 接口参数
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/1   Create
 * </pre>
 */
@ApiModel("接口参数")
public class InterfaceParamDto {

    @ApiModelProperty("字段名称")
    private String name;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("节点类型")
    private String domType;

    @ApiModelProperty("id名称")
    private String id;

    @ApiModelProperty("JSON数据")
    private String dataJSON;

    @ApiModelProperty("service")
    private String service;

    @ApiModelProperty("placeholder")
    private String placeholder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue 要设置的defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDomType() {
        return domType;
    }

    public void setDomType(String domType) {
        this.domType = domType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataJSON() {
        return dataJSON;
    }

    public void setDataJSON(String dataJSON) {
        this.dataJSON = dataJSON;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
