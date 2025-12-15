/*
 * @(#)10/14/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
 * 10/14/24.1	    zhulh		10/14/24		    Create
 * </pre>
 * @date 10/14/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowSimulationRuntimeParams extends BaseObject {
    private static final long serialVersionUID = 5059944119759677090L;

    private Map<String, Boolean> setSimulationTaskUserFlags = Maps.newHashMap();
    private Map<String, Boolean> setSimulationDefaultTaskUserFlags = Maps.newHashMap();
    private Map<String, Boolean> specifyTaskUserMap = Maps.newHashMap();
    private Map<String, Boolean> simulationDecisionMakerFlags = Maps.newHashMap();
    private Map<String, String> taskUserNoFoundMap = Maps.newHashMap();
    private Map<String, String> taskDecisionMakerNoFoundMap = Maps.newHashMap();
    private Map<String, String> taskCopyUserNoFoundMap = Maps.newHashMap();
    private Map<String, String> taskSuperviseUserNoFoundMap = Maps.newHashMap();
    private Map<String, String> chooseOneUserMap = Maps.newHashMap();
    private Map<String, String> chooseMultiUserMap = Maps.newHashMap();
    private Map<String, Integer> randomUserCountMap = Maps.newHashMap();
    private Map<String, List<String>> candidateUserIdMap = Maps.newHashMap();

    /**
     * @param taskId
     * @param custom
     */
    public void setSimulationTaskUserFlags(String taskId, boolean custom) {
        setSimulationTaskUserFlags.put(taskId, custom);
    }

    /**
     * @param taskId
     * @return
     */
    public Boolean getSimulationTaskUserFlag(String taskId) {
        return setSimulationTaskUserFlags.get(taskId);
    }

    /**
     * @param taskId
     * @param custom
     */
    public void setSimulationDefaultTaskUserFlags(String taskId, boolean custom) {
        setSimulationDefaultTaskUserFlags.put(taskId, custom);
    }

    /**
     * @param taskId
     */
    public Boolean getSimulationDefaultTaskUserFlag(String taskId) {
        return setSimulationDefaultTaskUserFlags.get(taskId);
    }

    /**
     * @param taskId
     * @param specifyTaskUser
     */
    public void setSpecifyTaskUser(String taskId, boolean specifyTaskUser) {
        specifyTaskUserMap.put(taskId, specifyTaskUser);
    }

    /**
     * @param taskId
     * @return
     */
    public Boolean getSpecifyTaskUser(String taskId) {
        return specifyTaskUserMap.get(taskId);
    }

    /**
     * @param taskId
     * @param custom
     */
    public void setSimulationDecisionMakerFlags(String taskId, boolean custom) {
        simulationDecisionMakerFlags.put(taskId, custom);
    }

    /**
     * @param taskId
     */
    public Boolean getSimulationDecisionMakerFlag(String taskId) {
        return simulationDecisionMakerFlags.get(taskId);
    }

    /**
     * @param taskId
     * @param taskUserNoFound
     */
    public void setTaskUserNoFound(String taskId, String taskUserNoFound) {
        taskUserNoFoundMap.put(taskId, taskUserNoFound);
    }

    /**
     * @param taskId
     */
    public String getTaskUserNoFound(String taskId) {
        return taskUserNoFoundMap.get(taskId);
    }

    /**
     * @param taskId
     * @param taskDecisionMakerNoFound
     */
    public void setTaskDecisionMakerNoFound(String taskId, String taskDecisionMakerNoFound) {
        taskDecisionMakerNoFoundMap.put(taskId, taskDecisionMakerNoFound);
    }

    /**
     * @param taskId
     * @return
     */
    public String getTaskDecisionMakerNoFound(String taskId) {
        return taskDecisionMakerNoFoundMap.get(taskId);
    }

    /**
     * @param taskId
     * @param taskCopyUserNoFound
     */
    public void setTaskCopyUserNoFound(String taskId, String taskCopyUserNoFound) {
        taskCopyUserNoFoundMap.put(taskId, taskCopyUserNoFound);
    }

    /**
     * @param taskId
     * @return
     */
    public String getTaskCopyUserNoFound(String taskId) {
        return taskCopyUserNoFoundMap.get(taskId);
    }

    /**
     * @param taskId
     * @param taskSuperviseUserNoFound
     */
    public void setTaskSuperviseUserNoFound(String taskId, String taskSuperviseUserNoFound) {
        taskSuperviseUserNoFoundMap.put(taskId, taskSuperviseUserNoFound);
    }

    /**
     * @param taskId
     */
    public String getTaskSuperviseUserNoFound(String taskId) {
        return taskSuperviseUserNoFoundMap.get(taskId);
    }

    /**
     * @param taskId
     * @param chooseOneUser
     */
    public void setChooseOneUser(String taskId, String chooseOneUser) {
        chooseOneUserMap.put(taskId, chooseOneUser);
    }

    /**
     * @param taskId
     * @return
     */
    public String getChooseOneUser(String taskId) {
        return chooseOneUserMap.get(taskId);
    }

    /**
     * @param taskId
     * @param chooseMultiUser
     */
    public void setChooseMultiUser(String taskId, String chooseMultiUser) {
        chooseMultiUserMap.put(taskId, chooseMultiUser);
    }

    /**
     * @param taskId
     * @return
     */
    public String getChooseMultiUser(String taskId) {
        return chooseMultiUserMap.get(taskId);
    }

    /**
     * @param taskId
     * @param randomUserCount
     */
    public void setRandomUserCount(String taskId, int randomUserCount) {
        randomUserCountMap.put(taskId, randomUserCount);
    }

    /**
     * @param taskId
     * @return
     */
    public Integer getRandomUserCount(String taskId) {
        return randomUserCountMap.get(taskId);
    }

    /**
     * @param taskId
     * @param userIds
     */
    public void setCandidateUserIds(String taskId, List<String> userIds) {
        candidateUserIdMap.put(taskId, userIds);
    }

    /**
     * @param taskId
     * @return
     */
    public List<String> getCandidateUserIds(String taskId) {
        return candidateUserIdMap.get(taskId);
    }
}
