/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.security.audit.entity.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

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
public class ResourceButtonAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.BUTTON;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<Resource> resources = this.dao.getAll(Resource.class);
        for (Resource resource : resources) {
            String resourceType = resource.getType();
            String functionType = getAppFunctionType();
            if (!StringUtils.equals(resourceType, functionType)) {
                continue;
            }
            appFunctionSources.add(convert2AppFunctionSource(resource));
        }
        return appFunctionSources;
    }


    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        Resource resource = this.dao.get(Resource.class, uuid);
        String resourceType = resource.getType();
        String functionType = getAppFunctionType();
        if (!StringUtils.equals(resourceType, functionType)) {
            return appFunctionSources;
        }
        appFunctionSources.add(convert2AppFunctionSource(resource));
        return appFunctionSources;
    }

    /**
     * @param resource
     * @return
     */
    private String getResourcePath(Resource resource) {
        List<String> names = new ArrayList<String>();
        names.add(resource.getName());
        Resource parent = resource.getParent();
        while (parent != null) {
            names.add(0, parent.getName());
            parent = parent.getParent();
        }
        return StringUtils.join(names, Separator.UNDERLINE.getValue());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#convert2AppFunctionSource(java.io.Serializable)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        Resource resource = (Resource) item;
        String resourceType = resource.getType();
        String functionType = getAppFunctionType();
        if (!StringUtils.equals(resourceType, functionType)) {
            return null;
        }
        String uuid = resource.getUuid();
        String resourcePath = getResourcePath(resource);
        String fullName = resourcePath + "_" + resource.getCode();
        String name = "按钮资源_" + resourcePath;
        String id = resource.getCode();
        String code = resource.getCode();
        String category = functionType;
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put("id", id);
        extras.put("type", resourceType);
        extras.put("btnId", code);
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                true,
                AppFunctionType.Resource, true, extras);
    }

    @Override
    public Collection<Object> getObjectIdentities(AppFunctionSource appFunctionSource) {
        String btnId = appFunctionSource.getId();
        if (StringUtils.isBlank(btnId)) {
            return super.getObjectIdentities(appFunctionSource);
        }

        List<Object> objectIdentities = new ArrayList<Object>();
        objectIdentities.add(StringUtils.trim(btnId));
        return objectIdentities;
    }

}
