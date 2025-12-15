package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 组织同步日志表
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
@Table(name = "MULTI_ORG_SYNC_LOG")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class MultiOrgSyncLog extends IdEntity {
    /**
     *
     */
    private static final long serialVersionUID = -7154646995062875693L;

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
}
