/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.iexport.acceptor;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.entity.UserJob;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Description: 数据字典数据源
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-29.1	linz		2015-6-29		Create
 * </pre>
 * @date 2015-6-29
 */
public class UserIexportData extends IexportData {
    public User user;

    public UserIexportData(User user) {
        this.user = user;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return user.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return user.getUserName();
    }

    @Override
    public Integer getRecVer() {
        // TODO Auto-generated method stub
        return user.getRecVer();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getType()
     */
    @Override
    public String getType() {
        return IexportType.User;
    }

    /**
     * (non-Javadoc)
     *
     * @throws IOException
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, user);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        Set<UserJob> userJobs = user.getUserJobs();
        List<IexportData> dependencies = new ArrayList<IexportData>();
        // TODO zyguo
        // job
        // for (UserJob userJob : userJobs) {
        // dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.Job).getData(
        // userJob.getJob().getUuid()));
        // }
        // group
        // for (Group group : user.getGroups()) {
        // dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.Group).getData(group.getUuid()));
        // }
        // //role
        // for (Role role : user.getRoles()) {
        // dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.Role).getData(role.getUuid()));
        // }
        return dependencies;
    }

}
