/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.DutyRole;

import java.util.List;

/**
 * Description:职务角色
 *
 * </pre>
 */
public interface DutyRoleService {
    /**
     * 获取职务角色中间表
     *
     * @param dutyUuid 职务UUID
     * @return
     */
    public List<DutyRole> getDutyRoleByDutyUuid(String dutyUuid);

    /**
     * 获取职务角色中间表
     *
     * @param roleUuid 角色uuid
     * @return
     */
    public List<DutyRole> getDutyRoleByRoleUuid(String roleUuid);

    /**
     * 根据duty和role删除对应的角色中间表
     *
     * @param dutyUuid
     * @param roleUuid
     */
    public void deleteDutyRoleByDutyUuidAndRoleUuid(String dutyUuid, String roleUuid);

    public void save(DutyRole dutyRole);
}
