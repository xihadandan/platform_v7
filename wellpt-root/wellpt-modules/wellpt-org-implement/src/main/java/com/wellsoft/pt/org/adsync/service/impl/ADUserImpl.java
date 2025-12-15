package com.wellsoft.pt.org.adsync.service.impl;

import com.wellsoft.pt.org.adsycn.service.ADGroupService;
import com.wellsoft.pt.org.adsycn.service.ADUserService;
import com.wellsoft.pt.org.adsycn.vo.ADUser;
import com.wellsoft.pt.org.service.impl.OrgUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ADUserImpl implements ADUserService {
    /**
     * The default maximum number of results to return.
     */
    private static final int DEFAULT_MAX_NUMBER_OF_RESULTS = 1000;
    /**
     * The default timeout.
     */
    private static final int DEFAULT_TIMEOUT = 1000;
    private Logger logger = LoggerFactory.getLogger(ADDeptImpl.class);
    private int scope = SearchControls.SUBTREE_SCOPE;
    @Autowired(required = false)
    private LdapTemplate ldapTemplate;
    @Autowired
    private ADGroupService adGroupService;
    /**
     * The maximum number of results to return.
     */
    private int maxNumberResults = DEFAULT_MAX_NUMBER_OF_RESULTS;
    /**
     * The amount of time to wait.
     */
    private int timeout = DEFAULT_TIMEOUT;

    @Override
    public void add(ADUser adUser) throws Exception {

        Name dn = buildDn(adUser);

        Attributes attrs = new BasicAttributes();
        Attribute objclass = new BasicAttribute("objectclass");

        objclass.add("top");
        objclass.add("person");
        objclass.add("organizationalPerson");
        objclass.add("user");
        attrs.put(objclass);

        attrs.put(new BasicAttribute("name", adUser.getName()));

        attrs.put(new BasicAttribute("cn", adUser.getCn()));

        attrs.put(new BasicAttribute("sn", adUser.getSn()));

        attrs.put(new BasicAttribute("displayName", adUser.getDisplayName()));

        attrs.put(new BasicAttribute("userPrincipalName", adUser.getUserPrincipalName()));

        attrs.put(new BasicAttribute("givenName", adUser.getGivenName()));

        attrs.put(new BasicAttribute("sAMAccountName", adUser.getsAMAccountName()));

        if (!StringUtils.isEmpty(adUser.getMail())) {
            attrs.put(new BasicAttribute("mail", adUser.getMail()));
        }

        if (!StringUtils.isEmpty(adUser.getFacsimileTelephoneNumber())) {
            attrs.put(new BasicAttribute("facsimileTelephoneNumber", adUser.getFacsimileTelephoneNumber()));
        }

        if (!StringUtils.isEmpty(adUser.getHomePhone())) {
            attrs.put(new BasicAttribute("homePhone", adUser.getHomePhone()));
        }

        if (!StringUtils.isEmpty(adUser.getMobile())) {
            attrs.put(new BasicAttribute("mobile", adUser.getMobile()));
        }

        if (!StringUtils.isEmpty(adUser.getTelephoneNumber())) {
            attrs.put(new BasicAttribute("telephoneNumber", adUser.getTelephoneNumber()));
        }

        if (adUser.isEnabled()) {
            attrs.put(new BasicAttribute("userAccountControl", "66080"));
        } else {
            attrs.put(new BasicAttribute("userAccountControl", "514"));
        }

        String newPassword = adUser.getPwd();
        //密码不为空时才进行修改
        if (!StringUtils.isEmpty(newPassword)) {
            String newQuotedPassword = "\"" + newPassword + "\"";
            byte[] newUnicodePassword;
            try {
                newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
                attrs.put(new BasicAttribute("unicodePwd", newUnicodePassword));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                logger.error(e.getMessage(), e);
            }
        }

        // userAccountControl: 66082禁用 66080启用
        ldapTemplate.bind(dn, null, attrs);

        String userDn = getDnByLoginName(adUser.getsAMAccountName());
        //adGroupService.addMemberToGroup(userDn, getUserGroupDn(adUser).toString());
    }

    @Override
    public void update(ADUser adUser) throws Exception {
        DirContextOperations context = ldapTemplate.lookupContext(this.buildDn(adUser));
        context.getObjectAttribute("sAMAccountName");
        context.setAttributeValue("name", adUser.getName());
        //context.setAttributeValue("cn", adUser.getCn()); //通过rename 更新
        context.setAttributeValue("sn", adUser.getSn());
        context.setAttributeValue("displayName", adUser.getDisplayName());
        context.setAttributeValue("mail", adUser.getMail());
        context.setAttributeValue("userPrincipalName", adUser.getUserPrincipalName());
        context.setAttributeValue("givenName", adUser.getGivenName());
        context.setAttributeValue("sAMAccountName", adUser.getsAMAccountName());

        context.setAttributeValue("facsimileTelephoneNumber", adUser.getFacsimileTelephoneNumber());
        context.setAttributeValue("homePhone", adUser.getHomePhone());
        context.setAttributeValue("mobile", adUser.getMobile());
        context.setAttributeValue("telephoneNumber", adUser.getTelephoneNumber());

        if (adUser.isEnabled()) {
            context.setAttributeValue("userAccountControl", "66080");
        } else {
            context.setAttributeValue("userAccountControl", "514");
        }

        //修改时如果DN重复 不抛异常了，支持手工调整数据.
        try {
            ldapTemplate.modifyAttributes(context);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        //修改密码
        String userDn = getDnByLoginName(adUser.getsAMAccountName());
        String realdydn = userDn.replace("," + OrgUtil.getAdBase(), "");
        changePwdByDn(realdydn, adUser.getPwd());

    }

    @Override
    public void delete(ADUser adUser) throws Exception {
        Name dn = buildDn(adUser);
        ldapTemplate.unbind(dn);

    }

    protected Name buildDn(ADUser adUser) throws Exception {
        DistinguishedName dn = new DistinguishedName();
        String[] deptPath = adUser.getDeptPath();
        // dn.add("DC", "cn");
        // dn.add("DC", "lcp");
        dn.add("ou", OrgUtil.getAdBaseOu());
        for (int i = 0; i < deptPath.length; i++) {
            if (!StringUtils.isEmpty(deptPath[i])) {
                dn.add("ou", deptPath[i]);
            }

        }
        dn.add("cn", adUser.getCn());
        return dn;
    }

    /**
     * 校验用户是否存在AD中
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.adsync.service.ADUserService#checkIsExist(com.wellsoft.pt.org.adsycn.vo.ADUser)
     */
    @Override
    public boolean checkIsExist(ADUser adUser) throws Exception {
        try {
            DirContextOperations context = ldapTemplate.lookupContext(this.buildDn(adUser));
            return true;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 改变DN (场景：用户部门变更)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.adsync.service.ADUserService#renameDn(com.wellsoft.pt.org.adsycn.vo.ADUser, com.wellsoft.pt.org.adsycn.vo.ADUser)
     */
    @Override
    public void renameDn(ADUser oldadUser, ADUser newadUser) throws Exception {
        Name oldDn = buildDn(oldadUser);
        Name newDn = buildDn(newadUser);
        ldapTemplate.rename(oldDn, newDn);

        //String groupDn = getUserGroupDn(newadUser).toString();
        //组改名时相应的组成员名也自动同步了，所以此处不需要再另外做同步
        //adGroupService.removerMemberFromGroup(oldDn.toString(), groupDn);
        //adGroupService.addMemberToGroup(newDn.toString(), groupDn);
    }

    @Override
    public String getDnByLoginName(String loginName) throws Exception {
        final String base = "OU=" + OrgUtil.getAdBaseOu();
        final String searchFilter = "sAMAccountName=" + loginName;

        final List<String> cns = new ArrayList<String>();
        final SearchControls searchControls = getSearchControls();
        this.ldapTemplate.search(new SearchExecutor() {
            public NamingEnumeration executeSearch(final DirContext context) throws NamingException {
                return context.search(base, searchFilter, searchControls);
            }
        }, new NameClassPairCallbackHandler() {

            public void handleNameClassPair(final NameClassPair nameClassPair) {
                cns.add(nameClassPair.getNameInNamespace());
            }
        });
        String userDn = "";
        if (!cns.isEmpty()) {
            userDn = cns.get(0);
        }
        return userDn;
    }

    private SearchControls getSearchControls() {
        final SearchControls constraints = new SearchControls();
        constraints.setSearchScope(this.scope);
        constraints.setReturningAttributes(new String[0]);
        constraints.setTimeLimit(this.timeout);
        constraints.setCountLimit(this.maxNumberResults);

        return constraints;
    }

    /**
     * 校验用户和密码是否匹配
     *
     * @param userDn
     * @param pwd
     * @return
     */
    @Override
    public boolean checkUserPwd(String userDn, String pwd) throws Exception {
        DirContext test = null;
        String finalDn = userDn;
        try {
            test = ldapTemplate.getContextSource().getContext(finalDn, pwd);
            if (test != null) {
                return true;
            }
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeContext(test);
        }
        return false;
    }

    private void closeContext(final DirContext context) {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void changePwdByDn(String userDn, String pwd) throws Exception {
        if (StringUtils.isEmpty(userDn)) {
            return;
        }
        String newPassword = pwd;
        if (!StringUtils.isEmpty(newPassword)) {
            ModificationItem[] mods = new ModificationItem[1];
            String newQuotedPassword = "\"" + newPassword + "\"";
            byte[] newUnicodePassword;
            try {
                newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
                mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd",
                        newUnicodePassword));

                ldapTemplate.modifyAttributes(userDn, mods);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 获得用户所在组的dn
     *
     * @param adUser
     * @return
     * @throws Exception
     */
    protected Name getUserGroupDn(ADUser adUser) throws Exception {
        DistinguishedName dn = new DistinguishedName();
        String[] deptPath = adUser.getDeptPath();
        dn.add("ou", OrgUtil.getAdBaseOu());
        String lastgroupName = "";
        for (int i = 0; i < deptPath.length; i++) {
            if (!StringUtils.isEmpty(deptPath[i])) {
                dn.add("ou", deptPath[i]);
            }
            if (i == deptPath.length - 1) {
                lastgroupName = deptPath[i];
            }
        }
        dn.add("cn", lastgroupName);
        return dn;
    }

    @Override
    public void renameDn(String oldadUserDn, String newadUserDn) throws Exception {
        ldapTemplate.rename(oldadUserDn, newadUserDn);
    }

    @Override
    public String getDnByAdUser(ADUser adUser) throws Exception {
        Name name = buildDn(adUser);
        if (name != null) {
            return buildDn(adUser).toString();
        }
        return "";
    }

    /**
     * @param loginName
     * @return
     * @throws Exception
     * @author liyb
     * 判断OU=OU中是否存在此用户
     */
    @Override
    public String getOUDnByLoginName(String loginName) throws Exception {
        final String base = "OU=" + "OU";
        final String searchFilter = "sAMAccountName=" + loginName;

        final List<String> cns = new ArrayList<String>();
        final SearchControls searchControls = getSearchControls();
        this.ldapTemplate.search(new SearchExecutor() {
            public NamingEnumeration executeSearch(final DirContext context) throws NamingException {
                return context.search(base, searchFilter, searchControls);
            }
        }, new NameClassPairCallbackHandler() {

            public void handleNameClassPair(final NameClassPair nameClassPair) {
                cns.add(nameClassPair.getNameInNamespace());
            }
        });
        String userDn = "";
        if (!cns.isEmpty()) {
            userDn = cns.get(0);
        }
        return userDn;
    }

}
