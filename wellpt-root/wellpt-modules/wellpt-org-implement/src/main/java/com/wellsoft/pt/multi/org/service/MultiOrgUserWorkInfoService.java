/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserWorkInfoDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserWorkInfo;

import java.util.List;
import java.util.Map;

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
public interface MultiOrgUserWorkInfoService extends JpaService<MultiOrgUserWorkInfo, MultiOrgUserWorkInfoDao, String> {

    void deleteAllUserByUnit(String unitId);

    /**
     * 如何描述该方法
     *
     * @param id
     */
    void deleteUser(String id);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @return 找不到数据时会返回null
     */
    MultiOrgUserWorkInfo getUserWorkInfo(String userId);

    /**
     * 根据职位ID获取用户工作信息列表
     *
     * @param jobId
     * @return
     */
    List<MultiOrgUserWorkInfo> getUserWorkInfosByJobId(String jobId);

    int countUserByJob(Map<String, Object> params);

    /**
     * 更新用户默认工作台
     *
     * @param defPageUuid
     */
    void updateDefWorkbench(String defPageUuid);

    List<MultiOrgUserWorkInfo> getUserWorkInfosByUserIds(List<String> userids);

    /**
     * 根获取用户工作信息列表
     *
     * @param userIds 用户ID集合
     **/
    List<MultiOrgUserWorkInfo> getUserWorkInfoByUserIds(List<String> userIds);

    /**
     * 根据领导Id获取用户工作信息
     *
     * @param leaderIds 领导ID列表
     * @return
     * @author baozh
     * @date 2022/1/27 11:40
     */
    List<MultiOrgUserWorkInfo> getUserWorkInfoByLeaderIds(List<String> leaderIds);
}
