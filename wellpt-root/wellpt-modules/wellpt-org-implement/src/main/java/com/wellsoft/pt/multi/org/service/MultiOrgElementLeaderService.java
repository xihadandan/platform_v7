/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgElementLeaderDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElementLeader;

import java.util.List;

/**
 * Description:
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
public interface MultiOrgElementLeaderService extends JpaService<MultiOrgElementLeader, MultiOrgElementLeaderDao, String> {
    /**
     * 按类型批量添加对应的管理者对象
     *
     * @param eleId
     * @param eleOrgVersionId
     * @param ids
     * @param names
     * @param leaderType
     * @return
     */
    List<MultiOrgElementLeader> addLeaderListByType(String eleId, String eleOrgVersionId, String ids, String names, Integer leaderType);

    /**
     * 按类型获取组织元素对应的管理者列表
     *
     * @param eleId
     * @param leaderType
     * @return
     */
    List<MultiOrgElementLeader> queryLeaderListByType(String eleId, String eleOrgVersionId, Integer leaderType);

    /**
     * 批量删除指定节点元素的管理者信息
     *
     * @param eleId
     * @param eleOrgVersionId
     */
    boolean deleteLeaderList(String eleId, String eleOrgVersionId);

    /**
     * 获取指定职位分管的下属节点
     *
     * @param jobId
     * @return
     */
    List<MultiOrgElementLeader> queryMyBranchUnderlingListByEleId(String jobId, String eleOrgVersionId);

    /**
     * 获取指定节点负责的下属节点
     *
     * @param jobId
     * @return
     */
    List<MultiOrgElementLeader> queryMyBossUnderlingListByEleId(String jobId, String eleOrgVersionId);

    /**
     * 获取指定节点的分管领导
     *
     * @param eleId
     * @return
     */
    List<MultiOrgElementLeader> queryMyBranchLeaderListByEleId(String eleId);

    /**
     * 或者指定节点的负责人
     *
     * @param eleId
     * @return
     */
    List<MultiOrgElementLeader> queryMyBossLeaderListByEleId(String eleId, String eleOrgVersionId);

    /**
     * 处理元素ID变更事件
     *
     * @param oldEleId
     * @param newEleId
     * @param orgVersionId
     */
    void dealElementIdChangeEvent(String oldEleId, String newEleId, String orgVersionId);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    List<MultiOrgElementLeader> queryLeaderByEleOrgVersionId(String eleId);

    /**
     * 删除节点，
     *
     * @param eleId
     */
    void deleteAllLeaderByEleId(String eleId, String orgVersionId);
}
