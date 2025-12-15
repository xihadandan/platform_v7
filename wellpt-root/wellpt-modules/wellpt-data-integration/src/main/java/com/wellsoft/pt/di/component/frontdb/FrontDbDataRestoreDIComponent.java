package com.wellsoft.pt.di.component.frontdb;

import com.wellsoft.pt.di.component.AbstractDIComponent;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/30    chenq		2019/8/30		Create
 * </pre>
 */
public class FrontDbDataRestoreDIComponent extends AbstractDIComponent<FrontDbDataResotreEndpoint> {
    @Override
    protected String name() {
        return "数据交换-前置库数据还原";
    }
}
