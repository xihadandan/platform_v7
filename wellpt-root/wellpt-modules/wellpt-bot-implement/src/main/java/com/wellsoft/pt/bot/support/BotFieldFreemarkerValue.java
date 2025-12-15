package com.wellsoft.pt.bot.support;

import com.wellsoft.pt.bot.exception.BotException;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

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
public class BotFieldFreemarkerValue extends AbstractBotFieldValue {


    public BotFieldFreemarkerValue(
            Map<String, ?> data,
            Object fromFieldValue, Object toFieldValue, String script) {
        super(data, fromFieldValue, toFieldValue, script);
    }

    @Override
    public Object value() {
        try {
            if (StringUtils.isNotBlank(this.script)) {
                return TemplateEngineFactory.getDefaultTemplateEngine().process(
                        this.script, this.values);
            }
        } catch (Exception e) {
            logger.error("单据转换-freemarker转换字段值异常：", e);
            throw new BotException("单据转换-freemarker转换字段值异常：" + e.getMessage(), e);
        }

        return null;
    }
}
