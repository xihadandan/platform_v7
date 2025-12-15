package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.pt.app.dao.AppCodeI18nDao;
import com.wellsoft.pt.app.dao.AppI18nLocaleDao;
import com.wellsoft.pt.app.entity.AppCodeI18nEntity;
import com.wellsoft.pt.app.entity.AppI18nLocaleEntity;
import com.wellsoft.pt.app.service.AppCodeI18nService;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.common.translate.service.TranslateService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

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
 * 2025年02月11日   chenq	 Create
 * </pre>
 */
@Service
public class AppCodeI18nServiceImpl extends AbstractJpaServiceImpl<AppCodeI18nEntity, AppCodeI18nDao, Long> implements AppCodeI18nService {

    @Autowired
    AppI18nLocaleDao appI18nLocaleDao;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    TranslateService translateService;


    @Override
    @Transactional
    public void deleteAppCodeI18nByCodeAndApplyTo(String code, String applyTo) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("code", code);
        params.put("applyTo", applyTo);
        this.dao.deleteByHQL("delete from AppCodeI18nEntity where code=:code and applyTo=:applyTo", params);
        this.updateCache();
    }

    @Override
    public List<AppCodeI18nEntity> getAppCodeI18nByCodeAndApplyTo(String code, String applyTo) {
        AppCodeI18nEntity example = new AppCodeI18nEntity();
        example.setCode(code);
        example.setApplyTo(applyTo);
        List<AppCodeI18nEntity> list = dao.listByEntity(example);
        return list;
    }

    @Override
    public AppCodeI18nEntity getLocaleAppCodeI18nByCodeAndApplyTo(String code, String applyTo, String locale) {
        AppCodeI18nEntity example = new AppCodeI18nEntity();
        example.setCode(code);
        example.setApplyTo(applyTo);
        example.setLocale(locale);
        List<AppCodeI18nEntity> list = dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<AppCodeI18nEntity> getAppCodeI18nByApplyTo(String applyTo) {
        return dao.listByFieldEqValue("applyTo", applyTo);
    }

    @Override
    public List<AppI18nLocaleEntity> getAllLocales() {
        return appI18nLocaleDao.listAllByOrderPage(null, "seq asc");
    }

    @Override
    public Set<String> getAllLocaleString() {
        Set<String> locales = Sets.newHashSet();
        List<AppI18nLocaleEntity> i18nLocaleEntities = getAllLocales();
        for (AppI18nLocaleEntity entity : i18nLocaleEntities) {
            locales.add(entity.getLocale());
        }
        return locales;
    }

    @Override
    public void updateCache() {
        List<AppCodeI18nEntity> list = this.listAll();
        if (CollectionUtils.isNotEmpty(list)) {
            StopWatch watch = new StopWatch("update app code i18n cache");
            Cache cache = cacheManager.getCache(ModuleID.I18N);
            watch.start("初始化缓存i18n数据");
            for (AppCodeI18nEntity i : list) {
                String key = String.format("%s%s%s%s%s%s%s", "i18n", Separator.COLON.getValue(), i.getApplyTo(), Separator.COLON.getValue(), i.getCode(), Separator.COLON.getValue(), i.getLocale());
                cache.put(key, i.getContent());
            }
            watch.stop();
            logger.info("{}", watch.prettyPrint());
        }
    }

    @Override
    @Transactional
    public void saveI18nsCode(List<AppCodeI18nEntity> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<AppCodeI18nEntity> saves = Lists.newArrayList();
            for (AppCodeI18nEntity temp : list) {
                AppCodeI18nEntity entity = temp.getUuid() == null ? new AppCodeI18nEntity() : getOne(temp.getUuid());
                if (temp.getUuid() == null) {
                    AppCodeI18nEntity find = this.getLocaleAppCodeI18nByCodeAndApplyTo(temp.getCode(), temp.getApplyTo(), temp.getLocale());
                    if (find != null) {
                        entity = find;
                        BeanUtils.copyProperties(temp, entity, ArrayUtils.addAll(find.BASE_FIELDS, "code", "applyTo", "locale"));
                    } else {
                        BeanUtils.copyProperties(temp, entity);
                    }
                } else {
                    BeanUtils.copyProperties(temp, entity, entity.BASE_FIELDS);
                }
                saves.add(entity);
            }
            saveAll(saves);
        }
        this.updateCache();
    }

    @Override
    public String getLocaleSortLetters(String locale) {
        AppI18nLocaleEntity entity = appI18nLocaleDao.getOneByFieldEq("locale", locale);
        if (entity != null) {
            return entity.getSortLetters();
        }
        return null;
    }

    @Override
    @Transactional
    public void addLocale(AppI18nLocaleEntity locale) {
        AppI18nLocaleEntity entity = appI18nLocaleDao.getOneByFieldEq("locale", locale.getLocale());
        if (entity != null) {
            throw new BusinessException("已存在的国际化语言");
        }
        entity = new AppI18nLocaleEntity();
        BeanUtils.copyProperties(locale, entity, entity.BASE_FIELDS);
        appI18nLocaleDao.save(entity);
    }

    @Override
    public AppI18nLocaleEntity getLocale(String locale) {
        AppI18nLocaleEntity entity = new AppI18nLocaleEntity();
        entity.setLocale(locale);
        List<AppI18nLocaleEntity> entities = appI18nLocaleDao.listByEntity(entity);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    @Override
    public void translateAllToLocale(String fromLocale, String toLocale) {
        //TODO: 多线程翻译
    }

    @Override
    public long countWordsToTranslate(String fromLocale) {
        long num = this.dao.countByHQL("from AppCodeI18nEntity where locale = :locale", ImmutableMap.<String, Object>builder().put("locale", fromLocale).build());
        return num;
    }

}
