/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.job;

import org.quartz.JobDataMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
public class JobData extends JobDataMap {

    private static final long serialVersionUID = -426785433857414301L;

    public String getType() {
        return this.getString("type");
    }

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return this.getString("tenantId");
    }

    public String getUserId() {
        return this.getString("userId");
    }

    public String getJobUuid() {
        return this.getString("jobUuid");
    }


    public String getAssignIp() {
        return this.getString("assignIp");
    }

}
