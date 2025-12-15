/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dms.bean.DmsFolderAssignRoleBean;
import com.wellsoft.pt.dms.bean.DmsFolderBean;
import com.wellsoft.pt.dms.bean.DmsFolderChildrenBean;
import com.wellsoft.pt.dms.bean.DmsFolderConfigurationBean;
import com.wellsoft.pt.dms.entity.DmsFolderConfigurationEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.facade.service.DmsFolderMgr;
import com.wellsoft.pt.dms.model.DmsFolderConfiguration;
import com.wellsoft.pt.dms.service.*;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
@Service
public class DmsFolderMgrImpl extends BaseServiceImpl implements DmsFolderMgr {
    Logger logger = LoggerFactory.getLogger(DmsFolderMgrImpl.class);
    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private DmsFolderConfigurationService dmsFolderConfigurationService;

    @Autowired
    private DmsObjectAssignRoleService dmsObjectAssignRoleService;

    @Autowired
    private DmsObjectIdentityService dmsObjectIdentityService;

    @Autowired
    private DmsObjectAssignRoleItemService dmsObjectAssignRoleItemService;

    @Autowired
    private DmsRoleService dmsRoleService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsFolderMgr#getBean(java.lang.String)
     */
    @Override
    public DmsFolderBean getBean(String uuid) {
        DmsFolderEntity entity = dmsFolderService.get(uuid);
        DmsFolderBean bean = new DmsFolderBean();
        BeanUtils.copyProperties(entity, bean);

        String folderUuid = entity.getUuid();
        // 夹配置
        DmsFolderConfigurationEntity folderConfigurationEntity = dmsFolderConfigurationService.getByFolderUuid(folderUuid);
        if (folderConfigurationEntity == null) {
            folderConfigurationEntity = new DmsFolderConfigurationEntity();
        }
        String configuration = folderConfigurationEntity.getConfiguration();
        if (StringUtils.isBlank(configuration)) {
            configuration = "{}";
        }
        DmsFolderConfigurationBean configurationBean = JsonUtils.json2Object(configuration,
                DmsFolderConfigurationBean.class);
        // 夹权限配置，实时加载角色名称
        List<DmsFolderAssignRoleBean> assignRoleBeans = configurationBean.getAssignRoles();
        // 填充配置的内置角色
        if (CollectionUtils.isEmpty(assignRoleBeans)
                && (StringUtils.isNotBlank(configurationBean.getAdministrator())
                || StringUtils.isNotBlank(configurationBean.getEditor())
                || StringUtils.isNotBlank(configurationBean.getReader()))) {
            fillAssignRoleBeans(assignRoleBeans, configurationBean);
        }
        for (DmsFolderAssignRoleBean assignRoleBean : assignRoleBeans) {
            String roleUuid = assignRoleBean.getRoleUuid();
            if (StringUtils.isBlank(roleUuid)) {
                logger.error("文件库管理夹设置中的角色不存在， 角色名称为{}, 角色ID为{}", assignRoleBean.getRoleName(),
                        assignRoleBean.getRoleUuid());
                continue;
            }
            DmsRoleEntity dmsRoleEntity = dmsRoleService.get(roleUuid);

            if (dmsRoleEntity != null) {
                assignRoleBean.setRoleName(dmsRoleEntity.getName());
            } else {
                logger.error("文件库管理夹设置中的角色不存在， 角色名称为{}, 角色ID为{}", assignRoleBean.getRoleName(),
                        assignRoleBean.getRoleUuid());
            }
        }
        bean.setConfiguration(configurationBean);
        bean.setI18ns(appDefElementI18nService.getI18ns(bean.getUuid(), null, new BigDecimal(1), IexportType.DmsFolder));
        return bean;
    }

    /**
     * @param assignRoleBeans
     * @param folderConfiguration
     */
    private void fillAssignRoleBeans(List<DmsFolderAssignRoleBean> assignRoleBeans, DmsFolderConfigurationBean folderConfiguration) {
        String administratorRoleId = "administrator_" + folderConfiguration.getFolderUuid();
        String editorRoleId = "editor_" + folderConfiguration.getFolderUuid();
        String readerRoleId = "reader_" + folderConfiguration.getFolderUuid();

        DmsRoleEntity administratorRoleEntity = dmsRoleService.getById(administratorRoleId);
        DmsRoleEntity editorRoleEntity = dmsRoleService.getById(editorRoleId);
        DmsRoleEntity readerRoleEntity = dmsRoleService.getById(readerRoleId);

        addAssignRoleBean(administratorRoleEntity, folderConfiguration.getAdministrator(), assignRoleBeans);
        addAssignRoleBean(editorRoleEntity, folderConfiguration.getEditor(), assignRoleBeans);
        addAssignRoleBean(readerRoleEntity, folderConfiguration.getReader(), assignRoleBeans);
    }

    /**
     * @param administratorRoleEntity
     * @param orgIds
     * @param assignRoleBeans
     */
    private void addAssignRoleBean(DmsRoleEntity administratorRoleEntity, String orgIds, List<DmsFolderAssignRoleBean> assignRoleBeans) {
        if (administratorRoleEntity != null) {
            DmsFolderAssignRoleBean assignRoleBean = new DmsFolderAssignRoleBean();
            assignRoleBean.setRoleUuid(administratorRoleEntity.getUuid());
            assignRoleBean.setRoleName(administratorRoleEntity.getName());
            assignRoleBean.setOrgIds(orgIds);
            assignRoleBean.setPermit("Y");
            assignRoleBean.setDeny("N");
            assignRoleBeans.add(assignRoleBean);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsFolderMgr#saveBean(com.wellsoft.pt.dms.bean.DmsFolderBean)
     */
    @Override
    @Transactional
    public void saveBean(DmsFolderBean bean) {
        String uuid = bean.getUuid();
        DmsFolderEntity entity = new DmsFolderEntity();
        if (StringUtils.isNotBlank(uuid)) {
            entity = dmsFolderService.get(uuid);
        } else {
            bean.setSystem(RequestSystemContextPathResolver.system());
            bean.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }

        BeanUtils.copyProperties(bean, entity);
        dmsFolderService.save(entity);
        // 更新夹UUID路径
        entity.setUuidPath(getUuidPath(entity));
        if (StringUtils.isBlank(entity.getParentUuid())) {
            entity.setLibraryUuid(entity.getUuid());
        }
        dmsFolderService.save(entity);
        if (CollectionUtils.isNotEmpty(bean.getI18ns())) {
            appDefElementI18nService.deleteAllI18n(null, entity.getUuid(), null, IexportType.DmsFolder);
            for (AppDefElementI18nEntity i : bean.getI18ns()) {
                i.setDefId(entity.getUuid());
                i.setApplyTo(IexportType.DmsFolder);
                i.setVersion(new BigDecimal(1.0));
            }
            appDefElementI18nService.saveAll(bean.getI18ns());
        }

        // 保存夹配置
        DmsFolderConfiguration dmsFolderConfiguration = JsonUtils.json2Object(
                JsonUtils.object2Json(bean.getConfiguration()), DmsFolderConfiguration.class);
        dmsFolderConfiguration.setFolderUuid(entity.getUuid());
        dmsFolderConfigurationService.saveFolderConfiguration(dmsFolderConfiguration);
    }

    /**
     * @return
     */
    private String getUuidPath(DmsFolderEntity entity) {
        String parentUuid = entity.getParentUuid();
        if (StringUtils.isBlank(parentUuid)) {
            return "/" + entity.getUuid();
        }
        DmsFolderEntity parent = dmsFolderService.get(parentUuid);
        return parent.getUuidPath() + "/" + entity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsFolderMgr#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.deleteOrNot(uuid);
        dmsFolderService.remove(uuid);
    }

    private void deleteOrNot(String uuid) {
        DmsFolderEntity entity = dmsFolderService.get(uuid);
        if (entity == null) {
            return;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        String hqlStr = "select count(*) from DmsFolderEntity where parentUuid=:parentUuid";
        query.put("parentUuid", uuid);
        long count = this.dao.count(hqlStr, query);
        if (count > 0l) {
            throw new RuntimeException(entity.getName() + ":存在子夹，不能删除");
        }
        hqlStr = "select count(*) from DmsFileEntity where folderUuid=:folderUuid";
        query.put("folderUuid", uuid);
        count = this.dao.count(hqlStr, query);
        if (count > 0l) {
            throw new RuntimeException(entity.getName() + ":存在文件，不能删除");
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsFolderMgr#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.deleteOrNot(uuid);
        }
        dmsFolderService.removeAllByPk(uuids);
    }

    @Override
    public List<DmsFolderChildrenBean> children(String uuid) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        StringBuilder hql = new StringBuilder("from DmsFolderEntity where systemUnitId=:systemUnitId ");
        if (StringUtils.isBlank(uuid)) {
            hql.append("and parentUuid is null ");
        } else {
            query.put("parentUuid", uuid);
            hql.append("and parentUuid=:parentUuid ");
        }
        hql.append(" order by code,createTime ");
        List<DmsFolderEntity> list = this.dao.find(hql.toString(), query, DmsFolderEntity.class);
        List<DmsFolderChildrenBean> childrenBeans = new ArrayList<>();
        for (DmsFolderEntity dmsFolderEntity : list) {
            DmsFolderChildrenBean childrenBean = new DmsFolderChildrenBean();
            childrenBean.setDmsFolder(dmsFolderEntity);
            String hqlStr = "select count(*) from DmsFolderEntity where systemUnitId=:systemUnitId and parentUuid=:parentUuid";
            query.put("parentUuid", dmsFolderEntity.getUuid());
            long count = this.dao.count(hqlStr, query);
            if (count > 0l) {
                childrenBean.setIsParent(true);
            } else {
                childrenBean.setIsParent(false);
            }
            childrenBeans.add(childrenBean);
        }
        return childrenBeans;
    }
}
