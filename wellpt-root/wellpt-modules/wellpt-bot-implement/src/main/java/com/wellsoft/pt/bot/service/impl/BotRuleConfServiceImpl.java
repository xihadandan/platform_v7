/*
 * @(#)2018-09-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.bot.dao.BotRuleConfDao;
import com.wellsoft.pt.bot.dto.*;
import com.wellsoft.pt.bot.entity.*;
import com.wellsoft.pt.bot.service.*;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表BOT_RULE_CONF的service服务接口实现类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-09-14.1	chenq		2018-09-14		Create
 * </pre>
 * @date 2018-09-14
 */
@Service
public class BotRuleConfServiceImpl extends
        AbstractJpaServiceImpl<BotRuleConfEntity, BotRuleConfDao, String> implements
        BotRuleConfService {
    private static final String DYFORM_CACHE_NAME = "Dynamic Table";

    @Autowired
    BotRuleObjMappingService botRuleObjMappingService;

    @Autowired
    BotRuleObjRelaService botRuleObjRelaService;


    @Autowired
    BotRuleObjRelaMappingService botRuleObjRelaMappingService;

    @Autowired
    DyFormFacade dyFormFacade;

    @Autowired
    FormDefinitionService formDefinitionService;

    @Autowired
    BotRuleObjMappingIgnoreService botRuleColIgnoreService;

    @Override
    @Transactional
    @CacheEvict(value = DYFORM_CACHE_NAME, allEntries = true)
    public void save(BotRuleConfDto dto) {
        BotRuleConfEntity confEntity = StringUtils.isNotBlank(dto.getUuid()) ? this.getOne(dto.getUuid()) : new BotRuleConfEntity();
        confEntity.setRuleName(dto.getRuleName());
        confEntity.setIsPersist(dto.getIsPersist());
        confEntity.setScriptAfterTrans(dto.getScriptAfterTrans());
        confEntity.setScriptBeforeTrans(dto.getScriptBeforeTrans());
        confEntity.setTargetObjId(dto.getTargetObjId());
        confEntity.setTargetObjName(dto.getTargetObjName());
        confEntity.setTransferType(dto.getTransferType());
        confEntity.setSourceObjId(dto.getSourceObjId());
        confEntity.setId(dto.getId());
        confEntity.setAutoMapSameColumn(dto.getAutoMapSameColumn());
        if (StringUtils.isBlank(dto.getUuid())) {
            confEntity.setSystem(RequestSystemContextPathResolver.system());
            confEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        save(confEntity);

        List<BotRuleObjMappingEntity> updateObjMappings = Lists.newArrayList();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            List<BotRuleObjMappingEntity> exists = botRuleObjMappingService.listByConfUuid(dto.getUuid());
            Map<String, BotRuleObjMappingEntity> uuidEntityMap = Maps.newHashMap();
            for (BotRuleObjMappingEntity entity : exists) {
                uuidEntityMap.put(entity.getUuid(), entity);
            }
            for (BotRuleObjMappingDto md : dto.getObjMappingDtos()) {
                if (StringUtils.isBlank(md.getUuid())) {
                    //新增的数据
                    BotRuleObjMappingEntity mappingEntity = new BotRuleObjMappingEntity();
                    BeanUtils.copyProperties(md, mappingEntity);
                    mappingEntity.setRuleConfUuid(confEntity.getUuid());
                    updateObjMappings.add(mappingEntity);
                } else {
                    //更新的数据
                    BeanUtils.copyProperties(md, uuidEntityMap.get(md.getUuid()), IdEntity.BASE_FIELDS);
                    uuidEntityMap.get(md.getUuid()).setRuleConfUuid(confEntity.getUuid());
                    updateObjMappings.add(uuidEntityMap.get(md.getUuid()));
                    uuidEntityMap.remove(md.getUuid());
                }
            }
            if (!uuidEntityMap.isEmpty()) {//删除的数据
                botRuleObjMappingService.deleteByUuids(Lists.newArrayList(uuidEntityMap.keySet()));
            }
        } else {
            for (BotRuleObjMappingDto md : dto.getObjMappingDtos()) {
                //新增的数据
                BotRuleObjMappingEntity mappingEntity = new BotRuleObjMappingEntity();
                BeanUtils.copyProperties(md, mappingEntity);
                mappingEntity.setRuleConfUuid(confEntity.getUuid());
                updateObjMappings.add(mappingEntity);
            }
        }

        if (CollectionUtils.isNotEmpty(updateObjMappings)) {
            botRuleObjMappingService.saveAll(updateObjMappings);
        }

        if (dto.getRelaDto() != null) {
            dto.getRelaDto().setRuleConfUuid(confEntity.getUuid());
            botRuleObjRelaService.save(dto.getRelaDto());
        }

        botRuleColIgnoreService.deleteByConfUuid(dto.getUuid());
        if (CollectionUtils.isNotEmpty(dto.getIgnoreMappings())) {
            List<BotRuleObjMappginIgnoreEntity> ignoreEntities = Lists.newArrayList();
            for (BotRuleObjMappingIgnoreDto ignore : dto.getIgnoreMappings()) {
                BotRuleObjMappginIgnoreEntity ignoreEntity = new BotRuleObjMappginIgnoreEntity();
                BeanUtils.copyProperties(ignore, ignoreEntity);
                ignoreEntity.setRuleConfUuid(confEntity.getUuid());
                ignoreEntities.add(ignoreEntity);
            }
            botRuleColIgnoreService.saveAll(ignoreEntities);
        }

    }

    @Override
    @Transactional
    @CacheEvict(value = DYFORM_CACHE_NAME, allEntries = true)
    public void deleteBotRuleConf(List<String> uuids) {
        if (CollectionUtils.isNotEmpty(uuids)) {
            for (String uuid : uuids) {
                botRuleObjMappingService.deleteByRuleConfUuid(uuid);
                botRuleObjRelaService.deleteByRuleConfUuid(uuid);
                delete(uuid);
            }
        }
    }

    @Override
    public BotRuleConfDto getDetailByUuid(String uuid) {
        BotRuleConfDto confDto = new BotRuleConfDto();
        BotRuleConfEntity confEntity = this.getOne(uuid);
        BeanUtils.copyProperties(confEntity, confDto);
        List<BotRuleObjMappingEntity> objMappingEntities = botRuleObjMappingService.listByConfUuid(
                uuid);
        if (CollectionUtils.isNotEmpty(objMappingEntities)) {
            for (BotRuleObjMappingEntity mappingEntity : objMappingEntities) {
                BotRuleObjMappingDto mappingDto = new BotRuleObjMappingDto();
                BeanUtils.copyProperties(mappingEntity, mappingDto);
                confDto.getObjMappingDtos().add(mappingDto);
            }
        }

        BotRuleObjRelaEntity relaEntity = botRuleObjRelaService.getByConfUuid(uuid);
        if (relaEntity != null) {
            BotRuleObjRelaDto relaDto = new BotRuleObjRelaDto();
            BeanUtils.copyProperties(relaEntity, relaDto);
            List<BotRuleObjRelaMappingEntity> relaMappingEntities = botRuleObjRelaMappingService.listByRelaUuid(
                    relaEntity.getUuid());
            if (CollectionUtils.isNotEmpty(relaMappingEntities)) {
                for (BotRuleObjRelaMappingEntity relaMappingEntity : relaMappingEntities) {
                    BotRuleObjRelaMappingDto relaMappingDto = new BotRuleObjRelaMappingDto();
                    BeanUtils.copyProperties(relaMappingEntity, relaMappingDto);
                    relaDto.getRelaMappingDtos().add(relaMappingDto);
                }
            }
            confDto.setRelaDto(relaDto);
        }

        List<BotRuleObjMappginIgnoreEntity> ignoreEntities = botRuleColIgnoreService.listByConfUuid(uuid);
        if (CollectionUtils.isNotEmpty(ignoreEntities)) {
            for (BotRuleObjMappginIgnoreEntity ignoreEntity : ignoreEntities) {
                BotRuleObjMappingIgnoreDto ignoreDto = new BotRuleObjMappingIgnoreDto();
                BeanUtils.copyProperties(ignoreEntity, ignoreDto);
                confDto.getIgnoreMappings().add(ignoreDto);
            }
        }
        return confDto;
    }

    @Override
    @Cacheable(value = DYFORM_CACHE_NAME)
    public BotRuleConfDto getDetailById(String ruleId) {
        List<BotRuleConfEntity> entities = this.dao.listByFieldEqValue("id", ruleId);
        if (CollectionUtils.isNotEmpty(entities)) {
            return this.getDetailByUuid(entities.get(0).getUuid());
        }
        return null;
    }

    @Override
    @Cacheable(value = DYFORM_CACHE_NAME)
    public BotRuleConfEntity getById(String ruleId) {
        List<BotRuleConfEntity> entities = this.dao.listByFieldEqValue("id", ruleId);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    @Override
    public List<BotRuleConfEntity> listByTransferType(Integer type) {
        BotRuleConfEntity entity = new BotRuleConfEntity();
        if (type != null) {
            entity.setTransferType(type);
            // return this.dao.listByFieldEqValue("transferType", type);
        }
        entity.setSystem(RequestSystemContextPathResolver.system());
        return this.listByEntity(entity);// this.listAll();
    }


}
