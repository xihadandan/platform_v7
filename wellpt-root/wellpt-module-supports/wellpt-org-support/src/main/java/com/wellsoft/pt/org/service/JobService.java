package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.org.bean.JobBean;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.support.UsersGrade;

import java.util.*;

/**
 * Description: 岗位service
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-11.1  zhengky	2014-8-11	  Create
 * </pre>
 * @date 2014-8-11
 */
public interface JobService {

    /**
     * 根据UUID获取岗位
     *
     * @param uuid
     * @return
     */
    Job getByUuid(String uuid);

    /**
     * 根据岗位ID获取岗位
     *
     * @param id
     * @return
     */
    Job getById(String id);

    /**
     * 保存
     * 如何描述该方法
     *
     * @param job
     */
    void save(Job job);

    /**
     * @param uuid
     * @return
     */
    JobBean getBean(String uuid);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    JobBean getBeanById(String id);

    /**
     * 如何描述该方法
     *
     * @param bean
     */
    void saveBean(JobBean bean);

    /**
     * 根据岗位UUID加载角色树，自动选择已有角色
     *
     * @param uuid
     * @return
     */
    TreeNode getRoleTree(String uuid);

    /**
     * 根据岗位UUID加载权限树，选择已有权限
     *
     * @param uuid
     * @return
     */
    TreeNode getPrivilegeTree(String uuid);

    /**
     * 根据岗位UUID加载权限树，展示使用
     *
     * @param uuid
     */
    TreeNode getJobPrivilegeTree(String uuid);

    /**
     * 根据UUID加载岗位角色嵌套树, 包含角色嵌套及权限
     *
     * @param uuid
     * @return
     */
    TreeNode getJobRoleNestedRoleTree(String uuid);

    /**
     * 根据岗位ID删除岗位
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 根据岗位ID，批量删除岗位
     *
     * @param ids
     */
    void deleteByIds(Collection<String> ids);

    /**
     * 根据岗位ID获取岗位所在部门的顶级部门
     *
     * @param uuid
     * @return
     */
    Department getTopDepartment(String id);

    /**
     * 将职位数据列表保存到职位表中(Excel导入)
     *
     * @param list
     */
    HashMap<String, Object> saveJobFromList(List list);

    /**
     * 导入后更新汇报对象
     *
     * @param userRsMp
     */
    public void updateJobLeadersAfterImport(HashMap<String, Object> jobRsMp);

    /**
     * 通过职位ID获得用户
     *
     * @param jobId
     * @return
     */
    List<User> getUsersByJobId(String jobId);

    List<Job> getAll();

    Set<Job> getJobByRoleUuid(String roleUuid);

    /**
     * 通过职务UUID获得职位
     *
     * @param deptUuid
     * @return
     */
    public List<Job> getJobByDutyUuid(String dutyUuid);

    /**
     * 通过部门ID获得岗位
     *
     * @param jobname
     * @param deptname
     * @return
     */
    public List<Job> getJobByDeptUuid(String deptUuid);

    /**
     * 通过部门名称和岗位名称定位岗位
     *
     * @param jobname
     * @param deptname
     * @return
     */
    public List<Job> getJobByNameAndDeptName(String jobname, String deptname);

    /**
     * @param id
     * @return
     */
    public Job getByUuId(String uuid);

    List<Job> namedQuery(String string, Map<String, Object> values, Class<Job> class1, PagingInfo pagingInfo);

    List<Job> namedQuery(String string, Map<String, Object> values, Class<Job> class1);

    /**
     * 对用户根据职级高低进行排序，返回排序后的用户ID列表
     *
     * @param userIds
     * @return
     */
    UsersGrade orderedUserIdsByJobGrade(Collection<String> userIds);

}
