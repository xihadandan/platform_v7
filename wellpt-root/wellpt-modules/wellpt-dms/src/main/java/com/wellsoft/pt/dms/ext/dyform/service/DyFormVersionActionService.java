/*
 * @(#)May 31, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.entity.DmsDataVersion;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 31, 2017.1	zhulh		May 31, 2017		Create
 * </pre>
 * @date May 31, 2017
 */
public interface DyFormVersionActionService extends BaseService {
    /**
     * @param dyFormActionData
     * @return
     */
    DyFormData saveNewVersion(DyFormActionData dyFormActionData);

    /**
     * @param dyFormActionData
     * @param remark
     * @return
     */
    DyFormData saveNewVersion(String title, String remark, DyFormActionData dyFormActionData);

    /**
     * @param formUuid
     * @param dataUuid
     */
    List<DmsDataVersion> getAllVersion(String formUuid, String dataUuid);

    /**
     * @param dataType
     * @param formUuid
     * @param dataUuid
     * @return
     */
    List<DmsDataVersion> getAllVersion(String dataType, String formUuid, String dataUuid);

    /**
     * @param formUuid
     * @param dataUuid
     */
    void deleteAllVersion(String formUuid, String dataUuid);

}
