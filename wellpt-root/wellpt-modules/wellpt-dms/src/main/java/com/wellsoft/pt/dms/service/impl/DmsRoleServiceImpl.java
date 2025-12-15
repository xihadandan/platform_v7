/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.selective.facade.SelectiveDatas;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.dms.dao.DmsRoleDao;
import com.wellsoft.pt.dms.entity.DmsRoleActionEntity;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.file.action.FileActionManager;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.service.DmsRoleActionService;
import com.wellsoft.pt.dms.service.DmsRoleService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
@Service
public class DmsRoleServiceImpl extends AbstractJpaServiceImpl<DmsRoleEntity, DmsRoleDao, String> implements DmsRoleService {

    @Autowired
    private DmsRoleActionService dmsRoleActionService;

    @Autowired
    private FileActionManager fileActionManager;

    private String GET_ALL_ROLE_CATEGORIES = "select distinct t.category from DmsRoleEntity t where t.systemUnitId = :systemUnitId";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#get(java.lang.String)
     */
    @Override
    public DmsRoleEntity get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#getAll()
     */
    @Override
    public List<DmsRoleEntity> getAll() {
        DmsRoleEntity example = new DmsRoleEntity();
        example.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        return this.dao.listByEntity(example);
    }

    /**
     * @param example
     * @return
     */
    @Override
    public List<DmsRoleEntity> findByExample(DmsRoleEntity example) {
        return this.dao.listByEntity(example);
    }

    /**
     * @param entity
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    @Transactional
    public void save(DmsRoleEntity entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#saveAll(java.util.Collection)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    @Transactional
    public void saveAll(Collection<DmsRoleEntity> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    /**
     * @param entities
     */
    @Override
    @Transactional
    public void removeAll(Collection<DmsRoleEntity> entities) {
        this.dao.deleteByEntities(entities);
    }

    /**
     * @param entity
     */
    @Override
    @Transactional
    public void remove(DmsRoleEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#getAdminRole()
     */
    @Override
    public DmsRoleEntity getAdminRole() {
        String adminRoleUuid = DigestUtils.md5Hex("AdminRole");
        DmsRoleEntity adminRoleEntity = this.get(adminRoleUuid);
        if (adminRoleEntity != null) {
            return adminRoleEntity;
        }

        List<String> actionArray = fileActionManager.getAllActionIds();

        adminRoleEntity = new DmsRoleEntity();
        adminRoleEntity.setUuid(adminRoleUuid);
        adminRoleEntity.setName("内置文件角色_" + adminRoleUuid);
        adminRoleEntity.setId(adminRoleUuid);
        adminRoleEntity.setCode(adminRoleUuid);
        adminRoleEntity.setActions(StringUtils.join(actionArray, Separator.COMMA.getValue()));
        this.dao.save(adminRoleEntity);

        for (String action : actionArray) {
            DmsRoleActionEntity roleActionEntity = new DmsRoleActionEntity();
            roleActionEntity.setRoleUuid(adminRoleUuid);
            roleActionEntity.setAction(action);
            dmsRoleActionService.save(roleActionEntity);
        }
        return adminRoleEntity;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#isUse(java.lang.String)
     */
    @Override
    public boolean isUse(String uuid) {
        String hql = "select count(t.uuid) from DmsFolderConfigurationEntity t where t.configuration like '%' || :roleUuid || '%'";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("roleUuid", uuid);
        return this.dao.countByHQL(hql, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#getByCategory(java.lang.String)
     */
    @Override
    public List<DmsRoleEntity> getByCategory(String category) {
        DmsRoleEntity example = new DmsRoleEntity();
        example.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        example.setCategory(category);
        return this.dao.listByEntity(example, null, "code asc", null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsRoleService#getRoleCategories()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DataItem> getRoleCategories() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        List<String> categories = this.dao.listCharSequenceByHQL(GET_ALL_ROLE_CATEGORIES, values);
        List<DataItem> items = (List<DataItem>) SelectiveDatas.getItems("MODULE_CATEGORY");
        List<DataItem> categoryItems = new ArrayList<DataItem>();
        for (DataItem item : items) {
            if (categories.contains(item.getValue())) {
                categoryItems.add(item);
            }
        }
        return categoryItems;
    }

    @Override
    public DmsRoleEntity getById(String id) {
        return this.dao.getOneByFieldEq("id", id);
    }

    @Override
    public List<DmsRoleEntity> listBySystem(String system) {
        DmsRoleEntity entity = new DmsRoleEntity();
        entity.setSystem(system);
        return this.dao.listByEntityAndPage(entity, new PagingInfo(1, Integer.MAX_VALUE), "code asc");
    }

    @Override
    @Transactional
    public void initBySystemAndTenant(String system, String tenant) {
        DmsRoleEntity entity = new DmsRoleEntity();
        entity.setSystem(system);
        entity.setTenant(tenant);
        long count = this.dao.countByEntity(entity);
        if (count > 0) {
            return;
        }

        List<DmsRoleEntity> roleDefinitionEntities = Lists.newArrayList();
        roleDefinitionEntities.add(getInitById("manager", system, tenant));
        roleDefinitionEntities.add(getInitById("editor", system, tenant));
        roleDefinitionEntities.add(getInitById("creator", system, tenant));
        roleDefinitionEntities.add(getInitById("reader", system, tenant));
        this.dao.saveAll(roleDefinitionEntities);
    }

    @Override
    public DmsRoleEntity getInitById(String id, String system, String tenant) {
        DmsRoleEntity entity = null;
        switch (id) {
            case "manager":
                entity = createBuiltInRoleDefinition("管理者", "manager", "001", Lists.newArrayList(FileActions.VIEW_FOLDER_ATTRIBUTES,
                        FileActions.EDIT_FOLDER_ATTRIBUTES, FileActions.DELETE_FOLDER, FileActions.DELETE_FILE, FileActions.RESTORE_FILE), system, tenant);
                break;
            case "editor":
                entity = createBuiltInRoleDefinition("编辑者", "editor", "002", Lists.newArrayList(FileActions.CREATE_FOLDER,
                        FileActions.RENAME_FOLDER, FileActions.MOVE_FOLDER, FileActions.COPY_FOLDER, FileActions.CREATE_FILE, FileActions.CREATE_DOCUMENT, FileActions.RENAME_FILE, FileActions.MOVE_FILE, FileActions.COPY_FILE), system, tenant);
                break;
            case "creator":
                entity = createBuiltInRoleDefinition("作者", "creator", "003", Lists.newArrayList(FileActions.CREATE_FILE, FileActions.CREATE_DOCUMENT, FileActions.READ_FILE), system, tenant);
                break;
            case "reader":
                entity = createBuiltInRoleDefinition("阅读者(可下载)", "reader", "004", Lists.newArrayList(FileActions.READ_FILE,
                        FileActions.DOWNLOAD_FILE, FileActions.PREVIEW_FILE, FileActions.SHARE_FILE, FileActions.VIEW_FILE_ATTRIBUTES), system, tenant);
                break;
        }
        return entity;
    }

    /**
     * @param name
     * @param id
     * @param code
     * @param actions
     * @param system
     * @param tenant
     * @return
     */
    private DmsRoleEntity createBuiltInRoleDefinition(String name, String id, String code, List<String> actions, String system, String tenant) {
        Map<String, Object> definitionJson = Maps.newHashMap();
        Map<String, Object> defaultRule = Maps.newHashMap();
        defaultRule.put("mode", "all");
        defaultRule.put("limitUsers", Collections.emptyList());
        definitionJson.put("readFileRule", defaultRule);
        definitionJson.put("editFileRule", defaultRule);
        definitionJson.put("manageFileRule", defaultRule);
        definitionJson.put("actions", actions);
        DmsRoleEntity role = new DmsRoleEntity();
        role.setName(name);
        role.setId(id);
        role.setCode(code);
        role.setBuiltIn(true);
        role.setDefinitionJson(JsonUtils.object2Json(definitionJson));
        role.setRemark(name);
        role.setSystem(system);
        role.setTenant(tenant);
        return role;
    }

    @Override
    public List<DmsRoleEntity> listByIdsAndSystem(List<String> ids, String system) {
        Assert.notEmpty(ids, "ids不能为空");
        Assert.hasText(system, "system不能为空");

        String hql = "from DmsRoleEntity where id in (:ids) and system = :system";
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        params.put("system", system);
        return this.dao.listByHQL(hql, params);
    }

}
