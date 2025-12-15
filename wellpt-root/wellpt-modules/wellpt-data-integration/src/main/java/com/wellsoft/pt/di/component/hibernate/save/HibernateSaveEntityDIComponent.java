package com.wellsoft.pt.di.component.hibernate.save;

import com.wellsoft.pt.di.component.AbstractDIComponent;

/**
 * Description:
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
public class HibernateSaveEntityDIComponent extends
        AbstractDIComponent<HibernateSaveEntityEndpoint> {
    @Override
    protected String name() {
        return "数据交换-基于Hibernate的实体保存组件";
    }
}
