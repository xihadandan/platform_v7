/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service;

import com.wellsoft.context.component.tree.TreeNode;
import org.dom4j.Document;

import java.util.HashMap;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-25.1	zhulh		2012-12-25		Create
 * </pre>
 * @date 2012-12-25
 */
public interface UnitTreeService {
    Document searchXml(String optionType, String all, String login, String searchValue, String filterCondition,
                       HashMap<String, String> filterDisplayList);

    /**
     * @param isShowGroup 显示所有群组
     * @param psType      默认类型
     * @return
     */
    Document parserType(String isShowGroup, String psType);

    /**
     * @param type
     * @param all
     * @param login
     * @param filterCondition   //过滤条件 （使用场景:公共群组支持传群组code进行过滤）
     * @param filterDisplayList //不显示的对应ID集合
     * @return
     */
    Document parserUnit(String type, String all, String login, String filterCondition,
                        HashMap<String, String> filterDisplayList);

    /**
     * @param optionType
     * @param id
     * @param all
     * @param login
     * @return
     */
    Document toggleUnit(String optionType, String id, String all, String login,
                        HashMap<String, String> filterDisplayList);

    /**
     * 获取组织单元根结点
     *
     * @param optionType
     * @param id
     * @param type
     * @param login
     * @return
     */
    Document leafUnit(String optionType, String id, String type, String login, HashMap<String, String> filterDisplayList);

    /**
     * 解析我的单位组织树
     *
     * @param treeId
     * @return TreeNode列表
     */
    List<TreeNode> parseUnitTree(String treeId, HashMap<String, String> filterDisplayList);
}
