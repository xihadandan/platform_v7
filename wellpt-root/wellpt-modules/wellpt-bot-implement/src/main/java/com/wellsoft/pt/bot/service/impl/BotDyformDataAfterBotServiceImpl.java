/*
 * @(#)8/22/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.pt.bot.service.BotDyformDataAfterBotService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bot.support.DyFormBoter;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 表单从表数据单据转换复制、同步服务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/22/24.1	    zhulh		8/22/24		    Create
 * </pre>
 * @date 8/22/24
 */
@Service
public class BotDyformDataAfterBotServiceImpl implements BotDyformDataAfterBotService {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Override
    public void copySubformData(String subformId, DyFormBoter dyFormBoter, BotResult result) {
        BotParam botParam = dyFormBoter.getBotParam();
        Set<BotParam.BotFromParam> fromParams = botParam.getFroms();
        if (CollectionUtils.isEmpty(fromParams)) {
            return;
        }

        DyFormData targetDyformData = (DyFormData) result.getDyformData();
        if (targetDyformData == null) {
            return;
        }

        fromParams.forEach(fromParam -> {
            DyFormData dyFormData = (DyFormData) fromParam.getFromObjData();
            if (dyFormData == null) {
                dyFormData = dyFormFacade.getDyFormData(fromParam.getFromObjId(), fromParam.getFromUuid());
            }
            if (StringUtils.equals(dyFormData.getFormUuid(), targetDyformData.getFormUuid())
                    && StringUtils.equals(dyFormData.getDataUuid(), targetDyformData.getDataUuid())) {
                return;
            }

            List<Map<String, Object>> subformDatas = dyFormData.getFormDatasById(subformId);
            if (CollectionUtils.isEmpty(subformDatas)) {
                return;
            }

            copySubformData(subformId, subformDatas, targetDyformData);
        });
    }

    /**
     * @param subformId
     * @param subformDatas
     * @param targetDyformData
     */
    private void copySubformData(String subformId, List<Map<String, Object>> subformDatas, DyFormData targetDyformData) {
        String subformUuid = dyFormFacade.getFormUuidById(subformId);
        List<String> basicFields = Lists.newArrayList(Entity.BASE_FIELDS);
        // 附加从表数据
        for (Map<String, Object> subFormData : subformDatas) {
            DyFormData copySubFormData = dyFormFacade.createDyformData(subformUuid);
            for (Map.Entry<String, Object> entry : subFormData.entrySet()) {
                if (basicFields.contains(entry.getKey())) {
                    continue;
                }
                copySubFormData.setFieldValue(entry.getKey(), entry.getValue());
            }
            targetDyformData.addSubformData(copySubFormData);
        }
        dyFormFacade.saveFormData(targetDyformData);
    }

    @Override
    public void syncSubformData(String subformId, String rowKeyField, DyFormBoter dyFormBoter, BotResult result) {
        BotParam botParam = dyFormBoter.getBotParam();
        Set<BotParam.BotFromParam> fromParams = botParam.getFroms();
        if (CollectionUtils.isEmpty(fromParams)) {
            return;
        }

        DyFormData targetDyformData = (DyFormData) result.getDyformData();
        if (targetDyformData == null) {
            return;
        }

        fromParams.forEach(fromParam -> {
            DyFormData dyFormData = (DyFormData) fromParam.getFromObjData();
            if (dyFormData == null) {
                dyFormData = dyFormFacade.getDyFormData(fromParam.getFromObjId(), fromParam.getFromUuid());
            }
            if (StringUtils.equals(dyFormData.getFormUuid(), targetDyformData.getFormUuid())
                    && StringUtils.equals(dyFormData.getDataUuid(), targetDyformData.getDataUuid())) {
                return;
            }

            List<Map<String, Object>> subformDatas = dyFormData.getFormDatasById(subformId);
            if (CollectionUtils.isEmpty(subformDatas)) {
                return;
            }

            syncSubformData(subformId, rowKeyField, subformDatas, targetDyformData);
        });
    }

    /**
     * @param subformId
     * @param subformDatas
     * @param targetDyformData
     */
    private void syncSubformData(String subformId, String rowKeyField, List<Map<String, Object>> subformDatas, DyFormData targetDyformData) {
        String subformUuid = dyFormFacade.getFormUuidById(subformId);
        List<String> basicFields = Lists.newArrayList(Entity.BASE_FIELDS);
        // 附加从表数据
        for (Map<String, Object> subFormData : subformDatas) {
            Object rowKey = subFormData.get(rowKeyField);
            DyFormData copySubFormData = getRowData(rowKey, rowKeyField, subformId, targetDyformData);
            boolean newRowData = false;
            if (copySubFormData == null) {
                newRowData = true;
                copySubFormData = dyFormFacade.createDyformData(subformUuid);
            }
            for (Map.Entry<String, Object> entry : subFormData.entrySet()) {
                if (basicFields.contains(entry.getKey())) {
                    continue;
                }
                copySubFormData.setFieldValue(entry.getKey(), entry.getValue());
            }
            if (newRowData) {
                targetDyformData.addSubformData(copySubFormData);
            }
        }
        dyFormFacade.saveFormData(targetDyformData);
    }

    /**
     * @param rowKey
     * @param rowKeyField
     * @param subformId
     * @param targetDyformData
     * @return
     */
    private DyFormData getRowData(Object rowKey, String rowKeyField, String subformId, DyFormData targetDyformData) {
        if (rowKey == null || StringUtils.isBlank(rowKeyField)) {
            return null;
        }
        List<DyFormData> dyFormDatas = targetDyformData.getDyformDatasByFormId(subformId);
        if (CollectionUtils.isEmpty(dyFormDatas)) {
            return null;
        }
        return dyFormDatas.stream().filter(dyFormData -> rowKey.equals(dyFormData.getFieldValue(rowKeyField))).findFirst().orElse(null);
    }

}
