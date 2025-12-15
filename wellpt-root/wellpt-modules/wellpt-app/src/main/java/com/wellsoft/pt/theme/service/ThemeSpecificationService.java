package com.wellsoft.pt.theme.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.theme.dao.impl.ThemeSpecificationDaoImpl;
import com.wellsoft.pt.theme.dto.ThemeSpecificationDto;
import com.wellsoft.pt.theme.entity.ThemeSpecificationEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月19日   Qiong	 Create
 * </pre>
 */
public interface ThemeSpecificationService extends JpaService<ThemeSpecificationEntity, ThemeSpecificationDaoImpl, Long> {
    Long saveSpecify(ThemeSpecificationDto dto);

    ThemeSpecificationDto getDetails(Long uuid);

    ThemeSpecificationDto getEnableThemeSpecify();

    List<ThemeSpecificationDto> getAll();

    void deleteThemeSpecify(Long uuid);
}
