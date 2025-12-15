/*
 * @(#)2018年3月15日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailRevocationDto;
import com.wellsoft.pt.webmail.dao.WmMailRevocationDao;
import com.wellsoft.pt.webmail.entity.WmMailRevocationEntity;
import com.wellsoft.pt.webmail.service.WmMailRevocationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 邮件撤回服务实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月15日.1	chenqiong		2018年3月15日		Create
 * </pre>
 * @date 2018年3月15日
 */
@Service
public class WmMailRevocationServiceImpl extends
        AbstractJpaServiceImpl<WmMailRevocationEntity, WmMailRevocationDao, String> implements WmMailRevocationService {


    @Override
    @Transactional
    public WmMailRevocationEntity saveMailRevocation(String mailUuid, String toMailAddress,
                                                     Boolean isSuccess) {
        WmMailRevocationEntity entity = new WmMailRevocationEntity();
        entity.setToMailAddress(toMailAddress);
        entity.setIsRevokeSuccess(isSuccess);
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        entity.setMailboxUuid(mailUuid);
        this.dao.save(entity);
        return entity;
    }

    @Override
    public List<WmMailRevocationEntity> queryMailRevocation(WmMailRevocationDto paramDto) {
        WmMailRevocationEntity entity = new WmMailRevocationEntity();
        BeanUtils.copyProperties(paramDto, entity);
        List<WmMailRevocationEntity> entities = this.dao.listByEntity(entity);
        return entities;
    }

}
