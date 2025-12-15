package com.wellsoft.pt.dm.dto;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/3    chenq		2018/7/3		Create
 * </pre>
 */
public class TreeDataModelDataRequestParam implements Serializable {

    private static final long serialVersionUID = 1413408020603950347L;

    private String searchText;

    private Long dataModelUuid;

    private String dataModelId;

    private String valueColumn;//值字段

    private String uniqueColumn;//唯一标识字段

    private String parentColumn;//父级字段

    private String displayColumn;//导航节点展示文本字段

    private String treeRootName;//根节点名称

    private String defaultCondition;//默认查询条件

    private String parentColumnValue;//父级字段值

    private Boolean async = false;//是否异步加载各子节点

    private Set<Integer> noCheckLevel = Sets.newHashSet();//不显示checkbox的等级节点

    // 查询参数
    private Map<String, Object> params = Maps.newHashMapWithExpectedSize(0);

    public Long getDataModelUuid() {
        return dataModelUuid;
    }

    public void setDataModelUuid(Long dataModelUuid) {
        this.dataModelUuid = dataModelUuid;
    }

    public String getDataModelId() {
        return dataModelId;
    }

    public void setDataModelId(String dataModelId) {
        this.dataModelId = dataModelId;
    }

    public String getUniqueColumn() {
        return uniqueColumn;
    }

    public void setUniqueColumn(String uniqueColumn) {
        this.uniqueColumn = uniqueColumn;
    }

    public String getParentColumn() {
        return parentColumn;
    }

    public void setParentColumn(String parentColumn) {
        this.parentColumn = parentColumn;
    }

    public String getDisplayColumn() {
        return displayColumn;
    }

    public void setDisplayColumn(String displayColumn) {
        this.displayColumn = displayColumn;
    }

    public String getTreeRootName() {
        return treeRootName;
    }

    public void setTreeRootName(String treeRootName) {
        this.treeRootName = treeRootName;
    }

    public String getDefaultCondition() {
        return defaultCondition;
    }

    public void setDefaultCondition(String defaultCondition) {
        this.defaultCondition = defaultCondition;
    }

    public String getValueColumn() {
        return valueColumn;
    }

    public void setValueColumn(String valueColumn) {
        this.valueColumn = valueColumn;
    }

    public Set<Integer> getNoCheckLevel() {
        return noCheckLevel;
    }

    public void setNoCheckLevel(Set<Integer> noCheckLevel) {
        this.noCheckLevel = noCheckLevel;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public String getParentColumnValue() {
        return parentColumnValue;
    }

    public void setParentColumnValue(String parentColumnValue) {
        this.parentColumnValue = parentColumnValue;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * @return the params
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * @param params 要设置的params
     */
    public void setParams(Map<String, Object> params) {

        if (MapUtils.isNotEmpty(params)) {
            Map<String, Object> p = Maps.newHashMap();
            // 格式化处理参数对象
            Set<Map.Entry<String, Object>> entries = params.entrySet();
            for (Map.Entry<String, Object> ent : entries) {
                if (ent.getKey().indexOf("#DATE(") != -1) {
                    if (ent.getValue() != null) {
                        String[] parts = ent.getKey().split("#");
                        try {
                            String pattern = parts[1].substring(parts[1].indexOf("(") + 1, parts[1].lastIndexOf(")"));
                            pattern = pattern.replaceAll("D", "d");
                            p.put(parts[0], DateUtils.parseDate(ent.getValue().toString(), pattern));
                        } catch (Exception e) {
                        }
                    }
                } else if (ent.getKey().indexOf("#NUMBER") != -1) {
                    if (ent.getValue() != null) {
                        String[] parts = ent.getKey().split("#");
                        try {
                            p.put(parts[0], NumberUtils.createNumber(ent.getValue().toString()));
                        } catch (Exception e) {
                        }
                    }
                } else {
                    p.put(ent.getKey(), ent.getValue());
                }
            }
            this.params = p;
        }
    }

}
