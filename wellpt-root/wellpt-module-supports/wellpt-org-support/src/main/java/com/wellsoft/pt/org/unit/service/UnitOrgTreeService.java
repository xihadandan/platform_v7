package com.wellsoft.pt.org.unit.service;

import com.wellsoft.context.component.tree.TreeNode;

import java.util.List;

/**
 * Description: 组织树
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月30日.1	Xiem		2016年5月30日		Create
 * </pre>
 * @date 2016年5月30日
 */
public interface UnitOrgTreeService {
    /**
     * 获取管理者权限的组织树时调用
     *
     * @param uuid
     * @return
     */
    List<TreeNode> getOrgEmployeeManageTree(String uuid);

    /**
     * 获取管理者权限的组织树查询时调用
     *
     * @param uuid
     * @return
     */
    List<TreeNode> getOrgEmployeeeManageTreeBySearchName(String searchName);

    /**
     * 获无权限组织树时调用
     *
     * @param uuid
     * @return
     */
    List<TreeNode> getOrgEmployeeTree(String uuid);

    /**
     * 获无权限组织树查询时调用
     *
     * @param searchName
     * @return
     */
    List<TreeNode> getOrgEmployeeeTreeBySearchName(String searchName);
}
