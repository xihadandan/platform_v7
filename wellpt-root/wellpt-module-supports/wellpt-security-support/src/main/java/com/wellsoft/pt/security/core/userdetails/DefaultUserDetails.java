/*
 * @(#)2012-12-4 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgUserJobDto;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.entity.OrgElementEntity;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-4.1	lilin		2012-12-4		Create
 * </pre>
 * @date 2012-12-4
 */
public class DefaultUserDetails extends User implements UserDetails {

    private static final long serialVersionUID = -8949944190700324625L;
    /**
     * //密码规则校验输出对象
     **/
    public PwdRoleCheckObj pwdRoleCheckObj;
    private Logger log = LoggerFactory.getLogger(DefaultUserDetails.class);
    private OrgUserVo user;
    private String tenant;
    private String tenantId;
    private String loginName;
    // private String userName;
    // private String code;
    // private String userId;
    // private String userUuid;
    private boolean isAdmin = false;
    private boolean isSuperAdmin = false;
    private String loginType;
    // 账号身份 0:普通账号，1：平台管理员，2：业务管理员
    private Integer type;
    // 归属的系统单位ID
    private String systemUnitId;
    private MultiOrgSystemUnit systemUnit;
    // 主职位
    private OrgTreeNodeDto mainJob;
    // 其他职位
    private List<OrgTreeNodeDto> otherJobs = new ArrayList<OrgTreeNodeDto>();
    // 员工工号
    // private String employeeNumber;
    // SESSIONID
    private String tokenId;
    // 归属的业务单位ID
    private String mainBusinessUnitId;
    // 归属的业务单位名称
    private String mainBusinessUnitName;
    private List<String> otherBusinessUniIds;
    private String token;
    private Boolean isAllowMultiDeviceLogin;
    /**
     * 附加数据
     */
    private Map<String, Object> extraData = new HashMap<String, Object>();

    private LoginNextAction loginNextAction;

    private String userIp;

    // 7.0 多系统组织
    private UserSystemOrgDetails userSystemOrgDetails = new UserSystemOrgDetails();


    public DefaultUserDetails(Tenant tenant, OrgUserVo user, Collection<? extends GrantedAuthority> authorities) {
        this(tenant, user, authorities, LoginType.USER);
    }

    public DefaultUserDetails(Tenant tenant, OrgUserVo user, Collection<? extends GrantedAuthority> authorities,
                              String loginType) {
        super(user.getLoginName(), user.getPassword(), true, true, true, true, authorities);
        if (tenant != null) {
            this.tenant = tenant.getAccount();
            this.tenantId = tenant.getId();
        }
        this.user = user;
        this.loginName = user.getLoginName();
        // this.userName = user.getUserName();
        // this.code = user.getCode();
        // this.userId = user.getId();
        // this.userUuid = user.getUuid();
        // this.employeeNumber = user.getEmployeeNumber();
        this.systemUnitId = user.getSystemUnitId();
        if (this.systemUnitId == null) {
            this.systemUnitId = MultiOrgSystemUnit.PT_ID;
        }
        this.setType(user.getType());
        this.isAdmin = false;
        if (user.getType() != null && user.getType() == 1) {
            isAdmin = true;
        }

        this.loginType = loginType;


        try {
            if (DefaultUserDetails.class.equals(this.getClass())) {
                if (!CollectionUtils.isEmpty(user.getJobList())) {
                    for (OrgUserJobDto job : user.getJobList()) {
                        // 判断是否主职
                        boolean isMain = false;
                        if (job.getIsMain() != null && job.getIsMain() == 1) {
                            isMain = true;
                        }
                        if (isMain) {
                            this.mainJob = job.getOrgTreeNodeDto();
                            // 获取主职业最近的归属业务单位ID
                            this.mainBusinessUnitId = user.getMainBusinessUnitId();
                            this.mainBusinessUnitName = user.getMainBusinessUnitName();
                        } else {
                            this.otherJobs.add(job.getOrgTreeNodeDto());
                            // 获取副职业最近的归属业务单位ID
                            if (StringUtils.isNotBlank(user.getOtherBusinessUnitIds())) {
                                this.otherBusinessUniIds = Arrays.asList(user.getOtherBusinessUnitIds().split(";"));
                            }
                        }
                    }
                }

            }

            if (this instanceof SuperAdminDetails) {
                isSuperAdmin = true;
                this.systemUnitId = MultiOrgSystemUnit.PT_ID;
            }

            if (StringUtils.isNotBlank(this.systemUnitId)) {
                if (this.systemUnitId.equals(MultiOrgSystemUnit.PT_ID)) {
                    this.systemUnit = MultiOrgSystemUnit.PT;
                } else {
                    OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
                    this.systemUnit = orgApiFacade.getSystemUnitById(this.systemUnitId);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    public PwdRoleCheckObj getPwdRoleCheckObj() {
        return pwdRoleCheckObj;
    }

    public void setPwdRoleCheckObj(PwdRoleCheckObj pwdRoleCheckObj) {
        this.pwdRoleCheckObj = pwdRoleCheckObj;
    }

    /**
     * @return the code
     */
    @Override
    public String getCode() {
        return user.getCode();
    }

    /**
     * @return the userId
     */
    @Override
    public String getUserId() {
        return user.getId();
    }

    /**
     * @return the userUuid
     */
    @Override
    public String getUserUuid() {
        return user.getUuid();
    }

    @Override
    public String getLoginType() {
        return loginType;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public String getTenant() {
        return tenant;
    }

    /**
     * @return the tenant
     */
    @Override
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @return the loginName
     */
    @Override
    public String getLoginName() {
        return loginName;
    }

    /**
     * @return the userName
     */
    @Override
    public String getUserName() {
        return user.getUserName();
    }

    /**
     * @return
     */
    @Override
    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    /**
     * @return
     */
    @Override
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * @return the extraData
     */
    @Override
    public Object getExtraData(String key) {
        return extraData.get(key);
    }

    // /**
    // * @return the commonUnit
    // */
    // public CommonUnit getCommonUnit(String businessTypeId) {
    // String key = "KEY_COMMON_UNIT" + businessTypeId;
    // if (extraData.containsKey(key)) {
    // return (CommonUnit) extraData.get(key);
    // }
    //
    // UnitApiFacade unitApiFacade =
    // ApplicationContextHolder.getBean(UnitApiFacade.class);
    // List<CommonUnit> tempUnits = unitApiFacade
    // .getCommonUnitsByBusinessTypeIdAndUserId(businessTypeId, user.getId());
    // CommonUnit unit = null;
    // if (!tempUnits.isEmpty()) {
    // unit = new CommonUnit();
    // BeanUtils.copyProperties(tempUnits.get(0), unit);
    // }
    // extraData.put(key, unit);
    // return unit;
    // }
    //
    // /**
    // * @param commonUnit 要设置的commonUnit
    // */
    // public void setCommonUnit(String businessTypeId, CommonUnit commonUnit) {
    // String key = "KEY_COMMON_UNIT" + businessTypeId;
    // extraData.put(key, commonUnit);
    // }

    @Override
    public void putExtraData(String key, Object value) {
        if (extraData.containsKey(key)) {
            log.warn("当前用户会话已经包含键值为[" + key + "]的额外数据");
        }
        extraData.put(key, value);
    }

    @Override
    public boolean containsExtraData(String key) {
        return extraData.containsKey(key);
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((loginName == null) ? 0 : loginName.hashCode());
        result = prime * result + ((user.getId() == null) ? 0 : user.getId().hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultUserDetails other = (DefaultUserDetails) obj;
        if (loginName == null) {
            if (other.loginName != null)
                return false;
        } else if (!loginName.equals(other.loginName))
            return false;
        if (user.getId() == null) {
            if (other.getUserId() != null)
                return false;
        } else if (!user.getId().equals(other.getUserId()))
            return false;
        return true;
    }

    @Override
    public String getEmployeeNumber() {
        return user.getEmployeeNumber();
    }

    /**
     * @return the tokenId
     */
    @Override
    public String getTokenId() {
        return tokenId;
    }

    /**
     * @param tokenId 要设置的tokenId
     */
    @Override
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
     * @return the unitId
     */
    @Override
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @return the mainJob
     */
    @Override
    public OrgTreeNodeDto getMainJob() {
        UserSystemOrgDetails.OrgDetail detail = this.userSystemOrgDetails.currentSystemOrgDetail();
        if (detail != null) {
            return detail.getMainJob();
        }
        return mainJob;
    }

    /**
     * @param mainJob 要设置的mainJob
     */
    public void setMainJob(OrgTreeNodeDto mainJob) {
        this.mainJob = mainJob;
    }

    // 获取主职
    @Override
    public String getMainJobName() {
        UserSystemOrgDetails.OrgDetail detail = this.userSystemOrgDetails.currentSystemOrgDetail();
        if (detail != null) {
            return detail.getMainJob() != null ? detail.getMainJob().getName() : null;
        }
        if (mainJob == null) {
            return null;
        }
        return this.mainJob.getName();
    }

    // 获取主部门ID
    @Override
    public String getMainDepartmentId() {
        UserSystemOrgDetails.OrgDetail detail = this.userSystemOrgDetails.currentSystemOrgDetail();
        if (detail != null) {
            if (detail.getMainDept() != null) {
                return detail.getMainDept().getEleId();
            }
            if (detail.getMainJob() != null) {
                return MultiOrgTreeNode.getNearestEleIdByType(detail.getMainJob().getEleIdPath(), IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue());
            }
            // 不存在主职、主部门情况下取其他职位的作为主部门名称返回
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(detail.getOtherDepts())) {
                return detail.getOtherDepts().get(0).getEleId();
            }
        }
        if (mainJob == null) {
            return null;
        }
        return MultiOrgTreeNode.getNearestEleIdByType(mainJob.getEleIdPath(), IdPrefix.DEPARTMENT.getValue());
    }

    // 获取主部门名称
    @Override
    public String getMainDepartmentName() {
        UserSystemOrgDetails.OrgDetail detail = this.userSystemOrgDetails.currentSystemOrgDetail();
        if (detail != null) {
            if (detail.getMainDept() != null) {
                return detail.getMainDept().getName();
            }
            if (detail.getMainJob() != null) {
                String[] idParts = detail.getMainJob().getEleIdPath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(idParts);
                String[] nameParts = detail.getMainJob().getEleNamePath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(nameParts);
                for (int i = 1, len = idParts.length; i < len; i++) {
                    if (idParts[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                        return nameParts[i];
                    }
                }
            }

            // 不存在主职、主部门情况下取其他职位的作为主部门名称返回
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(detail.getOtherDepts())) {
                return detail.getOtherDepts().get(0).getName();
            }

        }
        String depId = this.getMainDepartmentId();
        if (StringUtils.isBlank(depId)) {
            return "";
        }
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        MultiOrgElement depEle = orgApiFacade.getOrgElementById(depId);
        return depEle == null ? "" : depEle.getName();
    }

    // 获取主部门路径
    @Override
    public String getMainDepartmentPath() {
        UserSystemOrgDetails.OrgDetail detail = this.userSystemOrgDetails.currentSystemOrgDetail();
        if (detail != null) {
            if (detail.getMainDept() != null) {
                return detail.getMainDept().getEleNamePath();
            }
            if (detail.getMainJob() != null) {
                String[] idParts = detail.getMainJob().getEleIdPath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(idParts);
                String[] nameParts = detail.getMainJob().getEleNamePath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(nameParts);
                for (int i = 1, len = idParts.length; i < len; i++) {
                    if (idParts[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                        String[] subarr = (String[]) ArrayUtils.subarray(nameParts, i, len);
                        ArrayUtils.reverse(subarr);
                        return StringUtils.join(subarr, Separator.SLASH.getValue());
                    }
                }
            }
            // 不存在主职、主部门情况下取其他职位的作为主部门名称返回
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(detail.getOtherDepts())) {
                return detail.getOtherDepts().get(0).getEleNamePath();
            }

        }

        if (mainJob == null) {
            return null;
        }
        return this.mainJob.getParentNamePath();
    }

    @Override
    public String getLocalMainDepartmentPath() {
        UserSystemOrgDetails.OrgDetail detail = this.userSystemOrgDetails.currentSystemOrgDetail();
        if (detail != null) {
            if (detail.getMainDept() != null) {
                return StringUtils.defaultIfBlank(detail.getMainDept().getLocalEleNamePath(), detail.getMainDept().getEleNamePath());
            }
            if (detail.getMainJob() != null) {
                String[] idParts = detail.getMainJob().getEleIdPath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(idParts);
                String eleNamePath = StringUtils.defaultIfBlank(detail.getMainJob().getLocalEleNamePath(), detail.getMainJob().getEleNamePath());
                String[] nameParts = eleNamePath.split(Separator.SLASH.getValue());
                ArrayUtils.reverse(nameParts);
                for (int i = 1, len = idParts.length; i < len; i++) {
                    if (idParts[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                        String[] subarr = (String[]) ArrayUtils.subarray(nameParts, i, len);
                        ArrayUtils.reverse(subarr);
                        return StringUtils.join(subarr, Separator.SLASH.getValue());
                    }
                }
            }
            // 不存在主职、主部门情况下取其他职位的作为主部门名称返回
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(detail.getOtherDepts())) {
                return StringUtils.defaultIfBlank(detail.getOtherDepts().get(0).getLocalEleNamePath(), detail.getOtherDepts().get(0).getEleNamePath());
            }

        }

        if (mainJob == null) {
            return null;
        }
        return this.mainJob.getParentLocalNamePath();
    }

    @Override
    public String getMainBusinessUnitId() {
        return mainBusinessUnitId;
    }

    @Override
    public String getMainBusinessUnitName() {
        return mainBusinessUnitName;
    }

    @Override
    public List<String> getOtherBusinessUniIds() {
        return otherBusinessUniIds;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    // 获取其他职位
    @Override
    public List<OrgTreeNodeDto> getOtherJobs() {
        UserSystemOrgDetails.OrgDetail detail = this.userSystemOrgDetails.currentSystemOrgDetail();
        if (detail != null) {
            return detail.getOtherJobs();
        }

        return this.otherJobs;
    }

    /**
     * @return the systemUnit
     */
    @Override
    public MultiOrgSystemUnit getSystemUnit() {
        return systemUnit;
    }

    @Override
    public String getLoginNameLowerCase() {
        return loginName == null ? null : loginName.toLowerCase();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getPhotoUuid() {
        return user.getPhotoUuid();
    }

    public Boolean getIsAllowMultiDeviceLogin() {
        return isAllowMultiDeviceLogin;
    }

    public void setIsAllowMultiDeviceLogin(Boolean allowMultiDeviceLogin) {
        isAllowMultiDeviceLogin = allowMultiDeviceLogin;
    }

    public UserSystemOrgDetails getUserSystemOrgDetails() {
        return userSystemOrgDetails;
    }

    public void setUserSystemOrgDetails(UserSystemOrgDetails userSystemOrgDetails) {
        this.userSystemOrgDetails = userSystemOrgDetails;
    }

    @Override
    public String getLocalUserName() {
        return StringUtils.defaultIfBlank(this.user.getLocalUserName(), this.user.getUserName());
    }

    @Override
    public String getLocalMainDepartmentName() {

        UserSystemOrgDetails.OrgDetail detail = this.userSystemOrgDetails.currentSystemOrgDetail();
        if (detail != null) {
            if (detail.getMainDept() != null) {
                return StringUtils.defaultIfBlank(detail.getMainDept().getLocalName(), detail.getMainDept().getName());
            }
            if (detail.getMainJob() != null) {
                String[] idParts = detail.getMainJob().getEleIdPath().split(Separator.SLASH.getValue());
                ArrayUtils.reverse(idParts);
                String eleNamePath = StringUtils.defaultIfBlank(detail.getMainJob().getLocalEleNamePath(), detail.getMainJob().getEleNamePath());
                String[] nameParts = eleNamePath.split(Separator.SLASH.getValue());
                ArrayUtils.reverse(nameParts);
                for (int i = 1, len = idParts.length; i < len; i++) {
                    if (idParts[i].startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                        return nameParts[i];
                    }
                }
            }

            // 不存在主职、主部门情况下取其他职位的作为主部门名称返回
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(detail.getOtherDepts())) {
                return StringUtils.defaultIfBlank(detail.getOtherDepts().get(0).getLocalName(), detail.getOtherDepts().get(0).getName());
            }

        }
        String depId = this.getMainDepartmentId();
        if (StringUtils.isBlank(depId)) {
            return "";
        }
        OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
        OrgElementEntity depEle = orgFacadeService.getOrgElementByIdAndOrgVersionId(depId, null);
        return depEle == null ? "" : StringUtils.defaultIfBlank(depEle.getLocalName(), depEle.getName());
    }

    public LoginNextAction getLoginNextAction() {
        return loginNextAction;
    }

    public void setLoginNextAction(LoginNextAction loginNextAction) {
        this.loginNextAction = loginNextAction;
    }

    public String getUserNamePy() {
        return this.user.getUserNamePy();
    }

    @Override
    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }
}
