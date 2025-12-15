package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.common.pinyin.entity.Pinyin;
import com.wellsoft.pt.common.pinyin.service.PinyinService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
@Service
@Transactional
public class EmployeeServiceImpl extends BaseServiceImpl implements EmployeeService {
    private static final String EMPLOYEE_ID_PATTERN = "E0000000000";
    private Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    @Autowired
    private com.wellsoft.pt.common.generator.service.IdGeneratorService idGeneratorService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentEmployeeJobService departmentEmployeeJobService;

    @Autowired
    private PinyinService pinyinService;

    @Autowired
    private JobService jobService;

    @Autowired
    private EmployeeJobService employeeJobService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#get(java.lang.String)
     */
    @Override
    public Employee get(String uuid) {
        return this.dao.get(Employee.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getByIdNumber(java.lang.String)
     */
    @Override
    public Employee getByIdNumber(String idNumber) {
        Employee example = new Employee();
        example.setIdNumber(idNumber);
        List<Employee> employees = this.dao.findByExample(example);
        if (employees.size() == 1) {
            return employees.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getAll()
     */
    @Override
    public List<Employee> getAll() {
        return this.dao.getAll(Employee.class);
    }

    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public Employee getById(String id) {
        return this.dao.findUniqueBy(Employee.class, "id", id);
    }

    /**
     * 获取用户的所有用户拼音实体
     *
     * @param employee
     * @return
     */
    private Set<Pinyin> getEmployeePinyin(Employee employee) {
        Set<String> pinyins = new HashSet<String>();
        String employeeUuid = employee.getUuid();
        String employeeName = employee.getName();
		/*	if (StringUtils.isEmpty(PinyinUtil.getPinYin(employeeName))) {
				throw new RuntimeException("工号" + employee.getEmployeeNumber() + "姓名不能为空");
			}*/
        System.out.println("工号" + employee.getEmployeeNumber() + "------------------------");
        pinyins.add(PinyinUtil.getPinYin(employeeName));
        pinyins.add(PinyinUtil.getPinYinHeadChar(employeeName));

        Set<Pinyin> employeePinyins = new HashSet<Pinyin>();
        for (String pinyin : pinyins) {
            Pinyin employeePinyin = new Pinyin();
            employeePinyin.setType(Employee.class.getSimpleName());
            employeePinyin.setEntityUuid(employeeUuid);
            employeePinyin.setPinyin(pinyin);
            employeePinyins.add(employeePinyin);
        }
        return employeePinyins;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#deleteById(java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteById(String id) {
        Employee employee = this.getById(id);

        // 5、删除用户拼音信息
        pinyinService.deleteByEntityUuid(employee.getUuid());

        this.dao.delete(employee);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#deleteByIds(java.util.Collection)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteByIds(Collection<String> ids) {
        for (String id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 根据用户UUID获取用户所在部门的顶级部门
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getTopDepartment(java.lang.String)
     */
    @Override
    public Department getTopDepartment(String uuid) {
        DepartmentEmployeeJob departmentEmployee = departmentEmployeeJobService.getMajor(uuid);
        Department department = null;
        if (departmentEmployee != null) {
            department = departmentService.getTopDepartment(departmentEmployee.getDepartment().getUuid());
        }
        return department;
    }

    /**
     * 根据用户ID获取用户所属的部门ID
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getDepartmentIdsByEmployeeId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getDepartmentIdsByEmployeeId(String employeeId) {
        List<String> departmentIds = new ArrayList<String>();
        Employee employee = this.getById(employeeId);
        Set<DepartmentEmployeeJob> departmentEmployeeJobs = employee.getDepartmentEmployees();
        for (DepartmentEmployeeJob departmentEmployeeJob : departmentEmployeeJobs) {
            departmentIds.add(departmentEmployeeJob.getDepartment().getId());
        }
        return departmentIds;
    }

    /**
     * @param employeeId
     * @return
     */
    @Override
    public Department getDepartmentByEmployeeId(String employeeId) {
        Employee employee = this.getById(employeeId);
        if (employee == null) {
            return null;
        }
        Set<DepartmentEmployeeJob> departmentEmployeeJobs = employee.getDepartmentEmployees();
        if (departmentEmployeeJobs.isEmpty()) {
            return null;
        }
        return departmentEmployeeJobs.toArray(new DepartmentEmployeeJob[0])[0].getDepartment();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getParentDepartmentIdByEmployeeId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public String getParentDepartmentIdByEmployeeId(String employeeId) {
        Employee employee = this.getById(employeeId);
        // 获取用户所属部门信息
        Set<DepartmentEmployeeJob> departmentEmployeeJobs = employee.getDepartmentEmployees();
        if (departmentEmployeeJobs.isEmpty()) {
            return null;
        }
        // 获取上级部门
        DepartmentEmployeeJob departmentEmployeeJob = departmentEmployeeJobs.iterator().next();
        Department department = departmentEmployeeJob.getDepartment();
        if (department != null) {
            return department.getId();
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getParentDepartmentIdByEmployeeId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<Department> getParentDepartmentsByEmployeeId(String employeeId) {
        List<Department> departments = new ArrayList<Department>();
        Employee employee = this.getById(employeeId);
        // 获取用户所属部门信息
        Set<DepartmentEmployeeJob> departmentEmployeeJobs = employee.getDepartmentEmployees();
        if (departmentEmployeeJobs.isEmpty()) {
            return departments;
        }
        // 获取上级部门
        Iterator<DepartmentEmployeeJob> it = departmentEmployeeJobs.iterator();
        while (it.hasNext()) {
            Department department = it.next().getDepartment();
            Department target = new Department();
            BeanUtils.copyProperties(department, target);
            departments.add(target);
        }
        return departments;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getRootDepartmentIdByEmployeeId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public String getRootDepartmentIdByEmployeeId(String employeeId) {
        Employee employee = this.getById(employeeId);
        // 获取用户所属部门信息
        Set<DepartmentEmployeeJob> departmentEmployeeJobs = employee.getDepartmentEmployees();
        if (departmentEmployeeJobs.isEmpty()) {
            return null;
        }
        // 获取上级部门
        DepartmentEmployeeJob departmentEmployeeJob = departmentEmployeeJobs.iterator().next();
        Department department = departmentEmployeeJob.getDepartment();
        if (department != null) {
            Department parent = department;
            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
            return parent.getId();
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getByIds(java.util.Collection)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<Employee> getByIds(Collection<String> ids) {
        List<Employee> employees = new ArrayList<Employee>();
        for (String id : ids) {
            Employee employeeModel = this.getById(id);
            if (employeeModel == null) {
                // employees.add(employeeModel);
            } else {
                Employee employee = new Employee();
                BeanUtils.copyProperties(employeeModel, employee);
                employees.add(employee);
            }
        }
        return employees;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#queryByLoginName(java.lang.String)
     */
    @Override
    public List<Employee> queryByLoginName(String loginName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("loginName", loginName);
        values.put("employeeName", loginName);
        values.put("pinyin", loginName);
        List<Employee> dataList = this.nativeDao.namedQuery("employeePinyinQuery", values, Employee.class,
                new PagingInfo(1, Integer.MAX_VALUE));
        return BeanUtils.convertCollection(dataList, Employee.class);
    }

    /**
     * 更新用户和职位的关系.
     *
     * @param list
     * @param employeeJobList
     * @return
     */
    @Override
    public HashMap<String, Object> saveEmployeeJobRelactionFromList(List employeeList, List employeeJobList) {
        HashMap<String, Object> rsmap = new HashMap<String, Object>();
        int successcount = 0;
        int totalcount = employeeJobList.size();
        int nullAccount = 0;

        // 校验用户对应的职位信息是否存在职位表中.
        // checkImportEmployeeAndJobRelaction(employeeJobList);

        // 通过主职位的部门全路径和employeeid确定唯一性
        HashMap<String, String> employeeMainDeptPathMap = new HashMap<String, String>();
        HashMap<String, Set<String>> employeeOtherDeptPathMap = new HashMap<String, Set<String>>();

        // employee jobmap
        HashMap<String, String> employeeMajorJobhMap = new HashMap<String, String>();
        HashMap<String, Set<String>> employeeOtherJobhMap = new HashMap<String, Set<String>>();
        HashMap<String, String> empnumbermp = new HashMap<String, String>();
        if (employeeJobList != null && !employeeJobList.isEmpty()) {
            for (Object o : employeeJobList) {
                EmployeeJob employeejob = (EmployeeJob) o;
                String jobName = employeejob.getJobName();
                // 帐号为空的就不导入了
                if (StringUtils.isEmpty(employeejob.getEmployeeNumber())) {
                    continue;
                }
                String employeeLoginName = employeejob.getEmployeeNumber();
                String deptPath = jobName.substring(0, jobName.lastIndexOf("/"));
                empnumbermp.put(employeeLoginName, employeeLoginName);
                // 主岗
                if (employeejob.getIsMajor() == true) {
                    if (employeeMainDeptPathMap.get(employeeLoginName) != null) {
                        throw new RuntimeException("用户:" + employeeLoginName + "不能有两个主岗信息!");
                    }
                    employeeMainDeptPathMap.put(employeeLoginName, deptPath);
                    // 构建用户，主职位map
                    employeeMajorJobhMap.put(employeeLoginName, jobName);

                    // 其他岗位 其他部门
                } else {
                    if (employeeOtherDeptPathMap.get(employeeLoginName) == null) {
                        HashSet<String> otherdeptpath = new HashSet<String>();
                        otherdeptpath.add(deptPath);
                        employeeOtherDeptPathMap.put(employeeLoginName, otherdeptpath);
                    } else {
                        employeeOtherDeptPathMap.get(employeeLoginName).add(deptPath);
                    }

                    // 构建其他职位map
                    if (employeeOtherJobhMap.get(employeeLoginName) == null) {
                        HashSet<String> otherjobname = new HashSet<String>();
                        otherjobname.add(jobName);
                        employeeOtherJobhMap.put(employeeLoginName, otherjobname);
                    } else {
                        employeeOtherJobhMap.get(employeeLoginName).add(jobName);
                    }
                }
            }
        }

		/*		String checkmsg = "";
				if (employeeList != null && !employeeList.isEmpty()) {
					for (Object o : employeeList) {
						Employee user1 = (Employee) o;
						if (StringUtils.isEmpty(user1.getEmployeeNumber())) {
							continue;
						}
						//判断用户登录名是否存在于职位关联表中
						if (!empnumbermp.containsKey(user1.getEmployeeNumber())) {
							checkmsg = checkmsg + "\n" + user1.getEmployeeNumber();
						}

						String departmentName = employeeMainDeptPathMap.get(user1.getEmployeeNumber());
						if (StringUtils.isEmpty(departmentName)) {
							checkmsg = checkmsg + "\n" + "人员" + user1.getEmployeeNumber() + "找不到对应的主职位信息!";
						}
					}
				}

				if (checkmsg != "") {
					throw new RuntimeException(checkmsg);
				}*/

        if (employeeList != null && !employeeList.isEmpty()) {
            for (Object o : employeeList) {
                Employee employee1 = (Employee) o;
                if (StringUtils.isEmpty(employee1.getEmployeeNumber())) {
                    nullAccount = nullAccount + 1;
                    continue;
                }

                Employee employee = getByEmployeeNumber(employee1.getEmployeeNumber());

                // 设置主部门
                String employeeNumber = employee.getEmployeeNumber();
                String departmentName = employeeMainDeptPathMap.get(employeeNumber);
                ArrayList<String> departmentNames = new ArrayList<String>();
                if (StringUtils.isEmpty(departmentName)) {
                    throw new RuntimeException("用户" + employeeNumber + "找不到对应的职位信息!");
                }

                // 更新部门关系
                departmentEmployeeJobService.deleteByEmployee(employee.getUuid());
                // 判断部门名称全路径是否已经存在，如果存在则建立关联关系
                if (StringUtils.isNotBlank(departmentName)) {
                    createDepartmentEmployeeRelation(employee, departmentName, true);
                    departmentNames.add(departmentName);
                }

                this.dao.save(employee);

                // 设置其他部门
                Set<String> otherdepartmentNameSet = employeeOtherDeptPathMap.get(employeeNumber);
                // 判断部门名称全路径是否已经存在，如果存在则建立关联关系
                // 否则新增部门
                if (otherdepartmentNameSet != null) {
                    for (String otherdeptpath : otherdepartmentNameSet) {
                        if (StringUtils.isNotBlank(otherdeptpath)) {
                            if (departmentName.equals(otherdeptpath)) {
                                continue;
                            }
                            createDepartmentEmployeeRelation(employee, otherdeptpath, false);
                            departmentNames.add(otherdeptpath);
                        }
                    }

                }

                String deptnames = "";
                for (int i = 0; i < departmentNames.size(); i++) {
                    if (i == departmentNames.size() - 1) {
                        deptnames = deptnames + departmentNames.get(i);
                    } else {
                        deptnames = deptnames + departmentNames.get(i) + ";";
                    }

                }
                employee.setDepartmentName(departmentName);

                // 更新岗位关系
                employeeJobService.deleteByEmployee(employee.getUuid());
                // 主岗位 20140817 add
                String jobName = employeeMajorJobhMap.get(employeeNumber);
                createEmployeeJobRelation(employee, jobName, true);
                successcount = successcount + 1;
                employee.setMajorJobName(jobName);

                // 设置其他岗位
                Set<String> otherJobNameSet = employeeOtherJobhMap.get(employeeNumber);
                int otherJobCount = 0;
                String otherJobNames = "";
                if (otherJobNameSet != null) {
                    for (String otherJobName : otherJobNameSet) {
                        EmployeeJob employeejob = createEmployeeJobRelation(employee, otherJobName, false);
                        successcount = successcount + 1;
                        Job job = employeejob.getJob();
                        if (otherJobCount == otherJobNameSet.size() - 1) {
                            otherJobNames = otherJobNames + job.getDepartmentName() + "/" + job.getName();
                        } else {
                            otherJobNames = otherJobNames + job.getDepartmentName() + "/" + job.getName() + ";";
                        }
                        otherJobCount++;
                    }
                    employee.setOtherJobNames(otherJobNames);
                }
            }
        }
        String msg = "选择的用户职位数据共【" + totalcount + "】行，帐号为空数据共【" + nullAccount + "】行,成功导入【" + successcount + "】行";
        System.out.println(msg);
        rsmap.put("msg", msg);
        return rsmap;
    }

    /**
     * 获取用户密码加密的随机盐
     *
     * @param user
     * @return
     */
    private Object getSalt(Employee employee) {
        return employee.getLoginName();
    }

    @Override
    public HashMap<String, Object> saveEmployeeFromList(List list) {
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        String tenantId = userDetail.getTenantId();
        int duplicatecount = 0;
        int successcount = 0;
        int totalcount = list.size();
        HashMap<String, Object> rsmap = new HashMap<String, Object>();
        HashMap<String, String> employeeleadersMap = new HashMap<String, String>();// 用户名，上级领导名字

        HashSet<String> numberset = new HashSet<String>();
        for (Object o : list) {
            Employee employee = (Employee) o;
            // 空行
            if (StringUtils.isEmpty(employee.getEmployeeNumber()) && StringUtils.isEmpty(employee.getName())) {
                continue;
            }
            numberset.add(employee.getEmployeeNumber());
        }

        String duplickmsg = "";
        for (String strnumber : numberset) {
            int count = 0;
            for (Object o : list) {
                Employee employee = (Employee) o;
                if (employee.getEmployeeNumber().equals(strnumber)) {
                    count = count + 1;
                }
            }
            if (count > 1) {
                duplickmsg = duplickmsg + "\n" + strnumber;
            }
        }
        // 工号重复校验
        if (!duplickmsg.equals("")) {
            throw new RuntimeException(duplickmsg);
        }

        if (list != null && !list.isEmpty()) {
            for (Object o : list) {
                Employee employee = (Employee) o;

                if (StringUtils.isEmpty(employee.getEmployeeNumber())) {
                    continue;
                }

                employee.setUuid(null);
                // 加密密码
                if (!StringUtils.isEmpty(employee.getLoginName())) {
                    employee.setPassword(passwordEncoder.encodePassword(employee.getPassword(), getSalt(employee)));
                }
                employee.setEnabled(true);

                if (!StringUtils.isEmpty(employee.getLeaderNames())) {
                    employeeleadersMap.put(employee.getEmployeeNumber(), employee.getLeaderNames());
                }

                employee.setExternalId(employee.getEmployeeNumber());

                Employee employeeUpdate = getByEmployeeNumber(employee.getEmployeeNumber());
                if (employeeUpdate != null) {
                    employeeUpdate.setPrincipalCompany(employee.getPrincipalCompany());
                    employeeUpdate.setLoginName(employee.getLoginName());
                    employeeUpdate.setName(employee.getName());// 名字也更新
                    employeeUpdate.setPassword(employee.getPassword());
                    employeeUpdate.setSex(employee.getSex());

                    employeeUpdate.setMobilePhone(employee.getMobilePhone());
                    employeeUpdate.setOtherMobilePhone(employee.getOtherMobilePhone());
                    employeeUpdate.setMainEmail(employee.getMainEmail());
                    employeeUpdate.setHomePhone(employee.getHomePhone());
                    employeeUpdate.setOtherEmail(employee.getOtherEmail());
                    employeeUpdate.setFax(employee.getFax());
                    employeeUpdate.setOfficePhone(employee.getOfficePhone());
                    employeeUpdate.setEnglishName(employee.getEnglishName());

                    this.dao.save(employeeUpdate);
                    // 拼音也是需要保存的.
                    pinyinService.deleteByEntityUuid(employeeUpdate.getUuid());
                    try {
                        Set<Pinyin> employeePinyins = getEmployeePinyin(employeeUpdate);
                        for (Pinyin employeePinyin : employeePinyins) {
                            pinyinService.save(employeePinyin);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    duplicatecount = duplicatecount + 1;
                } else {
                    // TODO 用户ID限定11位（U+4位租户ID+6位用户ID）
                    String id = idGeneratorService.generate(Employee.class, EMPLOYEE_ID_PATTERN);
                    employee.setId(id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11));
                    employee.setTenantId(SpringSecurityUtils.getCurrentTenantId());
                    this.dao.save(employee);
                    try {
                        Set<Pinyin> employeePinyins = getEmployeePinyin(employee);
                        for (Pinyin employeePinyin : employeePinyins) {
                            pinyinService.save(employeePinyin);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }

                    successcount = successcount + 1;
                }
            }
        }
        String msg = "选择的人员数据共【" + totalcount + "】行,成功更新【" + duplicatecount + "】行，成功导入【" + successcount + "】行";
        System.out.println(msg);
        rsmap.put("msg", msg);
        rsmap.put("employeeleadersMap", employeeleadersMap);
        return rsmap;

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#findByExample(com.wellsoft.pt.org.entity.Employee)
     */
    @Override
    public List<Employee> findByExample(Employee example) {
        return this.dao.findByExample(example);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#createEmployee(com.wellsoft.pt.org.entity.Employee)
     */
    @Override
    public void createEmployee(Employee entity) {
        String id = idGeneratorService.generate(Employee.class, EMPLOYEE_ID_PATTERN);

        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
        // TODO 用户ID限定11位（U+4位租户ID+6位用户ID）
        entity.setId(id);
        entity.setTenantId(tenantId);
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getMobilesByEmployeeIds(java.util.List)
     */
    @Override
    public List<QueryItem> getMobilesByEmployeeIds(List<String> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return new ArrayList<QueryItem>(0);
        }
        String hql = "select o.mobilePhone as mobilePhone, o.id as id from Employee o where o.id in(:employeeIds)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("employeeIds", employeeIds);
        List<QueryItem> queryItems = this.dao.query(hql, values, QueryItem.class);
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.EmployeeService#getMobilesForSmsByEmployeeIds(java.util.List)
     */
    @Override
    public List<QueryItem> getMobilesForSmsByEmployeeIds(List<String> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return new ArrayList<QueryItem>(0);
        }
        String hql = "select o.mobilePhone as mobilePhone, o.id as id, o.employeeName as employeeName from Employee o where o.id in(:employeeIds) "
                + " and exists (select uuid from EmployeeProperty up where o.uuid = up.employeeUuid and up.name = 'receive.sms.message' "
                + " and up.value = 'true')";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("employeeIds", employeeIds);
        List<QueryItem> queryItems = this.dao.query(hql, values, QueryItem.class);
        return queryItems;
    }

    /**
     * 创建用户和职位关联Vo
     *
     * @param employee
     * @param jobName
     * @return EmployeeJob
     */
    private EmployeeJob createEmployeeJobRelation(Employee employee, String jobName, boolean isMajor) {
        if (StringUtils.isEmpty(jobName)) {
            throw new RuntimeException("用户" + employee.getIdNumber() + "职位不能为空!");
        }
        String realJobName = jobName.substring(jobName.lastIndexOf("/") + 1, jobName.length());
        String realDeptPath = jobName.substring(0, jobName.lastIndexOf("/"));
        List<Job> jobList = jobService.getJobByNameAndDeptName(realJobName, realDeptPath);
        if (jobList == null || jobList.size() == 0) {
            throw new RuntimeException("用户:" + employee.getIdNumber() + "职位：【" + realJobName + "】+部门:【" + realDeptPath
                    + "】找不到相应的职位记录！");
        }

        Job job = jobList.get(0);

        EmployeeJob employeeJob = new EmployeeJob();
        employeeJob.setEmployee(employee);
        employeeJob.setJob(job);
        employeeJob.setIsMajor(isMajor);
        this.dao.save(employeeJob);
        return employeeJob;
    }

    /**
     * 创建用户和部门关联VO
     *
     * @param employee
     * @param detppath
     */
    private void createDepartmentEmployeeRelation(Employee employee, String detppath, boolean isMajor) {
        if (StringUtils.isNotBlank(detppath)) {
            // 立达信绿色照明股份有限公司导入测试/CFL产品线/CFL长泰分部/PMC部/长泰计划课 处理
            List<Department> deptList = this.dao.findBy(Department.class, "path", detppath);
            if (deptList.isEmpty()) {
                throw new RuntimeException("用户【" + employee.getIdNumber() + "】" + "找不到对应的部门【" + detppath + "】");
            }
            // 否则直接建立关联关系
            Department dept = deptList.get(0);
            // 保存部门-用户关联关系
            DepartmentEmployeeJob departmentEmployee = new DepartmentEmployeeJob();
            departmentEmployee.setEmployee(employee);
            departmentEmployee.setDepartment(dept);
            departmentEmployee.setIsMajor(isMajor);
            this.dao.save(departmentEmployee);
        }
    }

    @Override
    public Job getMajorJobByEmployeeId(String id) {
        Employee employee = getById(id);
        return getMajorJobByEmployee(employee);
    }

    @Override
    public List<Job> getOtherJobsByEmployeeId(String id) {
        Employee employee = getById(id);
        return getOtherJobsByEmployee(employee);
    }

    private Job getMajorJobByEmployee(Employee employee) {
        List<EmployeeJob> jobls = employeeJobService.getMajorJobs(employee.getUuid());
        if (jobls != null && jobls.size() > 0) {
            return jobService.getByUuId(jobls.get(0).getJob().getUuid());
        }
        return null;
    }

    private List<Job> getOtherJobsByEmployee(Employee employee) {
        List<EmployeeJob> jobls = employeeJobService.getOtherJobs(employee.getUuid());
        List<Job> jobs = Lists.newArrayList();
        if (jobls != null && jobls.size() > 0) {
            for (int i = 0; i < jobls.size(); i++) {
                jobs.add(jobService.getByUuId(jobls.get(i).getJob().getUuid()));
            }
        }
        return jobs;
    }

    /**
     * 校验用户和职位关系
     *
     * @return
     */
    public void checkImportEmployeeAndJobRelaction(List<Map<String, Object>> employeeJobList) {
        String errorMsg = "";
        if (employeeJobList != null && !employeeJobList.isEmpty()) {
            for (Object o : employeeJobList) {
                EmployeeJob employeejob = (EmployeeJob) o;
                String jobName = employeejob.getJobName();
                // 帐号为空的就不导入了
                if (StringUtils.isEmpty(employeejob.getEmployeeNumber())) {
                    continue;
                }
                String deptPath = jobName.substring(0, jobName.lastIndexOf("/"));
                String realJobName = jobName.substring(jobName.lastIndexOf("/") + 1, jobName.length());

                // 判断职位是否存在 不存在就报错了..
                List<Job> jobls = jobService.getJobByNameAndDeptName(realJobName, deptPath);
                if (jobls == null || jobls.size() == 0) {

                    errorMsg = errorMsg + "" + "用户:" + employeejob.getEmployeeNumber() + "职位：【" + realJobName
                            + "】+部门:【" + deptPath + "】找不到相应的职位记录！";
                }
            }
        }
        if (!StringUtils.isEmpty(errorMsg)) {
            throw new RuntimeException(errorMsg);
        }
    }

    @Override
    public Employee getByEmployeeNumber(String employeeNumber) {
        Employee example = new Employee();
        example.setEmployeeNumber(employeeNumber);
        List<Employee> employees = dao.findByExample(example);
        if (employees.size() == 1) {
            return employees.get(0);
        }
        return null;
    }

    @Override
    public List<Employee> getEmployeesByName(String name) {
        List<Employee> employee = dao.findBy(Employee.class, "name", name);
        return employee;
    }

    @Override
    public void save(Employee employee) {
        // TODO Auto-generated method stub
        this.dao.save(employee);
    }

}
