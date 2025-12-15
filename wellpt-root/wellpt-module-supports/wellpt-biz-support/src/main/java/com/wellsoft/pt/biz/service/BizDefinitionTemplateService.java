/*
 * @(#)11/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizDefinitionTemplateDao;
import com.wellsoft.pt.biz.entity.BizDefinitionTemplateEntity;
import com.wellsoft.pt.biz.query.BizDefinitionTemplateQueryItem;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
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
 * 11/22/22.1	zhulh		11/22/22		Create
 * </pre>
 * @date 11/22/22
 */
public interface BizDefinitionTemplateService extends JpaService<BizDefinitionTemplateEntity, BizDefinitionTemplateDao, String> {

    /**
     * 获取或创建阶段定义模板
     *
     * @param nodeId
     * @param processDefUuid
     * @return
     */
    ProcessNodeConfig getOrCreateNodeDefinition(String nodeId, String processDefUuid);

    /**
     * 获取或创建事项定义模板
     *
     * @param itemId
     * @param processDefUuid
     * @return
     */
    ProcessItemConfig getOrCreateItemDefinition(String itemId, String processDefUuid);

    /**
     * @param processDefUuids
     * @param templateTypes
     * @return
     */
    List<BizDefinitionTemplateQueryItem> listItemByProcessDefUuidsAndTypes(List<String> processDefUuids, List<String> templateTypes);

    /**
     * @param processDefUuid
     * @return
     */
    List<BizDefinitionTemplateEntity> listByProcessDefUuid(String processDefUuid);

    /**
     * @param processDefUuid
     * @param types
     * @return
     */
    boolean existsByProcessDefUuidAndTypes(String processDefUuid, List<String> types);

    /**
     * @param id
     * @param type
     * @param processDefUuid
     */
    void deleteByIdAndTypeAndProcessDefUuid(String id, String type, String processDefUuid);
}
