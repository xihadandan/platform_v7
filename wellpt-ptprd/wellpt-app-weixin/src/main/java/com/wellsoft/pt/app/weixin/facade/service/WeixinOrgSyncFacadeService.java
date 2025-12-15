/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
import com.wellsoft.pt.app.weixin.vo.WeixinDepartmentVo;

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
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
public interface WeixinOrgSyncFacadeService extends Facade {
    void syncOrg(List<WeixinDepartmentVo> departmentVos, WeixinConfigVo weixinConfigVo);

    void createUser(WeixinDepartmentVo.User user, WeixinConfigVo weixinConfigVo);

    void updateUser(WeixinDepartmentVo.User user, WeixinConfigVo weixinConfigVo);

    void deleteUser(String userId, WeixinConfigVo weixinConfigVo);

    void createDepartment(WeixinDepartmentVo.Department department, WeixinConfigVo weixinConfigVo);

    void updateDepartment(WeixinDepartmentVo.Department department, WeixinConfigVo weixinConfigVo);

    void deleteDepartment(Long deptId, WeixinConfigVo weixinConfigVo);
}
