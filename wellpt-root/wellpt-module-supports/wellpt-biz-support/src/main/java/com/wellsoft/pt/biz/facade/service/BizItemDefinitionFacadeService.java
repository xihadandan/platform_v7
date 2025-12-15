/*
 * @(#)9/29/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizItemDefinitionDto;
import com.wellsoft.pt.biz.dto.BizProcessDefinitionItemIncludeItemDto;
import com.wellsoft.pt.biz.support.ItemMaterial;
import com.wellsoft.pt.biz.support.ItemTimeLimit;
import com.wellsoft.pt.jpa.criteria.QueryContext;

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
 * 9/29/22.1	zhulh		9/29/22		Create
 * </pre>
 * @date 9/29/22
 */
public interface BizItemDefinitionFacadeService extends Facade {
    /**
     * 根据业务事项UUID获取业务事项定义
     *
     * @param uuid
     * @return
     */
    BizItemDefinitionDto getDto(String uuid);

    /**
     * 根据业务事项ID获取业务事项定义
     *
     * @param id
     * @return
     */
    BizItemDefinitionDto getDtoById(String id);

    /**
     * 根据业务ID获取事项定义
     *
     * @param businessId
     * @return
     */
    List<BizItemDefinitionDto> listByBusinessId(String businessId);

    /**
     * 保存业务事项定义
     *
     * @param dto
     */
    void saveDto(BizItemDefinitionDto dto);

    /**
     * 根据业务事项定义UUID列表删除业务事项定义
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * 获取表单定义下拉数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData listDyFormDefinitionSelectData(Select2QueryInfo queryInfo);

    /**
     * 根据表单ID获取表单定义下拉数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getDyFormDefinitionSelectDataByIds(Select2QueryInfo queryInfo);

    /**
     * 查询事项数据
     *
     * @param processDefUuid
     * @param context
     * @return
     */
    List<QueryItem> queryItemData(String processDefUuid, QueryContext context);

    /**
     * 获取事项数据的办理时限列表
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData listTimeLimitDataSelectData(Select2QueryInfo queryInfo);

    /**
     * 根据办理时限事项数据的办理时限列表
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData getTimeLimitDataSelectDataByIds(Select2QueryInfo queryInfo);

    /**
     * 根据事项编码获取事项包含的事项
     *
     * @param id
     * @param itemCode
     * @return
     */
    List<BizProcessDefinitionItemIncludeItemDto> listIncludeItemDataByItemCode(String id, String itemCode);

    /**
     * 根据事项编码获取事项办理时限配置
     *
     * @param id
     * @param itemCode
     * @return
     */
    List<ItemTimeLimit> listTimeLimitDataByItemCode(String id, String itemCode);

    /**
     * 根据事项编码获取事项材料配置
     *
     * @param id
     * @param itemCode
     * @return
     */
    List<ItemMaterial> listMaterialDataByItemCode(String id, String itemCode);

}
