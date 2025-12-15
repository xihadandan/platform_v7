package com.wellsoft.pt.di.component.sql;

import com.wellsoft.pt.di.component.AbstractDIComponent;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/23    chenq		2019/8/23		Create
 * </pre>
 */
public class SqlDIComponent extends AbstractDIComponent<SqlEndpoint> {
    @Override
    protected String name() {
        return "数据交换-SQL组件";
    }
}
