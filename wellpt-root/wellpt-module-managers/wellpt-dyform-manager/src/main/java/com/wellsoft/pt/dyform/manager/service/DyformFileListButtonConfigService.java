package com.wellsoft.pt.dyform.manager.service;

import com.wellsoft.pt.dyform.manager.dao.DyformFileListButtonConfigDao;
import com.wellsoft.pt.dyform.manager.dto.DyformFileListButtonConfigDto;
import com.wellsoft.pt.dyform.manager.entity.DyformFileListButtonConfig;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

public interface DyformFileListButtonConfigService extends JpaService<DyformFileListButtonConfig, DyformFileListButtonConfigDao, String> {


    List<DyformFileListButtonConfig> getAllBean();

    void saveAllBean(List<DyformFileListButtonConfigDto> listButtonConfigDtoList);

    List<DyformFileListButtonConfig> getBeansByIds(List<String> ids);
}
