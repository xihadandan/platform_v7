/*
 * @(#)2018年3月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailSignatureDto;
import com.wellsoft.pt.webmail.entity.WmMailSignatureEntity;
import com.wellsoft.pt.webmail.facade.service.WmMailSignatureFacadeService;
import com.wellsoft.pt.webmail.service.WmMailSignatureService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.util.List;

/**
 * Description: 邮件签名门面服务实现
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月9日.1	chenqiong		2018年3月9日		Create
 * </pre>
 * @date 2018年3月9日
 */
@Service
public class WmMailSignatureFacadeServiceImpl extends AbstractApiFacade implements WmMailSignatureFacadeService {

    @Resource
    WmMailSignatureService wmMailSignatureService;

    @Override
    public void deleteSignatures(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            throw new RuntimeException("删除邮件签名，未指定数据范围！");
        }
        wmMailSignatureService.deleteByUuids(uuids);
    }

    @Override
    @Transactional
    public void updateSignature(WmMailSignatureDto dto) {
        if (dto != null && StringUtils.isNotBlank(dto.getUuid())) {
            WmMailSignatureEntity entity = wmMailSignatureService.getOne(dto.getUuid());
            if (entity != null) {
                if (StringUtils.isNotBlank(dto.getSignatureName())) {
                    entity.setSignatureName(dto.getSignatureName());
                }
                if (StringUtils.isNotBlank(dto.getSignatureContent())) {
                    try {
                        entity.setSignatureContent(new SerialClob(dto.getSignatureContent().toCharArray()));
                    } catch (Exception e) {
                        logger.error("", e);
                    }

                }

                if (dto.getIsDefault() != null) {
                    this.updateSignatureDefault(dto.getUuid(), dto.getIsDefault());
                }

                wmMailSignatureService.save(entity);
            }
        }
    }

    @Override
    public void updateSignatureDefault(String uuid, Boolean isDefault) {
        wmMailSignatureService.updateSignatureDefault(uuid, SpringSecurityUtils.getCurrentUserId(), isDefault);
    }

    @Override
    @Transactional
    public String addSinagure(WmMailSignatureDto dto) {
        WmMailSignatureEntity entity = new WmMailSignatureEntity();
        try {
            entity.setSignatureName(dto.getSignatureName());
            entity.setSignatureContent(new SerialClob(dto.getSignatureContent().toCharArray()));
            entity.setUserId(SpringSecurityUtils.getCurrentUserId());
            entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        } catch (Exception e) {
            logger.error("保存签名，拷贝对象异常：", e);
        }
        wmMailSignatureService.save(entity);
        if (dto.getIsDefault()) {
            wmMailSignatureService.updateSignatureDefault(entity.getUuid(), SpringSecurityUtils.getCurrentUserId(),
                    true);
        }
        return entity.getUuid();
    }

    @Override
    public List<WmMailSignatureDto> queryCurrentUserMailSignatures() {
        return wmMailSignatureService.queryUserMailSignatures(SpringSecurityUtils.getCurrentUserId());
    }

    @Override
    public WmMailSignatureDto getSignatureDetail(String uuid) {
        WmMailSignatureDto dto = new WmMailSignatureDto();
        WmMailSignatureEntity entity = wmMailSignatureService.getOne(uuid);
        org.springframework.beans.BeanUtils.copyProperties(entity, dto, "signatureContent");
        Clob content = entity.getSignatureContent();
        try {
            dto.setSignatureContent(IOUtils.toString(content.getCharacterStream()));
        } catch (Exception e) {
            logger.error("查询签名大字段详情异常：", e);
        }
        return dto;
    }

}
