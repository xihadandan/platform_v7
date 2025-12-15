/*
 * @(#)11/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizDefinitionTemplateDto;

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
public interface BizDefinitionTemplateFacadeService extends Facade, Select2QueryApi {

    /**
     * 根据UUID获取业务流程配置项模板
     *
     * @param uuid
     * @return
     */
    BizDefinitionTemplateDto getDto(String uuid);

    /**
     * 保存业务流程配置项模板
     *
     * @param dto
     */
    void saveDto(BizDefinitionTemplateDto dto);

    /**
     * 根据UUID列表删除业务流程配置项模板
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * @param id
     * @param type
     * @param processDefUuid
     */
    void deleteByIdAndTypeAndProcessDefUuid(String id, String type, String processDefUuid);
}
