package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.common.pinyin.entity.Pinyin;
import com.wellsoft.pt.common.pinyin.service.PinyinService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.bean.JobBean;
import com.wellsoft.pt.org.dao.JobDao;
import com.wellsoft.pt.org.dao.JobPrincipalDao;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.org.support.UserGradeItem;
import com.wellsoft.pt.org.support.UsersGrade;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 岗位实现类
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
@Service
@Transactional
public class JobServiceImpl extends BaseServiceImpl implements JobService {

    private static final String JOB_ID_PATTERN = "J0000000000";
    private static final String GET_JOB_BY_NAME_AND_DEPTNAME = " from Job job where job.name = :jobName  and job.departmentName = :departmentName";
    private static final String GET_JOB_BY_DEPTUUID = " select  j from Job j ,Duty d  where j.duty=d and j.departmentUuid = :departmentUuid order by d.ilevel desc,j.code,j.name";
    private static final String GET_JOB_BY_DUTYUUID = " select  j from Job j ,Duty d  where j.duty=d and d.uuid = :dutyUuid order by d.ilevel desc,j.code,j.name  ";
    @Autowired
    private JobDao jobDao;
    @Autowired
    private com.wellsoft.pt.common.generator.service.IdGeneratorService idGeneratorService;
    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;
    @Autowired
    private RoleFacadeService roleFacadeService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PinyinService pinyinService;
    @Autowired
    private DutyService dutyService;
    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private JobFunctionService jobFunctionService;
    @Autowired
    private UserService userService;
    @Autowired
    private JobPrincipalDao jobPrincipalDao;
    @Autowired
    private DepartmentUserJobService departmentUserJobService;
    @Autowired
    private JobRoleService jobRoleService;
    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Override
    public Job getByUuid(String uuid) {
        return this.getByUuId(uuid);
    }

    @Override
    public void save(Job job) {
        this.dao.save(job);
    }

    @Override
    public JobBean getBean(String uuid) {
        return getBeanById(this.getByUuId(uuid).getId());
    }

    @Override
    public JobBean getBeanById(String id) {
        Job job = this.getById(id);
        JobBean bean = new JobBean();
        BeanUtils.copyProperties(job, bean);

        Department department = departmentService.get(job.getDepartmentUuid());
        bean.setDepartmentId(department.getId());

        // 设置汇报对象tobean
        setJobPrincipalToBean(job, bean);

        // 获得职能
        Set<JobFunction> functions = job.getFunctions();
        StringBuilder functionUuids = new StringBuilder();
        StringBuilder functionNames = new StringBuilder();
        Iterator<JobFunction> itfunction = functions.iterator();
        while (itfunction.hasNext()) {
            JobFunction jobFunction = itfunction.next();
            DataDictionary dataDictionary = dataDictionaryService.get(jobFunction.getFunctionUuid());
            functionUuids.append(dataDictionary.getUuid());
            functionNames.append(dataDictionary.getName());
            if (itfunction.hasNext()) {
                functionUuids.append(Separator.SEMICOLON.getValue());
                functionNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setFunctionUuids(functionUuids.toString());
        bean.setFunctionNames(functionNames.toString());
        bean.setDutyUuid(job.getDuty().getUuid());
        /**
         * begin 2014-12-11 yuyq end 2014-12-11
         * 在bean里面获取职级和职位序列两个字段的值
         */
        bean.setSeriesName(job.getDuty().getSeriesName());
        bean.setDutyLevel(job.getDuty().getDutyLevel());
        // 4、获取用户角色信息
        // Set<Role> roles =
        // this.securityApiFacade.getRolesByJobUuid(job.getUuid());
        // Set<Role> roleBeans = new HashSet<Role>();
        // for (Role role : roles) {
        // Role roleBean = new Role();
        // BeanUtils.copyProperties(role, roleBean);
        // roleBeans.add(roleBean);
        // }
        // bean.setRoles(roleBeans);
        return bean;
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(JobBean bean) {
        Job job = new Job();

        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            String id = idGeneratorService.generate(Job.class, JOB_ID_PATTERN);
            String tenantId = SpringSecurityUtils.getCurrentTenantId();
            id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
            bean.setId(id);
            bean.setTenantId(tenantId);
            // 防止ID序列认为破坏导致ID重复问题
            while (this.getById(id) != null) {
                id = idGeneratorService.generate(Job.class, JOB_ID_PATTERN);
                tenantId = SpringSecurityUtils.getCurrentTenantId();
                id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
                bean.setId(id);
                bean.setTenantId(tenantId);
            }
        } else {
            job = this.getByUuid(bean.getUuid());
            /**
             * @author liyb
             * 2015.1.4
             */
            Set<UserJob> userJobs = job.getJobUsers();
            for (UserJob userJob : userJobs) {
                User user = userJob.getUser();
                // 职位与用户同步
                syncJobUser(bean, user, job);
                // 部门与用户同步
                syncDeptUser(bean, user, job);
            }
        }
        if (StringUtils.isEmpty(bean.getDutyName())) {
            throw new RuntimeException("职务不能为空!");
        }

        if (StringUtils.isEmpty(bean.getDutyUuid())) {
            throw new RuntimeException("职务不能为空!");
        }

        if (StringUtils.isEmpty(bean.getDepartmentName())) {
            throw new RuntimeException("所属部门不能为空!");
        }

        if (bean.getDepartmentName().indexOf(";") > 0) {
            throw new RuntimeException("所属部门只能有一个!");
        }

        BeanUtils.copyProperties(bean, job);

        Department department = departmentService.getById(bean.getDepartmentId());
        // 显示用部门全路径显示
        job.setDepartmentName(department.getPath());
        job.setDepartmentUuid(department.getUuid());

        Duty duty = dutyService.getByUuid(bean.getDutyUuid());
        job.setDuty(duty);

        this.save(job);

        // 设置汇报对象，支持部门，职位，人员
        saveJobPrincipal(bean, job);

        // 3、设置职位职能关系
        String functionUuidString = bean.getFunctionUuids();
        if (StringUtils.isNotBlank(functionUuidString)) {
            // 删除领导
            jobFunctionService.deleteByJob(job.getUuid());
            // 新的领导
            String[] functionUuids = functionUuidString.split(Separator.SEMICOLON.getValue());
            List<JobFunction> newFunctions = new ArrayList<JobFunction>();
            for (String functionUuid : functionUuids) {
                List<DataDictionary> functions = dataDictionaryService.findBy("uuid", functionUuid);
                JobFunction jobFunction = new JobFunction();
                jobFunction.setJob(job);
                jobFunction.setFunctionUuid(functions.get(0).getUuid());
                newFunctions.add(jobFunction);
            }
            // 更新
            for (JobFunction entity : newFunctions) {
                jobFunctionService.save(entity);
            }
        } else {
            // 删除岗位对应职能
            jobFunctionService.deleteByJob(job.getUuid());
        }

        // 5.1、设置岗位角色信息
        List<JobRole> jobRoles = this.jobRoleService.getJobRoleByJobUuid(job.getUuid());
        if (jobRoles.size() > 0) {
            for (JobRole jobRole : jobRoles) {
                this.jobRoleService.delete(jobRole);
            }
        }
        // Set<Role> roles = securityApiFacade.getRolesByJobUuid(job.getUuid());
        // roles.clear();
        // for (Role role : bean.getRoles()) {
        // Role tmp = this.roleDao.get(role.getUuid());
        // if (tmp != null) {
        // JobRole jobRole = new JobRole();
        // JobRoleId jobRoleId = new JobRoleId(job.getUuid(), tmp.getUuid(),
        // SpringSecurityUtils.getCurrentTenantId());
        // jobRole.setJobRoleId(jobRoleId);
        // this.jobRoleService.save(jobRole);
        // roles.add(tmp);
        // }
        // }

        // 5.2、设置岗位权限信息
        Set<Privilege> privileges = null;// this.securityApiFacade.getPrivilegesByJobUuid(job.getUuid());
        privileges.clear();
        for (Privilege privilege : bean.getPrivileges()) {
            Privilege pritmp = this.privilegeFacadeService.get(privilege.getUuid());
            if (pritmp != null) {
                privileges.add(pritmp);
            }
        }

        // 6、保存用户拼音信息，用于拼音搜索
        pinyinService.deleteByEntityUuid(job.getUuid());
        Set<Pinyin> departmentPinyins = getJobPinyin(job);
        for (Pinyin departmentPinyin : departmentPinyins) {
            pinyinService.save(departmentPinyin);
        }
    }

    /**
     * @param bean
     * @param user
     * @param job
     * @author liyb
     * date 2015.1.6
     */
    public void syncJobUser(JobBean bean, User user, Job job) {
        String uMajorName = user.getMajorJobName();
        String uOtherName = user.getOtherJobNames();
        String jobName = job.getDepartmentName() + "/" + job.getName(); // 职位全称
        int start = -1;
        if (uOtherName != null) {
            start = uOtherName.indexOf(jobName);
        }
        if (jobName.equals(uMajorName)) { // 如果主职位与修改的职位相等，说明修改的是主职位
            // 替换与到数据库中修改
            String deptId = bean.getDepartmentId();
            Department dept = departmentService.getById(deptId);
            String newJobName = dept.getPath() + "/" + bean.getName();
            userService.updateMajorName(newJobName, user.getUuid());
        }
        if (start > -1) {
            String tName[] = uOtherName.split(Separator.SEMICOLON.getValue());
            StringBuilder newJobName = new StringBuilder();

            for (String name : tName) {
                if (name.indexOf(jobName) > -1) { // 如果职位被改了（可能部门路径也被改了）
                    String deptId = bean.getDepartmentId();
                    Department dept = departmentService.getById(deptId);
                    newJobName.append(Separator.SEMICOLON.getValue());
                    newJobName.append(dept.getPath() + "/" + bean.getName());
                    continue;
                }
                newJobName.append(Separator.SEMICOLON.getValue());
                newJobName.append(name);
            }
            String newName = newJobName.toString().replaceFirst(";", "");
            userService.updateOtherName(newName, user.getUuid());
        }
    }

    /**
     * @param bean
     * @param user
     * @param job
     * @author liyb
     * @date 2015.1.6
     */
    public void syncDeptUser(JobBean bean, User user, Job job) {
        // 删除用户所属部门(未改职位前)
        departmentUserJobService.deleteByUser(user.getUuid());
        Set<UserJob> userJobs = user.getUserJobs();
        StringBuilder userDeptNames = new StringBuilder();

        for (UserJob userJob : userJobs) {
            DepartmentUserJob departmentUser = new DepartmentUserJob();
            Job uJob = userJob.getJob();
            String jDeptUuid = ""; // 职位与部门对应的部门uuid
            // 如果此修改的职位与用户相对应，则，获取新修改的部门uuid
            if (job.getUuid().equals(uJob.getUuid())) {
                String deptId = bean.getDepartmentId();
                Department dept = departmentService.getById(deptId);
                jDeptUuid = dept.getUuid();
            } else {
                jDeptUuid = uJob.getDepartmentUuid();
            }
            Department department = departmentService.get(jDeptUuid);
            userDeptNames.append(";");
            userDeptNames.append(department.getPath()); // 获得部门全路径

            departmentUser.setUser(user);
            departmentUser.setDepartment(department);
            departmentUser.setJobCode(""); // 未配置它的岗位编码（它属于临时数据）
            departmentUser.setJobName(user.getJobName());

            // 判断是否是主职位
            String majorJobName = user.getMajorJobName();
            String jobName = uJob.getDepartmentName() + "/" + uJob.getName();

            if (majorJobName.equals(jobName)) {
                departmentUser.setIsMajor(true);
            } else {
                departmentUser.setIsMajor(false);
            }
            this.dao.save(departmentUser);
        }
        String newDeptName = userDeptNames.toString().replaceFirst(";", "");
        String uuid = user.getUuid();
        userService.updateUserDeptName(newDeptName, uuid);
    }

    /**
     * 获取用户的所有用户拼音实体
     *
     * @param user
     * @return
     */
    private Set<Pinyin> getJobPinyin(Job job) {
        Set<String> pinyins = new HashSet<String>();
        String userUuid = job.getUuid();
        String Name = job.getName();
        pinyins.add(PinyinUtil.getPinYin(Name));
        pinyins.add(PinyinUtil.getPinYinHeadChar(Name));

        Set<Pinyin> jobPinyins = new HashSet<Pinyin>();
        for (String pinyin : pinyins) {
            Pinyin jobPinyin = new Pinyin();
            jobPinyin.setType(Job.class.getSimpleName());
            jobPinyin.setEntityUuid(userUuid);
            jobPinyin.setPinyin(pinyin);
            jobPinyins.add(jobPinyin);
        }
        return jobPinyins;
    }

    /**
     * 根据岗位uuid加载角色树
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.JobService#getRoleTree(java.lang.String)
     */
    @Override
    public TreeNode getRoleTree(String uuid) {
        // Set<Role> userRoles = this.securityApiFacade.getRolesByJobUuid(uuid);
        // List<Role> roles = roleDao.getAll();
        // return OrgUtil.getCategoryRoleTree(dataDictionaryDao, userRoles,
        // roles);
        return null;
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    @Override
    public TreeNode getPrivilegeTree(String uuid) {
        // 附加构建权限树
        Set<Privilege> ownerPrivileges = null;// this.securityApiFacade.getPrivilegesByJobUuid(uuid);
        List<Privilege> privileges = this.privilegeFacadeService.getAll();
        TreeNode treeNode = OrgUtil.buildPrivilegeTree(privileges,
                Arrays.asList(ownerPrivileges.toArray(new Privilege[0])));
        return treeNode;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.JobService#getJobPrivilegeTree(java.lang.String)
     */
    @Override
    public TreeNode getJobPrivilegeTree(String uuid) {
        Job job = this.getByUuid(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(job.getName());
        treeNode.setId(TreeNode.ROOT_ID);
        Set<Privilege> privileges = null;// this.securityApiFacade.getPrivilegesByJobUuid(uuid);
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Privilege privilege : privileges) {
            TreeNode child = new TreeNode();
            child.setId(privilege.getUuid());
            child.setName(privilege.getName());
            children.add(child);
        }
        treeNode.setChildren(children);
        return treeNode;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.JobService#getJobRoleNestedRoleTree(java.lang.String)
     */
    @Override
    public TreeNode getJobRoleNestedRoleTree(String uuid) {
        Job job = this.getByUuid(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(job.getName());
        treeNode.setId(TreeNode.ROOT_ID);
        // Set<Role> roles = this.securityApiFacade.getRolesByJobUuid(uuid);
        // List<TreeNode> children = new ArrayList<TreeNode>();
        // for (Role role : roles) {
        // TreeNode child = new TreeNode();
        // child.setId(role.getUuid());
        // child.setName(role.getName());
        // children.add(child);
        // this.roleService.buildRoleNestedRoleTree(child, role);
        // }
        // treeNode.setChildren(children);
        return treeNode;
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteByIds(Collection<String> ids) {
        for (String id : ids) {
            this.deleteById(id);
        }
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteById(String id) {
        Job job = this.getById(id);

        if (job.getJobUsers().size() > 0) { // 判断职位是否与用户关联
            Set<UserJob> userJobs = job.getJobUsers();
            Iterator<UserJob> userJob = userJobs.iterator();
            StringBuilder userName = new StringBuilder();
            while (userJob.hasNext()) {
                User user = userJob.next().getUser();
                userName.append(user.getUserName());
                if (userJob.hasNext()) {
                    userName.append(Separator.SEMICOLON.getValue());
                }
            }
            throw new RuntimeException("职位已被【" + userName + "】的用户引用，不能删除");
            // } else if (job.getGroups().size() > 0) { // 判断职位是否与群组关联
            // Set<Group> groups = job.getGroups();
            // Iterator<Group> group = groups.iterator();
            // StringBuilder groupName = new StringBuilder();
            // while (group.hasNext()) {
            // groupName.append(group.next().getName());
            // if (group.hasNext()) {
            // groupName.append(Separator.SEMICOLON.getValue());
            // }
            // }
            // throw new RuntimeException("职位已被【" + groupName + "】的组群引用，不能删除");
            // // } else if
            // (this.securityApiFacade.getRolesByJobUuid(job.getUuid()).size() >
            // 0) { // 判断职位是否与角色关联
            // Set<Role> roles =
            // this.securityApiFacade.getRolesByJobUuid(job.getUuid());
            // StringBuilder roleName = new StringBuilder();
            // Iterator<Role> role = roles.iterator();
            // while (role.hasNext()) {
            // roleName.append(role.next().getName());
            // if (role.hasNext()) {
            // roleName.append(Separator.SEMICOLON.getValue());
            // }
            // }
            // throw new RuntimeException("职位已被【" + roleName + "】的组群引用，不能删除");
            // } else if
            // (this.securityApiFacade.getPrivilegesByJobUuid(job.getUuid()).size()
            // > 0) { // 判断职位是否与权限关联
            // Set<Privilege> privileges = null;//
            // this.securityApiFacade.getPrivilegesByJobUuid(job.getUuid());
            // StringBuilder privilegeName = new StringBuilder();
            // Iterator<Privilege> privilege = privileges.iterator();
            // while (privilege.hasNext()) {
            // privilegeName.append(privilege.next().getName());
            // if (privilege.hasNext()) {
            // privilegeName.append(Separator.SEMICOLON.getValue());
            // }
            // }
            // throw new RuntimeException("职位已被【" + privilegeName +
            // "】的组群引用，不能删除");
        }
        this.dao.delete(job);
    }

    @Override
    public Department getTopDepartment(String id) {
        Job job = this.getById(id);
        Department department = null;
        Department jobdepartment = departmentService.get(job.getDepartmentUuid());
        if (jobdepartment != null) {
            department = departmentService.getTopDepartment(jobdepartment.getUuid());
        }
        return department;
    }

    @Override
    public HashMap<String, Object> saveJobFromList(List list) {
        // "共获得100条部门记录，成功导入80条，已经存在20条!"
        int duplicatecount = 0;
        int successcount = 0;
        int totalcount = list.size();
        HashMap<String, Object> rsmap = new HashMap<String, Object>();
        HashMap<String, String> jobLeaderMap = new HashMap<String, String>();// 职位uuid，汇报对象名字

        if (list != null && !list.isEmpty()) {
            // name type定义
            List<DataDictionary> datadictionarys = dataDictionaryService.getDataDictionariesByType("FUNCTION_TYPE");
            for (Object o : list) {
                Job job = (Job) o;

                Job jobUpdate = new Job();
                boolean isUpdate = false;

                List<Job> jobls = this.getJobByNameAndDeptName(job.getName(), job.getDepartmentUuid());
                // findBy("externalId", job.getExternalId());
                if (jobls != null && jobls.size() > 0) {
                    jobUpdate = jobls.get(0);
                    isUpdate = true;
                }

                // name type定义
                List<Duty> dutys = this.dao.findBy(Duty.class, "externalId", job.getDutyName());
                if (dutys.size() > 0) {
                    job.setDuty(dutys.get(0));
                    job.setDutyName(dutys.get(0).getName());
                }

                List<Department> deptList = this.dao.findBy(Department.class, "externalId", job.getDepartmentName());
                if (deptList.isEmpty()) {
                    throw new RuntimeException("职位【" + job.getName() + "】找不到对应的部门【" + job.getDepartmentName() + "】");
                }
                Department department = deptList.get(0);
                job.setDepartmentUuid(department.getUuid());
                job.setDepartmentName(department.getPath());
                if (isUpdate) {
                    jobUpdate.setName(job.getName());
                    jobUpdate.setDuty(job.getDuty());
                    jobUpdate.setDutyName(job.getDutyName());
                    jobUpdate.setDepartmentName(job.getDepartmentName());
                    jobUpdate.setDepartmentUuid(job.getDepartmentUuid());
                    job.setCode(job.getExternalId());
                    jobUpdate.setExternalId(job.getExternalId());
                    this.save(jobUpdate);
                    jobLeaderMap.put(jobUpdate.getUuid(), job.getLeaderNames());
                    duplicatecount = duplicatecount + 1;
                } else {
                    job.setUuid(null);

                    String id = idGeneratorService.generate(Job.class, JOB_ID_PATTERN);
                    String tenantId = SpringSecurityUtils.getCurrentTenantId();
                    id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
                    job.setId(id);
                    job.setCode(job.getExternalId());
                    this.save(job);
                    jobLeaderMap.put(job.getUuid(), job.getLeaderNames());
                    successcount = successcount + 1;
                }

                // 职能线
                if (!StringUtils.isEmpty(job.getFunctionNames())) {
                    String[] functionnames = job.getFunctionNames().split(";");
                    List<JobFunction> newFunctions = new ArrayList<JobFunction>();
                    for (int i = 0; i < functionnames.length; i++) {
                        for (DataDictionary dataDictionary : datadictionarys) {
                            if (functionnames[i].equals(dataDictionary.getName())) {

                                List<DataDictionary> functions = dataDictionaryService.findBy("uuid",
                                        dataDictionary.getUuid());
                                JobFunction jobFunction = new JobFunction();
                                jobFunction.setJob(job);
                                jobFunction.setFunctionUuid(functions.get(0).getUuid());
                                newFunctions.add(jobFunction);
                            }
                        }
                    }
                    // 建立职能关系
                    for (JobFunction entity : newFunctions) {
                        jobFunctionService.save(entity);
                    }
                }

            }
        }

        String msg = "选择的职位数据共【" + totalcount + "】行，成功更新【" + duplicatecount + "】行，成功导入【" + successcount + "】行";
        System.out.println(msg);
        rsmap.put("msg", msg);
        rsmap.put("jobLeaderMap", jobLeaderMap);
        return rsmap;
    }

    @Override
    public void updateJobLeadersAfterImport(HashMap<String, Object> jobRsMp) {
    }

    @Override
    public List<User> getUsersByJobId(String jobId) {
        Job job = this.getById(jobId);
        List<User> userls = new ArrayList<User>();
        if (job == null) { // 增加空判断
            return userls;
        }
        Set<UserJob> userJobs = job.getJobUsers();
        for (UserJob userJob : userJobs) {
            userls.add(userJob.getUser());
        }
        return userls;
    }

    /**
     * 设置汇报对象新
     *
     * @param bean
     * @param department
     */
    private void saveJobPrincipal(JobBean bean, Job job) {

        // 1、设置汇报对象
        String principalIdString = bean.getLeaderIds();
        if (StringUtils.isNotBlank(principalIdString)) {
            // 删除汇报对象
            jobPrincipalDao.deletePrincipal(job.getUuid());

            // 新的汇报对象
            String[] principalIds = principalIdString.split(Separator.SEMICOLON.getValue());
            List<JobPrincipal> newPrincipals = new ArrayList<JobPrincipal>();
            for (String principalId : principalIds) {
                JobPrincipal principal = new JobPrincipal();
                principal.setJobUuid(job.getUuid());
                principal.setType(JobPrincipal.TYPE_PRINCIPAL);
                principal.setOrgId(principalId);
                newPrincipals.add(principal);
            }
            // 更新
            for (JobPrincipal entity : newPrincipals) {
                jobPrincipalDao.save(entity);
            }
        } else {
            // 删除汇报对象
            jobPrincipalDao.deletePrincipal(job.getUuid());
        }
    }

    /**
     * 设置汇报对象到bean(新)
     *
     * @param department
     * @param bean
     */
    private void setJobPrincipalToBean(Job job, JobBean bean) {
        // // 2、获取汇报对象
        List<JobPrincipal> principals = jobPrincipalDao.getPrincipal(job.getUuid());
        StringBuilder principalIds = new StringBuilder();
        StringBuilder principalNames = new StringBuilder();
        Iterator<JobPrincipal> principalIt = principals.iterator();
        while (principalIt.hasNext()) {
            JobPrincipal principal = principalIt.next();
            principalIds.append(principal.getOrgId());
            principalNames.append(getOrgNameById(principal.getOrgId()));
            if (principalIt.hasNext()) {
                principalIds.append(Separator.SEMICOLON.getValue());
                principalNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setLeaderIds(principalIds.toString());
        bean.setLeaderNames(principalNames.toString());
    }

    /**
     * 跟组组织id获得组织名称
     *
     * @param orgId
     * @return
     */
    private String getOrgNameById(String orgId) {
        String name = "";
        if (orgId.startsWith(IdPrefix.USER.getValue())) {// U
            name = userService.getById(orgId).getUserName();
        } else if (orgId.startsWith(IdPrefix.DEPARTMENT.getValue())) {// D
            name = this.departmentService.getById(orgId).getPath();
        } else if (orgId.startsWith("J")) {// J
            Job job = this.getById(orgId);
            name = job.getDepartmentName() + "/" + this.getById(orgId).getName();
        }
        return name;
    }

    @Override
    public List<Job> getAll() {
        // TODO Auto-generated method stub
        return this.dao.getAll(Job.class);
    }

    @Override
    public Set<Job> getJobByRoleUuid(String roleUuid) {
        List<JobRole> jobRoles = jobRoleService.getJobRoleByRoleUuid(roleUuid);
        Set<Job> jobs = new HashSet<Job>();
        for (JobRole jobRole : jobRoles) {
            jobs.add(this.getByUuid(jobRole.getJobRoleId().getJobUuid()));
        }
        return jobs;
    }

    /**
     * @param id
     * @return
     */
    public Job getById(String id) {
        return this.dao.findUniqueBy(Job.class, "id", id);
    }

    /**
     * @param id
     * @return
     */
    public Job getByUuId(String uuid) {
        return this.dao.findUniqueBy(Job.class, "uuid", uuid);
    }

    /**
     * 通过部门名称和岗位名称定位岗位
     *
     * @param jobname
     * @param deptname
     * @return
     */
    public List<Job> getJobByNameAndDeptName(String jobname, String deptname) {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("jobName", StringUtils.trimToEmpty(jobname));
        values.put("departmentName", StringUtils.trimToEmpty(deptname));
        return this.dao.find(GET_JOB_BY_NAME_AND_DEPTNAME, values, Job.class);
    }

    /**
     * 通过部门ID获得岗位
     *
     * @param jobname
     * @param deptname
     * @return
     */
    public List<Job> getJobByDeptUuid(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.dao.find(GET_JOB_BY_DEPTUUID, values, Job.class);
    }

    /**
     * 通过职务UUID获得职位
     *
     * @param deptUuid
     * @return
     */
    public List<Job> getJobByDutyUuid(String dutyUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dutyUuid", dutyUuid);
        return this.dao.find(GET_JOB_BY_DUTYUUID, values, Job.class);
    }

    @Override
    public List<Job> namedQuery(String string, Map<String, Object> values, Class<Job> class1, PagingInfo pagingInfo) {
        return this.dao.namedQuery(string, values, class1, pagingInfo);
    }

    @Override
    public List<Job> namedQuery(String string, Map<String, Object> values, Class<Job> class1) {
        return this.dao.namedQuery(string, values, class1);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.JobService#orderedUserIdsByJobGrade(java.util.Collection)
     */
    @Override
    public UsersGrade orderedUserIdsByJobGrade(Collection<String> userIds) {
        if (userIds.isEmpty()) {
            return new UsersGrade();
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIds", userIds);
        List<UserGradeItem> orderedIds = this.nativeDao.namedQuery("orderedUserIdsByJobGrade", values,
                UserGradeItem.class);
        UsersGrade usersGrade = new UsersGrade();
        List<String> orderedUserIds = new ArrayList<String>();
        Map<String, Set<String>> gradeMap = new LinkedHashMap<String, Set<String>>();
        for (UserGradeItem userGradeItem : orderedIds) {
            String userId = userGradeItem.getUserId();
            String grade = userGradeItem.getGrade();
            if (StringUtils.isBlank(grade)) {
                grade = "-1";
            }
            if (!gradeMap.containsKey(grade)) {
                gradeMap.put(grade, new LinkedHashSet<String>());
            }
            orderedUserIds.add(userId);
            gradeMap.get(grade).add(userId);
        }
        usersGrade.setUserIds(orderedUserIds);
        usersGrade.setGradeMap(gradeMap);
        return usersGrade;
    }

}
