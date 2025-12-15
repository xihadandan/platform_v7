package com.wellsoft.pt.theme.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.theme.dao.impl.ThemeTagDaoImpl;
import com.wellsoft.pt.theme.entity.ThemeTagEntity;
import com.wellsoft.pt.theme.service.ThemeTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
public class ThemeTagServiceImpl extends AbstractJpaServiceImpl<ThemeTagEntity, ThemeTagDaoImpl, Long> implements ThemeTagService {
    @Override
    @Transactional
    public Long createTag(String name) {
        ThemeTagEntity entity = new ThemeTagEntity();
        entity.setName(name);
        save(entity);
        return entity.getUuid();
    }

    @Override
    public List<ThemeTagEntity> getByUuids(List<Long> tagUuids) {
        return this.dao.listByFieldInValues("uuid", tagUuids);
    }
}
