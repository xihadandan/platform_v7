package com.wellsoft.pt.basicdata.ldap.impl;

import com.wellsoft.pt.basicdata.ldap.service.LdapService;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Description: LDAP服务实现类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-20.1  zhengky	2014-8-20	  Create
 * </pre>
 * @date 2014-8-20
 */
public class LdapImpl implements LdapService {

    private LdapTemplate ldapTemplate;

    public LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.ldap.service.LdapService#bind(org.springframework.ldap.core.DirContextAdapter)
     */
    @Override
    public void bind(DirContextAdapter context) {
        ldapTemplate.bind(context);
    }

    @Override
    /**
     *
     * 如何描述该方法
     *
     * (non-Javadoc)
     * @see com.wellsoft.pt.basicdata.ldap.service.LdapService#modifyAttributes(org.springframework.ldap.core.DirContextAdapter)
     */
    public void modifyAttributes(DirContextAdapter context) {
        ldapTemplate.modifyAttributes(context);
    }

    @Override
    /**
     *
     * 如何描述该方法
     *
     * (non-Javadoc)
     * @see com.wellsoft.pt.basicdata.ldap.service.LdapService#deleteByDn(java.lang.String)
     */
    public void deleteByDn(String dn) {
        ldapTemplate.unbind(dn);
    }

}
