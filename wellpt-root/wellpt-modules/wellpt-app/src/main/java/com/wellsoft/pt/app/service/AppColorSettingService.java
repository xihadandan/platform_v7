/*
 * @(#)2022-04-15 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;


import com.wellsoft.pt.app.dao.AppColorSettingDao;
import com.wellsoft.pt.app.dto.AppColorSettingClassifyDto;
import com.wellsoft.pt.app.entity.AppColorSettingEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表APP_COLOR_SETTING的service服务接口
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-15.1	shenhb		2022-04-15		Create
 * </pre>
 * @date 2022-04-15
 */
public interface AppColorSettingService extends JpaService<AppColorSettingEntity, AppColorSettingDao, String> {

    void saveBean(AppColorSettingClassifyDto appColorSettingSaveDto);

    List<AppColorSettingClassifyDto> getAllBean();

    AppColorSettingClassifyDto getBean(String moduleCode, String type);

    void deleteBean(String moduleCode, String type);
}
