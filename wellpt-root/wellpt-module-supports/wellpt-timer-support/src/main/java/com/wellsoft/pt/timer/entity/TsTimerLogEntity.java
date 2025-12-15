/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 计时实例日志实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
@Entity
@Table(name = "TS_TIMER_LOG")
@DynamicUpdate
@DynamicInsert
public class TsTimerLogEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1978634676735634709L;

    // 计时器UUID
    private String timerUuid;
    // 记录时间
    private Date logTime;
    // 记录类型启动START、暂停PAUSE、重启RESUME、结束END、预警ALARM、到期DUE_DOING、逾期OVER_DUE、强制终止预警FORCE_STOP_ALARM、强制终止到期处理FORCE_STOP_DUE_DOING、信息INFO、错误ERROR
    private String type;
    // 办理时限数字
    private Double timeLimit;
    // 到期时间
    private Date dueTime;
    // 备注
    private String remark;

    /**
     * @return the timerUuid
     */
    public String getTimerUuid() {
        return timerUuid;
    }

    /**
     * @param timerUuid 要设置的timerUuid
     */
    public void setTimerUuid(String timerUuid) {
        this.timerUuid = timerUuid;
    }

    /**
     * @return the logTime
     */
    public Date getLogTime() {
        return logTime;
    }

    /**
     * @param logTime 要设置的logTime
     */
    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the timeLimit
     */
    public Double getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param timeLimit 要设置的timeLimit
     */
    public void setTimeLimit(Double timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * @return the dueTime
     */
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
