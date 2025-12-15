/*
 * @(#)8/29/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/29/25.1	    zhulh		8/29/25		    Create
 * </pre>
 * @date 8/29/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowDataSnapshotAuditLog extends BaseObject {
    private static final long serialVersionUID = -6304425809331445719L;
    private String formName;

    private String formId;

    private String formUuid;

    private List<FieldAuditItem> mainFormFields;

    private List<SubformAudit> subforms;

    /**
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @param formName 要设置的formName
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * @return the formId
     */
    public String getFormId() {
        return formId;
    }

    /**
     * @param formId 要设置的formId
     */
    public void setFormId(String formId) {
        this.formId = formId;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the mainFormFields
     */
    public List<FieldAuditItem> getMainFormFields() {
        return mainFormFields;
    }

    /**
     * @param mainFormFields 要设置的mainFormFields
     */
    public void setMainFormFields(List<FieldAuditItem> mainFormFields) {
        this.mainFormFields = mainFormFields;
    }

    /**
     * @return the subforms
     */
    public List<SubformAudit> getSubforms() {
        return subforms;
    }

    /**
     * @param subforms 要设置的subforms
     */
    public void setSubforms(List<SubformAudit> subforms) {
        this.subforms = subforms;
    }

    public static FlowDataSnapshotAuditLog create(Map<String, Object> dataMap, Map<String, Object> oldDataMap) {
        String formUuid = Objects.toString(dataMap.get("formUuid"), StringUtils.EMPTY);
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(formUuid);

        FlowDataSnapshotAuditLog log = new FlowDataSnapshotAuditLog();
        log.setFormName(formDefinition.getName());
        log.setFormId(formDefinition.getId());
        log.setFormUuid(formDefinition.getUuid());
        log.setMainFormFields(getMainFormFieldAudit(formDefinition, dataMap, oldDataMap));
        log.setSubforms(getSubformAudit(formDefinition, dataMap, oldDataMap));
        return log;
    }

    /**
     * @param formDefinition
     * @param newDataMap
     * @param oldDataMap
     * @return
     */
    private static List<FieldAuditItem> getMainFormFieldAudit(DyFormFormDefinition formDefinition, Map<String, Object> newDataMap,
                                                              Map<String, Object> oldDataMap) {
        List<FieldAuditItem> auditItems = Lists.newArrayList();
        List<DyformFieldDefinition> dyformFieldDefinitions = formDefinition.doGetFieldDefintions();
        Set<String> systemFieldCodes = Arrays.stream(EnumSystemField.values()).map(EnumSystemField::getColumn).collect(Collectors.toSet());
        Map<String, Object> newMainFormData = getMainFormData(newDataMap, formDefinition);
        Map<String, Object> oldMainFormData = getMainFormData(oldDataMap, formDefinition);
        Set<String> dataFieldNameSet = Sets.newLinkedHashSet(newMainFormData.keySet());
        dataFieldNameSet.addAll(oldMainFormData.keySet());
        // 用户创建的字段
        dyformFieldDefinitions.forEach(fieldDefinition -> {
            String fieldName = fieldDefinition.getFieldName();
            dataFieldNameSet.remove(fieldName);
            if (systemFieldCodes.contains(fieldName)) {
                return;
            }
            FieldAuditItem auditItem = new FieldAuditItem();
            Object newValue = newMainFormData.get(fieldName);
            Object oldValue = oldMainFormData.get(fieldName);
            auditItem.setName(fieldDefinition.getDisplayName());
            auditItem.setCode(fieldName);
            auditItem.setNewValue(newValue);
            auditItem.setOldValue(oldValue);
            auditItem.setNewValueExists(newMainFormData.containsKey(fieldName));
            auditItem.setOldValueExists(oldMainFormData.containsKey(fieldName));
            auditItems.add(auditItem);
        });
        // 新增、删除的字段
        // TODO
        // 系统字段
        EnumSystemField systemFields[] = EnumSystemField.values();
        for (EnumSystemField systemField : systemFields) {
            String fieldName = systemField.getColumn();
            dataFieldNameSet.remove(fieldName);
            if (!newMainFormData.containsKey(fieldName) && !oldMainFormData.containsKey(fieldName)) {
                continue;
            }
            FieldAuditItem auditItem = new FieldAuditItem();
            auditItem.setName(getSystemFieldName(fieldName));
            auditItem.setCode(fieldName);
            auditItem.setNewValue(newMainFormData.get(fieldName));
            auditItem.setOldValue(oldMainFormData.get(fieldName));
            auditItem.setNewValueExists(newMainFormData.containsKey(fieldName));
            auditItem.setOldValueExists(oldMainFormData.containsKey(fieldName));
            auditItems.add(auditItem);
        }
        if (CollectionUtils.isNotEmpty(dataFieldNameSet)) {
            dataFieldNameSet.forEach(fieldName -> {
                FieldAuditItem auditItem = new FieldAuditItem();
                auditItem.setName(getSystemFieldName(fieldName));
                auditItem.setCode(fieldName);
                auditItem.setNewValue(newMainFormData.get(fieldName));
                auditItem.setOldValue(oldMainFormData.get(fieldName));
                auditItem.setNewValueExists(newMainFormData.containsKey(fieldName));
                auditItem.setOldValueExists(oldMainFormData.containsKey(fieldName));
                auditItems.add(auditItem);
            });
        }
        return auditItems;
    }

    /**
     * @param dataMap
     * @param formDefinition
     * @return
     */
    private static Map<String, Object> getMainFormData(Map<String, Object> dataMap, DyFormFormDefinition formDefinition) {
        if (MapUtils.isEmpty(dataMap)) {
            return Collections.emptyMap();
        }
        String formUuid = formDefinition.getUuid();
        Map<String, Map<String, Object>> formDatas = JsonUtils.json2Object(Objects.toString(dataMap.get("formDatas"), "{}"), Map.class);
        if (formDatas.containsKey(formUuid)) {
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) formDatas.get(formUuid);
            return CollectionUtils.isEmpty(dataList) ? Collections.emptyMap() : dataList.get(0);
        }
        String dataFormUuid = Objects.toString(dataMap.get("formUuid"), StringUtils.EMPTY);
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        DyFormFormDefinition dataFormDefinition = dyFormFacade.getFormDefinition(dataFormUuid);
        if (StringUtils.equals(dataFormDefinition.getTableName(), formDefinition.getTableName())) {
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) formDatas.get(formUuid);
            return CollectionUtils.isEmpty(dataList) ? Collections.emptyMap() : dataList.get(0);
        }
        return Collections.emptyMap();
    }

    /**
     * @param formDefinition
     * @param dataMap
     * @param oldDataMap
     * @return
     */
    private static List<SubformAudit> getSubformAudit(DyFormFormDefinition formDefinition, Map<String, Object> dataMap, Map<String, Object> oldDataMap) {
        List<SubformAudit> subformAudits = Lists.newArrayList();
        List<DyformSubformFormDefinition> subformDefinitions = formDefinition.doGetSubformDefinitions();
        if (CollectionUtils.isEmpty(subformDefinitions)) {
            return subformAudits;
        }
        subformDefinitions.forEach(subformDefinition -> {
            subformAudits.add(getSubformAuditData(subformDefinition, dataMap, oldDataMap));
        });
        return subformAudits;
    }

    private static SubformAudit getSubformAuditData(DyformSubformFormDefinition subformDefinition, Map<String, Object> newDataMap, Map<String, Object> oldDataMap) {
        List<SubformRowAudit> rows = Lists.newArrayList();
        List<Map<String, Object>> newMainFormDatas = getSubFormData(newDataMap, subformDefinition);
        List<Map<String, Object>> oldMainFormDatas = getSubFormData(oldDataMap, subformDefinition);
        List<DyformSubformFieldDefinition> subformFieldDefinitions = subformDefinition.getSubformFieldDefinitions();
        List<String> existsUuids = Lists.newArrayList();
        newMainFormDatas.forEach(newRowData -> {
            String uuid = Objects.toString(newRowData.get("uuid"), StringUtils.EMPTY);
            Map<String, Object> oldRowData = oldMainFormDatas.stream().filter(row -> StringUtils.equals(uuid, Objects.toString(row.get("uuid")))).findFirst().orElse(Collections.emptyMap());
            rows.add(getSubformRowAuditData(newRowData, oldRowData, subformFieldDefinitions));
            existsUuids.add(uuid);
        });
        // 删除的行
        List<Map<String, Object>> deletedMainFormDatas = oldMainFormDatas.stream().filter(row -> !existsUuids.contains(Objects.toString(row.get("uuid")))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deletedMainFormDatas)) {
            deletedMainFormDatas.forEach(deletedRowData -> {
                rows.add(getSubformRowAuditData(Collections.emptyMap(), deletedRowData, subformFieldDefinitions));
            });
        }

        SubformAudit subformAudit = new SubformAudit();
        subformAudit.setName(StringUtils.defaultIfBlank(subformDefinition.getDisplayName(), subformDefinition.getName()));
        subformAudit.setCode(subformDefinition.getOuterId());
        subformAudit.setRows(rows);
        return subformAudit;
    }

    private static SubformRowAudit getSubformRowAuditData(Map<String, Object> newRowData, Map<String, Object> oldRowData, List<DyformSubformFieldDefinition> subformFieldDefinitions) {
        SubformRowAudit subformRowAudit = new SubformRowAudit();
        List<FieldAuditItem> auditItems = Lists.newArrayList();
        Set<String> systemFieldCodes = Arrays.stream(EnumSystemField.values()).map(EnumSystemField::getColumn).collect(Collectors.toSet());
        Map<String, Object> newMainFormData = newRowData;
        Map<String, Object> oldMainFormData = oldRowData;
        Set<String> dataFieldNameSet = Sets.newLinkedHashSet(newMainFormData.keySet());
        dataFieldNameSet.addAll(oldMainFormData.keySet());
        // 用户创建的字段
        subformFieldDefinitions.forEach(fieldDefinition -> {
            String fieldName = fieldDefinition.getName();
            dataFieldNameSet.remove(fieldName);
            if (systemFieldCodes.contains(fieldName)) {
                return;
            }
            FieldAuditItem auditItem = new FieldAuditItem();
            Object newValue = newMainFormData.get(fieldName);
            Object oldValue = oldMainFormData.get(fieldName);
            auditItem.setName(fieldDefinition.getDisplayName());
            auditItem.setCode(fieldName);
            auditItem.setNewValue(newValue);
            auditItem.setOldValue(oldValue);
            auditItem.setNewValueExists(newMainFormData.containsKey(fieldName));
            auditItem.setOldValueExists(oldMainFormData.containsKey(fieldName));
            auditItems.add(auditItem);
        });
        // 新增、删除的字段
        // TODO
        // 系统字段
        EnumSystemField systemFields[] = EnumSystemField.values();
        for (EnumSystemField systemField : systemFields) {
            String fieldName = systemField.getColumn();
            dataFieldNameSet.remove(fieldName);
            if (!newMainFormData.containsKey(fieldName) && !oldMainFormData.containsKey(fieldName)) {
                continue;
            }
            FieldAuditItem auditItem = new FieldAuditItem();
            auditItem.setName(getSystemFieldName(fieldName));
            auditItem.setCode(fieldName);
            auditItem.setNewValue(newMainFormData.get(fieldName));
            auditItem.setOldValue(oldMainFormData.get(fieldName));
            auditItem.setNewValueExists(newMainFormData.containsKey(fieldName));
            auditItem.setOldValueExists(oldMainFormData.containsKey(fieldName));
            auditItems.add(auditItem);
        }
        if (CollectionUtils.isNotEmpty(dataFieldNameSet)) {
            dataFieldNameSet.forEach(fieldName -> {
                FieldAuditItem auditItem = new FieldAuditItem();
                auditItem.setName(getSystemFieldName(fieldName));
                auditItem.setCode(fieldName);
                auditItem.setNewValue(newMainFormData.get(fieldName));
                auditItem.setOldValue(oldMainFormData.get(fieldName));
                auditItem.setNewValueExists(newMainFormData.containsKey(fieldName));
                auditItem.setOldValueExists(oldMainFormData.containsKey(fieldName));
                auditItems.add(auditItem);
            });
        }

        if (MapUtils.isNotEmpty(newRowData) && MapUtils.isEmpty(oldRowData)) {
            subformRowAudit.setAdded(true);
        }
        if (MapUtils.isEmpty(newRowData) && MapUtils.isNotEmpty(oldRowData)) {
            subformRowAudit.setDeleted(true);
        }
        subformRowAudit.setFormFields(auditItems);
        return subformRowAudit;
    }

    private static String getSystemFieldName(String fieldName) {
        if (EnumSystemField.uuid.getColumn().equals(fieldName)) {
            return "数据UUID";
        } else if (EnumSystemField.create_time.getColumn().equals(fieldName)) {
            return "创建时间";
        } else if (EnumSystemField.modify_time.getColumn().equals(fieldName)) {
            return "修改时间";
        } else if (EnumSystemField.creator.getColumn().equals(fieldName)) {
            return "创建人";
        } else if (EnumSystemField.modifier.getColumn().equals(fieldName)) {
            return "修改人";
        } else if (EnumSystemField.rec_ver.getColumn().equals(fieldName)) {
            return "数据版本号";
        } else if (EnumSystemField.system.getColumn().equals(fieldName)) {
            return "归属系统";
        } else if (EnumSystemField.tenant.getColumn().equals(fieldName)) {
            return "归属租户";
        } else if (EnumSystemField.form_uuid.getColumn().equals(fieldName)) {
            return "表单定义UUID";
        } else if (EnumSystemField.status.getColumn().equals(fieldName)) {
            return "状态";
        } else if ("sort_order".equals(fieldName)) {
            return "排序号";
        } else {
            return fieldName;
        }
    }

    private static List<Map<String, Object>> getSubFormData(Map<String, Object> dataMap, DyformSubformFormDefinition subformDefinition) {
        if (MapUtils.isEmpty(dataMap)) {
            return Collections.emptyList();
        }
        String formUuid = subformDefinition.getFormUuid();
        Map<String, Map<String, Object>> formDatas = JsonUtils.json2Object(Objects.toString(dataMap.get("formDatas"), "{}"), Map.class);
        if (formDatas.containsKey(formUuid)) {
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) formDatas.get(formUuid);
            return CollectionUtils.isEmpty(dataList) ? Collections.emptyList() : dataList;
        }

        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(formUuid);
        Set<String> subformUuids = formDatas.keySet();
        for (String dataFormUuid : subformUuids) {
            DyFormFormDefinition dataFormDefinition = dyFormFacade.getFormDefinition(dataFormUuid);
            if (StringUtils.equals(dataFormDefinition.getTableName(), formDefinition.getTableName())) {
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) formDatas.get(formUuid);
                return CollectionUtils.isEmpty(dataList) ? Collections.emptyList() : dataList;
            }
        }

        return Collections.emptyList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class FieldAuditItem extends BaseObject {
        private static final long serialVersionUID = 6329271462824129181L;

        private String name;
        private String code;
        private Object newValue;
        private Object oldValue;
        private boolean newValueExists;
        private boolean oldValueExists;

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
         * @return the newValue
         */
        public Object getNewValue() {
            return newValue;
        }

        /**
         * @param newValue 要设置的newValue
         */
        public void setNewValue(Object newValue) {
            this.newValue = newValue;
        }

        /**
         * @return the oldValue
         */
        public Object getOldValue() {
            return oldValue;
        }

        /**
         * @param oldValue 要设置的oldValue
         */
        public void setOldValue(Object oldValue) {
            this.oldValue = oldValue;
        }

        /**
         * @return the newValueExists
         */
        public boolean isNewValueExists() {
            return newValueExists;
        }

        /**
         * @param newValueExists 要设置的newValueExists
         */
        public void setNewValueExists(boolean newValueExists) {
            this.newValueExists = newValueExists;
        }

        /**
         * @return the oldValueExists
         */
        public boolean isOldValueExists() {
            return oldValueExists;
        }

        /**
         * @param oldValueExists 要设置的oldValueExists
         */
        public void setOldValueExists(boolean oldValueExists) {
            this.oldValueExists = oldValueExists;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SubformAudit extends BaseObject {
        private static final long serialVersionUID = -902635928363269825L;
        private String name;
        private String code;
        private List<SubformRowAudit> rows;

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
         * @return the rows
         */
        public List<SubformRowAudit> getRows() {
            return rows;
        }

        /**
         * @param rows 要设置的rows
         */
        public void setRows(List<SubformRowAudit> rows) {
            this.rows = rows;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SubformRowAudit extends BaseObject {
        private static final long serialVersionUID = 6034283861658129779L;
        private boolean added;
        private boolean deleted;
        private List<FieldAuditItem> formFields;

        /**
         * @return the added
         */
        public boolean isAdded() {
            return added;
        }

        /**
         * @param added 要设置的added
         */
        public void setAdded(boolean added) {
            this.added = added;
        }

        /**
         * @return the deleted
         */
        public boolean isDeleted() {
            return deleted;
        }

        /**
         * @param deleted 要设置的deleted
         */
        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        /**
         * @return the formFields
         */
        public List<FieldAuditItem> getFormFields() {
            return formFields;
        }

        /**
         * @param formFields 要设置的formFields
         */
        public void setFormFields(List<FieldAuditItem> formFields) {
            this.formFields = formFields;
        }
    }

}
