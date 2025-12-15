/*
 * @(#)8/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.message.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/21/25.1	    zhulh		8/21/25		    Create
 * </pre>
 * @date 8/21/25
 */
public class WorkMessageSendEvent extends WellptEvent {

    private static final long serialVersionUID = -5532973352284447166L;

    private String dataUuid;
    private String sendWay;
    private TaskInstance taskInstance;
    private TaskData taskData;
    private FlowInstance flowInstance;
    private MessageTemplate template;
    private List<IdEntity> entities;
    private Map<Object, Object> dyformValues;
    private Collection<String> toSendOrgIds;
    private String system;

    /**
     * @param dataUuid
     * @param sendWay
     * @param taskInstance
     * @param taskData
     * @param flowInstance
     * @param template
     * @param entities
     * @param dyformValues
     * @param toSendOrgIds
     * @param system
     */
    public WorkMessageSendEvent(String dataUuid, String sendWay, TaskInstance taskInstance, TaskData taskData, FlowInstance flowInstance,
                                MessageTemplate template, List<IdEntity> entities, Map<Object, Object> dyformValues,
                                Collection<String> toSendOrgIds, String system) {
        this(template);
        this.dataUuid = dataUuid;
        this.sendWay = sendWay;
        this.taskInstance = taskInstance;
        this.taskData = taskData;
        this.flowInstance = flowInstance;
        this.template = template;
        this.entities = entities;
        this.dyformValues = dyformValues;
        this.toSendOrgIds = toSendOrgIds;
        this.system = system;
    }

    /**
     * @param source
     */
    public WorkMessageSendEvent(Object source) {
        super(source);
    }


    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @return the sendWay
     */
    public String getSendWay() {
        return sendWay;
    }

    /**
     * @return the taskInstance
     */
    public TaskInstance getTaskInstance() {
        return taskInstance;
    }

    /**
     * @return the taskData
     */
    public TaskData getTaskData() {
        return taskData;
    }

    /**
     * @return the flowInstance
     */
    public FlowInstance getFlowInstance() {
        return flowInstance;
    }

    /**
     * @return the template
     */
    public MessageTemplate getTemplate() {
        return template;
    }

    /**
     * @return the entities
     */
    public List<IdEntity> getEntities() {
        return entities;
    }

    /**
     * @return the dyformValues
     */
    public Map<Object, Object> getDyformValues() {
        return dyformValues;
    }

    /**
     * @return the toSendOrgIds
     */
    public Collection<String> getToSendOrgIds() {
        return toSendOrgIds;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return StringUtils.isBlank(system) ? flowInstance.getSystem() : system;
    }
    
}
