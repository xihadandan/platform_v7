/*
 * @(#)8/15/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/15/24.1	    zhulh		8/15/24		    Create
 * </pre>
 * @date 8/15/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefinitionTemplateInfo extends BaseObject {
    private static final long serialVersionUID = -326187131748667600L;

    private String name;

    private String type;

    private String itemId;

    private String nodeId;

    private String processDefUuid;

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
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId 要设置的itemId
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId 要设置的nodeId
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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
}
