/*
 * @(#)Jun 8, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.ext.support.ListViewRowData;
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
 * Jun 8, 2017.1	zhulh		Jun 8, 2017		Create
 * </pre>
 * @date Jun 8, 2017
 */
public interface DyFormActionService extends BaseService {
    /**
     * 保存表单数据
     *
     * @param dyFormData
     * @param isVersioning
     * @return
     */
    String save(DyFormData dyFormData);

    /**
     * 保存表单数据并保存版本
     *
     * @param actionContext
     * @param dyFormActionData
     * @return
     */
    String saveVersion(ActionContext actionContext, DyFormActionData dyFormActionData);

    /**
     * 删除表单列表数据
     *
     * @param listViewRowDatas
     * @param globalFormUuid
     * @param isVersioning
     */
    void delete(List<ListViewRowData> listViewRowDatas, String globalFormUuid, boolean isVersioning);

    /**
     * 删除表单数据
     *
     * @param formUuid
     * @param dataUuid
     * @param isVersioning
     */
    void delete(String formUuid, String dataUuid, boolean isVersioning);

    /**
     * 视图列表逻辑删除
     *
     * @param listViewRowDatas
     * @param statusField
     * @param statusValue
     * @param globalFormUuid
     */
    void logicDelete(List<ListViewRowData> listViewRowDatas, String statusField, String statusValue, String globalFormUuid);

    /**
     * 表单单据逻辑删除
     *
     * @param dyFormActionData
     * @param statusField
     * @param statusValue
     */
    void logicDelete(DyFormActionData dyFormActionData, String statusField, String statusValue);

}
