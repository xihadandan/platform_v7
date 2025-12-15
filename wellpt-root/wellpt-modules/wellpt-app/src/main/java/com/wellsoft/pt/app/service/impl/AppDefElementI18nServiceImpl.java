package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppDefElementI18nDao;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
 * 2025年03月04日   chenq	 Create
 * </pre>
 */
@Service
public class AppDefElementI18nServiceImpl extends AbstractJpaServiceImpl<AppDefElementI18nEntity, AppDefElementI18nDao, Long> implements AppDefElementI18nService {
    @Override
    @Transactional
    public void deleteAllI18n(String elementId, String defId, BigDecimal version, String applyTo) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("elementId", elementId);
        params.put("defId", defId);
        params.put("version", version);
        params.put("applyTo", applyTo);
        StringBuilder hql = new StringBuilder();
        hql.append("delete from AppDefElementI18nEntity where defId = :defId");
        if (StringUtils.isNotBlank(elementId)) {
            hql.append(" and elementId =:elementId");
        }
        if (version != null) {
            hql.append(" and version = :version");
        }
        if (StringUtils.isNotBlank(applyTo)) {
            hql.append(" and applyTo = :applyTo");
        }
        dao.deleteByHQL(hql.toString(), params);
    }

    @Override
    public void deleteAllCodeI18n(String code, String defId, BigDecimal version, String applyTo) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("code", code);
        params.put("defId", defId);
        params.put("version", version);
        params.put("applyTo", applyTo);
        StringBuilder hql = new StringBuilder();
        hql.append("delete from AppDefElementI18nEntity where defId = :defId");
        if (StringUtils.isNotBlank(code)) {
            hql.append(" and code =:code");
        }
        if (version != null) {
            hql.append(" and version = :version");
        }
        if (StringUtils.isNotBlank(applyTo)) {
            hql.append(" and applyTo = :applyTo");
        }
        dao.deleteByHQL(hql.toString(), params);
    }


    @Override
    public List<AppDefElementI18nEntity> getI18ns(String defId, String elementId, BigDecimal version, String applyTo) {
        return this.getI18ns(defId, elementId, version, applyTo, null);
    }

    @Override
    public List<AppDefElementI18nEntity> getI18ns(Set<String> defId, String applyTo, String code, String locale) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("defId", defId);
        params.put("applyTo", applyTo);
        params.put("locale", locale);
        params.put("code", code);
        StringBuilder hql = new StringBuilder();
        hql.append("from AppDefElementI18nEntity a where a.defId in :defId ");
        if (StringUtils.isNotBlank(code)) {
            hql.append(" and code =:code");
        }
        if (StringUtils.isNotBlank(locale)) {
            hql.append(" and locale =:locale");
        }
        if (StringUtils.isNotBlank(applyTo)) {
            hql.append(" and applyTo = :applyTo");
        }
        return dao.listByHQL(hql.toString(), params);
    }

    @Override
    public List<AppDefElementI18nEntity> getI18ns(Set<String> defId, String applyTo, String code, BigDecimal version, String locale) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("defId", defId);
        params.put("version", version);
        params.put("applyTo", applyTo);
        params.put("locale", locale);
        params.put("code", code);
        StringBuilder hql = new StringBuilder();
        hql.append("from AppDefElementI18nEntity a where a.defId in :defId ");
        if (StringUtils.isNotBlank(locale)) {
            hql.append(" and locale = :locale");
        }
        if (StringUtils.isNotBlank(code)) {
            hql.append(" and code = :code");
        }
        if (version != null) {
            hql.append(" and a.version = :version");
        } else {
            hql.append(" and a.version = ( select max(b.version) from AppDefElementI18nEntity b " +
                    " where b.applyTo=a.applyTo and  b.defId = a.defId and b.code = a.code  )");
        }
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            params.put("a.system", system);
            params.put("a.tenant", SpringSecurityUtils.getCurrentTenantId());
        }
        if (StringUtils.isNotBlank(applyTo)) {
            hql.append(" and applyTo = :applyTo");
        }
        List<AppDefElementI18nEntity> list = dao.listByHQL(hql.toString(), params);
        return list;
    }

    @Override
    public List<AppDefElementI18nEntity> getI18ns(String defId, String elementId, BigDecimal version, String applyTo, String locale) {
        return this.getI18ns(defId, elementId, null, version, applyTo, locale);
    }

    @Override
    public AppDefElementI18nEntity getI18n(String defId, String elementId, String code, BigDecimal version, String applyTo, String locale) {
        List<AppDefElementI18nEntity> i18nEntities = this.getI18ns(defId, elementId, code, version, applyTo, locale);
        return CollectionUtils.isNotEmpty(i18nEntities) ? i18nEntities.get(0) : null;
    }

    @Override
    public List<AppDefElementI18nEntity> getI18ns(String defId, String elementId, String code, BigDecimal version, String applyTo, String locale) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("elementId", elementId);
        params.put("defId", defId);
        params.put("version", version);
        params.put("applyTo", applyTo);
        params.put("locale", locale);
        params.put("code", code);
        StringBuilder hql = new StringBuilder();
        hql.append("from AppDefElementI18nEntity a where a.defId = :defId ");
        if (StringUtils.isNotBlank(elementId)) {
            hql.append(" and elementId = :elementId");
        }
        if (StringUtils.isNotBlank(locale)) {
            hql.append(" and locale = :locale");
        }
        if (StringUtils.isNotBlank(code)) {
            hql.append(" and code = :code");
        }
        if (version != null) {
            hql.append(" and a.version = :version");
        } else {
            hql.append(" and a.version = ( select max(b.version) from AppDefElementI18nEntity b " +
                    " where b.applyTo=a.applyTo and  b.defId = a.defId and b.code = a.code  )");
        }
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            params.put("a.system", system);
            params.put("a.tenant", SpringSecurityUtils.getCurrentTenantId());
        }
        if (StringUtils.isNotBlank(applyTo)) {
            hql.append(" and applyTo = :applyTo");
        }
        List<AppDefElementI18nEntity> list = dao.listByHQL(hql.toString(), params);
        return list;
    }
}
