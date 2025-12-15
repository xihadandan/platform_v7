/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.view;

import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.core.web.ViewResolver;

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
public abstract class AbstractViewResolver implements ViewResolver {

    protected static final boolean isDataStoreType(String dataType) {
        return DataType.DATA_STORE.getId().equals(dataType);
    }

    protected static final boolean isDyFormType(String dataType) {
        return DataType.DYFORM.getId().equals(dataType);
    }

    protected static final boolean isSupportTypeOfDyForm(String dataType) {
        return DataType.DYFORM.getId().equals(dataType) || DataType.MIXTURE.getId().equals(dataType);
    }

    protected static final boolean isTableType(String dataType) {
        return DataType.TABLE.getId().equals(dataType);
    }

    /**
     * @param request
     * @return
     */
    protected String getIdKey(HttpServletRequest request) {
        return request.getParameter("idKey");
    }

    /**
     * @param request
     * @return
     */
    protected String getIdValue(HttpServletRequest request) {
        return request.getParameter("idValue");
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
