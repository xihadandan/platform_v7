/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgVersionDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgVersionService extends JpaService<MultiOrgVersion, MultiOrgVersionDao, String> {

    /**
     * 给指定的组织创建一个新的版本号
     * 每次升级，版本号 + 0.1
     *
     * @param rootVersionId
     * @param id
     * @return
     */
    String createNewVersionNum(String systemUnitId, String rootVersionId);

    /**
     * 获取所有组织单位的所有组织的当前启动版本
     */
    List<MultiOrgVersion> queryCurrentActiveVersionListOfAllUnit();

    /**
     * 获取指定组织的当前启用版本
     *
     * @param unitId
     * @return
     */
    List<MultiOrgVersion> queryCurrentActiveVersionListOfSystemUnit(String unitId);

    /**
     * 获取启用默认版本
     *
     * @param unitId
     * @return
     */
    MultiOrgVersion enabledByDefault(String unitId);

    /**
     * 获取组织的其他版本
     *
     * @param maxVersionUuid
     * @return
     */
    List<MultiOrgVersion> queryHistoryVersionOfOrg(String maxVersionUuid);

    /**
     * 获取所有的组织的最大版本
     *
     * @param queryInfo
     * @return
     */
    List<MultiOrgVersion> queryMaxVersionOfAllOrg();

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    List<MultiOrgVersion> queryAllVersionListBySystemUnitId(String id);

    /**
     * 如何描述该方法
     *
     * @param systemUnitId
     * @param rootVersionId
     * @return
     */
    List<MultiOrgVersion> queryCurrentActiveVersionListOfUnitAndRootVersionId(String systemUnitId, String rootVersionId);

    /**
     * 如何描述该方法
     *
     * @param orgVersionId
     * @return
     */
    MultiOrgVersion getById(String orgVersionId);

    /**
     * 如何描述该方法
     *
     * @param q
     * @return
     */
    List<MultiOrgVersion> findByExample(MultiOrgVersion q);

    /**
     * 根据userId 查询所属 组织版本
     *
     * @param userId
     * @return
     */
    List<MultiOrgVersion> queryByUserId(String userId);

    void updateUnDefault(String systemUnitId);

    long countBySystemUnitId(String systemUnitId);

    long countBySystemUnitIds(List<String> systemUnitIds);
}
