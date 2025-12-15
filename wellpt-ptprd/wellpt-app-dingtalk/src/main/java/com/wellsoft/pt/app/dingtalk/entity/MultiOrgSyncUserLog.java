package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织同步人员同步日志表
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年8月16日.1	liuyz		2021年8月16日		Create
 * </pre>
 * @date 2021年8月16日
 */
@Entity
@Table(name = "MULTI_ORG_SYNC_USER_LOG")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class MultiOrgSyncUserLog extends IdEntity {

    /**
     *
     */
    private static final long serialVersionUID = -7154646995062875695L;

    // MultiOrgSyncLog的uuid
    private String logId;
    // 钉钉用户的id
    private String userId;
    // 钉钉用户的姓名
    private String name;
    // 用户操作
    private String operationName;
    // 手机
    private String mobile;
    // 同步状态
    private Integer syncStatus;
    // 异常原因
    private String remark;
    // 是否多部门人员
    private int isMultiDepts;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIsMultiDepts() {
        return isMultiDepts;
    }

    public void setIsMultiDepts(int isMultiDepts) {
        this.isMultiDepts = isMultiDepts;
    }
}
