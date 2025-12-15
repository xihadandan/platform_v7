/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-9-17.1	zhulh		2013-9-17		Create
 * </pre>
 * @date 2013-9-17
 */
@Entity
@Table(name = "task_fired_job_details_his")
@DynamicUpdate
@DynamicInsert
public class JobFiredDetailsHistory extends TenantEntity implements UUIDGeneratorIndicate {

    private static final long serialVersionUID = 2745137050271598717L;

    // 任务名称
    private String name;

    // 任务类型：timing定时、temporary临时
    private String type;

    // 触发类型
    private Integer firedType;

    // 任务类名
    private String jobClassName;

    // JobDetail序列化内容
    @UnCloneable
    private Blob content;

    // 调度结果，1成功，0失败
    private Integer result;

    // 调度结果信息
    private String resultMsg;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the firedType
     */
    public Integer getFiredType() {
        return firedType;
    }

    /**
     * @param firedType 要设置的firedType
     */
    public void setFiredType(Integer firedType) {
        this.firedType = firedType;
    }

    /**
     * @return the jobClassName
     */
    public String getJobClassName() {
        return jobClassName;
    }

    /**
     * @param jobClassName 要设置的jobClassName
     */
    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    /**
     * @return the content
     */
    public Blob getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(Blob content) {
        this.content = content;
    }

    /**
     * @return the result
     */
    public Integer getResult() {
        return result;
    }

    /**
     * @param result 要设置的result
     */
    public void setResult(Integer result) {
        this.result = result;
    }

    /**
     * @return the resultMsg
     */
    public String getResultMsg() {
        return resultMsg;
    }

    /**
     * @param resultMsg 要设置的resultMsg
     */
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

}
