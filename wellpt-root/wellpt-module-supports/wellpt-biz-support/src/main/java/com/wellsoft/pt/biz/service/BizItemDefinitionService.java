/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.biz.dao.BizItemDefinitionDao;
import com.wellsoft.pt.biz.entity.BizItemDefinitionEntity;
import com.wellsoft.pt.biz.support.ItemData;
import com.wellsoft.pt.biz.support.ItemIncludeItem;
import com.wellsoft.pt.biz.support.ItemMaterial;
import com.wellsoft.pt.biz.support.ItemTimeLimit;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
public interface BizItemDefinitionService extends JpaService<BizItemDefinitionEntity, BizItemDefinitionDao, String> {
    /**
     * 根据业务ID列表获取业务流程定义数量
     *
     * @param businessIds
     * @return
     */
    Long countByBusinessIds(List<String> businessIds);

    /**
     * 根据业务流程定义UUID，获取相同业务的事项定义列表
     *
     * @param processDefUuid
     * @return
     */
    List<BizItemDefinitionEntity> listByProcessDefUuid(String processDefUuid);

    /**
     * 查询事项数据
     *
     * @param processDefUuid
     * @param values
     * @param pagingInfo
     * @return
     */
    List<QueryItem> queryItemData(String processDefUuid, Map<String, Object> values, PagingInfo pagingInfo);

    /**
     * 根据事项定义ID获取事项定义
     *
     * @param id
     * @return
     */
    BizItemDefinitionEntity getById(String id);

    /**
     * 根据业务ID获取事项定义
     *
     * @param businessId
     * @return
     */
    List<BizItemDefinitionEntity> listByBusinessId(String businessId);

    /**
     * 根据ID获取数量
     *
     * @param id
     * @return
     */
    Long countById(String id);

    /**
     * @param entity
     * @return
     */
    long countByEntity(BizItemDefinitionEntity entity);

    /**
     * @param processDefUuid
     * @param itemCode
     * @return
     */
    List<ItemData> listItemDataByProcessDefUuidAndItemCode(String processDefUuid, String... itemCode);


    /**
     * @param businessId
     * @param itemCode
     * @return
     */
    List<ItemData> listItemDataByBusinessIdAndItemCode(String businessId, String... itemCode);

    /**
     * 根据事项编码获取事项办理材料
     *
     * @param id
     * @param itemCode
     * @return
     */
    List<ItemMaterial> listMaterialDataByItemCode(String id, String itemCode);

    /**
     * 根据事项编码获取事项办理时限
     *
     * @param id
     * @param itemCode
     * @return
     */
    List<ItemTimeLimit> listTimeLimitDataByItemCode(String id, String itemCode);

    /**
     * 根据事项编码获取事项包含的事项
     *
     * @param id
     * @param itemCode
     * @return
     */
    List<ItemIncludeItem> listIncludeItemDataByItemCode(String id, String itemCode);

    /**
     * 获取事项表单主表数据
     *
     * @param id
     * @param itemCode
     * @return
     */
    Map<String, Object> getItemFormDataOfMainform(String id, String itemCode);

    /**
     * 获取事项表单材料
     *
     * @param id
     * @param itemCode
     * @return
     */
    List<DyFormData> getItemDyFormDataOfMaterials(String id, String itemCode);

    /**
     * 根据事项编码获取事项包含的互斥事项
     *
     * @param id
     * @param itemCode
     * @return
     */
//    List<ItemMutexItem> listMutexItemDataByItemCode(String id, String itemCode);

    /**
     * 根据事项编码获取事项包含的关联事项
     *
     * @param id
     * @param itemCode
     * @return
     */
//    List<ItemRelateItem> listRelateItemDataByItemCode(String id, String itemCode);

}
