/*
 * @(#)8 Mar 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.config;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.app.ui.client.widget.configuration.AppWidgetDefinitionConfiguration;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8 Mar 2017.1	Xiem		8 Mar 2017		Create
 * </pre>
 * @date 8 Mar 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BootstrapTableConfiguration extends AppWidgetDefinitionConfiguration {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7223430932385835217L;
    private String name;
    private String categoryName;
    private String categoryCode;
    private String dataStoreId;
    private String dataStoreName;
    private String jsModule;
    private String defaultCondition;
    private String pageSize;
    private String width;
    private String height;
    private Boolean pagination;
    private String pageList;
    private Boolean hasExport;
    private String exportDataType;
    private String fileName;
    private String exportTypes;
    private String exportTypeShows;
    private Boolean readMarker;
    private String readMarkerField;
    private Boolean hideChecked;
    private Boolean multiSelect;
    private Boolean columnDnd;
    private Boolean hideColumnHeader;
    private List<DefaultSort> DefaultSort = new ArrayList<DefaultSort>();
    private List<Column> columns = new ArrayList<Column>();
    private List<Button> buttons = new ArrayList<Button>();
    private Query query;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getDataStoreId() {
        return dataStoreId;
    }

    public void setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
    }

    public String getDataStoreName() {
        return dataStoreName;
    }

    public void setDataStoreName(String dataStoreName) {
        this.dataStoreName = dataStoreName;
    }

    public String getJsModule() {
        return jsModule;
    }

    public void setJsModule(String jsModule) {
        this.jsModule = jsModule;
    }

    public String getDefaultCondition() {
        return defaultCondition;
    }

    public void setDefaultCondition(String defaultCondition) {
        this.defaultCondition = defaultCondition;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Boolean getPagination() {
        return pagination;
    }

    public void setPagination(Boolean pagination) {
        this.pagination = pagination;
    }

    public String getPageList() {
        return pageList;
    }

    public void setPageList(String pageList) {
        this.pageList = pageList;
    }

    public Boolean getHasExport() {
        return hasExport;
    }

    public void setHasExport(Boolean hasExport) {
        this.hasExport = hasExport;
    }

    public String getExportDataType() {
        return exportDataType;
    }

    public void setExportDataType(String exportDataType) {
        this.exportDataType = exportDataType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExportTypes() {
        return exportTypes;
    }

    public void setExportTypes(String exportTypes) {
        this.exportTypes = exportTypes;
    }

    public String getExportTypeShows() {
        return exportTypeShows;
    }

    public void setExportTypeShows(String exportTypeShows) {
        this.exportTypeShows = exportTypeShows;
    }

    public Boolean getReadMarker() {
        return readMarker;
    }

    public void setReadMarker(Boolean readMarker) {
        this.readMarker = readMarker;
    }

    public String getReadMarkerField() {
        return readMarkerField;
    }

    public void setReadMarkerField(String readMarkerField) {
        this.readMarkerField = readMarkerField;
    }

    public Boolean getHideChecked() {
        return hideChecked;
    }

    public void setHideChecked(Boolean hideChecked) {
        this.hideChecked = hideChecked;
    }

    public Boolean getMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(Boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public Boolean getColumnDnd() {
        return columnDnd;
    }

    public void setColumnDnd(Boolean columnDnd) {
        this.columnDnd = columnDnd;
    }

    public Boolean getHideColumnHeader() {
        return hideColumnHeader;
    }

    public void setHideColumnHeader(Boolean hideColumnHeader) {
        this.hideColumnHeader = hideColumnHeader;
    }

    public List<DefaultSort> getDefaultSort() {
        return DefaultSort;
    }

    public void setDefaultSort(List<DefaultSort> defaultSort) {
        DefaultSort = defaultSort;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        List<FunctionElement> functionElements = Lists.newArrayList();
        // 1、数据仓库
        String dataStoreId = getDataStoreId();
        appendRefDataStoreFunctionElementById(dataStoreId, functionElements);
        // 2、视图按钮
        List<Button> buttons = getButtons();
        for (int index = 0; index < buttons.size(); index++) {
            appendButtonFunctionElement(index, buttons.get(index), functionElements);
        }
        //dataStore  dataDic
        List<Field> fieldList = getQuery().getFields();
        List<String> dataStoreList = new ArrayList<>();
        List<String> dataDicList = new ArrayList<>();
        for (Field field : fieldList) {
            Map<String, Object> queryOptions = field.getQueryOptions();
            if (queryOptions == null) {
                continue;
            }
            if (queryOptions.get("dataStore") != null) {
                dataStoreList.add(queryOptions.get("dataStore").toString());
            }
            if (queryOptions.get("dataDic") != null) {
                dataDicList.add(queryOptions.get("dataDic").toString());
            }
        }
        for (String id : dataStoreList) {
            appendRefDataStoreFunctionElementById(id, functionElements);
        }
        for (String uuid : dataDicList) {
            appendRefDataDicFunctionElementByUuid(uuid, functionElements);
        }
        return functionElements;
    }

    /**
     * @param index
     * @param button
     * @param functionElements
     */
    private void appendButtonFunctionElement(int index, Button button, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(button.getUuid());
        functionElement.setId(button.getCode());
        functionElement.setName("按钮_" + button.getText());
        functionElement.setCode("button_" + (index + 1));
        functionElements.add(functionElement);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Button extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6651358689424546134L;
        private String uuid;
        private String text;
        private String[] position;
        private String group;
        private String code;
        private String cssClass;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String[] getPosition() {
            return position;
        }

        public void setPosition(String[] position) {
            this.position = position;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCssClass() {
            return cssClass;
        }

        public void setCssClass(String cssClass) {
            this.cssClass = cssClass;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DefaultSort extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5555730811509795119L;
        private String uuid;
        private String sortName;
        private String sortOrder;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getSortName() {
            return sortName;
        }

        public void setSortName(String sortName) {
            this.sortName = sortName;
        }

        public String getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Field extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -1216237482356664248L;
        private String uuid;
        private String label;
        private String name;
        private String queryType;
        private String defaultValue;
        private String operator;
        private Map<String, Object> queryOptions;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public Map<String, Object> getQueryOptions() {
            return queryOptions;
        }

        public void setQueryOptions(Map<String, Object> queryOptions) {
            this.queryOptions = queryOptions;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Query extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -4291546723359540969L;
        private Boolean keyword;
        private Boolean fieldSearch;
        private Boolean allowSaveTemplate;
        private List<Field> fields = new ArrayList<Field>();

        public Boolean getKeyword() {
            return keyword;
        }

        public void setKeyword(Boolean keyword) {
            this.keyword = keyword;
        }

        public Boolean getFieldSearch() {
            return fieldSearch;
        }

        public void setFieldSearch(Boolean fieldSearch) {
            this.fieldSearch = fieldSearch;
        }

        public Boolean getAllowSaveTemplate() {
            return allowSaveTemplate;
        }

        public void setAllowSaveTemplate(Boolean allowSaveTemplate) {
            this.allowSaveTemplate = allowSaveTemplate;
        }

        public List<Field> getFields() {
            return fields;
        }

        public void setFields(List<Field> fields) {
            this.fields = fields;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Column extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -175838594889286046L;
        private String uuid;
        private String header;
        private String name;
        private String width;
        private String hidden;
        private String sortable;
        private String editable;
        private String idField;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHidden() {
            return hidden;
        }

        public void setHidden(String hidden) {
            this.hidden = hidden;
        }

        public String getSortable() {
            return sortable;
        }

        public void setSortable(String sortable) {
            this.sortable = sortable;
        }

        public String getEditable() {
            return editable;
        }

        public void setEditable(String editable) {
            this.editable = editable;
        }

        public String getIdField() {
            return idField;
        }

        public void setIdField(String idField) {
            this.idField = idField;
        }

    }

}
