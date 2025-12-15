/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/25/22.1	zhulh		10/25/22		Create
 * </pre>
 * @date 10/25/22
 */
public class BizConditionParam extends BaseObject {

    private String processInstUuid;

    private String entityName;

    private String entityId;

    private ProcessDefinitionJsonParser processDefinitionJsonParser;

    private Map<String, Object> paramMap;

    /**
     * @param processInstUuid
     * @param entityName
     * @param entityId
     * @param processDefinitionJsonParser
     */
    public BizConditionParam(String processInstUuid, String entityName, String entityId,
                             ProcessDefinitionJsonParser processDefinitionJsonParser) {
        this.processInstUuid = processInstUuid;
        this.entityName = entityName;
        this.entityId = entityId;
        this.processDefinitionJsonParser = processDefinitionJsonParser;
    }

    /**
     * @param paramMap
     */
    public BizConditionParam(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * @return the processInstUuid
     */
    public String getProcessInstUuid() {
        return processInstUuid;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the entityId
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * @return the processDefinitionJsonParser
     */
    public ProcessDefinitionJsonParser getProcessDefinitionJsonParser() {
        return processDefinitionJsonParser;
    }

    /**
     * @return the paramMap
     */
    public Map<String, Object> getParamMap() {
        return paramMap;
    }
}
