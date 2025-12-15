/*
 * @(#)2018年3月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.webmail.bean.WmMailSignatureDto;
import com.wellsoft.pt.webmail.dao.WmMailSignatureDao;
import com.wellsoft.pt.webmail.entity.WmMailSignatureEntity;
import com.wellsoft.pt.webmail.service.WmMailSignatureService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 邮件签名服务实现类
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
public class WmMailSignatureServiceImpl extends
        AbstractJpaServiceImpl<WmMailSignatureEntity, WmMailSignatureDao, String> implements WmMailSignatureService {

    @Override
    @Transactional
    public void updateSignatureDefault(String uuid, String userId, Boolean isDefault) {
        List<WmMailSignatureEntity> signatureEntities = this.dao.listByFieldEqValue("userId", userId);
        if (CollectionUtils.isNotEmpty(signatureEntities)) {
            for (WmMailSignatureEntity entity : signatureEntities) {
                entity.setIsDefault(false);
            }
            this.dao.saveAll(signatureEntities);
        }
        if (BooleanUtils.isTrue(isDefault) && StringUtils.isNotBlank(uuid)) {
            WmMailSignatureEntity entity = this.dao.getOne(uuid);
            entity.setIsDefault(true);
            this.dao.save(entity);
        }
    }

    @Override
    public List<WmMailSignatureDto> queryUserMailSignatures(String currentUserId) {
        List<WmMailSignatureDto> dtoList = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", currentUserId);
        List<WmMailSignatureEntity> wmMailSignatureEntities = listByHQL(
                "from WmMailSignatureEntity where userId=:userId order by isDefault desc,modifyTime desc", params);
        if (CollectionUtils.isEmpty(wmMailSignatureEntities)) {
            return null;
        }
        for (WmMailSignatureEntity entity : wmMailSignatureEntities) {
            WmMailSignatureDto dto = new WmMailSignatureDto();
            org.springframework.beans.BeanUtils.copyProperties(entity, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public WmMailSignatureDto getMailSignatureByNameAndUserId(String signatureName, String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("signatureName", signatureName);
        List<WmMailSignatureEntity> wmMailSignatureEntities = listByHQL(
                "from WmMailSignatureEntity where userId=:userId and signatureName=:signatureName", params);
        if (CollectionUtils.isNotEmpty(wmMailSignatureEntities)) {
            WmMailSignatureDto dto = new WmMailSignatureDto();
            WmMailSignatureEntity entity = wmMailSignatureEntities.get(0);
            org.springframework.beans.BeanUtils.copyProperties(entity, dto, "signatureContent");
            try {
                dto.setSignatureContent(IOUtils.toString(entity.getSignatureContent().getCharacterStream()));
            } catch (Exception e) {
            }
            return dto;
        }
        return null;
    }

}
