/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.DingtalkDeptDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkDeptEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface DingtalkDeptService extends JpaService<DingtalkDeptEntity, DingtalkDeptDao, Long> {
    String getOrgElementIdByDeptId(Long deptId);

    Long getOrgElementUuidByOrgElementId(String orgElementId);

    List<String> listOrgElementIdByDeptIdsAndOrgVersionUuid(List<Long> deptIds, Long orgVersionUuid);

    Long getOrgElementUuidByDeptIdAndOrgVersionUuid(Long deptId, Long orgVersionUuid);

    DingtalkDeptEntity getByDeptIdAndOrgVersionUuid(Long deptId, Long orgVersionUuid);

    DingtalkDeptEntity getByDeptId(Long deptId);
}
