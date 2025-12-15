/*
 * @(#)2016-03-01 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.bean.TenantTemplateBean;
import com.wellsoft.pt.mt.bean.TenantTemplateModuleBean;
import com.wellsoft.pt.mt.dao.TenantTemplateDao;
import com.wellsoft.pt.mt.entity.TenantTemplate;
import com.wellsoft.pt.mt.entity.TenantTemplateModule;
import com.wellsoft.pt.mt.service.TenantTemplateModuleService;
import com.wellsoft.pt.mt.service.TenantTemplateService;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-01.1	linz		2016-03-01		Create
 * </pre>
 * @date 2016-03-01
 */
@Service
public class TenantTemplateServiceImpl extends AbstractJpaServiceImpl<TenantTemplate, TenantTemplateDao, String>
        implements TenantTemplateService {

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private TenantTemplateModuleService tenanteTemplateModuleService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateService#get(java.lang.String)
     */
    @Override
    public TenantTemplate get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateService#getAll()
     */
    @Override
    public List<TenantTemplate> getAll() {
        return listAll();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateService#findByExample(TenantTemplate)
     */
    @Override
    public List<TenantTemplate> findByExample(TenantTemplate example) {
        return dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        TenantTemplate template = this.get(uuid);
        // 移除删除的DDL文件
        if (StringUtils.isNotBlank(template.getDdlFileUuids())) {
            String fileIds[] = template.getDdlFileUuids().split(";");
            for (String fileId : fileIds) {
                if (StringUtils.isNotBlank(template.getUuid())) {
                    mongoFileService.popFileFromFolder(template.getUuid(), fileId);
                }
            }
        }
        // 移除删除的DML文件
        if (StringUtils.isNotBlank(template.getDmlFileUuids())) {
            String fileIds[] = template.getDmlFileUuids().split(";");
            for (String fileId : fileIds) {
                if (StringUtils.isNotBlank(template.getUuid())) {
                    mongoFileService.popFileFromFolder(template.getUuid(), fileId);
                }
            }

        }
        Set<TenantTemplateModule> templateModules = template.getTenantTemplateModules();
        for (TenantTemplateModule tenantTemplateModule : templateModules) {
            if (tenantTemplateModule.getRepoFileUuids() != null) {
                String fileIds[] = tenantTemplateModule.getRepoFileUuids().split(";");
                for (String fileId : fileIds) {
                    mongoFileService.popFileFromFolder(tenantTemplateModule.getUuid(), fileId);
                }
            }
        }
        for (TenantTemplateModule tenantTemplateModule : templateModules) {
            dao.delete(tenantTemplateModule.getUuid());
        }
        dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.TenantTemplateService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<TenantTemplate> entities) {
        deleteByEntities(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateService#remove(TenantTemplate)
     */
    @Override
    @Transactional
    public void remove(TenantTemplate entity) {
        dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantTemplateService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        for (String uuid : uuids) {
            this.remove(uuid);
        }
    }

    @Override
    public QueryData queryDataGrid(String searchValue, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // TODO Auto-generated method stub
        String hql = "from TenantTemplate w ";
        Map<String, Object> obj = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(searchValue)) {
            obj.put("searchValue", "%" + searchValue + "%");
            hql += "where w.name like :searchValue or w.remark like :searchValue or"
                    + " w.ddlFileNames like :searchValue or w.dmlFileNames like :searchValue";
        }
        List<TenantTemplate> tenantTemplates = dao.listByHQLAndPage(hql, obj, queryInfo.getPagingInfo());
        QueryData queryData = new QueryData();
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        queryData.setDataList(tenantTemplates);
        return queryData;
    }

    @Override
    @Transactional
    public void saveBean(TenantTemplateBean tenantTemplateBean) {
        // TODO Auto-generated method stub
        TenantTemplate tenantTemplate = new TenantTemplate();
        if (StringUtils.isNotBlank(tenantTemplateBean.getUuid())) {
            tenantTemplate = this.get(tenantTemplateBean.getUuid());
        }
        BeanUtils.copyProperties(tenantTemplateBean, tenantTemplate);

        // 移除删除的DDL文件
        if (StringUtils.isNotBlank(tenantTemplateBean.getDelDdlFileUuids())) {
            String fileIds[] = tenantTemplateBean.getDelDdlFileUuids().split(";");
            for (String fileId : fileIds) {
                if (StringUtils.isNotBlank(tenantTemplate.getUuid())) {
                    mongoFileService.popFileFromFolder(tenantTemplate.getUuid(), fileId);
                }
            }

        }
        // 移除删除的DML文件
        if (StringUtils.isNotBlank(tenantTemplateBean.getDelDmlFileUuids())) {
            String fileIds[] = tenantTemplateBean.getDelDmlFileUuids().split(";");
            for (String fileId : fileIds) {
                if (StringUtils.isNotBlank(tenantTemplate.getUuid())) {
                    mongoFileService.popFileFromFolder(tenantTemplate.getUuid(), fileId);
                }
            }

        }
        this.save(tenantTemplate);
        if (tenantTemplateBean.getTenantTemplateModuleBeans().size() > 0) {
            Set<TenantTemplateModuleBean> tenantTemplateModuleBeans = tenantTemplateBean.getTenantTemplateModuleBeans();
            for (TenantTemplateModuleBean tenantTemplateModuleBean : tenantTemplateModuleBeans) {
                TenantTemplateModule tenantTemplateModule = new TenantTemplateModule();
                if (StringUtils.isNotBlank(tenantTemplateModuleBean.getUuid())
                        && !tenantTemplateModuleBean.getUuid().startsWith("new_")) {
                    tenantTemplateModule = tenanteTemplateModuleService.getOne(tenantTemplateModuleBean.getUuid());
                }
                BeanUtils.copyProperties(tenantTemplateModuleBean, tenantTemplateModule);
                tenantTemplateModule.setTenantTemplate(tenantTemplate);
                // 移除删除的DML文件
                if (StringUtils.isNotBlank(tenantTemplateModuleBean.getDelRepoFileUuids())) {
                    String fileIds[] = tenantTemplateModuleBean.getDelRepoFileUuids().split(";");
                    for (String fileId : fileIds) {
                        if (tenantTemplateModule.getUuid().indexOf("new_") < 0
                                && StringUtils.isNotBlank(tenantTemplateModule.getUuid())) {
                            mongoFileService.popFileFromFolder(tenantTemplateModule.getUuid(), fileId);
                        } else {
                            tenantTemplateModule.setUuid(null);
                        }
                    }

                }
                tenanteTemplateModuleService.save(tenantTemplateModule);
                if (StringUtils.isNotBlank(tenantTemplateModule.getRepoFileUuids())) {
                    String fileIds[] = tenantTemplateModule.getRepoFileUuids().split(";");
                    for (String fileId : fileIds) {
                        this.publishMongoFile(tenantTemplateModule.getUuid(), fileId);
                    }

                }
            }
        }
        if (StringUtils.isNotBlank(tenantTemplate.getDdlFileUuids())) {
            String fileIds[] = tenantTemplateBean.getDdlFileUuids().split(";");
            for (String fileId : fileIds) {
                this.publishMongoFile(tenantTemplate.getUuid(), fileId);
            }
        }
        if (StringUtils.isNotBlank(tenantTemplate.getDmlFileUuids())) {
            String fileIds[] = tenantTemplateBean.getDmlFileUuids().split(";");
            for (String fileId : fileIds) {
                this.publishMongoFile(tenantTemplate.getUuid(), fileId);
            }
        }

    }

    /**
     * 将文件移入夹中
     *
     * @param fordleId
     * @param fileIds
     */
    private void publishMongoFile(String fordleId, String fileIds) {
        String fileIdArray[] = fileIds.split(";");
        List<String> filIdList = new ArrayList<String>();
        for (String fileId : fileIdArray) {
            filIdList.add(fileId);
        }
        mongoFileService.pushFilesToFolder(fordleId, filIdList, null);
    }

    @Override
    public TenantTemplateBean getBean(String uuid) {
        // TODO Auto-generated method stub
        TenantTemplate tenantTemplate = this.get(uuid);
        TenantTemplateBean templateBean = new TenantTemplateBean();
        BeanUtils.copyProperties(tenantTemplate, templateBean);
        return templateBean;
    }

    @Override
    @Transactional
    public void deleteById(String uuid) {
        this.remove(uuid);
    }

}
