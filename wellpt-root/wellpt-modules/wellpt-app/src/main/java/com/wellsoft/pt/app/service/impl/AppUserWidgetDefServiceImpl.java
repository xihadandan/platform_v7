package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.dao.impl.AppUserWidgetDefDaoImpl;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.entity.AppUserWidgetDefEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.app.service.AppUserWidgetDefService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/27
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/27    chenq		2019/8/27		Create
 * </pre>
 */
@Service
public class AppUserWidgetDefServiceImpl extends
        AbstractJpaServiceImpl<AppUserWidgetDefEntity, AppUserWidgetDefDaoImpl, String> implements
        AppUserWidgetDefService {

    @Autowired
    AppWidgetDefinitionService appWidgetDefinitionService;

    @Autowired
    AppDefElementI18nService appDefElementI18nService;

    @Override
    @Transactional
    public void saveUserWidgetDef(String userId, String widgetId, String json, AppUserWidgetDefEntity.Type type) {
        AppUserWidgetDefEntity userWidgetDefEntity = new AppUserWidgetDefEntity();
        userWidgetDefEntity.setUserId(userId);
        userWidgetDefEntity.setWidgetId(widgetId);
        userWidgetDefEntity.setType(type);
        if (AppUserWidgetDefEntity.Type.WIDGET.equals(type)) {
            List<AppUserWidgetDefEntity> exists = this.dao.listByEntity(userWidgetDefEntity);
            if (CollectionUtils.isNotEmpty(exists)) {
                userWidgetDefEntity = exists.get(0);
            }
        }
        userWidgetDefEntity.setDefinitionJson(json);
        save(userWidgetDefEntity);
    }

    @Override
    public String getUserWidgetDefition(String userId, String widgetId) {
        AppUserWidgetDefEntity userWidgetDefEntity = new AppUserWidgetDefEntity();
        userWidgetDefEntity.setUserId(userId);
        userWidgetDefEntity.setWidgetId(widgetId);
        List<AppUserWidgetDefEntity> entityList = this.dao.listByEntity(userWidgetDefEntity);
        return CollectionUtils.isNotEmpty(entityList) ? entityList.get(0).getDefinitionJson() : "";
    }

    @Override
    public List<AppUserWidgetDefEntity> getUserWidgetDefition(String currentUserId, AppUserWidgetDefEntity.Type type) {
        AppUserWidgetDefEntity userWidgetDefEntity = new AppUserWidgetDefEntity();
        userWidgetDefEntity.setUserId(currentUserId);
        userWidgetDefEntity.setType(type);
        return this.dao.listByEntity(userWidgetDefEntity);
    }

    @Override
    public List<AppUserWidgetDefEntity> getWidgetDefinitionByType(AppUserWidgetDefEntity.Type type) {
        AppUserWidgetDefEntity userWidgetDefEntity = new AppUserWidgetDefEntity();
        userWidgetDefEntity.setType(type);
        return this.dao.listByEntity(userWidgetDefEntity);
    }

    @Override
    public List<AppUserWidgetDefEntity> getWidgetDefinitionByTypeAndAppId(AppUserWidgetDefEntity.Type type, String appId) {
        AppUserWidgetDefEntity example = new AppUserWidgetDefEntity();
        example.setAppId(appId);
        example.setType(type);
        return dao.listByEntity(example);
    }

    @Override
    public List<AppUserWidgetDefEntity> pageQueryDefTypeAppWidgets(String keyword, AppUserWidgetDefEntity.Type type, List<String> appId, PagingInfo page) {
        Map<String, Object> params = Maps.newHashMap();
        StringBuilder sql = new StringBuilder("from AppUserWidgetDefEntity where 1=1 ");
        if (type != null) {
            sql.append(" and type=:type");
            params.put("type", type);
        }
        if (CollectionUtils.isNotEmpty(appId)) {
            sql.append(" and appId in :appId");
            params.put("appId", appId);
        }
        if (StringUtils.isNotBlank(keyword)) {
            params.put("keyword", "%" + keyword + "%");
            sql.append(" and ( title like :keyword or remark like :keyword)");
        }
        sql.append(" order by createTime desc");
        if (page != null) {
            return this.dao.listByHQLAndPage(sql.toString(), params, page.getPageSize() == -1 ? null : page);
        } else {
            return this.dao.listByHQL(sql.toString(), params);
        }
    }

    @Override
    @Transactional
    public void saveUserWidgetDef(AppUserWidgetDefEntity body) {
        AppUserWidgetDefEntity userWidgetDefEntity = new AppUserWidgetDefEntity();
        if (StringUtils.isNotBlank(body.getUuid())) {
            userWidgetDefEntity = getOne(body.getUuid());
        } else {
            userWidgetDefEntity.setUserId(StringUtils.defaultIfBlank(body.getUserId(), SpringSecurityUtils.getCurrentUserId()));
            userWidgetDefEntity.setWidgetId(body.getWidgetId());
            userWidgetDefEntity.setType(body.getType());
            if (AppUserWidgetDefEntity.Type.WIDGET.equals(body.getType())) {
                List<AppUserWidgetDefEntity> exists = this.dao.listByEntity(userWidgetDefEntity);
                if (CollectionUtils.isNotEmpty(exists)) {
                    userWidgetDefEntity = exists.get(0);
                }
            }
        }
        if (body.getEnabled() != null) {
            userWidgetDefEntity.setEnabled(body.getEnabled());
        }
        userWidgetDefEntity.setDefinitionJson(body.getDefinitionJson());
        userWidgetDefEntity.setTitle(body.getTitle());
        userWidgetDefEntity.setRemark(body.getRemark());
        userWidgetDefEntity.setAppId(body.getAppId());
        save(userWidgetDefEntity);
        appDefElementI18nService.deleteAllI18n(null, userWidgetDefEntity.getUuid(), new BigDecimal("1.0"), IexportType.AppUserWidgetDef);
        if (CollectionUtils.isNotEmpty(body.getI18ns())) {
            for (AppDefElementI18nEntity e : body.getI18ns()) {
                e.setVersion(new BigDecimal("1.0"));
                e.setDefId(userWidgetDefEntity.getUuid());
                e.setApplyTo(IexportType.AppUserWidgetDef);
            }
        }
        appDefElementI18nService.saveAll(body.getI18ns());

    }

    @Override
    @Transactional
    public void updateEnabled(String uuid, boolean enabled) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        params.put("enabled", enabled);
        this.dao.updateByHQL("update AppUserWidgetDefEntity set enabled=:enabled where uuid=:uuid", params);
    }
}
