/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.dao.AppModuleDao;
import com.wellsoft.pt.app.dao.impl.AppModuleResSeqDaoImpl;
import com.wellsoft.pt.app.dto.AppModuleDto;
import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.entity.AppModuleResSeqEntity;
import com.wellsoft.pt.app.entity.AppTagEntity;
import com.wellsoft.pt.app.service.AppModuleService;
import com.wellsoft.pt.app.service.AppTagService;
import com.wellsoft.pt.app.web.api.request.AppModuleKeywordQuery;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
@Service
public class AppModuleServiceImpl extends
        AbstractJpaServiceImpl<AppModule, AppModuleDao, String> implements
        AppModuleService {

    @Autowired
    AppModuleResSeqDaoImpl appModuleResSeqDao;

    @Autowired
    AppTagService appTagService;

    @Override
    public AppModule get(String uuid) {
        return getOne(uuid);
    }

    @Override
    public AppModule getById(String id) {
        List<AppModule> appModules = this.getByIds(new String[]{id});
        return CollectionUtils.isNotEmpty(appModules) ? appModules.get(0) : null;
    }

    @Override
    public String getModuleNameById(String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        String hql = "select t.name from AppModule t where t.id = :id";
        return dao.getCharSequenceByHQL(hql, params);
    }

    @Override
    public List<AppModule> getAll() {
        return listAll();
    }

    @Override
    public List<AppModule> findByExample(AppModule example) {
        return this.dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        AppModule appModule = getOne(uuid);
        if (appModule != null) {
            this.dao.delete(uuid);
            // 删除数据标签
            this.appTagService.deleteDataTag(appModule.getId(), null);
            this.appTagService.deleteUnUsedTag();
            //TODO: 删除模块下的资源 ??


        }

    }

    @Override
    @Transactional
    public void removeAll(Collection<AppModule> entities) {
        deleteByEntities(entities);
    }

    @Override
    @Transactional
    public void remove(AppModule entity) {
        this.dao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    @Override
    public Long countByParentUuid(String uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("parentUuid", uuid);
        return this.dao.countByHQL("select count(uuid) from AppModule where parentUuid=:parentUuid",
                param);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppModuleService#getByIds(java.lang.String[])
     */
    @Override
    public List<AppModule> getByIds(String[] ids) {
        return this.dao.listByFieldInValues("id", Lists.newArrayList(ids));
    }

    @Override
    public long countBySystemUnitId(String systemUnitId) {
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitId", systemUnitId);
        long count = this.dao.countBySQL("select count(a.uuid) from app_module a left join app_product_integration b on  a.uuid = b.data_uuid and b.data_type=2 where a.system_unit_id=:systemUnitId", params);
        return count;
    }

    @Override
    public List<AppModule> queryByKeyword(AppModuleKeywordQuery query) {
        Map<String, Object> param = Maps.newHashMap();
        StringBuilder hql = new StringBuilder("from AppModule p where 1=1 ");
        if (query.getKeyword() != null) {
            param.put("keyword", "%" + query.getKeyword() + "%");
            hql.append(" and (p.name like :keyword or p.id like :keyword or p.remark like :keyword)");
        }
        if (CollectionUtils.isNotEmpty(query.getTagUuids())) {
            param.put("tagUuid", query.getTagUuids());
            hql.append(" and exists ( select 1 from AppDataTagEntity dt where dt.dataId = p.id and dt.tagUuid in (:tagUuid) )");
        }
        if (query.getCategoryUuid() != null) {
            param.put("categoryUuid", query.getCategoryUuid());
            hql.append(" and exists ( select 1 from AppCategoryEntity c where c.uuid =:categoryUuid and c.uuid = p.categoryUuid ) ");
        }
        if (StringUtils.isNotBlank(query.getOrderBy())) {
            hql.append(query.getOrderBy());
        } else {
            hql.append(" order by createTime desc");
        }

        List<AppModule> entities = query.getPage() != null ? this.listByHQLAndPage(hql.toString(), param, query.getPage()) : this.listByHQL(hql.toString(), param);
        return entities;
    }

    @Override
    public List<AppModule> listModuleByIds(List<String> id) {
        return dao.listByFieldInValues("id", id);
    }

    @Override
    public List<AppModule> getAllEnableModules() {
        return dao.listByHQL("from AppModule where enabled=true order by createTime desc", null);
    }

    @Override
    @Transactional
    public void deleteModulesByUuids(List<String> uuid) {
        if (CollectionUtils.isNotEmpty(uuid)) {
            for (String uid : uuid) {
                this.remove(uid);
            }
        }
    }

    @Override
    public List<String> queryRelaModuleIds(String moduleId) {
        List<QueryItem> queryItems = this.queryRelaModulesByModuleId(moduleId);
        List<String> ids = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(queryItems)) {
            queryItems.forEach(queryItem -> {
                ids.add(queryItem.getString("id"));
            });
        }
        return ids;
    }

    @Override
    public List<QueryItem> queryRelaModulesByModuleId(String moduleId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", moduleId);
        List<QueryItem> queryItems = this.dao.listQueryItemBySQL("select a.uuid as uuid, a.id as id , a.name as name,a.icon as icon \n" +
                "  from app_module a\n" +
                " where a.id <>:id and  exists (\n" +
                // 同一个产品版本下:
                "        select 1\n" +
                "          from app_prod_module p, app_prod_module v\n" +
                "         where p.module_id = a.id\n" +
                "           and p.prod_version_uuid = v.prod_version_uuid\n" +
                "           and v.module_id = :id)\n" +
                "      \n" +
                "    or exists\n" +
                " (select 1\n" +
                "          from app_system_info s, app_module m\n" +
                "         where m.id = :id \n" +
                "           and (\n" +
                // 模块在系统上: 获取同系统上模块 、获取系统指定产品版本下模块
                "                ( m.system = s.system and m.tenant = s.tenant and\n" +
                "                ((a.system = s.system and a.tenant = s.tenant) or exists\n" +
                "                 (select 1\n" +
                "                     from app_prod_module v\n" +
                "                    where v.prod_version_uuid = s.prod_version_uuid\n" +
                "                      and v.module_id = a.id))\n" +
                "                \n" +
                "                )\n" +
                "               \n" +
                "                or\n" +
                // 模块在系统指定产品版本下里：获取同系统上模块
                "                (exists (select 1\n" +
                "                           from app_prod_module v\n" +
                "                          where v.prod_version_uuid = s.prod_version_uuid\n" +
                "                            and v.module_id = m.id) and\n" +
                "                 a.system = s.system and a.tenant = s.tenant)\n" +
                "               \n" +
                "               )\n" +
                "        \n" +
                "        )", param, null);

        queryItems.addAll(dao.listQueryItemBySQL("select a.uuid as uuid, a.id as id , a.name as name,a.icon as icon from app_module a where a.id=:id", param, null));
        return queryItems;
    }

    @Override
    public List<AppModule> listModuleUnderSystem(String system, String tenant) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("system", system);
        param.put("tenant", tenant);
        return this.dao.listBySQL("select a.*\n" +
                "  from app_module a\n" +
                " where (a.system = :system and a.tenant = :tenant )\n" +
                "    or exists (select 1\n" +
                "          from app_system_info s, app_prod_module p \n" +
                "         where p.module_id = a.id \n" +
                "           and p.prod_version_uuid = s.prod_version_uuid \n" +
                "           and s.system =  :system \n" +
                "           and s.tenant = :tenant )", param);
    }

    @Override
    public Set<String> getModuleRelaSystems(String moduleId) {
        Set<String> systems = Sets.newHashSet();
        AppModule appModule = getById(moduleId);
        if (StringUtils.isNotBlank(appModule.getSystem())) {
            systems.add(appModule.getSystem());
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put("moduleId", moduleId);
        List<QueryItem> items = this.dao.listQueryItemBySQL("select v.prod_id as system from app_prod_module m, app_prod_version v \n" +
                "where v.uuid =m.prod_version_uuid and m.module_id = :moduleId" +
                "", param, null);
        if (CollectionUtils.isNotEmpty(items)) {
            for (QueryItem i : items) {
                systems.add(i.getString("system"));
            }
        }
        return systems;
    }

    @Override
    @Transactional
    public void saveModuleResSeq(List<AppModuleResSeqEntity> list) {
        appModuleResSeqDao.updateModuleResSeq(list);
    }

    @Override
    public List<AppModuleResSeqEntity> listModuleResSeq(String moduleId, String type) {
        AppModuleResSeqEntity example = new AppModuleResSeqEntity();
        example.setModuleId(moduleId);
        if (StringUtils.isNotBlank(type)) {
            example.setType(type);
        }
        return appModuleResSeqDao.listByEntity(example);
    }

    @Override
    public Boolean moduleIdExist(String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        return dao.listCharSequenceBySQL("select 1 from app_module where id=:id", params).size() > 0;
    }

    @Override
    @Transactional
    public void updateEnabled(String uuid, boolean enable) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        params.put("enabled", enable);
        this.dao.updateByHQL("update AppModule set enabled=:enabled where uuid=:uuid", params);
    }

    @Override
    @Transactional
    public String saveModule(AppModuleDto dto) {
        AppModule appModule = StringUtils.isNotBlank(dto.getUuid()) ? getOne(dto.getUuid()) : new AppModule();
        BeanUtils.copyProperties(dto, appModule, AppModule.BASE_FIELDS);
        save(appModule);
        // 删除模块标签
        appTagService.deleteDataTag(dto.getId(), null);
        if (CollectionUtils.isNotEmpty(dto.getTags())) {
            List<Long> tagUuids = Lists.newArrayListWithCapacity(dto.getTags().size());
            for (AppTagEntity tag : dto.getTags()) {
                tagUuids.add(tag.getUuid());
            }
            appTagService.saveDataTag(Lists.newArrayList(dto.getId()), tagUuids);
        }
        return appModule.getUuid();
    }
}
