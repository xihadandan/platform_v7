package com.wellsoft.pt.jpa.template.freemarker;

import freemarker.template.TemplateModel;
import org.springframework.stereotype.Component;

/**
 * @author yt
 * @title: ConvertDateSharedVariable
 * @date 2020/5/28 2:55 下午
 */
@Component
public class ConvertDateSharedVariable implements CustomFreemarkerTemplateSharedVariable {

    public static final String CONVERT_DATE = "convertDate";

    @Override
    public String getName() {
        return CONVERT_DATE;
    }

    @Override
    public TemplateModel getValue() {
        return new ConvertDateMethod();
    }
}
