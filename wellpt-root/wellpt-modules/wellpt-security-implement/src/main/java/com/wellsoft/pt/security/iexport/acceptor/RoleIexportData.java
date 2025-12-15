/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.iexport.acceptor;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 角色
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-01-18	linz		2016-01-18		Create
 * </pre>
 * @date 2016-01-18
 */
public class RoleIexportData extends IexportData {
    public Role role;

    public RoleIexportData(Role role) {
        this.role = role;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return role.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return "角色：" + role.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getType()
     */
    @Override
    public String getType() {
        return IexportType.Role;
    }

    @Override
    public Integer getRecVer() {
        // TODO Auto-generated method stub
        return role.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, role);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        for (Privilege privilege : role.getPrivileges()) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.Privilege).getData(
                    privilege.getUuid()));
        }
        return dependencies;
    }

}
