package com.wellsoft.pt.basicdata.treecomponent.facade.support;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.tree.TreeNode;

import java.util.List;
import java.util.Map;

/**
 * Description: 树节点接口
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年05月25日   chenq	 Create
 * </pre>
 */
public interface TreeNodeDataProvider {

    /**
     * 获取树形节点数据集合
     *
     * @return
     */
    List<TreeNode> getNodes(String parentId, boolean isAsync, Map<String, Object> params);

    /**
     * 根据树节点键获取对应的节点名称，提供根据节点id显示名称的使用用途
     *
     * @param keys
     * @return
     */
    List<Select2DataBean> getNodeNamesByKeys(String[] keys);


    /**
     * 根据搜索值返回符合的节点数据
     *
     * @param name
     * @return
     */
    List<TreeNode> asyncSearchNodes(String parentId, String name, Map<String, Object> params);

    String name();

}
