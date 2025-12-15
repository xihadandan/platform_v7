/*
 * @(#)2012-11-16 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 网关节点
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-16.1	zhulh		2012-11-16		Create
 * </pre>
 * @date 2012-11-16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowElement implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4427868682769139157L;

    // 是否XML流程定义
    private boolean xmlDefinition;

    // 名称
    private String name;
    // 别名
    private String id;
    // 编号
    private String code;
    // UUID
    private String uuid;
    // 版本号
    private String version;
    // 归属系统单位ID
    private String systemUnitId;
    //归属模块ID
    private String moduleId;

    private PropertyElement property;

    private List<TimerElement> timers;

    private List<TaskElement> tasks;

    private Map<String, TaskElement> taskMap;

    private List<GatewayElement> gateways;

    @JsonIgnore
    private Map<String, GatewayElement> gatewayMap;

    private List<DirectionElement> directions;

    @JsonIgnore
    private Map<String, List<DirectionElement>> directionMap;
    @JsonIgnore
    private Map<String, DirectionElement> directionIdMap;

    private Map<String, Map<String, String>> i18n = Maps.newHashMap();


    /* add by huanglinchuan2014.10.20 begin */
    // 流程标题表达式
    private String titleExpression;

    private String applyId;

    // 图形数据
    private String graphData;

    private String definitionJson;

    private String definitionXml;

    public String getTitleExpression() {
        return titleExpression;
    }

    public void setTitleExpression(String titleExpression) {
        this.titleExpression = titleExpression;
    }

    /* add by huanglinchuan2014.10.20 end */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the xmlDefinition
     */
    public boolean isXmlDefinition() {
        return xmlDefinition;
    }

    /**
     * @param xmlDefinition 要设置的xmlDefinition
     */
    public void setXmlDefinition(boolean xmlDefinition) {
        this.xmlDefinition = xmlDefinition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public PropertyElement getProperty() {
        return property;
    }

    public void setProperty(PropertyElement property) {
        this.property = property;
    }

    public List<TimerElement> getTimers() {
        return timers;
    }

    public void setTimers(List<TimerElement> timers) {
        // 设置计时器事件监听
        for (TimerElement timerElement : timers) {
            String timerListener = this.property.getTimerListener();
            timerElement.setTimerListener(timerListener);
        }
        this.timers = timers;
    }

    @JsonIgnore
    public TaskElement getTask(String taskId) {
        return taskMap.get(taskId);
    }

    public List<TaskElement> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskElement> tasks) {
        this.tasks = tasks;
        initTaskMap();
    }

    /**
     *
     */
    @JsonIgnore
    private void initTaskMap() {
        taskMap = new HashMap<String, TaskElement>();
        for (TaskElement task : tasks) {
            taskMap.put(task.getId(), task);
        }
    }

    /**
     * @return the gateways
     */
    public List<GatewayElement> getGateways() {
        return gateways;
    }

    /**
     * @param gateways 要设置的gateways
     */
    public void setGateways(List<GatewayElement> gateways) {
        this.gateways = gateways;
        initGatewayMap();
    }

    @JsonIgnore
    private void initGatewayMap() {
        gatewayMap = new HashMap<String, GatewayElement>();
        if (CollectionUtils.isNotEmpty(gateways)) {
            for (GatewayElement gatewayElement : gateways) {
                gatewayMap.put(gatewayElement.getId(), gatewayElement);
            }
        }
    }

    /**
     * @param id
     * @return
     */
    @JsonIgnore
    public GatewayElement getGateway(String id) {
        return gatewayMap.get(id);
    }

    public List<DirectionElement> getDirections() {
        return directions;
    }

    public void setDirections(List<DirectionElement> directions) {
        this.directions = directions;
        Collections.sort(this.directions);
        initDirectionMap();
    }

    @JsonIgnore
    private void initDirectionMap() {
        directionMap = new HashMap<>();
        directionIdMap = new HashMap<>();
        for (DirectionElement directionElement : directions) {
            List<DirectionElement> directionElements = directionMap.get(directionElement.getFromID() + "_" + directionElement.getToID());
            if (directionElements == null) {
                directionElements = Lists.newArrayListWithCapacity(1);
                directionMap.put(directionElement.getFromID() + "_" + directionElement.getToID(), directionElements);
            }
            directionElements.add(directionElement);
            directionIdMap.put(directionElement.getId(), directionElement);
        }
    }

    /**
     * @param directionId
     * @return
     */
    @JsonIgnore
    public DirectionElement getDirection(String directionId) {
        return directionIdMap.get(directionId);
    }

    /**
     * @param fromId
     * @param toId
     * @return
     */
    @JsonIgnore
    public List<DirectionElement> getDirection(String fromId, String toId) {
        return directionMap.get(fromId + "_" + toId);
    }

    /**
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return
     */
    @JsonIgnore
    public Map<String, TaskElement> getTaskAsMap() {
        Map<String, TaskElement> taskMap = Maps.newHashMap();
        List<TaskElement> taskElements = getTasks();
        taskElements.forEach(t -> taskMap.put(t.getId(), t));
        return taskMap;
    }

    /**
     * @return the graphData
     */
    public String getGraphData() {
        return graphData;
    }

    /**
     * @param graphData 要设置的graphData
     */
    public void setGraphData(String graphData) {
        this.graphData = graphData;
    }

    public Map<String, Map<String, String>> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }

    /**
     * @return the definitionJson
     */
    @JsonIgnore
    public String getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    /**
     * @return the definitionXml
     */
    @JsonIgnore
    public String getDefinitionXml() {
        return definitionXml;
    }

    /**
     * @param definitionXml 要设置的definitionXml
     */
    public void setDefinitionXml(String definitionXml) {
        this.definitionXml = definitionXml;
    }
}
