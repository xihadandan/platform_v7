/*
 * @(#)2019年9月30日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreColumnBean;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.dms.bean.DmsDataPermissionDefinitionDto;
import com.wellsoft.pt.dms.dp.owner.AbstractCurrentUserDataOwnerProvider;
import com.wellsoft.pt.dms.dp.owner.DataOwnerProvider;
import com.wellsoft.pt.dms.entity.DmsDataPermissionDefinitionEntity;
import com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService;
import com.wellsoft.pt.dms.service.DmsDataPermissionDefinitionService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年9月30日.1	zhulh		2019年9月30日		Create
 * </pre>
 * @date 2019年9月30日
 */
@Service
public class DmsDataPermissionDefinitionFacadeServiceImpl extends BaseServiceImpl implements
        DmsDataPermissionDefinitionFacadeService {

    @Autowired
    private DmsDataPermissionDefinitionService dmsDataPermissionDefinitionService;

    @Autowired
    private RoleFacadeService roleFacadeService;

    @Autowired
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;

    @Autowired
    private CommonValidateService commonValidateService;

    @Autowired
    private List<AbstractCurrentUserDataOwnerProvider> dataOwnerProviders;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#saveDto(com.wellsoft.pt.dms.bean.DmsDataPermissionDefinitionDto)
     */
    @Override
    public void saveDto(DmsDataPermissionDefinitionDto dto) {
        String uuid = dto.getUuid();
        DmsDataPermissionDefinitionEntity entity = new DmsDataPermissionDefinitionEntity();
        if (StringUtils.isNotBlank(uuid)) {
            entity = dmsDataPermissionDefinitionService.getOne(uuid);
        }
        BeanUtils.copyProperties(dto, entity, new String[]{IdEntity.REC_VER});
        dmsDataPermissionDefinitionService.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#getDto(java.lang.String)
     */
    @Override
    public DmsDataPermissionDefinitionDto getDto(String uuid) {
        DmsDataPermissionDefinitionEntity entity = dmsDataPermissionDefinitionService.getOne(uuid);
        DmsDataPermissionDefinitionDto dto = new DmsDataPermissionDefinitionDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        dmsDataPermissionDefinitionService.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        dmsDataPermissionDefinitionService.deleteByUuids(Lists.newArrayList(uuids));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#loadColumnsSelectData(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadRolesSelectData(Select2QueryInfo queryInfo) {
        List<Role> roles = roleFacadeService.getAll();
        return new Select2QueryData(roles, "id", "name");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#loadFieldNamesSelectData(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadFieldNamesSelectData(Select2QueryInfo queryInfo) {
        String type = queryInfo.getOtherParams("type");
        String dataName = queryInfo.getOtherParams("dataName");
        List<CdDataStoreColumnBean> columnBeans = Lists.newArrayList();
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(dataName)) {
            if (StringUtils.equals("1", type)) {
                columnBeans = cdDataStoreDefinitionService.loadTableColumns(dataName);
            } else {
                columnBeans = cdDataStoreDefinitionService.loadViewColumns(dataName);
            }
        }
        return new Select2QueryData(columnBeans, "columnName", "title");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#getCurrentUserDataOwnerProviders()
     */
    @Override
    public List<DataItem> getCurrentUserDataOwnerProviders() {
        List<DataItem> dataItems = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dataOwnerProviders)) {
            for (DataOwnerProvider dataOwnerProvider : dataOwnerProviders) {
                DataItem dataItem = new DataItem();
                dataItem.setLabel(dataOwnerProvider.getName());
                dataItem.setValue(dataOwnerProvider.getType());
                dataItems.add(dataItem);
            }
        }
        return dataItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#checkExistsByUuidAndName(java.lang.String, java.lang.String)
     */
    @Override
    public boolean checkExistsByUuidAndName(String uuid, String name) {
        if (StringUtils.isBlank(uuid)) {
            return commonValidateService.checkExists("dmsDataPermissionDefinitionEntity", "name", name);
        } else {
            return commonValidateService.checkUnique(uuid, "dmsDataPermissionDefinitionEntity", "name", name);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataPermissionDefinitionFacadeService#checkExistsByUuidAndId(java.lang.String, java.lang.String)
     */
    @Override
    public boolean checkExistsByUuidAndId(String uuid, String id) {
        if (StringUtils.isBlank(uuid)) {
            return commonValidateService.checkExists("dmsDataPermissionDefinitionEntity", "id", id);
        } else {
            return commonValidateService.checkUnique(uuid, "dmsDataPermissionDefinitionEntity", "id", id);
        }
    }

}
