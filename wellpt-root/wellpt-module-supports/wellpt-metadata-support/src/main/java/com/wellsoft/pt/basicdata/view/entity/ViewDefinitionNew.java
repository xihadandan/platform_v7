/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
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
@Deprecated
//@Entity
//@Table(name = "view_definition")
//@DynamicUpdate
//@DynamicInsert
public class ViewDefinitionNew extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1204247570639768479L;

    private String id;//视图的jqgrid行标识

    @NotBlank
    private String viewName; // 视图名称

    @NotBlank
    private String code; // 视图编号

    @NotBlank
    private String viewId; // 视图id

    private Boolean showTitle; //是否显示视图的标题

    private Boolean showCheckBox;//是否显示复选框

    private String checkKey;//复选框key

    private Boolean isRead;//启用已读未读

    private String readKey;//启用已读未读
    private String tableDefinitionText; //数据源的名称
    @NotBlank
    private String tableDefinitionId; //数据源的id
    @NotBlank
    private String cateUuid; //所属模块id

    private String cateName; //所属模块名

    private String moduleId; //所属模块ID

    private String defaultCondition; //默认的搜索条件

    private Boolean pageAble; // 是否分页

    private Integer pageRow; // 每页的行数

    private String clickEvent;//行点击事件

    private String url; // 点击事件跳转的url

    private String lineType; // 点击事件跳转的url

    private String jsSrc;//自定义js的路径

    private Boolean buttonPlace;//是否头尾部显示

    private Boolean specialField;//是否存在特殊列值计算

    private String specialFieldMethod;//特殊列值计算调用的js方法

    private String requestParamName;//特殊列值计算调用的js方法的请求参数name

    private String requestParamId;//特殊列值计算调用的js方法的请求参数id

    private String responseParamName;//特殊列值计算调用的js方法的输出参数name

    private String responseParamId;//特殊列值计算调用的js方法的输出参数id

    private String dataModuleName;//数据导出模版的名字

    private String dataModuleId;//数据导出模版的id

    private String absoluteWidth;//设置视图的绝对宽度

    private String dataInterfaceName;//数据导出接口的名字

    private String dataInterfaceId;//数据导出接口的Id

    @UnCloneable
    private Set<ColumnDefinitionNew> columnDefinitionNews = new LinkedHashSet<ColumnDefinitionNew>();

    @UnCloneable
    private Set<CustomButtonNew> customButtonNews = new LinkedHashSet<CustomButtonNew>();

    @UnCloneable
    private SelectDefinitionNew selectDefinitionNews;

    @UnCloneable
    private PageDefinitionNew pageDefinitionNews;

    @UnCloneable
    private Set<ColumnCssDefinitionNew> columnCssDefinitionNew = new LinkedHashSet<ColumnCssDefinitionNew>();

    /**
     * @return the dataInterfaceName
     */
    public String getDataInterfaceName() {
        return dataInterfaceName;
    }

    /**
     * @param dataInterfaceName 要设置的dataInterfaceName
     */
    public void setDataInterfaceName(String dataInterfaceName) {
        this.dataInterfaceName = dataInterfaceName;
    }

    /**
     * @return the dataInterfaceId
     */
    public String getDataInterfaceId() {
        return dataInterfaceId;
    }

    /**
     * @param dataInterfaceId 要设置的dataInterfaceId
     */
    public void setDataInterfaceId(String dataInterfaceId) {
        this.dataInterfaceId = dataInterfaceId;
    }

    /**
     * @return the absoluteWidth
     */
    public String getAbsoluteWidth() {
        return absoluteWidth;
    }

    /**
     * @param absoluteWidth 要设置的absoluteWidth
     */
    public void setAbsoluteWidth(String absoluteWidth) {
        this.absoluteWidth = absoluteWidth;
    }

    /**
     * @return the clickEvent
     */
    public String getClickEvent() {
        return clickEvent;
    }

    /**
     * @param clickEvent 要设置的clickEvent
     */
    public void setClickEvent(String clickEvent) {
        this.clickEvent = clickEvent;
    }

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
     * @return the columnDefinitionNews
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "viewDefinitionNew")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("sortOrder asc")
    public Set<ColumnDefinitionNew> getColumnDefinitionNews() {
        return columnDefinitionNews;
    }

    /**
     * @param columnDefinitionNews 要设置的columnDefinitionNews
     */
    public void setColumnDefinitionNews(Set<ColumnDefinitionNew> columnDefinitionNews) {
        this.columnDefinitionNews = columnDefinitionNews;
    }

    /**
     * @return the customButtonNews
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "viewDefinitionNew")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("code asc")
    public Set<CustomButtonNew> getCustomButtonNews() {
        return customButtonNews;
    }

    /**
     * @param customButtonNews 要设置的customButtonNews
     */
    public void setCustomButtonNews(Set<CustomButtonNew> customButtonNews) {
        this.customButtonNews = customButtonNews;
    }

    /**
     * @return the selectDefinitionNews
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "viewDefinitionNew")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public SelectDefinitionNew getSelectDefinitionNews() {
        return selectDefinitionNews;
    }

    /**
     * @param selectDefinitionNews 要设置的selectDefinitionNews
     */
    public void setSelectDefinitionNews(SelectDefinitionNew selectDefinitionNews) {
        this.selectDefinitionNews = selectDefinitionNews;
    }

    /**
     * @return the pageDefinitionNews
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "viewDefinitionNew")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public PageDefinitionNew getPageDefinitionNews() {
        return pageDefinitionNews;
    }

    /**
     * @param pageDefinitionNews 要设置的pageDefinitionNews
     */
    public void setPageDefinitionNews(PageDefinitionNew pageDefinitionNews) {
        this.pageDefinitionNews = pageDefinitionNews;
    }

    /**
     * @return the columnCssDefinitionNew
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "viewDefinitionNew")
    @Cascade({CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<ColumnCssDefinitionNew> getColumnCssDefinitionNew() {
        return columnCssDefinitionNew;
    }

    /**
     * @param columnCssDefinitionNew 要设置的columnCssDefinitionNew
     */
    public void setColumnCssDefinitionNew(Set<ColumnCssDefinitionNew> columnCssDefinitionNew) {
        this.columnCssDefinitionNew = columnCssDefinitionNew;
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


    public Boolean getPageAble() {
        return pageAble;
    }

    public void setPageAble(Boolean pageAble) {
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

    /**
     * @return the tableDefinitionId
     */
    public String getTableDefinitionId() {
        return tableDefinitionId;
    }

    /**
     * @param tableDefinitionId 要设置的tableDefinitionId
     */
    public void setTableDefinitionId(String tableDefinitionId) {
        this.tableDefinitionId = tableDefinitionId;
    }

}
