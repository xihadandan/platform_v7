/*
 * @(#)2016年8月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.js.RequireJSJavaScriptModule;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.support.RequireJsHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
public class JavaScriptModuleAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.JavaScriptModule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        Collection<JavaScriptModule> javaScriptModules = AppContextHolder.getContext().getAllJavaScriptModules();
        // 要确保JavaScriptModule模块依赖的共享模块优先加载
        javaScriptModules = RequireJsHelper.javaScriptModuleDependenciesUseExports(javaScriptModules);
        for (JavaScriptModule javaScriptModule : javaScriptModules) {
            appFunctionSources.add(this.convert2AppFunctionSource(javaScriptModule));
        }
        return appFunctionSources;
    }

    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        JavaScriptModule javaScriptModule = (JavaScriptModule) item;
        String fullName = javaScriptModule.getName();
        String name = "JavaScript模块功能_" + javaScriptModule.getName();
        String id = javaScriptModule.getId();
        String code = javaScriptModule.getId();
        String category = getAppFunctionType();
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put("isJsModule", true);
        extras.put("jsModule", javaScriptModule.getExports());
        extras.put("id", id);
        extras.put("path", javaScriptModule.getPath());
        if (javaScriptModule instanceof RequireJSJavaScriptModule) {
            extras.put("isConfuse", ((RequireJSJavaScriptModule) javaScriptModule).isConfuse());
        }
        extras.put("cssFiles", javaScriptModule.getCssFiles());
        extras.put("deps", javaScriptModule.getDependencies());
        extras.put("exports", javaScriptModule.getExports());
        return new SimpleAppFunctionSource(DigestUtils.md5Hex(javaScriptModule.getId()), fullName, name, id, code, null, null, category,
                false, category, false, extras);
    }
}
