package com.wellsoft.pt.multi.group.service.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.group.dao.MultiGroupDao;
import com.wellsoft.pt.multi.group.entity.MultiGroup;
import com.wellsoft.pt.multi.group.entity.MultiGroupTreeNode;
import com.wellsoft.pt.multi.group.service.MultiGroupService;
import com.wellsoft.pt.multi.group.service.MultiGroupTreeNodeService;
import com.wellsoft.pt.multi.group.vo.MultiGroupVo;
import com.wellsoft.pt.multi.group.vo.TreeNodeVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: yt
 * @Date: 2022/2/10 11:26
 * @Description:
 */
@Service
public class MultiGroupServiceImpl extends AbstractJpaServiceImpl<MultiGroup, MultiGroupDao, String> implements MultiGroupService {
    private static final String MULTI_GROUP_ID_PATTERN = IdPrefix.MULTI_GROUP.getValue() + "0000000000";
    private static final String MULTI_GROUP_CATEGORY_ID_PATTERN = IdPrefix.MULTI_GROUP_CATEGORY.getValue() + "0000000000";

    @Autowired
    private MultiGroupTreeNodeService multiGroupTreeNodeService;
    @Autowired
    private IdGeneratorService idGeneratorService;


    @Override
    public MultiGroupVo get(String uuid) {
        MultiGroup multiGroup = this.getOne(uuid);
        Assert.notNull(multiGroup, "集团数据不存在");
        MultiGroupVo multiGroupVo = new MultiGroupVo();
        BeanUtils.copyProperties(multiGroup, multiGroupVo);
        List<MultiGroupTreeNode> treeNodeList = multiGroupTreeNodeService.getDao().listByFieldEqValue("groupUuid", uuid);
        if (treeNodeList.size() > 0) {
            this.addTreeNode(multiGroupVo, treeNodeList);
        }
        return multiGroupVo;
    }

    private void addTreeNode(MultiGroupVo groupVo, List<MultiGroupTreeNode> treeNodeList) {
        //key id
        Map<String, TreeNodeVo> nodeVoMap = new HashMap<>();
        //key parentId
        Map<String, List<MultiGroupTreeNode>> nodeListMap = new HashMap<>();
        for (MultiGroupTreeNode treeNode : treeNodeList) {
            TreeNodeVo nodeVo = new TreeNodeVo();
            BeanUtils.copyProperties(treeNode, nodeVo);
            nodeVoMap.put(nodeVo.getId(), nodeVo);
            if (StringUtils.isNotBlank(treeNode.getParentId())) {
                List<MultiGroupTreeNode> nodeList = nodeListMap.get(treeNode.getParentId());
                if (nodeList == null) {
                    nodeList = new ArrayList<>();
                    nodeListMap.put(treeNode.getParentId(), nodeList);
                }
                nodeList.add(treeNode);
            }
        }
        //根据 seq 排序list null值排最前面 防止错误
        for (String key : nodeListMap.keySet()) {
            List<MultiGroupTreeNode> nodeList = nodeListMap.get(key);
            nodeList = nodeList.stream().sorted(Comparator.comparing(MultiGroupTreeNode::getSeq,
                    Comparator.nullsFirst(Integer::compareTo)))
                    .collect(Collectors.toList());
            nodeListMap.put(key, nodeList);
        }
        //根据根节点id添加子节点
        this.addChildren(nodeListMap, nodeVoMap, groupVo.getId());
        //取出根节点
        TreeNodeVo rootVo = nodeVoMap.get(groupVo.getId());
        groupVo.setTreeNode(rootVo);
    }

    private void addChildren(Map<String, List<MultiGroupTreeNode>> nodeListMap, Map<String, TreeNodeVo> nodeVoMap, String id) {
        TreeNodeVo nodeVo = nodeVoMap.get(id);
        List<MultiGroupTreeNode> nodeList = nodeListMap.get(id);
        if (nodeList == null) {
            return;
        }
        for (MultiGroupTreeNode treeNode : nodeList) {
            //添加nodevo到子节点
            nodeVo.getChildren().add(nodeVoMap.get(treeNode.getId()));
            //递归添加下一级节点
            this.addChildren(nodeListMap, nodeVoMap, treeNode.getId());
        }
    }

    @Override
    @Transactional
    public void addOrUpdate(MultiGroupVo multiGroupVo) {
        MultiGroup multiGroup = new MultiGroup();
        if (StringUtils.isNotBlank(multiGroupVo.getUuid())) {
            multiGroup = this.getOne(multiGroupVo.getUuid());
            Assert.notNull(multiGroup, "集团数据不存在");
            BeanUtils.copyProperties(multiGroupVo, multiGroup, "id");
            List<MultiGroupTreeNode> treeNodeList = multiGroupTreeNodeService.getDao().listByFieldEqValue("groupUuid", multiGroup.getUuid());
            multiGroupTreeNodeService.deleteByEntities(treeNodeList);
        } else {
            String id = idGeneratorService.generate(MultiGroup.class, MULTI_GROUP_ID_PATTERN);
            BeanUtils.copyProperties(multiGroupVo, multiGroup, "id");
            multiGroup.setId(id);
            multiGroup.setIsEnable(1);
        }
        this.save(multiGroup);
        if (multiGroupVo.getTreeNode() != null) {
            this.addNodeList(multiGroup, multiGroupVo.getTreeNode(), null, 1);
        }
    }

    private void addNodeList(MultiGroup multiGroup, TreeNodeVo treeNodeVo, String parentId, int seq) {
        MultiGroupTreeNode treeNode = new MultiGroupTreeNode();
        BeanUtils.copyProperties(treeNodeVo, treeNode);
        treeNode.setParentId(parentId);
        treeNode.setGroupUuid(multiGroup.getUuid());
        treeNode.setSeq(seq);
        if (treeNodeVo.getType().intValue() == 1) {
            treeNode.setId(multiGroup.getId());
        } else if (treeNodeVo.getType().intValue() == 2) {
            Assert.notNull(treeNodeVo.getId(), "单位节点Id不能为空");
        } else if (treeNodeVo.getType().intValue() == 3 && StringUtils.isBlank(treeNodeVo.getId())) {
            String id = idGeneratorService.generate(MultiGroupTreeNode.class, MULTI_GROUP_CATEGORY_ID_PATTERN);
            treeNode.setId(id);
        }
        multiGroupTreeNodeService.save(treeNode);
        for (int i = 0; i < treeNodeVo.getChildren().size(); i++) {
            TreeNodeVo children = treeNodeVo.getChildren().get(i);
            this.addNodeList(multiGroup, children, treeNode.getId(), i + 1);
        }

    }

    @Override
    @Transactional
    public void del(List<String> uuids) {
        if (CollectionUtils.isNotEmpty(uuids)) {
            this.deleteByUuids(uuids);
            Map<String, Object> params = new HashMap<>();
            params.put("groupUuids", uuids);
            multiGroupTreeNodeService.updateByHQL("delete from MultiGroupTreeNode where groupUuid in (:groupUuids) ", params);
        }
    }

    @Override
    @Transactional
    public void isEnable(String uuid, Integer isEnable) {
        MultiGroup multiGroup = this.getOne(uuid);
        Assert.notNull(multiGroup, "集团数据不存在");
        multiGroup.setIsEnable(isEnable);
        this.update(multiGroup);
    }

    @Override
    public MultiGroup getById(String id) {
        MultiGroup multiGroup = this.getDao().getOneByFieldEq("id", id);
        return multiGroup;
    }

    @Override
    public List<MultiGroup> getEnableList() {
        Map<String, Object> params = new HashMap<>();
        params.put("isEnable", 1);
        List<MultiGroup> multiGroupList = this.getDao().listByHQL("from MultiGroup where isEnable=:isEnable order by code", params);
        return multiGroupList;
    }

    @Override
    public List<MultiGroup> getEnableList(String systemUnitId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", systemUnitId);
        return this.dao.listByHQL("from MultiGroup where isEnable = 1 and uuid in (select groupUuid from MultiGroupTreeNode where id = :id or eleId = :id) order by code desc ", params);
    }

}
