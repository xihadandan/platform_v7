package com.wellsoft.pt.mt.wizard;

/**
 * Description: 向导步骤接口
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月10日.1	linz		2016年3月10日		Create
 * </pre>
 * @date 2016年3月10日
 */
public interface Step {
    String process(WizardContext context);
}
