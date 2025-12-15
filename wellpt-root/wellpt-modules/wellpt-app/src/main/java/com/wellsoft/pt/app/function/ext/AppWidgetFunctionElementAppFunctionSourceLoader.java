/*
 * @(#)2019年6月18日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.support.AppFunctionType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * 2019年6月18日.1	zhulh		2019年6月18日		Create
 * </pre>
 * @date 2019年6月18日
 */
@Component
public class AppWidgetFunctionElementAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {
    @Autowired
    AppFunctionService appFunctionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.AppWidgetFunctionElement;
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

    @Override
    public List<AppFunctionSource> getAppFunctionSourceById(String id) {
        List<AppFunction> appFunctions = appFunctionService.getByIds(new String[]{id});
        if (CollectionUtils.isNotEmpty(appFunctions)) {
            return Lists.newArrayList(new SimpleAppFunctionSource(appFunctions.get(0).getUuid(),
                    appFunctions.get(0).getName(), appFunctions.get(0).getName(), appFunctions.get(0).getId(),
                    appFunctions.get(0).getCode(), null, null, null, false,
                    this.getAppFunctionType(), false, null));
        }
        return super.getAppFunctionSourceById(id);
    }
}
