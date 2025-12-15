package com.wellsoft.pt.multi.group.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.group.dao.MultiGroupTreeNodeDao;
import com.wellsoft.pt.multi.group.entity.MultiGroupTreeNode;

import java.util.List;
import java.util.Set;

/**
 * @Auther: yt
 * @Date: 2022/2/10 10:09
 * @Description:
 */
public interface MultiGroupTreeNodeService extends JpaService<MultiGroupTreeNode, MultiGroupTreeNodeDao, String> {

    MultiGroupTreeNode getById(String id);

    Set<String> getOrgVersionIdById(String id);

    long countByGroupUuid(String groupUuid);


    long countByParentId(String parentId);

    long countByParentId(String parentId, String groupId);

    List<MultiGroupTreeNode> queryParentId(String parentId);

    List<MultiGroupTreeNode> queryParentId(String parentId, String groupId);
}
