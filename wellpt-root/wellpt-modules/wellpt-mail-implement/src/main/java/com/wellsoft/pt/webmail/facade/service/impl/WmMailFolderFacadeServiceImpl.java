/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailFolderDto;
import com.wellsoft.pt.webmail.entity.WmMailFolderEntity;
import com.wellsoft.pt.webmail.enums.WmMailBoxStatus;
import com.wellsoft.pt.webmail.facade.service.WmMailFolderFacadeService;
import com.wellsoft.pt.webmail.service.WmMailFolderService;
import com.wellsoft.pt.webmail.service.WmMailUseCapacityService;
import com.wellsoft.pt.webmail.service.WmMailboxService;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description: 邮件文件夹门面服务实现
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月27日.1	chenqiong		2018年2月27日		Create
 * </pre>
 * @date 2018年2月27日
 */
@Service
public class WmMailFolderFacadeServiceImpl extends AbstractApiFacade implements
        WmMailFolderFacadeService {

    @Resource
    WmMailFolderService wmMailFolderService;

    @Resource
    WmMailboxService wmMailboxService;

    @Resource
    WmMailUseCapacityService wmMailUseCapacityService;


    @Override
    @Transactional
    public void deleteFolder(String uuid, boolean isDeleteEmail) {
        WmMailFolderEntity entity = wmMailFolderService.getOne(uuid);
        long folderMailSize = wmMailboxService.countMailSizeByMailboxNameAndUserId(
                entity.getFolderCode(), entity.getUserId()).intValue();
        if (wmMailUseCapacityService.updateseCapacityTransform(folderMailSize, folderMailSize,
                entity.getFolderCode(),
                isDeleteEmail ? WmWebmailConstants.RECYCLE : WmWebmailConstants.INBOX,
                entity.getUserId()) == 0) {
            throw new RuntimeException("邮件空间不足");
        }
        if (isDeleteEmail) {
            wmMailboxService.updateMailBoxStatusByMailBoxName(entity.getFolderCode(),
                    WmMailBoxStatus.LOGICAL_DELETE);

        } else {
            // 非删除的则移动到收件箱
            wmMailboxService.updateMailBoxNameByMailBoxName(WmWebmailConstants.INBOX,
                    entity.getFolderCode());
        }
        wmMailFolderService.delete(uuid);
    }

    @Override
    public String addFolder(WmMailFolderDto wmMailFolderDto) {
        WmMailFolderEntity entity = wmMailFolderService.addFolder(wmMailFolderDto);
        return entity.getFolderCode();
    }

    @Override
    public void updateFolderName(String uuid, String rename) {
        WmMailFolderEntity entity = wmMailFolderService.getOne(uuid);
        if (entity != null) {
            entity.setFolderName(rename);
            if (wmMailFolderService.existSameNameFolder(entity.getUserId(), entity.getFolderName())) {
                throw new RuntimeException("已存在同名的文件夹");
            }
            wmMailFolderService.save(entity);
        }
    }

    @Override
    public List<WmMailFolderDto> queryUserFolders() {
        List<WmMailFolderEntity> wmMailFolderEntities = wmMailFolderService.queryUserFolders(
                SpringSecurityUtils
                        .getCurrentUserId());
        List<WmMailFolderDto> wmMailFolderDtos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(wmMailFolderEntities)) {
            for (WmMailFolderEntity entity : wmMailFolderEntities) {
                WmMailFolderDto dto = new WmMailFolderDto();
                dto.setFolderName(entity.getFolderName());
                dto.setSeq(entity.getSeq());
                dto.setUuid(entity.getUuid());
                dto.setFolderCode(entity.getFolderCode());
                wmMailFolderDtos.add(dto);
            }

        }
        return wmMailFolderDtos;
    }

    @Override
    @Transactional
    public void addFolderAndEmailMovIn(List<String> emailUuids, WmMailFolderDto dto) {
        WmMailFolderEntity entity = wmMailFolderService.addFolder(dto);
        wmMailboxService.updateMailBoxName(entity.getFolderCode(), emailUuids);
    }

}
