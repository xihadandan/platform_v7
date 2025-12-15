/*
 * @(#)27 Feb 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.support.treecomponent;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.AbstractAsyncTreeComponentDataProvider;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentRequestParam;
import com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeType;
import com.wellsoft.pt.org.unit.service.UnitTreeService;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 27 Feb 2017.1	Xiem		27 Feb 2017		Create
 * </pre>
 * @date 27 Feb 2017
 */
@Deprecated
public abstract class AbstractUnitTreeDataProvider extends AbstractAsyncTreeComponentDataProvider {
    protected static final String ATTRIBUTE_CHILDREN = "children";
    protected static final String ATTRIBUTE_ISPARENT = "isParent";
    @Autowired
    private UnitTreeService unitTreeService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#getNodeTypes()
     */
    @Override
    public List<TreeType> getNodeTypes() {
        List<TreeType> types = new ArrayList<TreeType>();
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_DEP, "部门"));
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_USER, "用户"));
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_GROUP, "群组"));
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_ORG_UNIT, "ORG单位"));
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_CATEGORY, "分类"));
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_JOB, "岗位"));
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_DUTY, "职务"));
        types.add(TreeType.createTreeType(ATTRIBUTE_TYPE_UNIT, "单位"));
        return types;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataProvider#loadTreeData(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public List<TreeNode> loadTreeData(TreeComponentRequestParam param) {
        Document unitxml = unitTreeService.parserUnit(getOrgType(), getAll(), getLogin(), getFilterCondition(),
                getFilterDisplayList());
        Element root = unitxml.getRootElement();
        return element2Json(root.elementIterator(), false);
    }

    private List<TreeNode> element2Json(Iterator<?> elementIt, boolean showPath) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        while (elementIt.hasNext()) {
            Element element = (Element) elementIt.next();
            Iterator<?> it = element.attributeIterator();
            Map<String, Object> data = new HashMap<String, Object>();
            TreeNode treeNode = new TreeNode();
            while (it.hasNext()) {
                Attribute attribute = (Attribute) it.next();
                String attributeName = attribute.getName();
                String attributeValue = attribute.getValue();
                data.put(attributeName, attributeValue);
                // ID
                if (ATTRIBUTE_ID.equals(attributeName)) {
                    treeNode.setId(attributeValue);
                }
                // 名称
                if (ATTRIBUTE_NAME.equals(attributeName)) {
                    treeNode.setName(attributeValue);
                }
                // 类型
                if (ATTRIBUTE_TYPE.equals(attributeName)) {
                    treeNode.setType(attributeValue);
                }
                // 路径
                if (ATTRIBUTE_PATH.equals(attributeName)) {
                    treeNode.setPath(attributeValue);
                }
                // 是否子结点
                if (ATTRIBUTE_ISLEAF.equals(attributeName)) {
                    String isLeaf = attributeValue;
                    treeNode.setIsParent(FALSE.equalsIgnoreCase(isLeaf));
                }
            }
            // 显示路径
            if (showPath) {
                treeNode.setName(treeNode.getPath());
            }
            // 设置子结点
            if (treeNode.getIsParent()) {
                treeNode.setChildren(element2Json(element.elementIterator(), false));
            }
            // 自定义条件查询的bean
            data.put("customCriterion", "unitTree" + getOrgType() + "Criterion");
            treeNode.setData(data);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataAsyncSupport#asyncLoadTreeData(java.lang.String, java.util.Map)
     */
    @Override
    public List<TreeNode> asyncLoadTreeData(TreeComponentRequestParam params) {
        Document unitxml = unitTreeService.toggleUnit(getOrgType(), params.getParentId(), getAll(), getLogin(),
                getFilterDisplayList());
        Element root = unitxml.getRootElement();
        List<TreeNode> treeNodes = element2Json(root.elementIterator(), false);
        if (treeNodes.size() == 1) {
            return treeNodes.get(0).getChildren();
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.treecomponent.facade.support.TreeComponentDataSearchSupport#search(java.lang.String, java.util.Map)
     */
    @Override
    public List<TreeNode> search(TreeComponentRequestParam params) {
        Document unitxml = unitTreeService.searchXml(getOrgType(), getAll(), getLogin(), params.getSearchText(),
                getFilterCondition(), getFilterDisplayList());
        Element root = unitxml.getRootElement();
        return element2Json(root.elementIterator(), true);
    }

    protected abstract String getOrgType();

    protected String getAll() {
        return "1";
    }

    protected String getLogin() {
        return "0";
    }

    protected String getFilterCondition() {
        return "";
    }

    protected HashMap<String, String> getFilterDisplayList() {
        return new HashMap<String, String>();
    }

    public String getFilterHint() {
        return "";
    }
}
