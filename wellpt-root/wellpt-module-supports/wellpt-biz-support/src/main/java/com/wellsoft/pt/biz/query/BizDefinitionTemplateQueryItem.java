/*
 * @(#)11/24/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/24/22.1	zhulh		11/24/22		Create
 * </pre>
 * @date 11/24/22
 */
public class BizDefinitionTemplateQueryItem implements BaseQueryItem {
    private static final long serialVersionUID = 9035962165405998045L;

    private String uuid;

    private String name;

    private String type;

    private String itemId;

    private String nodeId;

    private String processDefUuid;

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
