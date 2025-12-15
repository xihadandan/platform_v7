package com.wellsoft.pt.di.transform;

import com.google.common.collect.Maps;
import com.wellsoft.pt.di.anotation.ProcessorParameter;
import com.wellsoft.pt.di.enums.DIParameterDomType;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * Description: 数据转换器-通过freemarker模板转换数据，输出字符串
 *
 * @author chenq
 * @date 2019/7/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/16    chenq		2019/7/16		Create
 * </pre>
 */
public class FreemarkerDataTransform extends
        AbstractDataTransform<Serializable, String> {


    @ProcessorParameter(name = "模板", domType = DIParameterDomType.TEXTAREA, remark = "变量：[DATA=数据][EXCHANGE_ID=交换批次ID]")
    private String templateContent;


    @Override
    public String transform(Serializable in) throws Exception {
        try {
            Map params = Maps.newHashMap();
            params.put("DATA", in);
            params.put("EXCHANGE_ID", getExchangeId());
            return TemplateEngineFactory.getDefaultTemplateEngine().process(templateContent,
                    params);
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s执行异常", name()));
        }

    }


    @Override
    public String name() {
        return "数据转换_Freemarker模板转换";
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

}
