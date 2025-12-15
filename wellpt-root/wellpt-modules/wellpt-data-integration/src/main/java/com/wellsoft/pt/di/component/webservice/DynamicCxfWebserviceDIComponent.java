package com.wellsoft.pt.di.component.webservice;

import com.wellsoft.pt.di.component.AbstractDIComponent;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/15    chenq		2019/8/15		Create
 * </pre>
 */
public class DynamicCxfWebserviceDIComponent extends AbstractDIComponent<DynamicCxfWebserviceEndpoint> {
    @Override
    protected String name() {
        return "Cxf-Webservice服务组件";
    }
}
