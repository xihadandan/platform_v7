/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.dms.bean.DmsRoleBean;
import com.wellsoft.pt.dms.entity.DmsRoleActionEntity;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.facade.service.DmsRoleMgr;
import com.wellsoft.pt.dms.service.DmsRoleActionService;
import com.wellsoft.pt.dms.service.DmsRoleService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
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
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
@Service
public class DmsRoleMgrImpl implements DmsRoleMgr {

    @Autowired
    private DmsRoleService dmsRoleService;

    @Autowired
    private DmsRoleActionService dmsRoleActionService;

    @Autowired
    private CommonValidateService commonValidateService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsRoleMgr#getBean(java.lang.String)
     */
    @Override
    public DmsRoleBean getBean(String uuid) {
        DmsRoleEntity entity = dmsRoleService.get(uuid);
        DmsRoleBean bean = new DmsRoleBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsRoleMgr#getAll()
     */
    @Override
    public List<DmsRoleBean> getAll() {
        List<DmsRoleEntity> entities = dmsRoleService.getAll();
        List<DmsRoleBean> beans = BeanUtils.copyCollection(entities, DmsRoleBean.class);
        return beans;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsRoleMgr#saveBean(com.wellsoft.pt.dms.bean.DmsRoleBean)
     */
    @Override
    @Transactional
    public String saveBean(DmsRoleBean bean) {
        String uuid = bean.getUuid();
        DmsRoleEntity entity = new DmsRoleEntity();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            entity = dmsRoleService.get(uuid);
            // 类型非空唯一性判断
            if (StringUtils.isNotBlank(bean.getId())
                    && !commonValidateService.checkUnique(bean.getUuid(), "dmsRoleEntity", "id", bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的操作权限！");
            }
        } else {
            // ID非空唯一性判断
            if (StringUtils.isNotBlank(bean.getId())
                    && commonValidateService.checkExists("dmsRoleEntity", "id", bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的操作权限！");
            }
            bean.setSystem(RequestSystemContextPathResolver.system());
            bean.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }

        BeanUtils.copyProperties(bean, entity);
        dmsRoleService.save(entity);

        dmsRoleActionService.removeByRoleUuid(entity.getUuid());
        String actions = entity.getActions();
        String definitionJson = entity.getDefinitionJson();
        List<String> actionArray = null;
        if (StringUtils.isNotBlank(definitionJson)) {
            Map<String, Object> definitionMap = JsonUtils.json2Object(definitionJson, Map.class);
            actionArray = (List<String>) definitionMap.get("actions");
        } else if (StringUtils.isNotBlank(actions)) {
            actionArray = Arrays.asList(StringUtils.split(actions, Separator.COMMA.getValue()));
        }
        if (CollectionUtils.isNotEmpty(actionArray)) {
//            DmsRoleActionEntity readFolderActionEntity = new DmsRoleActionEntity();
//            readFolderActionEntity.setRoleUuid(entity.getUuid());
//            readFolderActionEntity.setAction(FileActions.READ_FOLDER);
//            dmsRoleActionService.save(readFolderActionEntity);
//
//            List<String> readFileActions = Lists.newArrayList(FileActions.READ_FILE,
//                    FileActions.DOWNLOAD_FILE, FileActions.PREVIEW_FILE, FileActions.SHARE_FILE, FileActions.VIEW_FILE_ATTRIBUTES);
//            if (CollectionUtils.containsAny(actionArray, readFileActions)) {
//                DmsRoleActionEntity readFileActionEntity = new DmsRoleActionEntity();
//                readFileActionEntity.setRoleUuid(entity.getUuid());
//                readFileActionEntity.setAction(FileActions.READ_FILE);
//                dmsRoleActionService.save(readFileActionEntity);
//            }
            for (String action : actionArray) {
                DmsRoleActionEntity roleActionEntity = new DmsRoleActionEntity();
                roleActionEntity.setRoleUuid(entity.getUuid());
                roleActionEntity.setAction(action);
                dmsRoleActionService.save(roleActionEntity);
            }
        }
        return entity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsRoleMgr#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        // 夹配置引用判断
        if (dmsRoleService.isUse(uuid)) {
            throw new RuntimeException("夹操作权限定义被引用，不能删除！");
        }
        dmsRoleActionService.removeByRoleUuid(uuid);
        dmsRoleService.remove(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsRoleMgr#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.remove(uuid);
        }
    }

}
