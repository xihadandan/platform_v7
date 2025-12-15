/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailTagDto;
import com.wellsoft.pt.webmail.entity.WmMailRelaTagEntity;
import com.wellsoft.pt.webmail.entity.WmMailTagEntity;
import com.wellsoft.pt.webmail.facade.service.WmMailTagFacadeService;
import com.wellsoft.pt.webmail.service.WmMailRelaTagService;
import com.wellsoft.pt.webmail.service.WmMailTagService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 邮件标签门面服务实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Service
public class WmMailTagFacadeServiceImpl extends AbstractApiFacade implements WmMailTagFacadeService {

    @Resource
    WmMailTagService wmMailTagService;

    @Resource
    WmMailRelaTagService wmMailRelaTagService;

    @Override
    public void deleteTag(String uuid) {
        wmMailTagService.deleteTagAndEmailRela(uuid);
    }

    @Override
    public void updateTag(WmMailTagDto dto) {
        wmMailTagService.updateTag(dto);
    }

    @Override
    public String addMailTag(WmMailTagDto dto) {
        WmMailTagEntity entity = wmMailTagService.addMailTag(dto);
        return entity.getUuid();
    }

    @Override
    public List<WmMailTagDto> queryUserMailTags() {
        List<WmMailTagEntity> entities = wmMailTagService.queryUserMailTags(SpringSecurityUtils.getCurrentUserId());
        List<WmMailTagDto> dtos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(entities)) {
            for (WmMailTagEntity entity : entities) {
                WmMailTagDto tagDto = new WmMailTagDto();
                tagDto.setUuid(entity.getUuid());
                tagDto.setTagColor(entity.getTagColor());
                tagDto.setTagName(entity.getTagName());
                dtos.add(tagDto);
            }
        }
        return dtos;
    }

    @Override
    @Transactional
    public void addTagAndMarkEmails(List<String> emailUuids, WmMailTagDto dto) {
        WmMailTagEntity entity = wmMailTagService.addMailTag(dto);
        wmMailRelaTagService.addMailRelaTag(entity.getUuid(), emailUuids);
    }

    @Override
    public void addMailRelaTag(String tagUuid, List<String> emailUuids) {
        wmMailRelaTagService.addMailRelaTag(tagUuid, emailUuids);
    }

    @Override
    public void deleteEmailRelaTag(List<String> emailUuids, String tagUuid) {
        wmMailRelaTagService.deleteEmailRelaTag(emailUuids, tagUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmMailTagFacadeService#queryMailRelaTag(java.util.List)
     */
    @Override
    public Map<String, List<WmMailTagDto>> queryMailRelaTag(List<String> emailUuids) {
        List<WmMailRelaTagEntity> wmMailRelaTagEntities = wmMailRelaTagService.queryMailRelaTag(emailUuids);
        Map<String, List<WmMailTagDto>> result = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(wmMailRelaTagEntities)) {
            for (WmMailRelaTagEntity rela : wmMailRelaTagEntities) {
                WmMailTagDto dto = new WmMailTagDto();
                WmMailTagEntity tagEntity = wmMailTagService.getOne(rela.getTagUuid());
                dto.setTagColor(tagEntity.getTagColor());
                dto.setTagName(tagEntity.getTagName());
                dto.setUuid(tagEntity.getUuid());
                if (!result.containsKey(rela.getMailUuid())) {
                    result.put(rela.getMailUuid(), new ArrayList<WmMailTagDto>());
                }
                result.get(rela.getMailUuid()).add(dto);
            }
        }
        return result;
    }

}
