/*
 * @(#)2017年11月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.jqgrid.JqTreeGridNode;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.util.collection.List2GroupMap;
import com.wellsoft.pt.common.bean.LabelValueBean;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
public class MultiOrgVersionFacadeImpl implements MultiOrgVersionFacade {
    static Logger logger = LoggerFactory.getLogger(MultiOrgVersionFacadeImpl.class);
    @Autowired
    private IdGeneratorService idGeneratorService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgVersionService multiOrgVersionService;
    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private MultiOrgService multiOrgService;
    @Autowired
    private MultiOrgElementLeaderService multiOrgElementLeaderService;
    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;

    /**
     * 1，添加新版本组织
     * 2，初始版本从1.0开始， 组织版本ID,每次自增+1
     * 3，
     * 4，同步数据到组织树表中
     *
     * @param vo
     * @return
     */
    @Transactional
    public OrgVersionVo addMultiOrgVersion(OrgVersionVo vo) {
        // 检查参数
        checkOrgVersionVo(vo, false);
        MultiOrgSystemUnit systemUnit = this.multiOrgService.getSystemUnitById(vo.getSystemUnitId());
        Assert.notNull(systemUnit != null, "对应的系统单位不存在");
        // 参数没有问题，处理业务逻辑
        // 创建该组织的版本信息
        MultiOrgVersion version = new MultiOrgVersion();
        BeanUtils.copyProperties(vo, version);
        version.setVersion("1.0");
        version.setInitSystemUnitName(systemUnit.getName());
        String versionId = MultiOrgServiceImpl.createNewOrgElementId(idGeneratorService,
                IdPrefix.ORG_VERSION.getValue());
        version.setId(versionId);
        // 设置根版本是自己
        version.setRootVersionId(versionId);

        if (vo.getIsDefault()) {
            // 更新其他未非默认
            this.multiOrgVersionService.updateUnDefault(vo.getSystemUnitId());
            version.setIsDefault(true);
        }
        if (!vo.getIsDefault()) {
            long count = this.multiOrgVersionService.countBySystemUnitId(vo.getSystemUnitId());
            if (count == 0l) {
                version.setIsDefault(true);
            }
        }
        this.multiOrgVersionService.save(version);

        // 冗余一份版本信息作为组织元素，存起来，方便后面以组织树搜索和展示的时候，不用到 multi_org_version 表来关联 使用，
        MultiOrgElement versionEle = new MultiOrgElement();
        versionEle.setName(version.getName()); // 用单位名称替代组织的名称，不可修改
        versionEle.setId(version.getId());
        versionEle.setType(IdPrefix.ORG_VERSION.getValue());
        versionEle.setCode(systemUnit.getCode());
        versionEle.setSystemUnitId(systemUnit.getId());
        this.multiOrgElementService.save(versionEle);

        // 将该版本节点挂到到这个版本的组织树中去，作为根节点
        MultiOrgTreeNode rootNode = new MultiOrgTreeNode();
        rootNode.setEleId(versionId);
        rootNode.setEleIdPath(versionId);
        rootNode.setOrgVersionId(version.getId());
        this.multiOrgTreeNodeService.save(rootNode);

        // 将该系统单位作为业务单位挂到该组织下面
        MultiOrgElement bUnitEle = new MultiOrgElement();
        String bUnitId = MultiOrgServiceImpl.createNewOrgElementId(idGeneratorService,
                IdPrefix.BUSINESS_UNIT.getValue());
        bUnitEle.setName(systemUnit.getName()); // 用单位名称替代组织的名称，不可修改
        bUnitEle.setId(bUnitId);
        bUnitEle.setType(IdPrefix.BUSINESS_UNIT.getValue());
        bUnitEle.setCode(systemUnit.getCode());
        bUnitEle.setRemark(systemUnit.getRemark());
        bUnitEle.setSystemUnitId(systemUnit.getId());
        this.multiOrgElementService.save(bUnitEle);

        // 将该业务单位节点挂到到这个版本的组织树中去
        MultiOrgTreeNode orgNode = new MultiOrgTreeNode();
        orgNode.setEleId(bUnitEle.getId());
        orgNode.setEleIdPath(versionId + MultiOrgService.PATH_SPLIT_SYSMBOL + bUnitId);
        orgNode.setOrgVersionId(version.getId());
        this.multiOrgTreeNodeService.save(orgNode);

        // 将该业务单位的eleId保存到该版本中记录起来，供给升级版本使用
        version.setSelfBusinessUnitId(bUnitEle.getId());
        this.multiOrgVersionService.save(version);

        vo.setUuid(version.getUuid());
        return vo;

    }

    private void checkOrgVersionVo(OrgVersionVo vo, boolean isModify) {
        Assert.notNull(vo, "参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getSystemUnitId()), "单位不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getFunctionType()), "类型不能为空");
        if (vo.getIsDefault() != null && vo.getIsDefault()) {
            Assert.isTrue(vo.getStatus() != null && vo.getStatus() == 1, "只能设置启用状态的组织版本为默认！");
        }
        if (isModify) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getUuid()), "uuid不能为空");
        }
    }

    @Override
    @Transactional
    public OrgVersionVo modifyOrgVersionBaseInfo(OrgVersionVo vo) {
        // 检查参数
        checkOrgVersionVo(vo, true);
        MultiOrgVersion obj = this.multiOrgVersionService.getOne(vo.getUuid());
        if (vo.getIsDefault() && !obj.getIsDefault()) {
            // 更新其他未非默认
            this.multiOrgVersionService.updateUnDefault(vo.getSystemUnitId());
        }
        Assert.notNull(obj, "对应的组织版本不存在");
        MultiOrgSystemUnit systemUnit = this.multiOrgService.getSystemUnitById(vo.getSystemUnitId());
        Assert.notNull(systemUnit != null, "对应的系统单位不存在");

        int oldStatus = obj.getStatus().intValue();
        // 组织版本一旦建立，则对应的系统单位，系统单位名称，对应的类型不能修改,以及自动生成的版本ID不能修改
        // 只能修改对应的备注，状态，版本名称
        obj.setName(vo.getName());
        obj.setStatus(vo.getStatus());
        obj.setRemark(vo.getRemark());
        obj.setIsDefault(vo.getIsDefault());
        this.multiOrgVersionService.save(obj);

        // 因为multi_org_element表中冗余了一份数据，所以需要同步修改对应的名称信息
        MultiOrgElement verEle = this.multiOrgElementService.getById(obj.getId());
        verEle.setName(obj.getName());
        this.multiOrgElementService.save(verEle);

        // 激活版本，则需要禁用其他的版本
        if (oldStatus == 0 && vo.getStatus().intValue() == 1) {
            this.activeOrgVersion(obj);
        }
        return vo;
    }

    /**
     * 获取一个组织版本信息
     */
    @Override
    public OrgVersionVo getOrgVersionVo(String orgVersionUuid) {
        Assert.isTrue(StringUtils.isNotBlank(orgVersionUuid), "参数不能为空");
        MultiOrgVersion obj = this.multiOrgVersionService.getOne(orgVersionUuid);
        if (obj != null) {
            OrgVersionVo vo = new OrgVersionVo();
            BeanUtils.copyProperties(obj, vo);
            return vo;
        }
        return null;
    }

    /**
     * 升级新版本
     * 1, 组织版本表里面插入一条新记录，版本号+0.1
     * 2, 复制组织树中的记录，替换掉对应的组织版本ID，然后批量插入
     */
    @Override
    @Transactional
    public MultiOrgVersion addNewOrgVersionForUpgrade(String sourceVersionUuid, boolean isSyncName) {
        Assert.isTrue(StringUtils.isNotBlank(sourceVersionUuid), "参数不能为空");
        MultiOrgVersion oldVersion = this.multiOrgVersionService.getOne(sourceVersionUuid);
        Assert.notNull(oldVersion, "对应的组织版本不存在");
        // 获取对应的单位信息
        MultiOrgSystemUnit systemUnit = this.multiOrgService.getSystemUnitById(oldVersion.getSystemUnitId());
        Assert.notNull(systemUnit, "对应的系统单位不存在");

        // 插入新组织版本
        MultiOrgVersion newVersion = new MultiOrgVersion();
        BeanUtils.copyProperties(oldVersion, newVersion, IdEntity.BASE_FIELDS);
        // 如果需要同步名字，则该版本的初始化系统单位名称要用最新的单位名称
        if (isSyncName) {
            newVersion.setInitSystemUnitName(systemUnit.getName());
        }
        // 版本号+0.1
        String newVersionNum = this.multiOrgVersionService.createNewVersionNum(oldVersion.getSystemUnitId(),
                oldVersion.getRootVersionId());
        newVersion.setVersion(newVersionNum);
        // 产生新版本ID
        String newVersionId = MultiOrgServiceImpl.createNewOrgElementId(idGeneratorService,
                IdPrefix.ORG_VERSION.getValue());
        newVersion.setId(newVersionId);
        // 设置来源版本
        newVersion.setSourceVersionUuid(sourceVersionUuid);
        // 升级的版本，默认不启用
        newVersion.setStatus(0);
        if (newVersion.getIsDefault()) {
            // 更新其他未非默认
            this.multiOrgVersionService.updateUnDefault(oldVersion.getSystemUnitId());
            newVersion.setIsDefault(true);
        }
        this.multiOrgVersionService.save(newVersion);

        // 冗余一份版本信息作为组织元素，存起来，方便后面以组织树搜索和展示的时候，不用到 multi_org_version 表来关联 使用，
        MultiOrgElement versionEle = new MultiOrgElement();
        versionEle.setName(newVersion.getName()); // 用单位名称替代组织的名称，不可修改
        versionEle.setId(newVersion.getId());
        versionEle.setType(IdPrefix.ORG_VERSION.getValue());
        versionEle.setSystemUnitId(systemUnit.getId());
        this.multiOrgElementService.save(versionEle);

        // 通知组织模块升级
        Stopwatch w1 = Stopwatch.createStarted();
        this.multiOrgService.dealOrgUpgradeEvent(oldVersion, newVersion);
        w1.stop();
        logger.info("升级组织版本, 处理组织模块事宜，耗时=" + w1.toString());
        // 如果需要同步名称,等同于该组织下的对应的该业务单位的变更了名字
        if (isSyncName) {
            OrgTreeNodeDto bUnitNode = this.multiOrgService.getNodeByEleIdAndOrgVersion(
                    oldVersion.getSelfBusinessUnitId(), newVersionId);
            OrgTreeNodeVo nodeVo = this.multiOrgService.getOrgNodeByTreeUuid(bUnitNode.getUuid());
            nodeVo.setName(newVersion.getInitSystemUnitName());
            this.multiOrgService.modifyOrgChildNode(nodeVo, true);
        }
        // 通知其他相关模块，组织版本升级了
        noticeRelateModulesOrgUpgrade(oldVersion, newVersion);
        return newVersion;
    }

    private void noticeRelateModulesOrgUpgrade(MultiOrgVersion oldOrg, MultiOrgVersion newOrg) {
        // 通知用户模块
        Stopwatch w1 = Stopwatch.createStarted();
        this.multiOrgUserService.dealOrgUpgradeEvent(oldOrg, newOrg);
        w1.stop();
        logger.info("升级组织版本, 处理用户模块事宜，耗时=" + w1.toString());
    }

    /**
     * 如何描述该方法
     */
    @Override
    @Transactional
    public boolean activeOrgVersion(String orgVersionUuid) {
        Assert.isTrue(StringUtils.isNotBlank(orgVersionUuid), "参数不能为空");
        MultiOrgVersion orgVer = this.multiOrgVersionService.getOne(orgVersionUuid);
        Assert.notNull(orgVer, "对应的组织版本不存在");
        return this.activeOrgVersion(orgVer);
    }

    private boolean activeOrgVersion(MultiOrgVersion orgVer) {
        if (orgVer != null) {
            orgVer.setStatus(1);
            this.multiOrgVersionService.save(orgVer);
            // 其他的同来源版本需要被禁用掉
            MultiOrgVersion q = new MultiOrgVersion();
            q.setRootVersionId(orgVer.getRootVersionId());
            List<MultiOrgVersion> objs = this.multiOrgVersionService.findByExample(q);
            String fromVersionId = null;
            for (MultiOrgVersion otherVersion : objs) {
                if (!otherVersion.getId().equals(orgVer.getId())) {
                    if (1 == otherVersion.getStatus()) {
                        fromVersionId = otherVersion.getId();
                    }
                    otherVersion.setStatus(0);
                    this.multiOrgVersionService.save(otherVersion);
                }
            }
            // 启用新版本，需要重新计算用户的工作冗余信息
            //this.multiOrgUserService.recomputeUserWorkInfoByUnit(orgVer.getSystemUnitId());
            this.multiOrgUserService.recomputeUserWorkInfoByVersions(fromVersionId, orgVer.getId());
            return true;
        }
        return false;
    }

    /**
     * 如何描述该方法
     */
    @Override
    public boolean unactiveOrgVersion(String orgVersionUuid) {
        Assert.isTrue(StringUtils.isNotBlank(orgVersionUuid), "参数不能为空");
        MultiOrgVersion orgVer = this.multiOrgVersionService.getOne(orgVersionUuid);
        Assert.notNull(orgVer, "对应的组织版本不存在");
        orgVer.setStatus(0);
        orgVer.setIsDefault(false);
        this.multiOrgVersionService.save(orgVer);
        // 禁用版本，需要重新计算用户的工作冗余信息
        this.multiOrgUserService.recomputeUserWorkInfoByVersions(orgVer.getId(), null);
        return true;
    }

    @Override
    public QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // 设置查询字段条件
        String parentId = jqGridQueryInfo.getNodeid();
        String systemUnitId = jqGridQueryInfo.get_search();

        List<MultiOrgVersion> results = null;
        if (StringUtils.isNotBlank(parentId)) {
            // 获取指定组织的其他历史版本，需要扣除自己（最大版本）
            results = this.multiOrgVersionService.queryHistoryVersionOfOrg(parentId);
        } else {
            // 获取每个组织的最大版本
            results = this.multiOrgVersionService.queryMaxVersionOfAllOrg();
        }

        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();

        int level = jqGridQueryInfo.getN_level() == null ? 0 : jqGridQueryInfo.getN_level() + 1;
        for (int index = 0; index < results.size(); index++) {
            MultiOrgVersion version = results.get(index);
            // 设置了搜索条件，需要返回指定的系统单位ID
            if (!StringUtils.isBlank(systemUnitId)) {
                if (!version.getSystemUnitId().equals(systemUnitId)) {
                    continue;
                }
            }
            JqTreeGridNode node = new JqTreeGridNode();
            node.setId(version.getUuid());// ID
            List<Object> cell = node.getCell();
            cell.add(version.getUuid());// UUID
            cell.add(version.getFullName()); // 组织的全称
            cell.add(version.getId());// ID
            MultiOrgSystemUnit systemUnit = this.multiOrgService.getSystemUnitById(version.getSystemUnitId());
            cell.add(version.getSystemUnitId());// 对应的系统单位ID
            cell.add(version.getInitSystemUnitName());// 版本创建时的系统单位名称
            cell.add(systemUnit.getName());// 系统单位的当前名称
            // 获取来源版本名称
            String sourceVersionName = "";
            if (StringUtils.isNotBlank(version.getSourceVersionUuid())) {
                MultiOrgVersion sourceVersion = this.multiOrgVersionService.getOne(version.getSourceVersionUuid());
                sourceVersionName = sourceVersion.getFullName();
            }
            cell.add(sourceVersionName);// sourceVersion
            cell.add(version.getRootVersionId());// sourceVersion
            cell.add(version.getStatus());
            cell.add(version.getCreateTime());
            // level field, 节点的级别，默认最高级为0
            cell.add(level);

            // parent id field 该行数据父节点的id
            cell.add(parentId);

            // leaf field, 是否为叶节点，为true时表示该节点下面没有子节点了
            // 如果是获取历史版本，则不管有没有子版本，都认为是最后一级了，
            // 如果是获取最大版本，则检查是否有子版本
            boolean isLeaf = true;
            if (StringUtils.isBlank(parentId)) {
                List<MultiOrgVersion> historyVersion = this.multiOrgVersionService.queryHistoryVersionOfOrg(version
                        .getUuid());
                isLeaf = CollectionUtils.isEmpty(historyVersion) ? true : false;
            }
            cell.add(isLeaf);

            // expanded field 第一个节点展开，
            cell.add(false);
            retResults.add(node);
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);
        queryInfo.getPagingInfo().setTotalCount(retResults.size());
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    /**
     * select2下拉框使用的数据
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        String unitId = select2QueryInfo.getOtherParams("unitId", "");
        String status = select2QueryInfo.getOtherParams("status", "");// 是否启用的状态
        List<LabelValueBean> list = new ArrayList<LabelValueBean>();
        if (StringUtils.isNotBlank(unitId)) {
            MultiOrgVersion q = new MultiOrgVersion();
            q.setSystemUnitId(unitId);
            if ("1".equals(status)) {
                q.setStatus(1);
            }
            List<MultiOrgVersion> verList = this.multiOrgVersionService.findByExample(q);
            Collections.sort(verList, new Comparator<MultiOrgVersion>() {
                @Override
                public int compare(MultiOrgVersion o1, MultiOrgVersion o2) {
                    String s1 = o1.getRootVersionId() + "_" + o1.getVersion();
                    String s2 = o2.getRootVersionId() + "_" + o2.getVersion();
                    return s1.compareTo(s2);
                }

            });
            if (!CollectionUtils.isEmpty(verList)) {
                for (MultiOrgVersion orgVer : verList) {
                    String fullname = orgVer.getFullName();
                    LabelValueBean b = new LabelValueBean();
                    b.setLabel(fullname);
                    b.setValue(orgVer.getId());
                    list.add(b);
                }
            }
        }
        return new Select2QueryData(list, "value", "label", select2QueryInfo.getPagingInfo());
    }

    @Override
    public MultiOrgVersion getOrgVersionById(String orgVersionId) {
        if (StringUtils.isNotBlank(orgVersionId)) {
            return this.multiOrgVersionService.getById(orgVersionId);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade#getAllActiveOrgVersions()
     */
    @Override
    public List<MultiOrgVersion> getAllActiveOrgVersions() {
        MultiOrgVersion q = new MultiOrgVersion();
        q.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        q.setStatus(1);
        List<MultiOrgVersion> objs = this.multiOrgVersionService.findByExample(q);
        return objs;
    }

    @Override
    public List<MultiOrgVersion> queryCurrentActiveVersionListOfAllUnit() {
        return this.multiOrgVersionService.queryCurrentActiveVersionListOfAllUnit();
    }

    @Override
    public List<MultiOrgVersion> queryCurrentActiveVersionListOfSystemUnit(String unitId) {
        return this.multiOrgVersionService.queryCurrentActiveVersionListOfSystemUnit(unitId);
    }

    // 获取所有单位的版本树
    @Override
    public List<OrgTreeNode> queryVersionTreeOfAllSystemUnit(boolean isAllVersion) {
        List<MultiOrgVersion> allVerList = null;
        if (isAllVersion) {
            allVerList = this.multiOrgVersionService.listAll();
        } else {
            allVerList = this.queryCurrentActiveVersionListOfAllUnit();
        }
        if (CollectionUtils.isEmpty(allVerList)) {
            return null;
        }
        List<OrgTreeNode> nodeList = new ArrayList<OrgTreeNode>();
        Map<String, List<MultiOrgVersion>> unitMap = new List2GroupMap<MultiOrgVersion>() {
            @Override
            protected String getGroupUuid(MultiOrgVersion obj) {
                return obj.getSystemUnitId();
            }
        }.convert(allVerList);
        for (String unitId : unitMap.keySet()) {
            OrgTreeNode unitNode = this.unitVersionList2TreeNode(unitId, unitMap.get(unitId));
            nodeList.add(unitNode);
        }
        return nodeList;
    }

    // 获取指定单位的版本树
    @Override
    public OrgTreeNode queryVersionTreeOfUnit(String unitId, boolean isAllVersion) {
        List<MultiOrgVersion> verList = null;
        if (isAllVersion) {
            verList = this.multiOrgVersionService.queryAllVersionListBySystemUnitId(unitId);
        } else {
            verList = this.queryCurrentActiveVersionListOfSystemUnit(unitId);
        }
        return this.unitVersionList2TreeNode(unitId, verList);
    }

    public OrgTreeNode unitVersionList2TreeNode(String unitId, List<MultiOrgVersion> verList) {
        if (CollectionUtils.isEmpty(verList)) {
            return null;
        } else {
            MultiOrgSystemUnit systemUnit = this.multiOrgService.getSystemUnitById(unitId);
            OrgTreeNode rootNode = new OrgTreeNode();
            rootNode.setName(systemUnit.getName());
            rootNode.setType(IdPrefix.SYSTEM_UNIT.getValue());
            rootNode.setId(systemUnit.getId());
            rootNode.setNocheck(true);
            rootNode.setData(systemUnit);
            for (MultiOrgVersion ver : verList) {
                OrgTreeNode versionNode = ver.convert2TreeNode();
                versionNode.setData(ver);
                rootNode.getChildren().add(versionNode);
            }
            return rootNode;
        }
    }

    @Override
    public List<OrgTreeNode> queryVersionTreeOfOtherSystemUnit(String excludeUnitId, boolean isAllVersion) {
        List<OrgTreeNode> allList = this.queryVersionTreeOfAllSystemUnit(isAllVersion);
        if (CollectionUtils.isEmpty(allList)) {
            return null;
        } else {
            List<OrgTreeNode> list = new ArrayList<OrgTreeNode>();
            String currUnitId = SpringSecurityUtils.getCurrentUserUnitId();
            for (OrgTreeNode treeNode : allList) {
                if (treeNode.getId().equals(excludeUnitId) || treeNode.getId().equals(currUnitId)) {
                    // 扣除掉指定的单位，而且也不包含自己的单位信息
                    continue;
                }
                list.add(treeNode);
            }
            return list;
        }
    }

    @Override
    public List<OrgTreeNode> queryVersionTreeOfMySubUnit(boolean isAllVersion) {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        MultiOrgSystemUnit unit = multiOrgService.getSystemUnitById(unitId);
        List<OrgTreeNode> list = Lists.newArrayList();
        if (unit != null && unit.getIsGroupUnit() != null && unit.getIsGroupUnit() == 1) {
            OrgSystemUnitVo unitVo = multiOrgService.getSystemUnitVo(unit.getUuid());
            if (StringUtils.isNotBlank(unitVo.getMembers())) {
                String[] members = unitVo.getMembers().split(";");
                for (String mId : members) {
                    OrgTreeNode node = this.queryVersionTreeOfUnit(mId, isAllVersion);
                    if (node != null) {
                        list.add(node);
                    }
                }
            }
        }
        return list;

    }

    @Override
    public MultiOrgVersion getCurrentActiveVersionListOfUnitAndRootVersionId(String systemUnitId, String functionType) {
        List<MultiOrgVersion> objs = this.multiOrgVersionService.queryCurrentActiveVersionListOfUnitAndRootVersionId(
                systemUnitId, functionType);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public void updateSelfBussinessUnitId(String orgVerisonId, String selfBussinessUnitId) {
        MultiOrgVersion v = this.getOrgVersionById(orgVerisonId);
        if (v != null) {
            v.setSelfBusinessUnitId(selfBussinessUnitId);
            this.multiOrgVersionService.save(v);
        }
    }

    @Override
    public String getCurrentActiveVersionByFunctionType(String unitId, String functionType) {
        MultiOrgVersion q = new MultiOrgVersion();
        q.setSystemUnitId(unitId);
        q.setFunctionType(functionType);
        q.setStatus(1);
        List<MultiOrgVersion> objs = this.multiOrgVersionService.findByExample(q);
        if (!CollectionUtils.isEmpty(objs)) {
            return objs.get(0).getId();
        }
        return null;
    }

    // 组织类型名称发生变化，同步更新组织版本里的数据
    @Override
    @Transactional
    public void updateOrgFunctionTypeName(String systemUnitId, String id, String name) {
        MultiOrgVersion q = new MultiOrgVersion();
        q.setSystemUnitId(systemUnitId);
        q.setFunctionType(id);
        List<MultiOrgVersion> objs = this.multiOrgVersionService.findByExample(q);
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgVersion ver : objs) {
                ver.setFunctionTypeName(name);
                this.multiOrgVersionService.save(ver);
            }
        }
    }

    @Override
    public MultiOrgVersion getVersionById(String id) {
        return this.multiOrgVersionService.getById(id);
    }

    // 通过停用版本获取当前正在启用的版本
    @Override
    public MultiOrgVersion getCurrentActiveVersionByOrgVersionId(String orgVersionId) {
        MultiOrgVersion ver = this.multiOrgVersionService.getById(orgVersionId);
        if (ver == null) {
            return null;
        } else if (ver.getStatus() == 1) {
            // 传进来的就是正在启用的版本，不需要换
            return ver;
        } else {
            MultiOrgVersion q = new MultiOrgVersion();
            q.setStatus(1);
            q.setSystemUnitId(ver.getSystemUnitId());
            q.setRootVersionId(ver.getRootVersionId());
            List<MultiOrgVersion> objs = this.multiOrgVersionService.findByExample(q);
            if (CollectionUtils.isEmpty(objs)) {
                return null;
            }
            return objs.get(0);
        }
    }

    /**
     * 移动组织元素所属版本
     *
     * @param multiOrgTreeNode
     * @param targetOrgVersion
     * @ MultiOrgVersionFacadeImpl.addNewOrgVersionForUpgrade
     * @ MultiOrgVersionFacadeImpl.activeOrgVersion
     */
    @Transactional
    public void moveOrgTreeNodeToOrgVersion(MultiOrgTreeNode sourceTreeNode, MultiOrgTreeNode targetTreeNode) {
        if (StringUtils.equals(sourceTreeNode.getEleId(), IdPrefix.ORG_VERSION.getValue())) {
            throw new IllegalArgumentException("组织版本不能移动");
        }
        Map<String, MultiOrgTreeNode> treeNodeMaps = Maps.newHashMap();
        String oldOrgVersionId = sourceTreeNode.getOrgVersionId();
        String newOrgVersionId = targetTreeNode.getOrgVersionId();
        List<MultiOrgTreeNode> treeNodes = multiOrgTreeNodeService.queryNodeByVersionId(oldOrgVersionId);
        String oldEleIdPath = sourceTreeNode.getEleIdPath();
        String newEleIdPath = targetTreeNode.getEleIdPath();
        for (MultiOrgTreeNode treeNode : treeNodes) {
            String eleIdPath = treeNode.getEleIdPath();
            if (StringUtils.startsWith(eleIdPath, oldEleIdPath)) {
                treeNode.setEleIdPath(eleIdPath.replace(sourceTreeNode.getParentIdPath(), newEleIdPath));
                treeNode.setOrgVersionId(newOrgVersionId);
                multiOrgTreeNodeService.save(treeNode);
                treeNodeMaps.put(treeNode.getEleId(), treeNode);
            }
        }
        // 复制整个组织节点的领导关系,
        List<MultiOrgElementLeader> leaderList = multiOrgElementLeaderService
                .queryLeaderByEleOrgVersionId(oldOrgVersionId);
        for (MultiOrgElementLeader leader : leaderList) {
            MultiOrgTreeNode treeNode = treeNodeMaps.get(leader.getEleId());
            if (null != treeNode) {
                leader.setEleOrgVersionId(treeNode.getOrgVersionId());
            }
            MultiOrgTreeNode treeNode2 = treeNodeMaps.get(leader.getTargetObjId());
            if (null != treeNode2) {
                leader.setTargetObjOrgVersionId(treeNode2.getOrgVersionId());
            }
            if (null != treeNode || null != treeNode2) {
                multiOrgElementLeaderService.save(leader);
            }
        }

        List<MultiOrgUserTreeNode> userTreeNodes = multiOrgUserTreeNodeService.queryAllNodeByVersionId(oldOrgVersionId);
        for (MultiOrgUserTreeNode userTreeNode : userTreeNodes) {
            MultiOrgTreeNode treeNode = treeNodeMaps.get(userTreeNode.getEleId());
            if (null != treeNode) {
                userTreeNode.setOrgVersionId(treeNode.getOrgVersionId());
                multiOrgUserTreeNodeService.save(userTreeNode);
            }
        }

    }

    @Override
    public MultiOrgVersion getDefaultVersionBySystemUnitId(String systemUnitId) {
        MultiOrgVersion example = new MultiOrgVersion();
        example.setStatus(1);
        example.setIsDefault(true);
        example.setSystemUnitId(systemUnitId);
        List<MultiOrgVersion> objs = multiOrgVersionService.findByExample(example);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }
}
