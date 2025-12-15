/*
 * @(#)Jun 8, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.ext.support.ListViewRowData;
import com.wellsoft.pt.dms.facade.api.DmsDataVersionFacade;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
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
 * Jun 8, 2017.1	zhulh		Jun 8, 2017		Create
 * </pre>
 * @date Jun 8, 2017
 */
@Service
@Transactional
public class DyFormActionServiceImpl extends BaseServiceImpl implements DyFormActionService {

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private DmsDataVersionFacade dmsDataVersionFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService#save(com.wellsoft.pt.dyform.facade.dto.DyFormData)
     */
    @Override
    public String save(DyFormData dyFormData) {
        return dyFormApiFacade.saveFormData(dyFormData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService#saveVersion(com.wellsoft.pt.dms.core.context.ActionContext, com.wellsoft.pt.dms.core.support.DyFormActionData)
     */
    @Override
    public String saveVersion(ActionContext actionContext, DyFormActionData dyFormActionData) {
        String formUuid = dyFormActionData.getDyFormData().getFormUuid();
        String dataUuid = dyFormApiFacade.saveFormData(dyFormActionData.getDyFormData());
        DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        String title = actionContext.getDocumentTitle(dyFormData);
        String remark = dyFormActionData.getExtraString("remark");
        dmsDataVersionFacade.saveVersion("dyform", title, formUuid, dataUuid, 1, 0.1, remark);
        return dataUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService#delete(java.util.List, java.lang.String, boolean)
     */
    @Override
    public void delete(List<ListViewRowData> listViewRowDatas, String globalFormUuid, boolean isVersioning) {
        for (ListViewRowData rowData : listViewRowDatas) {
            String formUuid = rowData.getFormUuid();
            if (StringUtils.isBlank(formUuid)) {
                formUuid = globalFormUuid;
            }
            String dataUuid = rowData.getUuid();
            if (dataUuid != null) {
                delete(formUuid, dataUuid, isVersioning);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService#delete(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void delete(String formUuid, String dataUuid, boolean isVersioning) {
        dyFormApiFacade.delFullFormData(formUuid, dataUuid);
        if (isVersioning) {
            dmsDataVersionFacade.deleteVersion("dyform", formUuid, null, dataUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService#logicDelete(java.util.List, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void logicDelete(List<ListViewRowData> listViewRowDatas, String statusField, String statusValue,
                            String globalFormUuid) {
        for (ListViewRowData rowData : listViewRowDatas) {
            String formUuid = rowData.getFormUuid();
            if (StringUtils.isBlank(formUuid)) {
                formUuid = globalFormUuid;
            }
            executeLogicDelete(rowData.getUuid(), formUuid, statusField, statusValue);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService#logicDelete(com.wellsoft.pt.dms.core.support.DyFormActionData, java.lang.String, java.lang.String)
     */
    @Override
    public void logicDelete(DyFormActionData dyFormActionData, String statusField, String statusValue) {
        executeLogicDelete(dyFormActionData.getDataUuid(), dyFormActionData.getFormUuid(), statusField, statusValue);
    }

    /**
     * @param statusField
     * @param statusValue
     * @param rowData
     * @param formUuid
     */
    public void executeLogicDelete(String dataUuid, String formUuid, String statusField, String statusValue) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("dataUuid", dataUuid);
        values.put("statusValue", statusValue);
        // 表名
        String tableName = dyFormApiFacade.getTblNameByFormUuid(formUuid);
        // 更新SQL
        StringBuilder updateSb = new StringBuilder("update ");
        updateSb.append(tableName);
        updateSb.append(" t set t.").append(statusField);
        updateSb.append(" = :statusValue ");
        updateSb.append(" where t.uuid = :dataUuid ");
        // 执行更新
        this.nativeDao.batchExecute(updateSb.toString(), values);
    }

}
