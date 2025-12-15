/*
 * @(#)2012-12-4 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails;

import com.wellsoft.pt.mt.service.Tenantable;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;

import java.util.List;

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
public interface UserDetails extends org.springframework.security.core.userdetails.UserDetails, Tenantable {

    String getCode();

    String getUserId();

    String getUserUuid();

    String getLoginName();

    String getLoginNameLowerCase();

    String getLoginType();

    String getUserName();

    String getMainJobName();

    String getMainDepartmentId();

    String getMainDepartmentName();

    String getMainDepartmentPath();

    String getLocalMainDepartmentPath();

    String getMainBusinessUnitId();

    String getMainBusinessUnitName();

    List<String> getOtherBusinessUniIds();

    boolean isSuperAdmin();

    boolean isAdmin();

    Object getExtraData(String key);

    void putExtraData(String key, Object value);

    boolean containsExtraData(String key);

    // @Deprecated
    // CommonUnit getCommonUnit(String businessTypeId);
    // @Deprecated
    // void setCommonUnit(String businessTypeId, CommonUnit commonUnit);

    List<OrgTreeNodeDto> getOtherJobs();

    String getEmployeeNumber();

    // void setEmployeeNumber(String employeeNumber);

    /**
     * @return the tokenId
     */
    public String getTokenId();

    /**
     * @param tokenId 要设置的tokenId(SESSIONID)
     */
    public void setTokenId(String tokenId);

    /**
     * 获取系统单位ID
     *
     * @return
     */
    String getSystemUnitId();

    String getPhotoUuid();

    /**
     * 如何描述该方法
     *
     * @return
     */
    OrgTreeNodeDto getMainJob();

    MultiOrgSystemUnit getSystemUnit();

    UserSystemOrgDetails getUserSystemOrgDetails();

    String getLocalUserName();

    String getLocalMainDepartmentName();

    String getUserIp();
}
