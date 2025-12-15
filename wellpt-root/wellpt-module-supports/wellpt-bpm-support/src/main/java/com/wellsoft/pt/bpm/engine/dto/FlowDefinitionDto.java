/*
 * @(#)3/5/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dto;

import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 3/5/24.1	zhulh		3/5/24		Create
 * </pre>
 * @date 3/5/24
 */
public class FlowDefinitionDto extends FlowDefinition {
    private static final long serialVersionUID = 5309762669262470412L;

    // 是否XML流程定义
    private boolean xmlDefinition;

    /**
     * @return the xmlDefinition
     */
    public boolean isXmlDefinition() {
        return xmlDefinition;
    }

    /**
     * @param xmlDefinition 要设置的xmlDefinition
     */
    public void setXmlDefinition(boolean xmlDefinition) {
        this.xmlDefinition = xmlDefinition;
    }
}
