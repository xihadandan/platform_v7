package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.OrgElementI18nDao;
import com.wellsoft.pt.org.entity.OrgElementI18nEntity;
import com.wellsoft.pt.org.service.OrgElementI18nService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月11日   chenq	 Create
 * </pre>
 */
@Service
public class OrgElementI18nServiceImpl extends AbstractJpaServiceImpl<OrgElementI18nEntity, OrgElementI18nDao, Long> implements OrgElementI18nService {
    @Override
    public OrgElementI18nEntity getOrgElementI18n(String dataId, String dataCode, String locale) {
        Assert.noNullElements(new String[]{dataId, dataCode, locale}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        params.put("dataCode", dataCode.toLowerCase());
        params.put("locale", locale);
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        try {
            return this.dao.getOneByHQL("from OrgElementI18nEntity where dataCode=:dataCode and dataId=:dataId and locale=:locale and system=:system and tenant=:tenant", params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public OrgElementI18nEntity getOrgElementI18n(Long dataUuid, String dataCode, String locale) {
        Assert.noNullElements(new Object[]{dataUuid, dataCode, locale}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        params.put("dataCode", dataCode.toLowerCase());
        params.put("locale", locale);
        return this.dao.getOneByHQL("from OrgElementI18nEntity where dataCode=:dataCode and dataUuid=:dataUuid and locale=:locale", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18ns(Long dataUuid, String dataCode) {
        Assert.noNullElements(new Object[]{dataUuid, dataCode}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        params.put("dataCode", dataCode.toLowerCase());
        return this.dao.listByHQL("from OrgElementI18nEntity where dataCode=:dataCode and dataUuid=:dataUuid", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18ns(String dataId, String dataCode) {
        Assert.noNullElements(new Object[]{dataId, dataCode}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        params.put("dataCode", dataCode.toLowerCase());
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.listByHQL("from OrgElementI18nEntity where dataCode=:dataCode and dataId=:dataId and system=:system and tenant=:tenant", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18nsByDataIds(List<String> dataId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.listByHQL("from OrgElementI18nEntity where dataId in :dataId and system=:system and tenant=:tenant", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18nsByDataUuids(List<Long> dataUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        return this.dao.listByHQL("from OrgElementI18nEntity where dataUuid in :dataUuid", params);
    }

    @Override
    @Transactional
    public void saveAllAfterDelete(String dataId, List<OrgElementI18nEntity> newList) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        dao.deleteByHQL("delete from OrgElementI18nEntity where  dataId=:dataId and system=:system and tenant=:tenant", params);
        dao.saveAll(newList);
    }

    @Override
    @Transactional
    public void saveAllAfterDelete(Long dataUuid, List<OrgElementI18nEntity> newList) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        dao.deleteByHQL("delete from OrgElementI18nEntity where  dataUuid=:dataUuid", params);
        dao.saveAll(newList);
    }

    @Override
    @Transactional
    public void deleteByDataId(String dataId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        dao.deleteByHQL("delete from OrgElementI18nEntity where  dataId=:dataId and system=:system and tenant=:tenant", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18ns(Long dataUuid) {
        Assert.noNullElements(new Object[]{dataUuid}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        return this.dao.listByHQL("from OrgElementI18nEntity where dataUuid=:dataUuid", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18nsByDataUuidAndLocale(Long dataUuid, String locale) {
        Assert.noNullElements(new Object[]{dataUuid, locale}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        params.put("locale", locale);
        return this.dao.listByHQL("from OrgElementI18nEntity where dataUuid=:dataUuid and locale=:locale", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18nsByDataUuidsAndLocale(List<Long> dataUuid, String locale) {
        Assert.noNullElements(new Object[]{dataUuid, locale}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        params.put("locale", locale);
        return this.dao.listByHQL("from OrgElementI18nEntity where dataUuid in :dataUuid and locale=:locale", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18ns(String dataId) {
        Assert.noNullElements(new Object[]{dataId}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataId);
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.listByHQL("from OrgElementI18nEntity where dataId=:dataId and system=:system and tenant=:tenant", params);
    }

    @Override
    public List<OrgElementI18nEntity> getOrgElementI18ns(Set<String> dataIds, String dataCode, String locale) {
        Assert.noNullElements(new Object[]{dataIds, locale}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataId", dataIds);
        params.put("dataCode", dataCode.toLowerCase());
        params.put("locale", locale);
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.listByHQL("from OrgElementI18nEntity where dataCode=:dataCode and dataId in :dataId and locale=:locale and system=:system and tenant=:tenant", params);
    }

    @Override
    public List<OrgElementI18nEntity> listOrgElementI18ns(Set<Long> dataUuids, String dataCode, String locale) {
        Assert.noNullElements(new Object[]{dataUuids, dataCode}, "参数均不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuids", dataUuids);
        params.put("dataCode", dataCode.toLowerCase());
        params.put("locale", locale);
        return this.dao.listByHQL("from OrgElementI18nEntity where dataCode=:dataCode and dataUuid in :dataUuids " + (StringUtils.isNotBlank(locale) ? " and locale=:locale" : ""), params);
    }

    @Override
    @Transactional
    public void deleteByDataUuid(Long dataUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("dataUuid", dataUuid);
        dao.deleteByHQL("delete from OrgElementI18nEntity where  dataUuid=:dataUuid", params);
    }
}
