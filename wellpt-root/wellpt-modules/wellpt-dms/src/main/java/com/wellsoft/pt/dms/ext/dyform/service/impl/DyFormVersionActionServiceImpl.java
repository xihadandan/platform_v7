/*
 * @(#)May 31, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.entity.DmsDataVersion;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormVersionActionService;
import com.wellsoft.pt.dms.facade.api.DmsDataVersionFacade;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
@Service
@Transactional
public class DyFormVersionActionServiceImpl implements DyFormVersionActionService {

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private DmsDataVersionFacade dmsDataVersionFacade;

    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormVersionActionService#saveNewVersion(com.wellsoft.pt.dms.core.support.DyFormActionData)
     */
    @Override
    public DyFormData saveNewVersion(DyFormActionData dyFormActionData) {
        return saveNewVersion(StringUtils.EMPTY, StringUtils.EMPTY, dyFormActionData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormVersionActionService#saveNewVersion(com.wellsoft.pt.dms.core.support.DyFormActionData, java.lang.String)
     */
    @Override
    public DyFormData saveNewVersion(String title, String remark, DyFormActionData dyFormActionData) {
        DyFormData sourceDyFormData = dyFormActionData.getDyFormData();
        // 生成新数据
        DyFormData dyFormData = dyFormApiFacade.copyFormData(sourceDyFormData);
        dmsDataVersionFacade.saveNewVersion("dyform", title, sourceDyFormData.getFormUuid(),
                sourceDyFormData.getDataUuid(), dyFormData.getFormUuid(), dyFormData.getDataUuid(), 1, 0.1, remark);

        return dyFormData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormVersionActionService#getAllVersion(java.lang.String, java.lang.String)
     */
    @Override
    public List<DmsDataVersion> getAllVersion(String formUuid, String dataUuid) {
        return this.getAllVersion("dyform", formUuid, dataUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormVersionActionService#getAllVersion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<DmsDataVersion> getAllVersion(String dataType, String formUuid, String dataUuid) {
        List<DmsDataVersion> allVersions = dmsDataVersionFacade.getAllVersions(dataType, formUuid, null, dataUuid);
        Map<String, String> usermap = Maps.newHashMap();
        for (DmsDataVersion dv : allVersions) {
            if (!usermap.containsKey(dv.getCreator())) {
                usermap.put(dv.getCreator(), multiOrgApiFacade.getUserById(dv.getCreator()).getUserName());
            }
            dv.setCreatorName(usermap.get(dv.getCreator()));
        }
        return allVersions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormVersionActionService#deleteAllVersion(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteAllVersion(String formUuid, String dataUuid) {
        dyFormApiFacade.delFullFormData(formUuid, dataUuid);
        List<DmsDataVersion> allVersions = dmsDataVersionFacade.getAllVersions("dyform", formUuid, null, dataUuid);
        for (DmsDataVersion dmsDataVersion : allVersions) {
            dyFormApiFacade.delFullFormData(dmsDataVersion.getDataDefUuid(), dmsDataVersion.getDataUuid());

            dmsDataVersionFacade.delete(dmsDataVersion.getUuid());
        }
    }

}
