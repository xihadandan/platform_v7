package com.wellsoft.pt.org.adsycn.service;

import com.wellsoft.pt.org.adsycn.vo.ADGroup;
import com.wellsoft.pt.org.entity.Department;

/**
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-11-21.1  zhengky	2014-11-21	  Create
 * </pre>
 * @date 2014-11-21
 */
public interface ADGroupService {

    /**
     * 添加安全组/通讯组
     *
     * @param adGroup
     */
    public void add(ADGroup adGroup);

    /**
     * 删除安全组(子节点为空才能删除)
     *
     * @param adGroup
     * @param
     */
    public void delete(ADGroup adGroup);

    /**
     * 校验安全组/通讯组是否存在AD中
     *
     * @param addept
     * @return
     */
    public boolean checkIsExist(ADGroup addept);

    public boolean checkIsExistByPath(String deptPath);

    public void renameDn(ADGroup oldadGroup, ADGroup newadGroup);

    /**
     * 将成员添加到组
     *
     * @param memberDn
     * @param groupDn
     */
    public void addMemberToGroup(String memberDn, String groupDn);

    /**
     * 从组中删除成员
     *
     * @param memberDn
     * @param groupDn
     */
    public void removerMemberFromGroup(String memberDn, String groupDn);

    /**
     * 通过组实体获得DN
     *
     * @param adGroup
     * @return
     */
    public String getDnByAdGroup(ADGroup adGroup);

    /**
     * author liyb
     * date    2015.01.13
     * 将本部门的通讯组/安全组，加入上级的通讯组安全组中
     *
     * @param parentDept
     * @param groupDn
     * @param num
     */
    public void addInfoToGroup(Department parentDept, String groupDn, int num);

    /**
     * 通过部门获取它里面的通讯组/安全组的路径
     *
     * @param deptChild
     * @param num
     * @return
     */
    public String getDeptDn(Department deptChild, int num);

    /**
     * 在父部门组中删除它
     *
     * @param parentDn
     * @param groupDn
     */
    public void removerInfoFromGroup(String parentDn, String groupDn, int num);

}
