/*
 * @(#)2018-09-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bot.dao.BotRuleObjMappingDao;
import com.wellsoft.pt.bot.entity.BotRuleObjMappingEntity;
import com.wellsoft.pt.bot.service.BotRuleObjMappingService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表BOT_RULE_OBJ_MAPPING的service服务接口实现类
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
public class BotRuleObjMappingServiceImpl extends
        AbstractJpaServiceImpl<BotRuleObjMappingEntity, BotRuleObjMappingDao, String> implements
        BotRuleObjMappingService {


    @Override
    public List<BotRuleObjMappingEntity> listByConfUuid(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        return this.dao.listByHQL("from BotRuleObjMappingEntity where ruleConfUuid=:uuid order by seq asc", params);
    }

    @Override
    @Transactional
    public void deleteByRuleConfUuid(String uuid) {
        this.dao.deleteByRuleConfUuid(uuid);
    }
}
