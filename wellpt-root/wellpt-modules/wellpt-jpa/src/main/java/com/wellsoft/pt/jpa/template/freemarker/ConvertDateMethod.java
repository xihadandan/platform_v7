package com.wellsoft.pt.jpa.template.freemarker;

import com.wellsoft.context.util.date.DateUtils;
import freemarker.ext.beans.DateModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.List;

/**
 * @author yt
 * @title: DateMethod
 * @date 2020/5/28 2:51 下午
 */
public class ConvertDateMethod implements TemplateMethodModelEx {
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Object obj = arguments.get(0);
        if (obj != null && obj instanceof DateModel) {
            DateModel date = (DateModel) obj;
            String dateStr = DateUtils.convertDate(date.getAsDate());
            return dateStr;
        }
        return obj;
    }
}
