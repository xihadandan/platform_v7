/*
 * @(#)9/29/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.biz.dto.BizTagDto;
import com.wellsoft.pt.biz.entity.BizTagEntity;
import com.wellsoft.pt.biz.facade.service.BizTagFacadeService;
import com.wellsoft.pt.biz.service.BizTagService;
import com.wellsoft.pt.jpa.util.BeanUtils;
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
 * 9/29/22.1	zhulh		9/29/22		Create
 * </pre>
 * @date 9/29/22
 */
@Service
public class BizTagFacadeServiceImpl extends AbstractApiFacade implements BizTagFacadeService {
    @Autowired
    private BizTagService bizTagService;

    /**
     * 根据UUID获取业务标签
     *
     * @param uuid
     * @return
     */
    @Override
    public BizTagDto getDto(String uuid) {
        BizTagDto dto = new BizTagDto();
        BizTagEntity entity = bizTagService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 保存业务标签
     *
     * @param dto
     */
    @Override
    public void saveDto(BizTagDto dto) {
        BizTagEntity entity = new BizTagEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            entity = bizTagService.getOne(dto.getUuid());
        } else {
            // ID唯一性判断
            if (this.bizTagService.countById(dto.getId()) > 0) {
                throw new RuntimeException(String.format("已经存在ID为[%s]的业务标签!", dto.getId()));
            }
        }
        BeanUtils.copyProperties(dto, entity, IdEntity.BASE_FIELDS);
        bizTagService.save(entity);
    }

    /**
     * 根据业务标签UUID列表删除业务标签
     *
     * @param uuids
     */
    @Override
    public void deleteAll(List<String> uuids) {
        bizTagService.deleteByUuids(uuids);
    }

    /**
     * select2查询接口
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        List<BizTagEntity> bizTagEntities = bizTagService.listAllByOrderPage(new PagingInfo(1, Integer.MAX_VALUE), "code asc");
        return new Select2QueryData(bizTagEntities, "id", "name");
    }

    /**
     * 通过ID查找Text.对于远程查找分页需要实现，否则无法设置选中。
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        List<String> tagIds = Arrays.asList(queryInfo.getIds());
        List<BizTagEntity> bizTagEntities = bizTagService.listByIds(tagIds);
        return new Select2QueryData(bizTagEntities, "id", "name");
    }

}
