/*
 * @(#)2016年6月23日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.facade.service.AppJavaScriptModuleMgr;
import com.wellsoft.pt.app.js.JavaScriptModule;
import com.wellsoft.pt.app.support.RequireJsHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
 * 2016年6月23日.1	zhulh		2016年6月23日		Create
 * </pre>
 * @date 2016年6月23日
 */
@Service
public class AppJavaScriptModuleMgrImpl implements AppJavaScriptModuleMgr {

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        List<JavaScriptModule> jsModules = AppContextHolder.getContext().getAllJavaScriptModules();
        List<JavaScriptModule> result = new ArrayList<JavaScriptModule>();
        String dependencyFilter = select2QueryInfo.getOtherParams("dependencyFilter", "");
        for (JavaScriptModule jsModule : jsModules) {
            if (StringUtils.isNotBlank(dependencyFilter)) {
                // 解析依赖继承的模块，要依赖于ID
                List<JavaScriptModule> javaScriptModules = new ArrayList<JavaScriptModule>();
                javaScriptModules.add(jsModule);
                javaScriptModules = RequireJsHelper.sortWithResolveDependency(javaScriptModules);
                if (!isDependencyJsModuleFilter(javaScriptModules, dependencyFilter)) {
                    continue;
                }
            }
            result.add(jsModule);
            OrderComparator.sort(result);
        }
        return new Select2QueryData(result, "id", "name");
    }

    /**
     * @param jsModules
     * @param dependencyFilter
     * @return
     */
    private boolean isDependencyJsModuleFilter(List<JavaScriptModule> jsModules, String dependencyFilter) {
        if (StringUtils.isBlank(dependencyFilter)) {
            return false;
        }
        for (JavaScriptModule jsModule : jsModules) {
            if (jsModule == null) {
                continue;
            }
            if (CollectionUtils.contains(jsModule.getDependencies().iterator(), dependencyFilter)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] ids = select2QueryInfo.getIds();
        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        for (String id : ids) {
            JavaScriptModule module = AppContextHolder.getContext().getJavaScriptModule(id);
            if (module == null) {
                continue;
            }
            Select2DataBean bean = new Select2DataBean(module.getId(), module.getName());
            beans.add(bean);
        }
        return new Select2QueryData(beans);
    }

    @Override
    public JavaScriptModule getById(String id) {
        return AppContextHolder.getContext().getJavaScriptModule(id);
    }
}
