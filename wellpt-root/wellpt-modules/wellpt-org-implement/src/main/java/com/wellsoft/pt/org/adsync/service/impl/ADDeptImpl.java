package com.wellsoft.pt.org.adsync.service.impl;

import com.wellsoft.pt.org.adsycn.service.ADDeptService;
import com.wellsoft.pt.org.adsycn.service.ADUserService;
import com.wellsoft.pt.org.adsycn.vo.ADDept;
import com.wellsoft.pt.org.adsycn.vo.ADUser;
import com.wellsoft.pt.org.dao.DepartmentDao;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.org.service.impl.OrgUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: AD部门同步实现类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-10-14.1  zhengky	2014-10-14	  Create
 * </pre>
 * @date 2014-10-14
 */
@Service
@Transactional
public class ADDeptImpl implements ADDeptService {
    private Logger logger = LoggerFactory.getLogger(ADDeptImpl.class);

    @Autowired(required = false)
    private LdapTemplate ldapTemplate;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ADUserService adUserService;

    @Override
    public void add(ADDept adDept) {
        Name dn = buildDn(adDept);

        Attributes attrs = new BasicAttributes();
        Attribute objclass = new BasicAttribute("objectclass");

        objclass.add("top");
        objclass.add("organizationalUnit");
        attrs.put(objclass);

        attrs.put(new BasicAttribute("name", adDept.getName()));
        attrs.put(new BasicAttribute("ou", adDept.getOu()));

        ldapTemplate.bind(dn, null, attrs);

    }

    protected Name buildDn(ADDept addept) {
        DistinguishedName dn = new DistinguishedName();
        String[] deptPath = addept.getDeptPath();
        dn.add("ou", OrgUtil.getAdBaseOu());
        for (int i = 0; i < deptPath.length; i++) {
            if (!StringUtils.isEmpty(deptPath[i])) {
                dn.add("ou", deptPath[i]);
            }
        }
        dn.add("ou", addept.getOu());
        return dn;
    }

    public boolean checkIsExist(ADDept addept) {
        try {
            DirContextOperations context = ldapTemplate.lookupContext(this.buildDn(addept));
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean checkIsExistByPath(String deptPath) {
        String[] deptpaths = deptPath.split("/");
        try {
            DirContextOperations context = ldapTemplate.lookupContext(this.buildDnByPath(deptpaths));
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    protected Name buildDnByPath(String[] deptPath) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("ou", OrgUtil.getAdBaseOu());
        for (int i = 0; i < deptPath.length; i++) {
            if (!StringUtils.isEmpty(deptPath[i])) {
                dn.add("ou", deptPath[i]);
            }
        }
        return dn;
    }

    @Override
    public void delete(ADDept addept) {
        Name dn = buildDn(addept);
        ldapTemplate.unbind(dn);
    }

    @Override
    public void renameDn(ADDept oldadDept, ADDept newadDept) {
        Name oldDn = buildDn(oldadDept);
        Name newDn = buildDn(newadDept);
        ldapTemplate.rename(oldDn, newDn);
    }

    /**
     * 通过部门获得部门DN
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.adsync.service.ADDeptService#getDnByAdDept(com.wellsoft.pt.org.adsycn.vo.ADDept)
     */
    @Override
    public String getDnByAdDept(ADDept adDept) {
        Name name = buildDn(adDept);
        if (name != null) {
            return buildDn(adDept).toString();
        }
        return "";
    }

    //============================================================================================================

    /**
     * @author liyb  date 2015.1.9
     * 把数据库中的部门、用户的信息同步到AD域名下
     **/
    @Override
    public void adSync() {
        String deptId = "D0000000001";//立达信绿色照明股份有限公司    //"D0000000437"; 绿天光电
        Department department = departmentService.getById(deptId); //获取最顶级的部门
        setADDept(department); //临时的，测试的
        iteratorDeptUser(department); //遍历此部门下所有用户（包括子部门）
    }

    /**
     * 调用AD部门新增方法建立所有部门
     *
     * @param department
     */
    public void setADDept(Department department) {
        if (department == null) {
            return;
        }
        synchronousADDept(department);
        iteratorDeptChild(department);
    }

    /**
     * 迭代遍历子部门(并同步)
     *
     * @param department
     * @return
     */
    public Department iteratorDeptChild(Department department) {
        if (department == null) {
            return null;
        }
        //获取到本部门下的下一级的部门（即子部门）
        List<Department> departments = this.departmentService.getChildrenById(department.getId());
        for (Department dept : departments) {
            setADDept(dept);
        }
        return null;
    }

    public void synchronousADDept(Department department) {
        ADDept adDept = new ADDept();
        adDept.setName(department.getName());
        adDept.setOu(department.getName()); //此部门上级部门的全路径
        String ouNewPath = getDeptOuPath(department);
        //判断ouNewPath是否为空
        if (ouNewPath == null) {
            return;
        }
        adDept.setDeptPath(ouNewPath.split("/"));
        //判断父部门是否存在，如果不存在，则不创建
        if (!checkIsExistByPath(ouNewPath)) {
            return;
        }
        boolean isexist = checkIsExist(adDept);
        if (!isexist) {
            add(adDept);
        }
    }

    private String getDeptOuPath(Department department) {
        if (department == null) {
            return null;
        }
        //获取父部门
        Department parentDept = departmentService.getParentDepartment(department.getUuid());
        if (parentDept == null) {
            return null;
        }
        String sdeptPath = "";
        if (departmentService.getParentDepartment(parentDept.getUuid()) != null) {
            sdeptPath = parentDept.getPath(); //得到父部门的全路径
            sdeptPath = sdeptPath.substring(sdeptPath.indexOf("/") + 1, sdeptPath.length());
        }
        return sdeptPath;
    }

    /**
     * 把用户同步到AD域账号部门下
     * 通过部门获取用户
     *
     * @param department
     */
    private void iteratorDeptUser(Department department) {
        Set<String> setIds = new HashSet<String>();
        List<String> uIds = departmentService.getAllUserIdsByDepartmentId(department.getId());
        setIds.addAll(uIds); //把有相同的id去掉
        synchronousADUser(setIds);
    }

    /**
     * 实现用户同步
     *
     * @param userIds
     */
    private void synchronousADUser(Set<String> userIds) {
        for (String userId : userIds) {
            User user = userService.getById(userId);
            if (user == null) {
                continue;
            }
            addUserToAD(user);
        }
    }

    /**
     * 把用户同步到域账号的部门下
     *
     * @param user
     */
    private void addUserToAD(User user) {
        String fullName = user.getUserName(); //全名
        String familyName = ""; //姓氏
        String givenName = ""; //名
        if (fullName.length() >= 4) {
            familyName = fullName.substring(0, 2); //双姓氏
            givenName = fullName.substring(2, fullName.length());
        } else {
            familyName = fullName.substring(0, 1);
            givenName = fullName.substring(1, fullName.length());
        }
        Department department = userService.getDepartmentByUserId(user.getId()); //获取到主部门
        String sdeptPath = department.getPath(); //获取到主部门的全路径
        String ouNewPath = ""; //AD中的部门路径
        if (sdeptPath.indexOf("/") >= 0) {
            //去掉顶级部门后的部门全路径
            ouNewPath = sdeptPath.substring(sdeptPath.indexOf("/") + 1, sdeptPath.length());
        }
        //判断部门是否存在，如果不存在，则返回
        if (!checkIsExistByPath(ouNewPath)) {
            return;
        }
        ADUser adUser = new ADUser();
        String loginName = user.getLoginName();
        adUser.setCn(fullName);
        adUser.setSn(familyName);
        adUser.setDeptPath(ouNewPath.split("/"));
        adUser.setDisplayName(fullName);
        adUser.setGivenName(givenName);
        adUser.setMail(user.getMainEmail());
        adUser.setName(fullName);
        adUser.setPwd(user.getPassword());
        adUser.setsAMAccountName(loginName);
        adUser.setUserPrincipalName(loginName + "@" + OrgUtil.getAdPrincipalnameSuffix());
        adUser.setHomePhone(user.getHomePhone());
        adUser.setFacsimileTelephoneNumber(user.getFax());
        adUser.setMobile(user.getMobilePhone());
        adUser.setTelephoneNumber(user.getOfficePhone());
        adUser.setEnabled(user.getEnabled());
        try {
            //判断部门是否存在，如果不存在，则返回false--->return
            if (!checkIsExistByPath(ouNewPath)) {
                return;
            }
            String userDn = adUserService.getOUDnByLoginName(adUser.getsAMAccountName());
            if (!StringUtils.isEmpty(userDn)) {
                String newDn = adUserService.getDnByAdUser(adUser);
                //先重命名CN
                String realdydn = userDn.replace("," + OrgUtil.getAdBase(), "");
                adUserService.renameDn(realdydn, newDn);
                //再更新对应的值
                adUserService.update(adUser);
            } else {
                adUserService.add(adUser);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }
    }
}
