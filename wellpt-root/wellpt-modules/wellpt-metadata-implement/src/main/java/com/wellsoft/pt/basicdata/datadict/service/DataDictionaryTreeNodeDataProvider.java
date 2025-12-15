package com.wellsoft.pt.basicdata.datadict.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeNodeDataProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 数据字典树形数据接口，提供树形下拉控件的数据接口服务使用
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年05月25日   chenq	 Create
 * </pre>
 */
@Service
public class DataDictionaryTreeNodeDataProvider implements TreeNodeDataProvider {

    @Autowired
    DataDictionaryService dataDictionaryService;

    @Override
    public List<TreeNode> getNodes(String parentId, boolean isAsync, Map<String, Object> params) {
        return isAsync ? dataDictionaryService.getAsTreeAsync(parentId) : dataDictionaryService.getAllDataDicAsTree(parentId).getChildren();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Select2DataBean> getNodeNamesByKeys(String[] keys) {
        if (ArrayUtils.isNotEmpty(keys)) {
            List<Select2DataBean> select2DataBeans = Lists.newArrayList();
            for (String k : keys) {
                DataDictionary dataDictionary = dataDictionaryService.get(k);
                if (dataDictionary != null) {
                    Select2DataBean data = new Select2DataBean(dataDictionary.getUuid(), dataDictionary.getName());
                    while (dataDictionary.getParent() != null) {
                        data.setText(dataDictionary.getParent().getName() + "/" + data.getText());
                        dataDictionary = dataDictionary.getParent();
                    }
                    select2DataBeans.add(data);
                }
            }
            return select2DataBeans;
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreeNode> asyncSearchNodes(String parentId, String name, Map<String, Object> params) {
        List<DataDictionary> dataDictionaries = dataDictionaryService.getByNameLikes(name);
        Map<String, TreeNode> nodeMap = Maps.newHashMap();
        Map<String, Set<String>> childAdded = Maps.newHashMap();
        Set<String> parentIds = Sets.newLinkedHashSet();
        if (CollectionUtils.isNotEmpty(dataDictionaries)) {
            for (DataDictionary dataDictionary : dataDictionaries) {
                // 每个节点向上追溯父级节点
                TreeNode node = new TreeNode();
                node.setId(dataDictionary.getUuid());
                node.setName(dataDictionary.getName());
                int num = dataDictionaryService.countChildren(dataDictionary.getUuid());
                node.setIsParent(num > 0);
                node.setNocheck(num > 0);
                nodeMap.put(node.getId(), node);
                DataDictionary parent = dataDictionary.getParent();
                String prevParentId = null;
                if (parent != null) {
                    while (parent != null) {
                        TreeNode parentNode = new TreeNode();
                        if (nodeMap.containsKey(parent.getUuid())) {
                            parentNode = nodeMap.get(parent.getUuid());
                        } else {
                            parentNode.setId(parent.getUuid());
                            parentNode.setName(parent.getName());
                            parentNode.setIsParent(true);
                            parentNode.setNocheck(true);
                            nodeMap.put(parentNode.getId(), parentNode);
                        }
                        //已经添加到相关父节点下，则忽略添加
                        if (!childAdded.containsKey(parentNode.getId()) || !childAdded.get(parentNode.getId()).contains(node.getId())) {
                            parentNode.getChildren().add(nodeMap.get(node.getId()));
                        }
                        prevParentId = parentNode.getId();
                        parent = parent.getParent();
                    }
                } else {
                    prevParentId = node.getId();
                    nodeMap.put(node.getId(), node);
                }
                parentIds.add(prevParentId);
            }
            List<TreeNode> nodeList = Lists.newArrayList();
            for (String pid : parentIds) {
                if (StringUtils.isBlank(parentId) || TreeNode.ROOT_ID.equals(parentId) || pid.equals(parentId)) {
                    nodeList.add(nodeMap.get(pid));
                }
            }
            return nodeList;
        }
        return Collections.EMPTY_LIST;
    }


    @Override
    public String name() {
        return "数据字典-树形下拉节点服务";
    }
}
