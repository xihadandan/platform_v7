/*
 * @(#)9/29/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizTagDto;

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
public interface BizTagFacadeService extends Facade, Select2QueryApi {

    /**
     * 根据UUID获取业务标签
     *
     * @param uuid
     * @return
     */
    BizTagDto getDto(String uuid);

    /**
     * 保存业务标签
     *
     * @param dto
     */
    void saveDto(BizTagDto dto);

    /**
     * 根据业务标签UUID列表删除业务标签
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

}
