package com.wellsoft.pt.security.audit.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月26日   chenq	 Create
 * </pre>
 */
@javax.persistence.Entity
@Table(name = "AUDIT_DATA_ITEM_LOG")
@DynamicUpdate
@DynamicInsert
public class AuditDataItemLogEntity extends SysEntity {
    private static final long serialVersionUID = -3609565765908774860L;

    protected Long auditUuid;
    protected String dataItemName;
    protected String dataItemCode;
    protected String dataType;
    protected String newValue;
    protected String oldValue;


    public Long getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(Long auditUuid) {
        this.auditUuid = auditUuid;
    }

    public String getDataItemName() {
        return dataItemName;
    }

    public void setDataItemName(String dataItemName) {
        this.dataItemName = dataItemName;
    }

    public String getDataItemCode() {
        return dataItemCode;
    }

    public void setDataItemCode(String dataItemCode) {
        this.dataItemCode = dataItemCode;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
}
