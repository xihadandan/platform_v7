package com.wellsoft.pt.bot.facade.service;

import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.service.Facade;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.bot.dto.BotRuleConfDto;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotResult;

/**
 * Description: 单据转换服务，提供其他模块统一的调用入口
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
@GroovyUseable
public interface BotFacadeService extends Facade {


    /**
     * 开始单据转换
     *
     * @param param
     * @return
     */
    @Description("单据转换接口方法")
    BotResult startBot(BotParam param);

    /**
     * 根据单据转换规则ID获取配置
     *
     * @param ruleId
     * @return
     */
    @Description("根据单据转换规则ID获取配置")
    BotRuleConfDto getRuleConfById(String ruleId);

}
