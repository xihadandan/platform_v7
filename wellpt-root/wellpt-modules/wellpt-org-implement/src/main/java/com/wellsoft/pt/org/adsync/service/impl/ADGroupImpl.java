package com.wellsoft.pt.org.adsync.service.impl;

import com.wellsoft.pt.org.adsycn.service.ADGroupService;
import com.wellsoft.pt.org.adsycn.vo.ADGroup;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.service.DepartmentService;
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
import javax.naming.directory.*;

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
@Service
@Transactional
public class ADGroupImpl implements ADGroupService {
    @Autowired(required = false)
    private LdapTemplate ldapTemplate;
    @Autowired
    private DepartmentService departmentService;
    private Logger logger = LoggerFactory.getLogger(ADDeptImpl.class);

    @Override
    public void add(ADGroup adGroup) {
        Name dn = buildDn(adGroup);

        Attributes attrs = new BasicAttributes();
        Attribute objclass = new BasicAttribute("objectclass");

        objclass.add("top");
        objclass.add("group");

        attrs.put(objclass);

        attrs.put(new BasicAttribute("name", adGroup.getName()));
        attrs.put(new BasicAttribute("cn", adGroup.getCn()));
        attrs.put(new BasicAttribute("sAMAccountName", adGroup.getsAMAccountName()));

        if (adGroup.isCommunicationGroup()) {
            attrs.put(new BasicAttribute("groupType", "2"));  //2为通讯组类型，安全组随机获取组类型
        }
        ldapTemplate.bind(dn, null, attrs);
    }

    protected Name buildDn(ADGroup adgroup) {
        DistinguishedName dn = new DistinguishedName();
        String[] groupPath = adgroup.getDeptPath();
        dn.add("ou", OrgUtil.getAdBaseOu());
        for (int i = 0; i < groupPath.length; i++) {
            if (!StringUtils.isEmpty(groupPath[i])) {
                dn.add("ou", groupPath[i]);
            }
        }
        dn.add("cn", adgroup.getCn());
        return dn;
    }

    public boolean checkIsExist(ADGroup adgroup) {
        try {
            DirContextOperations context = ldapTemplate.lookupContext(this.buildDn(adgroup));
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean checkIsExistByPath(String groupPath) {
        String[] grouppaths = groupPath.split("/");
        try {
            DirContextOperations context = ldapTemplate.lookupContext(this.buildDnByPath(grouppaths));
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    protected Name buildDnByPath(String[] groupPath) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("ou", OrgUtil.getAdBaseOu());
        for (int i = 0; i < groupPath.length; i++) {
            if (!StringUtils.isEmpty(groupPath[i])) {
                dn.add("ou", groupPath[i]);
            }
        }
        return dn;
    }

    @Override
    public void delete(ADGroup adgroup) {
        Name dn = buildDn(adgroup);
        ldapTemplate.unbind(dn);
    }

    @Override
    public void renameDn(ADGroup oldadGroup, ADGroup newadGroup) {
        Name oldDn = buildDn(oldadGroup);
        Name newDn = buildDn(newadGroup);
        ldapTemplate.rename(oldDn, newDn);
    }

    /**
     * 将成员添加到组
     *
     * @param memberDn
     * @param groupDn
     */
    @Override
    public void addMemberToGroup(String memberDn, String groupDn) {
        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("member", memberDn));
        ldapTemplate.modifyAttributes(groupDn, mods);
    }

    /**
     * 从组中删除成员
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.adsync.service.ADGroupService#removerMemberFromGroup(java.lang.String, java.lang.String)
     */
    @Override
    public void removerMemberFromGroup(String memberDn, String groupDn) {
        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("member", memberDn));
        ldapTemplate.modifyAttributes(groupDn, mods);
    }

    @Override
    public String getDnByAdGroup(ADGroup adGroup) {
        Name name = buildDn(adGroup);
        if (name != null) {
            return buildDn(adGroup).toString();
        }
        return "";
    }

    /**
     * 从组中添加组（下级部门的组添加到上级部门中）
     */
    @Override
    public void addInfoToGroup(Department parentDept, String groupDn, int num) {
        ModificationItem[] mods = new ModificationItem[1];
        groupDn = groupDn + "," + OrgUtil.getAdBase();
        String parentGroupDn = getDeptDn(parentDept, num);  //获取父部门的组路径
        if (parentGroupDn == "") {
            return;
        }
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("member", groupDn));
        ldapTemplate.modifyAttributes(parentGroupDn, mods);
    }

    /**
     * 从组中删除组（下级部门的组，在上级组中被删除掉）
     */
    @Override
    public void removerInfoFromGroup(String parentDn, String groupDn, int num) {
        ModificationItem[] mods = new ModificationItem[1];
        String cGroupDn = groupDn + "," + OrgUtil.getAdBase(); //Dn全路径
        String parentGroupDn = getParentDn(parentDn, num);
        if (parentGroupDn == "") {
            return;
        }
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("member", cGroupDn));
        ldapTemplate.modifyAttributes(parentGroupDn, mods);
    }

    private String getParentDn(String parentDn, int num) {
        ADGroup group = new ADGroup();

        String parentName = parentDn.replaceAll("/", "");
        if (num == 0) {
            parentName = parentName + "-通讯组";
            group.setCommunicationGroup(true);   //true为通讯组
        } else if (num == 1) {
            parentName = parentName + "-安全组";
            group.setCommunicationGroup(false);   //false为安全组
        }
        group.setName(parentName);
        group.setCn(parentName);
        group.setsAMAccountName(parentName);

        group.setDeptPath(parentDn.split("/"));

        if (!checkIsExist(group)) {  //判断上级部门是否存在组
            return "";
        }
        return getDnByAdGroup(group);
    }

    @Override
    public String getDeptDn(Department parentDept, int num) {
        ADGroup group = new ADGroup();

        String grouptName = parentDept.getPath();  //获取新部门的全路径
        int start = grouptName.indexOf("/");
        grouptName = (grouptName.substring(start + 1, grouptName.length())).replaceAll("/", "");
        if (num == 0) {
            grouptName = grouptName + "-通讯组";
            group.setCommunicationGroup(true);   //true为通讯组
        } else if (num == 1) {
            grouptName = grouptName + "-安全组";
            group.setCommunicationGroup(false);   //false为安全组
        }
        group.setName(grouptName);
        group.setCn(grouptName);
        group.setsAMAccountName(grouptName);

        String ouPath = parentDept.getPath();   //本部门的全路径
        ouPath = ouPath.substring(ouPath.indexOf("/") + 1, ouPath.length());
        group.setDeptPath(ouPath.split("/"));

        if (!checkIsExist(group)) {  //判断上级部门是否存在组
            return "";
        }
        return getDnByAdGroup(group);
    }
}
