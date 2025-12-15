package com.wellsoft.pt.common.i18n.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.common.i18n.dao.I18nEntityDao;
import com.wellsoft.pt.common.i18n.entity.I18nEntity;
import com.wellsoft.pt.common.i18n.service.DataI18nService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月25日   chenq	 Create
 * </pre>
 */
@Service
public class DataI18nServiceImpl extends AbstractJpaServiceImpl<I18nEntity, I18nEntityDao, Long> implements DataI18nService {
    @Override
    public <T extends I18nEntity> T getByDataIdAndCodeAndLocale(Class<T> type, String dataId, String dataCode, String locale) {
        Assert.noNullElements(new Object[]{dataId, dataCode, locale, type}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        params.put("dataCode", dataCode);
        params.put("locale", locale);
        StringBuilder hql = new StringBuilder("from ");
        hql.append(type.getCanonicalName());
        hql.append(" where dataId=:dataId and dataCode =:dataCode and locale=:locale");
        List<T> list = (List<T>) dao.listByHQL(hql.toString(), params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public <T extends I18nEntity> T getByDataUuidAndCodeAndLocale(Class<T> type, Long dataUuid, String dataCode, String locale) {
        Assert.noNullElements(new Object[]{dataUuid, dataCode, locale, type}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        params.put("dataCode", dataCode);
        params.put("locale", locale);
        StringBuilder hql = new StringBuilder("from ");
        hql.append(type.getCanonicalName());
        hql.append(" where dataUuid=:dataUuid and dataCode =:dataCode and locale=:locale");
        List<T> list = (List<T>) dao.listByHQL(hql.toString(), params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public <T extends I18nEntity> List<T> listByDataUuids(Class<T> type, Collection<Long> dataUuids, String locale) {
        Assert.noNullElements(new Object[]{dataUuids, type}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("locale", locale);
        StringBuilder hql = new StringBuilder("from ");
        hql.append(type.getCanonicalName());
        hql.append(" where dataUuid in :dataUuid ");
        if (StringUtils.isNotBlank(locale)) {
            hql.append(" and locale=:locale ");
        }

        List<T> results = Lists.newArrayList();
        ListUtils.handleSubList(Lists.newArrayList(dataUuids), 200, list -> {
            params.put("dataUuid", list);
            results.addAll((List<T>) dao.listByHQL(hql.toString(), params));
        });

        return results;
    }

    @Override
    public <T extends I18nEntity> List<T> listByDataIds(Class<T> type, List<String> dataIds, String locale) {
        Assert.noNullElements(new Object[]{dataIds, type}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataIds", dataIds);
        params.put("locale", locale);
        StringBuilder hql = new StringBuilder("from ");
        hql.append(type.getCanonicalName());
        hql.append(" where dataId in :dataIds ");
        if (StringUtils.isNotBlank(locale)) {
            hql.append(" and locale=:locale ");
        }
        List<T> list = (List<T>) dao.listByHQL(hql.toString(), params);
        return list;
    }

    @Override
    public <T extends I18nEntity> List<T> listByDataUuidsAndCode(Class<T> type, List<Long> dataUuids, String dataCode) {
        Assert.noNullElements(new Object[]{dataUuids, dataCode}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuids);
        params.put("dataCode", dataCode);
        StringBuilder hql = new StringBuilder("from ");
        hql.append(type.getCanonicalName());
        hql.append(" where dataUuid in :dataUuid and dataCode=:dataCode ");
        List<T> list = (List<T>) dao.listByHQL(hql.toString(), params);
        return list;
    }

    @Override
    public <T extends I18nEntity> List<T> listByDataIdsCodeLocale(Class<T> type, List<String> dataIds, String dataCode, String locale) {
        Assert.noNullElements(new Object[]{dataIds, type, dataCode}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataIds", dataIds);
        params.put("locale", locale);
        params.put("dataCode", dataCode);
        StringBuilder hql = new StringBuilder("from ");
        hql.append(type.getCanonicalName());
        hql.append(" where dataId in :dataIds and dataCode=:dataCode ");
        if (StringUtils.isNotBlank(locale)) {
            hql.append(" and locale=:locale ");
        }
        List<T> list = (List<T>) dao.listByHQL(hql.toString(), params);
        return CollectionUtils.isNotEmpty(list) ? (List<T>) list.get(0) : null;
    }

    @Override
    public <T extends I18nEntity> List<T> listByDataUuidsCodeLocale(Class<T> type, List<Long> dataUuids, String dataCode, String locale) {
        Assert.noNullElements(new Object[]{dataUuids, type, dataCode}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuids", dataUuids);
        params.put("locale", locale);
        params.put("dataCode", dataCode);
        StringBuilder hql = new StringBuilder("from ");
        hql.append(type.getCanonicalName());
        hql.append(" where dataUuid in :dataUuids and dataCode=:dataCode ");
        if (StringUtils.isNotBlank(locale)) {
            hql.append(" and locale=:locale ");
        }
        List<T> list = (List<T>) dao.listByHQL(hql.toString(), params);
        return list;
    }

    @Override
    public <T extends I18nEntity> List<T> listByDataIdsAndCode(Class<T> type, List<String> dataIds, String dataCode) {
        Assert.noNullElements(new Object[]{dataIds, type, dataCode}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataIds", dataIds);
        params.put("dataCode", dataCode);
        StringBuilder hql = new StringBuilder("from ");
        hql.append(type.getCanonicalName());
        hql.append(" where dataId in :dataIds and dataCode=:dataCode ");
        List<T> list = (List<T>) dao.listByHQL(hql.toString(), params);
        return list;
    }

    @Override
    @Transactional
    public void deleteByDataUuid(Long dataUuid, Class<? extends I18nEntity> type) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        dao.deleteByHQL("delete from " + type.getName().substring(type.getName().lastIndexOf(".") + 1) + " where dataUuid=:dataUuid", params);
    }

    @Override
    @Transactional
    public void deleteByDataUuids(List<Long> dataUuid, Class<? extends I18nEntity> type) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        dao.deleteByHQL("delete from " + type.getName().substring(type.getName().lastIndexOf(".") + 1) + " where dataUuid in :dataUuid", params);
    }

    @Override
    public void deleteByDataId(String dataId, Class<? extends I18nEntity> type) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        dao.deleteByHQL("delete from " + type.getName().substring(type.getName().lastIndexOf(".") + 1) + " where dataId=:dataId", params);
    }

    @Override
    public void deleteByDataIdAndCodeAndLocale(String dataId, String dataCode, String locale, Class<? extends I18nEntity> type) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        params.put("dataCode", dataCode);
        params.put("locale", locale);
        dao.deleteByHQL("delete from " + type.getName().substring(type.getName().lastIndexOf(".") + 1) + " where dataId=:dataId and dataCode=:dataCode and locale=:locale", params);
    }

    @Override
    @Transactional
    public void saveAll(String dataId, List<? extends I18nEntity> list, Class<? extends I18nEntity> type) {
        this.deleteByDataId(dataId, type);
        this.dao.saveAll(list);
    }

    @Override
    @Transactional
    public void saveAll(Long dataUuid, List<? extends I18nEntity> list, Class<? extends I18nEntity> type) {
        this.deleteByDataUuid(dataUuid, type);
        this.dao.saveAll(list);
    }


}
