/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.preferences.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.basicdata.preferences.dto.CdUserPreferencesDto;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月8日.1	zhulh		2020年12月8日		Create
 * </pre>
 * @date 2020年12月8日
 */
public interface CdUserPreferencesFacadeService extends Facade {

    /**
     * 保存用户偏好数据
     *
     * @param moduleId
     * @param functionId
     * @param userId
     * @param dataKey
     * @param dataValue
     * @param remark
     * @return
     */
    String save(String moduleId, String functionId, String userId, String dataKey, String dataValue, String remark);

    /**
     * 根据模块ID、功能ID、用户ID、数据键，获取用户偏好数据
     *
     * @param moduleId
     * @param functionId
     * @param userId
     * @param dataKey
     * @return
     */
    CdUserPreferencesDto get(String moduleId, String functionId, String userId, String dataKey);

    /**
     * 根据模块ID、功能ID、用户ID、数据键，获取用户偏好数据值
     *
     * @param moduleId
     * @param functionId
     * @param userId
     * @param dataKey
     * @return
     */
    String getDataValue(String moduleId, String functionId, String userId, String dataKey);

    /**
     * 根据模块ID、功能ID、用户ID、数据键，获取用户偏好数据值
     *
     * @param userId
     * @return
     */
    String getThemeDataValue(String userId);

    /**
     * 根据模块ID、功能ID、用户ID、数据键，删除用户偏好数据值
     *
     * @param moduleId
     * @param functionId
     * @param userId
     * @param dataKey
     */
    void delete(String moduleId, String functionId, String userId, String dataKey);

}
