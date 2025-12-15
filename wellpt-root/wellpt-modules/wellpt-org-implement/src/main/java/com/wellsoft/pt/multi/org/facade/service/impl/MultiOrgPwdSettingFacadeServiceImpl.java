/*
 * @(#)2021-03-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service.impl;

import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.support.MessageParams;
import com.wellsoft.pt.multi.org.dto.MultiOrgPwdSettingDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgPwdSettingEntity;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.enums.IsPwdErrorLockAutoUnlockEnum;
import com.wellsoft.pt.multi.org.enums.IsPwdErrorLockEnum;
import com.wellsoft.pt.multi.org.enums.IsPwdValidityEnum;
import com.wellsoft.pt.multi.org.enums.PwdErrorLockEnum;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgPwdSettingFacadeService;
import com.wellsoft.pt.multi.org.query.MultiOrgUserAccountPwdCreateTimeQueryItem;
import com.wellsoft.pt.multi.org.service.MultiOrgPwdSettingService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 数据库表MULTI_ORG_PWD_SETTING的门面服务实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-03-23.1	zenghw		2021-03-23		Create
 * </pre>
 * @date 2021-03-23
 */
@Service
public class MultiOrgPwdSettingFacadeServiceImpl extends AbstractApiFacade implements MultiOrgPwdSettingFacadeService {

    @Autowired
    private MultiOrgPwdSettingService multiOrgPwdSettingService;
    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Override
    @Transactional
    public void saveMultiOrgPwdSetting(MultiOrgPwdSettingDto saveMultiOrgPwdSetting) {
        MultiOrgPwdSettingEntity multiOrgPwdSettingEntity = new MultiOrgPwdSettingEntity();
        if (StringUtils.isNotBlank(saveMultiOrgPwdSetting.getUuid())) {
            multiOrgPwdSettingEntity = multiOrgPwdSettingService.getOne(saveMultiOrgPwdSetting.getUuid());
            if (multiOrgPwdSettingEntity == null) {
                throw new BusinessException("密码配置找不到，通过uuid:" + saveMultiOrgPwdSetting.getUuid());
            }
            BeanUtils.copyProperties(saveMultiOrgPwdSetting, multiOrgPwdSettingEntity);
            multiOrgPwdSettingEntity.setModifyTime(new Date());
            multiOrgPwdSettingService.update(multiOrgPwdSettingEntity);
        } else {
            BeanUtils.copyProperties(saveMultiOrgPwdSetting, multiOrgPwdSettingEntity);
            multiOrgPwdSettingEntity.setUuid(UuidUtils.createUuid());
            multiOrgPwdSettingEntity.setModifyTime(new Date());
            multiOrgPwdSettingEntity.setCreateTime(new Date());
            multiOrgPwdSettingEntity.setCreator(SpringSecurityUtils.getCurrentUserId());
            multiOrgPwdSettingService.save(multiOrgPwdSettingEntity);
        }

        // 历史数据处理
        // 密码有效期开启时，初始化账号表的PWD_CREATE_TIME
        if (IsPwdValidityEnum.Yes.getValue().equals(multiOrgPwdSettingEntity.getIsPwdValidity())) {
            multiOrgUserAccountService.initPwdCreateTime();
        }

        // "关闭账号锁定时，自动解锁【已锁定账号】"开关开启时
        if (IsPwdErrorLockAutoUnlockEnum.Yes.getValue().equals(multiOrgPwdSettingEntity.getIsPwdErrorLockAutoUnlock())
                && IsPwdErrorLockEnum.NO.getValue().equals(multiOrgPwdSettingEntity.getIsPwdErrorLock())) {

            coercePwdUnlock();
        }
    }

    @Override
    public MultiOrgPwdSettingDto getMultiOrgPwdSetting() {
        List<MultiOrgPwdSettingEntity> list = multiOrgPwdSettingService.listAll();
        if (list.size() == 0) {
            return new MultiOrgPwdSettingDto();
        }
        MultiOrgPwdSettingDto multiOrgPwdSettingDto = new MultiOrgPwdSettingDto();
        BeanUtils.copyProperties(list.get(0), multiOrgPwdSettingDto);
        return multiOrgPwdSettingDto;
    }

    /**
     * 密码有效期提醒
     * 查找出符合条件的数据-提醒日=到期日-提醒天数（提醒日=当天的日期才提醒）
     * 发送信息 账号表有userId
     * 开多线程处理
     * messageClientApiFacade.sendByParams(params);
     **/
    @Override
    @Transactional
    public Integer pwdValidityWarn() {
        Integer warnNumber = 0;
        List<MultiOrgUserAccountPwdCreateTimeQueryItem> multiOrgUserAccounts = multiOrgUserAccountService
                .getAllAccountPwdCreateTime();
        List<MultiOrgPwdSettingEntity> multiOrgPwdSettingEntities = multiOrgPwdSettingService.listAll();
        if (multiOrgPwdSettingEntities.size() == 1) {
            MultiOrgPwdSettingEntity multiOrgPwdSetting = multiOrgPwdSettingEntities.get(0);
            if (multiOrgPwdSetting.getPwdValidity() != null && multiOrgPwdSetting.getAdvancePwdDay() != null) {
                List<MultiOrgUserAccountPwdCreateTimeQueryItem> needWarnAccounts = getNeedWarnAccounts(
                        multiOrgUserAccounts, multiOrgPwdSetting);
                warnNumber = sendPwdValidityWarnMessage(needWarnAccounts, multiOrgPwdSetting);
            }

        } else {
            logger.error("密码规则配置表MULTI_ORG_PWD_SETTING 未配置或者数据条数超出，有脏数据，请联系管理员处理");
        }
        return warnNumber;
    }

    @Override
    @Transactional
    public void pwdUnlock() {
        // 查找密码被锁定的账号 解锁时间小于等于当前时间时
        List<MultiOrgUserAccount> accounts = multiOrgUserAccountService.getUnlockAccounts();
        // 对查询出来的数据解锁
        for (MultiOrgUserAccount account : accounts) {
            account.setPwdErrorLock(PwdErrorLockEnum.UnLocked.getValue());
            account.setPwdErrorNum(0);// 错误次数重置为0
            multiOrgUserAccountService.save(account);
        }
    }

    /**
     * 强制解锁已锁定的账号
     *
     * @param
     * @return void
     **/
    private void coercePwdUnlock() {
        // 查找密码被锁定的账号
        List<MultiOrgUserAccount> accounts = multiOrgUserAccountService.getCoerceUnlockAccounts();
        // 对查询出来的数据解锁
        for (MultiOrgUserAccount account : accounts) {
            account.setPwdErrorLock(PwdErrorLockEnum.UnLocked.getValue());
            account.setPwdErrorNum(0);// 错误次数重置为0
            multiOrgUserAccountService.save(account);
        }
    }

    /**
     * 查找出符合条件的数据-提醒日=到期日-提醒天数（提醒日=当天的日期才提醒）
     *
     * @param multiOrgUserAccounts     所有用户的密码创建时间对象
     * @param multiOrgPwdSettingEntity 密码规则对象
     * @return java.util.List<com.wellsoft.pt.multi.org.query.MultiOrgUserAccountPwdCreateTimeQueryItem>
     **/
    private List<MultiOrgUserAccountPwdCreateTimeQueryItem> getNeedWarnAccounts(
            List<MultiOrgUserAccountPwdCreateTimeQueryItem> multiOrgUserAccounts,
            MultiOrgPwdSettingEntity multiOrgPwdSettingEntity) {
        List<MultiOrgUserAccountPwdCreateTimeQueryItem> accounts = new ArrayList<>();
        for (MultiOrgUserAccountPwdCreateTimeQueryItem accountPwdCreateTimeQueryItem : multiOrgUserAccounts) {
            Date dueDate = DateUtil.getNextDate(accountPwdCreateTimeQueryItem.getPwdCreateTime(),
                    multiOrgPwdSettingEntity.getPwdValidity());
            Date advanceDate = DateUtil.getPrevDate(dueDate, multiOrgPwdSettingEntity.getAdvancePwdDay());
            if (DateUtils.isSameDate(advanceDate, new Date())) {
                accounts.add(accountPwdCreateTimeQueryItem);
            }
        }
        return accounts;
    }

    /**
     * 发送信息 账号表有userId
     * 开多线程处理
     * messageClientApiFacade.sendByParams(params);
     *
     * @param needWarnAccounts
     * @return void
     **/
    private Integer sendPwdValidityWarnMessage(List<MultiOrgUserAccountPwdCreateTimeQueryItem> needWarnAccounts,
                                               MultiOrgPwdSettingEntity multiOrgPwdSetting) {

        Set<String> recipientIds = new HashSet<>();
        Set<String> recipientNames = new HashSet<>();
        Map<Object, Object> dataMap = new HashMap<>();
        for (final MultiOrgUserAccountPwdCreateTimeQueryItem multiOrgUserAccountPwdCreateTimeQueryItem : needWarnAccounts) {
            recipientIds = new HashSet<>();
            recipientNames = new HashSet<>();
            recipientIds.add(multiOrgUserAccountPwdCreateTimeQueryItem.getId());
            recipientNames.add(multiOrgUserAccountPwdCreateTimeQueryItem.getUserName());
            dataMap = new HashMap<>();
            dataMap.put("advancePwdDay", multiOrgPwdSetting.getAdvancePwdDay());
            MessageParams params = new MessageParams();
            params.setTemplateId("MSG_PWD_DUE_MESSAGE");
            params.setRecipientIds(recipientIds);
            params.setRecipientNames(recipientNames);
            params.setDataMap(dataMap);
            messageClientApiFacade.sendByParams(params);
        }
        recipientIds = null;
        dataMap = null;
        recipientNames = null;
        return needWarnAccounts.size();
    }
}
