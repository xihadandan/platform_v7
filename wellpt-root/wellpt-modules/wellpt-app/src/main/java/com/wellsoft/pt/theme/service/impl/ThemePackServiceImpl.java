package com.wellsoft.pt.theme.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.theme.dao.impl.ThemePackDaoImpl;
import com.wellsoft.pt.theme.dto.ThemePackDto;
import com.wellsoft.pt.theme.entity.ThemePackEntity;
import com.wellsoft.pt.theme.entity.ThemePackTagEntity;
import com.wellsoft.pt.theme.entity.ThemeTagEntity;
import com.wellsoft.pt.theme.service.ThemePackService;
import com.wellsoft.pt.theme.service.ThemeSpecificationService;
import com.wellsoft.pt.theme.service.ThemeTagService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
public class ThemePackServiceImpl extends AbstractJpaServiceImpl<ThemePackEntity, ThemePackDaoImpl, Long> implements ThemePackService {

    @Resource
    ThemeTagService themeTagService;

    @Resource
    ThemeSpecificationService themeSpecificationService;


    @Override
    public ThemePackDto getDetails(Long uuid) {
        ThemePackEntity entity = getOne(uuid);
        if (entity != null) {
            ThemePackDto dto = new ThemePackDto();
            BeanUtils.copyProperties(entity, dto);
            List<Long> tagUuids = this.dao.getThemePackTagUuids(uuid);
            if (CollectionUtils.isNotEmpty(tagUuids)) {
                List<ThemeTagEntity> tagEntities = themeTagService.getByUuids(tagUuids);
                if (CollectionUtils.isNotEmpty(tagEntities)) {
                    List<String> tagNames = Lists.newArrayListWithCapacity(tagEntities.size());
                    List<Long> tagUuidList = Lists.newArrayListWithCapacity(tagEntities.size());
                    for (ThemeTagEntity tag : tagEntities) {
                        tagUuidList.add(tag.getUuid());
                        tagNames.add(tag.getName());
                    }
                    dto.setTagUuids(tagUuidList);
                    dto.setTagNames(tagNames);
                }
            }
            return dto;
        }
        return null;
    }

    @Override
    public List<ThemePackDto> query(List<Long> tagUuids, ThemePackEntity.Type type, ThemePackEntity.Status status, PagingInfo page, String keyword) {
        Map<String, Object> param = Maps.newHashMap();
        StringBuilder sql = new StringBuilder("SELECT uuid  , name  , type   , " +
                " theme_class , status  , remark  , theme_colors, default_theme_color ,logo,thumbnail  from theme_pack p where 1=1 ");
        if (CollectionUtils.isNotEmpty(tagUuids)) {
            param.put("tagUuids", tagUuids);
            sql.append(" AND EXISTS ( SELECT 1 FROM THEME_PACK_TAG t where t.tag_uuid in (:tagUuids) and t.pack_uuid = p.uuid )");
        }
        if (type != null) {
            param.put("type", type.ordinal());
            sql.append(" and p.type = :type");
        }
        if (status != null) {
            param.put("status", status.ordinal());
            sql.append(" and p.status = :status");
        }
        if (StringUtils.isNotBlank(keyword)) {
            sql.append(" and ( p.name like :keyword or p.remark like :keyword ) ");
            param.put("keyword", "%" + keyword + "%");
        }
        sql.append(" order by create_time desc");
        List<ThemePackEntity> entities = page != null ? this.dao.listBySQLAndPage(sql.toString(), param, page)
                : this.dao.listBySQL(sql.toString(), param);
        if (CollectionUtils.isNotEmpty(entities)) {
            List<ThemePackDto> packDtos = Lists.newArrayListWithCapacity(entities.size());
            for (ThemePackEntity packEntity : entities) {
                ThemePackDto dto = new ThemePackDto();
                BeanUtils.copyProperties(packEntity, dto);
                packDtos.add(dto);
            }
            return packDtos;
        }
        return null;
    }

    @Override
    @Transactional
    public Long copyThemePack(Long uuid, String name, ThemePackEntity.Type type, String themeClass) {
        ThemePackEntity entity = getOne(uuid);
        if (entity != null) {
            ThemePackEntity newPack = new ThemePackEntity();
            BeanUtils.copyProperties(entity, newPack, Entity.BASE_FIELDS);
            if (StringUtils.isNotBlank(name)) {
                newPack.setName(name);
            }
            if (StringUtils.isNotBlank(themeClass)) {
                newPack.setThemeClass(themeClass);
            }
            if (type != null) {
                newPack.setType(type);
            }
            save(newPack);
            List<Long> tagUuids = this.dao.getThemePackTagUuids(uuid);
            if (CollectionUtils.isNotEmpty(tagUuids)) {
                List<ThemePackTagEntity> packTagEntities = Lists.newArrayListWithCapacity(tagUuids.size());
                for (Long tu : tagUuids) {
                    packTagEntities.add(new ThemePackTagEntity(tu, newPack.getUuid()));
                }
                this.dao.savePackTags(packTagEntities);
            }

        }
        return null;
    }

    @Override
    @Transactional
    public Long saveThemePack(ThemePackDto dto) {
        ThemePackEntity entity = dto.getUuid() != null ? getOne(dto.getUuid()) : new ThemePackEntity();
        if (dto.getUuid() == null) {
            BeanUtils.copyProperties(dto, entity);
        } else {
            BeanUtils.copyProperties(dto, entity, Entity.BASE_FIELDS);
            // 删除关联标签
            this.dao.deleteThemePackTag(dto.getUuid());
        }
        save(entity);
        if (CollectionUtils.isNotEmpty(dto.getTagUuids())) {
            List<ThemePackTagEntity> packTagEntities = Lists.newArrayListWithCapacity(dto.getTagUuids().size());
            for (Long tu : dto.getTagUuids()) {
                packTagEntities.add(new ThemePackTagEntity(tu, entity.getUuid()));
            }
            this.dao.savePackTags(packTagEntities);
        }
        return entity.getUuid();
    }

    @Override
    @Transactional
    public void updateStatus(Long uuid, ThemePackEntity.Status status) {
        ThemePackEntity entity = getOne(uuid);
        if (entity != null) {
            entity.setStatus(status);
            save(entity);
        }
    }

    @Override
    @Transactional
    public void deleteThemePack(Long uuid) {
        delete(uuid);
        this.dao.deleteThemePackTag(uuid);
    }


    @Override
    public List<ThemePackEntity> listDetailsByUuids(List<Long> uuids) {
        return dao.listByFieldInValues("uuid", uuids);
    }

    @Override
    public ThemePackEntity getByThemeClass(String themeClass) {
        return dao.getOneByFieldEq("themeClass", themeClass);
    }

    @Override
    public List<ThemePackEntity> listDetailsByThemeClasses(List<String> themeClass) {
        return dao.listByFieldInValues("themeClass", themeClass);

    }

    @Override
    public List<ThemePackEntity> getAllPublished(ThemePackEntity.Type type) {
        ThemePackEntity packEntity = new ThemePackEntity();
        if (type != null) {
            packEntity.setType(type);
        }
        packEntity.setStatus(ThemePackEntity.Status.PUBLISHED);
        return dao.listByEntity(packEntity);
    }

    @Override
    public List<ThemePackEntity> listDetailsIgnoreDefJsonByThemeClasses(List<String> themeClass) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("themeClass", themeClass);
        return this.dao.listBySQL("select name,uuid,theme_class,theme_colors,default_theme_color," +
                "font_sizes from theme_pack where theme_class in (:themeClass)", params);
    }


}
