/*
 * @(#)2019年3月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.dto;

import com.wellsoft.pt.dms.core.support.ActionData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.support.DyFormDataDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月22日.1	zhulh		2019年3月22日		Create
 * </pre>
 * @date 2019年3月22日
 */
public class UndertakeSituationActionData extends ActionData {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7688557055052610541L;

    // 数据来源
    private String dataSource;
    // 操作名称
    private String actionName;
    // 业务类别
    private String businessType;
    // 业务角色
    private String businessRole;
    // 归属环节实例UUID
    private String belongToTaskInstUuid;
    // 归属流程实例UUID
    private String belongToFlowInstUuid;
    // 转换规则UUID
    private String botRuleUuid;
    // 子表单数据
    @JsonDeserialize(using = DyFormDataDeserializer.class)
    private DyFormData childDyformData;

    /**
     * @return the dataSource
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource 要设置的dataSource
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param actionName 要设置的actionName
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
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
     * @return the botRuleUuid
     */
    public String getBotRuleUuid() {
        return botRuleUuid;
    }

    /**
     * @param botRuleUuid 要设置的botRuleUuid
     */
    public void setBotRuleUuid(String botRuleUuid) {
        this.botRuleUuid = botRuleUuid;
    }

    /**
     * @return the childDyformData
     */
    public DyFormData getChildDyformData() {
        return childDyformData;
    }

    /**
     * @param childDyformData 要设置的childDyformData
     */
    public void setChildDyformData(DyFormData childDyformData) {
        this.childDyformData = childDyformData;
    }

}
