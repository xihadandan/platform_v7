/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialLogAsynParms;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialogParams;
import com.wellsoft.pt.multi.org.bean.OrgTreeNode;
import com.wellsoft.pt.multi.org.bean.UserNodePy;
import com.wellsoft.pt.multi.org.entity.MultiOrgType;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月18日.1	zyguo		2017年12月18日		Create
 * </pre>
 * @date 2017年12月18日
 */
public interface MultiOrgTreeDialogService extends BaseService {
    /**
     * 按类型获取组织弹出框的数据，供给 MultiUnitTreeDialog.js里面使用
     *
     * @param type
     * @param params
     * @return
     */
    public List<TreeNode> queryUnitTreeDialogDataByType(String type, OrgTreeDialogParams params);

    /**
     * 异步加载 子节点
     *
     * @param type
     * @param params
     * @return
     */
    public List<OrgNode> children(String type, OrgTreeDialLogAsynParms params);

    /**
     * 搜索
     *
     * @param type
     * @param params
     * @return
     */
    public List<OrgNode> search(String type, OrgTreeDialLogAsynParms params);

    /**
     * 全部加载
     *
     * @param type
     * @param params
     * @return
     */
    public List<OrgNode> full(String type, OrgTreeDialLogAsynParms params);

    /**
     * 所有用户
     *
     * @param type
     * @param params
     * @return
     */
    public UserNodePy allUserSearch(String type, OrgTreeDialLogAsynParms params);

    /**
     * 如何描述该方法
     *
     * @param eleIdPath
     * @param orgVersionId
     * @param isNeedUser
     * @param isInMyUnit
     * @return
     */
    OrgTreeNode getTreeNodeByVerisonAndEleIdPath(String eleIdPath, String orgVersionId, int isNeedUser,
                                                 boolean isInMyUnit);

    public abstract List<MultiOrgType> getCurrentUnitOrgType();

    void eliminateDuplicateNames(Map<String, List<String>> eleIdStrsMap, int i);

    /**
     * 智能名称
     *
     * @param nameDisplayMethod
     * @param nodeIds
     * @return
     */
    Map<String, OrgNode> smartName(int nameDisplayMethod, List<String> nodeIds, List<String> nodeNames);

    Map<String, OrgNode> smartName(List<String> nodeIds, List<String> nodeNames);

    /**
     * 全名称路径
     *
     * @param orgVersionId
     * @param eleId
     * @return
     */
    String fullNamePath(String orgVersionId, String eleId);
}
