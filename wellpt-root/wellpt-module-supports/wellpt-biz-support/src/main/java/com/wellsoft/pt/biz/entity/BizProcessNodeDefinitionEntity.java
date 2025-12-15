/*
 * @(#)11/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务流程过程节点定义基本信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/13/23.1	zhulh		11/13/23		Create
 * </pre>
 * @date 11/13/23
 */
@ApiModel("业务流程过程节点定义基本信息")
@Entity
@Table(name = "BIZ_PROCESS_NODE_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class BizProcessNodeDefinitionEntity extends IdEntity {

    @ApiModelProperty("过程节点名称")
    private String name;

    @ApiModelProperty("过程节点ID")
    private String id;

    @ApiModelProperty("过程节点编号")
    private String code;

    @ApiModelProperty("扩展定义JSON信息")
    private String extAttrsJson;

    @ApiModelProperty("排序号")
    private Integer sortOrder;

    @ApiModelProperty("上级过程节点ID")
    private String parentId;

    @ApiModelProperty("过程定义UUID")
    private String processDefUuid;

    @ApiModelProperty("引用的过程定义UUID")
    private String refProcessDefUuid;

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
     * @return the extAttrsJson
     */
    public String getExtAttrsJson() {
        return extAttrsJson;
    }

    /**
     * @param extAttrsJson 要设置的extAttrsJson
     */
    public void setExtAttrsJson(String extAttrsJson) {
        this.extAttrsJson = extAttrsJson;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId 要设置的parentId
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the processDefUuid
     */
    public String getProcessDefUuid() {
        return processDefUuid;
    }

    /**
     * @param processDefUuid 要设置的processDefUuid
     */
    public void setProcessDefUuid(String processDefUuid) {
        this.processDefUuid = processDefUuid;
    }

    /**
     * @return the refProcessDefUuid
     */
    public String getRefProcessDefUuid() {
        return refProcessDefUuid;
    }

    /**
     * @param refProcessDefUuid 要设置的refProcessDefUuid
     */
    public void setRefProcessDefUuid(String refProcessDefUuid) {
        this.refProcessDefUuid = refProcessDefUuid;
    }
}
