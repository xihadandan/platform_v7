package com.wellsoft.pt.dyform.implement.definition.cache;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldDefinition;
import com.wellsoft.pt.dyform.implement.definition.entity.FormCommonFieldRef;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.event.FormDefinitionChangedEvent;
import com.wellsoft.pt.dyform.implement.definition.service.FormCommonFieldDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.service.FormCommonFieldRefService;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 动态表单缓存管理
 * 在DyFormDefinitionServiceImpl类中进行了初始化
 *
 * @author hunt
 */
public class DyformCacheUtils {
    private static final String CACHE_KEY_defintionsKeyAsUuid = "defintionsKeyAsUuid_";
    private static final String CACHE_KEY_defintionsKeyAsId = "defintionsKeyAsId_";
    private static final String CACHE_KEY_formUuidKeyAsTlbName = "defintionsKeyAsTblName_";
    private static final String CACHE_KEY_fieldsKeyAsId = "fieldsKeyAsId_";
    private static final String CACHE_KEY_querySubformDataSql = "querySubformDataSql_";
    private static Logger logger = LoggerFactory.getLogger(DyformCacheUtils.class);
    private static Cache cache = null;// cacheManager.getCache(ModuleID.DYTABLE);
    private static FormDefinitionService formDefinitionService = null;
    private static FormCommonFieldRefService formCommonFieldRefService = null;
    private static FormCommonFieldDefinitionService formCommonFieldDefinitionService = null;

    // 初始化缓存
    static {
        DyformCacheUtils.cache = ApplicationContextHolder.getBean(CacheManager.class).getCache(ModuleID.DYTABLE);
        DyformCacheUtils.formDefinitionService = ApplicationContextHolder.getBean(FormDefinitionService.class);
        DyformCacheUtils.formCommonFieldRefService = ApplicationContextHolder.getBean(FormCommonFieldRefService.class);
        DyformCacheUtils.formCommonFieldDefinitionService = ApplicationContextHolder
                .getBean(FormCommonFieldDefinitionService.class);
    }

    public static void updateOrAdd(DyFormFormDefinition def) {
        clearCache(def);// 清除缓存
        ApplicationContextHolder.getApplicationContext().publishEvent(new FormDefinitionChangedEvent(def.getUuid()));
    }

    // 清除缓存
    public static void clearCache(DyFormFormDefinition def) {
        if (def == null) {
            return;
        }
        cache.evict(getCacheKeyAsFormId(def.getId()));
        cache.evict(getCacheKeyAsFormUuid(def.getUuid()));
        List<String> subformUuids = ((FormDefinition) def).doGetFormDefinitionHandler().getFormUuidsOfSubform();
        for (String subformUuid : subformUuids) {
            cache.evict(getCacheKeyAsQuerySubformDataSql(def.getUuid(), subformUuid));
            cache.evict(getCacheKeyAsQuerySubformDataSql(def.getUuid(), subformUuid + ":full"));
        }
    }

    public static String getCacheKeyAsFormUuid(String formUuid) {
        return CACHE_KEY_defintionsKeyAsUuid + formUuid;
    }

    public static String getCacheKeyAsFormId(String formId) {
        return CACHE_KEY_defintionsKeyAsId + formId;
    }

    public static String getCacheKeyAsQuerySubformDataSql(String formUuid, String formUuidOfSubform) {
        return CACHE_KEY_querySubformDataSql + formUuid + "_" + formUuidOfSubform;
    }

    public static void delete(DyFormFormDefinition def) {
        clearCache(def);// 清除缓存
    }

    public static DyFormFormDefinition getDyformDefinitionByUuid(String formUuid) {
        Stopwatch timer = Stopwatch.createStarted();
        String cacheKey = getCacheKeyAsFormUuid(formUuid);
        DyFormFormDefinition dydf = (DyFormFormDefinition) getValueFromCache(cacheKey);
        if (dydf == null) {
            dydf = formDefinitionService.findDyFormFormDefinitionByFormUuid(formUuid);
            setValueToCache(cacheKey, dydf);
        }
        logger.debug("formUuid={} -> 获取缓存表单定义耗时:{}", formUuid, timer.stop());
        return dydf;
    }

    /**
     * 更新缓存
     *
     * @param formUuid
     * @param dyformdef
     */
    public static void updateCache(DyFormFormDefinition dyformdef) {
        clearCache(dyformdef);
    }

    public static DyFormFormDefinition getDyformDefinitionOfMaxVersionById(String formId) {
        Stopwatch timer = Stopwatch.createStarted();
        String cacheKey = getCacheKeyAsFormId(formId);
        DyFormFormDefinition dydf = (DyFormFormDefinition) getValueFromCache(cacheKey);
        if (dydf == null) {// 缓存中找不到，则重新缓存
            dydf = formDefinitionService.getDyFormFormDefinitionOfMaxVersionById(formId);
            setValueToCache(cacheKey, dydf);
        }
        logger.debug("formId={} -> 获取缓存表单定义耗时:{}", formId, timer.stop());
        return dydf;
    }

    public static Object getValueFromCache(String key) {
        return cache.getValue(key);
    }

    public static void setValueToCache(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 清理缓存及关联单据
     *
     * @param fieldUuid
     */
    public static void evictFieldJSONObject(String fieldUuid) {
        String cacheKey = CACHE_KEY_fieldsKeyAsId + fieldUuid;
        cache.evict(cacheKey);
        List<FormCommonFieldRef> refs = formCommonFieldRefService.getFormCommonFieldByFieldUuid(fieldUuid);
        for (FormCommonFieldRef ref : refs) {
            if (StringUtils.isBlank(ref.getFormUuid())) {
                continue;
            }
            DyformCacheUtils.clearCache(DyformCacheUtils.getDyformDefinitionByUuid(ref.getFormUuid()));
        }
    }

    /**
     * 获取字段定义
     *
     * @param fieldUuid
     * @return
     */
    public static JSONObject getFieldJSONObject(String fieldUuid) {
        String cacheKey = CACHE_KEY_fieldsKeyAsId + fieldUuid;
        FormCommonFieldDefinition fieldDef = (FormCommonFieldDefinition) cache.getValue(cacheKey);
        if (fieldDef == null) {
            synchronized (DyformCacheUtils.class) {
                fieldDef = (FormCommonFieldDefinition) cache.getValue(cacheKey);
                if (fieldDef == null) {
                    cache.put(cacheKey,
                            fieldDef = formCommonFieldDefinitionService.getDyformCommonFieldByUUID(fieldUuid));
                }

            }
        }
        return fieldDef == null ? null : fieldDef.getDefinitionJsonObject();
    }

    /**
     * @param formUuid
     * @return
     */
    public static String getQuerySubformDataSqlByUuid(String formUuid, String formUuidOfSubform) {
        String cacheKey = getCacheKeyAsQuerySubformDataSql(formUuid, formUuidOfSubform);
        String querySubformDataSql = (String) getValueFromCache(cacheKey);
        return querySubformDataSql;
    }

    /**
     * @param formUuid
     * @param sql
     */
    public static void setQuerySubformDataSqlByUuid(String formUuid, String formUuidOfSubform, String sql) {
        String cacheKey = getCacheKeyAsQuerySubformDataSql(formUuid, formUuidOfSubform);
        setValueToCache(cacheKey, sql);
    }

    /**
     * @param tblName
     * @return
     */
    public static String getFormUuidByTlbName(String tblName) {
        String cacheKey = CACHE_KEY_formUuidKeyAsTlbName + tblName;
        return (String) cache.getValue(cacheKey);
    }

    /**
     * @param tblName
     * @param formUuid
     */
    public static void setFormUuidByTlbName(String tblName, String formUuid) {
        String cacheKey = CACHE_KEY_formUuidKeyAsTlbName + tblName;
        cache.put(cacheKey, formUuid);
    }
}
