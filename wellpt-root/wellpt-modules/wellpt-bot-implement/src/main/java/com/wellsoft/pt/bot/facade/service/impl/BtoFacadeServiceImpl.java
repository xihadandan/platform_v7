package com.wellsoft.pt.bot.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.bot.dto.BotRuleConfDto;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.facade.service.BotRuleConfFacadeService;
import com.wellsoft.pt.bot.service.BotRuleConfService;
import com.wellsoft.pt.bot.support.BotFactory;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bot.support.Boter;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
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
@Service("botFacadeService")
public class BtoFacadeServiceImpl extends AbstractApiFacade implements BotFacadeService {

    @Autowired
    BotRuleConfService botRuleConfService;

    @Autowired
    DyFormFacade dyFormFacade;

    @Autowired
    FormDefinitionService formDefinitionService;

    @Autowired
    BotRuleConfFacadeService botRuleConfFacadeService;

    @Autowired
    BotFactory botFactory;


    @Override
    public BotResult startBot(BotParam param) {
        Boter boter = botFactory.buildBot(param);
        BotResult botResult = botFactory.startBot(boter);
        return botResult;
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bot.facade.service.BotFacadeService#getRuleConfById(java.lang.String)
     */
    @Override
    public BotRuleConfDto getRuleConfById(String ruleId) {
        return botRuleConfService.getDetailById(ruleId);
    }

}
