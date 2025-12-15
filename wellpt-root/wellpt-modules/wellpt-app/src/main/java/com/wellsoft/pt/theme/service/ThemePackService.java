package com.wellsoft.pt.theme.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.theme.dao.impl.ThemePackDaoImpl;
import com.wellsoft.pt.theme.dto.ThemePackDto;
import com.wellsoft.pt.theme.entity.ThemePackEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年06月20日   chenq	 Create
 * </pre>
 */
public interface ThemePackService extends JpaService<ThemePackEntity, ThemePackDaoImpl, Long> {
    ThemePackDto getDetails(Long uuid);

    List<ThemePackDto> query(List<Long> tagUuids, ThemePackEntity.Type type, ThemePackEntity.Status status, PagingInfo page, String keyword);

    Long copyThemePack(Long uuid, String name, ThemePackEntity.Type type, String themeClass);

    Long saveThemePack(ThemePackDto dto);

    void updateStatus(Long uuid, ThemePackEntity.Status status);

    void deleteThemePack(Long uuid);

    List<ThemePackEntity> listDetailsByUuids(List<Long> uuids);

    ThemePackEntity getByThemeClass(String themeClass);

    List<ThemePackEntity> listDetailsByThemeClasses(List<String> themeClass);

    List<ThemePackEntity> getAllPublished(ThemePackEntity.Type type);

    List<ThemePackEntity> listDetailsIgnoreDefJsonByThemeClasses(List<String> themeClass);
}
