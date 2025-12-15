/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月2日.1	zhulh		2016年8月2日		Create
 * </pre>
 * @date 2016年8月2日
 */
@Transactional(readOnly = true)
public abstract class AbstractAppFunctionSourceLoader extends BaseServiceImpl implements AppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSourceByUuid(java.lang.String)
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSourceByUuid(java.lang.String)
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSourceById(String id) {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#convert2AppFunctionSource(java.io.Serializable)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        throw new UnsupportedOperationException();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getObjectIdentities(com.wellsoft.pt.app.function.AppFunctionSource)
     */
    @Override
    public Collection<Object> getObjectIdentities(AppFunctionSource appFunctionSource) {
        List<Object> objectIdentities = new ArrayList<Object>();
        objectIdentities.add(appFunctionSource.getUuid());
        return objectIdentities;
    }

}
