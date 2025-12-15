/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service;


import com.wellsoft.pt.di.dao.DiConfigDao;
import com.wellsoft.pt.di.dto.DiConfigDto;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表DI_CONFIG的service服务接口
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-23.1	chenq		2019-07-23		Create
 * </pre>
 * @date 2019-07-23
 */
public interface DiConfigService extends JpaService<DiConfigEntity, DiConfigDao, String> {

    void saveConfig(DiConfigDto diConfigDto);

    List<DiConfigEntity> listByJobUuid(String jobUuid);

    DiConfigDto getDetails(String uuid);

    void deleteDiConfigs(List<String> uuids);

    DiConfigEntity getById(String id);
}
