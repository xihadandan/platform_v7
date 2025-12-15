/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service;

import com.wellsoft.pt.app.weixin.dao.WeixinDeptDao;
import com.wellsoft.pt.app.weixin.entity.WeixinDeptEntity;
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
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
public interface WeixinDeptService extends JpaService<WeixinDeptEntity, WeixinDeptDao, Long> {
    String getOrgElementIdByIdAndConfigUuid(Long id, Long configUuid);

    Long getOrgElementUuidByOrgElementId(String orgElementId);

    List<String> listOrgElementIdByIdsAndOrgVersionUuid(List<Long> ids, Long orgVersionUuid);

    Long getOrgElementUuidByIdAndOrgVersionUuid(Long id, Long orgVersionUuid);

    WeixinDeptEntity getByIdAndOrgVersionUuid(Long id, Long orgVersionUuid);

    boolean existsByIdAndConfigUuid(Long id, Long configUuid);
}
