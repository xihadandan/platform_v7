package com.wellsoft.pt.basicdata.excelexporttemplate.support;

/**
 * Description: 类值处理器
 *
 * @author FashionSUN
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-16.1	FashionSUN		2014-10-16		Create
 * </pre>
 * @date 2014-10-16
 */
public interface ClazzProcessor {
    public abstract Object process(Object value);
}
