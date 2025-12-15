/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.view;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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
public class MobileListDataView implements DataView {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.view.DataView#getName()
     */
    @Override
    public String getName() {
        return "MobileList视图展示";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.view.DataView#getType()
     */
    @Override
    public String getType() {
        return "mobileListDataView";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.view.DataView#render(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 10;
    }

}
