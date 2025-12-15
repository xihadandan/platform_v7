/*
 * @(#)2017年5月2日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.component.select2.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datastore.service.DataStoreQueryService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreQueryData;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreQueryInfo;
import com.wellsoft.pt.common.component.select2.facade.service.Select2DataStoreQueryService;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月2日.1	zhulh		2017年5月2日		Create
 * </pre>
 * @date 2017年5月2日
 */
@Service
@Transactional(readOnly = true)
public class Select2DataStoreQueryServiceImpl extends BaseServiceImpl implements Select2DataStoreQueryService {

    @Autowired
    private DataStoreQueryService dataStoreQueryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.component.select2.Select2QueryApi#loadSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String searchValue = queryInfo.getSearchValue();
        String dataStoreId = queryInfo.getOtherParams("dataStoreId");
        String idColumnIndex = queryInfo.getOtherParams("idColumnIndex");
        String textColumnIndex = queryInfo.getOtherParams("textColumnIndex");
        DataStoreQueryInfo dataStoreQueryInfo = new DataStoreQueryInfo();

        dataStoreQueryInfo.setDistinct(true);
        dataStoreQueryInfo.setDataStoreId(dataStoreId);

        Set<String> columnIndexes = new LinkedHashSet<String>();
        columnIndexes.add(idColumnIndex);
        columnIndexes.add(textColumnIndex);
        dataStoreQueryInfo.setProjection(columnIndexes);

        if (StringUtils.isNotBlank(searchValue)) {
            List<Condition> conditions = new ArrayList<Condition>();
            conditions.add(new Condition(idColumnIndex, searchValue, CriterionOperator.like.getType()));
            conditions.add(new Condition(textColumnIndex, searchValue, CriterionOperator.like.getType()));
            Condition select2Condition = new Condition();
            select2Condition.setConditions(conditions);
            select2Condition.setType(CriterionOperator.or.getType());
            dataStoreQueryInfo.getConditions().add(select2Condition);
            dataStoreQueryInfo.getQueryParams().put("keyword", searchValue);
        }

        // dataStoreQueryInfo.addOrder(new DataStoreOrder(idColumnIndex, true));
        dataStoreQueryInfo.setPagingInfo(queryInfo.getPagingInfo());

        DataStoreQueryData data = dataStoreQueryService.query(dataStoreQueryInfo);
        Select2QueryData returnData = new Select2QueryData(data.getDataList(), idColumnIndex, textColumnIndex,
                data.getPagingInfo());
        return returnData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.component.select2.Select2QueryApi#loadSelectDataByIds(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        String[] ids = queryInfo.getIds();
        if (ids == null || ids.length == 0) {
            return new Select2QueryData();
        }
        String dataStoreId = queryInfo.getOtherParams("dataStoreId");
        String idColumnIndex = queryInfo.getOtherParams("idColumnIndex");
        String textColumnIndex = queryInfo.getOtherParams("textColumnIndex");

        DataStoreQueryInfo dataStoreQueryInfo = new DataStoreQueryInfo();
        dataStoreQueryInfo.setDataStoreId(dataStoreId);

        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(idColumnIndex, StringUtils.join(ids, Separator.SEMICOLON.getValue()),
                CriterionOperator.in));
        dataStoreQueryInfo.getConditions().addAll(conditions);

        dataStoreQueryInfo.setPagingInfo(queryInfo.getPagingInfo());

        DataStoreQueryData data = dataStoreQueryService.query(dataStoreQueryInfo);
        Select2QueryData returnData = new Select2QueryData(data.getDataList(), idColumnIndex, textColumnIndex,
                data.getPagingInfo());
        return returnData;
    }

    @Override
    public Select2QueryData loadEntityData(Select2QueryInfo queryInfo) throws Exception {
        Map<String, Class<?>> entityClasses = ClassUtils.getEntityClasses();
        Select2QueryData result = new Select2QueryData();
        Iterator iterator = entityClasses.keySet().iterator();

        while (iterator.hasNext()) {
            String simpleName = (String) iterator.next();
            Class clazz = entityClasses.get(simpleName);

            try {
                if (Class.forName(queryInfo.getRequest().getParameter("superEntityClass").toString()).isAssignableFrom(
                        clazz)) {

                    result.addResultData(new Select2DataBean(((Class) entityClasses.get(simpleName)).getName(),
                            StringUtils.capitalize(simpleName)));
                }
            } catch (Exception e) {
                logger.error("获取实体异常：", e);
                throw new RuntimeException(e);

            }

        }

        return result;
    }

}
