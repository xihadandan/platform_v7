/*
 * @(#)2020年1月3日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年4月29日.1	zhongzh		2020年4月29日		Create
 * </pre>
 * @date 2020年4月29日
 */
@Component
public class CustomFormDefinitionDataStoreQuery extends FormDefinitionDataStoreQuery {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FormDefinitionService formDefinitionService;

    @Override
    public String getQueryName() {
        return "公共资源_产品表单管理";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = super.initCriteriaMetadata(context);
        metadata.add("cFormUuid", "c_form_uuid", "自定义单据UUID", String.class);
        metadata.add("updateFieldNames", "update_field_names", "产品更新字段", String.class);
        metadata.add("systemUnitId", "system_unit_id", "单位ID", String.class);
        return metadata;
    }

    @Override
    public long count(QueryContext context) {
        query2(context, true);
        return context.getPagingInfo().getTotalCount();
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        return query2(context, false);
    }

    /**
     * @param context
     * @param count
     * @return
     */
    private List<QueryItem> query2(QueryContext context, boolean count) {
        // 设置查询字段条件
        Map<String, Object> params = context.getQueryParams();
        if (null == params.get("orderBy")) {
            params.put("orderBy", " order by code asc");
        }
        if (StringUtils.isBlank(context.getWhereSqlString())) {
            params.put("formTypes", Lists.newArrayList("P"));
        } else {
            params.put("whereSql", context.getWhereSqlString());
        }
        NativeDao nativeDao = context.getNativeDao();
        List<QueryItem> results = nativeDao.namedQuery("dyFormDefinitionStore2", params, QueryItem.class,
                context.getPagingInfo());
        if (false == count && false == CollectionUtils.isEmpty(results)) {
            for (QueryItem item : results) {
                String formUuid = item.getString(QueryItem.getKey("uuid"));
                String formType = item.getString(QueryItem.getKey("formType"));
                if (DyformTypeEnum.isPform(formType)) {
                    String cFormUuid = formDefinitionService.getMaxExtFormUuidByFormUuid(formUuid, "C");
                    if (StringUtils.isNotBlank(cFormUuid)) {
                        List<String> fieldNames = formDefinitionService.getDiffFieldNamesByExtFormUuid(cFormUuid, true);
                        item.put("cFormUuid", cFormUuid);
                        item.put("updateFieldNames", StringUtils.join(fieldNames, ","));
                    }
                }
            }
        }
        return results;
    }
}
