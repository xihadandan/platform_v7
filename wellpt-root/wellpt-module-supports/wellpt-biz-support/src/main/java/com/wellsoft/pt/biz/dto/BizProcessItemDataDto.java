/*
 * @(#)10/13/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.support.DyFormDataDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: 业务事项实例数据传输对象
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/13/22.1	zhulh		10/13/22		Create
 * </pre>
 * @date 10/13/22
 */
@ApiModel("业务事项实例数据传输对象")
public class BizProcessItemDataDto extends BizProcessItemDataRequestParamDto {
    private static final long serialVersionUID = 3216096707419840781L;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("事项名称")
    private String itemName;

    @ApiModelProperty("事项编码")
    private String itemCode;

    @ApiModelProperty("事项类型")
    private String itemType;

    @ApiModelProperty("当前状态，00草稿，10运行中，20暂停，30已结束")
    private String state;

    @ApiModelProperty("计时器状态，0未启动、1计时中、2暂停、3结束")
    private Integer timerState;

    @JsonDeserialize(using = DyFormDataDeserializer.class)
    @ApiModelProperty("表单数据")
    private DyFormData dyFormData;

    @ApiModelProperty("二开JS模块")
    private String jsModule;

    @ApiModelProperty("业务事项配置信息")
    private ProcessItemConfig processItemConfig;

    @ApiModelProperty("是否启用办理情形")
    private boolean enabledSituation;

    @ApiModelProperty("流程交互性数据Map<itemId, InteractionTaskData>")
    private Map<String, InteractionTaskData> interactionTaskDataMap;

//    @ApiModelProperty("流程集成的流程业务定义ID")
//    private String flowBizDefId;

    @ApiModelProperty("流程集成的流程定义ID")
    private String flowDefId;

    @ApiModelProperty("附加参数")
    private Map<String, Object> extraParams = new LinkedHashMap<String, Object>(0);

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
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName 要设置的itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * @param itemCode 要设置的itemCode
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType 要设置的itemType
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state 要设置的state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the timerState
     */
    public Integer getTimerState() {
        return timerState;
    }

    /**
     * @param timerState 要设置的timerState
     */
    public void setTimerState(Integer timerState) {
        this.timerState = timerState;
    }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * @param dyFormData 要设置的dyFormData
     */
    public void setDyFormData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

    /**
     * @return the jsModule
     */
    public String getJsModule() {
        return jsModule;
    }

    /**
     * @param jsModule 要设置的jsModule
     */
    public void setJsModule(String jsModule) {
        this.jsModule = jsModule;
    }

    /**
     * @return the processItemConfig
     */
    public ProcessItemConfig getProcessItemConfig() {
        return processItemConfig;
    }

    /**
     * @param processItemConfig 要设置的processItemConfig
     */
    public void setProcessItemConfig(ProcessItemConfig processItemConfig) {
        this.processItemConfig = processItemConfig;
    }

    /**
     * @return
     */
    public boolean isEnabledSituation() {
        return enabledSituation;
    }

    /**
     * @param enabledSituation
     */
    public void setEnabledSituation(boolean enabledSituation) {
        this.enabledSituation = enabledSituation;
    }

    /**
     * @return the interactionTaskDataMap
     */
    public Map<String, InteractionTaskData> getInteractionTaskDataMap() {
        return interactionTaskDataMap;
    }

    /**
     * @param interactionTaskDataMap 要设置的interactionTaskDataMap
     */
    public void setInteractionTaskDataMap(Map<String, InteractionTaskData> interactionTaskDataMap) {
        this.interactionTaskDataMap = interactionTaskDataMap;
    }

//    /**
//     * @return the flowBizDefId
//     */
//    public String getFlowBizDefId() {
//        return flowBizDefId;
//    }
//
//    /**
//     * @param flowBizDefId 要设置的flowBizDefId
//     */
//    public void setFlowBizDefId(String flowBizDefId) {
//        this.flowBizDefId = flowBizDefId;
//    }

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the extraParams
     */
    public Map<String, Object> getExtraParams() {
        return extraParams;
    }

    /**
     * @param extraParams 要设置的extraParams
     */
    public void setExtraParams(Map<String, Object> extraParams) {
        this.extraParams = extraParams;
    }
}
