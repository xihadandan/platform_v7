/*
 * @(#)2017-04-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.entity.DmsDataVersion;
import com.wellsoft.pt.dms.service.DmsDataVersionService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-04-26.1	zhulh		2017-04-26		Create
 * </pre>
 * @date 2017-04-26
 */
@Service
@Transactional
public class DmsDataVersionServiceImpl extends BaseServiceImpl implements DmsDataVersionService {

    @Resource
    DyFormFacade dyFormFacade;

    // 版本格式
    private DecimalFormat versionFormat = new DecimalFormat("0.0");

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#get(java.lang.String)
     */
    @Override
    public DmsDataVersion get(String uuid) {
        return this.dao.get(DmsDataVersion.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#getByDataUuid(java.lang.String)
     */
    @Override
    public DmsDataVersion getByDataUuid(String dataUuid) {
        return this.dao.findUniqueBy(DmsDataVersion.class, "dataUuid", dataUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#getAll()
     */
    @Override
    public List<DmsDataVersion> getAll() {
        return this.dao.getAll(DmsDataVersion.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#findByExample(DmsDataVersion)
     */
    @Override
    public List<DmsDataVersion> findByExample(DmsDataVersion example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#save(com.wellsoft.pt.dms.entity.DmsDataVersion)
     */
    @Override
    public void save(DmsDataVersion entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<DmsDataVersion> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(DmsDataVersion.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsDataVersionService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<DmsDataVersion> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#remove(DmsDataVersion)
     */
    @Override
    public void remove(DmsDataVersion entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(DmsDataVersion.class, list);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#saveVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.Object, java.lang.String, int, double, java.lang.String)
     */
    @Override
    public void saveVersion(String dataType, String title, String dataDefUuid, String dataDefId, String dataUuid,
                            int initVerNumber, double verIncrement, String remark) {
        DmsDataVersion example = new DmsDataVersion();
        example.setDataType(dataType);
        example.setDataDefUuid(dataDefUuid);
        example.setDataDefId(dataDefId);
        example.setDataUuid(dataUuid);
        List<DmsDataVersion> dataVersions = this.dao.findByExample(example);
        if (dataVersions.size() > 1) {
            throw new RuntimeException("数据UUID[" + dataUuid + "]存在多个相同版本的数据，无法保存新版本！");
        }

        DmsDataVersion sourceVersion = null;
        // 创建初始版本
        if (dataVersions.isEmpty()) {
            sourceVersion = new DmsDataVersion();
            sourceVersion.setDataType(dataType);
            sourceVersion.setInitDefUuid(dataDefUuid);
            sourceVersion.setInitDefId(dataDefId);
            sourceVersion.setInitDataUuid(dataUuid);
            sourceVersion.setDataDefUuid(dataDefUuid);
            sourceVersion.setDataDefId(dataDefId);
            sourceVersion.setDataUuid(dataUuid);
            sourceVersion.setVersion(initVerNumber + StringUtils.EMPTY);
            sourceVersion.setIsLatestVersion(true);
        } else {
            sourceVersion = dataVersions.get(0);
        }

        // 更新版本数据信息
        sourceVersion.setTitle(title);
        sourceVersion.setRemark(remark);
        this.dao.save(sourceVersion);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#saveNewVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, double)
     */
    @Override
    public void saveNewVersion(String dataType, String title, String sourceDefUuid, String sourceDefId,
                               String sourceDataUuid, String dataDefUuid, String dataDefId, String dataUuid, int initVerNumber,
                               double verIncrement, String remark) {
        DmsDataVersion example = new DmsDataVersion();
        example.setDataType(dataType);
        example.setDataDefUuid(sourceDefUuid);
        example.setDataDefId(sourceDefId);
        example.setDataUuid(sourceDataUuid);
        List<DmsDataVersion> dataVersions = this.dao.findByExample(example);
        if (dataVersions.size() > 1) {
            throw new RuntimeException("数据UUID[" + sourceDataUuid + "]存在多个相同版本的数据，无法保存新版本！");
        }

        DmsDataVersion sourceVersion = null;
        // 创建初始版本
        if (dataVersions.isEmpty()) {
            sourceVersion = new DmsDataVersion();
            sourceVersion.setDataType(dataType);
            sourceVersion.setInitDefUuid(sourceDefUuid);
            sourceVersion.setInitDefId(sourceDefId);
            sourceVersion.setInitDataUuid(sourceDataUuid);
            sourceVersion.setDataDefUuid(sourceDefUuid);
            sourceVersion.setDataDefId(sourceDefId);
            sourceVersion.setDataUuid(sourceDataUuid);
            sourceVersion.setVersion(initVerNumber + StringUtils.EMPTY);
        } else {
            sourceVersion = dataVersions.get(0);
            // 只有最新版本才可以保存为新版本
            if (Boolean.FALSE.equals(sourceVersion.getIsLatestVersion())) {
                throw new RuntimeException("当前版本的数据不是最新版本，不能保存为新版本！");
            }
        }

        // 标记为旧版本
        sourceVersion.setIsLatestVersion(false);
        this.dao.save(sourceVersion);

        // 保存为新版本
        DmsDataVersion newVersion = new DmsDataVersion();
        BeanUtils.copyProperties(sourceVersion, newVersion, new String[]{IdEntity.UUID});
        newVersion.setTitle(title);
        newVersion.setSourceDefUuid(sourceDefUuid);
        newVersion.setSourceDefId(sourceDefId);
        newVersion.setSourceDataUuid(sourceDataUuid);
        newVersion.setDataDefUuid(dataDefUuid);
        newVersion.setDataDefId(dataDefId);
        newVersion.setDataUuid(dataUuid);
        String version = versionFormat.format(Double.valueOf(newVersion.getVersion()) + verIncrement);
        newVersion.setVersion(version);
        newVersion.setRemark(remark);
        newVersion.setIsLatestVersion(true);
        this.dao.save(newVersion);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#deleteVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void deleteVersion(String dataType, String dataDefUuid, String dataDefId, String dataUuid) {
        DmsDataVersion example = new DmsDataVersion();
        example.setDataType(dataType);
        example.setDataDefUuid(dataDefUuid);
        example.setDataDefId(dataDefId);
        example.setDataUuid(dataUuid);
        List<DmsDataVersion> dataVersions = this.dao.findByExample(example);
        for (DmsDataVersion dmsDataVersion : dataVersions) {
            this.dao.delete(dmsDataVersion);

            // 前一版本升级为最新版本
            String sourceDataType = dmsDataVersion.getDataType();
            String sourceDefUuid = dmsDataVersion.getSourceDefUuid();
            String sourceDefId = dmsDataVersion.getSourceDefId();
            String sourceDataUuid = dmsDataVersion.getSourceDataUuid();
            DmsDataVersion source = new DmsDataVersion();
            source.setDataType(sourceDataType);
            source.setDataDefUuid(sourceDefUuid);
            source.setDataDefId(sourceDefId);
            source.setDataUuid(sourceDataUuid);
            List<DmsDataVersion> sourceVersions = this.dao.findByExample(source);
            for (DmsDataVersion sourceVersion : sourceVersions) {
                sourceVersion.setIsLatestVersion(true);
                this.dao.save(sourceVersion);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsDataVersionService#getAllVersions(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<DmsDataVersion> getAllVersions(String dataType, String dataDefUuid, String dataDefId, String dataUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", dataType);
        values.put("dataDefUuid", dataDefUuid);
        values.put("dataDefId", dataDefId);
        values.put("dataUuid", dataUuid);
        return this.dao.namedQuery("getAllDmsDataVersions", values);
    }

    @Override
    public List<QueryItem> listLatestVersionFormDataByFormId(String formId, PagingInfo pagingInfo) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("formId", formId);
        return this.listLatestVersionFormDataByParams(params, pagingInfo);

    }

    @Override
    public List<QueryItem> listLatestVersionFormDataByParams(Map<String, Object> params, PagingInfo pagingInfo) {
        if (!params.containsKey("formId")) {
            return Collections.emptyList();
        }
        DyFormFormDefinition def = dyFormFacade.getFormDefinitionOfMaxVersionById(params.get("formId").toString());
        params.put("formUuid", def.getUuid());
        params.put("tableName", def.getTableName());
        return this.nativeDao.namedQuery("getLatestVersionFormData", params, QueryItem.class, pagingInfo);
    }

}
