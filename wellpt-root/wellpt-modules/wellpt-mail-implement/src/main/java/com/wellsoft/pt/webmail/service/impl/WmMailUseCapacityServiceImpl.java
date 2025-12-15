package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.dao.impl.WmMailUseCapacityDaoImpl;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.entity.WmMailFolderEntity;
import com.wellsoft.pt.webmail.entity.WmMailUseCapacityEntity;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService;
import com.wellsoft.pt.webmail.service.*;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 用户邮箱容量服务
 *
 * @author chenq
 * @date 2019/7/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/18    chenq		2019/7/18		Create
 * </pre>
 */
@Service
public class WmMailUseCapacityServiceImpl extends
        AbstractJpaServiceImpl<WmMailUseCapacityEntity, WmMailUseCapacityDaoImpl, String> implements
        WmMailUseCapacityService {

    @Autowired
    WmMailUserService wmMailUserService;
    @Autowired
    WmMailConfigService wmMailConfigService;
    @Autowired
    WmMailFolderService wmMailFolderService;
    @Autowired
    WmMailboxService wmMailboxService;
    @Autowired
    WmWebmailOutboxService wmWebmailOutboxService;
    @Autowired
    private WmMailboxInfoUserService wmMailboxInfoUserService;

    @Override
    public void addUseCapacity(String userId, String mailbox, Long used) {
        WmMailUseCapacityEntity capacityEntity = new WmMailUseCapacityEntity();
        capacityEntity.setUserId(userId);
        capacityEntity.setMailbox(mailbox);
        capacityEntity.setCapacityUsed(used);
        save(capacityEntity);
    }

    @Override
    public int updateUseCapacity(Long increasement, String userId, String systemUnitId,
                                 String mailbox) {
        if (systemUnitId == null) {
            systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        }
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(systemUnitId);
        if (configEntity.getDefaultCapacity() == null) {//不启用容量设置的
            return 1;
        }
        WmMailUserEntity userEntity = wmMailUserService.getInnerMailUser(userId);
        int row = wmMailUserService.updateMailUserAccountUseCapacity(userId, increasement, configEntity.getDefaultCapacity() != null);
        if (row == 0) {
            //用户空间不足
            logger.error("用户[{}]邮件空间不足：邮件大小[{}]，已用空间[{}]，剩余空间[{}]，无法保存邮件!",
                    new Object[]{userEntity.getUserName(),
                            increasement,
                            userEntity.getUsedCapacity(),
                            userEntity.getLimitCapacity() * 1024 * 1024 - userEntity.getUsedCapacity()});
            return 0;
        } else {
            this.dao.updateUseCapacity(increasement, userId,
                    mailbox);//占用用户空间
        }
        return 1;
    }


    @Override
    public int updateseCapacityTransform(Long fromCapacity, Long toCapacity,
                                         String fromMailBox,
                                         String toMailbox, String userId) {
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(
                SpringSecurityUtils.getCurrentUserUnitId());
        long increasement = toCapacity - fromCapacity;
        WmMailUserEntity userEntity = wmMailUserService.getInnerMailUser(userId);
        int row = wmMailUserService.updateMailUserAccountUseCapacity(userId, increasement,
                configEntity.getDefaultCapacity() != null);
        if (row == 0) {
            //用户空间不足
            logger.error("用户[{}]邮件空间不足：邮件大小[{}]，已用空间[{}]，剩余空间[{}]，无法保存邮件!",
                    new Object[]{userEntity.getUserName(),
                            increasement,
                            userEntity.getUsedCapacity(),
                            userEntity.getLimitCapacity() == null ? 0 : (userEntity.getLimitCapacity() * 1024 * 1024 - userEntity.getUsedCapacity())});
            return 0;
        } else {
            this.dao.updateUseCapacity(-fromCapacity, userId,
                    fromMailBox);
            this.dao.updateUseCapacity(toCapacity, userId,
                    toMailbox);//占用用户空间
        }
        return 1;
    }


    @Override
    @Transactional
    public void saveUserUseCapacityInitial(String userId) {
        List<String> mailboxes = Lists.newArrayList(WmWebmailConstants.INBOX, WmWebmailConstants.OUTBOX, WmWebmailConstants.DRAFT, WmWebmailConstants.RECYCLE);
        List<WmMailFolderEntity> wmMailFolderEntities = wmMailFolderService.queryUserFolders(userId);
        for (WmMailFolderEntity f : wmMailFolderEntities) {
            mailboxes.add(f.getFolderCode());
        }
        List<WmMailUseCapacityEntity> saves = Lists.newArrayList();
        WmMailUserEntity userEntity = wmMailUserService.getInnerMailUser(userId);
        long totalUsed = 0L;
        for (String mx : mailboxes) {
            WmMailUseCapacityEntity entity = this.getByUserIdAndMailbox(userId, mx);
            if (entity == null) {
                entity = new WmMailUseCapacityEntity();
                entity.setUserId(userId);
                entity.setMailbox(mx);
                entity.setSystemUnitId(userEntity.getSystemUnitId());
            }
            long capacityUsed = wmMailboxInfoUserService.capacityUsed(userId, mx);
            entity.setCapacityUsed(capacityUsed);
            saves.add(entity);
            totalUsed += capacityUsed;
        }
        this.saveAll(saves);
        userEntity.setUsedCapacity(totalUsed);
        wmMailUserService.save(userEntity);

    }

    @Override
    public WmMailUseCapacityEntity getByUserIdAndMailbox(String userId, String boxname) {
        WmMailUseCapacityEntity capacityEntity = new WmMailUseCapacityEntity();
        capacityEntity.setUserId(userId);
        capacityEntity.setMailbox(boxname);
        List<WmMailUseCapacityEntity> entities = this.dao.listByEntity(capacityEntity);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    @Override
    public void updateUseCapacity(Set<String> userIdSet, Long mailSize, String mailbox) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mailSize", mailSize);
        params.put("isInnerUser", true);
        StringBuilder sb = new StringBuilder("update WmMailUserEntity set usedCapacity=usedCapacity + :mailSize where isInnerUser=:isInnerUser and ");
        HqlUtils.appendSql("userId", params, sb, Sets.<Serializable>newHashSet(userIdSet));
        wmMailUserService.getDao().updateByHQL(sb.toString(), params);

        params.put("mailbox", mailbox);
        sb = new StringBuilder("update WmMailUseCapacityEntity set capacityUsed=capacityUsed + :mailSize where mailbox=:mailbox and ");
        HqlUtils.appendSql("userId", params, sb, Sets.<Serializable>newHashSet(userIdSet));
        this.updateByHQL(sb.toString(), params);

    }
}
