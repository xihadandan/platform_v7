package com.wellsoft.pt.di.component.datastore;

import com.wellsoft.pt.di.component.AbstractDIComponent;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/15    chenq		2019/7/15		Create
 * </pre>
 */
public class DataStoreDIComponent extends
        AbstractDIComponent<DataStoreEndpoint> {


    @Override
    protected String name() {
        return "数据交换-数据仓库组件";
    }
}
