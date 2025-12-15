/*
 * @(#)2016年8月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.app.function;

import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.service.DmsRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class DmsRoleAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    @Autowired
    private DmsRoleService dmsRoleService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return IexportType.DmsRole;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        Collection<DmsRoleEntity> dmsRoleEntities = dmsRoleService.getAll();
        for (DmsRoleEntity dmsRoleEntity : dmsRoleEntities) {
            String uuid = dmsRoleEntity.getUuid();
            String fullName = "数据管理_文件操作权限功能_" + dmsRoleEntity.getName();
            String name = fullName;
            String id = dmsRoleEntity.getId();
            String code = dmsRoleEntity.getCode();
            String category = getAppFunctionType();
            appFunctionSources.add(new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                    true, category, false, null));
        }
        return appFunctionSources;
    }

}
