/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgJobDutyDto;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.dao.MultiOrgTreeNodeDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import com.wellsoft.pt.multi.org.service.MultiOrgElementService;
import com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserTreeNodeService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
@Service
public class MultiOrgTreeNodeServiceImpl extends AbstractJpaServiceImpl<MultiOrgTreeNode, MultiOrgTreeNodeDao, String>
        implements MultiOrgTreeNodeService {

    @Autowired
    private MultiOrgVersionFacade multiOrgVersionFacade;
    @Autowired
    private MultiOrgElementService multiOrgElementService;
    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;

    // 排序使用，不要删除，目前因为数据库中有排序了， 所以暂时关闭了
    public static void sortListForOrgTree(List<OrgTreeNodeDto> objs) {
        if (!CollectionUtils.isEmpty(objs)) {
            // 进行排序，保证第一个节点是根节点，并且同级节点之间是按编码升序排序
            Collections.sort(objs, new Comparator<OrgTreeNodeDto>() {
                @Override
                public int compare(OrgTreeNodeDto o1, OrgTreeNodeDto o2) {
                    String path1 = o1.getParentIdPath();
                    String path2 = o2.getParentIdPath();
                    String code1 = o1.getCode() == null ? "" : o1.getCode();
                    String code2 = o2.getCode() == null ? "" : o2.getCode();
                    // 同一节点下，以编码排序为准
                    if (path1.equals(path2)) {
                        return code1.compareTo(code2);
                    } else {
                        return path1.compareTo(path2);
                    }
                }

            });
        }
    }

    // 供给 jqgrid使用的分页接口
    @Override
    public List<OrgTreeNodeDto> query(QueryInfo queryInfo) {
        List<PropertyFilter> paramsList = queryInfo.getPropertyFilters();
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (!CollectionUtils.isEmpty(paramsList)) {
            for (PropertyFilter f : paramsList) {
                for (String propertyName : f.getPropertyNames()) {
                    params.put(propertyName, f.getMatchValue());
                }
            }
        }
        params.put("orderBy", "ele_id_path asc");
        String orderBy = queryInfo.getOrderBy();
        if (StringUtils.isNotBlank(orderBy)) {
            if (orderBy.startsWith("eleId")) {
                orderBy = orderBy.replace("eleId", "ele_id");
            }
            params.put("orderBy", orderBy);
        }
        String eleIdPath = params.get("eleIdPath") == null ? "" : params.get("eleIdPath").toString();
        String orgVersionId = params.get("versionId").toString();
        // 查询的是引入的其他系统单位的情况
        if (StringUtils.isNotBlank(eleIdPath) && !eleIdPath.startsWith(orgVersionId)) {
            // 因为存在对方升级了版本，所以不能直接通过版本号来过滤，需要通过引入单位的单位ID来过滤
            String vid = eleIdPath.indexOf("/") != -1 ? eleIdPath.substring(0, eleIdPath.indexOf("/")) : eleIdPath;
            MultiOrgVersion ver = this.multiOrgVersionFacade.getVersionById(vid);
            params.put("otherSysUnitId", ver.getSystemUnitId());
            // 需要移除 eleIdPath的赋值
            params.put("eleIdPath", null);
        }

        List<OrgTreeNodeDto> objs = listItemByNameSQLQuery("queryOrgTreeNodeListForPage", OrgTreeNodeDto.class, params,
                queryInfo.getPagingInfo());

        return objs;
    }

    @Override
    public List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPath(String orgVersionId, String eleIdPath) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", orgVersionId);
        params.put("eleIdPath", eleIdPath);
        // 数据空中已经排序过了
        List<OrgTreeNodeDto> objs = listItemByNameSQLQuery("queryAllNodeOfOrgVersionByEleIdPath", OrgTreeNodeDto.class,
                params, null);

        // 因为数据库中已经排序过了，所以这里可以取消排序，如果数据库中不是按ele_id_path升序排，则需要重新开启这段排序代码
        this.sortListForOrgTree(objs);
        return objs;

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService#queryAllNodeOfOrgVersionByEleIdPath(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPath(String orgVersionId, String eleIdPath,
                                                                    String eleType) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", orgVersionId);
        params.put("eleIdPath", eleIdPath);
        params.put("eleType", eleType);
        // 数据空中已经排序过了
        List<OrgTreeNodeDto> objs = listItemByNameSQLQuery("queryAllNodeOfOrgVersionByEleIdPath", OrgTreeNodeDto.class,
                params, null);

        // 因为数据库中已经排序过了，所以这里可以取消排序，如果数据库中不是按ele_id_path升序排，则需要重新开启这段排序代码
        this.sortListForOrgTree(objs);
        return objs;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService#queryAllNodeOfOrgVersionByEleIdPathAndEleTypes(java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public List<OrgTreeNodeDto> queryAllNodeOfOrgVersionByEleIdPathAndEleTypes(String orgVersionId, String eleIdPath,
                                                                               String... eleTypes) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", orgVersionId);
        params.put("eleIdPath", eleIdPath);
        params.put("eleTypes", eleTypes);
        // 数据空中已经排序过了
        List<OrgTreeNodeDto> objs = listItemByNameSQLQuery("queryAllNodeOfOrgVersionByEleIdPath", OrgTreeNodeDto.class,
                params, null);

        // 因为数据库中已经排序过了，所以这里可以取消排序，如果数据库中不是按ele_id_path升序排，则需要重新开启这段排序代码
        this.sortListForOrgTree(objs);
        return objs;
    }

    /**
     * 获取一个组织的所有子节点，不包括根节点
     */
    @Override
    public List<OrgTreeNodeDto> queryAllChildrenNodeOfOrgVersionByEleIdPath(String orgVersionId, String eleIdPath) {
        List<OrgTreeNodeDto> objs = this.queryAllNodeOfOrgVersionByEleIdPath(orgVersionId, eleIdPath);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        // 剔除根节点（第一条记录）
        objs.remove(0);
        return objs;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.dao.MultiOrgTreeNodeDao#getParentNode(java.lang.String, java.lang.String)
     */
    @Override
    public MultiOrgTreeNode getParentNode(String orgVersionId, String parentEleIdPath) {
        MultiOrgTreeNode q = new MultiOrgTreeNode();
        q.setOrgVersionId(orgVersionId);
        q.setEleIdPath(parentEleIdPath);
        List<MultiOrgTreeNode> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    /**
     * 通过treeNodeUuid 获取一个树节点信息
     */
    @Override
    public OrgTreeNodeDto getNode(String uuid) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("uuid", uuid);
        // 数据空中已经排序过了
        List<OrgTreeNodeDto> objs = listItemByNameSQLQuery("getNode", OrgTreeNodeDto.class, params, null);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    /**
     * 修改节点和对应的所有的子节点的全路径名称信息和全路径ID
     */
    @Override
    public void modifyNodeAndAllChildrenNodePathByOrgVersion(String oldEleIdPath, String newEleIdPath,
                                                             String oldOrgVersionId, String newOrgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("oldIdPathPrev", oldEleIdPath);
        params.put("newIdPathPrev", newEleIdPath);
        params.put("oldOrgVersionId", oldOrgVersionId);
        params.put("newOrgVersionId", newOrgVersionId);
        if (!oldEleIdPath.equals(newEleIdPath)) {
            // 上级节点发生变化了，那不管名称变不变，全路径都要发生变化
            this.dao.updateByNamedSQL("updateEleIdPathAndEleNamePathOfChildrenNodes", params);
        }
    }

    /**
     * 获取一个节点元素被其他版本引用的情况
     */
    @Override
    public List<MultiOrgTreeNode> queryOtherOrgVersionListByEleId(String eleId, String orgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("eleId", eleId);
        params.put("orgVersionId", orgVersionId);
        return listByNameSQLQuery("queryOtherOrgVersionListByEleId", params);
    }

    /**
     * 如何描述该方法
     */
    @Override
    public List<OrgTreeNodeDto> queryTreeNodeByEleId(String eleId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("eleId", eleId);
        return listItemByNameSQLQuery("queryTreeNodeByEleId", OrgTreeNodeDto.class, params, null);
    }

    /**
     * 通过元素ID和orgVersion获取对应的组织树节点信息
     */
    @Override
    public OrgTreeNodeDto getNodeByEleIdAndOrgVersion(String eleId, String orgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("eleId", eleId);
        params.put("orgVersionId", orgVersionId);
        List<OrgTreeNodeDto> objs = listItemByNameSQLQuery("getNodeByEleIdAndOrgVersion", OrgTreeNodeDto.class, params,
                null);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    /**
     * 获取引用了该版本的其他版本
     */
    @Override
    public List<MultiOrgTreeNode> queryParentSystemUnitOrg(String rootVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("versionId", rootVersionId);
        return listByNameSQLQuery("queryParentSystemUnitOrg", params);
    }

    @Override
    public List<OrgJobDutyDto> queryJobListWithDutyByVersionId(String versionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("versionId", versionId);
        return listItemByNameSQLQuery("queryJobListWithDutyByVersionId", OrgJobDutyDto.class, params, null);
    }

    @Override
    public List<OrgJobDutyDto> queryJobListWithDutyByVersionIdList(List<String> versionIdList, String keyword) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("versionIdList", versionIdList);
        params.put("keyword", keyword);
        return listItemByNameSQLQuery("queryJobListWithDutyByVersionIdList", OrgJobDutyDto.class, params, null);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.dao.MultiOrgTreeNodeDao#getOtherSystemUnitByOrgVersionId(java.lang.String, java.lang.String)
     */
    @Override
    public MultiOrgTreeNode getOtherSystemUnitByOrgVersionId(String otherSystemUnitId, String orgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("versionId", orgVersionId);
        params.put("systemUnitId", otherSystemUnitId);
        List<MultiOrgTreeNode> objs = listByNameSQLQuery("getOtherSystemUnitByOrgVersionId", params);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.dao.MultiOrgTreeNodeDao#queryJobNodeListOfCurrentVerisonByDutyId(java.lang.String)
     */
    @Override
    public List<OrgTreeNodeDto> queryJobNodeListOfCurrentVerisonByDutyId(String dutyId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("dutyId", dutyId);
        return listItemByNameSQLQuery("queryJobNodeListOfCurrentVerisonByDutyId", OrgTreeNodeDto.class, params, null);
    }

    @Override
    public List<MultiOrgTreeNode> queryNodeByEleId(String id) {
        MultiOrgTreeNode q = new MultiOrgTreeNode();
        q.setEleId(id);
        return this.dao.listByEntity(q);
    }

    @Override
    public List<MultiOrgTreeNode> queryNodeByEleIdPath(String eleIdPath) {
        MultiOrgTreeNode q = new MultiOrgTreeNode();
        q.setEleIdPath(eleIdPath);
        return this.dao.listByEntity(q);
    }

    @Override
    public List<MultiOrgTreeNode> queryNodeByVersionId(String versionId) {
        MultiOrgTreeNode q = new MultiOrgTreeNode();
        q.setOrgVersionId(versionId);
        return this.dao.listByEntity(q);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService#queryJobListByEleIdAndVersionId(java.lang.String, java.lang.String)
     */
    @Override
    public List<OrgTreeNodeDto> queryJobListByEleIdAndVersionId(String orgVersionId, String eleId) {
        OrgTreeNodeDto node = this.getNodeByEleIdAndOrgVersion(eleId, orgVersionId);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", orgVersionId);
        params.put("eleIdPath", node.getEleIdPath());
        List<OrgTreeNodeDto> objs = listItemByNameSQLQuery("queryJobListByEleIdPath", OrgTreeNodeDto.class, params,
                null);
        return objs;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService#clearOrgTree(com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto)
     */
    @Override
    public void clearOrgTree(OrgTreeNodeDto selfUnit) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", selfUnit.getOrgVersionId());
        params.put("eleIdPath", selfUnit.getEleIdPath());
        this.dao.deleteByNamedSQL("clearOrgTree", params);

    }

    // 获取一个组织版本的所有领导上下级关系
    @Override
    public List<QueryItem> queryAllLeaderListByVerId(String verId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", verId);
        return this.dao.listQueryItemByNameSQLQuery("queryAllLeaderListByVerId", params, null);
    }

    @Override
    public MultiOrgTreeNode queryByVerIdEleId(String verId, String eleId) {
        MultiOrgTreeNode q = new MultiOrgTreeNode();
        q.setOrgVersionId(verId);
        q.setEleId(eleId);
        List<MultiOrgTreeNode> multiOrgTreeNodes = this.dao.listByEntity(q);
        if (multiOrgTreeNodes.size() < 1) {
            throw new RuntimeException(String.format("MultiOrgTreeNode <1 queryBy orgVersionId:%s,eleId:%s ", verId, eleId));
        }
        if (multiOrgTreeNodes.size() > 1) {
            throw new RuntimeException(String.format("MultiOrgTreeNode >1 queryBy orgVersionId:%s,eleId:%s ", verId, eleId));
        }
        MultiOrgTreeNode treeNode = multiOrgTreeNodes.get(0);
        return treeNode;
    }

    @Override
    public int countChildren(String verId, String eleId) {
        HashMap<String, Object> params = getQueryParams(verId, eleId);
        long count = this.dao.countByHQL("select count(uuid) from MultiOrgTreeNode where orgVersionId=:orgVersionId and eleIdPath like :eleIdPath ", params);
        return Integer.valueOf(count + "");
    }

    @Override
    public int countChildren(String verId, String eleId, Set<String> ignoreIdPrefix) {
        HashMap<String, Object> params = getQueryParams(verId, eleId);
        params.put("ignoreIdPrefix", ignoreIdPrefix);
        long count = this.dao.countByHQL("select count(a.uuid) from MultiOrgTreeNode a,MultiOrgElement b where " +
                "a.eleId=b.id and a.orgVersionId=:orgVersionId and a.eleIdPath like :eleIdPath and b.type not in (:ignoreIdPrefix)", params);
        return Integer.valueOf(count + "");
    }

    private HashMap<String, Object> getQueryParams(String verId, String eleId) {
        MultiOrgTreeNode treeNode = this.queryByVerIdEleId(verId, eleId);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", treeNode.getOrgVersionId());
        //根据固定长度 11位"_" 匹配下一级
        params.put("eleIdPath", treeNode.getEleIdPath() + MultiOrgService.PATH_SPLIT_SYSMBOL + "___________");
        return params;
    }

    @Override
    public List<MultiOrgElement> children(String verId, String eleId) {
        HashMap<String, Object> params = getQueryParams(verId, eleId);
        List<MultiOrgElement> elements = multiOrgElementService.listByHQL("from MultiOrgElement a where exists " +
                "(select 1 from MultiOrgTreeNode b where b.orgVersionId=:orgVersionId and b.eleId = a.id and b.eleIdPath like :eleIdPath ) " +
                "order by a.code", params);
        return elements;
    }

    @Override
    public List<MultiOrgElement> children(String verId, String eleId, Set<String> ignoreIdPrefix) {
        HashMap<String, Object> params = getQueryParams(verId, eleId);
        params.put("ignoreIdPrefix", ignoreIdPrefix);
        List<MultiOrgElement> elements = multiOrgElementService.listByHQL("from MultiOrgElement a where a.type not in (:ignoreIdPrefix) and exists " +
                "(select 1 from MultiOrgTreeNode b where b.orgVersionId=:orgVersionId and b.eleId = a.id and b.eleIdPath like :eleIdPath ) " +
                "order by a.code", params);
        return elements;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService#queryOrgTreeNodeByEleIdPaths(java.util.List)
     */
    @Override
    public List<OrgTreeNodeDto> queryOrgTreeNodeByEleIdPaths(List<String> eleIdPaths) {
        // 数据空中已经排序过了
        List<OrgTreeNodeDto> allObjs = Lists.newArrayList();

        Set<String> tmpEleIdPaths = new HashSet<String>();
        int num = 0;
        for (String eleIdPath : eleIdPaths) {
            num++;
            tmpEleIdPaths.add(eleIdPath);
            if (num % 1000 == 0 || num == eleIdPaths.size()) {
                Map<String, Object> values = Maps.newHashMap();
                values.put("eleIdPaths", tmpEleIdPaths);
                List<OrgTreeNodeDto> objs = listItemByNameSQLQuery("queryOrgTreeNodeByEleIdPaths", OrgTreeNodeDto.class,
                        values, null);
                allObjs.addAll(objs);
                tmpEleIdPaths.clear();
            }
        }

        // 因为数据库中已经排序过了，所以这里可以取消排序，如果数据库中不是按ele_id_path升序排，则需要重新开启这段排序代码
        this.sortListForOrgTree(allObjs);
        return allObjs;
    }
}
