/*
 * @(#)2019年8月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.collection.List2GroupMap;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Clob;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
 * 2019年8月21日.1	zhulh		2019年8月21日		Create
 * </pre>
 * @date 2019年8月21日
 */
public abstract class AbstractFormRepository implements FormRepository {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected MongoFileService mongoFileService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#getDataOfMainform(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public Map<String, Object> getDataOfMainform(String formId, String dataUuid, FormRepositoryContext repositoryContext) {
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put(FormData.KEY_UUID, dataUuid);
        return dataMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#getDataOfSubform(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public List<Map<String, Object>> getDataOfSubform(String subformId, String mainformId, String mainformDataUuid,
                                                      FormRepositoryContext repositoryContext) {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#saveFormData(com.wellsoft.pt.dyform.implement.repository.FormData, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public String saveFormData(FormData formData, FormRepositoryContext repositoryContext) {
        return formData.getDataUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#query(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public FormDataQueryData query(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext) {
        FormDataQueryData queryData = new FormDataQueryData();
        queryData.setDataList(Lists.newArrayList());
        return queryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#count(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public long count(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext) {
        return 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#deleteFormData(java.lang.String, java.lang.String, boolean, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteFormData(String formId, String dataUuid, boolean cascade, FormRepositoryContext repositoryContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#deleteSubformData(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteSubformData(String mainformId, String mainformDataUuid, FormRepositoryContext repositoryContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#deleteSubformData(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteSubformData(String mainformId, String mainformDataUuid, String subformId,
                                  FormRepositoryContext repositoryContext) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#getFormFields(java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public List<FormField> getFormFields(String formId, FormRepositoryContext repositoryContext) {
        return Collections.emptyList();
    }

    /**
     * @param repositoryContext
     * @param list
     */
    protected void decorateResultData(List<Map<String, Object>> list, FormRepositoryContext repositoryContext) {
        List2GroupMap<LogicFileInfo> fileGroupMapConvert = new List2GroupMap<LogicFileInfo>() {

            @Override
            protected String getGroupUuid(LogicFileInfo logicFileInfo) {
                return logicFileInfo.getPurpose();
            }

        };
        List<String> fieldNames = repositoryContext.getFieldNames();
        for (Map<String, Object> map : list) {
            // 数据的字段大小写匹配
            for (String fieldName : fieldNames) {
                String lowerCaseFieldName = StringUtils.lowerCase(fieldName);
                if (!StringUtils.equals(fieldName, lowerCaseFieldName) && map.containsKey(lowerCaseFieldName)) {
                    map.put(fieldName, map.remove(lowerCaseFieldName));
                }
            }

            // 附件字段处理
            String dataUuid = ObjectUtils.toString(map.get(FormData.KEY_UUID));
            List<LogicFileInfo> allfiles = this.mongoFileService.getNonioFilesFromFolder(dataUuid, null);
            Map<String, List<LogicFileInfo>> fileGroupMap = fileGroupMapConvert.convert(allfiles);
            if (CollectionUtils.isNotEmpty(allfiles)) {
                for (String fieldName : repositoryContext.getFieldNames()) {
                    if (repositoryContext.isFileField(fieldName)) {// 附件
                        List<LogicFileInfo> files = fileGroupMap.get(fieldName);
                        if (CollectionUtils.isNotEmpty(files)) {
                            map.put(fieldName, files);
                        }
                    }
                }
            }

            // 数据格式转化
            for (Entry<String, Object> entry : map.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                if (fieldValue == null) {
                    continue;
                }
                // 日期字段
                if (fieldValue instanceof Date && repositoryContext.isDateTimeField(fieldName)) {
                    String datePattern = repositoryContext.getDateTimeFieldPattern(fieldName);
                    fieldValue = DateUtils.format((Date) fieldValue, datePattern);
                } else if (fieldValue instanceof Clob) {
                    // 大字段
                    fieldValue = ClobUtils.ClobToString((Clob) fieldValue);
                }
                map.put(fieldName, fieldValue);
            }
        }

    }

}
