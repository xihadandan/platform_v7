package com.wellsoft.pt.bot.support;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bot.entity.BotRuleConfEntity;
import com.wellsoft.pt.bot.exception.BotException;
import com.wellsoft.pt.bot.service.BotRuleConfService;
import com.wellsoft.pt.dyform.implement.data.exceptions.FormDataValidateException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/18    chenq		2018/9/18		Create
 * </pre>
 */
@Component
public class BotFactory {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public Boter buildBot(BotParam param) {
        if (param == null) {
            throw new BotException("单据转换参数不为空");
        }
        if (StringUtils.isBlank(param.getRuleId())) {
            throw new BotException("需要指定单据转换规则ID");
        }

        BotRuleConfEntity confEntity = ApplicationContextHolder.getBean(
                BotRuleConfService.class).getById(param.getRuleId());

        //表单单据的转换
        if (confEntity == null) {
            throw new BotException("需要指定单据转换来源数据");
        }
        if (BotRuleConfEntity.TRANS_TYPE_FORM_2_FORM.equals(
                confEntity.getTransferType()) && CollectionUtils.isEmpty(param.getFroms())) {
            throw new BotException("需要指定单据转换来源数据[froms]参数");
        }
        if (BotRuleConfEntity.TRANS_TYPE_JSON_2_FORM.equals(
                confEntity.getTransferType()) && MapUtils.isEmpty(param.getJsonBody())) {
            throw new BotException("需要指定单据转换来源数据[jsonBody]参数");
        }

        if (BotRuleConfEntity.TRANS_TYPE_FORM_2_FORM.equals(confEntity.getTransferType())
                || BotRuleConfEntity.TRANS_TYPE_JSON_2_FORM.equals(confEntity.getTransferType())) {
            return new DyFormBoter(param);
        }
        //TODO:数据表的转换

        return null;


    }


    @Transactional
    public BotResult startBot(Boter boter) {
        Stopwatch timer = Stopwatch.createStarted();
        try {
            return boter.startBot();
        } catch (Exception e) {
            logger.error("单据转换开始进行转换数据处理异常：", Throwables.getStackTraceAsString(e));
            if (e instanceof FormDataValidateException) {
                throw (FormDataValidateException) e;
            } else {
                throw new BotException(e);
            }
        } finally {
            logger.info("单据转换开始进行转换数据处理，耗时：{}", timer.stop());
        }

    }


}
