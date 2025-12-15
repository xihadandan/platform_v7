package com.wellsoft.pt.bot.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.bot.dto.BotRuleConfDto;
import com.wellsoft.pt.bot.entity.BotRuleConfEntity;

import java.util.List;

/**
 * Description: 提供前端调用的服务接口封装
 *
 * @author chenq
 * @date 2018/9/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/14    chenq		2018/9/14		Create
 * </pre>
 */
public interface BotRuleConfFacadeService extends Facade {

    void saveBotRuleConfig(BotRuleConfDto dto);

    void deleteRuleConfig(List<String> uuids);


    BotRuleConfDto getBotRuleConfigDetail(String uuid);


    Select2QueryData loadSelectData(Select2QueryInfo queryInfo);

    List<BotRuleConfEntity> listAll();


    public Select2QueryData querySelectDataFromBotRuleConfig(Select2QueryInfo select2QueryInfo);

    public Select2QueryData loadSelectDataFromBotRuleConfig(Select2QueryInfo select2QueryInfo);


}
