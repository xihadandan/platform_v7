package com.wellsoft.pt.security.audit.webservice;

import net.sf.json.JSONObject;

/**
 * Description: 权限查询services(webservice调用)
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-8.1  zhengky	2014-8-8	  Create
 * </pre>
 * @date 2014-8-8
 */
public interface PrivilegeQueryService {

    /**
     * 根据登录名获得用户权限
     *
     * @param loginName
     * @return
     */
    JSONObject getUserPrivileges(String loginName, String[] moduleCodes);

}
