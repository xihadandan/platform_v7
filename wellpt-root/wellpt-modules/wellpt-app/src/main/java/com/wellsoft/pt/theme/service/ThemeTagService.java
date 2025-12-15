package com.wellsoft.pt.theme.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.theme.dao.impl.ThemeTagDaoImpl;
import com.wellsoft.pt.theme.entity.ThemeTagEntity;

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
public interface ThemeTagService extends JpaService<ThemeTagEntity, ThemeTagDaoImpl, Long> {
    Long createTag(String name);

    List<ThemeTagEntity> getByUuids(List<Long> tagUuids);
}
