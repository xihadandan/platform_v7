/*
 * @(#)4/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.material.dao.CdMaterialDefinitionDao;
import com.wellsoft.pt.basicdata.material.entity.CdMaterialDefinitionEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 4/27/23.1	zhulh		4/27/23		Create
 * </pre>
 * @date 4/27/23
 */
public interface CdMaterialDefinitionService extends JpaService<CdMaterialDefinitionEntity, CdMaterialDefinitionDao, Long> {

    /**
     * 根据材料编码获取材料
     *
     * @param code
     * @return
     */
    CdMaterialDefinitionEntity getByCode(String code);

    /**
     * 根据材料编码判断材料是否存在
     *
     * @param code
     * @return
     */
    boolean existsByCode(String code);

    /**
     * 根据材料UUID、编码判断材料是否唯一
     *
     * @param uuid
     * @param code
     * @return
     */
    boolean isUnique(Long uuid, String code);

    /**
     * 根据名称查询材料
     *
     * @param name
     * @param pagingInfo
     * @return
     */
    List<CdMaterialDefinitionEntity> queryByName(String name, PagingInfo pagingInfo);

    /**
     * @param codes
     * @return
     */
    List<String> listFormatByCodes(List<String> codes);
}
