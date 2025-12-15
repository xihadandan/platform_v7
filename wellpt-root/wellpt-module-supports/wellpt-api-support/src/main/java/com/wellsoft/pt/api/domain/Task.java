/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.domain;

import com.wellsoft.pt.api.WellptObject;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Task extends WellptObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6552236834293845001L;

    // 任务UUID
    private String uuid;

    // 任务所在流程实例UUID
    private String flowInstUuid;

    // 任务标题
    private String title;

    // 任务id
    private String id;

    // 任务名称
    private String name;

    // 前办理人名称
    private String preOperatorName;

    // 当前办理人
    private String todoUserName;

    // 任务开始时间
    private Date startTime;

    // 表单定义UUId
    private String formUuid;

    // 表单数据UUID
    private String dataUuid;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
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
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

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
     * @return the preOperatorName
     */
    public String getPreOperatorName() {
        return preOperatorName;
    }

    /**
     * @param preOperatorName 要设置的preOperatorName
     */
    public void setPreOperatorName(String preOperatorName) {
        this.preOperatorName = preOperatorName;
    }

    /**
     * @return the todoUserName
     */
    public String getTodoUserName() {
        return todoUserName;
    }

    /**
     * @param todoUserName 要设置的todoUserName
     */
    public void setTodoUserName(String todoUserName) {
        this.todoUserName = todoUserName;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

}
