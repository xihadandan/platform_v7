/*
 * @(#)2018年3月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailPaperDto;
import com.wellsoft.pt.webmail.entity.WmMailPaperEntity;
import com.wellsoft.pt.webmail.facade.service.WmMailPaperFacadeService;
import com.wellsoft.pt.webmail.service.WmMailPaperService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 信纸facade服务实现
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月7日.1	chenqiong		2018年3月7日		Create
 * </pre>
 * @date 2018年3月7日
 */
@Service
public class WmMailPaperFacadeServiceImpl extends AbstractApiFacade implements WmMailPaperFacadeService {

    @Resource
    WmMailPaperService mailPaperService;

    @Override
    public WmMailPaperDto queryCurrentUserDefaultPaper() {
        WmMailPaperEntity entity = mailPaperService.queryUserDefaultPaper(SpringSecurityUtils.getCurrentUserId());
        if (entity == null) {
            return null;
        }
        WmMailPaperDto dto = new WmMailPaperDto();
        try {
            BeanUtils.copyProperties(dto, entity);
        } catch (Exception e) {
            logger.error("信纸数据dto拷贝异常", e);
        }
        return dto;
    }

    @Override
    public void updateCurrentUserDefaultPaper(WmMailPaperDto dto) {
        dto.setUserId(SpringSecurityUtils.getCurrentUserId());
        mailPaperService.updateUserDefaultPaper(dto);
    }

    @Override
    public List<WmMailPaperDto> querySystemMailPapers() {
        List<WmMailPaperEntity> entities = mailPaperService.querySystemMailPapers();
        if (CollectionUtils.isNotEmpty(entities)) {
            List<WmMailPaperDto> dtos = Lists.newArrayList();
            for (WmMailPaperEntity en : entities) {
                WmMailPaperDto dto = new WmMailPaperDto();
                try {
                    BeanUtils.copyProperties(dto, en);
                } catch (Exception e) {
                }
                dtos.add(dto);
            }
            return dtos;
        }
        return null;
    }

}
