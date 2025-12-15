/*
 * @(#)10/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.dao.WfTaskTodoTempDao;
import com.wellsoft.pt.bpm.engine.entity.WfTaskTodoTempEntity;
import com.wellsoft.pt.bpm.engine.service.WfTaskTodoTempService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/9/25.1	    zhulh		10/9/25		    Create
 * </pre>
 * @date 10/9/25
 */
@Service
public class WfTaskTodoTempServiceImpl extends AbstractJpaServiceImpl<WfTaskTodoTempEntity, WfTaskTodoTempDao, Long>
        implements WfTaskTodoTempService {

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Override
    public WfTaskTodoTempEntity getByTaskInstUuidAndUserId(String taskInstUuid, String userId) {
        WfTaskTodoTempEntity entity = new WfTaskTodoTempEntity();
        entity.setTaskInstUuid(taskInstUuid);
        entity.setUserId(userId);
        List<WfTaskTodoTempEntity> list = this.dao.listByEntity(entity);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<WfTaskTodoTempEntity> listByTaskInstUuid(String taskInstUuid) {
        WfTaskTodoTempEntity entity = new WfTaskTodoTempEntity();
        entity.setTaskInstUuid(taskInstUuid);
        return this.dao.listByEntity(entity);
    }

    @Override
    @Transactional
    public void deleteByFlowInstUuid(String flowInstUuid) {
        if (StringUtils.isBlank(flowInstUuid)) {
            return;
        }

        List<WfTaskTodoTempEntity> entities = this.dao.listByFieldEqValue("flowInstUuid", flowInstUuid);
        if (CollectionUtils.isNotEmpty(entities)) {
            deleteByEntities(entities);
            entities.forEach(entity -> {
                if (StringUtils.isNotBlank(entity.getOpinionFileIds())) {
                    mongoFileService.popAllFilesFromFolder(entity.getUuid().toString());
                }
            });
        }
    }

    @Override
    public List<WfTaskTodoTempEntity> listByTaskInstUuidAndTaskInstRecVer(String taskInstUuid, Integer taskInstRecVer) {
        WfTaskTodoTempEntity entity = new WfTaskTodoTempEntity();
        entity.setTaskInstUuid(taskInstUuid);
        entity.setTaskInstRecVer(taskInstRecVer);
        return this.dao.listByEntity(entity);
    }

    @Override
    @Transactional
    public void updateTaskTodoTemp(List<WfTaskTodoTempEntity> taskTodoTempEntities, Integer taskInstRecVer) {
        Map<String, DyFormData> dyFormDataMap = Maps.newHashMap();
        taskTodoTempEntities.forEach(entity -> {
            DyFormData dyFormData = dyFormDataMap.get(entity.getFormUuid() + entity.getDataUuid());
            if (dyFormData == null) {
                dyFormData = dyFormFacade.getDyFormData(entity.getFormUuid(), entity.getDataUuid());
                dyFormDataMap.put(entity.getFormUuid() + entity.getDataUuid(), dyFormData);
            }
            Map<String, Object> formData = JsonUtils.json2Object(entity.getFormData(), Map.class);
            formData.put("init", dyFormData.getFormDatas());
            entity.setTaskInstRecVer(taskInstRecVer);
            entity.setFormData(JsonUtils.object2Json(formData));
        });
        this.dao.saveAll(taskTodoTempEntities);
    }

}
