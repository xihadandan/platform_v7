/*
 * @(#)8/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.log.model;

import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * 8/25/25.1	    zhulh		8/25/25		    Create
 * </pre>
 * @date 8/25/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("流转详细日志")
public class FlowOperationLogModel extends TaskOperation {
    private static final long serialVersionUID = -9166944728439113944L;

    @ApiModelProperty("日志ID")
    private String _id;

    @ApiModelProperty("流程标题")
    private String title;

    @ApiModelProperty("流程名称")
    private String flowName;

    @ApiModelProperty("流程ID")
    private String flowDefId;

    @ApiModelProperty("客户端IP地址")
    private String clientIp;

    @ApiModelProperty("服务器IP地址")
    private String serverIp;

    @ApiModelProperty("处理校验结果代码,0-成功,1-失败")
    private Integer resultCode;

    @ApiModelProperty("详情")
    private String details;

    @ApiModelProperty("归属系统")
    protected String system;
    @ApiModelProperty("归属租户")
    protected String tenant;

    /**
     * @return the _id
     */
    public String get_id() {
        return _id;
    }

    /**
     * @param _id 要设置的_id
     */
    public void set_id(String _id) {
        this._id = _id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the flowName
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * @param flowName 要设置的flowName
     */
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the clientIp
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * @param clientIp 要设置的clientIp
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * @return the serverIp
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * @param serverIp 要设置的serverIp
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * @return the resultCode
     */
    public Integer getResultCode() {
        return resultCode;
    }

    /**
     * @param resultCode 要设置的resultCode
     */
    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details 要设置的details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
