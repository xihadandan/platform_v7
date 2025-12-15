/*
 * @(#)2016年10月27日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.viewcomponent.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreColumnBean;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.basicdata.datastore.service.DataStoreQueryService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreQueryData;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreQueryInfo;
import com.wellsoft.pt.basicdata.datastore.support.export.DataStoreExportFactory;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractCustomDataStoreRenderer;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.ViewComponentService;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月27日.1	xiem		2016年10月27日		Create
 * </pre>
 * @date 2016年10月27日
 */
@Service
@Transactional
public class ViewComponentServiceImpl extends BaseServiceImpl implements ViewComponentService {
    @Autowired
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;
    @Autowired(required = false)
    private List<DataStoreRenderer> dataStoreRenderers;
    @Autowired
    private DataStoreExportFactory dataStoreExportFactory;
    @Autowired
    private CdDataStoreService cdDataStoreService;
    @Autowired
    private ReadMarkerService readMarkerService;
    @Autowired
    private DataStoreQueryService dataStoreQueryService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        return cdDataStoreDefinitionService.loadSelectData(queryInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public Select2QueryData loadColumnsSelectData(Select2QueryInfo queryInfo) {
        return cdDataStoreDefinitionService.loadColumnsSelectData(queryInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public List<CdDataStoreColumnBean> getColumnsById(String id) {
        return cdDataStoreDefinitionService.getColumnsById(id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public List<Map<String, String>> getQueryOperators() {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (CriterionOperator op : CriterionOperator.values()) {
            if (CriterionOperator.BASE == op.getModel()) {
                Map<String, String> opMap = new HashMap<String, String>();
                opMap.put("value", op.getType());
                opMap.put("text", op.getName());
                result.add(opMap);
            }
        }
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public Select2QueryData loadRendererSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        String type = queryInfo.getOtherParams("type");
        for (DataStoreRenderer dataStoreRenderer : this.dataStoreRenderers) {
            if (StringUtils.isBlank(type) || type.equals("1")) {
                select2Data
                        .addResultData(new Select2DataBean(dataStoreRenderer.getType(), dataStoreRenderer.getName()));
            } else if (type.equals("2")) {
                if ((dataStoreRenderer instanceof AbstractDataStoreRenderer)) {
                    select2Data.addResultData(
                            new Select2DataBean(dataStoreRenderer.getType(), dataStoreRenderer.getName()));
                }
            } else if (type.equals("3")) {
                if (dataStoreRenderer instanceof AbstractCustomDataStoreRenderer) {
                    select2Data.addResultData(
                            new Select2DataBean(dataStoreRenderer.getType(), dataStoreRenderer.getName()));
                }
            }
        }
        return select2Data;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    @Transactional(readOnly = true)
    public DataStoreData loadAllData(String dataStoreId) {
        DataStoreParams params = new DataStoreParams();
        params.setDataStoreId(dataStoreId);
        return cdDataStoreService.loadData(params);
    }

    @Override
    @Transactional(readOnly = true)
    public DataStoreData loadAllData(String dataStoreId, String key, List<Condition> keyConditions,
                                     String defaultCondition, Integer pageNum, Integer pageSize) {
        DataStoreParams params = new DataStoreParams();
        params.setDataStoreId(dataStoreId);
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(pageSize);
        pagingInfo.setCurrentPage(pageNum);
        params.setPagingInfo(pagingInfo);
        Map<String, Object> paramsHash = new HashMap<>();
        paramsHash.put("keyword", key);
        params.setParams(paramsHash);
        List<Condition> criterions = params.getCriterions();
        if (StringUtils.isNotBlank(key)) {
            Condition keyCondition = new Condition();
            keyCondition.setType("or");
            keyCondition.setConditions(keyConditions);
            criterions.add(keyCondition);
        }
        if (StringUtils.isNotBlank(defaultCondition)) {
            Condition defaultConditionSql = new Condition();
            defaultConditionSql.setSql(defaultCondition);
            criterions.add(defaultConditionSql);
        }
        return cdDataStoreService.loadData(params);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public DataStoreData loadAllDataWithNewTransaction(String dataStoreId) {
        return loadAllData(dataStoreId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.viewcomponent.facade.service.ViewComponentService#loadAllSelectData(boolean, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public DataStoreData loadAllSelectData(boolean distinct, String dataStoreId, String idColumnIndex,
                                           String textColumnIndex) {
        DataStoreQueryInfo dataStoreQueryInfo = new DataStoreQueryInfo();

        dataStoreQueryInfo.setDistinct(distinct);
        dataStoreQueryInfo.setDataStoreId(dataStoreId);

        Set<String> columnIndexes = new LinkedHashSet<String>();
        columnIndexes.add(idColumnIndex);
        columnIndexes.add(textColumnIndex);
        dataStoreQueryInfo.setProjection(columnIndexes);

        dataStoreQueryInfo.addOrder(new DataStoreOrder(idColumnIndex, true));

        DataStoreQueryData queryData = dataStoreQueryService.query(dataStoreQueryInfo);
        DataStoreData dataStoreData = new DataStoreData();
        List<?> dataList = queryData.getDataList();
        for (Object data : dataList) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put(idColumnIndex, ((QueryItem) data).get(QueryItem.getKey(idColumnIndex)));
            dataMap.put(textColumnIndex, ((QueryItem) data).get(QueryItem.getKey(textColumnIndex)));
            dataStoreData.getData().add(dataMap);
        }
        return dataStoreData;
    }

    @Override
    public Select2QueryData loadExportTypeSelectData(Select2QueryInfo queryInfo) {
        return dataStoreExportFactory.getSelectData(queryInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.viewcomponent.facade.service.ViewComponentService#filterUnread(java.util.List)
     */
    @Override
    public List<String> filterUnread(List<String> uuids) {
        return readMarkerService.getUnReadList(uuids, SpringSecurityUtils.getCurrentUserId());
    }

}
