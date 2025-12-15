/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Description: 视图定义类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2013-3-13.1	Administrator		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */
@Entity
@Table(name = "dyview_view_definition")
@DynamicUpdate
@DynamicInsert
public class ViewDefinition extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1204247570639768479L;

    private String id;//视图的jqgrid行标识

    private String viewName; // 视图名称

    private String code; // 视图编号

    private String viewId; // 视图id

    private Boolean showTitle; //是否显示视图的标题

    private Boolean showCheckBox;//是否显示复选框

    private String checkKey;//复选框key

    private Boolean isRead;//启用已读未读

    private String readKey;//启用已读未读

    private Integer dataScope; // 创建视图的数据源范围 1表示来自动态表单,2表示来自实体类,3表示来自模块数据

    private String tableDefinitionText; //数据源范围下的显示表名称

    private String tableDefinitionName; //数据源范围下的表名

    private String cateUuid; //所属模块id

    private String cateName; //所属模块名

    private String roleValue; // 角色类型（个人，群组，群组人员，所有）

    private String roleType; // 角色值（如角色为待办，则对应数据库字典中的一个值）

    private String roleName; //角色显示名

    private String moduleId; //所属模块ID

    private String formuuid; //数据源范围下的表对应的表uuid

    private String dataUuid; // 创建视图的数据源uuid

    private String defaultCondition; //默认的搜索条件

    private boolean pageAble; // 是否分页

    private Integer pageRow; // 每页的行数

    private String url; // 点击事件跳转的url

    private String lineType; // 点击事件跳转的url

    private String jsSrc;

    private Boolean buttonPlace;//是否头尾部显示

    private Boolean specialField;//是否存在特殊列值计算

    private String specialFieldMethod;//特殊列值计算调用的js方法

    private String requestParamName;//特殊列值计算调用的js方法的请求参数name

    private String requestParamId;//特殊列值计算调用的js方法的请求参数id

    private String responseParamName;//特殊列值计算调用的js方法的输出参数name

    private String responseParamId;//特殊列值计算调用的js方法的输出参数id

    private String dataModuleName;//数据导出模版的名字

    private String dataModuleId;//数据导出模版的id

    @UnCloneable
    private Set<ColumnDefinition> columnDefinitions = new LinkedHashSet<ColumnDefinition>();

    @UnCloneable
    private Set<CustomButton> customButtons = new LinkedHashSet<CustomButton>();

    @UnCloneable
    private SelectDefinition selectDefinitions;

    @UnCloneable
    private PageDefinition pageDefinitions;

    @UnCloneable
    private Set<ColumnCssDefinition> columnCssDefinition = new LinkedHashSet<ColumnCssDefinition>();

    /**
     * @return the dataModuleName
     */
    public String getDataModuleName() {
        return dataModuleName;
    }

    /**
     * @param dataModuleName 要设置的dataModuleName
     */
    public void setDataModuleName(String dataModuleName) {
        this.dataModuleName = dataModuleName;
    }

    /**
     * @return the dataModuleId
     */
    public String getDataModuleId() {
        return dataModuleId;
    }

    /**
     * @param dataModuleId 要设置的dataModuleId
     */
    public void setDataModuleId(String dataModuleId) {
        this.dataModuleId = dataModuleId;
    }

    /**
     * @return the specialField
     */
    public Boolean getSpecialField() {
        return specialField;
    }

    /**
     * @param specialField 要设置的specialField
     */
    public void setSpecialField(Boolean specialField) {
        this.specialField = specialField;
    }

    /**
     * @return the specialFieldMethod
     */
    public String getSpecialFieldMethod() {
        return specialFieldMethod;
    }

    /**
     * @param specialFieldMethod 要设置的specialFieldMethod
     */
    public void setSpecialFieldMethod(String specialFieldMethod) {
        this.specialFieldMethod = specialFieldMethod;
    }

    /**
     * @return the requestParamName
     */
    public String getRequestParamName() {
        return requestParamName;
    }

    /**
     * @param requestParamName 要设置的requestParamName
     */
    public void setRequestParamName(String requestParamName) {
        this.requestParamName = requestParamName;
    }

    /**
     * @return the requestParamId
     */
    public String getRequestParamId() {
        return requestParamId;
    }

    /**
     * @param requestParamId 要设置的requestParamId
     */
    public void setRequestParamId(String requestParamId) {
        this.requestParamId = requestParamId;
    }

    /**
     * @return the responseParamName
     */
    public String getResponseParamName() {
        return responseParamName;
    }

    /**
     * @param responseParamName 要设置的responseParamName
     */
    public void setResponseParamName(String responseParamName) {
        this.responseParamName = responseParamName;
    }

    /**
     * @return the responseParamId
     */
    public String getResponseParamId() {
        return responseParamId;
    }

    /**
     * @param responseParamId 要设置的responseParamId
     */
    public void setResponseParamId(String responseParamId) {
        this.responseParamId = responseParamId;
    }

    /**
     * @return the buttonPlace
     */
    public Boolean getButtonPlace() {
        return buttonPlace;
    }

    /**
     * @param buttonPlace 要设置的buttonPlace
     */
    public void setButtonPlace(Boolean buttonPlace) {
        this.buttonPlace = buttonPlace;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 要设置的url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName 要设置的roleName
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return the roleValue
     */
    public String getRoleValue() {
        return roleValue;
    }

    /**
     * @param roleValue 要设置的roleValue
     */
    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue;
    }

    /**
     * @return the roleType
     */
    public String getRoleType() {
        return roleType;
    }

    /**
     * @param roleType 要设置的roleType
     */
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the columnDefinitions
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "viewDefinition")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @OrderBy("sortOrder asc")
    public Set<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    /**
     * @param columnDefinitions 要设置的columnDefinitions
     */
    public void setColumnDefinitions(Set<ColumnDefinition> columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
    }

    /**
     * @return the selectDefinitions
     */
    @OneToOne(mappedBy = "viewDefinition")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    public SelectDefinition getSelectDefinitions() {
        return selectDefinitions;
    }

    /**
     * @param selectDefinitions 要设置的selectDefinitions
     */
    public void setSelectDefinitions(SelectDefinition selectDefinitions) {
        this.selectDefinitions = selectDefinitions;
    }

    /**
     * @return the pageDefinitions
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "viewDefinition")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    public PageDefinition getPageDefinitions() {
        return pageDefinitions;
    }

    /**
     * @param pageDefinitions 要设置的pageDefinitions
     */
    public void setPageDefinitions(PageDefinition pageDefinitions) {
        this.pageDefinitions = pageDefinitions;
    }

    /**
     * @return the customButtons
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "viewDefinition")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @OrderBy("code asc")
    public Set<CustomButton> getCustomButtons() {
        return customButtons;
    }

    /**
     * @param customButtons 要设置的customButtons
     */
    public void setCustomButtons(Set<CustomButton> customButtons) {
        this.customButtons = customButtons;
    }

    /**
     * @return the columnCssDefinition
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "viewDefinition")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    public Set<ColumnCssDefinition> getColumnCssDefinition() {
        return columnCssDefinition;
    }

    /**
     * @param columnCssDefinition 要设置的columnCssDefinition
     */
    public void setColumnCssDefinition(Set<ColumnCssDefinition> columnCssDefinition) {
        this.columnCssDefinition = columnCssDefinition;
    }

    /**
     * @return the lineType
     */
    public String getLineType() {
        return lineType;
    }

    /**
     * @param lineType 要设置的lineType
     */
    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    /**
     * @return the jsSrc
     */
    public String getJsSrc() {
        return jsSrc;
    }

    /**
     * @param jsSrc 要设置的jsSrc
     */
    public void setJsSrc(String jsSrc) {
        this.jsSrc = jsSrc;
    }

    /**
     * @return the showCheckBox
     */
    public Boolean getShowCheckBox() {
        return showCheckBox;
    }

    /**
     * @param showCheckBox 要设置的showCheckBox
     */
    public void setShowCheckBox(Boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
    }

    /**
     * @return the isRead
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * @param isRead 要设置的isRead
     */
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * @return the readKey
     */
    public String getReadKey() {
        return readKey;
    }

    /**
     * @param readKey 要设置的readKey
     */
    public void setReadKey(String readKey) {
        this.readKey = readKey;
    }

    /**
     * @return the checkKey
     */
    public String getCheckKey() {
        return checkKey;
    }

    /**
     * @param checkKey 要设置的checkKey
     */
    public void setCheckKey(String checkKey) {
        this.checkKey = checkKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the viewId
     */
    public String getViewId() {
        return viewId;
    }

    /**
     * @param viewId 要设置的viewId
     */
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    /**
     * @return the cateUuid
     */
    public String getCateUuid() {
        return cateUuid;
    }

    /**
     * @param cateUuid 要设置的cateUuid
     */
    public void setCateUuid(String cateUuid) {
        this.cateUuid = cateUuid;
    }

    /**
     * @return the cateName
     */
    public String getCateName() {
        return cateName;
    }

    /**
     * @param cateName 要设置的cateName
     */
    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    /**
     * @return the showTitle
     */
    public Boolean getShowTitle() {
        return showTitle;
    }

    /**
     * @param showTitle 要设置的showTitle
     */
    public void setShowTitle(Boolean showTitle) {
        this.showTitle = showTitle;
    }

    /**
     * @return the defaultCondition
     */
    public String getDefaultCondition() {
        return defaultCondition;
    }

    /**
     * @param defaultCondition 要设置的defaultCondition
     */
    public void setDefaultCondition(String defaultCondition) {
        this.defaultCondition = defaultCondition;
    }

    /**
     * @return the formuuid
     */
    public String getFormuuid() {
        return formuuid;
    }

    /**
     * @param formuuid 要设置的formuuid
     */
    public void setFormuuid(String formuuid) {
        this.formuuid = formuuid;
    }

    /**
     * @return the tableDefinitionName
     */
    public String getTableDefinitionName() {
        return tableDefinitionName;
    }

    /**
     * @param tableDefinitionName 要设置的tableDefinitionName
     */
    public void setTableDefinitionName(String tableDefinitionName) {
        this.tableDefinitionName = tableDefinitionName;
    }

    /**
     * @return the tableDefinitionText
     */
    public String getTableDefinitionText() {
        return tableDefinitionText;
    }

    /**
     * @param tableDefinitionText 要设置的tableDefinitionText
     */
    public void setTableDefinitionText(String tableDefinitionText) {
        this.tableDefinitionText = tableDefinitionText;
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
     * @return the viewName
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * @param viewName 要设置的viewName
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * @return the dataScope
     */
    public Integer getDataScope() {
        return dataScope;
    }

    /**
     * @param dataScope 要设置的dataScope
     */
    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
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

    /**
     * @return the pageAble
     */
    public boolean isPageAble() {
        return pageAble;
    }

    /**
     * @param pageAble 要设置的pageAble
     */
    public void setPageAble(boolean pageAble) {
        this.pageAble = pageAble;
    }

    /**
     * @return the pageRow
     */
    public Integer getPageRow() {
        return pageRow;
    }

    /**
     * @param pageRow 要设置的pageRow
     */
    public void setPageRow(Integer pageRow) {
        this.pageRow = pageRow;
    }
}
