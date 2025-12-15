/*
 * @(#)2017年11月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.constant.UnitParamConstant;
import com.wellsoft.pt.multi.org.dto.MultiOrgJobRankDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.multi.org.vo.MultiOrgJobRankVo;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

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
@Service
@Transactional
public class MultiOrgServiceImpl implements MultiOrgService {
    private static final String SYSTEM_UNIT_ID_PATTERN = IdPrefix.SYSTEM_UNIT.getValue() + "0000000000";
    private static final String DEPT_ID_PATTERN = IdPrefix.DEPARTMENT.getValue() + "0000000000";
    private static final String JOB_ID_PATTERN = IdPrefix.JOB.getValue() + "0000000000";
    private static final String ORG_ID_PATTERN = IdPrefix.ORG.getValue() + "0000000000";
    private static final String ORG_VERSION_ID_PATTERN = IdPrefix.ORG_VERSION.getValue() + "0000000000";
    private static final String BUSINESS_UNIT_ID_PATTERN = IdPrefix.BUSINESS_UNIT.getValue() + "0000000000";
    private static final String DUTY_ID_PATTERN = IdPrefix.DUTY.getValue() + "0000000000";
    private static final String JOB_RANK_ID_PATTERN = IdPrefix.RANK.getValue() + "0000000000";
    @Autowired
    UnitParamService unitParamService;
    @Autowired
    MultiOrgJobLevelService multiOrgJobLevelService;
    @Autowired
    MultiJobRankLevelService multiJobRankLevelRelationService;
    @Autowired
    OrgDutySeqService orgDutySeqService;
    @Autowired
    OrgJobGradeService orgJobGradeService;
    @Autowired
    private com.wellsoft.pt.common.generator.service.IdGeneratorService idGeneratorService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgElementAttrService multiOrgElementAttrService;
    @Autowired
    private MultiOrgElementLeaderService multiOrgElementLeaderService;
    @Autowired
    private MultiOrgElementRoleService multiOrgElementRoleService;
    @Autowired
    private MultiOrgSystemUnitService multiOrgSystemUnitService;
    @Autowired
    private MultiOrgDutyService multiOrgDutyService;
    @Autowired
    private MultiOrgSystemUnitMemberService multiOrgSystemUnitMemberService;
    @Autowired
    private MultiOrgJobRankService multiOrgJobRankService;
    @Autowired
    private MultiOrgJobDutyService multiOrgJobDutyService;
    @Autowired
    private MultiOrgOptionService multiOrgOptionService;
    @Autowired
    private MultiOrgTypeService multiOrgTypeService;
    @Autowired
    private MultiOrgUserWorkInfoService multiOrgUserWorkInfoService;
    @Autowired
    private RoleFacadeService roleService;
    @Autowired
    private MultiOrgVersionFacade orgVersionFacade;
    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;
    @Autowired
    private MultiOrgUserService multiOrgUserService;

    public static String createNewOrgElementId(IdGeneratorService idGeneratorService, String type) {
        if (IdPrefix.BUSINESS_UNIT.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgUnit.class, BUSINESS_UNIT_ID_PATTERN);
        } else if (IdPrefix.DEPARTMENT.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgDept.class, DEPT_ID_PATTERN);
        } else if (IdPrefix.JOB.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgJob.class, JOB_ID_PATTERN);
        } else if (IdPrefix.ORG.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgVersion.class, ORG_ID_PATTERN);
        } else if (IdPrefix.ORG_VERSION.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgVersion.class, ORG_VERSION_ID_PATTERN);
        } else if (IdPrefix.SYSTEM_UNIT.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgSystemUnit.class, SYSTEM_UNIT_ID_PATTERN);
        } else if (IdPrefix.DUTY.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgDuty.class, DUTY_ID_PATTERN);
        } else if (IdPrefix.RANK.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgJobRank.class, JOB_RANK_ID_PATTERN);
        }
        return null;
    }

    @Override
    public OrgTreeNode getOrgAsTreeByVersionId(String orgVersionId) {
        // 检查参数
        Assert.isTrue(StringUtils.isNotBlank(orgVersionId), "参数不能为空");
        MultiOrgVersion orgVersion = this.orgVersionFacade.getOrgVersionById(orgVersionId);
        Assert.notNull(orgVersion, "对应的组织版本不存在");
        OrgTreeNode treeNode = this.getOrgAsTreeByEleIdPath(orgVersionId, orgVersion.getId(), false);
        return treeNode;

    }

    private OrgTreeNode list2TreeNode(List<OrgTreeNodeDto> objs, Map<String, MultiOrgElement> allEleMap,
                                      boolean isInMyUnit) {
        Map<String, OrgTreeNode> map = new HashMap<String, OrgTreeNode>();
        for (OrgTreeNodeDto row : objs) {
            row.computeEleNamePath(allEleMap);
            // 转成ztree对应的树节点结构
            OrgTreeNode treeNode = row.convert2TreeNode();
            // 开始判断是否存在父节点，如果存在，则将该节点添加到对应的父节点的children中去
            String rowEleIdPath = row.getEleIdPath();
            String parentPath = row.getParentIdPath();
            if (map.containsKey(parentPath)) {
                TreeNode parentNode = map.get(parentPath);
                // 是否需要包含子单位数据
                if (isInMyUnit) {
                    // 我的单位，不包含子单位，和子组织，所以要过滤掉
                    if (!IdPrefix.ORG_VERSION.getValue().equals(row.getType())) {
                        parentNode.getChildren().add(treeNode);
                    }
                } else {
                    // 如果这个节点是个其他的系统单位，则需要递归获取这个系统单位对应的版本数据
                    if (row.getType().equals(IdPrefix.ORG_VERSION.getValue())) {
                        MultiOrgVersion version = computeOtherSystemUnitVersion(row);
                        // 版本必须是启用状态才可以，如果版本停用了，则不管是否自动更新都不能使用
                        if (version != null && version.getStatus() == 1) {
                            // 该节点切换成对应的其他系统单位的数据
                            treeNode = this.getOrgAsTreeByVersionId(version.getId());
                        }
                    }
                    // 因为有可能引用的系统单位把该类型的所有版本禁用了，则不能展示出来
                    if (treeNode != null) {
                        parentNode.getChildren().add(treeNode);
                    }

                }
            }

            map.put(rowEleIdPath, treeNode);
        }
        return map.get(objs.get(0).getEleIdPath());

    }

    /**
     * 如何描述该方法
     *
     * @param row
     * @return
     */
    private MultiOrgVersion computeOtherSystemUnitVersion(OrgTreeNodeDto row) {
        OrgTreeNodeParams params = JsonUtils.json2Object(row.getJsonParams(), OrgTreeNodeParams.class);
        MultiOrgVersion v = this.orgVersionFacade.getOrgVersionById(row.getEleId());
        // 自动升级到最新的版本
        if (params.getAutoUpgrade() == 1) {
            return this.orgVersionFacade.getCurrentActiveVersionListOfUnitAndRootVersionId(v.getSystemUnitId(),
                    v.getRootVersionId());
        } else { // 固定当前版本
            return v;
        }
    }

    @Override
    public OrgTreeNode getOrgAsTreeByEleIdPath(String orgVersionId, String eleIdPath, boolean isInMyUnit) {
        // 检查参数
        Assert.isTrue(StringUtils.isNotBlank(orgVersionId), "orgVersionId,参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(eleIdPath), "eleIdPath,参数不能为空");
        MultiOrgVersion orgVersion = this.orgVersionFacade.getOrgVersionById(orgVersionId);
        Assert.notNull(orgVersion, "对应的组织版本不存在");

        // 获取该版本所有的树节点，
        List<OrgTreeNodeDto> objs = this.multiOrgTreeNodeService.queryAllNodeOfOrgVersionByEleIdPath(orgVersionId,
                eleIdPath);
        if (objs == null) {
            return null;
        }

        Map<String, MultiOrgElement> allEleMap = this.multiOrgElementService.queryElementMapByOrgVersion(orgVersionId);
        OrgTreeNode treeNode = list2TreeNode(objs, allEleMap, isInMyUnit);
        if (treeNode.getData() instanceof OrgTreeNodeDto) {
            ((OrgTreeNodeDto) treeNode.getData()).setFunctionType(orgVersion.getFunctionType());
        }
        // 按编码调整整个组织的顺序
        return treeNode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgService#getOrgAsTreeByEleIdPathAndEleTypes(java.lang.String, java.lang.String, boolean, java.lang.String[])
     */
    @Override
    public OrgTreeNode getOrgAsTreeByEleIdPathAndEleTypes(String orgVersionId, String eleIdPath, boolean isInMyUnit,
                                                          String... eleTypes) {
        // 检查参数
        Assert.isTrue(StringUtils.isNotBlank(orgVersionId), "orgVersionId,参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(eleIdPath), "eleIdPath,参数不能为空");
        MultiOrgVersion orgVersion = this.orgVersionFacade.getOrgVersionById(orgVersionId);
        Assert.notNull(orgVersion, "对应的组织版本不存在");

        // 获取该版本所有的树节点，
        List<OrgTreeNodeDto> objs = this.multiOrgTreeNodeService
                .queryAllNodeOfOrgVersionByEleIdPathAndEleTypes(orgVersionId, eleIdPath, eleTypes);
        if (objs == null) {
            return null;
        }

        Map<String, MultiOrgElement> allEleMap = this.multiOrgElementService.queryElementMapByOrgVersion(orgVersionId);
        OrgTreeNode treeNode = list2TreeNode(objs, allEleMap, isInMyUnit);
        if (treeNode.getData() instanceof OrgTreeNodeDto) {
            ((OrgTreeNodeDto) treeNode.getData()).setFunctionType(orgVersion.getFunctionType());
        }
        // 按编码调整整个组织的顺序
        return treeNode;
    }

    // 检查一个组织节点的基本要素
    private void checkOrgTreeNodeVo(OrgTreeNodeVo nodeVo) {
        Assert.notNull(nodeVo, "参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(nodeVo.getOrgVersionId()), "组织版本ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(nodeVo.getName()), "名字不能为空");
        Assert.isTrue(StringUtils.isNotBlank(nodeVo.getCode()), "编码不能为空");
        Assert.isTrue(StringUtils.isNotBlank(nodeVo.getType()), "类型不能为空");
        Assert.isTrue(StringUtils.isNotBlank(nodeVo.getParentEleIdPath()), "父节点不能为空");
        MultiOrgVersion orgVersion = this.orgVersionFacade.getOrgVersionById(nodeVo.getOrgVersionId());
        Assert.notNull(orgVersion, "对应的组织版本不存在");
        Assert.isTrue(nodeVo.getParentEleIdPath().startsWith(orgVersion.getId()), "父级节点不能是其他系统单位。");
    }

    // 检查是否引用了重复的系统单位
    private void checkIsAddRepeatSystemUnit(OrgTreeNodeVo nodeVo) {
        String otherSystemUnitId = nodeVo.getParams().getSystemUnitId();
        MultiOrgTreeNode otherSystemUnit = this.multiOrgTreeNodeService
                .getOtherSystemUnitByOrgVersionId(otherSystemUnitId, nodeVo.getOrgVersionId());
        if (otherSystemUnit != null) {
            throw new RuntimeException("该版本已经引入过该系统单位，不能重复引用");
        }
    }

    /**
     * 添加子节点
     */
    @Override
    @Transactional
    public MultiOrgTreeNode addOrgChildNode(OrgTreeNodeVo nodeVo) {
        // 检查参数
        checkOrgTreeNodeVo(nodeVo);
        MultiOrgTreeNode parentNode = this.multiOrgTreeNodeService.getParentNode(nodeVo.getOrgVersionId(),
                nodeVo.getParentEleIdPath());
        Assert.notNull(parentNode, "对应的父级节点不存在");
        String type = nodeVo.getType();
        // 参数没有问题，处理业务逻辑
        // 添加的是一个单位节点，是引用关系，所以只需要维护子单位和父单位间的关系就可以
        if (type.equals(IdPrefix.ORG_VERSION.getValue())) {
            // 检查一个系统单位只能引入一个版本
            checkIsAddRepeatSystemUnit(nodeVo);
            // 同步数据到组织树中去，添加子节点数据
            MultiOrgTreeNode node = new MultiOrgTreeNode();
            // MultiOrgVersion ver =
            // this.orgVersionFacade.getOrgVersionById(nodeVo.getEleId());
            // 因为要计算自动更新的版本，所以这里的eleId，设置的是rootVersionId,方便将来搜索
            node.setEleId(nodeVo.getEleId());
            node.setEleIdPath(parentNode.getEleIdPath() + PATH_SPLIT_SYSMBOL + nodeVo.getEleId());
            node.setJsonParams(JsonUtils.object2Json(nodeVo.getParams()));
            node.setOrgVersionId(nodeVo.getOrgVersionId());
            this.multiOrgTreeNodeService.save(node);
            return node;
        } else {
            // 非单位节点
            // 添加一个新的节点元素信息
            MultiOrgElement ele = this.addNewOrgElementFromOrgTreeNodeVo(nodeVo);

            // 添加节点元素对应的管理者信息
            this.addOrgElementLeaderList(ele.getId(), nodeVo);

            // 添加节点元素对应的角色信息
            this.addOrgElementRoleList(ele.getId(), nodeVo);
            // 需要发布角色变更事件
            if (StringUtils.isNotBlank(nodeVo.getRoleUuids())) {
                String[] roles = nodeVo.getRoleUuids().split(";");
                for (String rUuid : roles) {
                    roleService.publishRoleUpdatedEvent(rUuid);
                }
            }

            // 职位节点，则需要检查是否设置了职位与职务的关系
            if (nodeVo.getType().equals(IdPrefix.JOB.getValue())) {
                this.addJobDuty(ele.getId(), nodeVo);
            }

            // 同步数据到组织树中去，添加子节点数据
            MultiOrgTreeNode node = new MultiOrgTreeNode();
            node.setEleId(ele.getId());
            node.setEleIdPath(parentNode.getEleIdPath() + PATH_SPLIT_SYSMBOL + ele.getId());
            node.setOrgVersionId(nodeVo.getOrgVersionId());
            this.multiOrgTreeNodeService.save(node);
            if (CollectionUtils.isNotEmpty(nodeVo.getOrgElementAttrs())) {
                for (OrgElementAttrVo avo : nodeVo.getOrgElementAttrs()) {
                    avo.setElementUuid(ele.getUuid());
                }
                this.multiOrgElementAttrService.saveDtos(nodeVo.getOrgElementAttrs());
            }

            return node;
        }

    }

    // 设置职位的归属职务
    private void addJobDuty(String jobId, OrgTreeNodeVo nodeVo) {
        if (StringUtils.isNotBlank(nodeVo.getDutyId())) {
            MultiOrgJobDuty jobDuty = new MultiOrgJobDuty();
            jobDuty.setJobId(jobId);
            jobDuty.setDutyId(nodeVo.getDutyId());
            this.multiOrgJobDutyService.save(jobDuty);
        }
    }

    // 添加角色信息
    private void addOrgElementRoleList(String eleId, OrgTreeNodeVo nodeVo) {
        if (StringUtils.isNotBlank(nodeVo.getRoleUuids())) {
            this.multiOrgElementRoleService.addRoleListOfElement(eleId, nodeVo.getRoleUuids());
        }
    }

    private void addOrgElementLeaderList(String eleId, OrgTreeNodeVo nodeVo) {
        // 添加该职位的分管领导
        if (StringUtils.isNotBlank(nodeVo.getBranchLeaderIdPaths())) {
            this.multiOrgElementLeaderService.addLeaderListByType(eleId, nodeVo.getOrgVersionId(),
                    nodeVo.getBranchLeaderIdPaths(), nodeVo.getBranchLeaderNames(),
                    MultiOrgElementLeader.TYPE_BRANCHED_LEADER);
        }
        // 添加对应的负责人
        if (StringUtils.isNotBlank(nodeVo.getBossIdPaths())) {
            this.multiOrgElementLeaderService.addLeaderListByType(eleId, nodeVo.getOrgVersionId(),
                    nodeVo.getBossIdPaths(), nodeVo.getBossNames(), MultiOrgElementLeader.TYPE_BOSS);
        }
        // 添加对应的管理员
        if (StringUtils.isNotBlank(nodeVo.getManagerIdPaths())) {
            this.multiOrgElementLeaderService.addLeaderListByType(eleId, nodeVo.getOrgVersionId(),
                    nodeVo.getManagerIdPaths(), nodeVo.getManagerNames(), MultiOrgElementLeader.TYPE_MANAGER);
        }
    }

    // 添加一个新的节点元素信息
    private MultiOrgElement addNewOrgElementFromOrgTreeNodeVo(OrgTreeNodeVo nodeVo) {
        String newEleId = createNewOrgElementId(idGeneratorService, nodeVo.getType());
        if (StringUtils.isBlank(newEleId)) {
            Assert.isTrue(false, "节点类型错误");
        }
        nodeVo.setEleId(newEleId);
        MultiOrgElement ele = new MultiOrgElement();
        ele.setAttrFromOrgTreeNodeVo(nodeVo, false);
        this.multiOrgElementService.save(ele);
        return ele;
    }

    /**
     * 获取组织节点信息
     */
    @Override
    public OrgTreeNodeVo getOrgNodeByTreeUuid(String orgTreeUuid) {
        // 检查参数
        Assert.isTrue(StringUtils.isNotBlank(orgTreeUuid), "参数UUID不能为空");
        OrgTreeNodeDto nodeDto = this.getOrgTreeNodeDto(orgTreeUuid);
        Assert.notNull(nodeDto, "对应的组织节点不存在");
        OrgTreeNodeVo vo = new OrgTreeNodeVo();
        vo.setAttrFromOrgNode(nodeDto);
        String type = nodeDto.getType();
        // 单位节点，需要转化jsonParams为对象
        if (type.equals(IdPrefix.ORG_VERSION.getValue())) {
            vo.setParams(JsonUtils.json2Object(nodeDto.getJsonParams(), OrgTreeNodeParams.class));
        } else {
            // 单位节点是引用关系，不能去计算其他版本的引用关系，
            List<MultiOrgVersion> otherVer = this.queryOtherOrgVersionListByEleId(vo.getEleId(), vo.getOrgVersionId());
            vo.setOtherVersion(otherVer);
        }

        if (IdPrefix.JOB.getValue().equals(vo.getType())) { // 职位节点
            this.setJobDutyList(vo);
        } else { // 单位部门节点
            // 获取分管领导信息
            this.setBranchList(vo);
            // 获取负责人信息
            this.setBossList(vo);
            // 获取管理员信息
            this.setManagerList(vo);
        }

        vo.setOrgElementAttrs(this.multiOrgElementAttrService.listByElementUuid(vo.getEleUuid()));

        // 获取角色信息
        List<MultiOrgElementRole> roleList = this.queryRoleListOfElement(vo.getEleId());
        String roleUuids = ListUtils.list2StringsByField(roleList, "roleUuid");
        vo.setRoleUuids(roleUuids);

        return vo;
    }

    private void setJobDutyList(OrgTreeNodeVo vo) {
        MultiOrgJobDuty jobDuty = this.multiOrgJobDutyService.getJobDutyByJobId(vo.getEleId());
        if (jobDuty != null) {
            vo.setDutyId(jobDuty.getDutyId());
            MultiOrgDuty duty = multiOrgDutyService.getById(jobDuty.getDutyId());
            if (StringUtils.isNotBlank(duty.getJobRank())) {
                List<MultiOrgJobRank> jobRankList = multiOrgJobRankService.getMultiOrgJobRankByJobRankId(duty.getJobRank().split(","));
                List<String> collect = jobRankList.stream().map(jobRank -> jobRank.getJobRank()).collect(Collectors.toList());
                vo.setDutyName(duty.getName() + "-" + StringUtils.join(collect, ","));
            } else {
                vo.setDutyName(duty.getName());
            }


        }
    }

    @Override
    public MultiOrgJobDuty getJobDutyByJobId(String jobId) {
        return this.multiOrgJobDutyService.getJobDutyByJobId(jobId);
    }

    private void setBranchList(OrgTreeNodeVo vo) {
        List<MultiOrgElementLeader> branchList = this.multiOrgElementLeaderService.queryLeaderListByType(vo.getEleId(),
                vo.getOrgVersionId(), MultiOrgElementLeader.TYPE_BRANCHED_LEADER);
        String branchDeptIds = MultiOrgElementLeader.leaderList2idPaths(branchList);
        String branchNames = getNamesFromLeadList(branchList);
        vo.setBranchLeaderIdPaths(branchDeptIds);
        vo.setBranchLeaderNames(branchNames);
    }

    private String getNamesFromLeadList(List<MultiOrgElementLeader> list) {
        ArrayList<String> names = new ArrayList<String>();
        for (MultiOrgElementLeader leader : list) {
            // 管理员是人
            if (leader.getTargetObjId().startsWith(IdPrefix.USER.getValue())) {
                MultiOrgUserAccount a = this.multiOrgUserService.getAccountByUserId(leader.getTargetObjId());
                if (null != a) {
                    names.add(a.getUserName());
                }
            } else { // 负责人是职位
                MultiOrgElement ele = this.multiOrgElementService.getById(leader.getTargetObjId());
                if (null != ele) {
                    names.add(ele.getName());
                }
            }
        }
        return StringUtils.join(names, ";");
    }

    private void setBossList(OrgTreeNodeVo vo) {
        List<MultiOrgElementLeader> bossList = this.multiOrgElementLeaderService.queryLeaderListByType(vo.getEleId(),
                vo.getOrgVersionId(), MultiOrgElementLeader.TYPE_BOSS);
        String bossIds = MultiOrgElementLeader.leaderList2idPaths(bossList);
        String bossNames = getNamesFromLeadList(bossList);
        vo.setBossIdPaths(bossIds);
        vo.setBossNames(bossNames);
    }

    private void setManagerList(OrgTreeNodeVo vo) {
        List<MultiOrgElementLeader> managerList = this.multiOrgElementLeaderService.queryLeaderListByType(vo.getEleId(),
                vo.getOrgVersionId(), MultiOrgElementLeader.TYPE_MANAGER);
        String managerIds = MultiOrgElementLeader.leaderList2idPaths(managerList);
        String managerNames = getNamesFromLeadList(managerList);
        vo.setManagerIdPaths(managerIds);
        vo.setManagerNames(managerNames);
    }

    @Override
    public List<String> queryBossListByEleId(String eleId, String orgVersionId) {
        List<MultiOrgElementLeader> bossList = this.multiOrgElementLeaderService.queryLeaderListByType(eleId, orgVersionId, MultiOrgElementLeader.TYPE_BOSS);
        List<String> collect = bossList.stream().map(MultiOrgElementLeader::getTargetObjId).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<MultiOrgElementRole> queryRoleListOfElement(String eleId) {
        return this.multiOrgElementRoleService.queryRoleListOfElement(eleId);
    }

    @Override
    public List<MultiOrgElementRole> queryRoleListOfElementByRoleUuid(String roleUuid) {
        return this.multiOrgElementRoleService.queryElementByRole(roleUuid);
    }

    // 获取节点元素，被其他版本引用的情况
    private List<MultiOrgVersion> queryOtherOrgVersionListByEleId(String eleId, String orgVersionId) {
        List<MultiOrgTreeNode> objs = this.multiOrgTreeNodeService.queryOtherOrgVersionListByEleId(eleId, orgVersionId);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        List<MultiOrgVersion> list = new ArrayList<MultiOrgVersion>();
        for (MultiOrgTreeNode multiOrgRelation : objs) {
            MultiOrgVersion dto = this.orgVersionFacade.getOrgVersionById(multiOrgRelation.getOrgVersionId());
            list.add(dto);
        }
        return list;
    }

    /**
     * isUnbind 是否解绑，是：插入一个新节点，继承的版本的节点信息不变，否：所有版本的节点信息都跟着变化
     */
    @Override
    @Transactional
    public boolean modifyOrgChildNode(OrgTreeNodeVo nodeVo, boolean isUnbind) {
        return modifyOrgChildNode(nodeVo, isUnbind, true);
    }

    @Override
    @Transactional
    public boolean modifyOrgChildNode(OrgTreeNodeVo nodeVo, boolean isUnbind, boolean includeChildNode) {
        // 检查参数
        checkOrgTreeNodeVo(nodeVo);
        MultiOrgTreeNode newParentNode = this.multiOrgTreeNodeService.getParentNode(nodeVo.getOrgVersionId(),
                nodeVo.getParentEleIdPath());
        Assert.notNull(newParentNode, "对应的父级节点不存在");
        MultiOrgTreeNode oldTreeNode = this.multiOrgTreeNodeService.getOne(nodeVo.getUuid());
        Assert.notNull(oldTreeNode, "节点不存在");
        MultiOrgElement oldEle = this.multiOrgElementService.getById(oldTreeNode.getEleId());
        Assert.notNull(oldTreeNode, "节点元素不存在");
        if (nodeVo.getParentEleIdPath().startsWith(oldTreeNode.getEleIdPath())) {
            Assert.notNull("父节点不能是自己或自己的子节点", null);
        }
        String nodeType = nodeVo.getType();
        // 修改的是单位节点,单位节点只是个引用关系，所以只需要变更对应的关系就可以了，其他信息不变
        if (nodeType.equals(IdPrefix.ORG_VERSION.getValue())) {
            String otherSystemUnitId = nodeVo.getParams().getSystemUnitId();
            // 找不到对应的系统单位ID,说明变化了一个单位，需要重新检查是否重复引用的问题
            if (oldTreeNode.getJsonParams().indexOf(otherSystemUnitId) < 0) {
                checkIsAddRepeatSystemUnit(nodeVo);
            }
            oldTreeNode.setEleId(nodeVo.getEleId());
            oldTreeNode.setEleIdPath(newParentNode.getEleIdPath() + PATH_SPLIT_SYSMBOL + nodeVo.getEleId());
            oldTreeNode.setJsonParams(JsonUtils.object2Json(nodeVo.getParams()));
            this.multiOrgTreeNodeService.save(oldTreeNode);
            this.multiOrgElementAttrService.deleteByElementUuid(oldEle.getUuid());
            // 集团单位-系统单位中少了自定义属性
            if (CollectionUtils.isNotEmpty(nodeVo.getOrgElementAttrs())) {
                for (OrgElementAttrVo avo : nodeVo.getOrgElementAttrs()) {
                    avo.setElementUuid(oldEle.getUuid());
                }
                this.multiOrgElementAttrService.saveDtos(nodeVo.getOrgElementAttrs());
            }
        } else {
            // 参数没有问题，处理业务逻辑
            String oldEleIdPath = oldTreeNode.getEleIdPath();
            String newEleIdPath = newParentNode.getEleIdPath() + PATH_SPLIT_SYSMBOL + oldTreeNode.getEleId();
            String orgVersionId = oldTreeNode.getOrgVersionId();
            if (isUnbind) {
                // 解绑关系，插入一个新组织组织元素，产生一个新的节点ID,并变更当前的节点关系
                // 插入一个节点元素
                MultiOrgElement newEle = this.addNewOrgElementFromOrgTreeNodeVo(nodeVo);
                // 插入新元素对应的管理者信息
                // 因为升级的时候，复制了一份数据，所以需要先删除旧数据, 然后插入新数据
                this.multiOrgElementAttrService.deleteByElementUuid(oldEle.getUuid());
                this.multiOrgElementLeaderService.deleteLeaderList(oldEle.getId(), nodeVo.getOrgVersionId());
                if (CollectionUtils.isNotEmpty(nodeVo.getOrgElementAttrs())) {
                    for (OrgElementAttrVo avo : nodeVo.getOrgElementAttrs()) {
                        avo.setElementUuid(newEle.getUuid());
                    }
                    this.multiOrgElementAttrService.saveDtos(nodeVo.getOrgElementAttrs());
                }
                this.addOrgElementLeaderList(newEle.getId(), nodeVo);
                // 插入新元素对应的角色信息
                this.addOrgElementRoleList(newEle.getId(), nodeVo);
                // 插入新元素对应的职
                if (nodeType.equals(IdPrefix.JOB.getValue())) {
                    this.addJobDuty(newEle.getId(), nodeVo);
                }
                // 变更节点关系
                newEleIdPath = newParentNode.getEleIdPath() + PATH_SPLIT_SYSMBOL + newEle.getId();
                oldTreeNode.setEleId(newEle.getId());
                oldTreeNode.setEleIdPath(newEleIdPath);
                this.multiOrgTreeNodeService.save(oldTreeNode);
                // 因为新节点是新ID,所以这里要从新计算下newEleIdPath
                newEleIdPath = newParentNode.getEleIdPath() + PATH_SPLIT_SYSMBOL + newEle.getId();
                // 检查名字和父级节点关系，来确定是否要更新所有子节点的名称全路径和ID全路径
                this.multiOrgTreeNodeService.modifyNodeAndAllChildrenNodePathByOrgVersion(oldEleIdPath, newEleIdPath,
                        orgVersionId, nodeVo.getOrgVersionId());

                // 因为解绑，所以变更了节点元素的ID,所以对应该节点下的用户也需要变更过来，迁移到该节点下面
                this.multiOrgUserService.dealElementIdChangeEvent(oldEle.getId(), newEle.getId(), orgVersionId);
                // 因为MultiOrgElementLeader表是双向关系，所以旧节点作为targetObjId的数据，需要复制一份，同时变更为新节点ID
                this.multiOrgElementLeaderService.dealElementIdChangeEvent(oldEle.getId(), newEle.getId(),
                        orgVersionId);

                // 如果修改的是业务单位节点，则需要检查该节点是不是系统单位本身对应的业务单位节点，如果是的话，
                // 需要同步变更multi_org_verison中的关联字段self_bussiness_unit_id，否则
                // 组织版本升级的的时候会出现问题
                if (IdPrefix.BUSINESS_UNIT.getValue().equals(nodeType)) {
                    MultiOrgVersion ver = this.orgVersionFacade.getOrgVersionById(oldTreeNode.getOrgVersionId());
                    // 是系统单位本身对应的业务单位ID,需要变更记录
                    if (ver.getSelfBusinessUnitId().equals(oldEle.getId())) {
                        this.orgVersionFacade.updateSelfBussinessUnitId(ver.getId(), newEle.getId());
                    }
                }

                // 因为解绑操作，ID肯定发生变化，所以需要重新计算这个单位的所有用户work_info信息,因为用户可能是多组织的，所以直接重算整个单位的更快
                // String unitId = SpringSecurityUtils.getCurrentUserUnitId();
                // multiOrgUserService.recomputeUserWorkInfoByUnit(unitId);

            } else {
                // 不解绑，因为是引用关系，所以只需要把对应的节点信息变更就可以了
                // 更新节点元素信息
                oldEle.setAttrFromOrgTreeNodeVo(nodeVo, true);
                this.multiOrgElementService.save(oldEle);
                // 更新节点元素的管理者信息, 采用 删除掉旧的数据，然后插入新数据的做法来处理，比较快速
                this.multiOrgElementLeaderService.deleteLeaderList(oldEle.getId(), nodeVo.getOrgVersionId());
                this.addOrgElementLeaderList(oldEle.getId(), nodeVo);
                this.multiOrgElementAttrService.deleteByElementUuid(oldEle.getUuid());
                if (CollectionUtils.isNotEmpty(nodeVo.getOrgElementAttrs())) {
                    for (OrgElementAttrVo avo : nodeVo.getOrgElementAttrs()) {
                        avo.setElementUuid(oldEle.getUuid());
                    }
                    this.multiOrgElementAttrService.saveDtos(nodeVo.getOrgElementAttrs());
                }
                // 更新节点元素的角色信息，采用 删除掉旧的数据，然后插入新数据的做法来处理，比较快速
                List<MultiOrgElementRole> oldRoles = this.multiOrgElementRoleService
                        .queryRoleListOfElement(oldEle.getId());
                this.multiOrgElementRoleService.deleteRoleListOfElement(oldEle.getId());
                this.addOrgElementRoleList(oldEle.getId(), nodeVo);
                // 角色信息变更 ，需要发布一下，不管是删除，还是新增，或是修改，对应的角色都需要发布下
                Set<String> roleUuids = Sets.newHashSet();
                // 旧角色需要放进来
                if (oldRoles != null) {
                    for (MultiOrgElementRole r : oldRoles) {
                        roleUuids.add(r.getRoleUuid());
                    }
                }
                // 新角色需要放进来
                if (StringUtils.isNotBlank(nodeVo.getRoleUuids())) {
                    roleUuids.addAll(Lists.newArrayList(nodeVo.getRoleUuids().split(";")));
                }
                for (String roleUuid : roleUuids) {
                    roleService.publishRoleUpdatedEvent(roleUuid);
                }

                if (nodeType.equals(IdPrefix.JOB.getValue())) {
                    // 更新职位对应的职务信息
                    this.multiOrgJobDutyService.deleteJobDutyByJobId(oldEle.getId());
                    this.addJobDuty(oldEle.getId(), nodeVo);
                }

                // 更新树节点关系,因为是同步，所以需要更新所有版本, 需要更新所有版本，这是因为如果只是升级了版本，新版本
                if (includeChildNode) {
                    List<MultiOrgTreeNode> objs = this.multiOrgTreeNodeService.queryNodeByEleId(oldEle.getId());
                    for (MultiOrgTreeNode multiOrgTreeNode : objs) {
                        this.multiOrgTreeNodeService.modifyNodeAndAllChildrenNodePathByOrgVersion(oldEleIdPath,
                                newEleIdPath, multiOrgTreeNode.getOrgVersionId(), nodeVo.getOrgVersionId());
                    }
                    // 节点的位置发生变化，则需要重新计算用户的工作信息
                    // if (!oldEleIdPath.equalsIgnoreCase(newEleIdPath)) {
                    // String unitId =
                    // SpringSecurityUtils.getCurrentUserUnitId();
                    // multiOrgUserService.recomputeUserWorkInfoByUnit(unitId);
                    // }
                } else if (false == oldEleIdPath.equalsIgnoreCase(newEleIdPath)) {
                    List<MultiOrgTreeNode> objs = multiOrgTreeNodeService.queryNodeByEleIdPath(oldEleIdPath);
                    for (MultiOrgTreeNode multiOrgTreeNode : objs) {
                        multiOrgTreeNode.setEleIdPath(nodeVo.getEleIdPath());
                        multiOrgTreeNode.setOrgVersionId(nodeVo.getOrgVersionId());
                        multiOrgTreeNodeService.save(multiOrgTreeNode);
                    }
                    multiOrgUserService.recomputeUserWorkInfoByEleId(nodeVo.getOrgVersionId(), nodeVo.getEleId());
                }
            }
        }
        return true;
    }

    /**
     * 删除节点，需要检查，如果存在子节点，则不能删除
     */
    @Override
    public boolean deleteOrgChildNode(String treeNodeUuId) {
        // 检查参数
        Assert.isTrue(StringUtils.isNotBlank(treeNodeUuId), "节点ID不能为空");

        MultiOrgTreeNode obj = this.multiOrgTreeNodeService.getOne(treeNodeUuId);
        Assert.notNull(obj == null, "对应的节点不存在");

        // 检查是否有子节点
        List<OrgTreeNodeDto> list = this.multiOrgTreeNodeService
                .queryAllChildrenNodeOfOrgVersionByEleIdPath(obj.getOrgVersionId(), obj.getEleIdPath());
        if (!CollectionUtils.isEmpty(list)) {
            Assert.isTrue(false, "该节点存在子节点，无法删除");
        }

        // 是个职位的节点，则需要检查该节点下是否有用户，有则不能删除
        if (obj.getEleId().startsWith(IdPrefix.JOB.getValue())) {
            List<OrgUserTreeNodeDto> users = this.multiOrgApiFacade.queryUserByOrgTreeNode(obj.getEleId(),
                    obj.getOrgVersionId());
            Assert.isTrue(CollectionUtils.isEmpty(users), "该职位存在用户，无法删除");
        }

        // 删除记录
        this.multiOrgTreeNodeService.delete(obj.getUuid());
        // 删除相关的领导关系
        this.multiOrgElementLeaderService.deleteAllLeaderByEleId(obj.getEleId(), obj.getOrgVersionId());
        //
        List<MultiOrgElementRole> oldRoles = this.multiOrgElementRoleService.queryRoleListOfElement(obj.getEleId());
        // 删除该节点对应的角色信息
        this.multiOrgElementRoleService.deleteRoleListOfElement(obj.getEleId());

        // 需要发布角色变更事件
        if (CollectionUtils.isNotEmpty(oldRoles)) {
            for (MultiOrgElementRole er : oldRoles) {
                roleService.publishRoleUpdatedEvent(er.getRoleUuid());
            }
        }

        return true;
    }

    /**
     * 获取组织树节点信息
     */
    @Override
    public OrgTreeNodeDto getOrgTreeNodeDto(String uuid) {
        OrgTreeNodeDto dto = this.multiOrgTreeNodeService.getNode(uuid);
        Map<String, MultiOrgElement> allEleMap = this.queryElementMapByEleIdPath(dto.getEleIdPath());
        dto.computeEleNamePath(allEleMap);
        return dto;
    }

    // TODO 需要缓存
    private Map<String, MultiOrgElement> queryElementMapByOrgVersion(String orgVersionId) {
        return this.multiOrgElementService.queryElementMapByOrgVersion(orgVersionId);
    }

    @Override
    public OrgTreeNodeDto getNodeByEleIdAndOrgVersion(String eleId, String orgVersionId) {
        return this.multiOrgTreeNodeService.getNodeByEleIdAndOrgVersion(eleId, orgVersionId);
    }

    // 我分管的下属节点
    @Override
    public List<OrgTreeNodeDto> queryBranchUnderlingNodeListByJobId(String jobId, String orgVersionId) {
        List<MultiOrgElementLeader> objs = this.multiOrgElementLeaderService.queryMyBranchUnderlingListByEleId(jobId,
                orgVersionId);
        List<OrgTreeNodeDto> list = new ArrayList<OrgTreeNodeDto>();
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgElementLeader obj : objs) {
                String underlingId = obj.getEleId();
                String objVersionId = obj.getEleOrgVersionId();
                OrgTreeNodeDto dto = this.getNodeByEleIdAndOrgVersion(underlingId, objVersionId);
                list.add(dto);
            }
        }
        return list;
    }

    // 获取我负责的下属
    @Override
    public List<OrgTreeNodeDto> queryBossUnderlingNodeListByJobId(String jobId, String orgVersionId) {
        List<MultiOrgElementLeader> objs = this.multiOrgElementLeaderService.queryMyBossUnderlingListByEleId(jobId,
                orgVersionId);
        List<OrgTreeNodeDto> list = new ArrayList<OrgTreeNodeDto>();
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgElementLeader obj : objs) {
                // 单位内下属，eleId是下属，targetObjId是领导，因为单位下属肯定是同一版本内，所以用orgVersionId就可以了
                String underlingId = obj.getEleId();
                OrgTreeNodeDto dto = this.getNodeByEleIdAndOrgVersion(underlingId, orgVersionId);
                if (dto != null) {
                    list.add(dto);
                }
            }
        }
        return list;

    }

    @Override
    public List<OrgTreeNodeDto> queryOrgNodeListByRole(String roleUuid) {
        List<OrgTreeNodeDto> list = new ArrayList<OrgTreeNodeDto>();
        List<MultiOrgElementRole> objs = this.multiOrgElementRoleService.queryElementByRole(roleUuid);
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgElementRole elementRole : objs) {
                OrgTreeNodeDto dto = this.getNodeOfCurrentVerisonByEleId(elementRole.getEleId());
                if (dto != null) {
                    list.add(dto);
                }
            }
        }
        return list;
    }

    /**
     * 角色删除了，对应的引用了 该角色的组织节点也需要删除关联关系
     */
    @Override
    public boolean dealRoleRemoveEvent(String roleUuid) {
        this.multiOrgElementRoleService.deleteElementListOfRole(roleUuid);
        return true;
    }

    @Override
    public TreeNode getOrgNodePrivilegeResultTree(String eleId) {
        MultiOrgElement ele = this.getOrgElementById(eleId);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(ele.getName());
        treeNode.setId(TreeNode.ROOT_ID);
        List<MultiOrgElementRole> roleList = this.multiOrgElementRoleService.queryRoleListOfElement(ele.getId());
        if (!CollectionUtils.isEmpty(roleList)) {
            List<TreeNode> children = new ArrayList<TreeNode>();
            for (MultiOrgElementRole eleRole : roleList) {
                Role role = this.roleService.get(eleRole.getRoleUuid());
                TreeNode child = new TreeNode();
                child.setId(role.getUuid());
                child.setName(role.getName());
                children.add(child);
                this.roleService.buildRoleNestedRoleTree(child, role);
            }
            treeNode.setChildren(children);
        }
        return treeNode;
    }

    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public MultiOrgElement getOrgElementById(String eleId) {
        return this.multiOrgElementService.getById(eleId);
    }

    /**
     * 批量获取组织元素
     */
    @Override
    public List<MultiOrgElement> queryOrgElementListByIds(Collection<String> eleIds) {
        List<MultiOrgElement> list = new ArrayList<MultiOrgElement>();
        if (!CollectionUtils.isEmpty(eleIds)) {
            List<String> eleIdList = Lists.newArrayList();
            for (String eleId : eleIds) {
                eleIdList.add(eleId);
            }
            list = this.multiOrgElementService.getOrgElementsByIds(eleIdList);
        }
        return list;
    }

    /**
     * 新增一个单位信息
     */
    @Override
    @Transactional
    public OrgSystemUnitVo addSystemUnit(OrgSystemUnitVo vo) {
        Assert.isTrue(vo != null, "参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getName()), "单位名称不能为空");
        // 检查名字是否重复
        MultiOrgSystemUnit obj = this.multiOrgSystemUnitService.getByName(vo.getName());
        Assert.isTrue(obj == null, "单位名称已经存在，请更换一个名称");
        // 参数没有问题，处理业务逻辑
        // 添加一个新的系统单位
        MultiOrgSystemUnit unit = new MultiOrgSystemUnit();
        BeanUtils.copyProperties(vo, unit);
        String newId = createNewOrgElementId(idGeneratorService, IdPrefix.SYSTEM_UNIT.getValue());
        unit.setId(newId);
        vo.setId(newId);
        this.multiOrgSystemUnitService.save(unit);
        vo.setUuid(unit.getUuid());
        // 保存成员信息
        this.addSystemUnitMembers(vo);

        // 保存自定义属性
        if (CollectionUtils.isNotEmpty(vo.getOrgElementAttrs())) {
            for (OrgElementAttrVo avo : vo.getOrgElementAttrs()) {
                avo.setElementUuid(unit.getUuid());
            }
            this.multiOrgElementAttrService.saveDtos(vo.getOrgElementAttrs());
        }

        return vo;
    }

    private void addSystemUnitMembers(OrgSystemUnitVo vo) {
        if (vo.getIsGroupUnit() != null && vo.getIsGroupUnit() == 1) { // 集团单位
            if (StringUtils.isNotBlank(vo.getMembers())) {
                String[] members = vo.getMembers().split(";");
                for (String member : members) {
                    MultiOrgSystemUnitMember m = new MultiOrgSystemUnitMember();
                    m.setSystemUnitId(vo.getId());
                    m.setMemberUnitId(member);
                    this.multiOrgSystemUnitMemberService.save(m);
                }

            }
        }
    }

    /**
     * 修改单位基本信息
     */
    @Override
    @Transactional
    public OrgSystemUnitVo modifySystemUnit(OrgSystemUnitVo vo) {
        Assert.isTrue(vo != null, "参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getName()), "单位名称不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getUuid()), "UUID不能为空");
        MultiOrgSystemUnit oldUnit = this.multiOrgSystemUnitService.getOne(vo.getUuid());
        Assert.isTrue(oldUnit != null, "对应的单位不存在");
        // 名字发生变化
        if (!vo.getName().equals(oldUnit.getName())) {
            MultiOrgSystemUnit obj = this.multiOrgSystemUnitService.getByName(vo.getName());
            Assert.isTrue(obj == null, "单位名称已经存在，请更换一个名称");
        }

        if (vo.getIsGroupUnit() != null && vo.getIsGroupUnit() == 1) {
            // 自己不能包含自己
            if (vo.getMembers().indexOf(oldUnit.getId()) >= 0) {
                Assert.isTrue(false, "单位成员不能是自己！");
            }

            // 集团单位，需要检查成员是否出现嵌套包含，会造成组织死循环
            String[] members = vo.getMembers().split(";");
            for (String memberId : members) {
                if (this.multiOrgSystemUnitMemberService.isMember(memberId, oldUnit.getId(), true)) {
                    MultiOrgSystemUnit memberUnit = this.multiOrgSystemUnitService.getById(memberId);
                    Assert.isTrue(false, memberUnit.getName() + "已经包含该单位，不允许互相嵌套包含");
                }
            }
        }

        // ID
        String[] excludeFields = new String[]{"id"};
        BeanUtils.copyPropertiesExcludeBaseField(vo, oldUnit, excludeFields);
        this.multiOrgSystemUnitService.save(oldUnit);

        // 删除所有的成员，重新插入新成员
        this.multiOrgSystemUnitMemberService.deleteMemberListOfUnit(oldUnit.getId());
        this.addSystemUnitMembers(vo);

        // 保存自定义属性
        if (CollectionUtils.isNotEmpty(vo.getOrgElementAttrs())) {
            this.multiOrgElementAttrService.deleteByElementUuid(vo.getUuid());
            for (OrgElementAttrVo avo : vo.getOrgElementAttrs()) {
                avo.setElementUuid(vo.getUuid());
            }
            this.multiOrgElementAttrService.saveDtos(vo.getOrgElementAttrs());
        }

        return vo;
    }

    @Override
    public OrgSystemUnitVo getSystemUnitVo(String uuid) {
        // 检查参数
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数UUID不能为空");
        MultiOrgSystemUnit oldUnit = this.multiOrgSystemUnitService.getOne(uuid);
        Assert.notNull(oldUnit, "对应的单位不存在");
        OrgSystemUnitVo vo = new OrgSystemUnitVo();
        BeanUtils.copyProperties(oldUnit, vo);
        if (oldUnit.getIsGroupUnit() != null && oldUnit.getIsGroupUnit() == 1) {
            List<MultiOrgSystemUnitMember> memberList = this.multiOrgSystemUnitMemberService
                    .queryMemberListOfUnit(oldUnit.getId(), false);
            if (!CollectionUtils.isEmpty(memberList)) {
                String members = ListUtils.list2StringsByField(memberList, "memberUnitId");
                vo.setMembers(members);
            }
        }

        vo.setOrgElementAttrs(this.multiOrgElementAttrService.listByElementUuid(uuid));

        return vo;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgService#getSystemUnitById(java.lang.String)
     */
    @Override
    public MultiOrgSystemUnit getSystemUnitById(String systemUnitId) {
        return this.multiOrgSystemUnitService.getById(systemUnitId);
    }

    @Override
    public List<OrgTreeNodeDto> computeMyCompanyListByVersionId(MultiOrgVersion ver) {
        List<OrgTreeNodeDto> list = new ArrayList<OrgTreeNodeDto>();
        // 获取引用了该组织的上级组织
        List<MultiOrgTreeNode> parentList = this.multiOrgTreeNodeService
                .queryParentSystemUnitOrg(ver.getRootVersionId());
        if (CollectionUtils.isEmpty(parentList)) {
            // 没有上级组织，则返回自己
            OrgTreeNodeDto dto = this.getNodeByEleIdAndOrgVersion(ver.getId(), ver.getId());
            dto.setFunctionType(ver.getFunctionType());
            list.add(dto);
        } else {
            // 有上级组织，递归查找上级
            for (MultiOrgTreeNode multiOrgTreeNode : parentList) {
                MultiOrgVersion parentVer = this.orgVersionFacade.getOrgVersionById(multiOrgTreeNode.getOrgVersionId());
                // 上级组织如果被禁用，则剔除
                if (parentVer != null && parentVer.getStatus() == 1) {
                    List<OrgTreeNodeDto> parent = this.computeMyCompanyListByVersionId(parentVer);
                    if (parent != null) {
                        list.addAll(parent);
                    }
                }
            }
        }
        return list;
    }

    // 获取指定节点的单位领导
    @Override
    public List<OrgTreeNodeDto> queryBossLeaderNodeListByNode(String eleId, String eleOrgVersionId) {
        List<OrgTreeNodeDto> list = new ArrayList<OrgTreeNodeDto>();
        List<MultiOrgElementLeader> leaders = this.multiOrgElementLeaderService.queryMyBossLeaderListByEleId(eleId,
                eleOrgVersionId);
        if (!CollectionUtils.isEmpty(leaders)) {
            Map<String, MultiOrgElement> allEleMap = this.queryElementMapByOrgVersion(eleOrgVersionId);
            for (MultiOrgElementLeader leader : leaders) {
                String leaderId = leader.getTargetObjId();
                // 单位领导， leaderObjId是 领导，eleId是下属, 单位负责人，肯定是同一组织内的，所以用
                // eleOrgVersionId
                OrgTreeNodeDto dto = this.getNodeByEleIdAndOrgVersion(leaderId, eleOrgVersionId);
                if (dto != null) {
                    dto.computeEleNamePath(allEleMap);
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // 获取指定节点的分管领导
    @Override
    public List<OrgTreeNodeDto> queryBranchLeaderNodeListByNode(String eleId, String orgVersionId) {
        List<OrgTreeNodeDto> list = new ArrayList<OrgTreeNodeDto>();
        List<MultiOrgElementLeader> leaders = this.multiOrgElementLeaderService.queryMyBranchLeaderListByEleId(eleId);
        if (!CollectionUtils.isEmpty(leaders)) {
            for (MultiOrgElementLeader leader : leaders) {
                String leaderId = leader.getTargetObjId();
                OrgTreeNodeDto dto = this.getNodeOfCurrentVerisonByEleId(leaderId);
                if (dto != null) {
                    list.add(dto);
                }
            }
        }
        return list;
    }

    @Override
    public List<String> queryBranchLeaderUserIdListByNode(String eleId, String orgVersionId) {
        List<String> userIdList = new ArrayList<>();
        List<MultiOrgElementLeader> leaders = this.multiOrgElementLeaderService.queryMyBranchLeaderListByEleId(eleId);
        if (!CollectionUtils.isEmpty(leaders)) {
            for (MultiOrgElementLeader leader : leaders) {
                String leaderId = leader.getTargetObjId();
                if (leaderId.startsWith(IdPrefix.USER.getValue())) {
                    userIdList.add(leaderId);
                }
            }
        }
        return userIdList;
    }

    @Override
    public OrgTreeNodeDto getNodeOfCurrentVerisonByEleId(String eleId) {
        // eleId不能是版本ID,如果是版本ID,说明数据异常了
        if (eleId.startsWith(IdPrefix.ORG_VERSION.getValue())) {
            return null;
        }
        List<MultiOrgTreeNode> objs = this.multiOrgTreeNodeService.queryNodeByEleId(eleId);
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgTreeNode node : objs) {
                MultiOrgVersion ver = this.orgVersionFacade.getOrgVersionById(node.getOrgVersionId());
                if (ver.getStatus() == 1) {
                    return this.getOrgTreeNodeDto(node.getUuid());
                }
            }
        }
        return null;
    }

    /**
     * 组织升级版本
     * 1， 复制整个组织树
     * 2，复制整个组织的领导关系
     */
    @Override
    @Transactional
    public void dealOrgUpgradeEvent(MultiOrgVersion oldVersion, MultiOrgVersion newVersion) {
        // 复制整个树
        List<MultiOrgTreeNode> list = this.multiOrgTreeNodeService.queryNodeByVersionId(oldVersion.getId());
        if (!CollectionUtils.isEmpty(list)) {
            List<MultiOrgTreeNode> newList = new ArrayList<MultiOrgTreeNode>();
            for (MultiOrgTreeNode row : list) {
                MultiOrgTreeNode newRow = new MultiOrgTreeNode();
                BeanUtils.copyProperties(row, newRow, IdEntity.BASE_FIELDS);
                newRow.setOrgVersionId(newVersion.getId());
                if (row.getEleId().equals(oldVersion.getId())) {//
                    // 旧版本节点，则需要替换eleId为新版本ID
                    newRow.setEleId(newVersion.getId());
                }
                // 因为数据库设置了eleIdPath字段唯一性的验证，所以不能通过先插入，后批量修改的方法来处理，
                // 这里需要直接替换eleIdPath
                String newEleIdPath = row.getEleIdPath().replace(oldVersion.getId(), newVersion.getId());
                newRow.setEleIdPath(newEleIdPath);
                newList.add(newRow);
            }
            this.multiOrgTreeNodeService.saveAll(newList);
        }

        // 复制整个组织节点的领导关系,
        List<MultiOrgElementLeader> leaderList = this.multiOrgElementLeaderService
                .queryLeaderByEleOrgVersionId(oldVersion.getId());
        if (!CollectionUtils.isEmpty(leaderList)) {
            List<MultiOrgElementLeader> newLeaderList = new ArrayList<MultiOrgElementLeader>();
            for (MultiOrgElementLeader leader : leaderList) {
                MultiOrgElementLeader newLeader = new MultiOrgElementLeader();
                BeanUtils.copyProperties(leader, newLeader, IdEntity.BASE_FIELDS);
                newLeader.setEleOrgVersionId(newVersion.getId());
                newLeaderList.add(newLeader);
            }
            this.multiOrgElementLeaderService.saveAll(newLeaderList);
        }

    }

    @Override
    public MultiOrgDuty addDuty(MultiOrgDuty vo) {
        checkDutyVo(vo, false);
        MultiOrgDuty d = this.multiOrgDutyService.getByName(vo.getName(), vo.getSystemUnitId());
        Assert.isTrue(d == null, "该职务名称已存在，请换一个");
        String newId = IdPrefix.DUTY.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId();
        vo.setId(newId);
        //获取职等
        vo.setJobGrade(getJobGradeByJobRank(vo.getJobRank()));
        this.multiOrgDutyService.save(vo);
        return vo;
    }

    private void checkDutyVo(MultiOrgDuty vo, boolean isModify) {
        Assert.isTrue(vo != null, "参数为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getName()), "名称不能为空");
        if (checkEnableDutyHierarchy() && StringUtils.isNotBlank(vo.getDutySeqUuid())) {
            //Assert.isTrue(StringUtils.isNotBlank(vo.getDutySeqUuid()), "职务序列不能为空");
            Assert.isTrue(StringUtils.isNotBlank(vo.getJobRank()), "职级不能为空");
        }

        if (isModify) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getUuid()), "uuid不能为空");
        }
    }

    /**
     * 根据职级获取职等
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/10/27 9:02
     */
    private String getJobGradeByJobRank(String jobRank) {
        //开启职务体系才需要修改
        if (checkEnableDutyHierarchy() && StringUtils.isNotBlank(jobRank)) {
            String order = unitParamService.getValue(UnitParamConstant.JOB_GRADE_ORDER);
            if (order == null) {
                order = "ASC";
            }
            List<Integer> list = multiOrgJobRankService.getJobGradeByJobRankId(order, jobRank.split(","));
            if (list != null && !list.isEmpty()) {
                return StringUtils.join(list, ",");
            }
        }
        return "";
    }

    @Override
    public MultiOrgDuty modifyDuty(MultiOrgDuty vo) {
        checkDutyVo(vo, true);
        MultiOrgDuty duty = this.multiOrgDutyService.getOne(vo.getUuid());
        Assert.isTrue(duty != null, "对应的职务不存在");
        // 职务名称发生变化，需要检查是否冲突
        if (!vo.getName().equals(duty.getName())) {
            MultiOrgDuty d = this.multiOrgDutyService.getByName(vo.getName(), vo.getSystemUnitId());
            Assert.isTrue(d == null, "该职务名称已存在，请换一个");
        }
        if (StringUtils.isNotBlank(vo.getDutySeqUuid())) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getJobRank()), "请选择职级");
        }
        // ID,归属单位不能变
        String[] exclude = new String[]{"id", "systemUnitId"};
        BeanUtils.copyPropertiesExcludeBaseField(vo, duty, exclude);
        duty.setJobGrade(getJobGradeByJobRank(vo.getJobRank()));
        this.multiOrgDutyService.save(duty);
        return duty;
    }

    @Override
    public MultiOrgDuty getDuty(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgDuty duty = this.multiOrgDutyService.getOne(uuid);
        return duty;
    }

    @Transactional
    @Override
    public MultiOrgJobRank addJobRank(MultiOrgJobRankVo vo) {
        checkJobRankVo(vo, false);
        String newId = createNewOrgElementId(idGeneratorService, IdPrefix.RANK.getValue());
        MultiOrgJobRank entity = new MultiOrgJobRank();
        BeanUtils.copyProperties(vo, entity);
        entity.setId(newId);
        multiOrgJobRankService.save(entity);
        saveJobLevel(vo.getJobLevel(), entity.getUuid());

        return entity;
    }

    private void checkJobRankVo(MultiOrgJobRankVo vo, boolean isModify) {
        Assert.isTrue(vo != null, "参数为空");
        //Assert.isTrue(StringUtils.isNotBlank(vo.getSystemUnitId()), "归属单位不能为空");
        //Assert.isTrue(StringUtils.isNotBlank(vo.getName()), "名称不能为空");
        if (checkEnableDutyHierarchy()) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getDutySeqUuid()), "职务序列不能为空");
            Assert.isTrue(vo.getJobGrade() != null, "职等不能为空");
        }
        if (isModify) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getUuid()), "uuid不能为空");
        }
    }

    /**
     * 开启职务体系才需要保存职等
     *
     * @param jobLevel, jobRankUuid
     * @return
     * @author baozh
     * @date 2021/10/27 9:54
     */
    private void saveJobLevel(List<String> jobLevel, String jobRankUuid) {
        if (checkEnableDutyHierarchy() && jobLevel != null) {
            multiOrgJobLevelService.saveJobLevel(jobLevel, jobRankUuid);
        }
    }

    /**
     * 判断是否启用职务体系
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/10/27 8:59
     */
    private boolean checkEnableDutyHierarchy() {
        //获取单位参数
        String isEnable = unitParamService.getValue(UnitParamConstant.DUTY_HIERARCHY_SWITCH);
        return isEnable == null || UnitParamConstant.INTEGER_TRUE.equals(isEnable);
    }

    @Transactional
    @Override
    public MultiOrgJobRank modifyJobRank(MultiOrgJobRankVo vo) {
        checkJobRankVo(vo, true);
        MultiOrgJobRank rank = this.multiOrgJobRankService.getOne(vo.getUuid());
        if (StringUtils.isBlank(vo.getId())) {
            vo.setId(rank.getId());
        }
        Assert.isTrue(rank != null, "对应的职级不存在");
        BeanUtils.copyProperties(vo, rank, new String[]{"id"});
        //TODO
        rank.setModifyTime(new Date());
        rank.setModifier(SpringSecurityUtils.getCurrentUserId());

        multiOrgJobRankService.save(rank);
        saveJobLevel(vo.getJobLevel(), rank.getUuid());
        return rank;
    }


    @Override
    public MultiOrgJobRankDto getJobRank(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgJobRank rank = multiOrgJobRankService.getOne(uuid);
        List<String> jobLevel = multiOrgJobLevelService.listByJobRankUuid(uuid);
        MultiOrgJobRankDto dto = new MultiOrgJobRankDto();
        BeanUtils.copyProperties(rank, dto);
        dto.setJobLevel(jobLevel);

        OrgDutySeqEntity seqEntity = orgDutySeqService.getOne(rank.getDutySeqUuid());
        if (seqEntity != null) {
            dto.setDutySeqName(seqEntity.getDutySeqName());
        }
        //获取职等
        OrgJobGradeEntity jobGradeEntity = orgJobGradeService.getByJobGrade(rank.getJobGrade());
        if (jobGradeEntity != null) {
            dto.setJobGradeName(jobGradeEntity.getJobGradeName());
        }
        return dto;
    }

    @Override
    public List<OrgJobDutyDto> queryJobListWithDutyByVersionId(String versionId) {
        return this.multiOrgTreeNodeService.queryJobListWithDutyByVersionId(versionId);
    }

    @Override
    public List<OrgJobDutyDto> queryJobListWithDutyByVersionIdList(List<String> versionIdList, String keyword) {
        if (CollectionUtils.isEmpty(versionIdList)) {
            return null;
        }
        return this.multiOrgTreeNodeService.queryJobListWithDutyByVersionIdList(versionIdList, keyword);
    }

    @Override
    public MultiOrgDuty getDutyById(String dutyId) {
        return this.multiOrgDutyService.getById(dutyId);
    }

    @Override
    public MultiOrgOption addOrgOption(MultiOrgOption vo) {
        checkOrgOptionVo(vo, false);
        this.multiOrgOptionService.save(vo);
        return vo;
    }

    private void checkOrgOptionVo(MultiOrgOption vo, boolean isModify) {
        Assert.isTrue(vo != null, "参数为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getSystemUnitId()), "归属单位不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getName()), "名称不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getId()), "id不能为空");
        if (isModify) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getUuid()), "uuid不能为空");
        }
    }

    @Override
    public MultiOrgOption modifyOrgOption(MultiOrgOption vo) {
        checkOrgOptionVo(vo, true);
        MultiOrgOption opt = this.multiOrgOptionService.getOne(vo.getUuid());
        Assert.isTrue(opt != null, "对应的选项不存在");
        String[] exclude = new String[]{};
        BeanUtils.copyPropertiesExcludeBaseField(vo, opt, exclude);
        this.multiOrgOptionService.save(opt);
        return opt;
    }

    @Override
    public MultiOrgOption getOrgOption(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgOption opt = this.multiOrgOptionService.getOne(uuid);
        return opt;
    }

    @Override
    public List<MultiOrgOption> getOrgOptionListByUnitId(String systemUnitId) {
        return getOrgOptionListByUnitId(systemUnitId, false);
    }

    @Override
    public List<MultiOrgOption> getOrgOptionListByUnitId(String systemUnitId, boolean onlyShow) {
        if (StringUtils.isBlank(systemUnitId) || "null".equalsIgnoreCase(systemUnitId)
                || "undefined".equalsIgnoreCase(systemUnitId)) {
            systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        }
        return queryOrgOptionListBySystemUnitIdAndOptionOfPT(systemUnitId, onlyShow);
    }

    @Override
    public List<MultiOrgOption> queryOrgOptionListBySystemUnitIdAndOptionOfPT(String systemUnitId) {
        return queryOrgOptionListBySystemUnitIdAndOptionOfPT(systemUnitId, true);
    }

    public List<MultiOrgOption> queryOrgOptionListBySystemUnitIdAndOptionOfPT(String systemUnitId, boolean onlyShow) {
        List<MultiOrgOption> list = new ArrayList<MultiOrgOption>();
        List<MultiOrgOption> objs = this.multiOrgOptionService.queryOrgOptionListBySystemUnitId(systemUnitId, onlyShow);
        if (!CollectionUtils.isEmpty(objs)) {
            list.addAll(objs);
        }
        // 防止重复数据
        if (!systemUnitId.equals(MultiOrgSystemUnit.PT_ID)) {
            // 带上平台的
            List<MultiOrgOption> ptObjs = this.multiOrgOptionService
                    .queryOrgOptionListBySystemUnitId(MultiOrgSystemUnit.PT_ID, onlyShow);
            if (!CollectionUtils.isEmpty(ptObjs)) {
                list.addAll(ptObjs);
            }
        }
        Collections.sort(list, new Comparator<MultiOrgOption>() {
            @Override
            public int compare(MultiOrgOption o1, MultiOrgOption o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
        return list;
    }

    // 获取所有的系统单位列表
    @Override
    public List<MultiOrgSystemUnit> queryAllSystemUnitList() {
        return this.multiOrgSystemUnitService.listAll();
    }

    @Override
    public MultiOrgType addOrgType(MultiOrgType vo) {
        checkOrgTypeVo(vo, false);
        MultiOrgType obj = this.multiOrgTypeService.getByIdAndSystemUnitId(vo.getId(), vo.getSystemUnitId());
        Assert.isNull(obj, "该类型ID已存在，请更换一个");
        obj = this.multiOrgTypeService.getByNameAndSystemUnitId(vo.getName(), vo.getSystemUnitId());
        Assert.isNull(obj, "该类型名称已存在，请更换一个");
        this.multiOrgTypeService.save(vo);
        return vo;
    }

    private void checkOrgTypeVo(MultiOrgType vo, boolean isModify) {
        Assert.isTrue(vo != null, "参数为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getSystemUnitId()), "归属单位不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getName()), "名称不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getId()), "id不能为空");
        if (isModify) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getUuid()), "uuid不能为空");
        }
    }

    @Override
    @Transactional
    public MultiOrgType modifyOrgType(MultiOrgType vo) {
        checkOrgTypeVo(vo, true);
        MultiOrgType obj = this.multiOrgTypeService.getOne(vo.getUuid());
        Assert.isTrue(obj != null, "对应的选项不存在");
        // ID发送变化，需要检查是否重名
        if (!vo.getId().equals(obj.getId())) {
            Assert.isTrue(false, "类型ID，不能变化");
        }
        // 名字发生变化，需要检查是否重名
        if (!vo.getName().equals(obj.getName())) {
            MultiOrgType type = this.multiOrgTypeService.getByNameAndSystemUnitId(vo.getName(), obj.getSystemUnitId());
            Assert.isNull(type, "该类型名称已存在，请更换一个");
        }
        String oldName = obj.getName();
        String[] exclude = new String[]{};
        BeanUtils.copyPropertiesExcludeBaseField(vo, obj, exclude);
        this.multiOrgTypeService.save(obj);

        // 名字发生变化，需要同步变化组织版本里的数据
        if (!oldName.equals(vo.getName())) {
            this.orgVersionFacade.updateOrgFunctionTypeName(obj.getSystemUnitId(), obj.getId(), vo.getName());
        }

        return obj;
    }

    @Override
    public MultiOrgType getOrgType(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgType obj = this.multiOrgTypeService.getOne(uuid);
        return obj;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgService#queryDutyListForSelect2(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData queryDutyListForSelect2(Select2QueryInfo select2QueryInfo) {
        return multiOrgDutyService.queryDutyListForSelect2(select2QueryInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgService#addUnitAdmin(com.wellsoft.pt.multi.org.bean.OrgUserVo)
     */
    @Override
    public OrgUserVo addUnitAdmin(OrgUserVo vo) throws UnsupportedEncodingException {
        return multiOrgUserService.addUnitAdmin(vo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgService#modifyUnitAdmin(com.wellsoft.pt.multi.org.bean.OrgUserVo)
     */
    @Override
    public OrgUserVo modifyUnitAdmin(OrgUserVo vo) {
        return multiOrgUserService.modifyUnitAdmin(vo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgService#getUser(java.lang.String)
     */
    @Override
    public OrgUserVo getUser(String uuid) {
        return multiOrgUserService.getUser(uuid);
    }

    @Override
    public TreeNode getUserPrivilegeResultTree(String uuid) {
        return multiOrgUserService.getUserPrivilegeResultTree(uuid);
    }

    @Override
    public TreeNode getUserAllPrivilegeResultTree(String uuid) {
        return multiOrgUserService.getUserAllPrivilegeResultTree(uuid);
    }

    @Override
    public OrgUserVo addUser(OrgUserVo vo) throws UnsupportedEncodingException {
        return multiOrgUserService.addUser(vo);
    }

    @Override
    public OrgUserVo modifyUser(OrgUserVo vo) {
        return multiOrgUserService.modifyUser(vo);
    }

    @Override
    public Select2QueryData queryOrgTypeListForSelect2(Select2QueryInfo select2QueryInfo) {
        return multiOrgTypeService.queryOrgTypeListForSelect2(select2QueryInfo);
    }

    @Override
    public String getDepartmentNamePathByJobIdPath(String mainJobIdPath, boolean isNeedUnit) {
        if (StringUtils.isNotBlank(mainJobIdPath)) {
            ArrayList<String> names = new ArrayList<String>();
            String[] ids = mainJobIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            // 第一个是版本号，所以i=1开始
            for (int i = 1; i < ids.length - 1; i++) {
                String id = ids[i];
                // 判断第一个节点是不是单位，如果是单位,则判断isNeedUnit，需不需要，需要则加，不需要则不加
                if (i == 1) {
                    if (id.startsWith(IdPrefix.BUSINESS_UNIT.getValue())) {
                        if (isNeedUnit) {
                            MultiOrgElement ele = this.getOrgElementById(id);
                            names.add(ele.getName());
                        }
                    } else {
                        MultiOrgElement ele = this.getOrgElementById(id);
                        names.add(ele.getName());
                    }
                } else {
                    MultiOrgElement ele = this.getOrgElementById(id);
                    names.add(ele.getName());
                }
            }
            return StringUtils.join(names, "/");
        }
        return "";
    }

    @Override
    public List<OrgJobDutyDto> queryJobListByDutyAndVersionId(String orgVersionId, String dutyId) {
        List<OrgJobDutyDto> list = this.queryJobListWithDutyByVersionId(orgVersionId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<OrgJobDutyDto> jobs = Lists.newArrayList();
        for (OrgJobDutyDto dto : list) {
            if (dto.getDutyId().equals(dutyId)) {
                jobs.add(dto);
            }
        }
        return jobs;
    }

    @Override
    public List<OrgTreeNodeDto> queryJobNodeListOfCurrentVerisonByDutyId(String dutyId) {
        return this.multiOrgTreeNodeService.queryJobNodeListOfCurrentVerisonByDutyId(dutyId);
    }

    @Override
    public List<OrgTreeNodeDto> queryJobListByEleIdAndVersionId(String orgVersionId, String eleId) {
        return this.multiOrgTreeNodeService.queryJobListByEleIdAndVersionId(orgVersionId, eleId);
    }

    @Override
    public MultiOrgDuty getDutyByName(String dutyName) {
        return this.multiOrgDutyService.getByName(dutyName, SpringSecurityUtils.getCurrentUserUnitId());
    }

    @Override
    public void clearOrgTree(OrgTreeNodeDto selfUnit) {
        this.multiOrgTreeNodeService.clearOrgTree(selfUnit);
    }

    /**
     * 清空一个单位的所有用户，不包括单位管理员,
     * 因为是低频的操作，所有就直接用for循环依次删除了
     * 等后面如果性能压力了，再改造成批量删除
     */
    @Override
    public void clearAllUserOfUnit() {
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<MultiOrgUserAccount> allUser = this.multiOrgUserService.queryAllAccountOfUnit(systemUnitId);
        if (CollectionUtils.isNotEmpty(allUser)) {
            for (MultiOrgUserAccount a : allUser) {
                if (a.getType().equals(MultiOrgUserAccount.TYPE_UNIT_ADMIN)) {
                    // 单位管理员不能删
                    continue;
                } else {
                    this.multiOrgUserService.deleteUser(a);
                }
            }
        }
    }

    @Override
    public void deleteUsers(String[] userIds) {
        if (userIds != null && userIds.length > 0) {
            for (String id : userIds) {
                MultiOrgUserAccount a = this.multiOrgUserService.getAccountByUserId(id);
                // 单位管理员不能删除
                if (a != null && !a.getType().equals(MultiOrgUserAccount.TYPE_UNIT_ADMIN)) {
                    this.multiOrgUserService.deleteUser(a);
                }
            }
        }
    }

    @Override
    public void addRoleListOfElement(String id, String roleUuid) {
        this.multiOrgElementRoleService.addRoleListOfElement(id, roleUuid);
    }

    @Override
    public Map<String, MultiOrgElement> queryElementMapByEleIdPath(String idPath) {
        Map<String, MultiOrgElement> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(idPath)) {
            String[] ids = idPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            for (String id : ids) {
                MultiOrgElement e = this.getOrgElementById(id);
                if (e != null) {
                    map.put(e.getId(), e);
                }
            }
        }
        return map;
    }

    @Override
    public List<MultiOrgSystemUnit> findMultiOrgSystemUnitByName(Map<String, Object> map) {
        return multiOrgSystemUnitService.listByHQL("from MultiOrgSystemUnit where name like :name", map);
    }

    @Override
    public MultiOrgSystemUnit getMultiOrgSystemUnit(String unit) {
        return multiOrgSystemUnitService.getById(unit);
    }

    @Override
    public List<MultiOrgSystemUnit> findMultiOrgSystemUnitForAll() {
        return multiOrgSystemUnitService.listAll();
    }

    @Override
    public List<OrgUserVo> queryDirectLeaderListByUserId(String userId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByUserId(userId);
        OrgUserJobDto mainJob = null;
        if (CollectionUtils.isEmpty(jobs)) {
            return null;
        }
        for (OrgUserJobDto jobDto : jobs) {
            if (jobDto.getIsMain() == 1) {
                mainJob = jobDto;
                break;
            }
        }
        if (mainJob == null) {
            return null;
        }
        // 根据主职归属的部门获取直接领导人
        OrgTreeNodeDto nodeDto = mainJob.getOrgTreeNodeDto();
        if (nodeDto == null) {
            return null;
        }
        List<MultiOrgElementLeader> leaders = multiOrgElementLeaderService
                .queryMyBossLeaderListByEleId(nodeDto.getDeptId(), nodeDto.getOrgVersionId());
        if (CollectionUtils.isNotEmpty(leaders)) {
            List<OrgUserVo> orgUserVos = Lists.newArrayList();
            for (MultiOrgElementLeader l : leaders) {
                if (l.getTargetObjId().startsWith(IdPrefix.JOB.getValue())) { // 负责人是职位
                    List<MultiOrgUserWorkInfo> workInfos = multiOrgUserWorkInfoService
                            .getUserWorkInfosByJobId(l.getTargetObjId());
                    for (MultiOrgUserWorkInfo wk : workInfos) {
                        orgUserVos.add(multiOrgUserService.getUserById(wk.getUserId()));
                    }
                }
            }

            return orgUserVos;
        }
        return null;
    }

    @Override
    public Integer countUnderling(String userId) {
        List<OrgUserJobDto> jobs = this.multiOrgUserService.queryUserJobByUserId(userId);
        Set<String> jobIds = Sets.newHashSet();
        List<String> jobSql = Lists.newArrayList();
        Map<String, OrgTreeNodeDto> jobNodes = Maps.newHashMap();
        if (CollectionUtils.isEmpty(jobs)) {
            return 0;
        }
        for (OrgUserJobDto jobDto : jobs) {
            jobNodes.put(jobDto.getOrgTreeNodeDto().getEleId(), jobDto.getOrgTreeNodeDto());

        }

        for (String jid : jobNodes.keySet()) {
            String vid = jobNodes.get(jid).getOrgVersionId();

            // 获取用户职位的分管部门，并且查询对应分管部门下的所有职位（不包括子部门）
            List<MultiOrgElementLeader> leaders = multiOrgElementLeaderService.queryMyBranchUnderlingListByEleId(jid,
                    vid);
            for (MultiOrgElementLeader l : leaders) {
                if (l.getEleId().startsWith(IdPrefix.DEPARTMENT.getValue())) {
                    // 获取分管部门下的所有职位
                    List<OrgTreeNodeDto> allJobs = this.multiOrgTreeNodeService.queryJobListByEleIdAndVersionId(vid,
                            l.getEleId());
                    for (OrgTreeNodeDto dto : allJobs) {
                        if (dto.getEleIdPath().endsWith(l.getTargetObjId() + "/" + dto.getEleId())) {// 只获取分管部门下的职位(不包括子部门)
                            jobIds.add(dto.getEleId());
                        }
                    }
                }
            }
            // 获取用户职位是哪些部门的“负责人”
            leaders = multiOrgElementLeaderService.queryMyBossUnderlingListByEleId(jid, vid);
            for (MultiOrgElementLeader l : leaders) {
                if (l.getEleId().startsWith(IdPrefix.DEPARTMENT.getValue())) {
                    List<OrgTreeNodeDto> allJobs = this.multiOrgTreeNodeService.queryJobListByEleIdAndVersionId(vid,
                            l.getEleId());
                    for (OrgTreeNodeDto dto : allJobs) {
                        jobIds.add(dto.getEleId());
                    }
                }
            }
        }

        for (String jid : jobIds) {
            jobSql.add(" ( i.job_ids like '%" + jid + "%' ) ");
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("excludeUsers", new String[]{userId});
        params.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        params.put("jobSql", StringUtils.join(jobSql, "or"));
        return this.multiOrgUserService.countUserByJob(params);
    }

    @Override
    public OrgNode queryOrgNode(String eleId) {
        if (StringUtils.isEmpty(eleId)) {
            return null;
        }
        MultiOrgElement multiOrgElement = this.multiOrgElementService.getById(eleId);
        if (multiOrgElement == null) {
            return null;
        }
        OrgNode orgNode = ConvertOrgNode.convert(multiOrgElement, 0);
        return orgNode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgService#queryOrgTreeNodeByEleIdPaths(java.util.List)
     */
    @Override
    public List<OrgTreeNodeDto> queryOrgTreeNodeByEleIdPaths(List<String> eleIdPaths) {
        return multiOrgTreeNodeService.queryOrgTreeNodeByEleIdPaths(eleIdPaths);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgService#getOrgEleOrderByEleIdPaths(java.util.List)
     */
    @Override
    public Map<String, Integer> getOrgEleOrderByEleIdPaths(List<String> eleIdPaths) {
        Map<String, Integer> map = Maps.newHashMap();
        List<String> paths = Lists.newArrayList(eleIdPaths);
        if (CollectionUtils.isEmpty(paths)) {
            paths.add(TreeNode.ROOT_ID);
        }
        for (String eleIdPath : eleIdPaths) {
            map.put(eleIdPath, 0);
        }
        List<OrgTreeNodeDto> orgTreeNodeDtos = this.queryOrgTreeNodeByEleIdPaths(eleIdPaths);
        int order = 0;
        for (OrgTreeNodeDto orgTreeNodeDto : orgTreeNodeDtos) {
            map.put(orgTreeNodeDto.getEleIdPath(), order++);
        }
        return map;
    }

    @Override
    public List<MultiOrgDuty> getDutysByIds(List<String> ids) {
        return multiOrgDutyService.getDutysByIds(ids);
    }

    /**
     * 1 校验选中的职务是否被职位引用
     * 有引用，提示：职务被职位关联使用，无法删除
     *
     * @param uuids
     * @return
     * @author baozh
     * @date 2021/11/5 14:51
     */
    @Transactional
    @Override
    public String deleteDuty(String... uuids) {
        for (String uuid : uuids) {
            MultiOrgDuty dutyEntity = multiOrgDutyService.getOne(uuid);
            if (dutyEntity == null) {
                throw new WellException("职务不存在");
            }
            //判断是否有职位关联
            MultiOrgJobDuty jobDuty = new MultiOrgJobDuty();
            jobDuty.setDutyId(dutyEntity.getId());
            List<MultiOrgJobDuty> jobDuties = multiOrgJobDutyService.listByEntity(jobDuty);
            if (jobDuties != null && !jobDuties.isEmpty()) {
                return dutyEntity.getName() + "职务被职位关联使用，无法删除";
            }
        }
        multiOrgDutyService.deleteByUuids(Arrays.asList(uuids));
        return null;
    }


    /**
     * 删除的职级如有被职务引用，则不可删除，提示：职级被职务关联使用，无法删除。
     *
     * @param uuid
     * @return
     * @author baozh
     * @date 2021/11/5 14:50
     */
    @Transactional
    @Override
    public String deleteJobRank(String uuid) {
        MultiOrgJobRank jobRankEntity = multiOrgJobRankService.getOne(uuid);
        String rank = jobRankEntity.getId();
        MultiOrgDuty dutyEntity = new MultiOrgDuty();
        dutyEntity.setDutySeqUuid(jobRankEntity.getDutySeqUuid());
        List<MultiOrgDuty> multiOrgDuties = multiOrgDutyService.listByEntity(dutyEntity);
        for (MultiOrgDuty multiOrgDuty : multiOrgDuties) {
            if (multiOrgDuty.getJobRank().indexOf(rank) == -1) {
                continue;
            }
            for (String jobRank : multiOrgDuty.getJobRank().split(",")) {
                if (rank.equals(jobRank)) {
                    return "职级被职务关联使用，无法删除";
                }
            }

        }
        multiOrgJobRankService.delete(uuid);
        return null;
    }

    @Override
    public List<MultiOrgJobRankDto> listJobRankByDutySeqUuid(String uuid) {
        List<MultiOrgJobRank> jobRanks = multiOrgJobRankService.getDao().listByFieldEqValue("dutySeqUuid", uuid);
        List<OrgJobGradeEntity> gradeEntities = orgJobGradeService.jobGradeList();
        Map<Integer, String> gradeMap = new HashMap<>();
        for (OrgJobGradeEntity gradeEntity : gradeEntities) {
            gradeMap.put(gradeEntity.getJobGrade(), gradeEntity.getJobGradeName());
        }
        List<MultiOrgJobRankDto> collect = jobRanks.stream().map(rank -> {
            MultiOrgJobRankDto dto = new MultiOrgJobRankDto();
            BeanUtils.copyProperties(rank, dto);
            dto.setJobGradeName(gradeMap.get(rank.getJobGrade()));
            return dto;
        }).sorted(Comparator.comparing(MultiOrgJobRankDto::getJobGrade, Comparator.nullsLast(Integer::compareTo)).reversed()).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<String> checkOnly(OrgUserVo vo) {
        return multiOrgUserService.checkOnly(vo);
    }

    @Override
    public List<MultiOrgJobRankDto> getJobRankByJobId(String orgVersion, String jobId) {
        if (StringUtils.isBlank(jobId)) {
            return new ArrayList<>();
        }
        MultiOrgJobDuty orgJobDuty = multiOrgJobDutyService.getJobDutyByJobId(jobId);
        List<MultiOrgJobRankDto> jobRankDtos = new ArrayList<>();
        if (orgJobDuty != null) {
            MultiOrgDuty orgDuty = multiOrgDutyService.getById(orgJobDuty.getDutyId());
            if (orgDuty != null && orgDuty.getJobRank() != null) {
                String order = unitParamService.getValue(UnitParamConstant.JOB_GRADE_ORDER);
                if (order == null) {
                    order = "ASC";
                }
                String[] rankIds = orgDuty.getJobRank().split(",");
                List<MultiOrgJobRank> jobRanks = multiOrgJobRankService.getMultiOrgJobRankByJobRankId(order, rankIds);
                jobRankDtos = jobRanks.stream().map(rank -> {
                    MultiOrgJobRankDto dto = new MultiOrgJobRankDto();
                    BeanUtils.copyProperties(rank, dto);
                    List<String> jobLevel = multiOrgJobLevelService.listByJobRankUuid(rank.getUuid());
                    dto.setJobLevel(jobLevel);
                    return dto;
                }).collect(Collectors.toList());
            }
        }
        return jobRankDtos;
    }

    @Override
    public List<QueryItem> queryRoleListOfElementIds(Set<String> userOrgIds) {
        return multiOrgElementRoleService.queryRoleListOfElementIds(userOrgIds);
    }
}
