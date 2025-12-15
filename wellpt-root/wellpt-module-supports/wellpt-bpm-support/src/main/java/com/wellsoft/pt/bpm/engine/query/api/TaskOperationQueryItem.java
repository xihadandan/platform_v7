/*
 * @(#)2015-10-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-10-19.1	zhulh		2015-10-19		Create
 * </pre>
 * @date 2015-10-19
 */
public class TaskOperationQueryItem extends TaskOperation implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2769400028628824127L;

    // 归属流程实例UUID
    private String belongToFlowInstUuid;

    // 归属环节实例UUID
    private String belongToTaskInstUuid;

    // 归属环节ID
    private String belongToTaskId;

    /**
     * @return the belongToFlowInstUuid
     */
    public String getBelongToFlowInstUuid() {
        return belongToFlowInstUuid;
    }

    /**
     * @param belongToFlowInstUuid 要设置的belongToFlowInstUuid
     */
    public void setBelongToFlowInstUuid(String belongToFlowInstUuid) {
        this.belongToFlowInstUuid = belongToFlowInstUuid;
    }

    /**
     * @return the belongToTaskInstUuid
     */
    public String getBelongToTaskInstUuid() {
        return belongToTaskInstUuid;
    }

    /**
     * @param belongToTaskInstUuid 要设置的belongToTaskInstUuid
     */
    public void setBelongToTaskInstUuid(String belongToTaskInstUuid) {
        this.belongToTaskInstUuid = belongToTaskInstUuid;
    }

    /**
     * @return the belongToTaskId
     */
    public String getBelongToTaskId() {
        return belongToTaskId;
    }

    /**
     * @param belongToTaskId 要设置的belongToTaskId
     */
    public void setBelongToTaskId(String belongToTaskId) {
        this.belongToTaskId = belongToTaskId;
    }

}
