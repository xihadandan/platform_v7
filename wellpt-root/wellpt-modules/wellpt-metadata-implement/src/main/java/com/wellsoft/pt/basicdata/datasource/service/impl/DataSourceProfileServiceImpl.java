/*
 * @(#)2014-8-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.datasource.bean.DataSourceProfileBean;
import com.wellsoft.pt.basicdata.datasource.dao.DataSourceProfileDao;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceProfile;
import com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService;
import com.wellsoft.pt.basicdata.datasource.support.JdbcSupports;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-13.1	wubin		2014-8-13		Create
 * </pre>
 * @date 2014-8-13
 */
@Service
@Transactional
public class DataSourceProfileServiceImpl extends BaseServiceImpl implements DataSourceProfileService {

    @Autowired
    private DataSourceProfileDao dataSourceProfileDao;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService#save(com.wellsoft.pt.basicdata.datasource.entity.DataSourceProfile)
     */
    @Override
    public void save(DataSourceProfile dataSourceProfile) {
        DataSourceProfile dataSourceProfile2 = new DataSourceProfile();
        if (StringUtils.isNotBlank(dataSourceProfile.getUuid())) {
            dataSourceProfile2 = this.dataSourceProfileDao.get(dataSourceProfile.getUuid());
        } else {
            dataSourceProfile.setId((new StringBuilder("P")).append(UUID.randomUUID()).toString());
        }
        BeanUtils.copyProperties(dataSourceProfile, dataSourceProfile2);
        this.dataSourceProfileDao.save(dataSourceProfile2);
        //CmsCacheUtils.clear();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService#JdbcTest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String jdbcTest(String databaseType, String databaseSid, String host, String port, String userName,
                           String passWord) {
        if (databaseType.equals("1")) {
            String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + databaseSid;
            JdbcSupports jt = new JdbcSupports();
            jt.test(databaseType, userName, passWord, url);
        } else if (databaseType.equals("2")) {

        } else if (databaseType.equals("3")) {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseSid;
            JdbcSupports jt = new JdbcSupports();
            jt.test(databaseType, userName, passWord, url);
        }
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService#getBeanById(java.lang.String)
     */
    @Override
    public DataSourceProfileBean getBeanById(String id) {
        DataSourceProfile dataSourceProfile = dataSourceProfileDao.getById(id);
        DataSourceProfileBean bean = new DataSourceProfileBean();
        BeanUtils.copyProperties(dataSourceProfile, bean);
        return bean;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService#getBeanById(java.lang.String)
     */
    @Override
    public DataSourceProfileBean getBeanByUuid(String uuid) {
        DataSourceProfile dataSourceProfile = dataSourceProfileDao.getByUuid(uuid);
        DataSourceProfileBean bean = new DataSourceProfileBean();
        BeanUtils.copyProperties(dataSourceProfile, bean);
        return bean;
    }

    @Override
    public DataSourceProfileBean getBeanByProfileId(String id) {
        DataSourceProfile dataSourceProfile = dataSourceProfileDao.getByProId(id);
        DataSourceProfileBean bean = new DataSourceProfileBean();
        BeanUtils.copyProperties(dataSourceProfile, bean);
        return bean;
    }

    /**
     * 批量删除
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService#deleteAllById(java.lang.String[])
     */
    @Override
    public void deleteAllById(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            DataSourceProfile dataSourceProfile = dataSourceProfileDao.getById(ids[i]);
            dataSourceProfileDao.delete(dataSourceProfile);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService#deleteById(java.lang.String)
     */
    @Override
    public void deleteById(String id) {
        DataSourceProfile dataSourceProfile = dataSourceProfileDao.getById(id);
        dataSourceProfileDao.delete(dataSourceProfile);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService#getAll()
     */
    @Override
    public List<DataSourceProfile> getAll() {
        String hql = "from DataSourceProfile";
        return dataSourceProfileDao.find(hql, new HashMap());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.service.DataSourceProfileService#getAllByTree(java.lang.String)
     */
    @Override
    public List getAllByTree(String s) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<DataSourceProfile> dataSourceProfiles = getAll();
        TreeNode treeNode = new TreeNode();
        for (Iterator iterator = dataSourceProfiles.iterator(); iterator.hasNext(); ) {
            DataSourceProfile df = (DataSourceProfile) iterator.next();
            DataSourceProfile dfNew = new DataSourceProfile();
            BeanUtils.copyProperties(df, dfNew);
            TreeNode child = new TreeNode();
            child.setId(df.getUuid());
            child.setName(df.getDataSourceProfileName());
            child.setData(df);
            treeNodes.add(child);
        }
        return treeNodes;
    }

}
