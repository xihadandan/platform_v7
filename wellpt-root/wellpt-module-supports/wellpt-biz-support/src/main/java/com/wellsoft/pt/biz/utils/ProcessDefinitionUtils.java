/*
 * @(#)10/14/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.utils;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.entity.BizProcessDefinitionEntity;
import com.wellsoft.pt.biz.enums.EnumDefinitionTemplateType;
import com.wellsoft.pt.biz.service.BizDefinitionTemplateService;
import com.wellsoft.pt.biz.service.BizProcessDefinitionService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.cache.CacheManager;
import org.springframework.cache.Cache;

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
 * 10/14/22.1	zhulh		10/14/22		Create
 * </pre>
 * @date 10/14/22
 */
public class ProcessDefinitionUtils {
    private static final String BIZ_PROCESS_CACHE_ID = "BIZ_PROCESS";

    private ProcessDefinitionUtils() {
    }

    public static ProcessDefinitionJsonParser getJsonParserByProcessDefUuid(String processDefUuid) {
        Cache cache = getCache();
        String cacheKey = getCacheKey(processDefUuid);
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            return (ProcessDefinitionJsonParser) valueWrapper.get();
        }

        BizProcessDefinitionService bizProcessDefinitionService = ApplicationContextHolder.getBean(BizProcessDefinitionService.class);
        ProcessDefinitionJson processDefinitionJson = bizProcessDefinitionService.getProcessDefinitionJsonByUuid(processDefUuid);

        ProcessDefinitionJsonParser parser = new ProcessDefinitionJsonParser(processDefinitionJson);

        cache.put(cacheKey, parser);
        return parser;
    }

    public static ProcessDefinitionJsonParser getJsonParserByProcessDefId(String processDefId) {
        Cache cache = getCache();
        String cacheKey = getCacheKey(processDefId);
        Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            return (ProcessDefinitionJsonParser) valueWrapper.get();
        }

        BizProcessDefinitionService bizProcessDefinitionService = ApplicationContextHolder.getBean(BizProcessDefinitionService.class);
        ProcessDefinitionJson processDefinitionJson = bizProcessDefinitionService.getProcessDefinitionJsonById(processDefId);
        ProcessDefinitionJsonParser parser = new ProcessDefinitionJsonParser(processDefinitionJson);

        cache.put(cacheKey, parser);
        return parser;
    }

    private static Cache getCache() {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        return cacheManager.getCache(BIZ_PROCESS_CACHE_ID);
    }

    private static String getCacheKey(String key) {
        return BIZ_PROCESS_CACHE_ID + Separator.UNDERLINE.getValue() + key;
    }

    public static void clearCache(String processDefUuid) {
        BizProcessDefinitionService processDefinitionService = ApplicationContextHolder.getBean(BizProcessDefinitionService.class);
        clearCache(processDefinitionService.getOne(processDefUuid));
    }

    public static void clearCache(BizProcessDefinitionEntity entity) {
        if (entity == null) {
            getCache().clear();
            return;
        }

        BizDefinitionTemplateService definitionTemplateService = ApplicationContextHolder.getBean(BizDefinitionTemplateService.class);
        List<String> templateTypes = Lists.newArrayList(EnumDefinitionTemplateType.ItemDefinition.getValue(), EnumDefinitionTemplateType.NodeDefinition.getValue());
        boolean existsTemplateRef = definitionTemplateService.existsByProcessDefUuidAndTypes(entity.getUuid(), templateTypes);
        if (existsTemplateRef) {
            getCache().clear();
        } else {
            getCache().evict(getCacheKey(entity.getUuid()));
            getCache().evict(getCacheKey(entity.getId()));
        }
    }
}
