/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.facade.service;

import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkDeptEntity;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkDepartmentVo;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/18/25.1	    zhulh		4/18/25		    Create
 * </pre>
 * @date 4/18/25
 */
public interface DingtalkOrgSyncFacadeService {
    /**
     * @param departmentVos
     * @param dingtalkConfigVo
     */
    void syncOrg(List<DingtalkDepartmentVo> departmentVos, DingtalkConfigVo dingtalkConfigVo);

    void createUser(OapiV2UserGetResponse.UserGetResponse userGetResponse, DingtalkConfigVo dingtalkConfigVo);

    void updateUser(OapiV2UserGetResponse.UserGetResponse userGetResponse, DingtalkConfigVo dingtalkConfigVo);

    void deleteUser(String userId, DingtalkConfigVo dingtalkConfigVo);

    void activeUser(String userId, DingtalkConfigVo dingtalkConfigVo);

    void createDepartment(OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse, DingtalkConfigVo dingtalkConfigVo);

    void updateDepartment(OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse, DingtalkConfigVo dingtalkConfigVo);

    void deleteDepartment(Long deptId, DingtalkConfigVo dingtalkConfigVo);

    boolean isExistsDepartment(Long deptId);

    DingtalkDeptEntity getDingtalkDeptById(Long deptId);
}
