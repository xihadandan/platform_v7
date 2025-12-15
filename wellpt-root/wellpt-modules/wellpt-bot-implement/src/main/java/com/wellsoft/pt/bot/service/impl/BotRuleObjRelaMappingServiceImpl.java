/*
 * @(#)2018-09-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.service.impl;

import com.wellsoft.pt.bot.dao.BotRuleObjRelaMappingDao;
import com.wellsoft.pt.bot.entity.BotRuleObjRelaMappingEntity;
import com.wellsoft.pt.bot.service.BotRuleObjRelaMappingService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 数据库表BOT_RULE_OBJ_RELA_MAPPING的service服务接口实现类
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
public class BotRuleObjRelaMappingServiceImpl extends
        AbstractJpaServiceImpl<BotRuleObjRelaMappingEntity, BotRuleObjRelaMappingDao, String> implements
        BotRuleObjRelaMappingService {


    @Override
    public List<BotRuleObjRelaMappingEntity> listByRelaUuid(String uuid) {
        return this.dao.listByFieldEqValue("ruleObjRelaUuid", uuid);
    }
}
