/*
 * @(#)7/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberCategoryDto;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberCategoryEntity;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberCategoryFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberCategoryService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/11/22.1	zhulh		7/11/22		Create
 * </pre>
 * @date 7/11/22
 */
@Service
public class SnSerialNumberCategoryFacadeServiceImpl extends AbstractApiFacade implements SnSerialNumberCategoryFacadeService {

    @Autowired
    private SnSerialNumberCategoryService snSerialNumberCategoryService;

    /**
     * 获取流水号分类
     *
     * @param uuid
     * @return
     */
    @Override
    public SnSerialNumberCategoryDto getDto(String uuid) {
        SnSerialNumberCategoryDto dto = new SnSerialNumberCategoryDto();
        SnSerialNumberCategoryEntity entity = snSerialNumberCategoryService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 流水号分类按系统单位及名称查询
     *
     * @param name
     * @return
     */
    @Override
    public List<SnSerialNumberCategoryDto> getAllBySystemUnitIdsLikeName(String name) {
        List<SnSerialNumberCategoryEntity> entities = this.snSerialNumberCategoryService.getAllBySystemUnitIdsLikeName(name);
        return BeanUtils.copyCollection(entities, SnSerialNumberCategoryDto.class);
    }

    /**
     * 保存流水号分类
     *
     * @param dto
     */
    @Override
    public void saveDto(SnSerialNumberCategoryDto dto) {
        SnSerialNumberCategoryEntity category = new SnSerialNumberCategoryEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            category = this.snSerialNumberCategoryService.getOne(dto.getUuid());
        } else {
            dto.setSystem(RequestSystemContextPathResolver.system());
            dto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }

        BeanUtils.copyPropertiesExcludeBaseField(dto, category, new String[]{"systemUnitId"});

        this.snSerialNumberCategoryService.save(category);
    }

    /**
     * 删除没用的流水号分类
     *
     * @param uuid
     * @return
     */
    @Override
    public int deleteWhenNotUsed(String uuid) {
        return this.snSerialNumberCategoryService.deleteWhenNotUsed(uuid);
    }

    /**
     * select2查询接口
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        PagingInfo pagingInfo = queryInfo.getPagingInfo();
        List<SnSerialNumberCategoryEntity> entities = snSerialNumberCategoryService.listAllByOrderPage(pagingInfo, "code");
        return new Select2QueryData(entities, "uuid", "name", pagingInfo);
    }

    /**
     * 通过ID查找Text.对于远程查找分页需要实现，否则无法设置选中。
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        String[] uuids = queryInfo.getIds();
        List<SnSerialNumberCategoryEntity> entities = snSerialNumberCategoryService.listByUuids(Arrays.asList(uuids));
        return new Select2QueryData(entities, "uuid", "name", queryInfo.getPagingInfo());
    }

    /**
     * 按编号升序获取全部流水号分类
     *
     * @return
     */
    @Override
    public List<SnSerialNumberCategoryDto> listAllByCodeAsc() {
        List<SnSerialNumberCategoryEntity> entities = snSerialNumberCategoryService.
                listAllByOrderPage(new PagingInfo(1, Integer.MAX_VALUE), "code");
        return BeanUtils.copyCollection(entities, SnSerialNumberCategoryDto.class);
    }

    @Override
    public List<SnSerialNumberCategoryDto> listBySystem(String system) {
        List<SnSerialNumberCategoryEntity> entities = snSerialNumberCategoryService.listBySystem(system);
        return BeanUtils.copyCollection(entities, SnSerialNumberCategoryDto.class);
    }

}
