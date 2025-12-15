/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import org.apache.commons.io.IOUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Clob;

/**
 * Description: 流程规划实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
@Entity
@Table(name = "WF_FLOW_SCHEMA")
@DynamicUpdate
@DynamicInsert
public class FlowSchema extends IdEntity {

    private static final long serialVersionUID = 6926085098303257813L;

    /**
     * 名称
     */
    private String name;
    /**
     * 内容
     */
    private Clob content;

    /**
     * 流程定义JSON信息
     */
    private Clob definitionJson;

    /**
     * 流程定义
     */
    // private FlowDefinition flowDefinition;

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
     * @return the content
     */
    public Clob getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(Clob content) {
        this.content = content;
    }

    /**
     * @return the definitionJson
     */
    public Clob getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(Clob definitionJson) {
        this.definitionJson = definitionJson;
    }

    //    /**
//     * @return the flowDefinition
//     */
//    @OneToOne(mappedBy = "flowSchema")
//    public FlowDefinition getFlowDefinition() {
//        return flowDefinition;
//    }
//
//    /**
//     * @param flowDefinition 要设置的flowDefinition
//     */
//    public void setFlowDefinition(FlowDefinition flowDefinition) {
//        this.flowDefinition = flowDefinition;
//    }

    /**
     * 获取流程规划的XML内容
     *
     * @return
     */
    @Transient
    public String getContentAsString() {
        try {
            if (this.content == null) {
                return null;
            }
            return IOUtils.toString(this.content.getCharacterStream());
        } catch (Exception e) {
            throw new WorkFlowException(e);
        }
    }

    /**
     * 获取流程规划的XML内容
     *
     * @return
     */
    @Transient
    public String getDefinitionJsonAsString() {
        try {
            if (this.definitionJson == null) {
                return null;
            }
            return IOUtils.toString(this.definitionJson.getCharacterStream());
        } catch (Exception e) {
            throw new WorkFlowException(e);
        }
    }

}
