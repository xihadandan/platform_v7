/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.core.preview.DataPreviewTemplate;
import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.core.view.DataView;
import com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptor;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookUnitEntity;
import com.wellsoft.pt.dms.facade.service.DocExchangerComponentService;
import com.wellsoft.pt.dms.service.DmsDocExcContactBookUnitService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档交换器组件服务
 *
 * @author chenq
 */
@Service
public class DocExchangerComponentServiceImpl extends BaseServiceImpl implements
        DocExchangerComponentService {

    @Autowired(required = false)
    private Map<String, ActionHandlerInterceptor> interceptorMap = new HashMap<String, ActionHandlerInterceptor>();

    @Autowired(required = false)
    private List<DataView> dataViews;

    @Autowired(required = false)
    private List<DataPreviewTemplate> dataPreviewTemplates;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private FlowDefineService flowDefineService;

    @Autowired
    private DmsDocExcContactBookUnitService dmsDocExcContactBookUnitService;


    @Override
    public Select2QueryData getDataViewSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        for (DataView dataView : this.dataViews) {
            if (dataView.getType().equalsIgnoreCase("bootstrapTableDataView")) {
                select2Data.addResultData(
                        new Select2DataBean(dataView.getType(), dataView.getName()));
            }
        }
        return select2Data;
    }


    @Override
    public Select2QueryData getDataTypeSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        select2Data.addResultData(
                new Select2DataBean(DataType.DYFORM.getId(), DataType.DYFORM.getName()));
        select2Data.addResultData(new Select2DataBean("FILE", "文件"));
        return select2Data;
    }

    @Override
    public Select2QueryData getWorkFlowSelectData(Select2QueryInfo queryInfo) {
        QueryInfo param = new QueryInfo();
        List<QueryItem> queryItemList = flowDefineService.query(param);
        Select2QueryData data = new Select2QueryData();
        for (QueryItem item : queryItemList) {
            data.addResultData(new Select2DataBean(item.getString("uuid"), item.getString("name")));
        }
        return data;
    }


    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        return null;
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        return null;
    }


    @Override
    public Select2QueryData getContactBookUnitSelectData(Select2QueryInfo queryInfo) {
        String moduleId = queryInfo.getRequest().getParameter("moduleId");
        /**
         * 产品说明：有通讯录管理权限的人都能看到所有单位
         */
//        List<DmsDocExcContactBookUnitEntity> contactBookUnitEntities = dmsDocExcContactBookUnitService.listByUserIdAndModule(
//                SpringSecurityUtils.getCurrentUserId(), moduleId);
        List<DmsDocExcContactBookUnitEntity> contactBookUnitEntities = dmsDocExcContactBookUnitService.listBySysUnitIdAndModule(
                SpringSecurityUtils.getCurrentUserUnitId(), moduleId);
        Select2QueryData select2Data = new Select2QueryData();
        for (DmsDocExcContactBookUnitEntity unitEntity : contactBookUnitEntities) {
            select2Data.addResultData(
                    new Select2DataBean(unitEntity.getUuid(), unitEntity.getUnitName()));
        }
        return select2Data;
    }

}
