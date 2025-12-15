/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.management.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Description: 流程规划实体类日记
 */
@Entity
@Table(name = "WF_FLOW_SCHEMA_LOG")
@DynamicUpdate
@DynamicInsert
public class FlowSchemaLog extends IdEntity {

    private static final long serialVersionUID = 6926085098303257813L;

    private String content;

    /**
     * 流程定义JSON信息
     */
    private String definitionJson;

    private String parentFlowSchemaUUID;

    private String log;

    private BigDecimal flowVersion;

	/*@Transient
	private Integer recVer;

	@Transient
	private String creator;

	@Transient
	private String modifier;

	@Transient
	private Date modifyTime;*/

    public BigDecimal getFlowVersion() {
        return flowVersion;
    }

    public void setFlowVersion(BigDecimal flowVersion) {
        this.flowVersion = flowVersion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the definitionJson
     */
    public String getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    public String getParentFlowSchemaUUID() {
        return parentFlowSchemaUUID;
    }

    public void setParentFlowSchemaUUID(String parentFlowSchemaUUID) {
        this.parentFlowSchemaUUID = parentFlowSchemaUUID;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    /**
     * 获取流程规划的XML内容
     *
     * @return
     */
    @Transient
    public String getContentAsString() {
        try {
            return this.content;// IOUtils.toString(getContent().getCharacterStream());
        } catch (Exception e) {
            throw new WorkFlowException(e);
        }
    }

}
