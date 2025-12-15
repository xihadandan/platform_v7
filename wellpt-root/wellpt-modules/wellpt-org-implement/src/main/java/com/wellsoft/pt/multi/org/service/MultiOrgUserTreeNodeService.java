/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserTreeNodeDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserTreeNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
public interface MultiOrgUserTreeNodeService extends JpaService<MultiOrgUserTreeNode, MultiOrgUserTreeNodeDao, String> {

    /**
     * 获取用户在指定版本组织下的职位列表
     *
     * @param orgVersionId
     * @param userId
     */
    List<MultiOrgUserTreeNode> queryUserJobByOrgVersion(String userId, String orgVersionId);

    /**
     * 获取指定节点下的所有用户
     *
     * @param eleId
     * @param orgVersionId
     */
    List<OrgUserTreeNodeDto> queryUserByOrgTreeNode(String eleId, String orgVersionId);

    List<SimpleUser> querSimpleUsers(String eleId, String orgVersionId);

    /**
     * 获取指定节点下的所有用户
     *
     * @param eleId        节点id
     * @param orgVersionId 组织版本ID
     * @return java.util.List<com.wellsoft.pt.multi.org.bean.OrgUserDto>
     **/
    List<OrgUserDto> querOrgUsers(String eleId, String orgVersionId);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @param orgVersionId
     */
    boolean deleteUserJobByOrgVersion(String userId, String orgVersionId);

    /**
     * 如何描述该方法
     *
     * @param orgVersionId
     * @return
     */
    List<OrgUserTreeNodeDto> queryUserByOrgVersion(String orgVersionId);

    /**
     * 批量变更对应的用户的节点位置
     *
     * @param oldEleId
     * @param newEleId
     * @param orgVersionId
     */
    void updateUserEleIdByOrgVersion(String oldEleId, String newEleId, String orgVersionId);

    /**
     * 获取指定版本的所有用户节点
     *
     * @param id
     * @return
     */
    List<MultiOrgUserTreeNode> queryAllNodeByVersionId(String id);

    /**
     * 如何描述该方法
     *
     * @param id
     */
    void deleteUser(String id);

    /**
     * 查询未禁用用户数量
     *
     * @param eleId
     * @return
     */
    int countUser(String verId, String eleId);

    /**
     * 查询禁用用户数量
     *
     * @param eleId
     * @return
     */
    int countDisUser(String verId, String eleId);

    MultiOrgUserTreeNode queryUserJobByOrgVersionEleId(String userId, String orgVersionId, String eleId);

    /**
     * 根据用户Id 查询职位
     * Map<String,UserJob>  key=userId
     *
     * @param userIds
     * @return
     */
    Map<String, UserJob> gerUserJob(List<String> userIds);

    Map<String, UserJob> gerUserJob(String orgVersionId, List<String> userIds);

    List<String> listUserIdsByVersionId(String fromVersionId);

    List<String> queryUserIdsByLikeElementId(String elementId, String orgVersionId);

    Map<String, Set<String>> getMainUserJobIdsIgnoreVersion(List<String> userids);

    /**
     * 返回用户的所有职位，包含主职和其他职位
     * key为userId
     *
     * @param userids
     **/
    Map<String, List<OrgUserJobDto>> getAllUserJobIdsIgnoreVersion(List<String> userids);

    List<MultiOrgUserTreeNode> queryMainJobByJobIdsAndUser(String[] jobids, String id);
}
