/*
 * @(#)2019年9月30日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.bean.DmsDataPermissionDefinitionDto;

import java.util.Collection;
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
 * 2019年9月30日.1	zhulh		2019年9月30日		Create
 * </pre>
 * @date 2019年9月30日
 */
public interface DmsDataPermissionDefinitionFacadeService extends BaseService {

    void saveDto(DmsDataPermissionDefinitionDto dto);

    DmsDataPermissionDefinitionDto getDto(String uuid);

    void remove(String uuid);

    void removeAll(Collection<String> uuids);

    Select2QueryData loadRolesSelectData(Select2QueryInfo queryInfo);

    Select2QueryData loadFieldNamesSelectData(Select2QueryInfo queryInfo);

    /**
     * 获取当前用户数据所有都信息
     *
     * @return
     */
    List<DataItem> getCurrentUserDataOwnerProviders();

    /**
     * 根据UUID及名称判断数据的唯一性
     *
     * @param uuid
     * @param name
     * @return
     */
    boolean checkExistsByUuidAndName(String uuid, String name);

    /**
     * 根据UUID及ID判断数据的唯一性
     *
     * @param uuid
     * @param id
     * @return
     */
    boolean checkExistsByUuidAndId(String uuid, String id);

}
