/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.app.dao.AppProductDao;
import com.wellsoft.pt.app.dto.AppProductDto;
import com.wellsoft.pt.app.entity.AppProdVersionEntity;
import com.wellsoft.pt.app.entity.AppProdVersionSettingEntity;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.entity.AppTagEntity;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.web.api.request.AppProdKeywordQuery;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 产品实体类服务实现
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-24.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
@Service
public class AppProductServiceImpl extends AbstractJpaServiceImpl<AppProduct, AppProductDao, String> implements
        AppProductService {

    @Autowired
    AppTagService appTagService;

    @Autowired
    AppProdVersionService appProdVersionService;

    @Autowired
    AppModuleService appModuleService;

    @Autowired
    AppProductAclService appProductAclService;

    @Autowired
    AppProdVersionSettingService appProdVersionSettingService;


    @Override
    public AppProduct get(String uuid) {
        return getOne(uuid);
    }

    @Override
    public AppProduct getProductByProdVersionUuid(Long prodVersionUuid) {
        AppProdVersionEntity version = appProdVersionService.getOne(prodVersionUuid);
        if (version != null) {
            AppProduct product = dao.getOneByFieldEq("id", version.getProdId());
            return product;
        }
        return null;
    }

    @Override
    @Transactional
    public AppProductDto saveProduct(AppProductDto dto) {
        AppProduct product = new AppProduct();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            product = getOne(dto.getUuid());
            BeanUtils.copyProperties(dto, product, product.BASE_FIELDS);
            save(product);
        } else {
            BeanUtils.copyProperties(dto, product, product.BASE_FIELDS);
            save(product);

        }
        if (product.getUuid() != null) {
            // 保存产品关联的标签
            appTagService.deleteDataTag(product.getId(), null);
            if (CollectionUtils.isNotEmpty(dto.getTags())) {
                List<Long> tagUuid = Lists.newArrayListWithCapacity(dto.getTags().size());
                for (AppTagEntity tag : dto.getTags()) {
                    tagUuid.add(tag.getUuid());
                }
                appTagService.saveDataTag(Lists.newArrayList(product.getId()), tagUuid);

            }
        }
        AppProductDto result = new AppProductDto();
        if (StringUtils.isBlank(dto.getUuid())) {
            // 创建初始版本
            AppProdVersionEntity version = new AppProdVersionEntity();
            version.setProdId(product.getId());
            version.setVersion("v1.0");
            version.setVersionId("PROD_VER_" + SnowFlake.getId());
            version.setStatus(AppProdVersionEntity.Status.BUILDING);
            appProdVersionService.save(version);
            result.setLatestVersion(version);

            // 保存生成空的产品版本配置
            AppProdVersionSettingEntity settingEntity = new AppProdVersionSettingEntity();
            settingEntity.setProdVersionId(version.getVersionId());
            settingEntity.setProdVersionUuid(version.getUuid());
            appProdVersionSettingService.save(settingEntity);

        }
        result.setModifyTime(product.getModifyTime());
        result.setUuid(product.getUuid());
        return result;
    }

    @Override
    public List<AppProduct> getAll() {
        return listAll();
    }

    @Override
    public List<AppProduct> findByExample(AppProduct example) {
        return this.dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    @Override
    @Transactional
    public void removeAll(Collection<AppProduct> entities) {
        deleteByEntities(entities);
    }

    @Override
    @Transactional
    public void remove(AppProduct entity) {
        this.dao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    @Override
    public long countBySystemUnitId(String systemUnitId) {
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitId", systemUnitId);
        long count = this.dao.countByHQL("select count(uuid) from AppProduct where systemUnitId=:systemUnitId", params);
        return count;
    }

    @Override
    public boolean idExist(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        long count = this.dao.countByHQL("select count(uuid) from AppProduct where id=:id", params);
        return count > 0;
    }

    @Override
    @Transactional
    public void deleteProd(String uuid) {
        AppProduct product = getOne(uuid);
        if (product != null) {
            dao.delete(uuid);
            appProdVersionService.deleteByProdId(product.getId());
            appProductAclService.deleteProdAcl(product.getId(), null);
            appTagService.deleteDataTag(product.getId(), null);
            appTagService.deleteUnUsedTag();
        }

    }

    @Override
    public List<AppProduct> queryByKeyword(AppProdKeywordQuery query) {
        Map<String, Object> param = Maps.newHashMap();
        StringBuilder hql = new StringBuilder("from AppProduct p where 1=1 ");
        if (query.getKeyword() != null) {
            param.put("keyword", "%" + query.getKeyword() + "%");
            hql.append(" and (p.name like :keyword or p.id like :keyword or p.remark like :keyword)");
        }
        if (CollectionUtils.isNotEmpty(query.getStatus())) {
            param.put("status", query.getStatus());
            hql.append(" and p.status in :status");
        }
        if (query.getCategoryUuid() != null) {
            param.put("categoryUuid", query.getCategoryUuid());
            hql.append(" and p.categoryUuid=:categoryUuid");
        }
        if (CollectionUtils.isNotEmpty(query.getExcludeIds())) {
            param.put("excludeIds", query.getExcludeIds());
            hql.append(" and p.id not in :excludeIds");
        }
        hql.append(" order by createTime desc");
        List<AppProduct> entities = query.getPage() != null ? this.listByHQLAndPage(hql.toString(), param, query.getPage()) : this.listByHQL(hql.toString(), param);
        return entities;
    }

    @Override
    public AppProductDto getProductDetail(String uuid) {
        AppProductDto dto = null;
        AppProduct product = getOne(uuid);
        if (product != null) {
            dto = new AppProductDto();
            // 获取最新发布的版本
            AppProdVersionEntity versionEntity = appProdVersionService.queryLatestPubVersion(product.getId());
            if (versionEntity != null) {
                dto.setLatestVersion(versionEntity);
            } else {
                // 获取最新创建的版本
                dto.setLatestVersion(appProdVersionService.queryLatestCreate(product.getId()));
            }
            BeanUtils.copyProperties(product, dto);
        }
        return dto;
    }

    @Override
    @Transactional
    public AppProductDto updateStatus(String uuid, AppProduct.Status status) {
        AppProduct product = this.dao.getOne(uuid);
        if (product != null) {
            AppProduct.Status oldStatus = product.getStatus();
            product.setStatus(status);
            save(product);
            // 初次发布上线，连同最近创建的版本一起发布
            if (AppProduct.Status.BUILDING.equals(oldStatus) && AppProduct.Status.LAUNCH.equals(status)) {
                AppProdVersionEntity versionEntity = appProdVersionService.queryLatestCreate(product.getId());
                if (!AppProdVersionEntity.Status.PUBLISHED.equals(versionEntity.getStatus())) {
                    appProdVersionService.updateStatus(versionEntity.getUuid(), AppProdVersionEntity.Status.PUBLISHED);
                }
            }
            return this.getProductDetail(uuid);
        }
        return null;
    }

    @Override
    public AppProductDto getProductDetailById(String id) {
        AppProduct product = this.dao.getOneByFieldEq("id", id);
        if (product != null) {
            return this.getProductDetail(product.getUuid());
        }
        return null;
    }

    @Override
    public AppProdVersionEntity getProdVersionByVersionId(String appId) {
        return appProdVersionService.getProdVersionByVersionId(appId);
    }


}
