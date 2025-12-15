package com.wellsoft.pt.app.dingtalk.vo;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/12/10.1	liuyz		2021/12/10		Create
 * </pre>
 * @date 2021/12/10
 */
@Deprecated
public class MultiOrgSyncLogVo {
    // 同步时间
    private Date syncTime;
    // 同步内容
    private String syncContent;
    // 同步状态
    private Integer syncStatus;
    // 操作人
    private String operator;
    // 操作人姓名
    private String operatorName;
    private int deptStatus;
    private int userStatus;
    private int userWorkStatus;

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public String getSyncContent() {
        return syncContent;
    }

    public void setSyncContent(String syncContent) {
        this.syncContent = syncContent;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public int getDeptStatus() {
        return deptStatus;
    }

    public void setDeptStatus(int deptStatus) {
        this.deptStatus = deptStatus;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public int getUserWorkStatus() {
        return userWorkStatus;
    }

    public void setUserWorkStatus(int userWorkStatus) {
        this.userWorkStatus = userWorkStatus;
    }
}
