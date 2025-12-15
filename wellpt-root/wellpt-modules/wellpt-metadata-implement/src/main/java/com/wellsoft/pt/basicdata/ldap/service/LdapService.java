package com.wellsoft.pt.basicdata.ldap.service;

import org.springframework.ldap.core.DirContextAdapter;

/**
 * Description: ldap服务
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
public interface LdapService {

    /**
     * 新增
     *
     * @param context
     */
    public void bind(DirContextAdapter context);

    /**
     * 修改
     */
    public void modifyAttributes(DirContextAdapter context);

    /**
     * 删除
     *
     * @param dn
     */
    public void deleteByDn(String dn);
}
