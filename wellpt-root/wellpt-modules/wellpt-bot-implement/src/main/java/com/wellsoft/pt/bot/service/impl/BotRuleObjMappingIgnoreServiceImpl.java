package com.wellsoft.pt.bot.service.impl;

import com.wellsoft.pt.bot.dao.BotRuleObjMappingIgnoreDao;
import com.wellsoft.pt.bot.entity.BotRuleObjMappginIgnoreEntity;
import com.wellsoft.pt.bot.service.BotRuleObjMappingIgnoreService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年10月13日   chenq	 Create
 * </pre>
 */
@Service
public class BotRuleObjMappingIgnoreServiceImpl extends AbstractJpaServiceImpl<BotRuleObjMappginIgnoreEntity, BotRuleObjMappingIgnoreDao, String> implements BotRuleObjMappingIgnoreService {
    @Override
    public List<BotRuleObjMappginIgnoreEntity> listByConfUuid(String uuid) {
        return this.dao.listByFieldEqValue("ruleConfUuid", uuid);
    }

    @Override
    @Transactional
    public void deleteByConfUuid(String uuid) {
        List list = this.listByConfUuid(uuid);
        if (CollectionUtils.isNotEmpty(list)) {
            this.dao.deleteByEntities(list);
        }
    }
}
