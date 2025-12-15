/*
 * @(#)2018-09-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bot.dao.BotRuleObjRelaDao;
import com.wellsoft.pt.bot.entity.BotRuleObjRelaEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Map;


/**
 * Description: 数据库表BOT_RULE_OBJ_RELA的DAO接口实现类
 *
 * @author chenq
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-09-14.1    chenq        2018-09-14		Create
 * </pre>
 * @date 2018-09-14
 */
@Repository
public class BotRuleObjRelaDaoImpl extends
        AbstractJpaDaoImpl<BotRuleObjRelaEntity, String> implements BotRuleObjRelaDao {


    @Override
    public void deleteByRuleConfUuid(String uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("confUuid", uuid);
        this.deleteByHQL("delete from BotRuleObjRelaEntity where ruleConfUuid=:confUuid", param);
    }
}

