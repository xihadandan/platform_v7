package com.wellsoft.pt.bot.support;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bot.exception.BotException;
import com.wellsoft.pt.bot.service.BotRuleConfService;
import com.wellsoft.pt.bot.service.BotRuleObjMappingIgnoreService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/19    chenq		2018/9/19		Create
 * </pre>
 */
public abstract class AbstractBoter implements Boter {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected BotParam botParam;

    protected BotRuleConfService botRuleConfService;

    protected BotRuleObjMappingIgnoreService botRuleColIgnoreService;

    protected FormDefinitionService formDefinitionService;

    protected DyFormFacade dyFormFacade;

    protected BoterPrepareData boterPrepareData;


    public AbstractBoter(BotParam param) {
        this.botParam = param;
        injectService();
        Stopwatch timer = Stopwatch.createStarted();
        try {
            this.prepare();
        } catch (Exception e) {
            logger.error("单据转换prepare阶段异常：", Throwables.getStackTraceAsString(e));
            throw new BotException("单据转换prepare阶段异常：" + e.getMessage(), e);
        } finally {
            logger.info("单据转换prepare阶段耗时：{}", timer.stop());
        }

    }


    private void injectService() {
        this.botRuleConfService = ApplicationContextHolder.getBean(
                BotRuleConfService.class);
        this.botRuleColIgnoreService = ApplicationContextHolder.getBean(BotRuleObjMappingIgnoreService.class);
        this.formDefinitionService = ApplicationContextHolder.getBean(FormDefinitionService.class);
        this.dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
    }


    @Override
    public BoterPrepareData getPrepareData() {
        return this.boterPrepareData;
    }

    /**
     * @return the botParam
     */
    public BotParam getBotParam() {
        return botParam;
    }

}
