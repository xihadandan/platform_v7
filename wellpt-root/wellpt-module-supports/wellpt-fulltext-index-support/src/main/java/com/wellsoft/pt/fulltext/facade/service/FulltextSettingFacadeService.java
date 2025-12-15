/*
 * @(#)6/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.fulltext.dto.FulltextSettingDto;
import com.wellsoft.pt.fulltext.support.FulltextSetting;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/16/25.1	    zhulh		6/16/25		    Create
 * </pre>
 * @date 6/16/25
 */
public interface FulltextSettingFacadeService extends Facade {
    /**
     * @param dto
     */
    void saveDto(FulltextSettingDto dto);

    /**
     * @param type
     * @param system
     * @return
     */
    FulltextSettingDto getByTypeAndSystem(String type, String system);

    /**
     * @param system
     * @return
     */
    FulltextSetting getSettingBySystem(String system);

    List<FulltextSetting> getAllFulltextSettings();

    /**
     * @param uuid
     * @param rebuildRule
     * @param state
     */
    void updateRuleState(Long uuid, FulltextSetting.RebuildRule rebuildRule, String state);
}
