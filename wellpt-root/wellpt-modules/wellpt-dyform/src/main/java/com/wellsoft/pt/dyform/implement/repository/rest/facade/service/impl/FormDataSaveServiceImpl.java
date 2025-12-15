/*
 * @(#)2019年8月30日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.repository.FormData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfoBuilder;
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.FormDataSaveRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormDataSaveResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
 * 2019年8月30日.1	zhulh		2019年8月30日		Create
 * </pre>
 * @date 2019年8月30日
 */
@Service(RepositoryFormDataApiServiceNames.FORM_DATA_SAVE)
public class FormDataSaveServiceImpl implements WellptService<FormDataSaveRequest> {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FormDataService formDataService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FormDataSaveRequest request) {
        // 表单数据
        FormData formData = request.getData();
        String mainformUuid = formData.getFormUuid();
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDatas = getFormDatas(formData);
        Map<String, List<String>> deletedFormDatas = getDeletedFormDatas(formData);
        Map<String/* 表单定义id */, Map<String /* 数据记录uuid */, Set<String> /* 字段值 */>> updatedFormDatas = getUpdatedFormDatas(formData);
        // 被新增的数据
        Map<String/* 表单定义id */, List<String>/* 表单数据id */> addedFormDatas = getAddedFormDatas(formData);
        // 数据已存在，不可新增，直接更新
        if (formData.isNew() && MapUtils.isNotEmpty(addedFormDatas)) {
            FormDataQueryInfoBuilder queryInfoBuilder = new FormDataQueryInfoBuilder(formData.getFormId());
            queryInfoBuilder.condition("uuid", formData.getDataUuid(), CriterionOperator.eq);
            long count = formDataService.count(queryInfoBuilder.build());
            if (count > 0) {
                addedFormDatas.clear();
            }
        }
        String dataUuid = formDataService.saveFormData(mainformUuid, formDatas, deletedFormDatas, updatedFormDatas,
                addedFormDatas, null, new DyformDataOptions());
        FormDataSaveResponse response = new FormDataSaveResponse();
        response.setDataUuid(dataUuid);
        return response;
    }

    /**
     * @param formData
     * @return
     */
    private Map<String, List<Map<String, Object>>> getFormDatas(FormData formData) {
        Map<String, List<Map<String, Object>>> formDatas = Maps.newHashMap();
        addFormData(formData, formDatas);
        return formDatas;
    }

    /**
     * @param formData
     * @param formDatas
     */
    private void addFormData(FormData formData, Map<String, List<Map<String, Object>>> formDatas) {
        // 主表数据
        String formUuid = formData.getFormUuid();
        Map<String, Object> dataMap = formData.getDataMap();
        if (!formDatas.containsKey(formUuid)) {
            List<Map<String, Object>> listMap = Lists.newArrayList();
            formDatas.put(formUuid, listMap);
        }
        formDatas.get(formUuid).add(dataMap);

        // 从表数据
        List<FormData> subformDatas = formData.getSubformDatas();
        if (CollectionUtils.isNotEmpty(subformDatas)) {
            for (FormData subformData : subformDatas) {
                addFormData(subformData, formDatas);
            }
        }
    }

    /**
     * @param formData
     * @return
     */
    private Map<String, List<String>> getDeletedFormDatas(FormData formData) {
        Map<String, List<String>> deletedFormDatas = Maps.newHashMap();
        addDeletedFormData(formData, deletedFormDatas);
        return deletedFormDatas;
    }

    /**
     * @param formData
     * @param deletedFormDatas
     */
    private void addDeletedFormData(FormData formData, Map<String, List<String>> deletedFormDatas) {
        // 主表数据
        if (formData.isDelete()) {
            String formUuid = formData.getFormUuid();
            if (!deletedFormDatas.containsKey(formUuid)) {
                List<String> deletedDataUuids = Lists.newArrayList();
                deletedFormDatas.put(formUuid, deletedDataUuids);
            }
            deletedFormDatas.get(formUuid).add(formData.getDataUuid());
        }

        // 从表数据
        List<FormData> subformDatas = formData.getSubformDatas();
        if (CollectionUtils.isNotEmpty(subformDatas)) {
            for (FormData subformData : subformDatas) {
                addDeletedFormData(subformData, deletedFormDatas);
            }
        }
    }

    /**
     * @param formData
     * @return
     */
    private Map<String, Map<String, Set<String>>> getUpdatedFormDatas(FormData formData) {
        Map<String, Map<String, Set<String>>> updatedFormDatas = Maps.newHashMap();
        addUpdatedFormData(formData, updatedFormDatas);
        return updatedFormDatas;
    }

    /**
     * @param formData
     * @param updatedFormDatas
     */
    private void addUpdatedFormData(FormData formData, Map<String, Map<String, Set<String>>> updatedFormDatas) {
        // 主表数据
        if (formData.isUpdate()) {
            String formUuid = formData.getFormUuid();
            String dataUuid = formData.getDataUuid();
            if (!updatedFormDatas.containsKey(formUuid)) {
                Map<String, Set<String>> updatedDataFields = Maps.newHashMap();
                updatedFormDatas.put(formUuid, updatedDataFields);
            }
            Map<String, Set<String>> updatedDataFields = updatedFormDatas.get(formUuid);
            if (!updatedDataFields.containsKey(dataUuid)) {
                Set<String> updatedFieldSet = Sets.newLinkedHashSet();
                updatedDataFields.put(dataUuid, updatedFieldSet);
            }
            updatedDataFields.get(dataUuid).addAll(formData.getUpdatedFields());
        }

        // 从表数据
        List<FormData> subformDatas = formData.getSubformDatas();
        if (CollectionUtils.isNotEmpty(subformDatas)) {
            for (FormData subformData : subformDatas) {
                addUpdatedFormData(subformData, updatedFormDatas);
            }
        }
    }

    /**
     * @param formData
     * @return
     */
    private Map<String, List<String>> getAddedFormDatas(FormData formData) {
        Map<String, List<String>> addedFormDatas = Maps.newHashMap();
        addAddedFormData(formData, addedFormDatas);
        return addedFormDatas;
    }

    /**
     * @param formData
     * @param addedFormDatas
     */
    private void addAddedFormData(FormData formData, Map<String, List<String>> addedFormDatas) {
        // 主表数据
        if (formData.isNew()) {
            String formUuid = formData.getFormUuid();
            if (!addedFormDatas.containsKey(formUuid)) {
                List<String> addedDataUuids = Lists.newArrayList();
                addedFormDatas.put(formUuid, addedDataUuids);
            }
            addedFormDatas.get(formUuid).add(formData.getDataUuid());
        }

        // 从表数据
        List<FormData> subformDatas = formData.getSubformDatas();
        if (CollectionUtils.isNotEmpty(subformDatas)) {
            for (FormData subformData : subformDatas) {
                addAddedFormData(subformData, addedFormDatas);
            }
        }
    }

}
