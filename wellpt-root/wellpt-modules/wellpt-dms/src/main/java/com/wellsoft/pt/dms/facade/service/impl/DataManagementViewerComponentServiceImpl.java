/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.dms.core.preview.DataPreviewTemplate;
import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.core.view.DataView;
import com.wellsoft.pt.dms.core.web.interceptor.ActionHandlerInterceptor;
import com.wellsoft.pt.dms.facade.service.DataManagementViewerComponentService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 15, 2017.1	zhulh		Feb 15, 2017		Create
 * </pre>
 * @date Feb 15, 2017
 */
@Service
public class DataManagementViewerComponentServiceImpl extends BaseServiceImpl implements
        DataManagementViewerComponentService {

    @Autowired(required = false)
    private Map<String, ActionHandlerInterceptor> interceptorMap = new HashMap<String, ActionHandlerInterceptor>();

    @Autowired(required = false)
    private List<DataView> dataViews;

    @Autowired(required = false)
    private List<DataPreviewTemplate> dataPreviewTemplates;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DataManagementViewerComponentService#getInterceptorSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getInterceptorSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        for (Entry<String, ActionHandlerInterceptor> entry : interceptorMap.entrySet()) {
            select2Data.addResultData(new Select2DataBean(entry.getKey(), entry.getValue().getName()));
        }
        return select2Data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DataManagementViewerComponentService#getDataTypeSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getDataTypeSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        // 暂时只支持表单配置
        // for (DataType dataType : DataType.values()) {
        // select2Data.addResultData(new Select2DataBean(dataType.getId(),
        // dataType.getName()));
        // }
        select2Data.addResultData(new Select2DataBean(DataType.DYFORM.getId(), DataType.DYFORM.getName()));
        return select2Data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DataManagementViewerComponentService#getDataTypeOfDyFormSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    @Transactional(readOnly = true)
    public Select2QueryData getDataTypeOfDyFormSelectData(Select2QueryInfo queryInfo) {
        List<FormDefinition> dyFormDefinitions = Lists.newArrayList();
        Select2QueryData select2Data = new Select2QueryData();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tableName", queryInfo.getSearchValue());
        values.put("name", queryInfo.getSearchValue());
        values.put("id", queryInfo.getSearchValue());
        String formType = queryInfo.getOtherParams("formType");
        values.put("formType", formType);
        String pformUuid = queryInfo.getOtherParams("pformUuid");
        if (StringUtils.isBlank(pformUuid)) {
            String currrentUserUnitId = SpringSecurityUtils.getCurrentUserUnitId();
            if (!MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(currrentUserUnitId)) {//查询当前单位的表单数据
                String piUuid = queryInfo.getOtherParams("piUuid");
                if (StringUtils.isNotBlank(piUuid)) {
                    //限定表单属于的模块范围
                    values.put("piUuid", piUuid);
                }
                //values.put("systemUnitId",currrentUserUnitId);
                dyFormDefinitions.addAll(this.dao.namedQuery("getDyFormDefinitionIncludeRefDyFormByPiUuid", values,
                        FormDefinition.class));

            }
            values.remove("piUuid");//平台级的表单暂不考虑限制归属模块
            values.put("systemUnitId", MultiOrgSystemUnit.PT_ID);
            dyFormDefinitions.addAll(this.dao.namedQuery("getDyFormDefinitionIncludeRefDyFormByPiUuid", values,
                    FormDefinition.class));

            for (DyFormFormDefinition dyFormDefinition : dyFormDefinitions) {
                String text = dyFormDefinition.getName() + "(" + dyFormDefinition.getVersion() + ")";
                select2Data.addResultData(new Select2DataBean(dyFormDefinition.getUuid(), text));
            }
        } else if (StringUtils.equals("M", formType)) {
            // 手机单据
            List<FormDefinition> dyFormFormDefinitions = dyFormApiFacade.getMformDefinitionsByPformId(pformUuid);
            for (FormDefinition formDefinition : dyFormFormDefinitions) {
                String text = formDefinition.getName() + "(" + formDefinition.getVersion() + ")";
                select2Data.addResultData(new Select2DataBean(formDefinition.getUuid(), text));
            }
        } else {
            // 显示表单
            List<FormDefinition> dyFormFormDefinitions = dyFormApiFacade.getVformDefinitionsByPformId(pformUuid);
            for (FormDefinition formDefinition : dyFormFormDefinitions) {
                String text = formDefinition.getName() + "(" + formDefinition.getVersion() + ")";
                select2Data.addResultData(new Select2DataBean(formDefinition.getUuid(), text));
            }
        }

        return select2Data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DataManagementViewerComponentService#getDataTypeOfDyFormSelectDataByIds(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    @Transactional(readOnly = true)
    public Select2QueryData getDataTypeOfDyFormSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] formUuids = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuids", formUuids);
        if (formUuids.length > 0) {
            values.put("uuidString", formUuids.toString());
        }
        List<FormDefinition> list = this.dao.namedQuery("getDyFormDefinitionBasicInfo", values, FormDefinition.class,
                select2QueryInfo.getPagingInfo());
        for (DyFormFormDefinition dyFormDefinition : list) {
            String text = dyFormDefinition.getName() + "(" + dyFormDefinition.getVersion() + ")";
            dyFormDefinition.setName(text);
        }
        return new Select2QueryData(list, "uuid", "name", select2QueryInfo.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DataManagementViewerComponentService#getColumnsOfDyFormSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getColumnsOfDyFormSelectData(Select2QueryInfo select2QueryInfo) {
        String formUuid = select2QueryInfo.getOtherParams("formUuid");
        if (StringUtils.isBlank(formUuid)) {
            return new Select2QueryData(Collections.emptyList(), "id", "text", select2QueryInfo.getPagingInfo());
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            List<DyformFieldDefinition> fieldDefinitions = dyFormApiFacade.getFieldDefinitions(formUuid);
            for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
                String fieldName = fieldDefinition.getFieldName();
                String displayName = fieldDefinition.getDisplayName();
                Map<String, String> value = new HashMap<String, String>();
                value.put("id", fieldName);
                value.put("text", displayName);
                list.add(value);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new Select2QueryData(list, "id", "text", select2QueryInfo.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DataManagementViewerComponentService#getDataViewSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getDataViewSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        for (DataView dataView : this.dataViews) {
            select2Data.addResultData(new Select2DataBean(dataView.getType(), dataView.getName()));
        }
        return select2Data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DataManagementViewerComponentService#getDataPreviewTemplateSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData getDataPreviewTemplateSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData select2Data = new Select2QueryData();
        for (DataPreviewTemplate dataPreviewTemplate : this.dataPreviewTemplates) {
            select2Data
                    .addResultData(new Select2DataBean(dataPreviewTemplate.getType(), dataPreviewTemplate.getName()));
        }
        return select2Data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.component.select2.Select2QueryApi#loadSelectData(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.component.select2.Select2QueryApi#loadSelectDataByIds(com.wellsoft.pt.common.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        return null;
    }

}
