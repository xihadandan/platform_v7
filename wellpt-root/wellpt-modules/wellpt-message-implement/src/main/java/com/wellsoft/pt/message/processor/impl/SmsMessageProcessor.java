/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.processor.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.message.dto.SmsUser;
import com.wellsoft.pt.message.entity.MasConfig;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.entity.ShortMessage;
import com.wellsoft.pt.message.facade.SmsClientApiFacade;
import com.wellsoft.pt.message.processor.AbstractMessageProcessor;
import com.wellsoft.pt.message.service.MessageTemplateService;
import com.wellsoft.pt.message.service.ShortMessageService;
import com.wellsoft.pt.message.sms.SmsClientApi;
import com.wellsoft.pt.message.strategy.SmsLackOfMobilePhoneStrategy;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.multi.org.bean.OrgUserDto;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
@Component
public class SmsMessageProcessor extends AbstractMessageProcessor {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.processor.MessageProcessor#doProcessor(com.wellsoft.pt.message.support.Message)
     */

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private SmsClientApiFacade smsClientApiFacade;

    @Autowired
    private ShortMessageService shortMessageService;

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired(required = false)
    private List<SmsLackOfMobilePhoneStrategy> lackOfMobilePhoneStrategies;


    public static String[] breakWord(String orig, Integer breakWord) {
        String[] moType = new String[0];
        if (StringUtils.isBlank(orig)) {
            return moType;
        }
        List<String> rtn = new ArrayList<String>();
        int osize = StringUtils.length(orig);
        breakWord = breakWord <= 0 ? osize : breakWord;
        int start = 0;
        do {
            int words = Math.min(breakWord, osize - start);
            rtn.add(orig.substring(start, start + words));
        } while ((start = breakWord * rtn.size()) < osize/* <去掉最后空格 */);
        String[] origs = rtn.toArray(moType);
        return origs;
    }

    public static void main(String[] args) {
        String orig = "int[] a = new int[10];数组名，也即引用a，指向数组元素的首地址。";
        System.out.println("----------------------");
        for (String split : breakWord(orig, 0)) {
            System.out.println(split);
        }
        System.out.println("----------------------");
        for (String split : breakWord(orig, -3)) {
            System.out.println(split);
        }
        System.out.println("----------------------");
        for (String split : breakWord(orig, 8)) {
            System.out.println(split);
        }
    }

    @Override
    public void doProcessor(Message msg) {
        Set<String> mobiles = new HashSet<String>();
        Set<MsgOrgUserDto> queryItems = Sets.newLinkedHashSet();
        if (msg.getRecipientMbPhones() != null && msg.getRecipientMbPhones().size() > 0) {
            // 发送短信给系统外的用户
            mobiles = msg.getRecipientMbPhones();
        } else {
            // 发送给系统内的用户
            List<String> recipients = msg.getRecipients();
            Map<String, String> recipientUserMap;
            if (StringUtils.equals(msg.getRecipientType(), Message.RECIPIENT_TYPE_INTERNET_USER)) {
                // 互联网用户 消息 -> 直接获取互联网用户信息
                recipientUserMap = orgApiFacade.getInternetUsersByLoginNames(recipients);
            } else {
                recipientUserMap = orgFacadeService.getUserIdNamesByOrgElementIds(recipients);
            }
            Set<String> recipientIds = recipientUserMap.keySet();
            List<UserInfoEntity> userInfoEntities = orgFacadeService.getUserInfosByUserId(Lists.newArrayList(recipientIds));
            if (CollectionUtils.isNotEmpty(userInfoEntities)) {
                // 缺失移动电话的用户
                List<SmsUser> smsUsers = Lists.newArrayList();
                for (UserInfoEntity user : userInfoEntities) {
                    String mobile = user.getCeilPhoneNumber();
                    if (StringUtils.isNotBlank(mobile)) {
                        mobiles.add(mobile);
                        MsgOrgUserDto dto = new MsgOrgUserDto();
                        dto.setUserName(user.getUserName());
                        dto.setUserId(user.getUserId());
                        dto.setMobilePhone(user.getCeilPhoneNumber());
                        queryItems.add(dto);
                    } else {
                        SmsUser smsUser = new SmsUser();
                        smsUser.setUserName(user.getUserName());
                        smsUser.setUserId(user.getUserId());
                        smsUsers.add(smsUser);
                    }
                }

                if (CollectionUtils.isNotEmpty(smsUsers)) {
                    if (CollectionUtils.isNotEmpty(lackOfMobilePhoneStrategies)) {
                        for (SmsLackOfMobilePhoneStrategy strategy : lackOfMobilePhoneStrategies) {
                            strategy.execute(smsUsers);
                        }
                    }
                    for (SmsUser su : smsUsers) {
                        if (StringUtils.isNotBlank(su.getMobilePhone())) {
                            mobiles.add(su.getMobilePhone());
                            MsgOrgUserDto dto = new MsgOrgUserDto();
                            dto.setUserName(su.getUserName());
                            dto.setUserId(su.getUserId());
                            dto.setMobilePhone(su.getMobilePhone());
                            queryItems.add(dto);
                        }
                    }
                }

            }

        }
        int breakWord = 0;// 短信拆分
        MessageTemplate template = messageTemplateService.getById(msg.getTemplateId());
        if (template != null && template.getBreakWord() != null && template.getBreakWord() > 0) {
            breakWord = template.getBreakWord();
        }
        if (!mobiles.isEmpty()) {
            long max = shortMessageService.getMaxSmid();
            if (max >= -1) {
                if (max >= 99999999) {
                    max = 0L;
                } else {
                    max++;
                }
            }
            MasConfig masConfig = shortMessageService.getBean();
            boolean apiAsync = masConfig.getApiAsync() != null && masConfig.getApiAsync() == true;
            boolean async = msg.getAsync() != null && msg.getAsync() == true;
            boolean isResend = false;
            // API异步配置最高，程序异步次之
            if (apiAsync
                    || async /* 异步发送 */
                    || smsClientApiFacade.sendSM(mobiles.toArray(new String[mobiles.size()]), msg.getBody(), max) == SmsClientApi.IM_SEND_FAIL) {
                isResend = true;
            }
            List<ShortMessage> shortsms = new ArrayList<ShortMessage>();
            OrgUserVo sendUser = new OrgUserVo();
            boolean isSystem = true;
            if (StringUtils.isNotBlank(msg.getSender())) {
                if (!msg.getSender().equals("system")) {
                    UserInfoEntity sendUserInfo = orgFacadeService.getUserInfoByUserId(msg.getSender());
                    if (sendUserInfo != null) {
                        sendUser.setUserName(sendUserInfo.getUserName());
                        sendUser.setMobilePhone(sendUserInfo.getCeilPhoneNumber());
                    }
                    isSystem = false;
                }
            }
            if (msg.getRecipientMbPhones() != null && msg.getRecipientMbPhones().size() > 0) {
                // 发送短信给系统外的用户
                int i = 0;
                List<String> recipients = msg.getRecipients();
                for (String phones : msg.getRecipientMbPhones()) {
                    if (StringUtils.isBlank(phones))/* 过滤手机号为空 */ {
                        continue;
                    }
                    for (String body : breakWord(msg.getBody(), breakWord)) {
                        if (sendUser == null) {
                            UserInfoEntity manager = orgFacadeService.getTenantManagerInfo(msg.getSystem(), msg.getTenant());
                            if (manager != null) {
                                sendUser = new OrgUserVo();
                                sendUser.setUserName(manager.getUserName());
                                sendUser.setMobilePhone(manager.getCeilPhoneNumber());
                            }
                        }
                        ShortMessage sm = new ShortMessage();
                        sm.setIsReSend(isResend);
                        sm.setSendStatus(0);
                        sm.setBody(body);
                        sm.setReceived("");
                        sm.setRecipientName(recipients.get(i));
                        sm.setRecipientMobilePhone(phones);
                        sm.setSenderName(sendUser.getUserName());
                        sm.setSendMobilePhone(sendUser.getMobilePhone());
                        sm.setSend(msg.getSender());
                        sm.setSendTime(msg.getSentTime());
                        sm.setSmid(++max);
                        sm.setType(smsClientApiFacade.getType()); // mas机
                        sm.setReservedText1(msg.getReservedText1());
                        sm.setReservedText2(msg.getReservedText2());
                        sm.setReservedText3(msg.getReservedText3());
                        sm.setMessageOutboxUuid(msg.getDataUuid());
                        shortsms.add(sm);
                    }
                }
            } else {
                // 发送给系统内的用户
                for (OrgUserDto queryItem : queryItems) {
                    //if (queryItem.get(User.MOBILE_PHONE) == null)/* 过滤手机号为空 */{
                    //continue;
                    //}
                    for (String body : breakWord(msg.getBody(), breakWord)) {
                        ShortMessage sm = new ShortMessage();
                        sm.setIsReSend(isResend);
                        sm.setSendStatus(0);
                        sm.setBody(body);
                        sm.setReceived(queryItem.getId());
                        sm.setRecipientName(queryItem.getUserName());
                        sm.setRecipientMobilePhone(queryItem.getMobilePhone());
                        //if (queryItem.get(User.MOBILE_PHONE) == null) {
                        //sm.setSendStatus(2);
                        //sm.setIsReSend(false);
                        //}

                        if (!isSystem) {
                            sm.setSenderName(sendUser.getUserName());
                            sm.setSendMobilePhone(sendUser.getMobilePhone());
                        } else {
                            sm.setSenderName("system"); // 系统发送的数据用system表示
                            sm.setSendMobilePhone("");
                        }
                        sm.setSend(msg.getSender());
                        sm.setSendTime(msg.getSentTime());
                        sm.setSmid(++max);
                        sm.setType(smsClientApiFacade.getType()); // mas机
                        sm.setReservedText1(msg.getReservedText1());
                        sm.setReservedText2(msg.getReservedText2());
                        sm.setReservedText3(msg.getReservedText3());
                        sm.setMessageOutboxUuid(msg.getDataUuid());
                        shortsms.add(sm);
                    }
                }
            }
            shortMessageService.saveShortMessage(shortsms);
        }
    }

    class MsgOrgUserDto extends OrgUserDto {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            OrgUserDto that = (OrgUserDto) o;
            return Objects.equals(getUserId(), that.getUserId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), getUserId());
        }
    }

}
