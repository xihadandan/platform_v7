/*
 * @(#)2017-04-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.api;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.dms.entity.DmsDataVersion;
import com.wellsoft.pt.dms.service.DmsDataVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * 2017-04-26.1	zhulh		2017-04-26		Create
 * </pre>
 * @date 2017-04-26
 */
@Component
public class DmsDataVersionFacade extends AbstractApiFacade {

    @Autowired
    private DmsDataVersionService dmsDataVersionService;

    /**
     * @param uuid
     * @return
     */
    public DmsDataVersion getVersion(String uuid) {
        return dmsDataVersionService.get(uuid);
    }

    /**
     * @param dataUuid
     * @return
     */
    public DmsDataVersion getVersionByDataUuid(String dataUuid) {
        return dmsDataVersionService.getByDataUuid(dataUuid);
    }

    /**
     * @param string
     * @param formUuid
     * @param object
     * @param dataUuid
     * @return
     */
    public List<DmsDataVersion> getAllVersions(String dataType, String dataDefUuid, String dataDefId, String dataUuid) {
        return dmsDataVersionService.getAllVersions(dataType, dataDefUuid, dataDefId, dataUuid);
    }

    /**
     * 保存数据版本，存在更新，不存在创建
     *
     * @param dataType
     * @param title
     * @param dataDefUuid
     * @param dataUuid
     * @param initVerNumber
     * @param verIncrement
     * @param remark
     */
    public void saveVersion(String dataType, String title, String dataDefUuid, String dataUuid, int initVerNumber,
                            double verIncrement, String remark) {
        dmsDataVersionService.saveVersion(dataType, title, dataDefUuid, null, dataUuid, initVerNumber, verIncrement,
                remark);
    }

    /**
     * @param string
     * @param formUuid
     * @param dataUuid
     * @param formUuid2
     * @param dataUuid2
     * @param i
     * @param d
     */
    public void saveNewVersion(String dataType, String title, String sourceDefUuid, String sourceDataUuid,
                               String dataDefUuid, String dataUuid, int initVerNumber, double verIncrement, String remark) {
        dmsDataVersionService.saveNewVersion(dataType, title, sourceDefUuid, null, sourceDataUuid, dataDefUuid, null,
                dataUuid, initVerNumber, verIncrement, remark);
    }

    /**
     * @param uuid
     */
    public void delete(String uuid) {
        dmsDataVersionService.remove(uuid);
    }

    /**
     * @param string
     * @param formUuid
     * @param object
     * @param dataUuid
     */
    public void deleteVersion(String dataType, String dataDefUuid, String dataDefId, String dataUuid) {
        dmsDataVersionService.deleteVersion(dataType, dataDefUuid, null, dataUuid);
    }

}
