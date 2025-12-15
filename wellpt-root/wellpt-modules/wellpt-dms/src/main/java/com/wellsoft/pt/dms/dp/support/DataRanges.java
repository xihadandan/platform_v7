/*
 * @(#)2019年10月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年10月13日.1	zhulh		2019年10月13日		Create
 * </pre>
 * @date 2019年10月13日
 */
public class DataRanges extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2288438542161904818L;

    // 包含的数据范围
    private List<DataRange> dataRanges = Lists.newArrayList();

    /**
     * @return the dataRanges
     */
    public List<DataRange> getDataRanges() {
        return dataRanges;
    }

    /**
     * @param dataRanges 要设置的dataRanges
     */
    public void setDataRanges(List<DataRange> dataRanges) {
        this.dataRanges = dataRanges;
    }

    /**
     * 是否需要去重
     *
     * @return
     */
    public boolean isRequiredDistinct() {
        Set<String> ownerFieldNameSet = Sets.newLinkedHashSet();
        Set<String> ownerIdSet = Sets.newLinkedHashSet();
        int ownerIdSize = 0;
        boolean hasDataRuleType = false;
        for (DataRange dataRange : dataRanges) {
            ownerFieldNameSet.add(dataRange.getOwnerFieldName());
            ownerIdSet.addAll(dataRange.getOwnerIds());
            ownerIdSize += CollectionUtils.size(dataRange.getOwnerIds());
            if (StringUtils.equals(DataRangeDefinition.DATA_RULE_TYPE, dataRange.getType())) {
                hasDataRuleType = true;
            }
        }
        // 1、所有都字段不一样，需要去重
        if (CollectionUtils.size(ownerFieldNameSet) > 1) {
            return true;
        }
        // 2、所有者值有重复，需要去重
        if (ownerIdSize > CollectionUtils.size(ownerIdSet)) {
            return true;
        }
        // 3、包含数据规则的过滤需要去重
        if (hasDataRuleType && CollectionUtils.size(dataRanges) > 1) {
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public String toSqlString(QueryContext queryContext) {
        if (CollectionUtils.size(dataRanges) > 1) {
            return toMultiDataRangeSqlString(queryContext);
        } else {
            return toSingleDataRangeSqlString(queryContext);
        }
    }

    /**
     * 多个单个数据范围处理
     *
     * @param queryContext
     * @return
     */
    private String toMultiDataRangeSqlString(QueryContext queryContext) {
        StringBuilder sb = new StringBuilder();
        Iterator<DataRange> it = dataRanges.iterator();
        DataRange dataRange = dataRanges.get(0);
        sb.append("select * from (");
        sb.append(dataRange.getSelectSql(queryContext)).append(" _t ");
        sb.append("where exists (");
        sb.append("select uuid from (");
        while (it.hasNext()) {
            DataRange range = it.next();
            sb.append("select uuid from ");
            sb.append(range.getDataName());
            sb.append(Separator.SPACE.getValue());
            sb.append(range.getWhereSql(queryContext));
            if (it.hasNext()) {
                sb.append(" union all ");
            }
        }
        sb.append(")");
        sb.append(" _d where _t.uuid = _d.uuid)");
        sb.append(")");
        return sb.toString();
    }

    /**
     * 单个数据范围处理
     *
     * @return
     */
    private String toSingleDataRangeSqlString(QueryContext queryContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("select").append(Separator.SPACE.getValue())
                .append(isRequiredDistinct() ? "distinct" : StringUtils.EMPTY).append(Separator.SPACE.getValue())
                .append(Separator.ASTERISK.getValue()).append(Separator.SPACE.getValue()).append("from").append("(");
        Iterator<DataRange> it = dataRanges.iterator();
        while (it.hasNext()) {
            sb.append("(");
            sb.append(it.next().toSqlString(queryContext));
            sb.append(")");
            if (it.hasNext()) {
                sb.append(" union all ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
