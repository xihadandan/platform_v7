/*
 * @(#)2018-09-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.bot.dao.BotRuleObjRelaDao;
import com.wellsoft.pt.bot.dto.BotRuleObjRelaDto;
import com.wellsoft.pt.bot.dto.BotRuleObjRelaMappingDto;
import com.wellsoft.pt.bot.entity.BotRuleObjRelaEntity;
import com.wellsoft.pt.bot.entity.BotRuleObjRelaMappingEntity;
import com.wellsoft.pt.bot.service.BotRuleObjRelaMappingService;
import com.wellsoft.pt.bot.service.BotRuleObjRelaService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表BOT_RULE_OBJ_RELA的service服务接口实现类
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
public class BotRuleObjRelaServiceImpl extends
        AbstractJpaServiceImpl<BotRuleObjRelaEntity, BotRuleObjRelaDao, String> implements
        BotRuleObjRelaService {

    @Autowired
    BotRuleObjRelaMappingService botRuleObjRelaMappingService;

    @Override
    public BotRuleObjRelaEntity getByConfUuid(String uuid) {
        List<BotRuleObjRelaEntity> relaEntities = this.dao.listByFieldEqValue("ruleConfUuid", uuid);
        return CollectionUtils.isNotEmpty(relaEntities) ? relaEntities.get(0) : null;
    }

    @Override
    @Transactional
    public void save(BotRuleObjRelaDto dto) {
        BotRuleObjRelaEntity relaEntity = StringUtils.isNotBlank(dto.getUuid()) ? this.getOne(dto.getUuid()) : new BotRuleObjRelaEntity();
        if (StringUtils.isNotBlank(dto.getRelaObjId())) {
            relaEntity.setRelaObjId(dto.getRelaObjId());
            relaEntity.setRelaObjName(dto.getRelaObjName());
            relaEntity.setRuleConfUuid(dto.getRuleConfUuid());
            save(relaEntity);
        } else if (StringUtils.isNotBlank(dto.getUuid())) {
            delete(dto.getUuid());
        }
        List<BotRuleObjRelaMappingEntity> updateMappingEntities = Lists.newArrayList();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            Map<String, BotRuleObjRelaMappingEntity> uuidEntities = Maps.newHashMap();
            List<BotRuleObjRelaMappingEntity> exists = botRuleObjRelaMappingService.listByRelaUuid(dto.getUuid());
            for (BotRuleObjRelaMappingEntity me : exists) {
                uuidEntities.put(me.getUuid(), me);
            }
            if (CollectionUtils.isNotEmpty(dto.getRelaMappingDtos())) {
                for (BotRuleObjRelaMappingDto md : dto.getRelaMappingDtos()) {
                    if (StringUtils.isNotBlank(md.getUuid())) {
                        // 更新数据
                        BeanUtils.copyProperties(md, uuidEntities.get(md.getUuid()), IdEntity.BASE_FIELDS);
                        uuidEntities.get(md.getUuid()).setRuleObjRelaUuid(relaEntity.getUuid());
                        updateMappingEntities.add(uuidEntities.get(md.getUuid()));
                        uuidEntities.remove(md.getUuid());
                    } else {
                        BotRuleObjRelaMappingEntity entity = new BotRuleObjRelaMappingEntity();
                        BeanUtils.copyProperties(md, entity);
                        entity.setRuleObjRelaUuid(relaEntity.getUuid());
                        updateMappingEntities.add(entity);
                    }
                }
            }
            if (!uuidEntities.isEmpty()) {
                botRuleObjRelaMappingService.deleteByUuids(Lists.newArrayList(uuidEntities.keySet()));
            }
        } else {
            for (BotRuleObjRelaMappingDto md : dto.getRelaMappingDtos()) {
                BotRuleObjRelaMappingEntity entity = new BotRuleObjRelaMappingEntity();
                BeanUtils.copyProperties(md, entity);
                entity.setRuleObjRelaUuid(relaEntity.getUuid());
                updateMappingEntities.add(entity);
            }
        }
        if (CollectionUtils.isNotEmpty(updateMappingEntities)) {
            botRuleObjRelaMappingService.saveAll(updateMappingEntities);
        }
    }

    @Override
    @Transactional
    public void deleteByRuleConfUuid(String uuid) {
        BotRuleObjRelaEntity entity = getByConfUuid(uuid);
        if (entity != null) {
            botRuleObjRelaMappingService.deleteByEntities(
                    botRuleObjRelaMappingService.listByRelaUuid(entity.getUuid()));
            delete(entity);
        }
    }
}
