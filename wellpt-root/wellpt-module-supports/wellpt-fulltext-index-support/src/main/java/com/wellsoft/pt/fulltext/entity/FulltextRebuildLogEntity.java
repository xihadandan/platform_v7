/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/13/25.1	    zhulh		6/13/25		    Create
 * </pre>
 * @date 6/13/25
 */
@Entity
@Table(name = "FULLTEXT_REBUILD_LOG")
@DynamicUpdate
@DynamicInsert
@ApiModel("全文检索索引重建日志")
public class FulltextRebuildLogEntity extends SysEntity {

    private static final long serialVersionUID = 2966493311705493325L;
    public static final String EXECUTE_STATE_RUNNING = "1";
    public static final String EXECUTE_STATE_SUCCESS = "2";
    public static final String EXECUTE_STATE_FAILED = "3";

    @ApiModelProperty("索引设置UUID")
    private Long settingUuid;

    @ApiModelProperty("规则ID")
    private String ruleId;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("耗时，单位秒")
    private Long elapsedTimeInSecond;

    @ApiModelProperty("原索引数")
    private Long originalIndexCount;

    @ApiModelProperty("重建索引数")
    private Long rebuildIndexCount;

    @ApiModelProperty("执行状态，1执行中，2执行，3失败")
    private String executeState;

    /**
     * @return the settingUuid
     */
    public Long getSettingUuid() {
        return settingUuid;
    }

    /**
     * @param settingUuid 要设置的settingUuid
     */
    public void setSettingUuid(Long settingUuid) {
        this.settingUuid = settingUuid;
    }

    /**
     * @return the ruleId
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * @param ruleId 要设置的ruleId
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
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
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the elapsedTimeInSecond
     */
    public Long getElapsedTimeInSecond() {
        return elapsedTimeInSecond;
    }

    /**
     * @param elapsedTimeInSecond 要设置的elapsedTimeInSecond
     */
    public void setElapsedTimeInSecond(Long elapsedTimeInSecond) {
        this.elapsedTimeInSecond = elapsedTimeInSecond;
    }

    /**
     * @return the originalIndexCount
     */
    public Long getOriginalIndexCount() {
        return originalIndexCount;
    }

    /**
     * @param originalIndexCount 要设置的originalIndexCount
     */
    public void setOriginalIndexCount(Long originalIndexCount) {
        this.originalIndexCount = originalIndexCount;
    }

    /**
     * @return the rebuildIndexCount
     */
    public Long getRebuildIndexCount() {
        return rebuildIndexCount;
    }

    /**
     * @param rebuildIndexCount 要设置的rebuildIndexCount
     */
    public void setRebuildIndexCount(Long rebuildIndexCount) {
        this.rebuildIndexCount = rebuildIndexCount;
    }

    /**
     * @return the executeState
     */
    public String getExecuteState() {
        return executeState;
    }

    /**
     * @param executeState 要设置的executeState
     */
    public void setExecuteState(String executeState) {
        this.executeState = executeState;
    }

}
