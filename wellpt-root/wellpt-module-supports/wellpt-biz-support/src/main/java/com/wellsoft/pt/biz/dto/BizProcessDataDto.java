/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.support.DyFormDataDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
@ApiModel("过程节点数据传输对象")
public class BizProcessDataDto extends BizProcessDataRequestParamDto {
    private static final long serialVersionUID = -7702432808078429036L;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("过程节点信息显示位置")
    private String processNodePlaceHolder;

    @JsonDeserialize(using = DyFormDataDeserializer.class)
    @ApiModelProperty("表单数据")
    private DyFormData dyFormData;

    @ApiModelProperty("二开JS模块")
    private String jsModule;

    @ApiModelProperty("表单配置")
    private ProcessDefinitionJson.ProcessFormConfig formConfig;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the processNodePlaceHolder
     */
    public String getProcessNodePlaceHolder() {
        return processNodePlaceHolder;
    }

    /**
     * @param processNodePlaceHolder 要设置的processNodePlaceHolder
     */
    public void setProcessNodePlaceHolder(String processNodePlaceHolder) {
        this.processNodePlaceHolder = processNodePlaceHolder;
    }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * @param dyFormData 要设置的dyFormData
     */
    public void setDyFormData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

    /**
     * @return the jsModule
     */
    public String getJsModule() {
        return jsModule;
    }

    /**
     * @param jsModule 要设置的jsModule
     */
    public void setJsModule(String jsModule) {
        this.jsModule = jsModule;
    }

    /**
     * @return the formConfig
     */
    public ProcessDefinitionJson.ProcessFormConfig getFormConfig() {
        return formConfig;
    }

    /**
     * @param formConfig 要设置的formConfig
     */
    public void setFormConfig(ProcessDefinitionJson.ProcessFormConfig formConfig) {
        this.formConfig = formConfig;
    }

}
