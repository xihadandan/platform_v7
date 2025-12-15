/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessRoleOrgUserService;
import com.wellsoft.pt.dms.dao.DmsDocExchangeExpireDao;
import com.wellsoft.pt.dms.entity.DmsDocExchangeConfigEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeExpireEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.dms.enums.DocExchangeNotifyWayEnum;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import com.wellsoft.pt.dms.enums.DocExchangeTypeEnum;
import com.wellsoft.pt.dms.service.DmsDocExchangeConfigService;
import com.wellsoft.pt.dms.service.DmsDocExchangeExpireService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordDetailService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_EXPIRE的service服务接口实现类
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
@Service
public class DmsDocExchangeExpireServiceImpl extends AbstractJpaServiceImpl<DmsDocExchangeExpireEntity, DmsDocExchangeExpireDao, String> implements DmsDocExchangeExpireService {

    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;
    @Autowired
    private DmsDocExchangeRecordDetailService docExchangeRecordDetailService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private DmsDocExchangeConfigService dmsDocExchangeConfigService;
    @Autowired
    private BusinessRoleOrgUserService businessRoleOrgUserService;

    @Override
    @Transactional
    public String add(Integer type, String msgTemplateUuid, String docExchangeRecordUuid, Date sendTime) {
        DmsDocExchangeExpireEntity expireEntity = new DmsDocExchangeExpireEntity();
        expireEntity.setType(type);
        expireEntity.setMsgTemplateUuid(msgTemplateUuid);
        expireEntity.setDocExchangeRecordUuid(docExchangeRecordUuid);
        expireEntity.setSendTime(sendTime);
        this.save(expireEntity);
        return expireEntity.getUuid();
    }

    /**
     * 文档交换记录状态位：
     * 发件方状态：0 草稿  1 已发送 2 已办结 8 待审批 9 审批拒绝
     * 收件方状态：3 已退回  4 已签收 5 已反馈 6 待签收 7 待反馈
     */
    @Override
    @Transactional
    public void taskJob() {
        Map<String, Object> params = new HashMap<>();
        params.put("now", new Date());
        List<DmsDocExchangeExpireEntity> expireEntityList = this.listByHQL("from DmsDocExchangeExpireEntity where sendTime<=:now", params);
        for (DmsDocExchangeExpireEntity expireEntity : expireEntityList) {
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(expireEntity.getDocExchangeRecordUuid());
            try {
                IgnoreLoginUtils.login(Config.DEFAULT_TENANT, recordEntity.getUserId());
                //非发送状态 直接删除，不发消息
                if (DocExchangeRecordStatusEnum.SENDED.ordinal() != recordEntity.getRecordStatus()) {
                    this.delete(expireEntity);
                    continue;
                }
                //消息提示方式
                List<DocExchangeNotifyWayEnum> notifyWayEnums = Lists.newArrayList();
                if (BooleanUtils.isTrue(recordEntity.getIsSmsNotify())) {
                    notifyWayEnums.add(DocExchangeNotifyWayEnum.SMS);
                }
                if (BooleanUtils.isTrue(recordEntity.getIsMailNotify())) {
                    notifyWayEnums.add(DocExchangeNotifyWayEnum.MAIL);
                }
                if (BooleanUtils.isTrue(recordEntity.getIsImNotify())) {
                    notifyWayEnums.add(DocExchangeNotifyWayEnum.IM);
                }
                Map<String, Object> formData = null;
                if (recordEntity.getExchangeType() == DocExchangeTypeEnum.DYFORM.ordinal()) {
                    DyFormData dyFormData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(), recordEntity.getDataUuid());
                    formData = dyFormData.getFormDataOfMainform();
                }
                DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(recordEntity.getConfigUuid());
                List<DmsDocExchangeRecordDetailEntity> detailEntityList = docExchangeRecordDetailService.listByDocExchangeRecordUuid(expireEntity.getDocExchangeRecordUuid());
                Set<String> toUserIdSet = Sets.newLinkedHashSet();
                for (DmsDocExchangeRecordDetailEntity recordDetailEntity : detailEntityList) {
                    //待签收发送消息 || 待反馈发送消息
                    if ((expireEntity.getType() == 1 && recordDetailEntity.getSignStatus() == DocExchangeRecordStatusEnum.WAIT_SIGN)
                            || (expireEntity.getType() == 2 && !recordDetailEntity.getIsFeedback())) {
                        if (recordDetailEntity.getToUserId().startsWith(
                                IdPrefix.EXTERNAL.getValue())) { //外部单位
                            if (configEntity.getBusinessCategoryUuid() != null) {
                                List<BusinessRoleOrgUserEntity> roleOrgUserEntityList = businessRoleOrgUserService.findByOrgUuidAndRoleUuid(configEntity.getBusinessCategoryUuid(), recordDetailEntity.getToUserId(), configEntity.getRecipientRoleUuid());
                                for (BusinessRoleOrgUserEntity businessRoleOrgUserEntity : roleOrgUserEntityList) {
                                    for (String userId : businessRoleOrgUserEntity.getUsers().split(";")) {
                                        toUserIdSet.add(userId);
                                    }
                                }
                            }
                        } else {
                            toUserIdSet.add(recordDetailEntity.getToUserId());
                        }

                    }
                }
                if (!toUserIdSet.isEmpty()) {
                    //发送消息
                    dmsDocExchangeRecordService.messageNotify(notifyWayEnums, formData, expireEntity.getMsgTemplateUuid()
                            , recordEntity.getCreator(),
                            toUserIdSet, null, recordEntity.getSignTimeLimit(),
                            recordEntity.getFeedbackTimeLimit(), recordEntity.getDocTitle(), "");
                }
                //删除
                this.delete(expireEntity);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IgnoreLoginUtils.logout();
            }


        }

    }

}
