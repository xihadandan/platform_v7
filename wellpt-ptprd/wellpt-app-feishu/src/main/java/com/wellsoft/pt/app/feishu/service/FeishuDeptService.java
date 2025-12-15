package com.wellsoft.pt.app.feishu.service;

import com.wellsoft.pt.app.feishu.dao.FeishuDeptDao;
import com.wellsoft.pt.app.feishu.entity.FeishuDeptEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

public interface FeishuDeptService extends JpaService<FeishuDeptEntity, FeishuDeptDao, Long> {
    String getOrgElementIdByOpenDepartmentIdAndOrgUuid(String openDepartmentId, Long orgUuid);

    Long getOrgElementUuidByOpenDepartmentIdAndOrgVersionUuid(String openDepartmentId, Long orgVersionUuid);

    List<FeishuDeptEntity> listHasLeaderUserIdByOrgVersionUuid(Long orgVersionUuid);

    FeishuDeptEntity getByOpenDepartmentIdAndOrgVersionUuid(String openDepartmentId, Long orgVersionUuid);

    List<String> listOrgElementIdByOpenDepartmentIdsAndOrgVersionUuid(List<String> openDepartmentIds, Long orgVersionUuid);

    String getOrgElementIdByOpenDepartmentId(String openDepartmentId);

    Long getOrgElementUuidByOrgElementId(String orgElementId);

    FeishuDeptEntity getByOpenDepartmentId(String openDepartmentId);
}
