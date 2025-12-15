/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/13/25.1	    zhulh		6/13/25		    Create
 * </pre>
 * @date 6/13/25
 */
@Entity
@Table(name = "FULLTEXT_MODEL")
@DynamicUpdate
@DynamicInsert
@ApiModel("全文检索数据模型")
public class FulltextModelEntity extends SysEntity {
    private static final long serialVersionUID = -1414878788694345965L;

    @ApiModelProperty("索引分类UUID")
    private Long categoryUuid;

    @ApiModelProperty("数据模型UUID")
    private Long dataModelUuid;

    @ApiModelProperty("数据模型索引条件定义")
    private String matchJson;

    @ApiModelProperty("查看数据的表单来源，auto数据创建表单，custom指定表单")
    private String viewDataFormSource;

    @ApiModelProperty("查看数据的表单定义UUID")
    private String viewDataFormUuid;

    /**
     * @return the categoryUuid
     */
    public Long getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(Long categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    /**
     * @return the dataModelUuid
     */
    public Long getDataModelUuid() {
        return dataModelUuid;
    }

    /**
     * @param dataModelUuid 要设置的dataModelUuid
     */
    public void setDataModelUuid(Long dataModelUuid) {
        this.dataModelUuid = dataModelUuid;
    }

    /**
     * @return the matchJson
     */
    public String getMatchJson() {
        return matchJson;
    }

    /**
     * @param matchJson 要设置的matchJson
     */
    public void setMatchJson(String matchJson) {
        this.matchJson = matchJson;
    }

    /**
     * @return the viewDataFormSource
     */
    public String getViewDataFormSource() {
        return viewDataFormSource;
    }

    /**
     * @param viewDataFormSource 要设置的viewDataFormSource
     */
    public void setViewDataFormSource(String viewDataFormSource) {
        this.viewDataFormSource = viewDataFormSource;
    }

    /**
     * @return the viewDataFormUuid
     */
    public String getViewDataFormUuid() {
        return viewDataFormUuid;
    }

    /**
     * @param viewDataFormUuid 要设置的viewDataFormUuid
     */
    public void setViewDataFormUuid(String viewDataFormUuid) {
        this.viewDataFormUuid = viewDataFormUuid;
    }

}
