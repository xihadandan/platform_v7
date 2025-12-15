/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.bean.OrgJobDutyDto;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.dao.MultiOrgTreeNodeDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;

import java.util.List;
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
public interface MultiOrgTreeNodeService extends JpaService<MultiOrgTreeNode, MultiOrgTreeNodeDao, String> {
    // 供给 jqgrid使用的分页接口
    public List<OrgTreeNodeDto> query(QueryInfo queryInfo);

    /**
     * 获取一棵组织树指定位置的所有子节点，不包括自己
     *
     * @param uuid
     * @return
     */
    List<OrgTreeNodeDto> queryAllChildrenNodeOfOrgVersionByEleIdPath(String orgVersionId, String eleIdPath);

    /**
     * 获取一个组织版本指定位置的所有节点，包括子节点和自己
     *
     * @param orgVersionId
     * @return
     */
    List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPath(String orgVersionId, String eleIdPath);

    /**
     * 获取一个组织版本指定位置的所有同类型节点，包括子节点和自己
     *
     * @param orgVersionId
     * @param eleIdPath
     * @param eleType
     * @return
     */
    List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPath(String orgVersionId, String eleIdPath, String eleType);

    /**
     * 获取一个组织版本指定位置、类型节点，包括子节点和自己
     *
     * @param orgVersionId
     * @param eleIdPath
     * @param eleTypes
     * @return
     */
    List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPathAndEleTypes(String orgVersionId, String eleIdPath,
                                                                        String... eleTypes);

    /**
     * 根据eleIdPath 获取上级节点
     *
     * @param orgVersionId
     * @param parentEleIdPath
     * @return
     */
    MultiOrgTreeNode getParentNode(String orgVersionId, String parentEleIdPath);

    /**
     * 通过UUID获取节点信息
     *
     * @param uuid
     * @return
     */
    OrgTreeNodeDto getNode(String uuid);

    /**
     * 如何描述该方法
     *
     * @param oldEleIdPath
     * @param newEleIdPath
     * @param oldOrgVersionId
     * @param newOrgVersionId
     */
    void modifyNodeAndAllChildrenNodePathByOrgVersion(String oldEleIdPath, String newEleIdPath, String oldOrgVersionId,
                                                      String newOrgVersionId);

    /**
     * 如何描述该方法
     *
     * @param eleId
     * @param orgVersionId
     * @return
     */
    List<MultiOrgTreeNode> queryOtherOrgVersionListByEleId(String eleId, String orgVersionId);

    /**
     * 通过元素ID,获取所有跟它相关的组织树节点信息
     *
     * @param nodeList
     * @return
     */
    List<OrgTreeNodeDto> queryTreeNodeByEleId(String eleId);

    /**
     * 通过元素ID,和组织版本Id,获取所有跟它相关的指定版本的组织树节点信息
     *
     * @param eleId
     * @param orgVersionId
     * @return
     */
    OrgTreeNodeDto getNodeByEleIdAndOrgVersion(String eleId, String orgVersionId);

    /**
     * 获取一个组织被其他组织引用的情况
     *
     * @param versionId
     * @return
     */
    List<MultiOrgTreeNode> queryParentSystemUnitOrg(String versionId);

    /**
     * 如何描述该方法
     *
     * @param versionId
     * @return
     */
    List<OrgJobDutyDto> queryJobListWithDutyByVersionId(String versionId);

    /**
     * 如何描述该方法
     *
     * @param versionIdList
     * @return
     */
    List<OrgJobDutyDto> queryJobListWithDutyByVersionIdList(List<String> versionIdList, String keyword);

    /**
     * 如何描述该方法
     *
     * @param otherSystemUnitId
     * @param orgVersionId
     * @return
     */
    MultiOrgTreeNode getOtherSystemUnitByOrgVersionId(String otherSystemUnitId, String orgVersionId);

    /**
     * 如何描述该方法
     *
     * @param dutyId
     * @return
     */
    public List<OrgTreeNodeDto> queryJobNodeListOfCurrentVerisonByDutyId(String dutyId);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    public List<MultiOrgTreeNode> queryNodeByEleId(String id);

    public List<MultiOrgTreeNode> queryNodeByEleIdPath(String eleIdPath);

    /**
     * 如何描述该方法
     *
     * @param versionId
     * @return
     */
    public List<MultiOrgTreeNode> queryNodeByVersionId(String versionId);

    /**
     * 如何描述该方法
     *
     * @param orgVersionId
     * @param eleId
     * @return
     */
    public List<OrgTreeNodeDto> queryJobListByEleIdAndVersionId(String orgVersionId, String eleId);

    /**
     * 如何描述该方法
     *
     * @param selfUnit
     */
    public void clearOrgTree(OrgTreeNodeDto selfUnit);

    /**
     * 获取一个版本的所有上下级领导关系
     * 如何描述该方法
     *
     * @param verId
     * @return
     */
    List<QueryItem> queryAllLeaderListByVerId(String verId);

    /**
     * 根据版本id 节点id 查询节点信息
     *
     * @param verId
     * @param eleId
     * @return
     */
    MultiOrgTreeNode queryByVerIdEleId(String verId, String eleId);

    /**
     * 统计子节点数量
     *
     * @param eleId
     * @return
     */
    public int countChildren(String verId, String eleId);

    public int countChildren(String verId, String eleId, Set<String> ignoreIdPrefix);

    /**
     * 查询子节点
     *
     * @param verId
     * @param eleId
     * @return
     */
    public List<MultiOrgElement> children(String verId, String eleId);

    /**
     * 忽略
     *
     * @param verId
     * @param eleId
     * @param ignoreIdPrefix
     * @return
     */
    public List<MultiOrgElement> children(String verId, String eleId, Set<String> ignoreIdPrefix);

    /**
     * @param eleIdPaths
     * @return
     */
    public List<OrgTreeNodeDto> queryOrgTreeNodeByEleIdPaths(List<String> eleIdPaths);

}
