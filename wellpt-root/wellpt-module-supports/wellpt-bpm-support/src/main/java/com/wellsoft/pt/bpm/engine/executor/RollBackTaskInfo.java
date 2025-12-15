/*
 * @(#)8/30/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/30/23.1	zhulh		8/30/23		Create
 * </pre>
 * @date 8/30/23
 */
public class RollBackTaskInfo extends RollBackTask {

    // 退回的环节办理人
    private List<String> userIds;

    /**
     * @param id
     * @param name
     * @param taskInstUuid
     */
    public RollBackTaskInfo(String id, String name, String taskInstUuid) {
        super(id, name, taskInstUuid);
    }

    /**
     * @return the userIds
     */
    public List<String> getUserIds() {
        return userIds;
    }

    /**
     * @param userIds 要设置的userIds
     */
    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
