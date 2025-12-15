package com.wellsoft.pt.org.webservice.service;


/**
 * Description: 组织外部接口查询服务类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-19.1  zhengky	2014-9-19	  Create
 * </pre>
 * @date 2014-9-19
 */
public interface OrgInterfaceQueryService {

    /**
     * 根据用户返回用户的组织信息
     *
     * @param userCode
     * @return
     */
    String getOrgInfoByUserCode(String userCode);

}
