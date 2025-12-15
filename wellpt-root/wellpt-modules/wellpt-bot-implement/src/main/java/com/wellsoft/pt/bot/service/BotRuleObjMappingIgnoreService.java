package com.wellsoft.pt.bot.service;

import com.wellsoft.pt.bot.dao.BotRuleObjMappingIgnoreDao;
import com.wellsoft.pt.bot.entity.BotRuleObjMappginIgnoreEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface BotRuleObjMappingIgnoreService extends JpaService<BotRuleObjMappginIgnoreEntity, BotRuleObjMappingIgnoreDao, String> {
    List<BotRuleObjMappginIgnoreEntity> listByConfUuid(String uuid);

    void deleteByConfUuid(String uuid);
}
