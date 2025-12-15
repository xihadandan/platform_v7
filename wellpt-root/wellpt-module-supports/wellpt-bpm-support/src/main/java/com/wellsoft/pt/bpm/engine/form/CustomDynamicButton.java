/*
 * @(#)2013-3-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-28.1	zhulh		2013-3-28		Create
 * </pre>
 * @date 2013-3-28
 */
@ApiModel("操作按钮")
public class CustomDynamicButton extends BaseObject {
    // 替换
    public static final String REPLACE = "1";
    // 新增操作
    public static final String NEW_OPERATE = "2";
    // 按钮来源
    public static final String SOURCE_EVENT_HANDLER = "2";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5347592786866201766L;
    // 按钮来源
    @ApiModelProperty("按钮类型1内置按钮、2事件处理")
    private String btnSource;
    // 按钮角色
    @ApiModelProperty("应用场景")
    private String btnRole;
    // 产品集成UUID
    @ApiModelProperty("产品集成UUID")
    private String piUuid;
    // 产品集成名称
    @ApiModelProperty("产品集成名称")
    private String piName;
    // 锚点类型
    @ApiModelProperty("锚点类型")
    private String hashType;
    // 锚点
    @ApiModelProperty("锚点")
    private String hash;
    // 事件处理
    @ApiModelProperty("事件处理")
    private String eventHandler;
    // 事件参数
    @ApiModelProperty("事件参数")
    private String eventParams;
    // 目标位置
    @ApiModelProperty("目标位置")
    private String targetPosition;
    // 按钮ID
    @ApiModelProperty("按钮ID")
    private String id;
    // 原权限编号
    @ApiModelProperty("原权限编号")
    private String code;
    // 新的权限编号
    @ApiModelProperty("新的权限编号")
    private String newCode;
    // 新的权限编号是否需要权限
    @ApiModelProperty("新的权限编号是否需要权限")
    private boolean requiredNewCodePrivilege = true;
    // 新的名称
    @ApiModelProperty("按钮名称")
    private String name;
    // 使用方式，0(替换名称)、1(全部替换)、2(新增操作)
    @ApiModelProperty("使用方式，0(替换名称)、1(全部替换)、2(新增操作)")
    private String useWay;

    @ApiModelProperty(hidden = true)
    private String script;
    // 样式名
    @ApiModelProperty("样式名")
    private String className;
    // 图标
    @ApiModelProperty("图标")
    private String btnIcon;
    // 排序号
    @ApiModelProperty("排序号")
    private int sortOrder;

    // 用户自定义动态按钮的提交环节
    @ApiModelProperty("用户自定义动态按钮的提交环节")
    private String taskId;

    @ApiModelProperty("按钮使用人")
    private List<String> owners = new ArrayList<String>(0);

    @ApiModelProperty("按钮提交对象")
    private List<String> users = new ArrayList<String>(0);

    @ApiModelProperty("按钮抄送对象")
    private List<String> copyUsers = new ArrayList<String>(0);

    @ApiModelProperty("国际化信息")
    private Map<String, Map<String, String>> i18n = Maps.newHashMap();// 国际化配置

    private String uuid;

    private String configuration;

    /**
     * @return the btnSource
     */
    public String getBtnSource() {
        return btnSource;
    }

    /**
     * @param btnSource 要设置的btnSource
     */
    public void setBtnSource(String btnSource) {
        this.btnSource = btnSource;
    }

    /**
     * @return the btnRole
     */
    public String getBtnRole() {
        return btnRole;
    }

    /**
     * @param btnRole 要设置的btnRole
     */
    public void setBtnRole(String btnRole) {
        this.btnRole = btnRole;
    }

    /**
     * @return the piUuid
     */
    public String getPiUuid() {
        return piUuid;
    }

    /**
     * @param piUuid 要设置的piUuid
     */
    public void setPiUuid(String piUuid) {
        this.piUuid = piUuid;
    }

    /**
     * @return the piName
     */
    public String getPiName() {
        return piName;
    }

    /**
     * @param piName 要设置的piName
     */
    public void setPiName(String piName) {
        this.piName = piName;
    }

    /**
     * @return the hashType
     */
    public String getHashType() {
        return hashType;
    }

    /**
     * @param hashType 要设置的hashType
     */
    public void setHashType(String hashType) {
        this.hashType = hashType;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash 要设置的hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the eventHandler
     */
    public String getEventHandler() {
        return eventHandler;
    }

    /**
     * @param eventHandler 要设置的eventHandler
     */
    public void setEventHandler(String eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * @return the eventParams
     */
    public String getEventParams() {
        return eventParams;
    }

    /**
     * @param eventParams 要设置的eventParams
     */
    public void setEventParams(String eventParams) {
        this.eventParams = eventParams;
    }

    /**
     * @return the targetPosition
     */
    public String getTargetPosition() {
        return targetPosition;
    }

    /**
     * @param targetPosition 要设置的targetPosition
     */
    public void setTargetPosition(String targetPosition) {
        this.targetPosition = targetPosition;
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
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the newCode
     */
    public String getNewCode() {
        return newCode;
    }

    /**
     * @param newCode 要设置的newCode
     */
    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    /**
     * @return the requiredNewCodePrivilege
     */
    public boolean isRequiredNewCodePrivilege() {
        return requiredNewCodePrivilege;
    }

    /**
     * @param requiredNewCodePrivilege 要设置的requiredNewCodePrivilege
     */
    public void setRequiredNewCodePrivilege(boolean requiredNewCodePrivilege) {
        this.requiredNewCodePrivilege = requiredNewCodePrivilege;
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
     * @return the useWay
     */
    public String getUseWay() {
        return useWay;
    }

    /**
     * @param useWay 要设置的useWay
     */
    public void setUseWay(String useWay) {
        this.useWay = useWay;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script 要设置的script
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className 要设置的className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the btnIcon
     */
    public String getBtnIcon() {
        return btnIcon;
    }

    /**
     * @param btnIcon 要设置的btnIcon
     */
    public void setBtnIcon(String btnIcon) {
        this.btnIcon = btnIcon;
    }

    /**
     * @return the sortOrder
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the owners
     */
    public List<String> getOwners() {
        return owners;
    }

    /**
     * @param owners 要设置的owners
     */
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    /**
     * @return the users
     */
    public List<String> getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(List<String> users) {
        this.users = users;
    }

    /**
     * @return the copyUsers
     */
    public List<String> getCopyUsers() {
        return copyUsers;
    }

    /**
     * @param copyUsers 要设置的copyUsers
     */
    public void setCopyUsers(List<String> copyUsers) {
        this.copyUsers = copyUsers;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomDynamicButton other = (CustomDynamicButton) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public static class CustomDynamicButtonComparator implements Comparator<CustomDynamicButton> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(CustomDynamicButton o1, CustomDynamicButton o2) {
            Integer order1 = o1.getSortOrder();
            Integer order2 = o2.getSortOrder();
            return order1.compareTo(order2);
        }

    }

    public Map<String, Map<String, String>> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the configuration
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration 要设置的configuration
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
