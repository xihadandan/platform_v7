package com.wellsoft.pt.unit.service;

import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.unit.entity.CommonDepartment;

import java.util.List;

/**
 * Description: CommonDepartmentService.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
public interface CommonDepartmentService {
    /**
     * 更新公共库部门是否在集团通讯录中可见
     *
     * @param commonUnitId
     * @param departmentId
     * @param isVisible    是否可见
     */
    void updateCommonDepartmentVisibleByCommonUnit(String commonUnitId, String departmentId, Boolean isVisible);

    /**
     * 更新公共库部门和人员
     *
     * @param srcCommonUnitId  原单位ID
     * @param departmentId     需要做更新的部门ID
     * @param destCommonUnitId 目标单位ID
     * @param isVisible        是否可见
     */
    void updateCommonDepartmentUserByCommonUnit(String srcCommonUnitId, String departmentId, String destCommonUnitId,
                                                Boolean isVisible, String tenantId);

    /**
     * 删除公共库部门
     *
     * @param departmentId
     * @param tenantId
     */
    void deleteCommonDepartment(String departmentId, String tenantId);

    /**
     * 更新公共库中的人员
     *
     * @param commonUnitId 公共库单位ID
     * @param departmentId 部门ID
     * @param user         租户库人员
     * @param updateType   更新类型 1 新增，2 删除
     */
    void updateCommonUser(String commonUnitId, String departmentId, User user, String updateType);

    CommonDepartment getCommonDepartmentById(String commonDepartmentId);

    List<CommonDepartment> getByUnitUuid(String uuid);
}
