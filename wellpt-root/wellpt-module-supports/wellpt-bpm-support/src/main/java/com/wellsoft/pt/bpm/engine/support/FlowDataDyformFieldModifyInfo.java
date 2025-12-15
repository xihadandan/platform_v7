/*
 * @(#)10/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/16/25.1	    zhulh		10/16/25		    Create
 * </pre>
 * @date 10/16/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowDataDyformFieldModifyInfo extends BaseObject {
    private static final long serialVersionUID = -6764998750487172015L;

    private Map<String, ModifyInfo> formFieldMap = Maps.newHashMap();

    /**
     * @return the formFieldMap
     */
    public Map<String, ModifyInfo> getFormFieldMap() {
        return formFieldMap;
    }

    /**
     * @param formFieldMap 要设置的formFieldMap
     */
    public void setFormFieldMap(Map<String, ModifyInfo> formFieldMap) {
        this.formFieldMap = formFieldMap;
    }

    private static class ModifyInfo {
        private String modifyUserName;
        private String modifier;
        private Date modifyTime;

        /**
         * @return the modifyUserName
         */
        public String getModifyUserName() {
            return modifyUserName;
        }

        /**
         * @param modifyUserName 要设置的modifyUserName
         */
        public void setModifyUserName(String modifyUserName) {
            this.modifyUserName = modifyUserName;
        }

        /**
         * @return the modifier
         */
        public String getModifier() {
            return modifier;
        }

        /**
         * @param modifier 要设置的modifier
         */
        public void setModifier(String modifier) {
            this.modifier = modifier;
        }

        /**
         * @return the modifyTime
         */
        public Date getModifyTime() {
            return modifyTime;
        }

        /**
         * @param modifyTime 要设置的modifyTime
         */
        public void setModifyTime(Date modifyTime) {
            this.modifyTime = modifyTime;
        }
    }


    public static FlowDataDyformFieldModifyInfo createDefaults(List<String> fieldNames, String modifyUserName, Date modifyTime) {
        FlowDataDyformFieldModifyInfo modifyInfo = new FlowDataDyformFieldModifyInfo();
        for (String fieldName : fieldNames) {
            ModifyInfo info = new ModifyInfo();
            info.setModifyUserName(modifyUserName);
            info.setModifyTime(modifyTime);
            modifyInfo.formFieldMap.put(fieldName, info);
        }
        return modifyInfo;
    }

    public static FlowDataDyformFieldModifyInfo create(List<String> fieldNames, List<QueryItem> queryItems) {
        FlowDataDyformFieldModifyInfo modifyInfo = new FlowDataDyformFieldModifyInfo();
        Map<String, Boolean> fieldChanged = Maps.newHashMap();
        Map<String, Object> latestFieldValueMap = Maps.newHashMap();
        QueryItem previousItem = null;
        for (QueryItem queryItem : queryItems) {
            String formDatas = queryItem.getString("formDatas");
            String formUuid = queryItem.getString("formUuid");
            if (StringUtils.isBlank(formDatas)) {
                continue;
            }
            Map<String, List<Map<String, Object>>> formDatasMap = JsonUtils.json2Object(formDatas, Map.class);
            List<Map<String, Object>> dataList = formDatasMap.get(formUuid);
            if (CollectionUtils.isEmpty(dataList)) {
                continue;
            }

            Map<String, Object> formData = dataList.get(0);
            for (String fieldName : fieldNames) {
                if (BooleanUtils.isTrue(fieldChanged.get(fieldName))) {
                    continue;
                }
                if (!latestFieldValueMap.containsKey(fieldName)) {
                    latestFieldValueMap.put(fieldName, formData.get(fieldName));
                } else {
                    if (isDiff(latestFieldValueMap.get(fieldName), formData.get(fieldName))) {
                        if (previousItem == null) {
                            previousItem = queryItem;
                        }
                        fieldChanged.put(fieldName, true);
                        String modifyUserName = previousItem.getString("createUserName");
                        String modifier = previousItem.getString("createUserId");
                        Date modifyTime = previousItem.getDate("createTime");
                        ModifyInfo info = new ModifyInfo();
                        info.setModifyUserName(modifyUserName);
                        info.setModifier(modifier);
                        info.setModifyTime(modifyTime);
                        modifyInfo.formFieldMap.put(fieldName, info);
                    }
                }
            }

            previousItem = queryItem;

            if (CollectionUtils.size(fieldNames) == CollectionUtils.size(fieldChanged)) {
                break;
            }
        }

        // 处理没有变化的字段
        if (CollectionUtils.size(fieldNames) != CollectionUtils.size(fieldChanged)) {
            previousItem = queryItems.get(queryItems.size() - 1);
            String modifyUserName = previousItem.getString("createUserName");
            String modifier = previousItem.getString("createUserId");
            Date modifyTime = previousItem.getDate("createTime");
            fieldNames.forEach(fieldName -> {
                if (!fieldChanged.containsKey(fieldName)) {
                    ModifyInfo info = new ModifyInfo();
                    info.setModifyUserName(modifyUserName);
                    info.setModifier(modifier);
                    info.setModifyTime(modifyTime);
                    modifyInfo.formFieldMap.put(fieldName, info);
                }
            });
        }
        return modifyInfo;
    }

    /**
     * @param newValue
     * @param oldValue
     * @return
     */
    private static boolean isDiff(Object newValue, Object oldValue) {
        if (newValue instanceof Collection || oldValue instanceof Collection) {
            if ((newValue == null && oldValue != null && CollectionUtils.size(oldValue) == 0)
                    || (newValue != null && oldValue == null && CollectionUtils.size(newValue) == 0)) {
                return false;
            }

            return !JsonUtils.object2Json(newValue).equals(JsonUtils.object2Json(oldValue));
        } else {
            if (newValue == null && oldValue == null) {
                return false;
            }
            if (newValue == null && oldValue != null || (newValue != null && oldValue == null)) {
                return true;
            }
            return !newValue.equals(oldValue);
        }
    }

}
