package com.wellsoft.pt.org.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Employee;
import com.wellsoft.pt.org.entity.Job;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 员工service
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-24.1  zhengky	2014-9-24	  Create
 * </pre>
 * @date 2014-9-24
 */
public interface EmployeeService {

    /**
     * 根据UUID获取用户
     *
     * @param uuid
     * @return
     */
    Employee get(String uuid);

    /**
     * 根据用户ID获取用户
     *
     * @param id
     * @return
     */
    Employee getById(String id);

    /**
     * 根据用户身份证获取用户
     *
     * @param idNumber
     * @return
     */
    Employee getByIdNumber(String idNumber);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<Employee> getAll();

    /**
     * 根据用户ID删除用户
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 根据用户ID，批量删除用户
     *
     * @param ids
     */
    void deleteByIds(Collection<String> ids);

    /**
     * 根据用户UUID获取用户所在部门的顶级部门
     *
     * @param uuid
     * @return
     */
    Department getTopDepartment(String uuid);

    /**
     * 根据用户ID获取用户所属的部门ID
     *
     * @param employeeId
     * @return
     */
    List<String> getDepartmentIdsByEmployeeId(String employeeId);

    /**
     * 根据用户ID获取用户所属的主部门
     *
     * @param employeeId
     * @return
     */
    Department getDepartmentByEmployeeId(String employeeId);

    /**
     * 根据用户ID获取用户上级的部门ID
     *
     * @param employeeId
     * @return
     */
    String getParentDepartmentIdByEmployeeId(String employeeId);

    /**
     * 根据用户ID获取用户所有的上级部门
     *
     * @param employeeId
     * @return
     */
    List<Department> getParentDepartmentsByEmployeeId(String employeeId);

    /**
     * 如何描述该方法
     *
     * @param employeeId
     * @return
     */
    String getRootDepartmentIdByEmployeeId(String employeeId);

    /**
     * 根据用户ID列表获取用户列表
     *
     * @param ids
     * @return
     */
    List<Employee> getByIds(Collection<String> ids);

    /**
     * 根据用户ID列表获取名列表
     *
     * @param ids
     * @return
     */
    //	 List<String> getEmployeeNamesByIds(Collection<String> ids);

    /**
     * 根据用户登录名查询用户
     *
     * @param loginName
     * @return
     */
    List<Employee> queryByLoginName(String loginName);

    /**
     * 将用户数据列表保存到用户表中
     *
     * @param list
     */
    HashMap<String, Object> saveEmployeeFromList(List list);

    /**
     * @param employee
     * @return
     */
    List<Employee> findByExample(Employee example);

    void createEmployee(Employee employee);

    /**
     * 根据用户ID列表获取相应的手机号码
     *
     * @param employeeIds
     * @return
     */
    List<QueryItem> getMobilesByEmployeeIds(List<String> employeeIds);

    /**
     * 根据用户ID列表获取相应的手机号码，用于发送短信
     *
     * @param employeeIds
     * @return
     */
    List<QueryItem> getMobilesForSmsByEmployeeIds(List<String> employeeIds);

    /**
     * 保存用户的职位、部门信息
     *
     * @param list
     */
    public HashMap<String, Object> saveEmployeeJobRelactionFromList(List employeeList,
                                                                    List employeeJoblist);

    /**
     * 根据用户Id获得主职位信息
     */
    public Job getMajorJobByEmployeeId(String id);

    /**
     * 根据用户ID获得其他职位信息
     *
     * @return
     */
    public List<Job> getOtherJobsByEmployeeId(String id);

    Employee getByEmployeeNumber(String employeeNumber);

    public List<Employee> getEmployeesByName(String name);

    void save(Employee employee);

}
