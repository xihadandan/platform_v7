/*
 * @(#)2012-11-16 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description: 如何描述该类
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
public class UserUnitElement extends UnitElement {

    private static final long serialVersionUID = -1877783614800883306L;

    // 办理人分组ID，可用于区分不同批次添加的办理人配置
    private String groupId;

    // 组织ID，一个分组下只有一个组织
    private String orgId;
    // 业务组织ID，一个分组下只有一个业务组织
    private String bizOrgId;
    // 值路径，多个值以分号隔开
    private String valuePath;
    // 用户选项列表
    private List<UserOptionElement> userOptions;
    // 是否启用职等，1启用，0不启用
    private String enabledJobGrade;
    // 职等序号，多个以分号隔开
    private String jobGrade;
    // 是否启用职级，1启用，0不启用
    private String enabledJobRank;
    // 职级ID，多个以分号隔开
    private String jobRankId;

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId 要设置的groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * @param orgId 要设置的orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * @return the bizOrgId
     */
    public String getBizOrgId() {
        return bizOrgId;
    }

    /**
     * @param bizOrgId 要设置的bizOrgId
     */
    public void setBizOrgId(String bizOrgId) {
        this.bizOrgId = bizOrgId;
    }

    /**
     * @return the valuePath
     */
    public String getValuePath() {
        return valuePath;
    }

    /**
     * @param valuePath 要设置的valuePath
     */
    public void setValuePath(String valuePath) {
        this.valuePath = valuePath;
    }

    /**
     * @return the userOptions
     */
    public List<UserOptionElement> getUserOptions() {
        return userOptions;
    }

    /**
     * @param userOptions 要设置的userOptions
     */
    public void setUserOptions(List<UserOptionElement> userOptions) {
        this.userOptions = userOptions;
    }

    /**
     * @return the enabledJobGrade
     */
    public String getEnabledJobGrade() {
        return enabledJobGrade;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEnabledJobGrade() {
        return StringUtils.equals("1", enabledJobGrade);
    }

    /**
     * @param enabledJobGrade 要设置的enabledJobGrade
     */
    public void setEnabledJobGrade(String enabledJobGrade) {
        this.enabledJobGrade = enabledJobGrade;
    }

    /**
     * @return the jobGrade
     */
    public String getJobGrade() {
        return jobGrade;
    }

    /**
     * @param jobGrade 要设置的jobGrade
     */
    public void setJobGrade(String jobGrade) {
        this.jobGrade = jobGrade;
    }

    /**
     * @return the enabledJobRank
     */
    public String getEnabledJobRank() {
        return enabledJobRank;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEnabledJobRank() {
        return StringUtils.equals("1", enabledJobRank);
    }

    /**
     * @param enabledJobRank 要设置的enabledJobRank
     */
    public void setEnabledJobRank(String enabledJobRank) {
        this.enabledJobRank = enabledJobRank;
    }

    /**
     * @return the jobRankId
     */
    public String getJobRankId() {
        return jobRankId;
    }

    /**
     * @param jobRankId 要设置的jobRankId
     */
    public void setJobRankId(String jobRankId) {
        this.jobRankId = jobRankId;
    }
}
