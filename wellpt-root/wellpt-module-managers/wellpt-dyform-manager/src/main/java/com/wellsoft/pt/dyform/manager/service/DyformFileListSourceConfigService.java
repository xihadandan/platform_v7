package com.wellsoft.pt.dyform.manager.service;

import com.wellsoft.pt.dyform.manager.dao.DyformFileListSourceConfigDao;
import com.wellsoft.pt.dyform.manager.dto.DyformFileListSourceConfigDto;
import com.wellsoft.pt.dyform.manager.entity.DyformFileListSourceConfig;
import com.wellsoft.pt.jpa.service.JpaService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DyformFileListSourceConfigService extends JpaService<DyformFileListSourceConfig, DyformFileListSourceConfigDao, String> {


    List<DyformFileListSourceConfig> getAllBean();

    void saveAllBean(List<DyformFileListSourceConfigDto> listSourceConfigList);

    @Transactional
    List<DyformFileListSourceConfig> getBeansByIds(List<String> ids);

}
