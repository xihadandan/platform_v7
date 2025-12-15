/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.security.audit.entity.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
@Service
@Transactional(readOnly = true)
public class ResourceAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.Resource;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#convert2AppFunctionSource(java.io.Serializable)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        Resource resource = (Resource) item;
        String resourceType = resource.getType();
        if (AppFunctionType.BUTTON.equals(resourceType)) {
            return ApplicationContextHolder.getBean(ResourceButtonAppFunctionSourceLoader.class)
                    .convert2AppFunctionSource(item);
        }
        if (AppFunctionType.MENU.equals(resourceType)) {
            return ApplicationContextHolder.getBean(ResourceMenuAppFunctionSourceLoader.class)
                    .convert2AppFunctionSource(item);
        }
        return super.convert2AppFunctionSource(item);
    }

}
