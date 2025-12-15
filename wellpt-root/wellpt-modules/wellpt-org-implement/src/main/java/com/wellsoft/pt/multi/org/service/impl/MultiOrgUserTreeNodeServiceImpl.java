/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.collection.List2Map;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserTreeNodeDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserTreeNode;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgElementService;
import com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserTreeNodeService;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
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
public class MultiOrgUserTreeNodeServiceImpl
        extends AbstractJpaServiceImpl<MultiOrgUserTreeNode, MultiOrgUserTreeNodeDao, String>
        implements MultiOrgUserTreeNodeService {

    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public List<MultiOrgUserTreeNode> queryUserJobByOrgVersion(String userId, String orgVersionId) {
        MultiOrgUserTreeNode q = new MultiOrgUserTreeNode();
        q.setOrgVersionId(orgVersionId);
        q.setUserId(userId);
        List<MultiOrgUserTreeNode> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            // 排序下，主职放前面
            Collections.sort(objs, new Comparator<MultiOrgUserTreeNode>() {
                @Override
                public int compare(MultiOrgUserTreeNode o1, MultiOrgUserTreeNode o2) {
                    Integer isMain1 = o1.getIsMain() == null ? new Integer(0) : o1.getIsMain();
                    Integer isMain2 = o2.getIsMain() == null ? new Integer(0) : o2.getIsMain();
                    return isMain2.compareTo(isMain1);
                }
            });
        }
        return objs;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.dao.MultiOrgUserTreeNodeDao#queryUserByOrgTreeNode(java.lang.String, java.lang.String)
     */
    @Override
    public List<OrgUserTreeNodeDto> queryUserByOrgTreeNode(String eleId, String orgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("eleId", eleId);
        params.put("orgVersionId", orgVersionId);
        return listItemByNameSQLQuery("queryUserByOrgTreeNode", OrgUserTreeNodeDto.class, params, null);
    }

    @Override
    public List<SimpleUser> querSimpleUsers(String eleId, String orgVersionId) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        query.put("eleId", eleId);
        StringBuilder hqlSb = new StringBuilder(
                "select distinct a.id as id,a.userName as name,a.code as code from MultiOrgUserAccount a,MultiOrgUserTreeNode b ");
        if (eleId.startsWith(IdPrefix.JOB.getValue())) {
            hqlSb.append(
                    "where a.id = b.userId and a.isForbidden = 0 and b.orgVersionId = :orgVersionId and b.eleId = :eleId ");
        } else {
            MultiOrgTreeNode treeNode = multiOrgTreeNodeService.queryByVerIdEleId(orgVersionId, eleId);
            query.put("eleIdPath", treeNode.getEleIdPath() + "%");
            hqlSb.append(",MultiOrgTreeNode c ");
            hqlSb.append(
                    " where a.id = b.userId and b.orgVersionId = c.orgVersionId and b.eleId = c.eleId and a.isForbidden = 0 and b.orgVersionId = :orgVersionId and c.eleIdPath like :eleIdPath ");
        }
        hqlSb.append(" order by a.code");
        List<SimpleUser> simpleUsers = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(), SimpleUser.class,
                query);
        return simpleUsers;
    }

    @Override
    public List<OrgUserDto> querOrgUsers(String eleId, String orgVersionId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("orgVersionId", orgVersionId);
        values.put("eleId", eleId);
        if (eleId.startsWith(IdPrefix.JOB.getValue())) {
            values.put("idPrefix", IdPrefix.JOB.getValue());
        } else {
            MultiOrgTreeNode treeNode = multiOrgTreeNodeService.queryByVerIdEleId(orgVersionId, eleId);
            values.put("eleIdPath", treeNode.getEleIdPath() + "%");
        }

        // QueryItem implements BaseQueryItem
        List<OrgUserDto> queryItems = this.dao.listItemByNameHQLQuery("querOrgUsers", OrgUserDto.class, values,
                new PagingInfo(1, Integer.MAX_VALUE));
        return queryItems;
    }

    /**
     * 删除用户的职位
     * <p>
     * (non-Javadoc)
     */
    @Override
    public boolean deleteUserJobByOrgVersion(String userId, String orgVersionId) {
        MultiOrgUserTreeNode q = new MultiOrgUserTreeNode();
        q.setUserId(userId);
        q.setOrgVersionId(orgVersionId);
        List<MultiOrgUserTreeNode> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            this.deleteByEntities(objs);
        }
        // 要先执行，释放掉，不然会被数据库的唯一索引检查到，然后失败
        this.dao.getSession().flush();
        return true;
    }

    @Override
    public List<OrgUserTreeNodeDto> queryUserByOrgVersion(String orgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", orgVersionId);
        return listItemByNameSQLQuery("queryUserByOrgTreeNode", OrgUserTreeNodeDto.class, params, null);
    }

    // 节点发生变化，需要迁移对应节点的用户到新节点下
    @Override
    public void updateUserEleIdByOrgVersion(String oldEleId, String newEleId, String orgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", orgVersionId);
        params.put("oldEleId", oldEleId);
        params.put("newEleId", newEleId);
        dao.updateByNamedSQL("updateUserEleIdByOrgVersion", params);
    }

    @Override
    public List<MultiOrgUserTreeNode> queryAllNodeByVersionId(String versionId) {
        MultiOrgUserTreeNode q = new MultiOrgUserTreeNode();
        q.setOrgVersionId(versionId);
        return this.dao.listByEntity(q);
    }

    @Override
    public void deleteUser(String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", id);
        this.dao.updateByNamedSQL("deleteUserFromTreeNode", params);
    }

    @Override
    public int countUser(String verId, String eleId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", verId);
        params.put("eleId", eleId);
        params.put("isForbidden", 0);
        long count = this.dao.countByHQL(
                "select count(userId) from MultiOrgUserTreeNode a where a.orgVersionId = :orgVersionId and a.eleId = :eleId and exists "
                        + "(select 1 from MultiOrgUserAccount b where b.id = a.userId and b.isForbidden = :isForbidden ) ",
                params);
        return Integer.valueOf(count + "");
    }

    @Override
    public int countDisUser(String verId, String eleId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", verId);
        params.put("eleId", eleId);
        params.put("isForbidden", 1);
        long count = this.dao.countByHQL(
                "select count(userId) from MultiOrgUserTreeNode a where a.orgVersionId = :orgVersionId and a.eleId = :eleId and exists "
                        + "(select 1 from MultiOrgUserAccount b where b.id = a.userId and b.isForbidden = :isForbidden ) ",
                params);
        return Integer.valueOf(count + "");
    }

    @Override
    public MultiOrgUserTreeNode queryUserJobByOrgVersionEleId(String userId, String orgVersionId, String eleId) {
        MultiOrgUserTreeNode q = new MultiOrgUserTreeNode();
        q.setOrgVersionId(orgVersionId);
        q.setUserId(userId);
        q.setEleId(eleId);
        List<MultiOrgUserTreeNode> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            return objs.get(0);
        }
        return null;
    }

    @Override
    public Map<String, UserJob> gerUserJob(String orgVersionId, List<String> userIds) {
        Map<String, UserJob> userJobs = new HashMap<>();
        if (userIds.size() == 0) {
            return userJobs;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder(
                "select a from MultiOrgUserTreeNode a,MultiOrgVersion b where a.orgVersionId = b.id and b.status=1 and ");
        HqlUtils.appendSql("a.userId", query, hql, Sets.<Serializable>newHashSet(userIds));
        if (StringUtils.isNotBlank(orgVersionId)) {
            hql.append(" and a.orgVersionId=:orgVersionId ");
            query.put("orgVersionId", orgVersionId);
        }
        List<MultiOrgUserTreeNode> userTreeNodes = this.listByHQL(hql.toString(), query);
        if (userTreeNodes.size() == 0) {
            return userJobs;
        }
        Map<String, OrgElementVo> jobIds = new HashMap<>();
        for (MultiOrgUserTreeNode userTreeNode : userTreeNodes) {
            // 初始化化UserJob
            if (!userJobs.containsKey(userTreeNode.getUserId())) {
                UserJob userJob = new UserJob();
                userJob.setMainJobs(new ArrayList<OrgElementVo>());
                userJob.setOtherJobs(new ArrayList<OrgElementVo>());
                userJobs.put(userTreeNode.getUserId(), userJob);
            }
            OrgElementVo orgElementVo = null;
            if (jobIds.containsKey(userTreeNode.getEleId())) {
                orgElementVo = jobIds.get(userTreeNode.getEleId());
            } else {
                orgElementVo = new OrgElementVo();
                orgElementVo.setId(userTreeNode.getEleId());
                jobIds.put(userTreeNode.getEleId(), orgElementVo);
            }
            // 分别添加到主职 副职
            UserJob userJob = userJobs.get(userTreeNode.getUserId());
            if (userTreeNode.getIsMain() == 1) {
                userJob.getMainJobs().add(orgElementVo);
            } else {
                userJob.getOtherJobs().add(orgElementVo);
            }
        }

        if (jobIds.size() == 0) {
            return userJobs;
        }

        StringBuilder orgTreeHql = new StringBuilder(
                "select c from MultiOrgTreeNode c,MultiOrgVersion b where c.orgVersionId = b.id and b.status=1 and ");
        HqlUtils.appendSql("c.eleId", query, orgTreeHql, Sets.<Serializable>newHashSet(jobIds.keySet()));
        // 查询职位相关 treeNode
        List<MultiOrgTreeNode> jobTreeNodes = multiOrgTreeNodeService.listByHQL(orgTreeHql.toString(), query);
        // set 职位全路径 eleIdPath.split(/) 排除组织版本V
        Set<String> elementIds = new HashSet<>();
        for (MultiOrgTreeNode jobTreeNode : jobTreeNodes) {
            String[] elePathIds = jobTreeNode.getEleIdPath().split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            OrgElementVo jobElement = jobIds.get(jobTreeNode.getEleId());
            /**
             * eleIdPath: V0000000420/D0000002097/D0000002101/D0000002117/D0000002119/J0000004581
             * 倒序遍历
             * J0000004581,D0000002119,D0000002117,D0000002101,D0000002097,V0000000420
             */
            for (int i = elePathIds.length - 1; i >= 0; i--) {
                String eleId = elePathIds[i];
                elementIds.add(eleId);
                // 递归 添加 parent
                ConvertOrgNode.setParent(jobElement, eleId);
            }
        }
        StringBuilder orgElementHql = new StringBuilder("from MultiOrgElement c where ");
        HqlUtils.appendSql("c.id", query, orgElementHql, Sets.<Serializable>newHashSet(elementIds));
        // 查询 节点信息
        List<MultiOrgElement> orgElements = multiOrgElementService.listByHQL(orgElementHql.toString(), query);
        // 转为map
        Map<String, MultiOrgElement> orgElementMap = new List2Map<MultiOrgElement>() {
            @Override
            protected String getMapKey(MultiOrgElement obj) {
                return obj.getId();
            }
        }.convert(orgElements);
        // 添加上级节点parent信息
        Iterator<String> iterator = jobIds.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            OrgElementVo orgElementVo = jobIds.get(key);

            // 递归添加 parentName
            ConvertOrgNode.setParentName(orgElementVo, orgElementMap);
        }
        return userJobs;
    }

    /**
     * 根据用户Id 查询职位
     * Map<String,UserJob>  key=userId
     *
     * @param userIds
     * @return
     */
    @Override
    public Map<String, UserJob> gerUserJob(List<String> userIds) {
        return gerUserJob(null, userIds);
    }

    @Override
    public List<String> listUserIdsByVersionId(String fromVersionId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("vid", fromVersionId);
        return this.dao.listCharSequenceBySQL(
                "select distinct user_id as user_id from MULTI_ORG_USER_TREE_NODE where org_version_id = :vid", params);
    }

    @Override
    public List<String> queryUserIdsByLikeElementId(String elementId, String orgVersionId) {
        Map<String, Object> params = Maps.newHashMap();

        // 职位节点默认是最后一级，职位节点下不会再有节点，只有用户，因此职位Id的查询条件为 like '%JobId'
        if (elementId.startsWith(IdPrefix.JOB.getValue())) {
            params.put("eleId", "%" + elementId);
        } else {
            params.put("eleId", "%" + elementId + "%");
        }

        params.put("orgVersionId", orgVersionId);
        return this.dao.listCharSequenceBySQL("\n" + "select n.user_id as id\n"
                        + "  from multi_org_user_tree_node n, multi_org_tree_node t\n" + " where t.ele_id_path like :eleId\n"
                        + "   and t.ele_id like 'J%'\n" + "   and n.ele_id = t.ele_id\n"
                        + "   and n.org_version_id = t.org_version_id\n" + "   and t.org_version_id = :orgVersionId\n"
                        + "union\n" + "select n.user_id as id\n" + "  from multi_org_user_tree_node n, multi_org_tree_node t\n"
                        + " where t.ele_id_path like :eleId\n" + "   and t.ele_id like 'V%'\n"
                        + "   and t.ele_id = n.org_version_id\n" + "   and t.org_version_id = :orgVersionId\n" + "     ",
                params);
    }

    @Override
    public Map<String, Set<String>> getMainUserJobIdsIgnoreVersion(List<String> userids) {
        List<MultiOrgUserTreeNode> multiOrgTreeNodes = this.dao.listByFieldInValues("userId", userids);
        Map<String, Set<String>> map = Maps.newHashMap();
        for (MultiOrgUserTreeNode node : multiOrgTreeNodes) {
            if (node.getIsMain() == 1) {
                if (!map.containsKey(node.getUserId())) {
                    map.put(node.getUserId(), Sets.<String>newHashSet());
                }
                map.get(node.getUserId()).add(node.getEleId());
            }
        }
        return map;
    }

    @Override
    public Map<String, List<OrgUserJobDto>> getAllUserJobIdsIgnoreVersion(List<String> userids) {
        List<MultiOrgUserTreeNode> multiOrgTreeNodes = this.dao.listByFieldInValues("userId", userids);
        Map<String, List<OrgUserJobDto>> map = Maps.newHashMap();
        Map<String, String> tempMap = Maps.newHashMap();
        for (MultiOrgUserTreeNode node : multiOrgTreeNodes) {
            if (!map.containsKey(node.getUserId())) {
                map.put(node.getUserId(), Lists.<OrgUserJobDto>newArrayList());
            }
            OrgUserJobDto orgUserJobDto = new OrgUserJobDto();
            orgUserJobDto.setUserId(node.getUserId());
            if (node.getIsMain() == 1) {
                // 是主职
                orgUserJobDto.setIsMain(1);
            } else {
                orgUserJobDto.setIsMain(0);
            }
            // 去重
            if (!tempMap.containsKey(node.getUserId() + "_" + node.getEleId())) {
                OrgTreeNodeDto orgTreeNodeDto = new OrgTreeNodeDto();
                BeanUtils.copyProperties(node, orgTreeNodeDto);
                orgUserJobDto.setOrgTreeNodeDto(orgTreeNodeDto);
                map.get(node.getUserId()).add(orgUserJobDto);

                tempMap.put(node.getUserId() + "_" + node.getEleId(), node.getEleId());
            }
        }
        tempMap = null;
        return map;
    }

    @Override
    public List<MultiOrgUserTreeNode> queryMainJobByJobIdsAndUser(String[] jobids, String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", id);
        params.put("eleId", jobids);
        return this.dao.listByHQL(
                "from MultiOrgUserTreeNode where isMain = 1 and  eleId in (:eleId) and userId=:userId", params);
    }

}
