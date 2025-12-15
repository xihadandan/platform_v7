/*
 * @(#)4/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.DingtalkJobDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkJobEntity;
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
 * 4/20/25.1	    zhulh		4/20/25		    Create
 * </pre>
 * @date 4/20/25
 */
public interface DingtalkJobService extends JpaService<DingtalkJobEntity, DingtalkJobDao, Long> {

    /**
     * @param deptId
     * @param title
     * @return
     */
    String getOrgElementIdByDeptIdAndTitle(Long deptId, String title);

    Long getOrgElementUuidByOrgElementId(String orgElementId);

    Long getOrgElementUuidByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid);
    
    DingtalkJobEntity getByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid);

    List<String> listOrgElementIdByDeptIdsAndOrgVersionUuid(String title, List<Long> deptIds, Long orgVersionUuid);

}
