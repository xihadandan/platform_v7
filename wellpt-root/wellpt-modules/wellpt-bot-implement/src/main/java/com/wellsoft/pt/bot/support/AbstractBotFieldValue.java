package com.wellsoft.pt.bot.support;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class AbstractBotFieldValue implements BotFieldValue {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Map<String, ?> data = Maps.newHashMap();//表单数据

    protected Object fromFieldValue;//来源字段值

    protected Object toFieldValue;//目标字段值

    protected String script;//脚本

    protected Map<String, Object> values = Maps.newHashMap();


    public AbstractBotFieldValue(
            Map<String, ?> data, Object fromFieldValue, Object toFieldValue, String script) {
        reload(data, fromFieldValue, toFieldValue);
        this.script = script;
        //其他可解析的系统变量
        values.putAll(TemplateEngineFactory.getExplainRootModel());

    }


    public void reload(Map<String, ?> data, Object fromFieldValue, Object toFieldValue) {
        this.data = data;
        if (data != null) {
            if (data.containsKey("formData")) {//源表单数据
                values.put("data", data.get("formData"));
                values.put("formData", data.get("formData"));
            }
            if (data.containsKey("targetFormData")) {//目标单据的数据
                values.put("targetFormData", data.get("targetFormData"));
            }
            if (data.containsKey("jsonBody")) {//json报文数据
                values.put("jsonBody", data.get("jsonBody"));
                if (!data.containsKey("formData")) {
                    //兼容旧的数据表达式
                    values.put("data", data.get("jsonBody"));
                }
            }
        }
        this.fromFieldValue = fromFieldValue;
        this.toFieldValue = toFieldValue;
        //values.put("value", fromFieldValue);
        values.put("sourceValue", fromFieldValue);
        values.put("targetValue", toFieldValue);
        values.put("data", data);
    }


}
