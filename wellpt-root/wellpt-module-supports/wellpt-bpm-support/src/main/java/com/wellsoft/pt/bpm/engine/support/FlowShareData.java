/*
 * @(#)2018年6月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicColumn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月4日.1	zhulh		2018年6月4日		Create
 * </pre>
 * @date 2018年6月4日
 */
@ApiModel("承办信息")
public class FlowShareData extends BaseObject implements Comparable<FlowShareData> {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3940743789578398626L;

    // 业务类别
    private String businessType;
    // 业务角色
    private String businessRole;

    // 归属环节ID
    @ApiModelProperty("归属环节ID")
    private String belongToTaskId;

    // 归属环节实例UUID
    @ApiModelProperty("归属环节实例UUID")
    private String belongToTaskInstUuid;

    // 归属流程实例UUID
    @ApiModelProperty("归属流程实例UUID")
    private String belongToFlowInstUuid;

    // 是否主办
    @ApiModelProperty("是否主办")
    private boolean isMajor;

    // 是否跟踪
    @ApiModelProperty("是否跟踪")
    private boolean isSupervise;

    // 是否办结
    @ApiModelProperty("是否办结")
    private boolean isOver;

    // 标题
    @ApiModelProperty("标题")
    private String title;

    // 分发时间
    @ApiModelProperty("分发时间")
    private Date distributeTime;

    // 操作按钮
    @ApiModelProperty("操作按钮列表")
    private List<CustomDynamicButton> buttons = Lists.newArrayListWithCapacity(0);

    // 列配置
    @ApiModelProperty("列配置列表")
    private List<CustomDynamicColumn> columns = Lists.newArrayListWithCapacity(0);

    // 办理信息
    @ApiModelProperty("办理信息列表")
    private List<FlowShareRowData> shareItems = Lists.newArrayListWithCapacity(0);

    private Integer pageNo;

    private Integer pageSize;

    private Long totalCount;
    // 加载子流程默认展开的配置
    @ApiModelProperty("加载子流程默认展开的配置 展开：1 不展开：0")
    private String expandListFlag;

    public String getExpandListFlag() {
        return this.expandListFlag;
    }

    public void setExpandListFlag(final String expandListFlag) {
        this.expandListFlag = expandListFlag;
    }

    /**
     * @return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * @param businessType 要设置的businessType
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * @return the businessRole
     */
    public String getBusinessRole() {
        return businessRole;
    }

    /**
     * @param businessRole 要设置的businessRole
     */
    public void setBusinessRole(String businessRole) {
        this.businessRole = businessRole;
    }

    /**
     * @return the belongToTaskId
     */
    public String getBelongToTaskId() {
        return belongToTaskId;
    }

    /**
     * @param belongToTaskId 要设置的belongToTaskId
     */
    public void setBelongToTaskId(String belongToTaskId) {
        this.belongToTaskId = belongToTaskId;
    }

    /**
     * @return the belongToTaskInstUuid
     */
    public String getBelongToTaskInstUuid() {
        return belongToTaskInstUuid;
    }

    /**
     * @param belongToTaskInstUuid 要设置的belongToTaskInstUuid
     */
    public void setBelongToTaskInstUuid(String belongToTaskInstUuid) {
        this.belongToTaskInstUuid = belongToTaskInstUuid;
    }

    /**
     * @return the belongToFlowInstUuid
     */
    public String getBelongToFlowInstUuid() {
        return belongToFlowInstUuid;
    }

    /**
     * @param belongToFlowInstUuid 要设置的belongToFlowInstUuid
     */
    public void setBelongToFlowInstUuid(String belongToFlowInstUuid) {
        this.belongToFlowInstUuid = belongToFlowInstUuid;
    }

    /**
     * @return the isMajor
     */
    public boolean isMajor() {
        return isMajor;
    }

    /**
     * @param isMajor 要设置的isMajor
     */
    public void setMajor(boolean isMajor) {
        this.isMajor = isMajor;
    }

    /**
     * @return the isSupervise
     */
    public boolean isSupervise() {
        return isSupervise;
    }

    /**
     * @param isSupervise 要设置的isSupervise
     */
    public void setSupervise(boolean isSupervise) {
        this.isSupervise = isSupervise;
    }

    /**
     * @return the isOver
     */
    public boolean isOver() {
        return isOver;
    }

    /**
     * @param isOver 要设置的isOver
     */
    public void setOver(boolean isOver) {
        this.isOver = isOver;
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
     * @return the distributeTime
     */
    public Date getDistributeTime() {
        return distributeTime;
    }

    /**
     * @param distributeTime 要设置的distributeTime
     */
    public void setDistributeTime(Date distributeTime) {
        this.distributeTime = distributeTime;
    }

    /**
     * @return the buttons
     */
    public List<CustomDynamicButton> getButtons() {
        return buttons;
    }

    /**
     * @param buttons 要设置的buttons
     */
    public void setButtons(List<CustomDynamicButton> buttons) {
        this.buttons = buttons;
    }

    /**
     * @return the columns
     */
    public List<CustomDynamicColumn> getColumns() {
        return columns;
    }

    /**
     * @param columns 要设置的columns
     */
    public void setColumns(List<CustomDynamicColumn> columns) {
        this.columns = columns;
    }

    /**
     * @return the shareItems
     */
    public List<FlowShareRowData> getShareItems() {
        return shareItems;
    }

    /**
     * @param shareItems 要设置的shareItems
     */
    public void setShareItems(List<FlowShareRowData> shareItems) {
        this.shareItems = shareItems;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(FlowShareData o) {
        Date date = o.getDistributeTime();
        if (this.distributeTime == null || date == null) {
            return 0;
        }
        return -this.distributeTime.compareTo(date);
    }

}
