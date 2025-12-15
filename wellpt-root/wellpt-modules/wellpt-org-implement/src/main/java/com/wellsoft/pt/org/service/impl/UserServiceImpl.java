package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.context.authentication.encoding.PasswordEncoderFactory;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.OrgUserGetRequest;
import com.wellsoft.pt.api.response.OrgUserGetResponse;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.common.pinyin.entity.Pinyin;
import com.wellsoft.pt.common.pinyin.service.PinyinService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.TenantGzUser;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.mt.service.TenantGzUserService;
import com.wellsoft.pt.org.adsycn.service.ADDeptService;
import com.wellsoft.pt.org.adsycn.service.ADGroupService;
import com.wellsoft.pt.org.adsycn.service.ADUserService;
import com.wellsoft.pt.org.adsycn.vo.ADGroup;
import com.wellsoft.pt.org.adsycn.vo.ADUser;
import com.wellsoft.pt.org.bean.UserBean;
import com.wellsoft.pt.org.dao.JobPrincipalDao;
import com.wellsoft.pt.org.dao.UserDeputyDao;
import com.wellsoft.pt.org.dao.UserJobDao;
import com.wellsoft.pt.org.dao.UserLeaderDao;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.org.support.UserProfile;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.service.CommonDepartmentService;
import com.wellsoft.pt.workflow.gz.support.WfGzDataConstant;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.encoding.BasePasswordEncoder;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author lilin
 * 2013-9-25    liuzq       2013-9-25       Update
 * @ClassName: UserManager
 * @Description: 主要实现用户、部门、群组、职位的相关等操作
 */
@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl implements UserService {
    public static final String KEY_WELL_PT_REST_ADDRESS = "api.restful.webservice.address";
    public static final String DEFAULT_WELL_PT_REST_ADDRESS = "http://localhost/webservices/wellpt/rest/service";
    private static final String USER_ID_PATTERN = "U0000000000";
    // Dao相关的查询
    private static final String QUERY_ALL_ADMIN_ID = "select user.id from User user where user.issys = true and user.enabled = true";

    // @Autowired
    // private UserDao userDao;
    private static final String QUERY_ALL_BY_DEPARTMENT = "select user.id from User user where exists ( select 1 from DepartmentUserJob userJob where userJob.user = user and userJob.department.uuid = :departmentUuid )";
    private static final String UPDATE_LAST_LOGIN_TIME = "update User user set user.lastLoginTime = user.modifyTime, modifyTime = :modifyTime where uuid = :uuid";
    private static final String GET_USER_ID_LIKE_NAME = "select id from User user where user.loginName like '%' || :name || '%' or user.userName like '%' || :name || '%'";
    private static final String GET_BY_ID_NUMBER_AND_SUBJECT_DN = "from User user where user.idNumber = :idNumber and "
            + " exists (select up.uuid from UserProperty up where user.uuid = up.userUuid and up.name = 'certificate.subject' and up.value = :subjectDN)";
    private static final String UPDATE_USER_MAJORNAME = "update User set majorJobName = :newJobName where uuid = :userUuid";
    private static final String UPDATE_USER_OTHERNAME = "update User set otherJobNames = :newJobName where uuid = :userUuid";
    private static final String UPDATE_USER_DEPTNAME = "update User set departmentName = :newDeptName where uuid = :uuid";
    private static final String UPDATE_USER_PASSWORD = "update User set password = :password where uuid = :uuid";
    private static final String FILTER_USER_BY_ORG_ID = "select t.id from User t where t.id in(:userIds) and exists (select duj.user.uuid from DepartmentUserJob duj where duj.department.orgId = :orgId and duj.user.uuid = t.uuid)";
    private static final String FILTER_DEPARTMENT_PRINCIPAL_BY_ORG_ID = "select t.id from User t where t.id in(:userIds) and exists (select dp.orgId from DeptPrincipal dp, Department d where dp.orgId = t.id and dp.departmentUuid = d.uuid and d.orgId = :orgId)";
    private static final String GET_USER_ORGS = "from Organization t1 where "
            + "exists(select t2.uuid from Department t2 left join t2.departmentUsers as t3 where t1.id = t2.orgId "
            + "and exists(select t4.uuid from User t4 where t4.uuid = t3.user.uuid and t4.id = :userId))"
            + " order by t1.code asc";
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
    @Autowired
    private com.wellsoft.pt.common.generator.service.IdGeneratorService idGeneratorService;
    @Autowired
    private UserPropertyService userPropertyService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private RoleFacadeService roleFacadeService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentUserJobService departmentUserJobService;
    @Autowired
    private UserLeaderDao userLeaderDao;
    @Autowired
    private UserDeputyDao userDeputyDao;
    @Autowired
    private DepartmentPrincipalService departmentPrincipalService;
    @Autowired
    private PinyinService pinyinService;
    @Autowired
    private CommonDepartmentService commonDepartmentService;
    @Autowired
    private DepartmentAdminService departmentAdminService;
    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;
    @Autowired
    private JobService jobService;
    @Autowired
    private UserJobDao userJobDao;
    @Autowired
    private DeptPrincipalService deptPrincipalService;
    @Autowired
    private JobPrincipalDao jobPrincipalDao;
    @Autowired
    private DutyService dutyService;
    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private ADUserService adUserService;
    @Autowired
    private ADDeptService adDeptService;
    @Autowired
    private ADGroupService adGroupService;
    @Autowired
    private TenantGzUserService tenantGzUserService;
    @Autowired
    private TenantFacadeService tenantService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private SecurityAuditFacadeService securityAuditFacadeService;
    //    @Autowired
//    private UserRoleService userRoleService;
    @Autowired
    private UserPrivilegeService userPrivilegeService;

    /**
     * 循环遍历获取上级领导ID
     *
     * @param user
     */
    private static void traverseAndAddLeaderIds(User user, List<String> leaderIds) {
        if (user != null) {
            Set<UserLeader> leaders = user.getLeaders();
            for (UserLeader userLeader : leaders) {
                leaderIds.add(userLeader.getLeader().getId());
                // 循环遍历获取上级领导ID
                traverseAndAddLeaderIds(userLeader.getLeader(), leaderIds);
            }
        }
    }

    /**
     * 循环遍历获取部门下的用户ID
     *
     * @param user
     */
    private static void traverseAndAddUserIds(Department department, List<String> userIds) {
        Set<DepartmentUserJob> departmentUserJobs = department.getDepartmentUsers();
        // 添加部门下的用户ID
        for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
            userIds.add(departmentUserJob.getUser().getId());
        }

        // 遍历子部门
        List<Department> children = department.getChildren();
        for (Department child : children) {
            // 循环遍历获取上级领导ID
            traverseAndAddUserIds(child, userIds);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#get(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public User get(String uuid) {
        User bean = this.dao.get(User.class, uuid);
        try {
            if (bean != null) {
                List<Object> us = dao.getSession()
                        .createSQLQuery(
                                "select bouser,bopwd from org_user where uuid='" + uuid + "'").list();
                if (us != null) {
                    Object object = us.get(0);
                    if (object instanceof Object[]) {
                        if (((Object[]) object)[0] != null) {
                            bean.setBoUser(((Object[]) object)[0].toString());
                        }
                        if (((Object[]) object)[1] != null) {
                            bean.setBoPwd(((Object[]) object)[1].toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getByLoginName(String loginName) {
        User bean = this.dao.findUniqueBy(User.class, "loginName", loginName);
        try {
            if (bean != null) {
                List<Object> us = dao.getSession()
                        .createSQLQuery(
                                "select bouser,bopwd from org_user where uuid='" + bean.getUuid() + "'").list();
                if (us != null) {
                    Object object = us.get(0);
                    if (object instanceof Object[]) {
                        if (((Object[]) object)[0] != null) {
                            bean.setBoUser(((Object[]) object)[0].toString());
                        }
                        if (((Object[]) object)[1] != null) {
                            bean.setBoPwd(((Object[]) object)[1].toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getBySubjectDN(java.lang.String)
     */
    @Override
    public User getBySubjectDN(String subjectDN) {
        UserProperty example = new UserProperty();
        example.setValue(subjectDN);
        List<UserProperty> subjectDNs = userPropertyService.findByExample(example);
        if (subjectDNs.size() == 1) {
            return this.dao.get(User.class, subjectDNs.get(0).getUserUuid());
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getByIdNumber(java.lang.String)
     */
    @Override
    public User getByIdNumber(String idNumber) {
        User example = new User();
        example.setIdNumber(idNumber);
        List<User> users = this.findByExample(example);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getByIdNumberAndSubjectDN(java.lang.String, java.lang.String)
     */
    @Override
    public User getByIdNumberAndSubjectDN(String idNumber, String subjectDN) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("idNumber", idNumber);
        values.put("subjectDN", subjectDN);
        return this.dao.findUnique(GET_BY_ID_NUMBER_AND_SUBJECT_DN, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getAll()
     */
    @Override
    public List<User> getAll() {
        return this.dao.findBy(User.class, "tenantId", SpringSecurityUtils.getCurrentTenantId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getById(String id) {
        User bean = this.dao.findUniqueBy(User.class, "id", id);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getLeaders(java.lang.String)
     */
    @Override
    public List<User> getLeaders(String uuid) {
        List<User> leaders = Lists.newArrayList();
        Set<UserLeader> userLeaders = this.get(uuid).getLeaders();
        for (UserLeader userLeader : userLeaders) {
            leaders.add(userLeader.getLeader());
        }
        return leaders;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getBean(java.lang.String)
     */
    @Override
    public UserBean getBean(String uuid) {
        return getBeanById(this.get(uuid).getId());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getBeanById(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public UserBean getBeanById(String id) {
        User user = this.getById(id);
        UserBean bean = new UserBean();
        BeanUtils.copyProperties(user, bean);

        if (StringUtils.isBlank(user.getTenantId())) {
            bean.setNotAllowedTenantId(true);
        }

        // 1、获取部门信息
        // DepartmentUserJob departmentUser =
        // departmentUserDao.getMajor(user.getUuid());
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        StringBuilder departmentIds = new StringBuilder();
        StringBuilder departmentNames = new StringBuilder();
        StringBuilder jobCodes = new StringBuilder();
        StringBuilder jobNames = new StringBuilder();
        Iterator<DepartmentUserJob> deptIt = departmentUsers.iterator();

        DepartmentUserJob departmentMajorUser = new DepartmentUserJob();
        Set<DepartmentUserJob> departmentOtherUsers = new HashSet<DepartmentUserJob>();

        while (deptIt.hasNext()) {
            DepartmentUserJob departmentUserJob = deptIt.next();
            if (departmentUserJob.getIsMajor()) {
                departmentMajorUser = departmentUserJob;
            } else {
                departmentOtherUsers.add(departmentUserJob);
            }
            String jobCode = departmentUserJob.getJobName();
            String jobName = departmentUserJob.getJobName();
            jobCodes.append(jobCode == null ? "" : jobCode);
            jobNames.append(jobName == null ? "" : jobName);
        }

        if (departmentMajorUser.getDepartment() != null) {
            departmentIds.append(departmentMajorUser.getDepartment().getId());
            departmentNames.append(departmentMajorUser.getDepartment().getPath());
            if (departmentOtherUsers.size() > 0) {
                departmentIds.append(Separator.SEMICOLON.getValue());
                departmentNames.append(Separator.SEMICOLON.getValue());
            }

            Iterator<DepartmentUserJob> deptOtherIt = departmentOtherUsers.iterator();
            while (deptOtherIt.hasNext()) {
                DepartmentUserJob departmentUserJob = deptOtherIt.next();
                departmentIds.append(departmentUserJob.getDepartment().getId());
                departmentNames.append(departmentUserJob.getDepartment().getPath());
                if (deptOtherIt.hasNext()) {
                    departmentIds.append(Separator.SEMICOLON.getValue());
                    departmentNames.append(Separator.SEMICOLON.getValue());
                }
            }
        }
        bean.setDepartmentId(departmentIds.toString());
        bean.setDepartmentName(departmentNames.toString());
        bean.setJobCode(jobCodes.toString());
        bean.setJobName(jobNames.toString());

        // 2、获取上级领导
        Set<UserLeader> leaders = user.getLeaders();
        StringBuilder leaderIds = new StringBuilder();
        StringBuilder leaderNames = new StringBuilder();
        if (leaders.size() > 0) {
            Iterator<UserLeader> it = leaders.iterator();
            while (it.hasNext()) {
                UserLeader userLeader = it.next();
                leaderIds.append(userLeader.getLeader().getId());
                leaderNames.append(userLeader.getLeader().getUserName());
                if (it.hasNext()) {
                    leaderIds.append(Separator.SEMICOLON.getValue());
                    leaderNames.append(Separator.SEMICOLON.getValue());
                }
            }
            bean.setLeaderIds(leaderIds.toString());
            bean.setLeaderNames(leaderNames.toString());
        }
        // 3、获取职务代理人
        Set<UserDeputy> deputies = user.getMinorJobs();
        StringBuilder deputyIds = new StringBuilder();
        StringBuilder deputyNames = new StringBuilder();
        Iterator<UserDeputy> deputyIt = deputies.iterator();
        while (deputyIt.hasNext()) {
            UserDeputy minorJob = deputyIt.next();
            deputyIds.append(minorJob.getDeputy().getId());
            deputyNames.append(minorJob.getDeputy().getUserName());
            if (deputyIt.hasNext()) {
                deputyIds.append(Separator.SEMICOLON.getValue());
                deputyNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setDeputyIds(deputyIds.toString());
        bean.setDeputyNames(deputyNames.toString());

        // 获得主职位
        Set<UserJob> jobs = user.getUserJobs();

        Iterator<UserJob> jobIt = jobs.iterator();
        Set<UserJob> otherjobs = new HashSet<UserJob>();
        while (jobIt.hasNext()) {
            UserJob userjob = jobIt.next();
            if (userjob.getIsMajor() == true) {
                bean.setMajorJobName(
                        userjob.getJob().getDepartmentName() + "/" + userjob.getJob().getName());
                bean.setMajorJobId(userjob.getJob().getId());
            } else {
                otherjobs.add(userjob);
            }
        }

        // 其他职位
        StringBuilder otherJobIds = new StringBuilder();
        StringBuilder otherJobNames = new StringBuilder();
        Iterator<UserJob> otherjobIt = otherjobs.iterator();
        while (otherjobIt.hasNext()) {
            UserJob userjob = otherjobIt.next();
            otherJobIds.append(userjob.getJob().getId());
            otherJobNames.append(
                    userjob.getJob().getDepartmentName() + "/" + userjob.getJob().getName());
            if (otherjobIt.hasNext()) {
                otherJobIds.append(Separator.SEMICOLON.getValue());
                otherJobNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setOtherJobIds(otherJobIds.toString());
        bean.setOtherJobNames(otherJobNames.toString());

        /**
         * @author liyb
         * date 2015.1.4
         * 通过职位获得，获得所属部门
         */
        Set<DepartmentUserJob> deptUsers = user.getDepartmentUsers();
        StringBuilder allDeptNames = new StringBuilder();
        StringBuilder allDeptIds = new StringBuilder();
        Iterator<DepartmentUserJob> allDept = deptUsers.iterator();
        while (allDept.hasNext()) {
            DepartmentUserJob deptUser = allDept.next();
            Department department = deptUser.getDepartment();
            if (department != null) {
                allDeptNames.append(department.getPath());
                allDeptIds.append(department.getId());
                if (allDept.hasNext()) {
                    allDeptNames.append(Separator.SEMICOLON.getValue());
                    allDeptIds.append(Separator.SEMICOLON.getValue());
                }
            }
        }
        bean.setDepartmentName(allDeptNames.toString());
        bean.setDepartmentId(allDeptIds.toString());

        // 4、获取用户角色信息
        /**
         * 修改组织权限强关联关系
         */
        // // Set<Role> roles = user.getRoles();
        // Collection<Role> roles = securityApiFacade.getRolesByUserId(id);
        // Set<Role> roleBeans = new HashSet<Role>();
        // for (Role role : roles) {
        // Role roleBean = new Role();
        // BeanUtils.copyProperties(role, roleBean);
        // roleBeans.add(roleBean);
        // }
        // bean.setRoles(roleBeans);

        // 5 、获取用户组
        // Set<Group> groups = user.getGroups();
        // StringBuilder groupIds = new StringBuilder();
        // StringBuilder groupNames = new StringBuilder();
        // Iterator<Group> groupId = groups.iterator();
        // while (groupId.hasNext()) {
        // Group g = groupId.next();
        // groupIds.append(g.getId());
        // groupNames.append(g.getName());
        // if (groupId.hasNext()) {
        // groupIds.append(Separator.SEMICOLON.getValue());
        // groupNames.append(Separator.SEMICOLON.getValue());
        // }
        // }
        // bean.setGroupIds(groupIds.toString());
        // bean.setGroupNames(groupNames.toString());

        // 6、 保存用户扩展属性
        // 6.1 只能以证书登录
        UserProperty example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_ONLY_LOGON_CERTIFICATE);
        // List<UserProperty> userProperties =
        // this.userPropertyService.findByExample(example);
        // if (userProperties.isEmpty()) {
        // bean.setOnlyLogonWidthCertificate(false);
        // } else {
        // if (StringUtils.isNotBlank(userProperties.get(0).getValue())) {
        // bean.setOnlyLogonWidthCertificate(Boolean.valueOf(userProperties.get(0).getValue()));
        // }
        // }
        // 6.2 证书主体
        example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_CERTIFICATE_SUBJECT_DN);
        List<UserProperty> subjectDNProperties = this.userPropertyService.findByExample(example);
        if (subjectDNProperties.isEmpty()) {
            bean.setSubjectDN("");
        } else {
            if (StringUtils.isNotBlank(subjectDNProperties.get(0).getValue())) {
                bean.setSubjectDN(subjectDNProperties.get(0).getValue());
            }
        }
        // 6.3 接收短信消息
        example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_RECEIVE_SMS_MESSAGE);
        List<UserProperty> receiveSmsMessageProperties = userPropertyService.findByExample(example);
        if (receiveSmsMessageProperties.isEmpty()) {
            bean.setReceiveSmsMessage(false);
        } else {
            if (StringUtils.isNotBlank(receiveSmsMessageProperties.get(0).getValue())) {
                bean.setReceiveSmsMessage(
                        Boolean.valueOf(receiveSmsMessageProperties.get(0).getValue()));
            }
        }
        // 6.4 显示联系人
        example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_SHOW_CONTACT);
        List<UserProperty> contactNames = this.userPropertyService.findByExample(example);
        if (contactNames.isEmpty()) {
            bean.setContactName("");
        } else {
            if (StringUtils.isNotBlank(contactNames.get(0).getValue())) {
                bean.setContactName(contactNames.get(0).getValue());
            }
        }
        // 6.5 邮件
        example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_EMAIL);
        List<UserProperty> emails = this.userPropertyService.findByExample(example);
        if (emails.isEmpty()) {
            bean.setEmail("");
        } else {
            if (StringUtils.isNotBlank(emails.get(0).getValue())) {
                bean.setEmail(emails.get(0).getValue());
            }
        }

        return bean;
    }

    /**
     * 通过部门路径获得部门
     *
     * @param detppath
     * @return
     */
    private Department getDepartmentByPath(String detppath) {
        List<Department> deptList = this.dao.findBy(Department.class, "path", detppath);
        if (!deptList.isEmpty()) {
            return deptList.get(0);
        }
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#saveBean(com.wellsoft.pt.org.bean.UserBean)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(UserBean bean) {
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        String oldOupath = getUserOuPath(bean.getId());
        HashMap<String, String> adusermap = new HashMap<String, String>();
        adusermap.put("oldoupath", oldOupath);
        adusermap.put("email", bean.getMainEmail());
        String oldLoginName = ""; // 获取已存在的旧用户登录名
        if (!StringUtils.isEmpty(bean.getId()) && getById(bean.getId()) != null) { // 判断id是否为空
            oldLoginName = getById(bean.getId()).getLoginName();
        }
        adusermap.put("oldLoginName", oldLoginName);

        if (!bean.getIssys()) {
            bean.setIssys(false);
        }
        User user = new User();
        String notMd5Pwd = null;
        // Boolean isUploadToTenant = true;
        StringBuilder requiredException = new StringBuilder();
        if (StringUtils.isBlank(bean.getLoginName())) {
            requiredException.append("、登录名");
        }
        if (StringUtils.isBlank(bean.getPassword())) {
            requiredException.append("、密码");
        }
        if (StringUtils.isBlank(bean.getUserName())) {
            requiredException.append("、姓名");
        }
        if (StringUtils.isBlank(bean.getMajorJobName())) {
            requiredException.append("、职位");
        }
        if (StringUtils.isBlank(bean.getDepartmentName())) {
            requiredException.append("、所属部门");
        }
        if (requiredException.length() > 0) {
            requiredException.append("不能为空，请填入相应信息！");
            throw new RuntimeException(requiredException.toString().replaceFirst("、", ""));
        }
        // 判断登录名是否存在空格 修改者yuyq by2014-12-03
        if (bean.getLoginName().indexOf(" ") > -1) {
            throw new RuntimeException("登录名存在空格，请检查！");
        }
        // System.out.println("bean------------------"+bean.getLoginName());
        // 保存新user 设置id值
        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            notMd5Pwd = bean.getPassword();
            user.setLoginName(bean.getLoginName());

            // 用户登录名唯一性验证
            User systemUser = getByLoginNameIgnoreCase(bean.getLoginName(), null);
            if (StringUtils.isNotBlank(bean.getLoginName()) && systemUser != null) {
                if (systemUser.getTenantId().equals(user.getTenantId()))
                    throw new RuntimeException(
                            "登录名[" + bean.getLoginName() + "],已经被引用，请修改对应的登录名后保存!");
                else
                    throw new RuntimeException(
                            "登录名需全局唯一[" + bean.getLoginName() + "]已经存在于其他租户中，请修改后保存!");
            }
            // //判断是否要全局性唯一
            // Boolean isUnique = OrgUtil.isLoginNameUniqueInGlobal();
            // //全局性唯一要到公共库做校验
            // if (isUnique) {
            // List<TenantUser> tenantUsers =
            // tenantUserService.getTeantUserByLoginName(bean.getLoginName());
            // if (tenantUsers.size() > 0) {
            // throw new RuntimeException("已经存在登录名为[" + bean.getLoginName() +
            // "]的用户!");
            // }
            // }
            String id = idGeneratorService.generate(User.class, USER_ID_PATTERN);
            String tenantId = userDetail.getTenantId();
            // TODO 用户ID限定11位（U+4位租户ID+6位用户ID）
            bean.setId(
                    id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4,
                            11));
            bean.setTenantId(tenantId);
            // 调用邮件接口创建邮件用户
            if (OrgUtil.isSyncMailAccount()) {
                // mailApiFacade.addMailUser(bean.getUserName(),
                // bean.getLoginName(), bean.getPassword());
            }

            // 加密密码
            bean.setPassword(passwordEncoder.encodePassword(bean.getPassword(), getSalt(bean)));

        } else {
            user = this.get(bean.getUuid());
            // 加密密码
            if (!StringUtils.equals(user.getPassword(), bean.getPassword())) {
                notMd5Pwd = bean.getPassword();
                if (OrgUtil.isSyncMailAccount()) {
                    // // 调用邮件接口创建邮件用户
                    // mailApiFacade.addMailUser(bean.getUserName(),
                    // bean.getLoginName(), notMd5Pwd);
                    // // 调用邮件接口更改密码
                    // mailApiFacade.alterMailUserPass(bean.getLoginName(),
                    // notMd5Pwd);
                }

                // 加密密码
                bean.setPassword(passwordEncoder.encodePassword(notMd5Pwd, getSalt(bean)));
            }
            // 判断是否要全局性唯一
            // Boolean isUnique = OrgUtil.isLoginNameUniqueInGlobal();
            // 新增是否挂职的判断，如果不是当前租户的才进行全局校验和同步，挂职不做处理。
            // String id = user.getId();
            // String tenantId = id.substring(0, 4).replace("U", "T");
            // String currentTenantId =
            // SpringSecurityUtils.getCurrentTenantId();
            // isUploadToTenant = tenantId.equals(currentTenantId);
            // //全局性唯一要到公共库做校验//当前租户ID和用户ID的格式化租户ID不一致，证明是挂职进来的人员无需加判断
            // if (isUnique && isUploadToTenant) {
            // List<TenantUser> tenantUsers =
            // tenantUserService.getTeantUserByLoginName(bean.getLoginName());
            // if (tenantUsers.size() > 0 &&
            // !user.getUuid().equals(tenantUsers.get(0).getUserUuid())) {
            // throw new RuntimeException("已经存在登录名为[" + bean.getLoginName() +
            // "]的用户!");
            // }
            // }
        }
        BeanUtils.copyProperties(bean, user);

        user.setTenantId(userDetail.getTenantId());

        // 记录登录名、密码MD5算法信息
        BasePasswordEncoder basePasswordEncoder = PasswordEncoderFactory.getPasswordEncoder(
                PasswordAlgorithm.MD5
                        .getValue());
        String loginNameHashValue = basePasswordEncoder.encodePassword(user.getLoginName(),
                user.getLoginName());
        user.setLoginNameHashAlgorithm(PasswordAlgorithm.MD5.getValue());
        user.setLoginNameHashValue(loginNameHashValue);
        user.setPasswordHashAlgorithm(PasswordAlgorithm.MD5.getValue());

        this.dao.save(user);

        // 添加或更新邮箱账号
        if (OrgUtil.isSyncMailAccount() && StringUtils.isNotBlank(notMd5Pwd)) {
            //TODO:发布添加账号的事件
        }

        // TODO 遍历用户所在的部门，如果用户部门有挂接单位，则同步更新集团通讯录
        // for (DepartmentUserJob deptUser : user.getDepartmentUsers()) {
        // Department department = deptUser.getDepartment();
        // this.updateCommonUser(department, user, "2", department.getId());
        // }
        // 删除用户所属部门
        departmentUserJobService.deleteByUser(user.getUuid());

        // 设置用户上级领导
        String leaderIdString = bean.getLeaderIds();
        if (StringUtils.isNotBlank(leaderIdString)) {
            // 删除领导
            userLeaderDao.deleteByUser(user.getUuid());

            // 新的领导
            String[] leaderIds = leaderIdString.split(Separator.SEMICOLON.getValue());
            List<UserLeader> newLeaders = new ArrayList<UserLeader>();
            for (String leaderId : leaderIds) {
                User leader = this.getById(leaderId);
                UserLeader userLeader = new UserLeader();
                userLeader.setUser(user);
                userLeader.setLeader(leader);
                newLeaders.add(userLeader);
            }
            // 更新
            for (UserLeader entity : newLeaders) {
                userLeaderDao.save(entity);
            }
        } else {
            // 删除用户上级领导
            userLeaderDao.deleteByUser(user.getUuid());
        }

        StringBuilder departmentIds = new StringBuilder();
        StringBuilder departmentNames = new StringBuilder();

        String majorJobId = bean.getMajorJobId();
        if (StringUtils.isEmpty(majorJobId)) {
            throw new RuntimeException("主职位不能为空!");
        }
        String majorDeptId = null;
        if (StringUtils.isNotBlank(majorJobId)) {
            userJobDao.deleteMajorByUser(user.getUuid());
            Job job = jobService.getById(majorJobId);
            if (job != null) {
                UserJob userJob = new UserJob();
                userJob.setUser(user);
                userJob.setJob(job);
                userJob.setIsMajor(true);
                // 设置主职位name
                user.setMajorJobName(job.getDepartmentName() + "/" + job.getName());
                // 更新
                userJobDao.save(userJob);

                Department dept = getDepartmentByPath(job.getDepartmentName());
                majorDeptId = dept.getId();
                departmentIds.append(dept.getId());
                departmentNames.append(job.getDepartmentName());
            }
        } else {
            userJobDao.deleteMajorByUser(user.getUuid());
        }

        // 设置用户其他职位信息 add by zky 20140825
        HashMap<String, String> deptIdPath = new HashMap<String, String>();
        String otherJobIdString = bean.getOtherJobIds();
        String otherJobNames = "";
        int otherJobCount = 0;
        if (StringUtils.isNotBlank(otherJobIdString)) {
            departmentIds.append(Separator.SEMICOLON.getValue());
            departmentNames.append(Separator.SEMICOLON.getValue());
            userJobDao.deleteOtherByUser(user.getUuid());
            String[] otherjobids = otherJobIdString.split(Separator.SEMICOLON.getValue());
            List<UserJob> newUserJobs = new ArrayList<UserJob>();
            for (String jobid : otherjobids) {
                Job job = jobService.getById(jobid);
                UserJob userJob = new UserJob();
                userJob.setUser(user);
                userJob.setJob(job);
                userJob.setIsMajor(false);
                newUserJobs.add(userJob);
                Department dept = getDepartmentByPath(job.getDepartmentName());
                if (!dept.getId().equals(majorDeptId) && !deptIdPath.containsKey(dept.getId())) {
                    deptIdPath.put(dept.getId(), job.getDepartmentName());
                }
                if (otherJobCount == otherjobids.length - 1) {
                    otherJobNames = otherJobNames + job.getDepartmentName() + "/" + job.getName();
                } else {
                    otherJobNames = otherJobNames + job.getDepartmentName() + "/" + job.getName() + ";";
                }
                otherJobCount++;

            }
            // 更新
            for (UserJob entity : newUserJobs) {
                userJobDao.save(entity);
            }
            user.setOtherJobNames(otherJobNames);

        } else {
            userJobDao.deleteOtherByUser(user.getUuid());
        }

        int deptidcount = 0;
        for (String deptId : deptIdPath.keySet()) {
            if (deptidcount == deptIdPath.size() - 1) {
                departmentIds.append(deptId);
                departmentNames.append(deptIdPath.get(deptId));
            } else {
                departmentIds.append(deptId).append(Separator.SEMICOLON.getValue());
                departmentNames.append(deptIdPath.get(deptId)).append(
                        Separator.SEMICOLON.getValue());
            }
            deptidcount++;
        }

        bean.setDepartmentId(departmentIds.toString());
        bean.setDepartmentName(departmentNames.toString());
        if (StringUtils.isNotBlank(bean.getDepartmentName())) {
            user.setDepartmentName(bean.getDepartmentName());
        }
        // 1、设置用户所属部门
        String deptIdString = bean.getDepartmentId();
        if (StringUtils.isNotBlank(deptIdString)) {
            String[] dpetIds = deptIdString.split(Separator.SEMICOLON.getValue());
            int i = 0;
            for (String deptId : dpetIds) {
                Department department = this.departmentService.getById(deptId);
                if (i == 0) {
                }
                DepartmentUserJob departmentUser = new DepartmentUserJob();
                departmentUser.setUser(user);
                departmentUser.setDepartment(department);
                departmentUser.setIsMajor(i == 0);
                departmentUser.setTenantId(SpringSecurityUtils.getCurrentTenantId());
                departmentUser.setJobCode(bean.getJobCode());
                departmentUser.setJobName(bean.getJobName());
                this.dao.save(departmentUser);
                i++;
                // TODO 如果是启用，则同步更新集团通讯录
                if (bean.getEnabled()) {
                    // this.updateCommonUser(department, user, "1",
                    // department.getId());
                }
            }
        }

        // 3、设置职务代理人
        String deputyIdString = bean.getDeputyIds();
        if (StringUtils.isNotBlank(deputyIdString)) {
            // 删除职务代理人
            userDeputyDao.deleteByUser(user.getUuid());

            // 新的职务代理人
            String[] minorJobIds = deputyIdString.split(Separator.SEMICOLON.getValue());
            List<UserDeputy> newDeputies = new ArrayList<UserDeputy>();
            for (String minorJobId : minorJobIds) {
                User deputy = this.getById(minorJobId);
                UserDeputy userDeputy = new UserDeputy();
                userDeputy.setUser(user);
                userDeputy.setDeputy(deputy);
                newDeputies.add(userDeputy);
            }
            // 更新
            for (UserDeputy entity : newDeputies) {
                userDeputyDao.save(entity);
            }
        } else {
            // 删除职务代理人
            userDeputyDao.deleteByUser(user.getUuid());
        }

        // 增加用户所属群组
        // Set<Group> groups = user.getGroups();
        // for (Group group : groups) {
        // // 先删除
        // group.getUsers().remove(user);
        // }
        // String groupIdString = bean.getGroupIds();
        // if (groupIdString != null && !groupIdString.equals("")) {
        // String[] groupIds =
        // groupIdString.split(Separator.SEMICOLON.getValue());
        // for (String groupId : groupIds) {
        // Group g = this.groupService.getById(groupId);
        // g.getUsers().add(user);
        // groups.add(g);
        // }
        // }

        // 4.1、设置用户角色信息
        // System.out.println( user.getRoles() == bean.getRoles());

        // 若当前登录人为部门管理员且所保存用户也为该部门管理员本身的话，不用操作权限
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        // if (!(departmentAdminService.isDepartmentAdmin(currentUserId) &&
        // user.getId().equals(currentUserId))) {
        // roles.clear();

        // user.setRoles(new HashSet<Role>());
        // for (Role role : bean.getRoles()) {
        // Role tmp = this.roleDao.get(role.getUuid());
        // if (tmp != null) {
        // user.getRoles().add(tmp);
        // }
        // }

//        List<UserRole> userRoles = userRoleService.getUserRoleByUserUuid(user.getUuid());
//        if (userRoles.size() > 0) {
//            for (UserRole userRole : userRoles)
//                userRoleService.deleteUserRoleByUserUuidAndRoleUuid(user.getUuid(),
//                        userRole.getUserRoleId()
//                                .getRoleUuid());
//        }
//        for (Role role : bean.getRoles()) {
//            Role tmp = this.roleFacadeService.get(role.getUuid());
//            if (tmp != null) {
//                UserRole userRole = new UserRole();
//                UserRoleId userRoleId = new UserRoleId(user.getUuid(), tmp.getUuid(),
//                        user.getTenantId());
//                userRole.setUserRoleId(userRoleId);
//                this.userRoleService.save(userRole);
//            }
//        }

        // }

        // 4.2、设置用户权限信息

        // 若当前登录人为部门管理员且所保存用户也为该部门管理员本身的话，不用操作权限
        // if (!(departmentAdminService.isDepartmentAdmin(currentUserId) &&
        // user.getId().equals(currentUserId))) {
        // privileges.clear();
        // user.setPrivileges(new HashSet<Privilege>());
        for (Privilege privilege : bean.getPrivileges()) {

            Privilege tmp = this.privilegeFacadeService.get(privilege.getUuid());
            if (tmp != null) {
                UserPrivilege userPrivilege = new UserPrivilege();
                UserPrivilegeId userPrivilegeId = new UserPrivilegeId(user.getUuid(), tmp.getUuid(),
                        user.getTenantId());
                userPrivilege.setUserPrivilegeId(userPrivilegeId);
                this.userPrivilegeService.save(userPrivilege);
            }
        }

        // }

        // 5、保存用户拼音信息，用于拼音搜索
        pinyinService.deleteByEntityUuid(user.getUuid());
        Set<Pinyin> userPinyins = getUserPinyin(user);
        for (Pinyin userPinyin : userPinyins) {
            pinyinService.save(userPinyin);
        }

        // 6、 保存用户扩展属性
        // 6.1 只能以证书登录

        UserProperty example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_ONLY_LOGON_CERTIFICATE);
        List<UserProperty> userProperties = userPropertyService.findByExample(example);
        UserProperty userProperty = null;
        if (userProperties.isEmpty()) {
            userProperty = new UserProperty();
            userProperty.setUserUuid(user.getUuid());
            userProperty.setName(UserProperty.KEY_ONLY_LOGON_CERTIFICATE);
        } else {
            userProperty = userProperties.get(0);
        }
        userPropertyService.save(userProperty);
        // 6.2 用户证书主体
        example = new UserProperty();
        if (user.getOnlyLogonWidthCertificate() != null && user.getOnlyLogonWidthCertificate()) {
            example.setUserUuid(user.getUuid());
            example.setName(UserProperty.KEY_CERTIFICATE_SUBJECT_DN);
            List<UserProperty> subjectDNProperties = userPropertyService.findByExample(example);
            UserProperty subjectDNProperty = null;

            if (subjectDNProperties.isEmpty()) {
                subjectDNProperty = new UserProperty();
                subjectDNProperty.setUserUuid(user.getUuid());
                subjectDNProperty.setName(UserProperty.KEY_CERTIFICATE_SUBJECT_DN);
            } else {
                subjectDNProperty = subjectDNProperties.get(0);
            }
            subjectDNProperty.setValue(bean.getSubjectDN() + "");
            userPropertyService.save(subjectDNProperty);
            // 证书主体唯一性确认

            example = new UserProperty();
            example.setValue(subjectDNProperty.getValue());
            List<UserProperty> subjectDNs = userPropertyService.findByExample(example);
            if (subjectDNs.size() > 1) {
                throw new RuntimeException("证书主体[" + subjectDNProperty.getValue() + "]已经存在!");
            }
        }
        // 6.3 接收短信消息
        example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_RECEIVE_SMS_MESSAGE);
        List<UserProperty> receiveSmsMessageProperties = userPropertyService.findByExample(example);
        UserProperty receiveSmsMessageProperty = null;
        if (receiveSmsMessageProperties.isEmpty()) {
            receiveSmsMessageProperty = new UserProperty();
            receiveSmsMessageProperty.setUserUuid(user.getUuid());
            receiveSmsMessageProperty.setName(UserProperty.KEY_RECEIVE_SMS_MESSAGE);
        } else {
            receiveSmsMessageProperty = receiveSmsMessageProperties.get(0);
        }
        receiveSmsMessageProperty.setValue(bean.isReceiveSmsMessage() + "");
        userPropertyService.save(receiveSmsMessageProperty);

        // 6.4保存用户邮件信息
        example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_EMAIL);
        List<UserProperty> emailProperties = userPropertyService.findByExample(example);
        UserProperty emailProperty = null;
        if (emailProperties.isEmpty()) {
            emailProperty = new UserProperty();
            emailProperty.setUserUuid(user.getUuid());
            emailProperty.setName(UserProperty.KEY_EMAIL);
        } else {
            emailProperty = emailProperties.get(0);
        }
        emailProperty.setValue(bean.getEmail());
        userPropertyService.save(emailProperty);

        // 7、文件上到mongondb
        String photpUuid = user.getPhotoUuid();
        if (StringUtils.isNotBlank(photpUuid)) {
            mongoFileService.pushFileToFolder(user.getUuid(), photpUuid, "photpUuid");
        }

        //contactApiFacade.synchronousOrgData(user.getUuid());

        // 8、AD同步
        try {
            if (OrgUtil.isAdSync()) {
                synchronousADUser(user, notMd5Pwd, adusermap);
            }
        } catch (Exception e) {
            logger.error("用户AD保存同步错误:", e);
        }
        // 同步公共库
        // if (isUploadToTenant) {
        // tenantUserService.saveTeantUserByOragUser(user);
        // }
    }

    /**
     * 根据用户获得用户的OU部门路径
     *
     * @return
     */
    private String getUserOuPath(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        Job job = getMajorJobByUserId(id);
        if (job == null) {
            return null;
        }
        String sdeptPath = job.getDepartmentName();
        if (sdeptPath.indexOf("/") < 0) {
            return "";// 返回根部门.
        }
        String ouPath = sdeptPath.substring(sdeptPath.indexOf("/") + 1, sdeptPath.length());
        return ouPath;
    }

    /**
     * 获得用户主部门
     *
     * @param id
     * @return
     */
    private Department getMajorDepartmentByUserId(User user) {
        if (user == null) {
            return null;
        }
        Set<DepartmentUserJob> DepartmentUserJobs = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : DepartmentUserJobs) {
            if (departmentUserJob.getIsMajor()) {
                return departmentUserJob.getDepartment();
            }
        }
        return null;
    }

    /**
     * 同步用户到AD
     * liyb 2015.01.16
     *
     * @param user
     * @param pwd
     * @param adusermap
     * @throws Exception
     */
    private void synchronousADUser(User user, String pwd,
                                   HashMap<String, String> adusermap) throws Exception {
        if (!OrgUtil.isAdSync()) {
            return;
        }
        String fullname = user.getUserName(); // 全名
        String familyName = fullname.substring(0, 1); // 姓氏
        String givenName = fullname.substring(1, fullname.length()); // 名

        String ouNewPath = getUserOuPath(user.getId());
        String oldLoginName = adusermap.get("oldLoginName"); // 获取未改前的用户登录名
        String oldOupath = adusermap.get("oldoupath");

        // 判断用户修改到的部门是否存在，如果不存在(返回false-->return)，
        if (!adDeptService.checkIsExistByPath(ouNewPath)) {
            return;
        }
        ADUser adUser = new ADUser();
        if (fullname.length() >= 4) {
            familyName = fullname.substring(0, 2);
            givenName = fullname.substring(2, fullname.length());
        }
        String loginName = user.getLoginName();
        adUser.setCn(fullname);
        adUser.setSn(familyName);
        adUser.setDeptPath(ouNewPath.split("/"));
        adUser.setDisplayName(fullname);
        adUser.setGivenName(givenName);
        adUser.setMail(adusermap.get("email"));
        adUser.setName(fullname);
        adUser.setPwd(pwd);
        adUser.setsAMAccountName(loginName);
        adUser.setUserPrincipalName(loginName + "@" + OrgUtil.getAdPrincipalnameSuffix());
        adUser.setHomePhone(user.getHomePhone());
        adUser.setFacsimileTelephoneNumber(user.getFax());
        adUser.setMobile(user.getMobilePhone());
        adUser.setTelephoneNumber(user.getOfficePhone());
        adUser.setEnabled(user.getEnabled());
        if (StringUtils.isEmpty(user.getHomePhone())) {
            adUser.setHomePhone(null);
        }
        if (StringUtils.isEmpty(user.getFax())) {
            adUser.setFacsimileTelephoneNumber(null);
        }
        if (StringUtils.isEmpty(user.getMobilePhone())) {
            adUser.setMobile(null);
        }
        if (StringUtils.isEmpty(user.getOfficePhone())) {
            adUser.setTelephoneNumber(null);
        }
        if (StringUtils.isEmpty(adusermap.get("email"))) {
            adUser.setMail(null);
        }

        // 通过登录名获取未改前的用户AD路径
        String userDn = adUserService.getDnByLoginName(oldLoginName);
        if (!StringUtils.isEmpty(userDn)) {
            String newDn = adUserService.getDnByAdUser(adUser);
            String realdydn = userDn.replace("," + OrgUtil.getAdBase(), ""); // 先重命名CN
            adUserService.renameDn(realdydn, newDn); // 先修改路径
            adUserService.update(adUser); // 再更新对应的值
            if (!ouNewPath.equals(oldOupath)) { // 当部门路径改变时，才会执行
                // 先删除，后添加
                removerUserFromGroup(adUser, oldOupath);
                addMemberToGroupByUser(adUser, user);
            }
        } else {
            adUserService.add(adUser);
            if (ouNewPath == "") { // 当为根部门时，不用添加组
                return;
            }
            // 把成员添加到通讯组和安全组中
            addMemberToGroupByUser(adUser, user);
        }
    }

    /**
     * 删除修改前所在的组里面的自己
     *
     * @param adUser
     * @param oldOupath
     * @throws Exception
     * @author liyb
     */
    protected void removerUserFromGroup(ADUser adUser, String oldOupath) throws Exception {
        String memberDn = adUserService.getDnByAdUser(adUser); // 获得新用户DN
        memberDn = memberDn + "," + OrgUtil.getAdBase();
        for (int i = 0; i <= 1; i++) {
            ADGroup group = new ADGroup();
            String grouptName = oldOupath.replaceAll("/", "");
            if (i == 0) {
                grouptName = grouptName + "-通讯组";
                group.setCommunicationGroup(true); // true为通讯组
            } else if (i == 1) {
                grouptName = grouptName + "-安全组";
                group.setCommunicationGroup(false); // false安全组
            }
            group.setName(grouptName);
            group.setCn(grouptName);
            group.setsAMAccountName(grouptName);
            group.setDeptPath(oldOupath.split("/"));

            if (!adGroupService.checkIsExist(group)) { // 判断本部门是否存在组
                continue;
            }
            adGroupService.removerMemberFromGroup(memberDn, adGroupService.getDnByAdGroup(group));
        }
    }

    /**
     * 新建用户时（或修改用户时，把用户添加到修改后的部门的组中），把用户添加到相应的组中
     *
     * @param adUser
     * @param user
     * @throws Exception
     * @author liyb
     */
    protected void addMemberToGroupByUser(ADUser adUser, User user) throws Exception {
        String memberDn = adUserService.getDnByAdUser(adUser); // 获得新用户DN
        memberDn = memberDn + "," + OrgUtil.getAdBase();
        for (int i = 0; i <= 1; i++) {
            ADGroup group = new ADGroup();
            String deptPath = getUserOuPath(user.getId()); // 获取部门路径（无最顶级）
            String grouptName = deptPath.replaceAll("/", "");
            if (i == 0) {
                grouptName = grouptName + "-通讯组";
                group.setCommunicationGroup(true); // true为通讯组
            } else if (i == 1) {
                grouptName = grouptName + "-安全组";
                group.setCommunicationGroup(false); // false安全组
            }
            group.setName(grouptName);
            group.setCn(grouptName);
            group.setsAMAccountName(grouptName);
            group.setDeptPath(deptPath.split("/"));

            if (!adGroupService.checkIsExist(group)) { // 判断本部门是否存在组
                continue;
            }
            adGroupService.addMemberToGroup(memberDn, adGroupService.getDnByAdGroup(group));
        }
    }

    /**
     * 同步删除AD用户
     *
     * @param user
     * @param sdeptPath
     */
    private void deleteADUser(User user) throws Exception {
        if (!OrgUtil.isAdSync()) {
            return;
        }
        // 获得主部门路径，并删除
        ADUser adUser = getDNUser(user);
        if (adUser == null) {
            return;
        }
        adUserService.delete(adUser);
    }

    /**
     * 修改AD用户密码
     *
     * @param user
     */
    private void changeADUserPwd(User user, String newPwd) throws Exception {
        if (!OrgUtil.isAdSync()) {
            return;
        }
        String userDn = adUserService.getDnByLoginName(user.getLoginName());
        if (StringUtils.isEmpty(userDn)) {
            return;
        }
        String realdydn = userDn.replace("," + OrgUtil.getAdBase(), "");
        adUserService.changePwdByDn(realdydn, newPwd);
    }

    /**
     * 构建DNuser
     *
     * @param user
     * @return
     */
    private ADUser getDNUser(User user) {
        ADUser adUser = new ADUser();
        adUser.setCn(user.getUserName());
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        String majorPath = "";
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            if (departmentUserJob.getIsMajor()) {
                majorPath = departmentUserJob.getDepartment().getPath();
                break;
            }
        }
        // 如果是顶级，先做返回处理
        if (majorPath.indexOf("/") < 1) {
            return null;
        }
        String ouPath = majorPath.substring(majorPath.indexOf("/") + 1, majorPath.length());
        adUser.setDeptPath(ouPath.split("/"));
        return adUser;
    }

    /**
     * 获取用户的所有用户拼音实体
     *
     * @param user
     * @return
     */
    private Set<Pinyin> getUserPinyin(User user) {
        Set<String> pinyins = new HashSet<String>();
        String userUuid = user.getUuid();
        String loginName = user.getLoginName();
        String userName = user.getUserName();
        pinyins.add(PinyinUtil.getPinYin(loginName));
        pinyins.add(PinyinUtil.getPinYinHeadChar(loginName));
        pinyins.add(PinyinUtil.getPinYin(userName));
        pinyins.add(PinyinUtil.getPinYinHeadChar(userName));

        Set<Pinyin> userPinyins = new HashSet<Pinyin>();
        for (String pinyin : pinyins) {
            Pinyin userPinyin = new Pinyin();
            userPinyin.setType(User.class.getSimpleName());
            userPinyin.setEntityUuid(userUuid);
            userPinyin.setPinyin(pinyin);
            userPinyins.add(userPinyin);
        }
        return userPinyins;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#deleteById(java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteById(String id) {
        User user = this.getById(id);
        if (user == null) {
            return;
        }

        // if (Boolean.TRUE.equals(user.getIssys())) {
        // throw new RuntimeException("用户[" + user.getUserName() +
        // "]是管理员账号, 不能删除!");
        // }

        // 1、删除用户与用户组多对多关系中作为被控方的关系表
        // Set<Group> groups = user.getGroups();
        // for (Group group : groups) {
        // group.getUsers().remove(user);
        // }
        // 2、删除用户与组织单元多对多关系中作为被控方的关系表
        // Set<Unit> units = user.getUnits();
        // for (Unit unit : units) {
        // unit.getUsers().remove(user);
        // }
        // 3、删除作为领导者的记录
        this.userLeaderDao.deleteLeader(user.getUuid());
        // 4、删除作为职务代理人的记录
        this.userDeputyDao.deleteDeputy(user.getUuid());
        // 5、删除用户拼音信息
        pinyinService.deleteByEntityUuid(user.getUuid());

        // 6、删除用户邮箱
        if (OrgUtil.isSyncMailAccount()) {

        }

        // 7、删除部门相关负责人，包括负责人、分管领导、管理员
        departmentPrincipalService.deleteByUserUuid(user.getUuid());

        departmentPrincipalService.deleteByUserId(user.getId());

        // TODO 遍历用户所在的部门，如果用户部门有挂接单位，则同步更新集团通讯录
        // for (DepartmentUserJob deptUser : user.getDepartmentUsers()) {
        // Department department = deptUser.getDepartment();
        // this.updateCommonUser(department, user, "2", department.getId());
        // }

        // 8、删除用户属性
        userPropertyService.deleteByUser(user.getUuid());
        // 删除公共库的用户
        // String tenantId = getTenantId(user.getId());
        // String currentTenantId = SpringSecurityUtils.getCurrentTenantId();
        // 公用租户废弃
        // if (StringUtils.equals(tenantId, currentTenantId)) {
        // tenantUserService.deleteByUserUuid(user.getUuid());
        // }

        // 9删除用户
        // user.setAccountNonExpired(false);
        // user.setAccountNonLocked(false);
        // this.userDao.save(user);

        // 10AD同步 删除用户如果AD有限制，则报错了.
        try {
            deleteADUser(user);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            logger.error("用户AD删除同步错误:" + e);
        }
        // 11、删除挂职用户
        tenantGzUserService.deleteById(user.getId(), user.getTenantId());

        this.dao.delete(user);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#deleteByIds(java.util.Collection)
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
     * @see com.wellsoft.pt.org.service.UserService#getTopDepartment(java.lang.String)
     */
    @Override
    public Department getTopDepartment(String uuid) {
        DepartmentUserJob departmentUser = departmentUserJobService.getMajor(uuid);
        Department department = null;
        if (departmentUser != null) {
            department = departmentService.getTopDepartment(
                    departmentUser.getDepartment().getUuid());
        }
        return department;
    }

    /**
     * 根据用户UUID加载角色树，自动选择已有角色
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getRoleTree()
     */
    @Override
    public TreeNode getRoleTree(String uuid) {
        String userUuid = uuid;
        if (uuid.contains(",add")) {
            uuid = uuid.substring(0, uuid.lastIndexOf(","));
        }
        User user = this.get(uuid);
        TreeNode treeNode = new TreeNode();
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        User admin = this.getById(currentUserId);
        List<String> departmentUuidList = departmentAdminService.getDepartmentUuidListByUserId(
                admin.getId());
        Department currentClickDepartment = departmentAdminService.getMajorDepartmentByUserId(
                user.getId());// 当前点击用户所在的主部门
        /*
         * //判断当前登录人是否为部门管理员 if
         * (departmentAdminService.isDepartmentAdmin(currentUserId)) { Set<Role>
         * commonRoles = new HashSet<Role>(); Set<Role> userRoles =
         * admin.getRoles();//管理员用户角色 Set<Role> departmentRoles = new
         * HashSet<Role>(); for (String departmentUuid : departmentUuidList) {
         * Department currentDept = departmentDao.get(departmentUuid);// if
         * (currentClickDepartment.getPath().contains(currentDept.getPath())) {
         * departmentRoles = currentDept.getRoles();//当前用户所在部门角色 } }
         *
         * commonRoles.clear(); commonRoles.addAll(userRoles);
         * commonRoles.addAll(departmentRoles); //判断当前点击用户是部门管理员或该部门其他人员 if
         * (user.getId().equals(currentUserId)) {//部门管理员
         *
         * treeNode.setName("角色列表"); treeNode.setId(TreeNode.ROOT_ID);
         * treeNode.setNocheck(true); List<TreeNode> children = new
         * ArrayList<TreeNode>(); List<Role> roles = roleDao.getAll(); for (Role
         * role : roles) { if (commonRoles.contains(role)) { TreeNode child =
         * new TreeNode(); child.setId(role.getUuid());
         * child.setName(role.getName()); if (userUuid.contains(",add")) {
         * child.setChecked(commonRoles.contains(role)); } else {
         * child.setNocheck(true); } children.add(child); } }
         * treeNode.setChildren(children); } else {//非部门管理员 Department
         * isNotAdminMajorDepartment =
         * departmentAdminService.getMajorDepartmentByUserId
         * (user.getId());//获取该用户主部门 Set<Role> isNotAdminCommonRoles = new
         * HashSet<Role>(); Set<Role> isNotAdminUserRoles =
         * user.getRoles();//该用户角色 Set<Role> isNotAdminDepartmentRoles =
         * isNotAdminMajorDepartment.getRoles();//该用户所在部门角色
         *
         * isNotAdminCommonRoles.clear();
         * isNotAdminCommonRoles.addAll(isNotAdminUserRoles);
         * isNotAdminCommonRoles.addAll(isNotAdminDepartmentRoles);
         *
         * treeNode.setName("角色列表"); treeNode.setId(TreeNode.ROOT_ID);
         * treeNode.setNocheck(true); List<TreeNode> children = new
         * ArrayList<TreeNode>(); for (Role role : commonRoles) { TreeNode child
         * = new TreeNode(); child.setId(role.getUuid());
         * child.setName(role.getName());
         * child.setChecked(isNotAdminCommonRoles.contains(role));
         * children.add(child); } treeNode.setChildren(children); }
         *
         * } else {
         */
        Set<Role> userRoles = this.getRole(user.getUuid());
        List<Role> roles = roleFacadeService.getAll();
        // }
        return OrgUtil.getCategoryRoleTree(dataDictionaryService, userRoles, roles);
    }

    /**
     * 根据UUID加载用户角色嵌套树, 包含角色嵌套及权限
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getUserRoleNestedRoleTree(java.lang.String)
     */
    @Override
    public TreeNode getUserRoleNestedRoleTree(String uuid) {
        User user = this.get(uuid);
        TreeNode treeNode = new TreeNode();
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        // 判断当前登录人是否为部门管理员
        /*
         * if (departmentAdminService.isDepartmentAdmin(currentUserId)) {
         * Department majorDepartment =
         * departmentAdminService.getMajorDepartmentByUserId
         * (user.getId());//获取管理员主部门 Set<Role> commonRoles = new
         * HashSet<Role>(); Set<Role> userRoles =
         * user.getRoles();//系统管理员设置的该管理员用户角色 Set<Role> departmentRoles =
         * majorDepartment.getRoles();//系统管理员设置的该管理员所在部门角色
         *
         * commonRoles.clear(); commonRoles.addAll(userRoles);
         * commonRoles.addAll(departmentRoles);
         *
         * treeNode.setName(user.getUserName());
         * treeNode.setId(TreeNode.ROOT_ID); List<TreeNode> children = new
         * ArrayList<TreeNode>(); for (Role role : commonRoles) { TreeNode child
         * = new TreeNode(); child.setId(role.getUuid());
         * child.setName(role.getName()); children.add(child);
         * this.roleService.buildRoleNestedRoleTree(child, role); }
         * treeNode.setChildren(children);
         *
         * } else { treeNode.setName(user.getUserName());
         * treeNode.setId(TreeNode.ROOT_ID); Set<Role> roles = user.getRoles();
         * List<TreeNode> children = new ArrayList<TreeNode>(); for (Role role :
         * roles) { TreeNode child = new TreeNode();
         * child.setId(role.getUuid()); child.setName(role.getName());
         * children.add(child); this.roleService.buildRoleNestedRoleTree(child,
         * role); } treeNode.setChildren(children); }
         */

        treeNode.setName(user.getUserName());
        treeNode.setId(TreeNode.ROOT_ID);
        Set<Role> roles = getUserOrgRoles(user);
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Role role : roles) {
            TreeNode child = new TreeNode();
            child.setId(role.getUuid());
            child.setName(role.getName());
            children.add(child);
            this.roleFacadeService.buildRoleNestedRoleTree(child, role);
        }
        treeNode.setChildren(children);
        return treeNode;
    }

    /**
     * (non-Javadoc)
     * @see com.wellsoft.pt.org.service.UserService#getUserNamesByIds(java.util.Collection)
     */
    // @Override
    // public List<String> getUserNamesByIds(Collection<String> ids) {
    // List<String> usernames = new ArrayList<String>();
    // for (String id : ids) {
    // User user = this.userDao.getById(id);
    // if (user == null) {
    // usernames.add(id);
    // } else {
    // usernames.add(user.getUserName());
    // }
    // }
    // return usernames;
    // }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getSubordinate(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<User> getSubordinate(String uuid) {
        List<User> users = new ArrayList<User>();
        List<UserLeader> userLeaders = userLeaderDao.getLeaders(uuid);
        for (UserLeader userLeader : userLeaders) {
            users.add(userLeader.getUser());
        }
        return BeanUtils.convertCollection(users, User.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.org.service.UserService#getSuperiorLeaderIds(java.lang
     * .String)
     */
    @Override
    public List<String> getUserLeaderIds(String userId) {
        User user = this.getById(userId);
        return getUserLeaderIds(user);
    }

    @Override
    public List<String> getUserLeaderIds(User user) {
        List<String> leaderIds = new ArrayList<String>();
        if (user == null) {
            return leaderIds;
        }
        Set<UserLeader> leaders = user.getLeaders();
        for (UserLeader userLeader : leaders) {
            String leadId = userLeader.getLeader().getId(); // 得到用户领导id
            if (!leadId.equals(user.getId())) {
                leaderIds.add(leadId);
            }
        }
        return leaderIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getAllUserLeaderIds(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getAllUserLeaderIds(String userId) {
        List<String> leaderIds = new ArrayList<String>();
        User user = this.getById(userId);
        traverseAndAddLeaderIds(user, leaderIds);
        return leaderIds;
    }

    /**
     * 根据用户ID获取用户所属的部门ID
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getDepartmentIdsByUserId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getDepartmentIdsByUserId(String userId) {
        List<String> departmentIds = new ArrayList<String>();
        User user = this.getById(userId);
        Set<DepartmentUserJob> departmentUserJobs = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
            departmentIds.add(departmentUserJob.getDepartment().getId());
        }
        return departmentIds;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public Department getDepartmentByUserId(String userId) {
        User user = this.getById(userId);
        if (user == null) {
            return null;
        }
        Set<DepartmentUserJob> departmentUserJobs = user.getDepartmentUsers();
        if (departmentUserJobs.isEmpty()) {
            return null;
        }
        return departmentUserJobs.toArray(new DepartmentUserJob[0])[0].getDepartment();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getParentDepartmentIdByUserId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public String getParentDepartmentIdByUserId(String userId) {
        User user = this.getById(userId);
        // 获取用户所属部门信息
        Set<DepartmentUserJob> departmentUserJobs = user.getDepartmentUsers();
        if (departmentUserJobs.isEmpty()) {
            return null;
        }
        // 获取上级部门
        DepartmentUserJob departmentUserJob = departmentUserJobs.iterator().next();
        Department department = departmentUserJob.getDepartment();
        if (department != null) {
            return department.getId();
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getParentDepartmentIdByUserId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<Department> getParentDepartmentsByUserId(String userId) {
        List<Department> departments = new ArrayList<Department>();
        User user = this.getById(userId);
        // 获取用户所属部门信息
        Set<DepartmentUserJob> departmentUserJobs = user.getDepartmentUsers();
        if (departmentUserJobs.isEmpty()) {
            return departments;
        }
        // 获取上级部门
        Iterator<DepartmentUserJob> it = departmentUserJobs.iterator();
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
     * @see com.wellsoft.pt.org.service.UserService#getRootDepartmentIdByUserId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public String getRootDepartmentIdByUserId(String userId) {
        User user = this.getById(userId);
        // 获取用户所属部门信息
        Set<DepartmentUserJob> departmentUserJobs = user.getDepartmentUsers();
        if (departmentUserJobs.isEmpty()) {
            return null;
        }
        // 获取上级部门
        DepartmentUserJob departmentUserJob = departmentUserJobs.iterator().next();
        Department department = departmentUserJob.getDepartment();
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
     * @see com.wellsoft.pt.org.service.UserService#getAllDepartmentUserIds()
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getAllDepartmentUserIds() {
        List<String> userIds = new ArrayList<String>();
        List<Department> departments = this.departmentService.getTopLevel();
        for (Department department : departments) {
            traverseAndAddUserIds(department, userIds);
        }
        return userIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getAllAdminIds()
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getAllAdminIds() {
        // (QUERY_ALL_ADMIN_ID, new HashMap<String, String>(0),String.class)
        return this.dao.query(QUERY_ALL_ADMIN_ID, null, String.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getByIds(java.util.Collection)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<User> getByIds(Collection<String> ids) {
        List<User> users = new ArrayList<User>();
        for (String id : ids) {
            User userModel = this.getById(id);
            if (userModel == null) {
                // users.add(userModel);
            } else {
                User user = new User();
                BeanUtils.copyProperties(userModel, user);
                users.add(user);
            }
        }
        return users;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getLoginNamesByIds(java.util.Collection)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getLoginNamesByIds(Collection<String> ids) {
        List<String> loginNames = new ArrayList<String>();
        for (String id : ids) {
            User user = this.getById(id);
            if (user == null) {
                loginNames.add(id);
            } else {
                loginNames.add(user.getLoginName());
            }
        }
        return loginNames;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getIdByLoginName(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public String getIdByLoginName(String loginName) {
        User user = this.dao.findUniqueBy(User.class, "loginName", loginName);
        return user == null ? null : user.getId();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getUserProfileByUserId(java.lang.String)
     */
    @Override
    public UserProfile getUserProfileByUserId(String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        List<UserProfile> userProfiles = this.dao.namedQuery("userProfileQuery", values,
                UserProfile.class);

        if (userProfiles.isEmpty()) {
            return new UserProfile();
        }

        UserProfile userProfile = userProfiles.get(0);

        if (StringUtils.isNotBlank(userProfile.getPhotoUuid())) {
            userProfile.setPhotoUrl("/org/user/view/photo/" + userProfile.getPhotoUuid());
        }

        // 允许登录后台
        if (!userProfile.getIsAdmin() && userProfile.getIsAllowedBack()) {
            userProfile.setIsAdmin(true);
        }

        // 获取最后登录时间
        UserLoginLog example = new UserLoginLog();
        example.setUserUuid(userProfile.getUuid());
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setAutoCount(false);
        pagingInfo.setCurrentPage(1);
        pagingInfo.setPageSize(1);
        List<UserLoginLog> userLoginLogs = this.dao.findByExample(example, "createTime desc",
                pagingInfo);
        if (!userLoginLogs.isEmpty()) {
            userProfile.setLastLoginTime(userLoginLogs.get(0).getLoginTime());
        }

        // 获取挂职租户信息
        String currentTenantId = SpringSecurityUtils.getCurrentTenantId();
        List<TenantGzUser> tenantGzUsers = tenantGzUserService.getTenantGzUser(userId,
                currentTenantId);
        List<QueryItem> gzTenants = new ArrayList<QueryItem>();
        for (TenantGzUser tenantGzUser : tenantGzUsers) {
            if (currentTenantId.equals(tenantGzUser.getZzTenantId())) {
                String gzTenantId = tenantGzUser.getGzTenantId();
                String gzTenantName = tenantGzUser.getGzTenantName();
                QueryItem item = new QueryItem();
                item.put("gzTenantId", gzTenantId);
                item.put("gzTenantName", gzTenantName);
                gzTenants.add(item);
            } else {
                // 加入主职
                if (gzTenants.isEmpty()) {
                    QueryItem item = new QueryItem();
                    item.put("gzTenantId", tenantGzUser.getZzTenantId());
                    item.put("gzTenantName", tenantGzUser.getZzTenantName());
                    gzTenants.add(item);
                }
                if (currentTenantId.equals(tenantGzUser.getGzTenantId())) {
                    continue;
                }
                // 加入挂职
                String gzTenantId = tenantGzUser.getGzTenantId();
                String gzTenantName = tenantGzUser.getGzTenantName();
                QueryItem item = new QueryItem();
                item.put("gzTenantId", gzTenantId);
                item.put("gzTenantName", gzTenantName);
                gzTenants.add(item);
            }
        }
        userProfile.setGzTenants(gzTenants);

        return userProfile;
    }

    /**
     * 更新上次登录时间
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#updateLastLoginTime(java.lang.String)
     */
    @Override
    public void updateLastLoginTime(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        values.put("modifyTime", Calendar.getInstance().getTime());
        this.dao.batchExecute(UPDATE_LAST_LOGIN_TIME, values);
    }

    /**
     * 更改用户密码
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#changePassword(java.lang.String, java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public String changePassword(String uuid, String oldPassword, String newPassword) {
        User user = this.get(uuid);
        String tempOldPassword = passwordEncoder.encodePassword(oldPassword, getSalt(user));
        if (!user.getPassword().equals(tempOldPassword)) {
            return "旧密码输入不正确！";
        }
        /*
         * if (!passwordEncoder.isPasswordValid(user.getPassword(), oldPassword,
         * getSalt(user))) { return false; }
         */

        try {
            if (OrgUtil.isAdSync()) {
                // 校验旧密码是否正确
                String userDn = adUserService.getDnByLoginName(user.getLoginName());
                if (StringUtils.isEmpty(userDn)) {
                    return "旧密码输入不正确！";
                }
                if (!adUserService.checkUserPwd(userDn, oldPassword)) {
                    return "密码修改失败！";
                }
            }
            user.setPassword(passwordEncoder.encodePassword(newPassword, getSalt(user)));
            this.dao.save(user);
            if (OrgUtil.isAdSync()) {
                changeADUserPwd(user, newPassword);
            }
        } catch (Exception e) {
            logger.error("用户AD修改密码错误:", e);
        }

        return "success";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#changePasswordById(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean changePasswordById(String userId, String oldPassword, String newPassword) {
        return "success".equals(
                changePassword(this.getById(userId).getUuid(), oldPassword, newPassword));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#resetPasswordById(java.lang.String, java.lang.String)
     */
    @Override
    public boolean resetPasswordById(String userId, String newPassword) {
        User user = this.getById(userId);
        try {
            if (OrgUtil.isAdSync()) {
                // 校验旧密码是否正确
                String userDn = adUserService.getDnByLoginName(user.getLoginName());
                if (StringUtils.isEmpty(userDn)) {
                    return false;
                }
            }
            user.setPassword(passwordEncoder.encodePassword(newPassword, getSalt(user)));
            this.dao.save(user);
            if (OrgUtil.isAdSync()) {
                changeADUserPwd(user, newPassword);
            }
        } catch (Exception e) {
            logger.error("用户AD修改密码错误:", e);
        }

        return true;
    }

    /**
     * 获取用户密码加密的随机盐
     *
     * @param user
     * @return
     */
    private Object getSalt(User user) {
        return user.getLoginName();
    }

    /**
     * 传递一个字符串参数，可以匹配出包含该字符串的所有用户的ID，作为数组返回，没有就返回空；
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getUserIdsLikeName(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getUserIdsLikeName(String rawName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", rawName);
        List<String> ids = this.dao.query(GET_USER_ID_LIKE_NAME, values);
        return ids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#queryByLoginName(java.lang.String)
     */
    @Override
    public List<User> queryByLoginName(String loginName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("loginName", loginName);
        values.put("userName", loginName);
        values.put("pinyin", loginName);
        List<User> dataList = this.dao.namedQuery("userPinyinQuery", values, User.class,
                new PagingInfo(1,
                        Integer.MAX_VALUE));
        return BeanUtils.convertCollection(dataList, User.class);
    }

    /**
     * 更新用户和职位的关系.
     *
     * @param list
     * @param userJobList
     * @return
     */
    @Override
    public HashMap<String, Object> saveUserJobRelactionFromList(List userList, List userJobList) {
        HashMap<String, Object> rsmap = new HashMap<String, Object>();
        int successcount = 0;
        int totalcount = userJobList.size();
        int nullAccount = 0;

        // 校验用户对应的职位信息是否存在职位表中.
        // checkImportUserAndJobRelaction(userJobList);

        // 通过主职位的部门全路径和userid确定唯一性
        HashMap<String, String> userMainDeptPathMap = new HashMap<String, String>();
        HashMap<String, Set<String>> userOtherDeptPathMap = new HashMap<String, Set<String>>();

        // user jobmap
        HashMap<String, String> userMajorJobhMap = new HashMap<String, String>();
        HashMap<String, Set<String>> userOtherJobhMap = new HashMap<String, Set<String>>();

        HashMap<String, String> loginnamemp = new HashMap<String, String>();
        if (userJobList != null && userJobList.size() > 0 && !userJobList.isEmpty()) {
            for (Object o : userJobList) {
                UserJob userjob = (UserJob) o;
                String jobName = userjob.getJobName();
                // 帐号为空的就不导入了
                if (StringUtils.isEmpty(userjob.getUserLoginName())) {
                    continue;
                }
                loginnamemp.put(userjob.getUserLoginName(), userjob.getUserLoginName());
                String userLoginName = userjob.getUserLoginName();
                String deptPath = jobName.substring(0, jobName.lastIndexOf("/"));

                // 主岗
                if (userjob.getIsMajor() == true) {
                    if (userMainDeptPathMap.get(userLoginName) != null) {
                        throw new RuntimeException("用户:" + userLoginName + "不能有两个主岗信息!");
                    }
                    userMainDeptPathMap.put(userLoginName, deptPath);
                    // 构建用户，主职位map
                    userMajorJobhMap.put(userLoginName, jobName);

                    // 其他岗位 其他部门
                } else {
                    if (userOtherDeptPathMap.get(userLoginName) == null) {
                        HashSet<String> otherdeptpath = new HashSet<String>();
                        otherdeptpath.add(deptPath);
                        userOtherDeptPathMap.put(userLoginName, otherdeptpath);
                    } else {
                        userOtherDeptPathMap.get(userLoginName).add(deptPath);
                    }

                    // 构建其他职位map
                    if (userOtherJobhMap.get(userLoginName) == null) {
                        HashSet<String> otherjobname = new HashSet<String>();
                        otherjobname.add(jobName);
                        userOtherJobhMap.put(userLoginName, otherjobname);
                    } else {
                        userOtherJobhMap.get(userLoginName).add(jobName);
                    }
                }
            }
        } else {
            throw new RuntimeException("excel中的用户职位标签内容为空，请检查标签及内容!");
        }

        String checkmsg = "";
        if (userList != null && !userList.isEmpty()) {
            for (Object o : userList) {
                User user1 = (User) o;
                if (StringUtils.isEmpty(user1.getLoginName())) {
                    continue;
                }
                // 判断用户登录名是否存在于职位关联表中
                if (!loginnamemp.containsKey(user1.getLoginName())) {
                    checkmsg = checkmsg + "\n" + user1.getLoginName();
                }

                String departmentName = userMainDeptPathMap.get(user1.getLoginName());
                if (StringUtils.isEmpty(departmentName)) {
                    checkmsg = checkmsg + "\n" + "用户" + user1.getLoginName() + "找不到对应的主职位信息!";
                }
            }
        }

        if (checkmsg != "") {
            throw new RuntimeException(checkmsg);
        }

        if (userList != null && !userList.isEmpty()) {
            for (Object o : userList) {
                User user1 = (User) o;
                if (StringUtils.isEmpty(user1.getLoginName())) {
                    nullAccount = nullAccount + 1;
                    continue;
                }

                User user = getByLoginName(user1.getLoginName());

                // 设置主部门
                String loginName = user.getLoginName();
                String departmentName = userMainDeptPathMap.get(loginName);
                ArrayList<String> departmentNames = new ArrayList<String>();

                // 更新部门关系
                departmentUserJobService.deleteByUser(user.getUuid());
                // 判断部门名称全路径是否已经存在，如果存在则建立关联关系
                if (StringUtils.isNotBlank(departmentName)) {
                    createDepartmentUserRelation(user, departmentName, true);
                    departmentNames.add(departmentName);
                }

                this.dao.save(user);
                // 设置其他部门
                Set<String> otherdepartmentNameSet = userOtherDeptPathMap.get(loginName);
                // 判断部门名称全路径是否已经存在，如果存在则建立关联关系
                // 否则新增部门
                if (otherdepartmentNameSet != null) {
                    for (String otherdeptpath : otherdepartmentNameSet) {
                        if (StringUtils.isNotBlank(otherdeptpath)) {
                            if (departmentName.equals(otherdeptpath)) {
                                continue;
                            }
                            createDepartmentUserRelation(user, otherdeptpath, false);
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
                user.setDepartmentName(departmentName);

                // 更新岗位关系
                userJobDao.deleteByUser(user.getUuid());
                // 主岗位 20140817 add
                String jobName = userMajorJobhMap.get(loginName);
                createUserJobRelation(user, jobName, true);
                successcount = successcount + 1;
                user.setMajorJobName(jobName);

                // 设置其他岗位
                Set<String> otherJobNameSet = userOtherJobhMap.get(loginName);
                int otherJobCount = 0;
                String otherJobNames = "";
                if (otherJobNameSet != null) {
                    for (String otherJobName : otherJobNameSet) {
                        UserJob userjob = createUserJobRelation(user, otherJobName, false);
                        successcount = successcount + 1;
                        Job job = userjob.getJob();
                        if (otherJobCount == otherJobNameSet.size() - 1) {
                            otherJobNames = otherJobNames + job.getDepartmentName() + "/" + job.getName();
                        } else {
                            otherJobNames = otherJobNames + job.getDepartmentName() + "/" + job.getName() + ";";
                        }
                        otherJobCount++;
                    }
                    user.setOtherJobNames(otherJobNames);
                }
            }
        }
        String msg = "选择的用户职位数据共【" + totalcount + "】行，帐号为空数据共【" + nullAccount + "】行,成功导入【" + successcount + "】行";
        System.out.println(msg);
        rsmap.put("msg", msg);
        return rsmap;
    }

    @Override
    public HashMap<String, Object> saveUserFromList(List list) {
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        int duplicatecount = 0;
        int successcount = 0;
        int totalcount = list.size();
        int nullAccount = 0;
        HashMap<String, Object> rsmap = new HashMap<String, Object>();
        HashMap<String, String> userleadersMap = new HashMap<String, String>();// 用户名，上级领导名字

        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        List<String> existUsers = new ArrayList<String>();
        if (list != null && !list.isEmpty()) {
            for (Object o : list) {
                User user = (User) o;

                if (StringUtils.isEmpty(user.getLoginName())) {
                    nullAccount = nullAccount + 1;
                    continue;
                }
                // 判断导入的用户登录名是否唯一
                // 判断登录名是否存在空格 修改者yuyq by2014-12-03
                if (user.getLoginName().indexOf(" ") > -1) {
                    throw new RuntimeException("登录名为[" + user.getLoginName() + "]的用户存在空格，请检查！");
                }
                // 用户登录名唯一性验证
                if (StringUtils.isNotBlank(user.getLoginName())
                        && getByLoginNameIgnoreCase(user.getLoginName(), null) != null) {
                    existUsers.add(user.getLoginName());
                    // throw new RuntimeException("已经存在登录名为[" +
                    // user.getLoginName() + "]的用户!");
                }
                // //判断是否要全局性唯一
                // Boolean isUnique = OrgUtil.isLoginNameUniqueInGlobal();
                // //全局性唯一要到公共库做校验
                // if (isUnique) {
                // List<TenantUser> tenantUsers =
                // tenantUserService.getTeantUserByLoginName(user.getLoginName());
                // if (tenantUsers.size() > 0) {
                // existUsers.add(user.getLoginName());
                // // throw new RuntimeException("已经存在登录名为[" +
                // user.getLoginName() + "]的用户!");
                // }
                // }

            }
            if (existUsers.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String eu : existUsers) {
                    sb.append(";" + eu);
                }
                throw new RuntimeException(
                        "已经存在登录名为[" + sb.toString().replaceFirst(";", "") + "]的用户!");
            } else {
                for (Object o : list) {
                    User user = (User) o;
                    user.setUuid(null);
                    // TODO 用户ID限定11位（U+4位租户ID+6位用户ID）
                    String id = idGeneratorService.generate(User.class, USER_ID_PATTERN);
                    user.setId(id.substring(0, 1) + tenantId.substring(1,
                            tenantId.length()) + id.substring(4, 11));
                    // 加密密码
                    // user.setPassword(passwordEncoder.encodePassword(user.getPassword(),
                    // getSalt(user)));
                    user.setTenantId(tenantId);
                    user.setAccountNonExpired(false);
                    user.setAccountNonLocked(false);
                    user.setCredentialsNonExpired(false);
                    user.setEnabled(true);
                    user.setIssys(false);

                    if (!StringUtils.isEmpty(user.getLeaderNames())) {
                        userleadersMap.put(user.getLoginName(), user.getLeaderNames());
                    }

                    user.setExternalId(user.getLoginName());

                    User userUpdate = this.dao.findUniqueBy(User.class, "loginName",
                            user.getLoginName());
                    if (userUpdate != null) {
                        userUpdate.setPrincipalCompany(user.getPrincipalCompany());
                        userUpdate.setEmployeeNumber(user.getEmployeeNumber());
                        userUpdate.setUserName(user.getUserName());// 名字也更新
                        userUpdate.setPassword(user.getPassword());
                        userUpdate.setSex(user.getSex());
                        userUpdate.setMobilePhone(user.getMobilePhone());
                        userUpdate.setOtherMobilePhone(user.getOtherMobilePhone());
                        userUpdate.setMainEmail(user.getMainEmail());
                        userUpdate.setHomePhone(user.getHomePhone());
                        userUpdate.setOtherEmail(user.getOtherEmail());
                        userUpdate.setCode(user.getExternalId());
                        userUpdate.setFax(user.getFax());
                        userUpdate.setOfficePhone(user.getOfficePhone());
                        userUpdate.setEnglishName(user.getEnglishName());
                        userUpdate.setDepartmentName("/");
                        UserBean userBean = new UserBean();
                        // 权限和角色表剥离 2015年12月15日 14:18:57 update by linz
                        // System.out.println("-------------------------");
                        // userUpdate.getRoles().size();
                        // userUpdate.getPrivileges().size();
                        // userBean.setRoles(userUpdate.getRoles());
                        // userBean.setPrivileges(userUpdate.getPrivileges());
                        //

                        Set<Role> roles = this.getRole(userUpdate.getUuid());
                        Set<Privilege> privileges = this.getPrivilege(userUpdate.getUuid());
                        userBean.setRoles(roles);
                        userBean.setPrivileges(privileges);
                        userBean.setDepartmentName("/");
                        BeanUtils.copyProperties(userUpdate, userBean);
                        userBean.setMajorJobId("J1800002643");
                        userBean.setMajorJobName("AAA");
                        this.saveBean(userBean);

                        // this.userDao.save(userUpdate);
                        // 拼音也是需要保存的.
                        pinyinService.deleteByEntityUuid(userUpdate.getUuid());
                        try {
                            Set<Pinyin> userPinyins = getUserPinyin(userUpdate);
                            for (Pinyin userPinyin : userPinyins) {
                                pinyinService.save(userPinyin);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                        duplicatecount = duplicatecount + 1;
                    } else {
                        UserBean userBean = new UserBean();
                        System.out.println("-------------------------");
                        // user.getRoles().size();
                        // user.getPrivileges().size();
                        userBean.setRoles(new HashSet<Role>());
                        userBean.setPrivileges(new HashSet<Privilege>());
                        BeanUtils.copyProperties(user, userBean);
                        userBean.setMajorJobId("J1800002643");
                        userBean.setMajorJobName("AAA");
                        userBean.setDepartmentId("111");
                        userBean.setCode(userBean.getExternalId());
                        userBean.setDepartmentName("aaa");
                        this.saveBean(userBean);

                        // this.userDao.save(user);
                        // String usertenantId = id.substring(0, 4).replace("U",
                        // "T");
                        // if (tenantId.equals(usertenantId))
                        // tenantUserService.saveTeantUserByOragUser(user);
                        try {
                            Set<Pinyin> userPinyins = getUserPinyin(user);
                            for (Pinyin userPinyin : userPinyins) {
                                pinyinService.save(userPinyin);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                        successcount = successcount + 1;
                    }
                }
            }
        }
        int reallyimport = totalcount - nullAccount;
        String msg = "选择的用户数据共【" + totalcount + "】行，帐号为空数据共【" + nullAccount + "】行,实际导入数据共【" + reallyimport + "】行,成功更新【"
                + duplicatecount + "】行，成功导入【" + successcount + "】行";
        System.out.println(msg);
        rsmap.put("msg", msg);
        rsmap.put("userleadersMap", userleadersMap);
        return rsmap;
    }

    /**
     * @param photoUUID 原图的uuid
     * @throws IOException
     * @Title: saveSmallPhoto
     * @Description: 将原始图片缩小成60*60的图片保存
     */
    public MongoFileEntity saveSmallPhoto(String photoUUID) throws IOException {
        MongoFileEntity fileEntity = mongoFileService.getFile(photoUUID);
        if (fileEntity != null) {
            ImageScaleUtil isu = new ImageScaleUtil();
            BufferedImage image1 = ImageIO.read(fileEntity.getInputstream());
            BufferedImage image2 = null;
            if (image1 != null) {
                image2 = isu.imageZoomOut(image1, 60, 60, true);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image2, isu.getFileExtension(fileEntity.getFileName()), os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                fileEntity = mongoFileService.saveFile(fileEntity.getFileName(), is);
            }
        }
        return fileEntity;
    }

    @SuppressWarnings("unchecked")
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveUserSet(String uuid, UserBean userBean) throws Exception {
        User user = this.get(uuid);
        user.setPhotoUuid(userBean.getPhotoUuid());
        // 小图标处理
        // 如果没有图片时，不处理
        if (StringUtils.isNotBlank(userBean.getPhotoUuid())) {
            MongoFileEntity mongoFileEntity = saveSmallPhoto(userBean.getPhotoUuid());
            user.setSmallPhotoUuid(mongoFileEntity.getId());
        }
        user.setSex(userBean.getSex());
        user.setMobilePhone(userBean.getMobilePhone());
        user.setOfficePhone(userBean.getOfficePhone());
        user.setFax(userBean.getFax());
        user.setIdNumber(userBean.getIdNumber());
        // user.setTrace(userBean.getTrace());
        user.setMainEmail(userBean.getMainEmail());
        user.setOtherEmail(userBean.getOtherEmail());
        user.setOtherMobilePhone(userBean.getOtherMobilePhone());

        // 保存是否接收短信消息
        UserProperty example = new UserProperty();
        example.setUserUuid(user.getUuid());
        example.setName(UserProperty.KEY_RECEIVE_SMS_MESSAGE);
        List<UserProperty> receiveSmsMessageProperties = userPropertyService.findByExample(example);
        UserProperty receiveSmsMessageProperty = null;
        if (receiveSmsMessageProperties.isEmpty()) {
            receiveSmsMessageProperty = new UserProperty();
            receiveSmsMessageProperty.setUserUuid(user.getUuid());
            receiveSmsMessageProperty.setName(UserProperty.KEY_RECEIVE_SMS_MESSAGE);
        } else {
            receiveSmsMessageProperty = receiveSmsMessageProperties.get(0);
        }
        receiveSmsMessageProperty.setValue(userBean.isReceiveSmsMessage() + "");
        userPropertyService.save(receiveSmsMessageProperty);

        // 保存联系人
        if (userBean.getContactName() != null) {
            example = new UserProperty();
            example.setUserUuid(user.getUuid());
            example.setName(UserProperty.KEY_SHOW_CONTACT);
            List<UserProperty> contactNames = userPropertyService.findByExample(example);
            UserProperty contactName = null;
            if (contactNames.isEmpty()) {
                contactName = new UserProperty();
                contactName.setUserUuid(user.getUuid());
                contactName.setName(UserProperty.KEY_SHOW_CONTACT);
            } else {
                contactName = contactNames.get(0);
            }
            contactName.setValue(userBean.getContactName());
            userPropertyService.save(contactName);
        }

        // 保存邮件
        if (userBean.getEmail() != null) {
            example = new UserProperty();
            example.setUserUuid(user.getUuid());
            example.setName(UserProperty.KEY_EMAIL);
            List<UserProperty> emails = userPropertyService.findByExample(example);
            UserProperty email = null;
            if (emails.isEmpty()) {
                email = new UserProperty();
                email.setUserUuid(user.getUuid());
                email.setName(UserProperty.KEY_EMAIL);
            } else {
                email = emails.get(0);
            }
            email.setValue(userBean.getEmail());
            userPropertyService.save(email);
        }

        // 7、文件上到mongondb
        String photpUuid = user.getPhotoUuid();
        if (StringUtils.isNotBlank(photpUuid)) {
            mongoFileService.pushFileToFolder(user.getUuid(), photpUuid, "photpUuid");
        }
        this.dao.save(user);
        /*
         * String boPwd = ""; if (StringUtils.isNotEmpty(userBean.getBoPwd())) {
         * boPwd = userBean.getBoPwd(); } try { List<Object> us =
         * dao.getSession()
         * .createSQLQuery("select bouser,bopwd from org_user where uuid='" +
         * user.getUuid() + "'").list(); Object object = us.get(0); if (object
         * instanceof Object[]) { if (((Object[]) object)[0] != null) {
         * user.setBoUser(((Object[]) object)[0].toString()); } if (((Object[])
         * object)[1] != null) { user.setBoPwd(((Object[])
         * object)[1].toString()); } } } catch (Exception e) {
         * logger.error(e.getMessage(), e); } if
         * (StringUtils.isNotEmpty(userBean.getBoUser()) &&
         * !boPwd.equals(user.getBoPwd())) { try { this.dao.getSession()
         * .createSQLQuery( "update org_user set BOPWD='" + boPwd +
         * "' where bouser='" + userBean.getBoUser() + "'") .executeUpdate(); }
         * catch (Exception e) { logger.error(e.getMessage(), e); } SAPRfcJson
         * util = new SAPRfcJson(""); util.setField("USERNAME",
         * userBean.getBoUser()); util.setField("PASSWORD", user.getBoPwd());
         * util.setField("NEW_PASSWORD", boPwd); SAPRfcJson rfcjson =
         * saprfcservice.RFC("boConnectConfig", "ZME_USER_CHANGE_PASSWORD",
         * util.getRfcJson(), 1, -1, null); String result =
         * rfcjson.getFieldValue("SUCCESSFUL").trim(); if
         * (StringUtils.isEmpty(result)) { throw new
         * RuntimeException(rfcjson.getFieldValue("ERROR_MESSAGE")); } }
         */
    }

    private void updateCommonUser(Department department, User user, String updateType,
                                  String departmentId) {
        if (department != null) {
            // 如果有挂接单位
            if (StringUtils.isNotBlank(department.getCommonUnitId())) {
                this.commonDepartmentService.updateCommonUser(department.getCommonUnitId(),
                        departmentId, user,
                        updateType);
            }
            Department parent = department.getParent();
            if (parent != null) {
                updateCommonUser(parent, user, updateType, departmentId);
            }
        }
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    @Deprecated
    public void saveCommonUsers(String commonUserNames, String commonUserIds) {
        String[] userIds = commonUserIds.split(Separator.SEMICOLON.getValue());
        String[] userNames = commonUserNames.split(Separator.SEMICOLON.getValue());

        for (int i = 0; i < userIds.length; i++) {
            String userId = userIds[i];
            if (this.getById(userId) != null) {
                throw new RuntimeException("已经存在用户ID为[" + userId + "]的用户[" + userNames[i] + "]!");
            }
            String serverUrl = SystemParams.getValue(WfGzDataConstant.KEY_WELL_PT_REST_ADDRESS,
                    WfGzDataConstant.DEFAULT_WELL_PT_REST_ADDRESS);
            String tenantId = getTenantId(userId);
            String username = userId;
            String password = userId;// userDetails.getPassword();
            WellptClient client = new DefaultWellptClient(serverUrl, tenantId, username, password);
            OrgUserGetRequest request = new OrgUserGetRequest();
            request.setId(userId);
            OrgUserGetResponse response = client.execute(request);
            UserBean bean = response.getData();
            User user = new User();
            String gzTenantId = SpringSecurityUtils.getCurrentTenantId();
            BeanUtils.copyProperties(bean, user);
            user.setUuid(null);
            user.setTenantId(gzTenantId);
            user.setAccountNonExpired(false);
            user.setAccountNonLocked(false);
            user.setCredentialsNonExpired(false);
            user.setEnabled(true);
            user.setIssys(false);
            this.dao.save(user);

            // 保存公共库挂职用户信息
            // TenantGzUser tenantGzUser = new TenantGzUser();
            // tenantGzUser.setUserName(userNames[i]);
            // tenantGzUser.setUserLoginName(tenantUserService.getTenantUserByUserId(userId).getUserLoginName());
            // tenantGzUser.setUserId(userId);
            // tenantGzUser.setZzTenantId(tenantId);
            // tenantGzUser.setZzTenantName(tenantService.getById(tenantId).getName());
            // tenantGzUser.setGzTenantId(gzTenantId);
            // tenantGzUser.setGzTenantName(tenantService.getById(gzTenantId).getName());
            // tenantGzUserService.save(tenantGzUser);
        }
    }

    /**
     * @param userId
     * @return
     */
    private String getTenantId(String userId) {
        return IdPrefix.TENANT.getValue() + userId.substring(1, 4);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#findByExample(com.wellsoft.pt.org.entity.User)
     */
    @Override
    public List<User> findByExample(User example) {
        return this.dao.findByExample(example);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#createUser(com.wellsoft.pt.org.entity.User)
     */
    @Override
    public void createUser(User entity) {
        String id = idGeneratorService.generate(User.class, USER_ID_PATTERN);

        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
        // TODO 用户ID限定11位（U+4位租户ID+6位用户ID）
        entity.setId(id);
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getSystemAdmin()
     */
    @Override
    public User getSystemAdmin() {
        User user = new User();
        user.setIssys(true);
        List<User> users = this.dao.findByExample(user);
        if (users.isEmpty()) {
            return null;
        }

        User admin = new User();
        BeanUtils.copyProperties(users.get(0), admin);

        return admin;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getMobilesByUserIds(java.util.List)
     */
    @Override
    public List<QueryItem> getMobilesByUserIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<QueryItem>(0);
        }
        String hql = "select o.mobilePhone as mobilePhone, o.id as id from User o where o.id in(:userIds)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIds", userIds);
        List<QueryItem> queryItems = this.dao.query(hql, values, QueryItem.class);
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getMobilesForSmsByUserIds(java.util.List)
     */
    @Override
    public List<QueryItem> getMobilesForSmsByUserIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<QueryItem>(0);
        }
        String hql = "select o.mobilePhone as mobilePhone, o.id as id, o.userName as userName from User o where o.id in(:userIds) "
                + " and exists (select uuid from UserProperty up where o.uuid = up.userUuid and up.name = 'receive.sms.message' "
                + " and up.value = 'true')";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIds", userIds);
        List<QueryItem> queryItems = this.dao.query(hql, values, QueryItem.class);
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#isMemberOf(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isMemberOf(String userId, String memberOf) {
        if (StringUtils.isBlank(memberOf)) {
            return false;
        }
        if (memberOf.contains(userId)) {
            return true;
        }
        String[] ids = StringUtils.split(memberOf, Separator.SEMICOLON.getValue());
        Set<String> groupIds = new HashSet<String>();
        Set<String> departmentIds = new HashSet<String>();
        Set<String> jobIds = new HashSet<String>();
        Set<String> dutyIds = new HashSet<String>();
        for (String id : ids) {
            if (id.equals(userId)) {
                return true;
            } else if (id.startsWith(IdPrefix.GROUP.getValue())) {
                groupIds.add(id);
            } else if (id.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                departmentIds.add(id);
            } else if (id.startsWith("J")) {
                jobIds.add(id);
            } else if (id.startsWith("W")) {
                dutyIds.add(id);
            }
        }

        // for (String groupid : groupIds) {
        // Set<String> membersid =
        // groupService.getAllMemberIdsByGroupId(groupid);
        // for (String id : membersid) {
        // if (id.equals(userId)) {
        // return true;
        // } else if (id.startsWith(IdPrefix.DEPARTMENT.getValue())) {
        // departmentIds.add(id);
        // } else if (id.startsWith("J")) {
        // jobIds.add(id);
        // } else if (id.startsWith("W")) {
        // dutyIds.add(id);
        // }
        // }
        // }

        if (groupIds.isEmpty()) {
            groupIds.add(userId);
        }
        if (jobIds.isEmpty()) {
            jobIds.add(userId);
        }
        if (dutyIds.isEmpty()) {
            dutyIds.add(userId);
        }
        if (departmentIds.isEmpty()) {
            departmentIds.add(userId);
        } else {
            // 获取子部门
            Set<String> childrenIds = new HashSet<String>();
            for (String departmentId : departmentIds) {
                this.departmentService.traverseAndAddChildrenIds(departmentId, childrenIds);
            }
            departmentIds.addAll(childrenIds);
        }

        String hql = "select count(user.id) from User as user "
                + " where  "
                + " exists (select uuid from DepartmentUserJob department_user_job "// 部门
                + " where department_user_job.user.uuid = user.uuid and user.id = :userId and department_user_job.department.id in (:departmentIds))"
                + " or exists (select uuid from UserJob user_job "// 职位
                + " where user_job.user.uuid = user.uuid and user.id = :userId and user_job.job.id in (:jobIds))"
                + " or exists (select user_job.uuid from UserJob user_job ,Duty duty "// 职务
                + " where user_job.user.uuid = user.uuid and user_job.job.duty=duty and user.id = :userId and user_job.job.duty.id in (:dutyIds))";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("departmentIds", departmentIds.toArray(new String[0]));
        values.put("jobIds", jobIds.toArray(new String[0]));
        values.put("dutyIds", dutyIds.toArray(new String[0]));
        return ((Long) this.dao.findUnique(hql, values)) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getContactNameByUserUuid(java.lang.String)
     */
    @Override
    public String getContactNameByUserUuid(String uuid) {
        UserProperty example = new UserProperty();
        example.setUserUuid(uuid);
        example.setName(UserProperty.KEY_SHOW_CONTACT);
        List<UserProperty> contactNames = this.userPropertyService.findByExample(example);
        if (contactNames.isEmpty()) {
            return "";
        } else if (StringUtils.isNotBlank(contactNames.get(0).getValue())) {
            return contactNames.get(0).getValue();
        }
        return "";
    }

    @Override
    public List<QueryItem> queryUserQueryItemData(String hql, Map<String, Object> values,
                                                  PagingInfo pagingInfo) {
        return this.dao.query(hql, values, QueryItem.class, pagingInfo);
    }

    @Override
    public Long findCountByHql(String hql, Map<String, Object> values) {
        return this.dao.findUnique(hql, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getPrivilegeTree(java.lang.String)
     */
    @Override
    public TreeNode getPrivilegeTree(String uuid) {
        User user = this.get(uuid);
        // 附加构建权限树
        Set<Privilege> ownerPrivileges = this.getPrivilege(uuid);
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
     * @see com.wellsoft.pt.org.service.UserService#getUserPrivilegeTree(java.lang.String)
     */
    @Override
    public TreeNode getUserPrivilegeTree(String uuid) {
        User user = this.get(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(user.getUserName());
        treeNode.setId(TreeNode.ROOT_ID);
        Set<Privilege> privileges = this.getPrivilege(uuid);
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
     * 导入后更新上级领导人
     *
     * @param deptRsMp
     */
    @Override
    public void updateUserLeadersAfterImport(HashMap<String, Object> userRsMp) {
        HashMap<String, String> userleadersMap = (HashMap<String, String>) userRsMp.get(
                "userleadersMap");
        for (String loginname : userleadersMap.keySet()) {
            User user = getByLoginName(loginname);

            String[] leaders = userleadersMap.get(loginname).split(";");
            List<UserLeader> newLeaders = new ArrayList<UserLeader>();
            String leadernames = "";
            for (int i = 0; i < leaders.length; i++) {
                User leader = getByLoginName(leaders[i]);
                if (leader == null) {
                    continue;
                }
                this.dao.save(user);
                UserLeader userLeader = new UserLeader();
                userLeader.setUser(user);
                userLeader.setLeader(leader);
                newLeaders.add(userLeader);
                if (i == leaders.length - 1) {
                    leadernames = leadernames + leader.getUserName();
                } else {
                    leadernames = leadernames + leader.getUserName() + ";";
                }
            }
            user.setLeaderNames(leadernames);
            // 更新
            for (UserLeader entity : newLeaders) {
                userLeaderDao.save(entity);
            }
        }
    }

    /**
     * 创建用户和职位关联Vo
     *
     * @param user
     * @param jobName
     * @return UserJob
     */
    private UserJob createUserJobRelation(User user, String jobName, boolean isMajor) {
        if (StringUtils.isEmpty(jobName)) {
            throw new RuntimeException("用户" + user.getLoginName() + "职位不能为空!");
        }
        String realJobName = jobName.substring(jobName.lastIndexOf("/") + 1, jobName.length());
        String realDeptPath = jobName.substring(0, jobName.lastIndexOf("/"));
        List<Job> jobList = jobService.getJobByNameAndDeptName(realJobName, realDeptPath);
        if (jobList == null || jobList.size() == 0) {
            throw new RuntimeException(
                    "用户:" + user.getLoginName() + "职位：【" + realJobName + "】+部门:【" + realDeptPath
                            + "】找不到相应的职位记录！");
        }

        Job job = jobList.get(0);

        UserJob userJob = new UserJob();
        userJob.setUser(user);
        userJob.setJob(job);
        userJob.setIsMajor(isMajor);
        userJobDao.save(userJob);
        return userJob;
    }

    /**
     * 创建用户和部门关联VO
     *
     * @param user
     * @param detppath
     */
    private void createDepartmentUserRelation(User user, String detppath, boolean isMajor) {
        if (StringUtils.isNotBlank(detppath)) {
            // 立达信绿色照明股份有限公司导入测试/CFL产品线/CFL长泰分部/PMC部/长泰计划课 处理
            List<Department> deptList = this.dao.findBy(Department.class, "path", detppath);
            if (deptList.isEmpty()) {
                throw new RuntimeException(
                        "用户【" + user.getUserName() + "】" + "找不到对应的部门【" + detppath + "】");
            }
            // 否则直接建立关联关系
            Department dept = deptList.get(0);
            // 保存部门-用户关联关系
            DepartmentUserJob departmentUser = new DepartmentUserJob();
            departmentUser.setJobName(user.getJobName());
            departmentUser.setUser(user);
            departmentUser.setDepartment(dept);
            departmentUser.setIsMajor(isMajor);
            departmentUser.setTenantId(SpringSecurityUtils.getCurrentTenantId());
            this.dao.save(departmentUser);
        }
    }

    @Override
    public Job getMajorJobByUserLoginName(String loginName) {
        User user = getByLoginName(loginName);
        return getMajorJobByUser(user);
    }

    @Override
    public List<Job> getOtherJobsByUserLoginName(String loginName) {
        User user = getByLoginName(loginName);
        return getOtherJobsByUser(user);
    }

    @Override
    public Job getMajorJobByUserId(String id) {
        User user = getById(id);
        return getMajorJobByUser(user);
    }

    @Override
    public List<Job> getOtherJobsByUserId(String id) {
        User user = getById(id);
        return getOtherJobsByUser(user);
    }

    private Job getMajorJobByUser(User user) {
        if (user != null) {
            List<UserJob> jobls = userJobDao.getMajorJobs(user.getUuid());
            if (jobls != null && jobls.size() > 0) {
                return jobService.getByUuId(jobls.get(0).getJob().getUuid());
            }
        }
        return null;
    }

    private List<Job> getOtherJobsByUser(User user) {
        List<UserJob> jobls = userJobDao.getOtherJobs(user.getUuid());
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
    public void checkImportUserAndJobRelaction(List<Map<String, Object>> userJobList) {
        String errorMsg = "";
        if (userJobList != null && !userJobList.isEmpty()) {
            for (Object o : userJobList) {
                UserJob userjob = (UserJob) o;
                String jobName = userjob.getJobName();
                // 帐号为空的就不导入了
                if (StringUtils.isEmpty(userjob.getUserLoginName())) {
                    continue;
                }
                String deptPath = jobName.substring(0, jobName.lastIndexOf("/"));
                String realJobName = jobName.substring(jobName.lastIndexOf("/") + 1,
                        jobName.length());

                // 判断职位是否存在 不存在就报错了..
                List<Job> jobls = jobService.getJobByNameAndDeptName(realJobName, deptPath);
                if (jobls == null || jobls.size() == 0) {

                    errorMsg = errorMsg + "" + "用户:" + userjob.getUserLoginName() + "职位：【" + realJobName + "】+部门:【"
                            + deptPath + "】找不到相应的职位记录！";
                }
            }
        }
        if (!StringUtils.isEmpty(errorMsg)) {
            throw new RuntimeException(errorMsg);
        }
    }

    @Override
    public Set<Role> getUserOrgRoles(User user) {
        Set<Role> grantedRoles = new HashSet<Role>();
        Collection<Role> roles = new HashSet<Role>();// securityApiFacade.getRolesByUserId(user.getId());
        // 1、获取分配给用户的角色
        for (Role role : roles) {
            grantedRoles.add(role);
        }
        // 2、获取用户所在的群组拥有的角色
        // Set<Group> groups = user.getGroups();
        // for (Group group : groups) {
        // Collection<Role> rolesGroup = new HashSet<Role>();//
        // securityApiFacade.getRolesByGroupUuid(group.getUuid());
        // for (Role role : rolesGroup) {
        // grantedRoles.add(role);
        // }
        // }
        // 3、获取用户所在部门拥有的角色
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            Department department = departmentUserJob.getDepartment();
            if (department != null) {
                Collection<Role> rolesDepartment = new HashSet<Role>();// securityApiFacade.getRolesByDepartmentUuid(department.getUuid());
                for (Role role : rolesDepartment) {
                    grantedRoles.add(role);
                }
                // 获取部门所在群组拥有的角色
                // for (Group group : department.getGroups()) {
                // Collection<Role> rolesGroup = new HashSet<Role>();//
                // securityApiFacade.getRolesByGroupUuid(group.getUuid());
                // for (Role role : rolesGroup) {
                // grantedRoles.add(role);
                // }
                // }
                obtainParentDeptRoles(grantedRoles, department);
            }
        }

        // 4、0获得用户所在职位拥有的角色
        Set<UserJob> userJobs = user.getUserJobs();
        HashSet<String> dutyUuids = new HashSet<String>();
        for (UserJob userJob : userJobs) {
            Job job = userJob.getJob();
            if (job != null) {
                dutyUuids.add(job.getDuty().getUuid());
                Collection<Role> jobRoles = new HashSet<Role>();// securityApiFacade.getRolesByJobUuid(job.getUuid());
                for (Role role : jobRoles) {
                    grantedRoles.add(role);
                }
                // 获取部门所在群组拥有的角色
                // for (Group group : job.getGroups()) {
                // Collection<Role> rolesGroup = new HashSet<Role>();//
                // securityApiFacade.getRolesByGroupUuid(group.getUuid());
                // for (Role role : rolesGroup) {
                // grantedRoles.add(role);
                // }
                // }
            }
        }

        // 5.0、获得用户所在职务拥有的角色
        // for (String dutyUuid : dutyUuids) {
        // Duty duty = dutyService.getByUuid(dutyUuid);
        // Collection<Role> dutyRoles =
        // roleService.getRolesByDutyUuid(duty.getUuid());
        // for (Role role : dutyRoles) {
        // grantedRoles.add(role);
        // }
        // }
        return grantedRoles;
    }

    /**
     * 获得所有父部门的角色
     *
     * @param grantedRoles
     * @param department
     */
    private void obtainParentDeptRoles(Set<Role> grantedRoles, Department department) {
        if (department.getParent() != null) {
            // Collection<Role> rolesDepartment =
            // securityApiFacade.getRolesByDepartmentUuid(department.getParent()
            // .getUuid());
            // for (Role role : rolesDepartment) {
            // grantedRoles.add(role);
            // }

            // 获取父级部门所在群组拥有的角色
            // for (Group group : department.getParent().getGroups()) {
            // Collection<Role> rolesGroup = new HashSet<Role>();//
            // securityApiFacade.getRolesByGroupUuid(group.getUuid());
            // for (Role role : rolesGroup) {
            // grantedRoles.add(role);
            // }
            // }

            obtainParentDeptRoles(grantedRoles, department.getParent());
        }
    }

    /**
     * 判断用户是否为领导（同一部门内）
     * 规则 同一级部门内 职级必须大于前用户
     *
     * @param leader
     * @param user
     * @return
     */
    @Override
    public boolean compareUserIsLeaderInTheSameDept(User leader, User user, Department department) {
        // 获得该用户在该部门的职位
        Set<UserJob> leaderjobs = leader.getUserJobs();
        Set<UserJob> userJobs = user.getUserJobs();
        boolean isAddLeader = true;
        for (UserJob leaderjob : leaderjobs) {
            if (!isAddLeader) {
                break;
            }
            Job lJob = leaderjob.getJob();
            // 只有该用户在本部门内，才进行比较确认
            if (lJob.getDepartmentUuid().equals(department.getUuid())) {
                // 获得本部门内的职级
                for (UserJob userJob : userJobs) {
                    Job uJob = userJob.getJob();
                    if (uJob.getDepartmentUuid().equals(department.getUuid())
                            && lJob.getDuty().getIlevel() <= uJob.getDuty().getIlevel()) {
                        // 如果负责人本部门内职级小于等于用户职级的，则pass
                        isAddLeader = false;
                        break;
                    }
                }

            }
        }
        return isAddLeader;
    }

    @Override
    public Set<String> getUserLeaderIds_new(String userId) {
        User user = getById(userId);

        Set<String> userIds = new HashSet<String>();
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        // 获得用户职位对照的汇报对象
        Set<UserJob> userJobs = user.getUserJobs();
        for (UserJob userJob : userJobs) {
            Job job = userJob.getJob();
            setJobPrincipals(userIds, job);

            if (userIds.size() > 0) {
                return userIds;
            }
        }

        // 获得用户所在部门 以及所在父部门的汇报对象
        // 如果当前部门能取到 则直接返回
        // 如果取不到，取辅部门

        // 还是说主部门先来？ 待确认
        // 先取主部门
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            Set<String> inneruserIds = new HashSet<String>();
            Set<String> directleaderIds = new HashSet<String>();
            Set<String> equalsIds = new HashSet<String>();
            Department department = departmentUserJob.getDepartment();
            setDeptPrincipals(inneruserIds, department);
            // 负责人是自己的时候要过滤 (再去他的上级找领导)
            for (String leaderid : inneruserIds) {
                if (leaderid.equals(userId)) {
                    if (department.getParent() != null) {
                        setDeptPrincipals(equalsIds, department.getParent());
                    }
                    continue;
                }
                directleaderIds.add(leaderid);
            }
            inneruserIds.addAll(equalsIds);

            if (directleaderIds.size() < 1) {
                obtainlatelyParentDeptPrincipal(directleaderIds, department);
            }
            for (String innerid : directleaderIds) {
                // 负责人是自己的时候要过滤 (再去他的上级找领导)
                if (innerid.equals(userId)) {
                    if (department.getParent() != null) {
                        setDeptPrincipals(equalsIds, department.getParent());
                    }
                    continue;
                }
                userIds.add(innerid);
            }
            directleaderIds.addAll(equalsIds);
        }

        userIds.remove(userId);
        return userIds;
    }

    private void obtainlatelyParentDeptPrincipal(Set<String> userIds, Department department) {
        if (department.getParent() != null) {
            setDeptPrincipals(userIds, department.getParent());
            if (userIds.size() > 0) {
                return;
            }
            obtainParentDeptPrincipal(userIds, department.getParent());
        }
    }

    /**
     * 获得所有父部门的角色
     *
     * @param grantedRoles
     * @param department
     */
    private void obtainParentDeptPrincipal(Set<String> userIds, Department department) {
        if (department.getParent() != null) {
            setDeptPrincipals(userIds, department.getParent());
            obtainParentDeptPrincipal(userIds, department.getParent());
        }
    }

    private void setDeptPrincipals(Set<String> userIds, Department department) {
        List<DeptPrincipal> deptPrincipals = deptPrincipalService.getPrincipal(
                department.getUuid());
        for (DeptPrincipal deptPrincipal : deptPrincipals) {
            String orgId = deptPrincipal.getOrgId();

            if (orgId.startsWith(IdPrefix.USER.getValue())) {// U
                User user = getById(orgId);
                if (user.getEnabled() == false) {
                    continue;
                }
                userIds.add(orgId);
            } else if (orgId.startsWith(IdPrefix.DEPARTMENT.getValue())) {// D
                // 暂时不处理
            } else if (orgId.startsWith("J")) {// J
                List<User> users = jobService.getUsersByJobId(orgId);
                for (User user : users) {
                    if (user.getEnabled() == false) {
                        continue;
                    }
                    userIds.add(user.getId());
                }
            }
        }
    }

    @Override
    public Set<String> getAllUserLeaderIds_new(String userId) {
        User user = getById(userId);
        Set<String> userIds = new HashSet<String>();
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            Set<String> inneruserIds = new HashSet<String>();
            Department department = departmentUserJob.getDepartment();
            setDeptPrincipals(inneruserIds, department);
            for (String leaderid : inneruserIds) {
                if (leaderid.equals(userId)) {
                    continue;
                }
                // TODO 不做职级大小过滤操作
                // User leader = getById(leaderid);
                // if (compareUserIsLeaderInTheSameDept(leader, user,
                // department)) {
                // userIds.add(leaderid);
                // }
                userIds.add(leaderid);
            }
            obtainParentDeptPrincipal(userIds, department);
        }

        // 获得职位的汇报对象 TODO
        Set<UserJob> userJobs = user.getUserJobs();
        for (UserJob userJob : userJobs) {
            Job job = userJob.getJob();
            setJobPrincipals(userIds, job);
        }

        // 获取到用户配置的领导
        List<String> superLeaderIds = new ArrayList<String>();
        superLeaderIds = getUserLeaderIds(userId);
        for (String supId : superLeaderIds) {
            userIds.add(supId);
        }

        userIds.remove(userId);
        // 获得用户职位对照的汇报对象 TODO

        return userIds;
    }

    private void setJobPrincipals(Set<String> userIds, Job job) {
        List<JobPrincipal> deptPrincipals = jobPrincipalDao.getPrincipal(job.getUuid());
        for (JobPrincipal deptPrincipal : deptPrincipals) {
            String orgId = deptPrincipal.getOrgId();
            if (orgId.startsWith(IdPrefix.USER.getValue())) {// U
                User user = getById(orgId);
                if (user.getEnabled() == false) {
                    continue;
                }
                userIds.add(orgId);
            } else if (orgId.startsWith(IdPrefix.DEPARTMENT.getValue())) {// D
                // 暂时不处理
            } else if (orgId.startsWith("J")) {// J
                List<User> users = jobService.getUsersByJobId(orgId);
                for (User user : users) {
                    if (user.getEnabled() == false) {
                        continue;
                    }
                    userIds.add(user.getId());
                }
            }
        }
    }

    @Override
    public String getUserOrgInfo(String userId) {
        JSONObject jsonobj = new JSONObject();
        User user = getById(userId);
        Set<DepartmentUserJob> departmentUserJobs = user.getDepartmentUsers();
        for (DepartmentUserJob du : departmentUserJobs) {
            if (du.getIsMajor()) {
                jsonobj.put("mainDepartmentId", du.getDepartment().getId());
                jsonobj.put("mainDepartmentName", du.getDepartment().getName());
                jsonobj.put("mainDepartmentFullPath",
                        departmentService.getFullPath(du.getDepartment().getId()));
            }
        }
        Set<UserJob> jobs = user.getUserJobs();
        for (UserJob du : jobs) {
            if (du.getIsMajor()) {
                jsonobj.put("mainJobId", du.getJob().getId());
                jsonobj.put("mainJobName", du.getJob().getName());
                jsonobj.put("dutyLevel", du.getJob().getDuty().getDutyLevel());
            }
        }

        JSONObject userObj = new JSONObject();
        userObj.put("id", user.getId());
        userObj.put("code", user.getCode());
        userObj.put("loginName", user.getLoginName());
        userObj.put("userName", user.getUserName());
        userObj.put("employeeNumber", user.getEmployeeNumber());
        userObj.put("mobilePhone", user.getMobilePhone());
        userObj.put("otherMobilePhone", user.getOtherMobilePhone());
        userObj.put("fax", user.getFax());
        userObj.put("officePhone", user.getOfficePhone());
        userObj.put("mainEmail", user.getMainEmail());
        userObj.put("otherEmail", user.getOtherEmail());
        jsonobj.put("user", userObj);
        return jsonobj.toString();
    }

    @Override
    public List<String> getUserOrgIds(String userId) {
        List<String> idLs = new ArrayList<String>();
        User user = getById(userId);
        idLs.add(user.getId());
        Set<DepartmentUserJob> departmentUserJobs = user.getDepartmentUsers();
        // 立达信绿色照明股份有限公司/财务中心/财务管理部
        for (DepartmentUserJob du : departmentUserJobs) {
            if (du.getIsMajor()) {
                idLs.add(du.getDepartment().getId());
                // 递归获得部门所有id
                getDepartmentIdsForGetUserOrgIds(idLs, du.getDepartment());
            }
        }
        Set<UserJob> jobs = user.getUserJobs();
        for (UserJob du : jobs) {
            if (du.getIsMajor()) {
                idLs.add(du.getJob().getId());
                idLs.add(du.getJob().getDuty().getId());
            }
        }
        // Set<Group> groups = user.getGroups();
        // for (Group group : groups) {
        // // 用户
        // Set<User> users = group.getUsers();
        // for (User user2 : users) {
        // idLs.add(user2.getId());
        // }
        // // 部门
        // Set<Department> departments = group.getDepartments();
        // for (Department department : departments) {
        // idLs.add(department.getId());
        // }
        // // 职位
        // Set<Job> jobs2 = group.getJobs();
        // for (Job job : jobs2) {
        // idLs.add(job.getId());
        // }
        // idLs.add(group.getId());
        // // 单位
        // Set<CommonUnit> commonUnits = group.getCommonUnits();
        // for (CommonUnit commonUnit : commonUnits) {
        // idLs.add(commonUnit.getUnitId());
        // }
        // idLs.add(group.getId());
        // }
        return idLs;
    }

    /**
     * 如何描述该方法
     *
     * @param idLs
     * @param dept
     */
    private void getDepartmentIdsForGetUserOrgIds(List<String> idLs, Department dept) {
        if (dept.getParent() != null) {
            idLs.add(dept.getParent().getId());
            getDepartmentIdsForGetUserOrgIds(idLs, dept.getParent());
        }
    }

    /**
     * 获得我的下属，根据user的职位定位部门负责人，根据部门负责人递归本部门下面所有子部门的人员.
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getSubordinate_new(java.lang.String)
     */
    @Override
    public List<String> getSubordinate_new(String uuid) {
        User user = this.get(uuid);
        List<String> userIds = new ArrayList<String>();
        List<DeptPrincipal> deptPrincipals = deptPrincipalService.getPrincipalByOrgId(user.getId(),
                getMajorJobByUserId(user.getId()).getId());
        for (DeptPrincipal deptPrincipal : deptPrincipals) {
            String deptUuid = deptPrincipal.getDepartmentUuid();
            List<String> deptUserids = departmentService.getAllUserIdsByDepartmentId(
                    departmentService.get(deptUuid)
                            .getId());
            userIds.addAll(deptUserids);
        }
        return userIds;
    }

    @Override
    public String getUserEmail(String loginName) {
        User user = getByLoginName(loginName);
        String email = user.getMainEmail();
        // 获得主邮件，取不到再取辅邮件.
        if (StringUtils.isEmpty(email)) {
            email = user.getOtherEmail();
        }
        return email;
    }

    @Override
    public List<User> getUsersByUserName(String userName) {
        List<User> user = this.dao.findBy(User.class, "userName", userName);
        return user;
    }

    @Override
    public Set<String> getDirectDepartmentUserIds(String userId) {
        User user = getById(userId);
        Set<String> userIds = new HashSet<String>();
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            Department department = departmentUserJob.getDepartment();
            setDirectDeptUserid(userIds, department);
            obtainParentDeptUserids(userIds, department);
        }
        return userIds;
    }

    /* add by huanglinchuan 2015.1.15 begin */

    /**
     * 获得所有父部门的角色
     *
     * @param grantedRoles
     * @param department
     */
    private void obtainParentDeptUserids(Set<String> userIds, Department department) {
        if (department.getParent() != null) {
            setDirectDeptUserid(userIds, department.getParent());
            obtainParentDeptUserids(userIds, department.getParent());
        } else {
            setDirectDeptUserid(userIds, department);
        }
    }

    /* add by huanglinchuan 2015.1.15 end */

    private void setDirectDeptUserid(Set<String> userIds, Department department) {
        Set<DepartmentUserJob> departmentUsers = department.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            userIds.add(departmentUserJob.getUser().getId());
        }
    }

    /* add by huanglinchuan 2015.3.22 end */

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getDirectDepartments(java.lang.String)
     */
    @Override
    public Set<Department> getDirectDepartments(String userId) {
        User user = getById(userId);
        Set<Department> Departments = new HashSet<Department>();
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            Department department = departmentUserJob.getDepartment();
            Departments.add(department);
            obtainParentDepts(Departments, department);
        }
        return Departments;
    }

    private void obtainParentDepts(Set<Department> Departments, Department department) {
        if (department.getParent() != null) {
            Departments.add(department.getParent());
            obtainParentDepts(Departments, department.getParent());
        } else {
            Departments.add(department);
        }
    }

    @Override
    public User getByLoginNameIgnoreCase(String loginName, String tenantId) {
        String lowerloginname = loginName.toLowerCase();
        String hql = "from User u where lower(u.loginName) ='" + lowerloginname + "'";
        if (StringUtils.isNotBlank(tenantId)) {
            hql += " and u.tenantId='" + tenantId + "'";
        }
        List<User> userList = this.dao.find(hql, null, User.class);
        if (userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }

    @Override
    public String getUserDnByLoginName(String loginName) {
        /*
         * if (!OrgUtil.isAdSync()) { return "域配置未启用，请先启用!"; }
         */
        try {
            String userDn = adUserService.getDnByLoginName(loginName);
            String realdydn = userDn.replace("," + OrgUtil.getAdBase(), "");
            return realdydn;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Set<String> getDirectLeaderIds(String userId) {
        Set<String> directIds = new HashSet<String>();
        Set<String> innerIds = new HashSet<String>();
        User user = getById(userId);
        if (user == null) {
            return directIds;
        }
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUser : departmentUsers) {
            Department department = departmentUser.getDepartment(); // 得到自己的部门
            if (department != null) {
                innerIds = getDeptPrincipal(department, userId); // 获取到部门负责人的id
                for (String leaderid : innerIds) {
                    if (leaderid.equals(userId)) {
                        continue;
                    }
                    directIds.add(leaderid);
                }
            }
        }
        return directIds;
    }

    /**
     * 迭代遍历,这一级就去上一级找它的部门负责人
     *
     * @param department
     * @return
     */
    public Set<String> getDeptPrincipal(Department department, String userId) {
        Set<String> set = new HashSet<String>();
        List<DeptPrincipal> depatPrincipals = deptPrincipalService.getPrincipal(
                department.getUuid());

        if (depatPrincipals != null && !depatPrincipals.isEmpty()) {
            for (DeptPrincipal principal : depatPrincipals) {
                String orgId = principal.getOrgId();

                if (orgId.startsWith(IdPrefix.USER.getValue())) { // 前缀是否是U
                    User user = getById(orgId);
                    if (user == null) {
                        continue;
                    }
                    if (user.getEnabled() == true) { // 判断用户是否被激活,true为激活状态
                        if (orgId.equals(userId)) { // 如果是本人的话，就去上一级找
                            if (department.getParent() != null) {
                                return getDeptPrincipal(department.getParent(), userId);
                            }
                            continue;
                        }
                        set.add(orgId);
                    }
                } else if (orgId.startsWith("J")) { // 前缀是否是J
                    List<User> users = jobService.getUsersByJobId(orgId);
                    for (User u : users) {
                        if (u.getEnabled() == true) { // 判断用户是否被激活,true为激活状态
                            if (u.getId().equals(userId)) { // 如果是本人的话，就去上一级找
                                if (department.getParent() != null) {
                                    return getDeptPrincipal(department.getParent(), userId);
                                }
                                continue;
                            }
                            set.add(u.getId());
                        }
                    }
                }
            }
        } else {
            if (department.getParent() != null) {
                return getDeptPrincipal(department.getParent(), userId);
            }
        }
        return set;
    }

    @Override
    public Set<String> getReportLeaderIds(String userId) {
        User user = getById(userId);
        if (user == null) {
            return new HashSet<String>();
        }
        return getReportLeaderIds(user);
    }

    @Override
    public Set<String> getReportLeaderIds(User user) {
        Set<String> reportIds = new HashSet<String>();
        // 获得用户职位对照的汇报对象
        Set<UserJob> userJobs = user.getUserJobs();
        for (UserJob userJob : userJobs) {
            Job job = userJob.getJob(); // 得到自己的职位
            if (job != null) {
                reportIds.addAll(setReportPrincipals(job)); // 获取职位汇报对象id
            }
        }
        // 职位汇报对象为自己时,要过滤
        reportIds.remove(user.getId());
        return reportIds;
    }

    /**
     * 获取职位汇报对象
     *
     * @param job
     * @return
     */
    public Set<String> setReportPrincipals(Job job) {
        Set<String> setJob = new HashSet<String>();
        // 根据职位uuid获取直属领导
        List<JobPrincipal> jobPrincipals = jobPrincipalDao.getPrincipal(job.getUuid());
        for (JobPrincipal principal : jobPrincipals) {
            String orgId = principal.getOrgId();
            if (orgId.startsWith(IdPrefix.USER.getValue())) {// U
                User user = getById(orgId);
                if (user == null) {
                    continue;
                }
                if (user.getEnabled() == true) {
                    setJob.add(orgId);
                }
            } else if (orgId.startsWith("J")) { // J
                List<User> users = jobService.getUsersByJobId(orgId);
                for (User user : users) {
                    if (user.getEnabled() == true) {
                        setJob.add(user.getId());
                    }
                }
            }
        }
        return setJob;
    }

    @Override
    public User getUserByEmployeeNumber(String employeeNumber) {
        return this.dao.findUniqueBy(User.class, "employeeNumber", employeeNumber);
    }

    /**
     * 根据用户获取其所有组织相关的ID
     *
     * @param userId
     * @return
     */
    public List<String> getAllRelatedGroupOrgs(String userId) {
        List<String> groupOrgs = new ArrayList<String>();
        User user = getById(userId);
        Set<DepartmentUserJob> userDepartments = user.getDepartmentUsers();
        Iterator<DepartmentUserJob> userDepartmentsIterator = userDepartments.iterator();
        while (userDepartmentsIterator.hasNext()) {
            DepartmentUserJob userDepartment = userDepartmentsIterator.next();
            Department department = userDepartment.getDepartment();
            groupOrgs.add(department.getId());
            while (department != null && department.getParent() != null) {
                groupOrgs.add(department.getParent().getId());
                department = department.getParent();
            }
        }
        // Set<Group> userGroups = user.getGroups();
        // Iterator<Group> userGroupsIterator = userGroups.iterator();
        // while (userGroupsIterator.hasNext()) {
        // Group group = userGroupsIterator.next();
        // groupOrgs.add(group.getId());
        // while (group != null && group.getParent() != null) {
        // groupOrgs.add(group.getParent().getId());
        // group = group.getParent();
        // }
        // }
        Set<UserJob> userJobs = user.getUserJobs();
        Iterator<UserJob> userJobsIterator = userJobs.iterator();
        while (userJobsIterator.hasNext()) {
            UserJob userJob = userJobsIterator.next();
            groupOrgs.add(userJob.getJob().getId());
            groupOrgs.add(userJob.getJob().getDuty().getId());
        }

        return groupOrgs;
    }

    /* add by huanglinchuan 2015.3.22 begin */
    public List<QueryItem> queryUserWithKeyWords(String keyWords, PagingInfo p) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("keyWords", StringUtils.trimToEmpty(keyWords));
        return nativeDao.namedQuery("queryUserWithKeyWords", values, QueryItem.class, p);
    }

    @Override
    public Set<String> getUserIds(String userId) {
        Set<String> innerIds = new HashSet<String>();
        Set<String> userIds = new HashSet<String>();

        User user = getById(userId);
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUser : departmentUsers) {
            if (departmentUser.getIsMajor()) { // 判断是否是主部门
                Department department = departmentUser.getDepartment(); // 得到自己的部门
                if (department != null) {
                    innerIds = getOwnDeptPrincipal(department, userId); // 获取到部门负责人的id
                    for (String leaderid : innerIds) {
                        if (leaderid.equals(userId)) {
                            List<String> list = departmentService.getAllUserIdsByDepartmentId(
                                    department.getId());
                            userIds.addAll(list);
                            break;
                        }
                    }
                    if (userIds.size() > 0) {
                        // 过滤职级比自己高的
                        for (String leaderid : innerIds) {
                            User leader = getById(leaderid);
                            if (leaderid.equals(userId)) {
                                continue;
                            }
                            if (compareUserIsLeaderInTheSameDept(leader, user, department)) {
                                userIds.remove(leaderid);
                            }
                        }
                    }
                }
            }
        }
        return userIds;
    }

    public Set<String> getOwnDeptPrincipal(Department department, String userId) {
        Set<String> set = new HashSet<String>();
        List<DeptPrincipal> depatPrincipals = deptPrincipalService.getPrincipal(
                department.getUuid());

        if (depatPrincipals.size() > 0) {
            for (DeptPrincipal principal : depatPrincipals) {
                String orgId = principal.getOrgId();
                if (orgId.startsWith(IdPrefix.USER.getValue())) { // 前缀是否是U
                    User user = getById(orgId);
                    if (user.getEnabled() == true) { // 判断用户是否被激活,true为激活状态
                        set.add(orgId);
                    }
                } else if (orgId.startsWith("J")) { // 前缀是否是J
                    List<User> users = jobService.getUsersByJobId(orgId);
                    for (User u : users) {
                        if (u.getEnabled() == true) { // 判断用户是否被激活,true为激活状态
                            set.add(u.getId());
                        }
                    }
                }
            }
        }
        return set;
    }

    @Override
    public void initPassword() {
        List<User> users = this.getAll();
        for (User u : users) {
            String password = passwordEncoder.encodePassword("0", getSalt(u));
            this.updateUserPassword(password, u.getUuid());
        }
    }

    /* add by xieming 2015-04-23 */
    @Override
    public Set<String> getUserDepartmentLeaderIds(String userId) {
        User user = getById(userId);
        return getUserDepartmentLeaderIds(user);
    }

    @Override
    public Set<String> getUserDepartmentLeaderIds(User user) {
        return getUserDepartmentLeaderIds(user, false, false);
    }

    @Override
    public Set<String> getUserAllDepartmentLeaderIds(String userId) {
        User user = getById(userId);
        return getUserAllDepartmentLeaderIds(user);
    }

    @Override
    public Set<String> getUserAllDepartmentLeaderIds(User user) {
        return getUserDepartmentLeaderIds(user, true, false);
    }

    @Override
    public Set<String> getUserDepartmentLeaderIdsHasSelf(String userId) {
        User user = getById(userId);
        return getUserDepartmentLeaderIds(user, false, true);
    }

    @Override
    public Set<String> getUserAllDepartmentLeaderIdsHasSelf(String userId) {
        User user = getById(userId);
        return getUserDepartmentLeaderIds(user, true, true);
    }

    private Set<String> getUserDepartmentLeaderIds(User user, boolean hasGetAll, boolean hasSelf) {
        Set<String> userIds = new HashSet<String>();
        if (user == null) {
            return userIds;
        }
        // 上级领导
        userIds.addAll(getUserLeaderIds(user));
        // 获得用户职位对照的汇报对象
        userIds.addAll(getReportLeaderIds(user));
        // 部门负责人
        userIds.addAll(getDepartmentPrincipals(user,
                Arrays.asList(new String[]{DeptPrincipal.TYPE_PRINCIPAL}),
                hasGetAll, hasSelf));
        return userIds;
    }

    /* add by xieming 2015-04-23 */
    @Override
    public List<String> getUserIdsByDepartment(String departmentUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", departmentUuid);
        return this.dao.query(QUERY_ALL_BY_DEPARTMENT, values);
    }

    @Override
    public Set<String> getUserDepartmentBranchedLeaders(String userId) {
        User user = getById(userId);
        return getDepartmentPrincipals(user,
                Arrays.asList(new String[]{DeptPrincipal.TYPE_BRANCHED}), false, false);
    }

    /**
     * @param user
     * @param types
     * @param hasGetAll True获取当前部门的所有上级部门的部门负责人,false获取最近上级部门有负责人的负责人
     * @param hasSelf   True获取部门领导时，如果自己是部门负责人会获取到自己，false自己是负责人或者上级部门的负责人
     * @return
     */
    private Set<String> getDepartmentPrincipals(User user, List<String> types, boolean hasGetAll,
                                                boolean hasSelf) {
        Set<String> principals = new HashSet<String>();
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            Department department = departmentUserJob.getDepartment();
            principals.addAll(
                    getDepartmentPrincipals(user.getId(), department, types, hasGetAll, hasSelf));
        }
        return principals;
    }

    /**
     * @param userId
     * @param department
     * @param types
     * @param hasGetAll  True获取当前部门的所有上级部门的部门负责人,false获取最近上级部门有负责人的负责人
     * @param hasSelf    True获取部门领导时，如果自己是部门负责人会获取到自己，false自己是负责人或者上级部门的负责人
     * @return
     */
    private Set<String> getDepartmentPrincipals(String userId, Department department,
                                                List<String> types,
                                                boolean hasGetAll, boolean hasSelf) {

        Set<String> userIds = new HashSet<String>();
        Set<String> principals = new HashSet<String>();
        Set<String> branchedPrincipals = new HashSet<String>();
        List<DeptPrincipal> deptPrincipals = new ArrayList<DeptPrincipal>();
        if (types.contains(DeptPrincipal.TYPE_PRINCIPAL)) {
            deptPrincipals.addAll(deptPrincipalService.getPrincipal(department.getUuid()));
        }
        if (types.contains(DeptPrincipal.TYPE_BRANCHED)) {
            deptPrincipals.addAll(deptPrincipalService.getBranched(department.getUuid()));
        }
        for (DeptPrincipal deptPrincipal : deptPrincipals) {
            String orgId = deptPrincipal.getOrgId();
            if (orgId.startsWith(IdPrefix.USER.getValue())) {// U
                User user = getById(orgId);
                if (user != null && user.getEnabled()) {
                    if (DeptPrincipal.TYPE_PRINCIPAL.equals(deptPrincipal.getType())) {
                        principals.add(orgId);
                    } else if (DeptPrincipal.TYPE_BRANCHED.equals(deptPrincipal.getType())) {
                        branchedPrincipals.add(orgId);
                    }
                }
            } else if (orgId.startsWith(IdPrefix.DEPARTMENT.getValue())) {// D
                // 暂时不处理
            } else if (orgId.startsWith("J")) {// J
                List<User> users = jobService.getUsersByJobId(orgId);
                for (User user : users) {
                    if (user.getEnabled()) {
                        if (DeptPrincipal.TYPE_PRINCIPAL.equals(deptPrincipal.getType())) {
                            principals.add(user.getId());
                        } else if (DeptPrincipal.TYPE_BRANCHED.equals(deptPrincipal.getType())) {
                            branchedPrincipals.add(user.getId());
                        }
                    }
                }
            }
        }
        if (!hasSelf && principals.contains(userId)) {
            principals.clear();
        }
        if (branchedPrincipals.contains(userId)) {
            branchedPrincipals.clear();
        }
        userIds.addAll(principals);
        userIds.addAll(branchedPrincipals);
        if ((userIds.size() == 0 || hasGetAll) && department.getParent() != null) {
            userIds.addAll(getDepartmentPrincipals(userId, department.getParent(), types, hasGetAll,
                    hasSelf));
        }
        return userIds;
    }

    @Override
    public void save(User user) {
        this.dao.save(user);
    }

    @Override
    public Set<Role> getRole(String userUuid) {
        // Collection<Role> roles =
        // securityApiFacade.getRolesByUserUuid(userUuid);
        // Set<Role> roleTemp = new HashSet<Role>();
        // for (Role role : roles) {
        // roleTemp.add(role);
        // }
        // return roleTemp;
        return null;
    }

    @Override
    public Set<Privilege> getPrivilege(String userUuid) {
        Set<Privilege> principals = null;// securityApiFacade.getPrivilegesByUserUuid(userUuid);
        return principals;
    }

    @Override
    public String getGroups(String userUuid) {
        User user = this.get(userUuid);
        String groupIds = null;
        // 设置用户所属群组
        // Set<Group> groups = user.getGroups();
        // for (Group group : groups) {
        // if (groupIds == null) {
        // groupIds = group.getId();
        // } else {
        // groupIds = groupIds + ";" + group.getId();
        // }
        // }
        return groupIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getGroupIdsByUserId(java.lang.String)
     */
    @Override
    public List<String> getGroupIdsByUserId(String userId) {
        List<String> groupIds = new ArrayList<String>();
        // User user = this.getById(userId);
        // Set<Group> groups = user.getGroups();
        // for (Group group : groups) {
        // groupIds.add(group.getId());
        // }
        return groupIds;
    }

    @Override
    public String getLeaderIds(String userUuid) {
        User user = this.get(userUuid);
        String leadersIds = null;
        // 设置上级领导
        Set<UserLeader> leaders = user.getLeaders();
        for (UserLeader userLeader : leaders) {
            String ids = userLeader.getLeader().getId();
            if (leadersIds == null) {
                leadersIds = ids;
            } else {
                leadersIds = leadersIds + ";" + ids;
            }
        }
        return leadersIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#filterUserIdsByOrgId(java.util.Collection, java.lang.String)
     */
    @Override
    public List<String> filterUserIdsByOrgId(Collection<String> userIds, String orgId) {
        List<String> returnUserIds = new ArrayList<String>();
        if (userIds.isEmpty()) {
            return returnUserIds;
        }

        // 分离1000个集合参数
        if (userIds.size() < 1000) {
            returnUserIds.addAll(this.filterUserIdsByOrgId(orgId, userIds));
        } else {
            List<String> tmpList = new ArrayList<String>();
            int i = 0;
            for (String userId : userIds) {
                i++;
                tmpList.add(userId);
                if (i % 1000 == 0) {
                    returnUserIds.addAll(this.filterUserIdsByOrgId(orgId, tmpList));
                    tmpList.clear();
                    continue;
                }
            }
            if (!tmpList.isEmpty()) {
                returnUserIds.addAll(this.filterUserIdsByOrgId(orgId, tmpList));
            }
        }
        return returnUserIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#filterDepartmentUserIdsByOrgId(java.util.Collection, java.lang.String)
     */
    @Override
    public List<String> filterDepartmentPrincipalsByOrgId(Collection<String> userIds,
                                                          String orgId) {
        List<String> returnUserIds = new ArrayList<String>();
        if (userIds.isEmpty()) {
            return returnUserIds;
        }

        // 分离1000个集合参数
        if (userIds.size() < 1000) {
            returnUserIds.addAll(this.filterDepartmentPrincipalsByOrgId(orgId, userIds));
        } else {
            List<String> tmpList = new ArrayList<String>();
            int i = 0;
            for (String userId : userIds) {
                i++;
                tmpList.add(userId);
                if (i % 1000 == 0) {
                    returnUserIds.addAll(this.filterDepartmentPrincipalsByOrgId(orgId, tmpList));
                    tmpList.clear();
                    continue;
                }
            }
            if (!tmpList.isEmpty()) {
                returnUserIds.addAll(this.filterDepartmentPrincipalsByOrgId(orgId, tmpList));
            }
        }
        return returnUserIds;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getUserOrganizationsByUserId(java.lang.String)
     */
    @Override
    public List<Organization> getUserOrganizationsByUserId(String userId) {
        return this.getUserOrganizations(userId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getDepartmentIdsByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getDepartmentIdsByUserId(String userId, String orgId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("orgId", orgId);
        return this.nativeDao.namedQuery("getDepartmentIdsByUserId", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getParentDepartmentIdsByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getParentDepartmentIdsByUserId(String userId, String orgId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("orgId", orgId);
        return this.nativeDao.namedQuery("getParentDepartmentIdsByUserId", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getRootDepartmentIdsByUserId(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getRootDepartmentIdsByUserId(String userId, String orgId) {
        List<String> rootDepartmentIds = new ArrayList<String>();
        // 获取用户据部门ID
        List<String> departmentIds = getDepartmentIdsByUserId(userId, orgId);

        for (String departmentId : departmentIds) {
            String tmpParentId = departmentId;
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("departmentId", tmpParentId);
            List<String> parentDepartmentIds = this.nativeDao.namedQuery(
                    "getParentDepartmentIdByDepartmentId", values);
            while (!parentDepartmentIds.isEmpty()) {
                tmpParentId = parentDepartmentIds.get(0);
                values.put("departmentId", tmpParentId);
                parentDepartmentIds = this.nativeDao.namedQuery(
                        "getParentDepartmentIdByDepartmentId", values);
            }
            if (parentDepartmentIds.isEmpty()) {
                rootDepartmentIds.add(tmpParentId);
            }
        }
        return rootDepartmentIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getDirectDepartmentUserIds(java.lang.String, java.lang.String)
     */
    @Override
    public Set<String> getDirectDepartmentUserIds(String userId, String orgId) {
        User user = getById(userId);
        Set<String> userIds = new HashSet<String>();
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            Department department = departmentUserJob.getDepartment();
            if (StringUtils.isBlank(orgId) && !StringUtils.equals(department.getOrgId(), orgId)) {
                continue;
            }
            userIds.addAll(departmentService.getAllUserIdsByDepartmentId(department.getId()));
            obtainParentDeptUserids(userIds, department);
        }
        return userIds;
    }

    @Override
    public void resetUserPassword(String loginName, String tenantId) {
        UserBean userBean = new UserBean();
        Set<Role> beanRoles = new HashSet<Role>();
        Set<Privilege> beanPrivileges = new HashSet<Privilege>();
        User user = this.getByLoginName(loginName.trim());
        Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        String adminPwd = passwordEncoder.encodePassword("0", loginName);
        user.setPassword(adminPwd);
        System.out.println("-------------------------");
        // user.getRoles().size();
        // user.getPrivileges().size();
        // BeanUtils.copyProperties(user.getRoles(), beanRoles);
        // BeanUtils.copyProperties(user.getPrivileges(), beanPrivileges);
        // Set<Role> roles =
        // securityApiFacade.getRolesByUserUuid(user.getUuid());
        // userBean.setRoles(roles);
        Set<Privilege> privileges = null;
        // securityApiFacade.getPrivilegesByUserUuid(user.getUuid());
        userBean.setPrivileges(privileges);
        BeanUtils.copyProperties(user, userBean);
        // System.out.println(userBean.getRoles().size());
        // System.out.println(userBean.getPrivileges().size());
        // userBean.setMajorJobId("J1800002643");

        List<UserJob> userJobs = userJobDao.getMajorJobs(user.getUuid());
        userBean.setMajorJobId((userJobs != null && userJobs.size() > 0) ? userJobs.get(
                0).getJob().getId() : null);

        this.saveBean(userBean);

    }

    @Override
    public List<User> getByTenantId(String tenantId) {
        String hql = "from User u where u.tenantId = :tenantId";
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("tenantId", tenantId);
        return this.dao.query(hql, map);
    }

    @Override
    public Set<User> getUserByRoleUuid(String roleUuid) {
        // TODO Auto-generate#d method stub
        Set<User> users = new HashSet<User>();
//        List<UserRole> userRoles = userRoleService.getUserRoleByRoleUuid(roleUuid);
//        for (UserRole userRole : userRoles) {
//            User user = this.get(userRole.getUserRoleId().getUserUuid());
//            users.add(user);
//        }
        return users;
    }

    /**
     * 作者： yuyq
     * 创建时间：2015-3-26 下午4:18:45
     * 类描述：TODO
     * 备注：设置用户初始化密码为0
     *
     * @param password
     * @param uuid
     */
    public void updateUserPassword(String password, String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("password", password);
        values.put("uuid", uuid);
        this.dao.batchExecute(UPDATE_USER_PASSWORD, values);
    }

    /**
     * @param orgId
     * @param userIds
     * @return
     */
    public List<String> filterUserIdsByOrgId(String orgId, Collection<String> userIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIds", userIds);
        values.put("orgId", orgId);
        return this.dao.query(FILTER_USER_BY_ORG_ID, values);
    }

    /**
     * @param orgId
     * @param userIds
     * @return
     */
    public List<String> filterDepartmentPrincipalsByOrgId(String orgId,
                                                          Collection<String> userIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIds", userIds);
        values.put("orgId", orgId);
        return this.dao.query(FILTER_DEPARTMENT_PRINCIPAL_BY_ORG_ID, values);
    }

    /**
     * @param userId
     * @return
     */
    public List<Organization> getUserOrganizations(String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        return this.dao.query(GET_USER_ORGS, values, Organization.class);
    }

    /**
     * @param oldJobName
     * @param newJobName
     * @author liyb
     * date    2015.1.4
     * 修改用户的主职位名称
     */
    public void updateMajorName(String newJobName, String userUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("newJobName", newJobName);
        values.put("userUuid", userUuid);
        this.dao.batchExecute(UPDATE_USER_MAJORNAME, values);
    }

    /**
     * @param newDeptName
     * @param uuid
     * @author liyb
     * date    2015.1.6
     * 修改用户的部门
     */
    public void updateUserDeptName(String newDeptName, String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("newDeptName", newDeptName);
        values.put("uuid", uuid);
        this.dao.batchExecute(UPDATE_USER_DEPTNAME, values);
    }

    /**
     * @param oldJobName
     * @param newJobName
     * @author liyb
     * date    2015.1.4
     * 修改用户的其他职位名称
     */
    public void updateOtherName(String newJobName, String userUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("newJobName", newJobName);
        values.put("userUuid", userUuid);
        this.dao.batchExecute(UPDATE_USER_OTHERNAME, values);
    }

    @Override
    public List<User> getUserByName(String userName) {
        // TODO Auto-generated method stub
        return this.dao.findBy(User.class, "userName", userName);
    }

    @Override
    public List<User> findBy(String pro, String userCode) {
        // TODO Auto-generated method stub
        return this.dao.findBy(User.class, pro, userCode);
    }

    @Override
    public Query createSQLQuery(String createSearchSql) {
        // TODO Auto-generated method stub
        return this.dao.getSession().createSQLQuery(createSearchSql);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#saveUserProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void saveUserProperty(String propertyName, String propertyValue) {
        UserProperty userProperty = new UserProperty();
        userProperty.setName(propertyName);
        userProperty.setValue(propertyValue);
        userPropertyService.saveUserProperty(userProperty);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getUserProperty(java.lang.String)
     */
    @Override
    public String getUserProperty(String propertyName) {
        UserProperty userProperty = userPropertyService.getUserProperty(propertyName);
        return userProperty.getValue();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#saveUserClobProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void saveUserClobProperty(String propertyName, String propertyClobValue) {
        UserProperty userProperty = new UserProperty();
        userProperty.setName(propertyName);
        userProperty.setClobValue(propertyClobValue);
        userPropertyService.saveUserProperty(userProperty);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserService#getUserClobProperty(java.lang.String)
     */
    @Override
    public String getUserClobProperty(String propertyName) {
        UserProperty userProperty = userPropertyService.getUserProperty(propertyName);
        return userProperty != null ? userProperty.getClobValue() : null;
    }


}
