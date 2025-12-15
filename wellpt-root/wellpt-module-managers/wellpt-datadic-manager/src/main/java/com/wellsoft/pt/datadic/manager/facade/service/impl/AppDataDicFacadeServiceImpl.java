package com.wellsoft.pt.datadic.manager.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.collection.List2GroupMap;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.datadic.manager.facade.service.AppDataDicFacadeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/11    chenq		2019/6/11		Create
 * </pre>
 */
@Service
public class AppDataDicFacadeServiceImpl extends AbstractApiFacade implements
        AppDataDicFacadeService {
    @Resource
    DataDictionaryService dataDictionaryService;

    @Override
    public TreeNode loadAppDataDicNodes(String uuid, String moduleId, Boolean includeRef) {

        TreeNode rootNode = new TreeNode();
        // 从根节点开始获取
        if (TreeNode.ROOT_ID.equals(uuid)) {
            rootNode.setName("数据字典");
            rootNode.setId(TreeNode.ROOT_ID);
            rootNode.setNocheck(true);
        } else {// 获取指定节点的数据
            DataDictionary obj = this.dataDictionaryService.get(uuid);
            rootNode.setName(obj.getName());
            rootNode.setId(obj.getUuid());
            rootNode.setData(obj.getCode());
            rootNode.setNocheck(true);
        }

        Map<String, Object> params = Maps.newHashMap();
//        params.put("parentUuidIsNull", true);
        if (includeRef) {
            params.put("catchRefDataDic", true);
        }
        if (StringUtils.isNotBlank(moduleId)) {
            params.put("moduleId", moduleId);
        }
        params.put("orderBy", " order by t1.seq asc, t1.create_time asc");
        List<DataDictionary> topParentDics = dataDictionaryService.queryModuleDataDics(params);

        params.remove("parentUuidIsNull");
        params.put("parentUuidIsNotNull", true);
        params.remove("moduleId");
        List<DataDictionary> all = dataDictionaryService.queryModuleDataDics(params);//查询全部子数据字典

        // 将所有节点数据按上下级关系分组
        Map<String, List<DataDictionary>> ddMap = new List2GroupMap<DataDictionary>() {
            @Override
            protected String getGroupUuid(DataDictionary dd) {
                if (dd.getIsRef()) {//引用挂载的父级uuid
                    return dd.getParentUuid();
                }
                return dd.getParentUuid();
            }
        }.convert(all);

        for (DataDictionary dataDictionary : topParentDics) {
            TreeNode thisNode = new TreeNode();
            thisNode.setName(dataDictionary.getName());
            thisNode.setId(dataDictionary.getUuid());
            thisNode.setData(dataDictionary.getIsRef());
            rootNode.getChildren().add(thisNode);
            appendChildDataDicNode(thisNode, dataDictionary, ddMap);
            rootNode.setTotal(rootNode.getTotal() + thisNode.getTotal() + 1);
            //cascadeLoadChildDataDicNode(thisNode, moduleId);
        }

        // 移除已挂在子结点的根结点
        List<TreeNode> children = rootNode.getChildren();
        List<TreeNode> newChildren = Lists.newArrayList();
        for (TreeNode treeNode : children) {
            if (!isIncludeInOtherTreeNode(treeNode, children)) {
                newChildren.add(treeNode);
            }
        }
        rootNode.setChildren(newChildren);

        //buildDataDictionary2TreeNode(rootNode, topParentDics, moduleId);
        return rootNode;
    }

    /**
     * @param treeNode
     * @param children
     * @return
     */
    private boolean isIncludeInOtherTreeNode(TreeNode treeNode, List<TreeNode> children) {
        for (TreeNode childTree : children) {
            if (treeNode == childTree) {
                continue;
            }
            if (TreeUtils.getTreeNode(childTree, treeNode.getId()) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TreeNode loadAppDataDicNodesByPiUuid(String uuid, String piUUid, Boolean includeRef) {
        return null;
    }

    private void appendChildDataDicNode(TreeNode thisNode, DataDictionary dataDictionary,
                                        Map<String, List<DataDictionary>> ddMap) {
        String key = thisNode.getId();
        List<DataDictionary> ddList = ddMap.get(key);
        if (ddList == null) {
            return;
        }
        for (DataDictionary dd : ddList) {
            TreeNode child = new TreeNode();
            child.setId(dd.getUuid());
            child.setName(dd.getName());
            child.setData(dataDictionary.getIsRef() ? true : dd.getIsRef());
            dd.setIsRef((Boolean) child.getData());//引用传递
            thisNode.getChildren().add(child);
            appendChildDataDicNode(child, dd, ddMap);
            thisNode.setTotal(thisNode.getTotal() + child.getTotal() + 1);
        }
    }

    private void cascadeLoadChildDataDicNode(TreeNode parentNode, String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentUuid", parentNode.getId());
        params.put("orderBy", " order by t1.seq asc, t1.create_time asc");
        params.put("catchRefDataDic", true);
        List<DataDictionary> dataDictionaries = this.dataDictionaryService.queryModuleDataDics(
                params);
        if (dataDictionaries.isEmpty()) {
            return;
        }
        buildDataDictionary2TreeNode(parentNode, dataDictionaries, moduleId);

    }

    private void buildDataDictionary2TreeNode(TreeNode node,
                                              List<DataDictionary> dataDictionaries,
                                              String moduleId) {
        for (DataDictionary dataDictionary : dataDictionaries) {
            TreeNode thisNode = new TreeNode();
            thisNode.setName(dataDictionary.getName());
            thisNode.setId(dataDictionary.getUuid());
            thisNode.setData(dataDictionary.getCode());
            thisNode.setIconStyle(moduleId.equals(dataDictionary.getModuleId()) ? "" : "");//引用其他模块的
            node.getChildren().add(thisNode);
            cascadeLoadChildDataDicNode(thisNode, moduleId);
        }
    }


}
