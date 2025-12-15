/*
 * @(#)3/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.facade.service;

import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.DepartmentEvent;
import com.lark.oapi.service.contact.v3.model.P2DepartmentUpdatedV3Data;
import com.lark.oapi.service.contact.v3.model.UserEvent;
import com.wellsoft.pt.app.feishu.model.DepartmentNode;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;

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
 * 3/20/25.1	    zhulh		3/20/25		    Create
 * </pre>
 * @date 3/20/25
 */
public interface FeishuOrgSyncFacadeService {
    void syncOrg(List<DepartmentNode> departmentNodes, FeishuConfigVo feishuConfigVo);

    void createDepartment(Department department, FeishuConfigVo feishuConfigVo);

    void deleteDepartment(DepartmentEvent departmentEvent, FeishuConfigVo feishuConfigVo);

    void updateDepartment(P2DepartmentUpdatedV3Data p2DepartmentUpdatedV3Data, FeishuConfigVo feishuConfigVo);

    void createUser(UserEvent userEvent, FeishuConfigVo feishuConfigVo);

    void deleteUser(UserEvent userEvent, FeishuConfigVo feishuConfigVo);

    void updateUser(UserEvent userEvent, FeishuConfigVo feishuConfigVo);

    boolean isDepartmentDeleted(String openDepartmentId);
}
