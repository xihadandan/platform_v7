/*
 * @(#)6/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.json.JsonUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/16/25.1	    zhulh		6/16/25		    Create
 * </pre>
 * @date 6/16/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FulltextSetting extends BaseObject {

    private static final long serialVersionUID = -1862343845468318182L;

    public static final String UPDATE_MODE_REGULAR = "regular";

    private Boolean enabled;

    private List<SearchScope> scopeList;

    private String resultDuplication;

    // 自动获取数据源的前n个字段作为索引摘要
    private Integer fieldCount;

    // 获取字段时排除以下字段
    private List<String> excludeFields;

    // 索引所有字段
    private Boolean indexAllField;
    // 索引前n个字段
    private Integer indexFieldCount;
    // 获取字段时排除以下索引字段
    private List<String> excludeIndexFields;

    // 枚举索引方式，value真实值、label显示值
    private List<String> enumIndexModes = Lists.newArrayList("label");

    // 是否索引附件
    private Boolean indexAttachment;

    // 平台内置
    private Map<String, Object> builtIn;

    // realtime实时，定时regula
    private String updateMode;

    // 定时时间点
    private String regularTimePoint;

    // 索引重建任务
    private List<RebuildRule> rebuildRules;

    private Long uuid;

    // 创建人
    private String creator;

    // 归属系统
    private String system;

    // 归属租户
    private String tenant;

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the scopeList
     */
    public List<SearchScope> getScopeList() {
        return scopeList;
    }

    /**
     * @param scopeList 要设置的scopeList
     */
    public void setScopeList(List<SearchScope> scopeList) {
        this.scopeList = scopeList;
    }

    /**
     * @return the resultDuplication
     */
    public String getResultDuplication() {
        return resultDuplication;
    }

    /**
     * @param resultDuplication 要设置的resultDuplication
     */
    public void setResultDuplication(String resultDuplication) {
        this.resultDuplication = resultDuplication;
    }

    /**
     * @return the fieldCount
     */
    public Integer getFieldCount() {
        return fieldCount;
    }

    /**
     * @param fieldCount 要设置的fieldCount
     */
    public void setFieldCount(Integer fieldCount) {
        this.fieldCount = fieldCount;
    }

    /**
     * @return the excludeFields
     */
    public List<String> getExcludeFields() {
        return excludeFields;
    }

    /**
     * @param excludeFields 要设置的excludeFields
     */
    public void setExcludeFields(List<String> excludeFields) {
        this.excludeFields = excludeFields;
    }

    /**
     * @return the indexAllField
     */
    public Boolean getIndexAllField() {
        return indexAllField;
    }

    /**
     * @param indexAllField 要设置的indexAllField
     */
    public void setIndexAllField(Boolean indexAllField) {
        this.indexAllField = indexAllField;
    }

    /**
     * @return the indexFieldCount
     */
    public Integer getIndexFieldCount() {
        return indexFieldCount;
    }

    /**
     * @param indexFieldCount 要设置的indexFieldCount
     */
    public void setIndexFieldCount(Integer indexFieldCount) {
        this.indexFieldCount = indexFieldCount;
    }

    /**
     * @return the excludeIndexFields
     */
    public List<String> getExcludeIndexFields() {
        return excludeIndexFields;
    }

    /**
     * @param excludeIndexFields 要设置的excludeIndexFields
     */
    public void setExcludeIndexFields(List<String> excludeIndexFields) {
        this.excludeIndexFields = excludeIndexFields;
    }

    /**
     * @return the enumIndexModes
     */
    public List<String> getEnumIndexModes() {
        return enumIndexModes;
    }

    /**
     * @param enumIndexModes 要设置的enumIndexModes
     */
    public void setEnumIndexModes(List<String> enumIndexModes) {
        this.enumIndexModes = enumIndexModes;
    }

    /**
     * @return the indexAttachment
     */
    public Boolean getIndexAttachment() {
        return BooleanUtils.isTrue(indexAttachment);
    }

    /**
     * @param indexAttachment 要设置的indexAttachment
     */
    public void setIndexAttachment(Boolean indexAttachment) {
        this.indexAttachment = indexAttachment;
    }

    /**
     * @return the builtIn
     */
    public Map<String, Object> getBuiltIn() {
        return builtIn;
    }

    /**
     *
     */
    public Set<String> getBuiltInKeys() {
        if (builtIn != null) {
            return builtIn.keySet();
        }
        return Sets.newHashSet("workflow", "dms_file");
    }

    /**
     * @return the builtIn
     */
    public <T> T getBuiltIn(String type, Class<T> cls) {
        if (builtIn == null) {
            builtIn = Maps.newHashMap();
        }
        if (StringUtils.equals("workflow", type) && builtIn.get(type) == null) {
            Map<String, Object> workflow = Maps.newHashMap();
            workflow.put("categoryName", "流程");
            workflow.put("titleExpression", "${流程标题}");
            workflow.put("contentExpression", "${流程标题}${流程名称}${流程ID}${流程编号}${发起人姓名}${发起人所在部门名称}");
            builtIn.put(type, workflow);
        }
        Object config = builtIn.get(type);
        if (config == null) {
            config = Maps.newHashMap();
        }
        T t = JsonUtils.json2Object(JsonUtils.object2Json(config), cls);
        return t;
    }

    /**
     * @param builtIn 要设置的builtIn
     */
    public void setBuiltIn(Map<String, Object> builtIn) {
        this.builtIn = builtIn;
    }

    /**
     * @return the updateMode
     */
    public String getUpdateMode() {
        return updateMode;
    }

    /**
     * @param updateMode 要设置的updateMode
     */
    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    /**
     * @return the regularTimePoint
     */
    public String getRegularTimePoint() {
        return regularTimePoint;
    }

    /**
     * @param regularTimePoint 要设置的regularTimePoint
     */
    public void setRegularTimePoint(String regularTimePoint) {
        this.regularTimePoint = regularTimePoint;
    }

    /**
     * @return the rebuildRules
     */
    public List<RebuildRule> getRebuildRules() {
        return rebuildRules;
    }

    /**
     * @param rebuildRules 要设置的rebuildRules
     */
    public void setRebuildRules(List<RebuildRule> rebuildRules) {
        this.rebuildRules = rebuildRules;
    }

    /**
     * @return the uuid
     */
    public Long getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator 要设置的creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RebuildRule extends BaseObject {

        private static final long serialVersionUID = -2752815149165984886L;

        public static String STATE_ENABLED = "1";
        public static String STATE_DISABLED = "2";
        public static String STATE_COMPLETED = "3";

        // 规则ID
        private String uuid;

        // 重复周期，none不重复、repeat重复
        private String repeatType;
        // 重复间隔数
        private Integer repeatInterval;
        // 重复间隔单位，day天、week周、month月、year年
        private String repeatUnit;
        // 周一～日
        private List<String> repeatDaysOfWeek;
        // first第一天、fifteen第十五天、last最后一天
        private String repeatDayOfMonth;
        // 1~12月
        private String repeatMonthOfYear;
        // 执行时间点
        private String timePoint;
        // 不重复的执行时间
        private String executeTime;
        // 1已启用、2已停用、3已完成
        private String state;

        /**
         * @return the uuid
         */
        public String getUuid() {
            return uuid;
        }

        /**
         * @param uuid 要设置的uuid
         */
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        /**
         * @return the repeatType
         */
        public String getRepeatType() {
            return repeatType;
        }

        /**
         * @param repeatType 要设置的repeatType
         */
        public void setRepeatType(String repeatType) {
            this.repeatType = repeatType;
        }

        /**
         * @return the repeatInterval
         */
        public Integer getRepeatInterval() {
            return repeatInterval;
        }

        /**
         * @param repeatInterval 要设置的repeatInterval
         */
        public void setRepeatInterval(Integer repeatInterval) {
            this.repeatInterval = repeatInterval;
        }

        /**
         * @return the repeatUnit
         */
        public String getRepeatUnit() {
            return repeatUnit;
        }

        /**
         * @param repeatUnit 要设置的repeatUnit
         */
        public void setRepeatUnit(String repeatUnit) {
            this.repeatUnit = repeatUnit;
        }

        /**
         * @return the repeatDaysOfWeek
         */
        public List<String> getRepeatDaysOfWeek() {
            return repeatDaysOfWeek;
        }

        /**
         * @param repeatDaysOfWeek 要设置的repeatDaysOfWeek
         */
        public void setRepeatDaysOfWeek(List<String> repeatDaysOfWeek) {
            this.repeatDaysOfWeek = repeatDaysOfWeek;
        }

        /**
         * @return the repeatDayOfMonth
         */
        public String getRepeatDayOfMonth() {
            return repeatDayOfMonth;
        }

        /**
         * @param repeatDayOfMonth 要设置的repeatDayOfMonth
         */
        public void setRepeatDayOfMonth(String repeatDayOfMonth) {
            this.repeatDayOfMonth = repeatDayOfMonth;
        }

        /**
         * @return the repeatMonthOfYear
         */
        public String getRepeatMonthOfYear() {
            return repeatMonthOfYear;
        }

        /**
         * @param repeatMonthOfYear 要设置的repeatMonthOfYear
         */
        public void setRepeatMonthOfYear(String repeatMonthOfYear) {
            this.repeatMonthOfYear = repeatMonthOfYear;
        }

        /**
         * @return the timePoint
         */
        public String getTimePoint() {
            return timePoint;
        }

        /**
         * @param timePoint 要设置的timePoint
         */
        public void setTimePoint(String timePoint) {
            this.timePoint = timePoint;
        }

        /**
         * @return the executeTime
         */
        public String getExecuteTime() {
            return executeTime;
        }

        /**
         * @param executeTime 要设置的executeTime
         */
        public void setExecuteTime(String executeTime) {
            this.executeTime = executeTime;
        }

        /**
         * @return the state
         */
        public String getState() {
            return state;
        }

        /**
         * @param state 要设置的state
         */
        public void setState(String state) {
            this.state = state;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkflowConfiguration extends BaseObject {

        private static final long serialVersionUID = 3569490076104390357L;

        // 数据分类名称
        private String categoryName;
        // 索引标题
        private String titleExpression;
        // 索引摘要
        private String contentExpression;
        // 索引备注
        private String remarkExpression;
        // 枚举索引方式，value真实值、label显示值
        private List<String> enumIndexModes = Lists.newArrayList("label");
        // 是否索引附件
        private Boolean indexAttachment;

        /**
         * @return the categoryName
         */
        public String getCategoryName() {
            return categoryName;
        }

        /**
         * @param categoryName 要设置的categoryName
         */
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        /**
         * @return the titleExpression
         */
        public String getTitleExpression() {
            return titleExpression;
        }

        /**
         * @param titleExpression 要设置的titleExpression
         */
        public void setTitleExpression(String titleExpression) {
            this.titleExpression = titleExpression;
        }

        /**
         * @return the contentExpression
         */
        public String getContentExpression() {
            return contentExpression;
        }

        /**
         * @param contentExpression 要设置的contentExpression
         */
        public void setContentExpression(String contentExpression) {
            this.contentExpression = contentExpression;
        }

        /**
         * @return the remarkExpression
         */
        public String getRemarkExpression() {
            return remarkExpression;
        }

        /**
         * @param remarkExpression 要设置的remarkExpression
         */
        public void setRemarkExpression(String remarkExpression) {
            this.remarkExpression = remarkExpression;
        }

        /**
         * @return the enumIndexModes
         */
        public List<String> getEnumIndexModes() {
            return enumIndexModes;
        }

        /**
         * @param enumIndexModes 要设置的enumIndexModes
         */
        public void setEnumIndexModes(List<String> enumIndexModes) {
            this.enumIndexModes = enumIndexModes;
        }

        /**
         * @return the indexAttachment
         */
        public Boolean getIndexAttachment() {
            return BooleanUtils.isTrue(indexAttachment);
        }

        /**
         * @param indexAttachment 要设置的indexAttachment
         */
        public void setIndexAttachment(Boolean indexAttachment) {
            this.indexAttachment = indexAttachment;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DmsFileConfiguration extends BaseObject {

        // 数据分类名称
        private String categoryName;
        // 是否索引附件
        private Boolean indexAttachment;

        /**
         * @return the categoryName
         */
        public String getCategoryName() {
            return categoryName;
        }

        /**
         * @param categoryName 要设置的categoryName
         */
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        /**
         * @return the indexAttachment
         */
        public Boolean getIndexAttachment() {
            return BooleanUtils.isTrue(indexAttachment);
        }

        /**
         * @param indexAttachment 要设置的indexAttachment
         */
        public void setIndexAttachment(Boolean indexAttachment) {
            this.indexAttachment = indexAttachment;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchScope extends BaseObject {

        private static final long serialVersionUID = -7271436058554449323L;

        private String name;

        private String value;

        private boolean visible;

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
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value 要设置的value
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * @return the visible
         */
        public boolean isVisible() {
            return visible;
        }

        /**
         * @param visible 要设置的visible
         */
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

}
