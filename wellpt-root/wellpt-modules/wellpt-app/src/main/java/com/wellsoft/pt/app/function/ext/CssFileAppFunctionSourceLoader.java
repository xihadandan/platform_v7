/*
 * @(#)2016年8月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: JavaScript模块功能加载
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
public class CssFileAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.CssFile;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        Collection<CssFile> cssFiles = AppContextHolder.getContext().getAllCssFile();
        for (CssFile cssFile : cssFiles) {
            String uuid = DigestUtils.md5Hex(cssFile.getId() + getAppFunctionType());
            String fullName = cssFile.getName();
            String name = "CSS模块功能_" + cssFile.getName();
            String id = cssFile.getId();
            String code = cssFile.getId();
            String category = getAppFunctionType();
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("id", id);
            extras.put("path", cssFile.getPath());
            appFunctionSources.add(new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                    false, category, false, extras));
        }
        return appFunctionSources;
    }
}
