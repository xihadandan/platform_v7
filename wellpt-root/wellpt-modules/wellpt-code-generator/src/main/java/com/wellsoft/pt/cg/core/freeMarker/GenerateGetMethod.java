package com.wellsoft.pt.cg.core.freeMarker;

import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.List;

/**
 * 实体属性Get方法生成函数
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-9.1	lmw		2015-7-9		Create
 * </pre>
 * @date 2015-7-9
 */
public class GenerateGetMethod implements TemplateMethodModelEx {

    private Method method;

    public GenerateGetMethod(Method method) {
        this.method = method;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object exec(List args) throws TemplateModelException {
        BeanModel model = (BeanModel) args.get(0);
        return method.exec(model.getWrappedObject());
    }
}
