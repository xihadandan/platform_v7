/*
 * @(#)2019年8月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.jdbc.entity.IdEntity;

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
 * 2019年8月21日.1	zhulh		2019年8月21日		Create
 * </pre>
 * @date 2019年8月21日
 */
public class FormData extends BaseObject {

    public static final String KEY_UUID = IdEntity.UUID;
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1973399102655091304L;
    // 表单ID
    private String formId;

    // 表单定义UUID
    private String formUuid;

    // 数据UUID
    private String dataUuid;

    // 数据MAP
    private Map<String, Object> dataMap = Maps.newHashMap();

    // 是否新增数据
    private boolean isNew;

    // 是否更新数据
    private boolean isUpdate;

    // 更新的字段
    private Set<String> updatedFields = Sets.newLinkedHashSetWithExpectedSize(0);

    // 是否删除数据
    private boolean isDelete;

    // 从表数据
    private List<FormData> subformDatas = Lists.newArrayListWithExpectedSize(0);

    /**
     *
     */
    public FormData() {
        super();
    }

    /**
     * @param formId
     * @param dataUuid
     */
    public FormData(String formId, String formUuid, String dataUuid) {
        this.formId = formId;
        this.formUuid = formUuid;
        this.dataUuid = dataUuid;
        this.dataMap.put(KEY_UUID, dataUuid);
    }

    /**
     * @return the formId
     */
    public String getFormId() {
        return formId;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @return the dataMap
     */
    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    /**
     * @param dataMap 要设置的dataMap
     */
    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    /**
     * @return the isNew
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * @param isNew 要设置的isNew
     */
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * @return the isUpdate
     */
    public boolean isUpdate() {
        return isUpdate;
    }

    /**
     * @param isUpdate 要设置的isUpdate
     */
    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    /**
     * @return the updatedFields
     */
    public Set<String> getUpdatedFields() {
        return updatedFields;
    }

    /**
     * @param updatedFields 要设置的updatedFields
     */
    public void setUpdatedFields(Set<String> updatedFields) {
        this.updatedFields = updatedFields;
    }

    /**
     * @return the isDelete
     */
    public boolean isDelete() {
        return isDelete;
    }

    /**
     * @param isDelete 要设置的isDelete
     */
    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * @return the subformDatas
     */
    public List<FormData> getSubformDatas() {
        return subformDatas;
    }

    /**
     * @param subformDatas 要设置的subformDatas
     */
    public void setSubformDatas(List<FormData> subformDatas) {
        this.subformDatas = subformDatas;
    }

}
