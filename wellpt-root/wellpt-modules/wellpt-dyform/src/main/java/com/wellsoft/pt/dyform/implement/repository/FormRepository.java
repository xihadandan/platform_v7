/*
 * @(#)2019年8月20日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;

import java.util.List;
import java.util.Map;

/**
 * Description: 表单存储接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月20日.1	zhulh		2019年8月20日		Create
 * </pre>
 * @date 2019年8月20日
 */
public interface FormRepository {

    /**
     * 获取主表数据
     *
     * @param formId
     * @param dataUuid
     * @param repositoryContext
     * @return
     */
    Map<String, Object> getDataOfMainform(String formId, String dataUuid, FormRepositoryContext repositoryContext);

    /**
     * 获取从表数据
     *
     * @param subformId
     * @param mainformId
     * @param mainformDataUuid
     * @param repositoryContext
     * @return
     */
    List<Map<String, Object>> getDataOfSubform(String subformId, String mainformId, String mainformDataUuid,
                                               FormRepositoryContext repositoryContext);

    /**
     * 保存表单存储数据
     *
     * @param formData
     * @param repositoryContext
     * @return
     */
    String saveFormData(FormData formData, FormRepositoryContext repositoryContext);

    /**
     * 根据表单查询信息查询数据
     *
     * @param queryInfo
     * @param repositoryContext
     * @return
     */
    FormDataQueryData query(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext);

    /**
     * 根据表单查询信息查询总数
     *
     * @param queryInfo
     * @param repositoryContext
     * @return
     */
    long count(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext);

    /**
     * 删除表单数据
     *
     * @param formId
     * @param dataUuid
     * @param cascade
     * @param repositoryContext
     */
    void deleteFormData(String formId, String dataUuid, boolean cascade, FormRepositoryContext repositoryContext);

    /**
     * 删除主表数据下的所有从表数据
     *
     * @param mainformId
     * @param mainformDataUuid
     * @param repositoryContext
     */
    void deleteSubformData(String mainformId, String mainformDataUuid, FormRepositoryContext repositoryContext);

    /**
     * 删除主表数据下的指定从表数据
     *
     * @param mainformId
     * @param mainformDataUuid
     * @param subformId
     * @param repositoryContext
     */
    void deleteSubformData(String mainformId, String mainformDataUuid, String subformId,
                           FormRepositoryContext repositoryContext);

    /**
     * 根据表单ID获取表单字段列表
     *
     * @param formId
     * @param repositoryContext
     * @return
     */
    List<FormField> getFormFields(String formId, FormRepositoryContext repositoryContext);

}
