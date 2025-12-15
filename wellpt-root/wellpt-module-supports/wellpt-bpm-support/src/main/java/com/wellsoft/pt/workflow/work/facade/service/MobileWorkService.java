/*
 * @(#)2015-2-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.workflow.work.bean.WorkBean;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-27.1	zhulh		2015-2-27		Create
 * </pre>
 * @date 2015-2-27
 */
public interface MobileWorkService extends BaseService {

    WorkBean getTodoWorkData(String taskInstUuid, String flowInstUuid);

    WorkBean getDoneWorkData(String taskInstUuid, String flowInstUuid);

    public abstract WorkBean getTodo(String taskInstUuid, String flowInstUuid);

    public abstract WorkBean getUnread(String taskInstUuid, String flowInstUuid, boolean openToRead);

    public abstract WorkBean getRead(String taskInstUuid, String flowInstUuid, boolean openToRead);

    public abstract WorkBean getAttention(String taskInstUuid, String flowInstUuid);

    public abstract WorkBean getOver(String taskInstUuid, String flowInstUuid);

    public abstract WorkBean getSupervise(String taskInstUuid, String flowInstUuid);

    public abstract WorkBean getMonitor(String taskInstUuid, String flowInstUuid);

    public abstract WorkBean getDone(String taskInstUuid, String flowInstUuid);

    public abstract WorkBean getWork(String taskInstUuid, String flowInstUuid);

    public abstract WorkBean getShare(String taskUuid, String flowInstUuid);

}
