/*
 * @(#)2018年3月15日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.webmail.bean.WmMailRevocationDto;
import com.wellsoft.pt.webmail.entity.WmMailRevocationEntity;
import com.wellsoft.pt.webmail.entity.WmMailboxInfo;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoStatus;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;
import com.wellsoft.pt.webmail.enums.WmMailboxInfoStatusEnum;
import com.wellsoft.pt.webmail.facade.service.WmMailRevocationFacadeService;
import com.wellsoft.pt.webmail.service.*;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 邮件撤回服务门面
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
public class WmMailRevocationFacadeServiceImpl extends AbstractApiFacade implements
        WmMailRevocationFacadeService {

    @Resource
    WmMailRevocationService wmMailRevocationService;

    @Resource
    WmMailboxService wmMailboxService;
    @Resource
    WmMailUserService wmMailUserService;
    @Autowired
    private WmMailboxInfoUserService wmMailboxInfoUserService;
    @Autowired
    private WmMailboxInfoService wmMailboxInfoService;
    @Autowired
    private WmMailboxInfoStatusService wmMailboxInfoStatusService;
    @Resource
    private WmMailUseCapacityService wmMailUseCapacityService;

    public List<WmMailRevocationDto> queryMailRevocationList(WmMailRevocationDto paramDto) {
        List<WmMailRevocationEntity> revocationEntities = wmMailRevocationService.queryMailRevocation(
                paramDto);
        if (CollectionUtils.isNotEmpty(revocationEntities)) {
            List<WmMailRevocationDto> dtoList = Lists.newArrayList();
            for (WmMailRevocationEntity entity : revocationEntities) {
                WmMailRevocationDto dto = new WmMailRevocationDto();
                BeanUtils.copyProperties(entity, dto);
                dtoList.add(dto);
            }
            return dtoList;
        }
        return null;
    }

    @Override
    @Transactional
    public Map<String, Boolean> revokeMail(String mailUuid) {
        WmMailboxInfoUser wmMailboxInfoUser = wmMailboxInfoUserService.getOne(mailUuid);
        WmMailboxInfo wmMailboxInfo = wmMailboxInfoService.getOne(wmMailboxInfoUser.getMailInfoUuid());
        //新数据
        if (wmMailboxInfo.getIsPublicEmail() != null) {
            return this.getResultMap(wmMailboxInfoUser, wmMailboxInfo);
        }

        Map<String, Boolean> result = Maps.newHashMap();
        if (wmMailboxInfoUser.getRevokeStatus() != null && wmMailboxInfoUser.getSendStatus() == 0) {
            result.put("all", true);
            return result;
        }
        if (StringUtils.isBlank(wmMailboxInfo.getActualToMailAddress())) {
            result.put("toMailAddressNull", true);
            return result;
        }
        List<WmMailboxInfoUser> inboxUserList = wmMailboxInfoUserService.getInboxList(wmMailboxInfoUser.getMailInfoUuid());
        if (wmMailboxInfoUser.getRevokeStatus() == null) {
            //待发送 直接撤回
            if (wmMailboxInfoUser.getSendStatus() == 0) {
                wmMailboxInfoUser.setRevokeStatus(1);
                wmMailboxInfoUserService.update(wmMailboxInfoUser);
                result.put("all", true);
                return result;
            }
            int fail = 0;
            int success = 0;
            for (WmMailboxInfoUser infoUser : inboxUserList) {
                infoUser.setRevokeStatus(1);
                if (wmMailUseCapacityService.updateUseCapacity(-wmMailboxInfo.getMailSize(),
                        infoUser.getUserId(), infoUser.getSystemUnitId(), infoUser.translateMailbox()) == 0) {
                    throw new RuntimeException("邮件空间不足");
                }
                infoUser.setStatus(WmWebmailConstants.STATUS_PHYSICAL_DELETE);
                wmMailboxInfoUserService.update(infoUser);
                success++;
            }
            //外部邮件判断
            if (wmMailboxInfo.getActualToMailAddress().indexOf(WmWebmailConstants.MAIL_SEPARATOR) > -1) {
                fail++;
            }
            if (StringUtils.isNotBlank(wmMailboxInfo.getActualCcMailAddress()) && wmMailboxInfo.getActualCcMailAddress().indexOf(WmWebmailConstants.MAIL_SEPARATOR) > -1) {
                fail++;
            }
            if (StringUtils.isNotBlank(wmMailboxInfo.getActualBccMailAddress()) && wmMailboxInfo.getActualBccMailAddress().indexOf(WmWebmailConstants.MAIL_SEPARATOR) > -1) {
                fail++;
            }

            if (success == 0) {
                wmMailboxInfoUser.setRevokeStatus(0);
            } else if (fail == 0) {
                wmMailboxInfoUser.setRevokeStatus(1);
            } else {
                wmMailboxInfoUser.setRevokeStatus(2);
            }
            wmMailboxInfoUserService.update(wmMailboxInfoUser);
        }
        for (WmMailboxInfoUser infoUser : inboxUserList) {
            result.put(infoUser.getUserName() + "<" + infoUser.getMailAddress() + ">", infoUser.getRevokeStatus() == null ? false : infoUser.getRevokeStatus() == 1);
        }
        List<String> externalAddresList = new ArrayList<>();
        externalAddresList.addAll(this.addressList(wmMailboxInfo.getActualToMailAddress()));
        externalAddresList.addAll(this.addressList(wmMailboxInfo.getActualCcMailAddress()));
        externalAddresList.addAll(this.addressList(wmMailboxInfo.getActualBccMailAddress()));
        for (String externalAddres : externalAddresList) {
            String address = externalAddres;
            result.put(address, false);
        }
        return result;
    }

    private Map<String, Boolean> getResultMap(WmMailboxInfoUser wmMailboxInfoUser, WmMailboxInfo wmMailboxInfo) {
        Map<String, Boolean> result = Maps.newHashMap();
        if ((wmMailboxInfoUser.getSendStatus() == 0 || wmMailboxInfoUser.getSendStatus() == 4)) {
            if (wmMailboxInfoUser.getRevokeStatus() == null) {
                wmMailboxInfoUser.setRevokeStatus(1);
                wmMailboxInfoUserService.update(wmMailboxInfoUser);
            }
            result.put("all", true);
            return result;
        }
        List<WmMailboxInfoStatus> infoStatusList = wmMailboxInfoStatusService.listByMialInfoUuid(wmMailboxInfo.getUuid());
        if (CollectionUtils.isEmpty(infoStatusList)) {
            result.put("toMailAddressNull", true);
            return result;
        }
        Map<String, Boolean> revokeMap = new HashMap<>();
        List<WmMailboxInfoUser> inboxUserList = wmMailboxInfoUserService.getInboxList(wmMailboxInfoUser.getMailInfoUuid());
        if (wmMailboxInfoUser.getRevokeStatus() == null) {
            for (WmMailboxInfoUser infoUser : inboxUserList) {
                infoUser.setRevokeStatus(1);
                if (wmMailUseCapacityService.updateUseCapacity(-wmMailboxInfo.getMailSize(),
                        infoUser.getUserId(), infoUser.getSystemUnitId(), infoUser.translateMailbox()) == 0) {
                    throw new RuntimeException("邮件空间不足");
                }
                infoUser.setStatus(WmWebmailConstants.STATUS_PHYSICAL_DELETE);
                revokeMap.put(infoUser.getUserId(), true);
                wmMailboxInfoUserService.update(infoUser);
            }
        } else {
            for (WmMailboxInfoUser infoUser : inboxUserList) {
                revokeMap.put(infoUser.getUserId(), infoUser.getRevokeStatus() == 1);
            }
        }
        int fail = 0;
        int success = 0;
        for (WmMailboxInfoStatus infoStatus : infoStatusList) {
            boolean flg = true;
            if (infoStatus.getStatus() == WmMailboxInfoStatusEnum.HasBeenSent.ordinal() || infoStatus.getStatus() == WmMailboxInfoStatusEnum.PostedToMailboxService.ordinal()) {
                if (StringUtils.isNotBlank(infoStatus.getUserId())) {
                    Boolean tmpFlg = revokeMap.get(infoStatus.getUserId());
                    flg = tmpFlg == null ? true : tmpFlg;
                } else {
                    flg = false;
                }
            }
            if (flg) {
                success++;
            } else {
                fail++;
            }
            String mailName = infoStatus.getMailName();
            if (StringUtils.isNotBlank(infoStatus.getMailAddress())) {
                mailName = mailName + "<" + infoStatus.getMailAddress() + ">";
            }
            result.put(mailName, flg);
        }
        if (wmMailboxInfoUser.getRevokeStatus() == null) {
            if (success == 0) {
                wmMailboxInfoUser.setRevokeStatus(0);
            } else if (fail == 0) {
                wmMailboxInfoUser.setRevokeStatus(1);
            } else {
                wmMailboxInfoUser.setRevokeStatus(2);
            }
            wmMailboxInfoUserService.update(wmMailboxInfoUser);
        }
        return result;
    }

    private List<String> addressList(String address) {
        List<String> addressList = new ArrayList<>();
        if (StringUtils.isBlank(address)) {
            return addressList;
        }
        String[] addresses = StringUtils.split(address, Separator.SEMICOLON.getValue());
        for (String addres : addresses) {
            if (addres.indexOf(WmWebmailConstants.MAIL_SEPARATOR) > -1) {
                addressList.add(addres.substring(0, addres.indexOf(WmWebmailConstants.MAIL_SEPARATOR)) + "<" + addres + ">");
            }
        }
        return addressList;
    }

}
