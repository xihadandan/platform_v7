package com.wellsoft.pt.xxljob.model;

import com.xxl.job.core.util.GsonTool;

import java.util.HashMap;

/**
 * 执行器参数
 */
public class ExecutionParam extends HashMap<String, String> {

    private String tenantId;
    private String userId;

    public String getTenantId() {
        return super.get("tenantId");
    }

    public ExecutionParam setTenantId(String tenantId) {
        this.tenantId = tenantId;
        super.put("tenantId", tenantId);
        return this;
    }

    public String getUserId() {
        return super.get("userId");
    }

    public ExecutionParam setUserId(String userId) {
        this.userId = userId;
        super.put("userId", userId);
        return this;
    }

    public ExecutionParam putKeyVal(String key, String value) {
        super.put(key, value);
        return this;
    }

    public String toJson() {
        return GsonTool.toJson(this);
    }
}
