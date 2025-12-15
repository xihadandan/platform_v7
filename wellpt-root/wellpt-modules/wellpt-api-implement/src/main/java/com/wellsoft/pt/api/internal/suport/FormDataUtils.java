/*
 * @(#)2015年7月22日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.internal.suport;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年7月22日.1	zhulh		2015年7月22日		Create
 * </pre>
 * @date 2015年7月22日
 */
public class FormDataUtils {

    private static Logger logger = LoggerFactory.getLogger(FormDataUtils.class);

    /**
     * @param formData
     * @param formUuid
     * @param dataUuid
     * @return
     */
    public static DyFormData merge2DyformData(Map<String, Object> formData, String formUuid, String dataUuid) {
        DyFormFacade dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
        DyFormData dyFormData = null;
        if (StringUtils.isBlank(dataUuid)) {
            dyFormData = dyFormApiFacade.createDyformData(formUuid);
        } else {
            dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        }
        if (formData != null) {
            List<DyformSubformFormDefinition> subformDefinitions = dyFormApiFacade.getSubformDefinitions(formUuid);
            Map<String, DyformSubformFormDefinition> subformDefinitionMap = new HashMap<String, DyformSubformFormDefinition>();
            for (DyformSubformFormDefinition subformDefinition : subformDefinitions) {
                String subformId = subformDefinition.getOuterId();
                subformDefinitionMap.put(subformId, subformDefinition);
            }
            for (String key : formData.keySet()) {
                if (!subformDefinitionMap.containsKey(key)) {
                    // 主表字段

                    // inputmode ==6 为附件
                    /**UPDATE BY LINZ DATE:2015年6月26日 16:22:19**/
                    if (dyFormData.isFileField(key) && formData.get(key) != null && String.class.isAssignableFrom(formData.get(key).getClass())) {
                        String fileId = (String) formData.get(key);
                        if (StringUtils.isNotBlank(fileId)) {
                            logger.info("[fileId] " + fileId);
                            String[] fileIds = fileId.split(";");
                            List<LogicFileInfo> logicFileInfos = new ArrayList<LogicFileInfo>();
                            for (String id : fileIds) {
                                MongoFileEntity mongoFileEntity = mongoFileService.getFile(id);
                                if (mongoFileEntity != null) {
                                    List<LogicFileInfo> infos = mongoFileService
                                            .getFilesByPhysicalFileId(mongoFileEntity.getPhysicalID());
                                    if (infos.size() > 0) {
                                        for (LogicFileInfo info : infos) {
                                            logicFileInfos.add(info);
                                        }
                                    }
                                }
                            }
                            logger.info("[logicFileInfos] " + logicFileInfos.size());
                            dyFormData.setFieldValue(key, logicFileInfos);
                        }
                    } else {
                        dyFormData.setFieldValue(key, formData.get(key));
                    }
                } else {
                    // 从表 字段
                    DyformSubformFormDefinition sub = subformDefinitionMap.get(key);
                    List<Map<String, Object>> subDataList = (List<Map<String, Object>>) formData.get(key);
                    if (sub != null && subDataList != null) {
                        for (Map<String, Object> subData : subDataList) {
                            String newDataUuid = UuidUtils.createUuid();
                            DyFormData subformData = dyFormData.getDyFormDataByFormId(key, newDataUuid);
                            for (String subKey : subData.keySet()) {

                                // inputmode ==6 为附件
                                /**UPDATE BY LINZ DATE:2015年6月26日 16:22:19**/
                                if (dyFormData.isFileField(key) && formData.get(key) != null && String.class.isAssignableFrom(formData.get(key).getClass())) {
                                    String fileId = (String) formData.get(key);
                                    List<LogicFileInfo> files = mongoFileService.getNonioFilesFromFolder(fileId, null);
                                    if (files.size() > 0) {
                                        subformData.setFieldValue(key, files);
                                    }
                                } else {
                                    subformData.setFieldValue(key, formData.get(key));
                                }
                            }
                        }
                    }
                }
            }
        }
        return dyFormData;
    }

}
