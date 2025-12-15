/*
 * @(#)2016年8月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.app.function;

import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.dms.core.web.ActionProxy;
import com.wellsoft.pt.dms.core.web.action.ActionManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据管理操作功能
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月24日.1	zhulh		2016年8月24日		Create
 * </pre>
 * @date 2016年8月24日
 */
@Service
@Transactional(readOnly = true)
public class DmsActionAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    private static final String DmsAction = "DmsAction";

    @Autowired
    private ActionManager actionManager;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return DmsAction;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        Collection<ActionProxy> actions = actionManager.getAllActions();
        for (ActionProxy action : actions) {
            String actionId = action.getId();
            String uuid = DigestUtils.md5Hex(actionId);
            String fullName = "数据管理_操作功能_" + action.getFullName();
            String name = fullName;
            String id = actionId;
            String code = actionId;
            String category = getAppFunctionType();
            Map<String, Object> extras = action.getProperties();
            appFunctionSources.add(new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                    false, category, false, extras));
        }
        return appFunctionSources;
    }
}
