/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.DmsDocExchangeDyformDao;
import com.wellsoft.pt.dms.dto.DmsDocExchangeDyformDto;
import com.wellsoft.pt.dms.entity.DmsDocExchangeConfigEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeDyformEntity;
import com.wellsoft.pt.dms.service.DmsDocExchangeConfigService;
import com.wellsoft.pt.dms.service.DmsDocExchangeDyformService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_DYFORM的service服务接口实现类
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
@Service
public class DmsDocExchangeDyformServiceImpl extends AbstractJpaServiceImpl<DmsDocExchangeDyformEntity, DmsDocExchangeDyformDao, String> implements DmsDocExchangeDyformService {


    @Autowired
    private DmsDocExchangeConfigService dmsDocExchangeConfigService;

    @Override
    @Transactional
    public String saveOrUpdate(DmsDocExchangeDyformDto dyformDto) {
        DmsDocExchangeDyformEntity dyformEntity = new DmsDocExchangeDyformEntity();
        if (StringUtils.isNotBlank(dyformDto.getUuid())) {
            dyformEntity = this.getOne(dyformDto.getUuid());
        }
        BeanUtils.copyProperties(dyformDto, dyformEntity);
        this.save(dyformEntity);
        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(dyformDto.getDmsDocExchangeConfigUuid());
        if (dyformDto.getEditType().equals("editDesigner")) {
            configEntity.setDmsDocExchangeDyformUuid(dyformEntity.getUuid());
        } else {
            configEntity.setReceiveDyformUuid(dyformEntity.getUuid());
        }
        dmsDocExchangeConfigService.update(configEntity);
        return dyformEntity.getUuid();
    }


}
