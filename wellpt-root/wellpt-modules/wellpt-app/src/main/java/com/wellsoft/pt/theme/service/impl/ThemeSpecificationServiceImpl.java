package com.wellsoft.pt.theme.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.theme.dao.impl.ThemeSpecificationDaoImpl;
import com.wellsoft.pt.theme.dto.ThemeSpecificationDto;
import com.wellsoft.pt.theme.entity.ThemeSpecificationEntity;
import com.wellsoft.pt.theme.service.ThemeSpecificationService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
@Service
public class ThemeSpecificationServiceImpl extends AbstractJpaServiceImpl<ThemeSpecificationEntity, ThemeSpecificationDaoImpl, Long> implements ThemeSpecificationService {
    @Override
    @Transactional
    public Long saveSpecify(ThemeSpecificationDto dto) {
        ThemeSpecificationEntity entity = new ThemeSpecificationEntity();
        boolean updateEnabled = false;
        if (dto.getNewVersion()) {
            // 获取源版本
            ThemeSpecificationEntity sourceEntity = getOne(dto.getSourceUuid());
            BeanUtils.copyProperties(sourceEntity, entity, Entity.BASE_FIELDS);
            entity.setVersion(dto.getVersion());
            entity.setRemark(dto.getRemark());
            entity.setEnabled(dto.getEnabled());
            updateEnabled = entity.getEnabled();
            dao.save(entity);
        } else {
            entity = dto.getUuid() == null ? new ThemeSpecificationEntity() : getOne(dto.getUuid());
            updateEnabled = (dto.getUuid() == null && BooleanUtils.isTrue(dto.getEnabled()))
                    || (dto.getUuid() != null && !entity.getEnabled() && BooleanUtils.isTrue(dto.getEnabled()));
            BeanUtils.copyProperties(dto, entity, Entity.BASE_FIELDS);
            dao.save(entity);
        }
        if (updateEnabled) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("uuid", entity.getUuid());
            dao.updateByHQL("UPDATE ThemeSpecificationEntity SET ENABLED = false WHERE ENABLED = TRUE and UUID <> :uuid", param);
        }
        return entity.getUuid();
    }

    @Override
    public ThemeSpecificationDto getDetails(Long uuid) {
        ThemeSpecificationEntity entity = getOne(uuid);
        if (entity != null) {
            ThemeSpecificationDto dto = new ThemeSpecificationDto();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }
        return null;
    }

    @Override
    public ThemeSpecificationDto getEnableThemeSpecify() {
        ThemeSpecificationEntity entity = this.dao.getOneByFieldEq("enabled", true);
        if (entity != null) {
            ThemeSpecificationDto dto = new ThemeSpecificationDto();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }
        return null;
    }

    @Override
    public List<ThemeSpecificationDto> getAll() {
        List<ThemeSpecificationEntity> entities = this.listAllByOrderPage(null, " createTime desc ");
        List<ThemeSpecificationDto> dtos = Lists.newArrayListWithCapacity(entities.size());
        for (ThemeSpecificationEntity entity : entities) {
            ThemeSpecificationDto dto = new ThemeSpecificationDto();
            BeanUtils.copyProperties(entity, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    @Transactional
    public void deleteThemeSpecify(Long uuid) {
        this.dao.delete(uuid);
        //TODO: 删除日志
    }
}
