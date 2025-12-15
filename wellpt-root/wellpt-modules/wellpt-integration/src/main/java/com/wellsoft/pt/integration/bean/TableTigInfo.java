/*
 * @(#)2014-12-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.bean;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-12-3.1	zhulh		2014-12-3		Create
 * </pre>
 * @date 2014-12-3
 */
public class TableTigInfo implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5694119693924435357L;

    private String tableName;

    private String triggerName;

    private String status;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
