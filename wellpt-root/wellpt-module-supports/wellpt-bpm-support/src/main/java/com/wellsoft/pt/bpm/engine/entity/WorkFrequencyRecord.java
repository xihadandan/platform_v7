package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 流程使用记录表
 *
 * @author xujm
 * @version 1.0
 * @date @2015-6-4
 */
@Entity
@Table(name = "Work_Frequency_Record")
@DynamicUpdate
@DynamicInsert
public class WorkFrequencyRecord extends IdEntity {
    private static final long serialVersionUID = 8919683732512703959L;
    /**
     * 流程定义id
     */
    private String flowDefUuid;

    /**
     * 流程实例id
     */
    private String flowInstUuid;

    /**
     * 流程环节id
     */
    private String taskInstUuid;

    /**
     * 提交时间
     */
    private Date submitTime = new Date();

    /**
     * 提交人员
     */
    private String clickPersonID;

    public WorkFrequencyRecord() {
    }

    public WorkFrequencyRecord(String flowDefUuid, String flowInstUuid,
                               String taskInstUuid, String clickPerson) {
        super();
        this.flowDefUuid = flowDefUuid;
        this.flowInstUuid = flowInstUuid;
        this.taskInstUuid = taskInstUuid;
        this.clickPersonID = clickPerson;
    }

    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getClickPersonID() {
        return clickPersonID;
    }

    public void setClickPersonID(String clickPersonID) {
        this.clickPersonID = clickPersonID;
    }
}
