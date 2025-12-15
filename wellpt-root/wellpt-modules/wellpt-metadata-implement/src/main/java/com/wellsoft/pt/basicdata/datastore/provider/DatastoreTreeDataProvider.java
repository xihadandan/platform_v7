package com.wellsoft.pt.basicdata.datastore.provider;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeDataStoreRequestParam;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description: 根据数据仓库以及字段参数配置加载树节点
 *
 * @author chenq
 * @date 2018/7/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/3    chenq		2018/7/3		Create
 * </pre>
 */
@Component
public class DatastoreTreeDataProvider {

    @Autowired
    CdDataStoreService cdDataStoreService;

    @Transactional(readOnly = true)
    public List<TreeNode> loadTreeData(final TreeDataStoreRequestParam reqParams) {
        if (StringUtils.isBlank(reqParams.getValueColumn())) {//值字段为空的话，则返回节点的值默认取主键字段值
            reqParams.setValueColumn(reqParams.getUniqueColumn());
        }
        TreeNode rootNode = null;
        if (StringUtils.isNotBlank(reqParams.getTreeRootName()) && StringUtils.isNotBlank(
                reqParams.getParentColumnValue())) {//未指定父级的情况下，是从顶级拉取起，可以自定义顶级节点
            rootNode = new TreeNode("0", reqParams.getTreeRootName(), null);
            rootNode.setIsParent(true);
            rootNode.setNodeLevel(0);
            rootNode.setData(Maps.newHashMap());
        }

        if (!reqParams.getAsync()) {//非异步情况下，拉取全部数据，生成树形节点数据
            reqParams.setParentColumnValue(null);
            DataStoreData dataStoreData = this.loadData(reqParams);
            List<Map<String, Object>> dataList = dataStoreData.getData();
            // 将所有节点数据按上下级关系分组
            Multimap<String, Map<String, Object>> map = Multimaps.index(dataList,
                    new Function<Map<String, Object>, String>() {
                        @Nullable
                        @Override
                        public String apply(@Nullable Map<String, Object> map) {
                            Object parentValue = map.get(reqParams.getParentColumn());
                            if (parentValue != null) {
                                return StringUtils.isNotBlank(
                                        parentValue.toString()) ? parentValue.toString() : "-1";
                            }
                            return "-1";
                        }
                    });
            Map<String, Collection<Map<String, Object>>> groupDataMap = map.asMap();
            List<TreeNode> nodeList = Lists.newArrayList();
            if (groupDataMap.size() == 0) {
                return nodeList;
            }
            Collection<Map<String, Object>> roots = groupDataMap.get("-1");
            if (CollectionUtils.isEmpty(roots)) {
                return null;
            }
            Iterator<Map<String, Object>> topIterator = roots.iterator();
            while (topIterator.hasNext()) {
                Map<String, Object> node = topIterator.next();
                TreeNode treeNode = new TreeNode(node.get(reqParams.getUniqueColumn()).toString(),
                        node.get(reqParams.getDisplayColumn()).toString(), null);
                treeNode.setNodeLevel(0);
                if (reqParams.getNoCheckLevel().contains(treeNode.getNodeLevel())) {
                    treeNode.setNocheck(true);
                }
                treeNode.setData(node);
                cascadeAddChildTreeNode(treeNode, groupDataMap, reqParams);
                nodeList.add(treeNode);
            }

            return nodeList;
        }


        List<TreeNode> treeNodeList = cascadeLoadTreeNodes(reqParams,
                rootNode);
        if (reqParams.getAsync()) {//异步拉取，直接反馈子节点数据
            return treeNodeList;
        }
        if (rootNode != null) {
            rootNode.getChildren().addAll(treeNodeList);
            return Lists.newArrayList(rootNode);
        }
        return treeNodeList;
    }

    private void cascadeAddChildTreeNode(TreeNode parentNode,
                                         Map<String, Collection<Map<String, Object>>> groupDataMap,
                                         TreeDataStoreRequestParam reqParams) {
        Collection collection = groupDataMap.get(parentNode.getId());
        if (collection == null) {
            return;
        }
        Iterator<Map<String, Object>> childIterator = groupDataMap.get(
                parentNode.getId()).iterator();
        while (childIterator.hasNext()) {
            Map<String, Object> node = childIterator.next();
            TreeNode treeNode = new TreeNode(node.get(reqParams.getUniqueColumn()).toString(),
                    node.get(reqParams.getDisplayColumn()).toString(), null);
            treeNode.setNodeLevel(parentNode != null ? parentNode.getNodeLevel() + 1 : 0);
            if (reqParams.getNoCheckLevel().contains(treeNode.getNodeLevel())) {
                treeNode.setNocheck(true);
            }
            treeNode.setData(node);
            cascadeAddChildTreeNode(treeNode, groupDataMap, reqParams);
            parentNode.setIsParent(true);
            // parentNode.setNocheck(true);
            parentNode.getChildren().add(treeNode);
        }

    }


    private List<TreeNode> cascadeLoadTreeNodes(TreeDataStoreRequestParam reqParams,
                                                TreeNode parentNode) {
        if (parentNode != null && parentNode.getNodeLevel() == 1000) {
            throw new RuntimeException("超过分类递归最大上限次数:1000");
        }
        List<TreeNode> treeNodes = Lists.newArrayList();
        DataStoreData data = loadData(reqParams);
        List<Map<String, Object>> dataList = data.getData();
        for (Map<String, Object> dtMap : dataList) {
            TreeNode treeNode = new TreeNode(dtMap.get(reqParams.getUniqueColumn()).toString(),
                    dtMap.get(reqParams.getDisplayColumn()).toString(), null);
            treeNode.setNodeLevel(parentNode != null ? parentNode.getNodeLevel() + 1 : 0);
            if (reqParams.getNoCheckLevel().contains(treeNode.getNodeLevel())) {
                treeNode.setNocheck(true);
            }
            treeNode.setData(dtMap);

            //遍历子节点的请求参数
            TreeDataStoreRequestParam cascadeChildParam = new TreeDataStoreRequestParam();
            BeanUtils.copyProperties(reqParams, cascadeChildParam);
            cascadeChildParam.setParentColumnValue(
                    dtMap.get(reqParams.getUniqueColumn()).toString());//父级值

            if (StringUtils.isNotBlank(reqParams.getParentColumn()) && !reqParams.getAsync()) {
                //非异步拉取情况下，需要全量拉取子节点数据
                treeNode.getChildren().addAll(
                        cascadeLoadTreeNodes(cascadeChildParam, treeNode));//递归遍历获取子节点
                treeNode.setIsParent(!treeNode.getChildren().isEmpty());
            }
            if (StringUtils.isNotBlank(reqParams.getParentColumn()) && reqParams.getAsync()) {
                //异步拉取的情况下，只需要判断是否有子节点即可
                treeNode.setIsParent(loadCount(cascadeChildParam) > 0);
            }
            treeNodes.add(treeNode);
        }

        return treeNodes;
    }

    private DataStoreData loadData(TreeDataStoreRequestParam reqParams) {
        return cdDataStoreService.loadData(buildDataStoreParams(reqParams));
    }

    private DataStoreParams buildDataStoreParams(TreeDataStoreRequestParam reqParams) {
        DataStoreParams dataStoreParams = new DataStoreParams();
        dataStoreParams.setDataStoreId(reqParams.getDataStoreId());
        dataStoreParams.setPagingInfo(null);
        dataStoreParams.setParams(reqParams.getParams());

        if (reqParams.getAsync() && StringUtils.isNotBlank(reqParams.getParentColumn())) {//父级查询条件
            //查询指定父级值的节点
            Condition parentEqCondition = new Condition(reqParams.getParentColumn(),
                    reqParams.getParentColumnValue(),
                    CriterionOperator.eq);
            if (StringUtils.isBlank(reqParams.getParentColumnValue())) {
                parentEqCondition = new Condition();
                parentEqCondition.setType(CriterionOperator.or.getType());
                Condition isNullCondition = new Condition(reqParams.getParentColumn(), null, CriterionOperator.ISNULL);
                Condition isEmptyCondition = new Condition(reqParams.getParentColumn(), StringUtils.EMPTY,
                        CriterionOperator.eq);
                parentEqCondition.setConditions(Lists.newArrayList(isNullCondition, isEmptyCondition));
            }
            dataStoreParams.getCriterions().add(parentEqCondition);
        }
        if (StringUtils.isNotBlank(reqParams.getDefaultCondition())) {//默认查询条件
            Condition sqlConditon = new Condition();
            sqlConditon.setSql(reqParams.getDefaultCondition());
            dataStoreParams.getCriterions().add(sqlConditon);
        }
        return dataStoreParams;
    }

    private long loadCount(TreeDataStoreRequestParam reqParams) {
        return cdDataStoreService.loadCount(buildDataStoreParams(reqParams));
    }

    /**
     * 加载指定树结点的数据
     *
     * @param treeId
     * @param reqParams
     * @return
     */
    public TreeNode loadTreeNodeById(String treeId, final TreeDataStoreRequestParam reqParams) {
        DataStoreParams dataStoreParams = buildDataStoreParams(reqParams);
        dataStoreParams.getCriterions().add(new Condition(reqParams.getUniqueColumn(), treeId, CriterionOperator.eq));
        DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams);
        List<Map<String, Object>> dataList = dataStoreData.getData();
        if (CollectionUtils.size(dataList) == 1) {
            TreeNode treeNode = new TreeNode(dataList.get(0).get(reqParams.getUniqueColumn()).toString(), dataList
                    .get(0).get(reqParams.getDisplayColumn()).toString(), null);
            treeNode.setData(dataList.get(0));
            return treeNode;
        }
        return null;
    }
}
