package com.wellsoft.pt.multi.group.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.group.dao.MultiGroupTreeNodeDao;
import com.wellsoft.pt.multi.group.entity.MultiGroupTreeNode;
import com.wellsoft.pt.multi.group.service.MultiGroupTreeNodeService;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.service.MultiOrgVersionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Auther: yt
 * @Date: 2022/2/10 10:09
 * @Description:
 */
@Service
public class MultiGroupTreeNodeServiceImpl extends AbstractJpaServiceImpl<MultiGroupTreeNode, MultiGroupTreeNodeDao, String> implements MultiGroupTreeNodeService {
    @Autowired
    private MultiOrgVersionService multiOrgVersionService;

    @Override
    public MultiGroupTreeNode getById(String id) {
        MultiGroupTreeNode multiGroupTreeNode = this.getDao().getOneByFieldEq("id", id);
        return multiGroupTreeNode;
    }

    @Override
    public Set<String> getOrgVersionIdById(String id) {
        Set<String> orgVersionIdSet = new HashSet<>();
        MultiGroupTreeNode multiGroupTreeNode = this.getById(id);
        if (multiGroupTreeNode == null) {
            return null;
        }
        this.addOrgVersionId(orgVersionIdSet, multiGroupTreeNode);
        return orgVersionIdSet;
    }

    @Override
    public long countByGroupUuid(String groupUuid) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupUuid", groupUuid);
        long count = this.getDao().countByHQL("select count(*) from MultiGroupTreeNode where groupUuid = :groupUuid ", params);
        return count;
    }


    @Override
    public long countByParentId(String parentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", parentId);
        long count = this.getDao().countByHQL("select count(*) from MultiGroupTreeNode where parentId = :parentId ", params);
        return count;
    }

    public long countByParentId(String parentId, String groupId) {
        if (StringUtils.isBlank(groupId)) {
            return countByParentId(parentId);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("parentId", parentId);
            params.put("groupId", groupId);
            return this.getDao().countByHQL("select count(*) from MultiGroupTreeNode where parentId = :parentId and groupUuid in (select uuid from MultiGroup where id = :groupId) ", params);
        }
    }

    @Override
    public List<MultiGroupTreeNode> queryParentId(String parentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", parentId);
        List<MultiGroupTreeNode> groupTreeNodeList = this.listByHQL("from MultiGroupTreeNode where parentId = :parentId order by seq", params);
        return groupTreeNodeList;
    }

    public List<MultiGroupTreeNode> queryParentId(String parentId, String groupId) {
        if (StringUtils.isBlank(groupId)) {
            return queryParentId(parentId);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("parentId", parentId);
            params.put("groupId", groupId);
            return this.listByHQL("from MultiGroupTreeNode where parentId = :parentId and groupUuid in (select uuid from MultiGroup where id = :groupId) order by seq", params);
        }
    }

    private void addOrgVersionId(Set<String> orgVersionIdSet, MultiGroupTreeNode multiGroupTreeNode) {
        String unitId = null;
        if (multiGroupTreeNode.getType() == 1) {
            unitId = multiGroupTreeNode.getEleId();
        } else if (multiGroupTreeNode.getType() == 2) {
            unitId = multiGroupTreeNode.getId();
        }
        if (unitId != null) {
            MultiOrgVersion multiOrgVersion = multiOrgVersionService.enabledByDefault(unitId);
            if (multiOrgVersion != null) {
                orgVersionIdSet.add(multiOrgVersion.getId());
            }
        }
        List<MultiGroupTreeNode> treeNodeList = this.queryParentId(multiGroupTreeNode.getId());
        for (MultiGroupTreeNode groupTreeNode : treeNodeList) {
            addOrgVersionId(orgVersionIdSet, groupTreeNode);
        }
    }
}
