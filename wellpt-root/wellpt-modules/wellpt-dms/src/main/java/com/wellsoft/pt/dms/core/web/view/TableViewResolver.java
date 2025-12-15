/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.view;

import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.View;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 15, 2017.1	zhulh		Feb 15, 2017		Create
 * </pre>
 * @date Feb 15, 2017
 */
@Component
public class TableViewResolver extends AbstractViewResolver {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.ViewResolver#resolveView(com.wellsoft.pt.dms.core.context.ActionContext, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public View resolveView(ActionContext actionContext, HttpServletRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.view.AbstractViewResolver#getOrder()
     */
    @Override
    public int getOrder() {
        return 30;
    }

}
