/*
 * @(#)2014-8-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.service;

import com.wellsoft.pt.basicdata.datasource.bean.DataSourceProfileBean;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceProfile;

import java.util.List;

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
public interface DataSourceProfileService {

    /**
     * 获取数据源配置的id
     *
     * @param id
     * @return
     */
    DataSourceProfileBean getBeanById(String id);

    /**
     * 获取数据源配置的id
     *
     * @param id
     * @return
     */
    DataSourceProfileBean getBeanByUuid(String uuid);

    /**
     * 根据前台页面配置里面的ID来获得
     *
     * @param id
     * @return
     */
    DataSourceProfileBean getBeanByProfileId(String id);

    /**
     * 获得所有的外部配置
     *
     * @return
     */
    List<DataSourceProfile> getAll();

    /**
     * 获得所有的外部配置的树
     *
     * @param s
     * @return
     */
    List getAllByTree(String s);

    /**
     * 保存
     *
     * @param dataSourceProfile
     * @return
     */
    void save(DataSourceProfile dataSourceProfile);

    void deleteById(String id);

    /**
     * 批量删除
     *
     * @param ids
     */
    void deleteAllById(String[] ids);

    /**
     * jdbcTest
     *
     * @param databaseType
     * @param databaseSid
     * @param host
     * @param port
     * @param userName
     * @param passWord
     * @return
     */
    String jdbcTest(String databaseType, String databaseSid, String host, String port, String userName, String passWord);
}
