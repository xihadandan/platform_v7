/*
 * @(#)6/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.fulltext.dto.FulltextSettingDto;
import com.wellsoft.pt.fulltext.entity.FulltextSettingEntity;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.service.FulltextRebuildIndexService;
import com.wellsoft.pt.fulltext.service.FulltextSettingService;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/16/25.1	    zhulh		6/16/25		    Create
 * </pre>
 * @date 6/16/25
 */
@Service
public class FulltextSettingFacadeServiceImpl extends AbstractApiFacade implements FulltextSettingFacadeService {

    private static final String CACHE_NAME = "Basic Data";

    @Autowired
    private FulltextSettingService fulltextSettingService;

    @Autowired
    private FulltextRebuildIndexService fulltextRebuildIndexService;

    /**
     * @param dto
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void saveDto(FulltextSettingDto dto) {
        FulltextSettingEntity entity = null;
        if (dto.getUuid() == null) {
            entity = fulltextSettingService.getByTypeAndSystem(dto.getType(), RequestSystemContextPathResolver.system());
            if (entity == null) {
                entity = new FulltextSettingEntity();
            }
        } else {
            entity = fulltextSettingService.getOne(dto.getUuid());
        }
        BeanUtils.copyProperties(dto, entity, SysEntity.BASE_FIELDS);
        String system = RequestSystemContextPathResolver.system();
        entity.setSystem(system);
        entity.setTenant(SpringSecurityUtils.getCurrentTenantId());

        fulltextSettingService.save(entity);

        if (StringUtils.equals(dto.getType(), "index") && StringUtils.isNotBlank(system)) {
            fulltextRebuildIndexService.rebuildIndex(getSettingBySystem(system));
        }
    }

    @Override
    public FulltextSettingDto getByTypeAndSystem(String type, String system) {
        FulltextSettingDto dto = new FulltextSettingDto();
        FulltextSettingEntity entity = fulltextSettingService.getByTypeAndSystem(type, system);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public FulltextSetting getSettingBySystem(String system) {
        FulltextSettingEntity entity = new FulltextSettingEntity();
        entity.setSystem(system);
        List<FulltextSettingEntity> entities = fulltextSettingService.listByEntity(entity);
        FulltextSettingEntity searchEntity = entities.stream().filter(e -> StringUtils.equals("search", e.getType())).findFirst().orElse(null);
        FulltextSettingEntity indexEntity = entities.stream().filter(e -> StringUtils.equals("index", e.getType())).findFirst().orElse(null);

        FulltextSetting fulltextSetting = new FulltextSetting();
        if (searchEntity != null && StringUtils.isNotBlank(searchEntity.getDefinitionJson())) {
            fulltextSetting = JsonUtils.json2Object(searchEntity.getDefinitionJson(), FulltextSetting.class);
        }
        if (BooleanUtils.isTrue(fulltextSetting.getEnabled()) && indexEntity != null && StringUtils.isNotBlank(indexEntity.getDefinitionJson())) {
            List<FulltextSetting.SearchScope> scopes = fulltextSetting.getScopeList();
            String resultDuplication = fulltextSetting.getResultDuplication();
            fulltextSetting = JsonUtils.json2Object(indexEntity.getDefinitionJson(), FulltextSetting.class);
            fulltextSetting.setEnabled(true);
            fulltextSetting.setScopeList(scopes);
            fulltextSetting.setResultDuplication(resultDuplication);
            BeanUtils.copyProperties(indexEntity, fulltextSetting);
        } else {
            fulltextSetting.setEnabled(false);
        }
        return fulltextSetting;
    }

    @Override
    public List<FulltextSetting> getAllFulltextSettings() {
        List<String> systems = fulltextSettingService.listSystem();
        List<FulltextSetting> fulltextSettings = Lists.newArrayList();
        for (String system : systems) {
            fulltextSettings.add(getSettingBySystem(system));
        }
        return fulltextSettings;
    }

    @Override
    public void updateRuleState(Long uuid, FulltextSetting.RebuildRule rebuildRule, String state) {
        if (uuid == null) {
            return;
        }
        FulltextSettingEntity entity = fulltextSettingService.getOne(uuid);
        if (entity == null) {
            return;
        }

        JSONObject jsonObject = new JSONObject(entity.getDefinitionJson());
        if (jsonObject.has("rebuildRules")) {
            JSONArray jsonArray = jsonObject.getJSONArray("rebuildRules");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rule = jsonArray.getJSONObject(i);
                if (rule.has("repeatType") && rule.has("executeTime")
                        && StringUtils.equals(rule.getString("repeatType"), rebuildRule.getRepeatType())
                        && StringUtils.equals(rule.getString("executeTime"), rebuildRule.getExecuteTime())) {
                    rule.put("state", state);
                }
            }
        }
        entity.setDefinitionJson(jsonObject.toString());
        fulltextSettingService.save(entity);
    }

}
