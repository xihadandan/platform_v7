/*
 * @(#)2017年11月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.dto.MultiOrgJobRankDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.vo.MultiOrgJobRankVo;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月22日.1	zyguo		2017年11月22日		Create
 * </pre>
 * @date 2017年11月22日
 */
public interface MultiOrgService extends BaseService {
    // 全路径分割符
    public static String PATH_SPLIT_SYSMBOL = "/";

    /**
     * 通用方法，添加一个组织子节点，根据里面的type做不同的业务处理
     *
     * @param nodeVo
     * @return
     */
    public MultiOrgTreeNode addOrgChildNode(OrgTreeNodeVo nodeVo);

    /**
     * 通用方法，修改一个组织子节点
     *
     * @param nodeVO
     * @param isUnbind ,是否解绑，是：插入一个新节点，继承的版本的节点信息不变，否：所有版本的节点信息都跟着变化
     * @return
     */
    public boolean modifyOrgChildNode(OrgTreeNodeVo nodeVo, boolean isUnbind);

    public boolean modifyOrgChildNode(OrgTreeNodeVo nodeVo, boolean isUnbind, boolean includeChildNode);

    /**
     * 通过树节点UUID获取一个组织节点的信息
     *
     * @param orgTreeUuid， multi_org_tree 表中的UUID
     * @return
     */
    public OrgTreeNodeVo getOrgNodeByTreeUuid(String orgTreeUuid);

    /**
     * 获取指定版本的组织的完整组织树
     *
     * @param orgVersionId
     * @return
     */
    public OrgTreeNode getOrgAsTreeByVersionId(String orgVersionId);

    /**
     * 获取指定版本，指定位置的组织树, 包含子单位
     *
     * @param orgVersionId
     * @param eleIdPath
     * @param isInMyUnit   是否只看本单位内的数据
     * @return
     */
    public OrgTreeNode getOrgAsTreeByEleIdPath(String orgVersionId, String eleIdPath, boolean isInMyUnit);

    /**
     * 获取指定版本，指定位置、类型的组织树, 包含子单位
     *
     * @param orgVersionId
     * @param eleIdPath
     * @param isInMyUnit   是否只看本单位内的数据
     * @param eleTypes     组织元素类型
     * @return
     */
    public OrgTreeNode getOrgAsTreeByEleIdPathAndEleTypes(String orgVersionId, String eleIdPath, boolean isInMyUnit,
                                                          String... eleTypes);

    /**
     * 删除组织节点
     *
     * @param treeNodeUuid
     * @param orgVersionId
     * @return
     */
    public boolean deleteOrgChildNode(String treeNodeUuid);

    /**
     * 获取一个组织树节点信息
     *
     * @param treeNodeUuid
     * @return
     */
    public OrgTreeNodeDto getOrgTreeNodeDto(String treeNodeUuid);

    /**
     * 通过元素ID,获取指定版本的组织树的节点信息
     *
     * @param unitId
     * @param orgVersionId
     * @return
     */
    OrgTreeNodeDto getNodeByEleIdAndOrgVersion(String unitId, String orgVersionId);

    /**
     * 获取指定职位分管的下属节点（可能是职位或单位或部门类型）
     *
     * @param jobId
     * @param orgVersionId
     * @return
     */
    public List<OrgTreeNodeDto> queryBranchUnderlingNodeListByJobId(String jobId, String orgVersionId);

    /**
     * 获取指定职位负责的下属节点（可能是职位或单位或部门类型）
     *
     * @param jobId
     * @param orgVersionId
     * @return
     */
    public List<OrgTreeNodeDto> queryBossUnderlingNodeListByJobId(String jobId, String orgVersionId);

    /**
     * 获取指定节点的负责人领导节点
     *
     * @param eleId
     * @param orgVersionId
     * @return
     */
    public List<OrgTreeNodeDto> queryBossLeaderNodeListByNode(String eleId, String orgVersionId);

    /**
     * 获取指定节点的一级分管领导职位节点
     *
     * @param eleId
     * @param orgVersionId
     * @return
     */
    public List<OrgTreeNodeDto> queryBranchLeaderNodeListByNode(String eleId, String orgVersionId);

    /**
     * 获取指定节点的一级分管领导用户Ids
     *
     * @param eleId
     * @param orgVersionId
     * @return
     */
    public List<String> queryBranchLeaderUserIdListByNode(String eleId, String orgVersionId);

    /**
     * 获取指定角色的组织节点列表
     *
     * @param roleUuid
     * @return
     */
    public List<OrgTreeNodeDto> queryOrgNodeListByRole(String roleUuid);

    /**
     * 处理角色删除对应的事件
     *
     * @param uuid
     */
    public boolean dealRoleRemoveEvent(String uuid);

    /**
     * 获取一个组织节点对应的角色的权限列表，以树形态返回
     *
     * @param eleId
     * @return
     */
    TreeNode getOrgNodePrivilegeResultTree(String eleId);

    /**
     * 获取一个组织节点对应的角色信息
     *
     * @param eleId
     * @return
     */
    List<MultiOrgElementRole> queryRoleListOfElement(String eleId);

    List<MultiOrgElementRole> queryRoleListOfElementByRoleUuid(String roleUuid);

    /**
     * 通过元素ID,获取组织元素信息
     *
     * @param eleId
     * @return
     */
    public MultiOrgElement getOrgElementById(String eleId);

    /**
     * 批量获取组织元素列表
     *
     * @param eleIds
     * @return
     */
    public List<MultiOrgElement> queryOrgElementListByIds(Collection<String> eleIds);

    /**
     * 添加系统单位
     *
     * @return
     */
    public OrgSystemUnitVo addSystemUnit(OrgSystemUnitVo unit);

    /**
     * 修改系统单位信息
     *
     * @param unit
     * @return
     */
    public OrgSystemUnitVo modifySystemUnit(OrgSystemUnitVo unit);

    /**
     * 获取系统单位信息
     *
     * @param uuid
     * @return
     */
    OrgSystemUnitVo getSystemUnitVo(String uuid);

    /**
     * 通过ID,获取系统单位信息
     *
     * @param systemUnitId
     * @return
     */
    public MultiOrgSystemUnit getSystemUnitById(String systemUnitId);

    /**
     * 获取一个组织的集团版本, 一个组织可能会被多个集团引用
     *
     * @param id
     * @return
     */
    public List<OrgTreeNodeDto> computeMyCompanyListByVersionId(MultiOrgVersion ver);

    /**
     * 通过元素ID,获取该元素在当前使用的版本的组织树节点
     *
     * @param eleId
     * @return
     */
    OrgTreeNodeDto getNodeOfCurrentVerisonByEleId(String eleId);

    /**
     * 处理组织版本升级事件
     *
     * @param oldVersion
     * @param newVersion
     */
    public void dealOrgUpgradeEvent(MultiOrgVersion oldVersion, MultiOrgVersion newVersion);

    /**
     * 添加职务
     *
     * @param vo
     * @return
     */
    public MultiOrgDuty addDuty(MultiOrgDuty vo);

    /**
     * 修改职务
     *
     * @param vo
     * @return
     */
    public MultiOrgDuty modifyDuty(MultiOrgDuty vo);

    /**
     * 获取职务信息
     *
     * @param uuid
     * @return
     */
    public MultiOrgDuty getDuty(String uuid);

    /**
     * 添加职级
     *
     * @param vo
     * @return
     */
    public MultiOrgJobRank addJobRank(MultiOrgJobRankVo vo);

    /**
     * 修改职级
     *
     * @param vo
     * @return
     */
    public MultiOrgJobRank modifyJobRank(MultiOrgJobRankVo vo);

    /**
     * 获取职级
     *
     * @param uuid
     * @return
     */
    public MultiOrgJobRankDto getJobRank(String uuid);

    /**
     * 获取一个版本内所有带职务的职位
     *
     * @param versionId
     */
    public List<OrgJobDutyDto> queryJobListWithDutyByVersionId(String versionId);

    /**
     * 获取多个版本内所有带职务的职位
     *
     * @param versionIdList
     */
    public List<OrgJobDutyDto> queryJobListWithDutyByVersionIdList(List<String> versionIdList, String keyword);

    /**
     * 通过ID,获取一个职务信息
     *
     * @param dutyId
     * @return
     */
    public MultiOrgDuty getDutyById(String dutyId);

    public MultiOrgDuty getDutyByName(String dutyName);

    /**
     * 添加组织选择项
     *
     * @param vo
     * @return
     */
    MultiOrgOption addOrgOption(MultiOrgOption vo);

    /**
     * 修改组织选择项
     *
     * @param vo
     * @return
     */
    MultiOrgOption modifyOrgOption(MultiOrgOption vo);

    /**
     * 获取一个组织选择项
     *
     * @param uuid
     * @return
     */
    MultiOrgOption getOrgOption(String uuid);

    /**
     * 获取某一系统单位的所有的组织选项，并且带平台的的组织选项;systemUnitId为空时,取当前单位
     *
     * @param systemUnitId
     * @return
     */
    List<MultiOrgOption> getOrgOptionListByUnitId(String systemUnitId);

    List<MultiOrgOption> getOrgOptionListByUnitId(String systemUnitId, boolean onlyShow);

    /**
     * 获取某一系统单位的所有的组织选项，并且带平台的的组织选项
     *
     * @param systemUnitId
     * @return
     */
    List<MultiOrgOption> queryOrgOptionListBySystemUnitIdAndOptionOfPT(String systemUnitId);

    /**
     * 获取所有的系统单位
     *
     * @return
     */
    List<MultiOrgSystemUnit> queryAllSystemUnitList();

    /**
     * 添加组织类型
     *
     * @param vo
     * @return
     */
    MultiOrgType addOrgType(MultiOrgType vo);

    /**
     * 修改组织选择项
     *
     * @param vo
     * @return
     */
    MultiOrgType modifyOrgType(MultiOrgType vo);

    /**
     * 获取一个组织选择项
     *
     * @param uuid
     * @return
     */
    MultiOrgType getOrgType(String uuid);

    Select2QueryData queryDutyListForSelect2(Select2QueryInfo select2QueryInfo);

    /**
     * 添加账号
     *
     * @param vo
     * @return
     */
    public OrgUserVo addUnitAdmin(OrgUserVo vo) throws UnsupportedEncodingException;

    /**
     * 修改账号信息
     *
     * @param vo 包含密码没加密
     * @return
     */
    public OrgUserVo modifyUnitAdmin(OrgUserVo vo);

    /**
     * 获取用户账号和INFO信息
     *
     * @param uuid 包含密码没加密
     * @return
     */
    public OrgUserVo getUser(String uuid);

    /**
     * 获取用户对应的角色的权限列表，以角色树形态展示
     *
     * @param uuid
     * @return
     */
    TreeNode getUserPrivilegeResultTree(String uuid);

    /**
     * 添加账号
     *
     * @param vo 包含的密码不加密
     * @return
     */
    public OrgUserVo addUser(OrgUserVo vo) throws UnsupportedEncodingException;

    /**
     * 修改账号信息
     *
     * @param vo
     * @return
     */
    public OrgUserVo modifyUser(OrgUserVo vo);

    Select2QueryData queryOrgTypeListForSelect2(Select2QueryInfo select2QueryInfo);

    /**
     * 通过职位ID获取职务ID
     *
     * @param jobId
     * @return
     */
    public MultiOrgJobDuty getJobDutyByJobId(String jobId);

    /**
     * 如何描述该方法
     *
     * @param mainJobIdPath
     * @param isNeedUnit
     * @return
     */
    String getDepartmentNamePathByJobIdPath(String mainJobIdPath, boolean isNeedUnit);

    /**
     * 获取指定版本内，指定职务对应的职位ID
     *
     * @param orgVersionId
     * @param orgId
     */
    public List<OrgJobDutyDto> queryJobListByDutyAndVersionId(String orgVersionId, String orgId);

    /**
     * 通过职务ID, 获取当前启用版本的职位节点信息
     *
     * @param dutyId
     * @return
     */
    public List<OrgTreeNodeDto> queryJobNodeListOfCurrentVerisonByDutyId(String dutyId);

    /**
     * 获取指定节点下的所有职位节点，如果自己是个职位ID, 则包括自己
     *
     * @param orgVersionId
     * @param eleId
     * @return
     */
    public List<OrgTreeNodeDto> queryJobListByEleIdAndVersionId(String orgVersionId, String eleId);

    /**
     * 获取用户所有的权限，包括组织节点，表单控件等其他地方继承过来的角色权限
     *
     * @param uuid
     * @return
     */
    TreeNode getUserAllPrivilegeResultTree(String uuid);

    /**
     * 清空一个组织版本的组织树，就只保留原始数据
     *
     * @param selfUnit
     */
    public void clearOrgTree(OrgTreeNodeDto selfUnit);

    /**
     * 清空一家单位的所有用户，不包括单位管理员
     */
    public void clearAllUserOfUnit();

    /**
     * 删除多个用户
     *
     * @param userIds
     */
    void deleteUsers(String[] userIds);

    /**
     * 如何描述该方法
     *
     * @param id
     * @param roleUuid
     */
    public void addRoleListOfElement(String id, String roleUuid);

    /**
     * 通过idPath获取对应的所有节点信息
     *
     * @param idPath
     * @return
     */
    public Map<String, MultiOrgElement> queryElementMapByEleIdPath(String idPath);

    public List<MultiOrgSystemUnit> findMultiOrgSystemUnitByName(Map<String, Object> map);

    public MultiOrgSystemUnit getMultiOrgSystemUnit(String unit);

    public List<MultiOrgSystemUnit> findMultiOrgSystemUnitForAll();

    public List<OrgUserVo> queryDirectLeaderListByUserId(String userId);

    public Integer countUnderling(String userId);

    OrgNode queryOrgNode(String eleId);

    /**
     * @param eleIdPaths
     * @return
     */
    public List<OrgTreeNodeDto> queryOrgTreeNodeByEleIdPaths(List<String> eleIdPaths);

    /**
     * @param eleIdPaths
     * @return
     */
    public Map<String, Integer> getOrgEleOrderByEleIdPaths(List<String> eleIdPaths);

    List<MultiOrgDuty> getDutysByIds(List<String> ids);

    /**
     * 删除职务
     *
     * @param uuids
     * @return
     * @author baozh
     * @date 2021/10/29 17:01
     */
    String deleteDuty(String... uuids);

    /**
     * 删除职级
     *
     * @param uuid
     * @return
     * @author baozh
     * @date 2021/11/5 14:49
     */
    String deleteJobRank(String uuid);

    /**
     * 方法描述
     *
     * @return
     * @author baozh
     * @date 2021/11/5 16:08
     * @params *@params
     */
    List<MultiOrgJobRankDto> listJobRankByDutySeqUuid(String uuid);

    /**
     * 校验登录用户名
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/11/24 14:09
     */
    List<String> checkOnly(OrgUserVo vo);

    /**
     * 根据职位uuid查询职级
     *
     * @param jobId
     * @return
     * @author baozh
     * @date 2021/11/30 16:12
     */
    List<MultiOrgJobRankDto> getJobRankByJobId(String orgVersion, String jobId);

    List<QueryItem> queryRoleListOfElementIds(Set<String> userOrgIds);


    List<String> queryBossListByEleId(String eleId, String orgVersionId);
}
