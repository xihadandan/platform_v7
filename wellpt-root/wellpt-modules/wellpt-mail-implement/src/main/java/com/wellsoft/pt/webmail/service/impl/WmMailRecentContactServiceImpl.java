/*
 * @(#)2018年3月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.webmail.bean.WmMailRecentContactDto;
import com.wellsoft.pt.webmail.dao.WmMailRecentContactDao;
import com.wellsoft.pt.webmail.entity.WmMailRecentContactEntity;
import com.wellsoft.pt.webmail.service.WmMailRecentContactService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Description: 最近联系人服务接口类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月19日.1	chenqiong		2018年3月19日		Create
 * </pre>
 * @date 2018年3月19日
 */
@Service
public class WmMailRecentContactServiceImpl extends
        AbstractJpaServiceImpl<WmMailRecentContactEntity, WmMailRecentContactDao, String> implements
        WmMailRecentContactService {

    @Override
    @Transactional
    public void deleteUserRecentContact(String currentUserId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", currentUserId);
        this.dao.deleteByHQL("delete from WmMailRecentContactEntity where userId=:userId", params);
    }

    @Override
    public List<WmMailRecentContactDto> queryRecentContactByUserId(String currentUserId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", currentUserId);
        List<WmMailRecentContactEntity> mailRecentContactEntities = this.dao.listByHQL(
                "from WmMailRecentContactEntity where userId=:userId order by lastContactTime desc", params);
        if (CollectionUtils.isNotEmpty(mailRecentContactEntities)) {
            List<WmMailRecentContactDto> dtoList = Lists.newArrayList();
            for (WmMailRecentContactEntity entity : mailRecentContactEntities) {
                WmMailRecentContactDto dto = new WmMailRecentContactDto();
                BeanUtils.copyProperties(entity, dto);
                dtoList.add(dto);
            }
            return dtoList;
        }
        return null;
    }

    @Override
    @Transactional
    public void saveRecentContact(List<WmMailRecentContactDto> dtoList) {
        List<WmMailRecentContactEntity> entities = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        for (WmMailRecentContactDto dto : dtoList) {
            params.put("userId", dto.getUserId());
            params.put("address", dto.getContacterMailAddress());
            WmMailRecentContactEntity entity = new WmMailRecentContactEntity();
            // 查看之前是否有联系过
            List<WmMailRecentContactEntity> exists = listByHQL(
                    "from WmMailRecentContactEntity where userId=:userId and contacterMailAddress=:address", params);
            if (CollectionUtils.isNotEmpty(exists)) {
                entity = exists.get(0);
                entity.setLastContactTime(Calendar.getInstance().getTime());
            } else {
                BeanUtils.copyProperties(dto, entity);
            }
            entities.add(entity);
        }
        saveAll(entities);
    }

}
