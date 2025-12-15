package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessRoleOrgUserService;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.facade.service.WorkHourFacadeService;
import com.wellsoft.pt.basicdata.workhour.service.WorkHourService;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUnit;
import com.wellsoft.pt.dms.bean.DmsDocExchangeRecordDto;
import com.wellsoft.pt.dms.core.context.ActionContextHolder;
import com.wellsoft.pt.dms.core.support.DataType;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.dao.impl.DmsDocExchangeRecordDaoImpl;
import com.wellsoft.pt.dms.entity.*;
import com.wellsoft.pt.dms.enums.*;
import com.wellsoft.pt.dms.event.DocExchangeEvent;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.service.*;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.facade.service.MessageTemplateApiFacade;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.james.util.io.IOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialClob;
import java.util.*;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/15    chenq		2018/5/15		Create
 * </pre>
 */
@Service
public class DmsDocExchangeRecordServiceImpl extends
        AbstractJpaServiceImpl<DmsDocExchangeRecordEntity, DmsDocExchangeRecordDaoImpl, String> implements
        DmsDocExchangeRecordService, InitializingBean {

    @Autowired
    private DyFormActionService dyFormActionService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DmsDocExchangeLogService dmsDocExchangeLogService;

    @Autowired
    private DmsDocExcExtraSendService dmsDocExcExtraSendService;

    @Autowired
    private DmsDocExchangeRecordDetailService dmsDocExchangeRecordDetailService;

    @Autowired
    private DmsDocExcFeedbackDetailService dmsDocExcFeedbackDetailService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private DmsDocExchangeForwardService dmsDocExchangeForwardService;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Autowired
    private FlowService flowService;

    @Autowired
    private DmsDocExcContactBookService dmsDocExcContactBookService;

    @Autowired
    private WorkHourFacadeService workHourFacadeService;

    @Autowired
    private DmsDocExchangeConfigService dmsDocExchangeConfigService;

    @Autowired
    private MessageTemplateApiFacade messageTemplateApiFacade;

    @Autowired
    private WorkHourService workHourService;

    @Autowired
    private DmsDocExchangeExpireService dmsDocExchangeExpireService;

    @Autowired
    private List<DocExchangeEvent> docExchangeEvents;

    @Autowired
    private BusinessRoleOrgUserService businessRoleOrgUserService;

    private Map<String, DocExchangeEvent> exchangeEventMap = Maps.newHashMap();

    @Override
    public void afterPropertiesSet() throws Exception {
        for (DocExchangeEvent docExchangeEvent : docExchangeEvents) {
            exchangeEventMap.put(docExchangeEvent.getId(), docExchangeEvent);
        }
    }

    @Override
    @Transactional
    public DmsDocExchangeRecordEntity saveDocExchangeRecordWithDyformData(
            DyFormActionData dyFormActionData, boolean isSend) {

        Map<String, Object> docExchangeConfig = (Map<String, Object>) dyFormActionData
                .getExtra("docExchangeConfig");
        String configUuid = docExchangeConfig.get("configUuid").toString();
        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(configUuid);
        if (StringUtils.isNotBlank(configEntity.getBusinessCategoryUuid()) && isSend) {
            List<BusinessRoleOrgUserEntity> roleOrgUserEntityList = businessRoleOrgUserService.findByOrgUuidAndRoleUuid(configEntity.getBusinessCategoryUuid(), null, configEntity.getSendRoleUuid());
            boolean flg = false;
            for (BusinessRoleOrgUserEntity businessRoleOrgUserEntity : roleOrgUserEntityList) {
                if (businessRoleOrgUserEntity.getUsers().indexOf(SpringSecurityUtils.getCurrentUserId()) > -1) {
                    flg = true;
                    break;
                }
            }
            if (!flg) {
                throw new RuntimeException("您当前无权限发件，请联系管理员");
            }
        }
        if (configEntity.getExchangeType() == 0) {//表单类型数据的调用表单保存接口进行表单数据保存
            boolean isVersioning = ActionContextHolder.getContext().getConfiguration().isEnableVersioning();
            String dataUuid = null;
            //保存表单数据
            if (isVersioning) {
                dataUuid = dyFormActionService.saveVersion(ActionContextHolder.getContext(),
                        dyFormActionData);
            } else {
                dataUuid = dyFormActionService.save(dyFormActionData.getDyFormData());
            }
            dyFormActionData.setDataUuid(dataUuid);
        }


        if (docExchangeConfig.containsKey("uuid") && StringUtils.isNotBlank(
                docExchangeConfig.get("uuid").toString())) {
            dmsDocExchangeRecordDetailService.deleteByDocExchangeRecordUuid(
                    docExchangeConfig.get("uuid").toString());
        }
        //保存文档交换记录数据
        DmsDocExchangeRecordEntity recordEntity = saveRecordEntityByDyFormData(dyFormActionData);

        //发送接收方的交换记录与表单数据
        if (isSend) {
            if (1 == configEntity.getApprove() && StringUtils.isNotBlank(recordEntity.getFlowUuid())) {
                //是否有送流程审批的定义
                sendDocExchangeRecordToWorkFlow(recordEntity, dyFormActionData);
                return recordEntity;
            }
            this.sendDoc(dyFormActionData, docExchangeConfig, recordEntity);
        }
        return recordEntity;
    }

    @Override
    @Transactional
    public void sendDoc(DyFormActionData dyFormActionData, Map<String, Object> docExchangeConfig, DmsDocExchangeRecordEntity recordEntity) {
        //保存文档交换的接收对象明细数据
        Set<String> allUserIdSet = Sets.newLinkedHashSet();
        List<DmsDocExchangeRecordDetailEntity> recordDetailEntityList = null;
        if (MapUtils.isNotEmpty(docExchangeConfig) && docExchangeConfig.containsKey(
                "selectUserIds")) {
            String selectUserIds = docExchangeConfig.get("selectUserIds").toString();
            allUserIdSet = dmsDocExcContactBookService.explainUserIdsBySelectIds(selectUserIds);
            if (allUserIdSet.size() == 0) {
                throw new RuntimeException("收件单位下无用户");
            }
            recordDetailEntityList = dmsDocExchangeRecordDetailService.saveRecordDetails(
                    recordEntity, allUserIdSet, 0, null);
        }
        recordEntity.setSendTime(new Date());
        saveReceiverDocExchangeRecordAndFormData(
                this.copyDocExchangeRecordToSender(recordEntity, recordDetailEntityList, 0, null),
                dyFormActionData, recordEntity,
                docExchangeConfig.get("documentTitle").toString());
        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(recordEntity.getConfigUuid());
        if (configEntity.getAutoFinish() != null && configEntity.getAutoFinish() == 1) {
            //如果收文方不需要签收或者反馈意见，则发文即已办结
            recordEntity.setRecordStatus(
                    recordEntity.getIsNeedFeedback() || recordEntity.getIsNeedSign() ? DocExchangeRecordStatusEnum.SENDED.ordinal() : DocExchangeRecordStatusEnum.FINISH.ordinal());
        } else {
            recordEntity.setRecordStatus(DocExchangeRecordStatusEnum.SENDED.ordinal());
        }

        this.dao.save(recordEntity);
    }


    @Override
    @Transactional
    public void sendDocExchange2Reciver(DmsDocExchangeRecordEntity recordEntity) {
        try {
            DyFormData formData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(),
                    recordEntity.getDataUuid());
            DyFormActionData dyFormActionData = new DyFormActionData();
            dyFormActionData.setDyFormData(formData);
            saveReceiverDocExchangeRecordAndFormData(
                    this.copyDocExchangeRecordToSender(recordEntity,
                            dmsDocExchangeRecordDetailService.listByDocExchangeRecordUuid(
                                    recordEntity.getUuid()), 0, null),
                    dyFormActionData, recordEntity,
                    recordEntity.getDocTitle());

            //如果收文方不需要签收或者反馈意见，则发文即已办结
            recordEntity.setRecordStatus(
                    recordEntity.getIsNeedFeedback() || recordEntity.getIsNeedSign() ? DocExchangeRecordStatusEnum.SENDED.ordinal() : DocExchangeRecordStatusEnum.FINISH.ordinal());
            this.dao.save(recordEntity);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void sendDocExchangeRecordToWorkFlow(DmsDocExchangeRecordEntity recordEntity,
                                                 DyFormActionData dyFormActionData) {
        FlowDefinition flowDefinition = flowService.getFlowDefinition(recordEntity.getFlowUuid());
        if (flowDefinition == null) {
            throw new RuntimeException("找不到UUID为[" + recordEntity.getFlowUuid() + "]的流程定义！");
        }

        FlowInstance flowInstance = flowService.startByFlowDefId(flowDefinition.getId(),
                recordEntity.getUserId(), FlowService.AUTO_SUBMIT, null,
                dyFormActionData.getFormUuid(), dyFormActionData.getDataUuid());
        flushSession();
        flowInstance = flowService.getFlowInstance(flowInstance.getUuid());
        flowInstance.setReservedText1(recordEntity.getDyformUuid());
        flowInstance.setReservedText2(recordEntity.getDataUuid());
        flowInstance.setReservedText3(recordEntity.getUuid());
        flowService.saveFlowInstance(flowInstance);

        recordEntity.setRecordStatus(DocExchangeRecordStatusEnum.WAIT_APPROVAL.ordinal());
        this.dao.save(recordEntity);
    }


    @Override
    public List<DmsDocExchangeRecordEntity> copyDocExchangeRecordToSender(
            DmsDocExchangeRecordEntity source,
            List<DmsDocExchangeRecordDetailEntity> recordDetailEntityList, Integer type, String extraSendUuid) {
        List<DmsDocExchangeRecordEntity> receiverRecordEntities = Lists.newArrayList();

        Date feedbackTimeLimit = source.getFeedbackTimeLimit();
        Date signTimeLimit = source.getSignTimeLimit();
        Boolean isImNotify = source.getIsImNotify();
        Boolean isSmsNotify = source.getIsSmsNotify();
        Boolean isMailNotify = source.getIsMailNotify();

        if (type == 1 && StringUtils.isNotBlank(extraSendUuid)) {
            DmsDocExcExtraSendEntity extraSendEntity = dmsDocExcExtraSendService.getOne(extraSendUuid);
            if (extraSendEntity.getIsImNotify() != null) {
                isImNotify = extraSendEntity.getIsImNotify();
            }
            if (extraSendEntity.getIsSmsNotify() != null) {
                isSmsNotify = extraSendEntity.getIsSmsNotify();
            }
            if (extraSendEntity.getIsMailNotify() != null) {
                isMailNotify = extraSendEntity.getIsMailNotify();
            }
            if (extraSendEntity.getFeedbackTimeLimit() != null) {
                feedbackTimeLimit = extraSendEntity.getFeedbackTimeLimit();
            }
            if (extraSendEntity.getSignTimeLimit() != null) {
                signTimeLimit = extraSendEntity.getSignTimeLimit();
            }
        }
        if (type == 2 && StringUtils.isNotBlank(extraSendUuid)) {
            DmsDocExchangeForwardEntity forwardEntity = dmsDocExchangeForwardService.getOne(extraSendUuid);
            if (forwardEntity.getIsImNotify() != null) {
                isImNotify = forwardEntity.getIsImNotify();
            }
            if (forwardEntity.getIsSmsNotify() != null) {
                isSmsNotify = forwardEntity.getIsSmsNotify();
            }
            if (forwardEntity.getIsMailNotify() != null) {
                isMailNotify = forwardEntity.getIsMailNotify();
            }
            if (forwardEntity.getFeedbackTimeLimit() != null) {
                feedbackTimeLimit = forwardEntity.getFeedbackTimeLimit();
            }
            if (forwardEntity.getSignTimeLimit() != null) {
                signTimeLimit = forwardEntity.getSignTimeLimit();
            }
        }

        for (DmsDocExchangeRecordDetailEntity detailEntity : recordDetailEntityList) {
            DmsDocExchangeRecordEntity receiveRecordEntity = new DmsDocExchangeRecordEntity();
            receiveRecordEntity.setUserId(detailEntity.getToUserId());
            receiveRecordEntity.setRecordStatus(
                    source.getIsNeedSign() ? DocExchangeRecordStatusEnum.WAIT_SIGN.ordinal() : DocExchangeRecordStatusEnum.SIGNED.ordinal());

            receiveRecordEntity.setIsImNotify(isImNotify);
            receiveRecordEntity.setIsMailNotify(isMailNotify);
            receiveRecordEntity.setIsSmsNotify(isSmsNotify);
            receiveRecordEntity.setFeedbackTimeLimit(feedbackTimeLimit);
            receiveRecordEntity.setSignTimeLimit(signTimeLimit);

            receiveRecordEntity.setDocUrgeLevel(source.getDocUrgeLevel());
            receiveRecordEntity.setDocEncryptionLevel(source.getDocEncryptionLevel());
            receiveRecordEntity.setExchangeType(source.getExchangeType());
            receiveRecordEntity.setDyformUuid(source.getDyformUuid());
            receiveRecordEntity.setFromRecordDetailUuid(detailEntity.getUuid());
            receiveRecordEntity.setToUserNames(source.getToUserNames());
            receiveRecordEntity.setToUserIds(source.getToUserIds());
            receiveRecordEntity.setIsNeedSign(source.getIsNeedSign());
            receiveRecordEntity.setIsNeedFeedback(source.getIsNeedFeedback());
            receiveRecordEntity.setFromUserId(source.getFromUserId());
            receiveRecordEntity.setFromUnitId(source.getFromUnitId());
            receiveRecordEntity.setConfigUuid(source.getConfigUuid());
            receiveRecordEntity.setRefuseToView(source.getRefuseToView());
            receiveRecordEntity.setNoReminders(source.getNoReminders());
            receiveRecordEntity.setSendTime(source.getSendTime());
            receiveRecordEntity.setDocTitle(source.getDocTitle());
            receiveRecordEntity.setConfigurationJson(source.getConfigurationJson());
            receiveRecordEntity.setOvertimeLevel(
                    this.getOvertimeLevel(receiveRecordEntity).ordinal());
            receiverRecordEntities.add(receiveRecordEntity);
        }
        return receiverRecordEntities;
    }

    @Override
    public DmsDocExchangeRecordEntity getByFormUuidAndDataUuid(String formUuid, String dataUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("dataUuid", dataUuid);
        param.put("dyformUuid", formUuid);
        return this.dao.getOneByNameHQLQuery("getByFormUuidAndDataUuid", param);
    }

    @Override
    @Transactional
    public void updateRefuseToView(String docExcRecordUuid, int refuseToView) {
        DmsDocExchangeRecordEntity entity = this.getOne(docExcRecordUuid);
        entity.setRefuseToView(refuseToView);
        this.save(entity);
    }

    @Override
    @Transactional
    public void updateNoReminders(String docExcRecordUuid, int noReminders) {
        DmsDocExchangeRecordEntity entity = this.getOne(docExcRecordUuid);
        entity.setNoReminders(noReminders);
        this.save(entity);
    }

    @Override
    @Transactional
    public void updateRecordStatus(String docExcRecordUuid, int recordStatus) {
        DmsDocExchangeRecordEntity entity = getOne(docExcRecordUuid);
        entity.setRecordStatus(recordStatus);
        save(entity);
    }

    @Override
    @Transactional
    public void updateRecordFinished(String docExcRecordUuid) {
        this.updateRecordStatus(docExcRecordUuid,
                DocExchangeRecordStatusEnum.FINISH.ordinal());
        dmsDocExchangeLogService.saveLog(docExcRecordUuid,
                DocEchangeOperationEnum.FINISH.getName(),
                SpringSecurityUtils.getCurrentUserName(), null, null, null, null);
    }

    @Override
    @Transactional
    public void feedbackDocExchange(DocExchangeActionData actionData) {
        DocExchangeActionData.DocExcOperateData feedbackData = actionData.getFeedbackData();
        this.feedbackDocExchange(actionData.getDocExcRecordUuid(), feedbackData);
    }

    @Override
    @Transactional
    public void feedbackDocExchange(String docExcRecordUuid, DocExchangeActionData.DocExcOperateData feedbackData) {
        DmsDocExchangeRecordEntity recordEntity = getOne(docExcRecordUuid);
        if (!recordEntity.getIsNeedFeedback()) {
            throw new RuntimeException("无需反馈");
        }
        recordEntity.setRecordStatus(DocExchangeRecordStatusEnum.FEEDBACK_DONE.ordinal());
        String fromUserName = null;
        DmsDocExchangeRecordDetailEntity fromDetailEntity = dmsDocExchangeRecordDetailService.getOne(
                recordEntity.getFromRecordDetailUuid());
        //1.收文方记录反馈信息
        DmsDocExcFeedbackDetailEntity feedbackDetailEntity = new DmsDocExcFeedbackDetailEntity();
        feedbackDetailEntity.setFeedbackType(DocExchangeFeedbackTypeEnum.RECEIVER_FEEDBACK);
        feedbackDetailEntity.setContent(feedbackData.getContent());
        feedbackDetailEntity.setDocExchangeRecordUuid(docExcRecordUuid);
        feedbackDetailEntity.setFromUserId(SpringSecurityUtils.getCurrentUserId());
        feedbackDetailEntity.setFromUnitId(recordEntity.getUserId());
        feedbackDetailEntity.setToUserId(fromDetailEntity.getCreator());
        feedbackDetailEntity.setFeedbackTime(new Date());
        feedbackDetailEntity.setFileUuids(feedbackData.getFileUuids());
        feedbackDetailEntity.setFileNames(feedbackData.getFileNames());
        dmsDocExcFeedbackDetailService.save(feedbackDetailEntity);
        recordEntity.setOvertimeLevel(this.getOvertimeLevel(recordEntity).ordinal());
        this.save(recordEntity);

        //反馈事件
        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(recordEntity.getConfigUuid());
        if (StringUtils.isNotBlank(configEntity.getFeedbackEvent())) {
            DocExchangeEvent docExchangeEvent = exchangeEventMap.get(configEntity.getFeedbackEvent());
            docExchangeEvent.feedbackEvent(recordEntity, feedbackDetailEntity);
        }


        //2.反馈信息发送给发文方
        try {
            //反馈给发送方
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, fromDetailEntity.getCreator());
            fromUserName = orgApiFacade.getUserNameById(fromDetailEntity.getCreator());
            fromDetailEntity.setIsFeedback(true);
            DmsDocExcFeedbackDetailEntity feedback2SenderEntity = new DmsDocExcFeedbackDetailEntity();
            feedback2SenderEntity.setContent(feedbackData.getContent());
            feedback2SenderEntity.setDocExchangeRecordUuid(
                    fromDetailEntity.getDocExchangeRecordUuid());
            feedback2SenderEntity.setFeedbackTime(feedbackDetailEntity.getFeedbackTime());
            feedback2SenderEntity.setFeedbackType(DocExchangeFeedbackTypeEnum.RECEIVER_FEEDBACK);
            feedback2SenderEntity.setFileUuids(feedbackDetailEntity.getFileUuids());
            feedback2SenderEntity.setFileNames(feedbackDetailEntity.getFileNames());
            feedback2SenderEntity.setFromUserId(feedbackDetailEntity.getFromUserId());
            feedback2SenderEntity.setFromUnitId(feedbackDetailEntity.getFromUnitId());
            feedback2SenderEntity.setToUserId(feedbackDetailEntity.getToUserId());
            feedback2SenderEntity.setFromFeedbackDetailUuid(feedbackDetailEntity.getUuid());
            dmsDocExcFeedbackDetailService.save(feedback2SenderEntity);
            dmsDocExchangeRecordDetailService.save(fromDetailEntity);
            flushSession();
            if (recordEntity.getIsNeedFeedback()) {
                if (configEntity.getAutoFinish() != null && configEntity.getAutoFinish() == 1) {
                    //需要反馈的情况下，如果发送方的收文对象都反馈结束，那么发送方自动更新为已办结
                    this.updateDocExchangeFinishedWhenDetailDone(
                            fromDetailEntity.getDocExchangeRecordUuid());
                }

            }

        } catch (Exception e) {
            logger.error("反馈意见给发送方异常：", e);
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }


        //保存操作日志
        dmsDocExchangeLogService.saveLog(recordEntity.getUuid(),
                DocEchangeOperationEnum.FEEDBACK.getName(),
                SpringSecurityUtils.getCurrentUserName(), fromUserName, feedbackData.getContent(),
                feedbackData.getFileUuids(), feedbackData.getFileNames());
    }

    @Override
    @Transactional
    public void updateDocExchangeFinishedWhenDetailDone(String docExchangeRecordUuid) {
        this.dao.updateDocExchangeFinishedWhenDetailDone(docExchangeRecordUuid);
    }

    @Override
    @Transactional
    public void updateDocExchangeSignFinishedWhenDetailDone(String docExchangeRecordUuid) {
        this.dao.updateDocExchangeSignFinishedWhenDetailDone(docExchangeRecordUuid);
    }

    @Override
    @Transactional
    public void forwardDocExchange(DocExchangeActionData actionData) {
        DocExchangeActionData.DocExcOperateData forwardData = actionData.getForwardData();
        DmsDocExchangeRecordEntity recordEntity = getOne(actionData.getDocExcRecordUuid());
        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(recordEntity.getConfigUuid());
        if (configEntity.getIsForward() != null && configEntity.getIsForward() != 1) {
            throw new RuntimeException("禁止转发");
        }


        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        //保存发送者的转发详情
        String uuid = dmsDocExchangeForwardService.saveForwardDetail(recordEntity.getUuid(), forwardData,
                DocExchangeRecordStatusEnum.SENDED, currentUserId, recordEntity.getUserId());

        DocExchangeActionData.ToUserData userData = forwardData.getToUserData().get(0);
        Set<String> allUserIdSet = dmsDocExcContactBookService.explainUserIdsBySelectIds(
                userData.getToUserId());

        List<DmsDocExchangeRecordDetailEntity> recordDetailEntityList = dmsDocExchangeRecordDetailService.saveRecordDetails(
                recordEntity, allUserIdSet, 2, uuid);
        List<DmsDocExchangeRecordEntity> receiveRecordEntities = this.copyDocExchangeRecordToSender(
                recordEntity, recordDetailEntityList, 2, uuid);

        //保存接收者文档交换记录与表单数据
        DyFormData formData = null;
        if (DocExchangeTypeEnum.DYFORM.ordinal() == recordEntity.getExchangeType()) {
            formData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(),
                    recordEntity.getDataUuid());
            formData.setFieldValue("is_read", "0");
        }
        for (DmsDocExchangeRecordEntity entity : receiveRecordEntities) {
            try {
                if (entity.getUserId().startsWith(IdPrefix.USER.getValue())) {
                    IgnoreLoginUtils.login(Config.DEFAULT_TENANT, entity.getUserId());
                }
                if (formData != null) {
                    String dataUuid = dyFormActionService.save(
                            formData.doCopy(true));
                    entity.setDataUuid(dataUuid);
                }
                this.save(entity);
                //保存接收者的转发详情
                dmsDocExchangeForwardService.saveForwardDetail(entity.getUuid(), forwardData,
                        DocExchangeRecordStatusEnum.SIGNED, currentUserId, entity.getUserId());

            } catch (Exception e) {
                logger.error("保存文档交换接收方的文档交换记录与表单数据异常：", e);
            } finally {
                if (entity.getUserId().startsWith(IdPrefix.USER.getValue())) {
                    IgnoreLoginUtils.logout();
                }
            }
        }


        //保存操作日志
        dmsDocExchangeLogService.saveLog(recordEntity.getUuid(),
                DocEchangeOperationEnum.FORWARD.getName(),
                SpringSecurityUtils.getCurrentUserName(), userData.getToUserName(), forwardData.getContent(),
                forwardData.getFileUuids(), forwardData.getFileNames());
        this.save(recordEntity);

    }

    @Override
    public DmsDocExchangeRecordEntity getByFromRecordDetailUuid(String uuid) {
        return this.dao.getByFromRecordDetailUuid(uuid);
    }


    private void saveReceiverDocExchangeRecordAndFormData(
            List<DmsDocExchangeRecordEntity> receiverRecordEntities,
            DyFormActionData dyFormActionData, DmsDocExchangeRecordEntity recordEntity,
            String documentTitle) {
        //消息提示方式
        List<DocExchangeNotifyWayEnum> notifyWayEnums = Lists.newArrayList();
        Set<String> toUserIdSet = Sets.newLinkedHashSet();
        boolean isDyformType = false;
        for (DmsDocExchangeRecordEntity entity : receiverRecordEntities) {
            try {
                if (entity.getUserId().startsWith(
                        IdPrefix.USER.getValue()) && !entity.getUserId().startsWith(
                        DocExcContactBookIdPrefixEnum.CONTACT_UNIT_ID.getId())) {
                    IgnoreLoginUtils.login(Config.DEFAULT_TENANT, entity.getUserId());
                }
                if (entity.getExchangeType() == DocExchangeTypeEnum.DYFORM.ordinal()) {
                    //保存表单数据
                    entity.setDataUuid(dyFormActionService.save(
                            dyFormActionData.getDyFormData().doCopy(true)));
                    isDyformType = true;
                }
                if (notifyWayEnums.isEmpty()) {
                    if (BooleanUtils.isTrue(entity.getIsSmsNotify())) {
                        notifyWayEnums.add(DocExchangeNotifyWayEnum.SMS);
                    }
                    if (BooleanUtils.isTrue(entity.getIsMailNotify())) {
                        notifyWayEnums.add(DocExchangeNotifyWayEnum.MAIL);
                    }
                    if (BooleanUtils.isTrue(entity.getIsImNotify())) {
                        notifyWayEnums.add(DocExchangeNotifyWayEnum.IM);
                    }
                }
                if (entity.getUserId().startsWith(
                        IdPrefix.USER.getValue()) && !entity.getUserId().startsWith(
                        DocExcContactBookIdPrefixEnum.CONTACT_UNIT_ID.getId())) {
                    toUserIdSet.add(entity.getUserId());
                }
                if (entity.getUserId().startsWith(IdPrefix.EXTERNAL.getValue())) {
                    toUserIdSet.add(entity.getUserId());
                }

                this.dao.save(entity);
            } catch (Exception e) {
                logger.error("保存文档交换接收方的文档交换记录与表单数据异常：", e);
                throw new RuntimeException(e);
            } finally {
                if (entity.getUserId().startsWith(
                        IdPrefix.USER.getValue()) && !entity.getUserId().startsWith(
                        DocExcContactBookIdPrefixEnum.CONTACT_UNIT_ID.getId())) {
                    IgnoreLoginUtils.logout();
                }
            }
        }

        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(recordEntity.getConfigUuid());

        this.addExpire(recordEntity, configEntity);

        String msgTemplateId = this.getMsgTemplateId(configEntity.getNotifyMsgUuid(), DmsDocExchangeMessageEntity.DOC_EXCHANGE_RECEIVE_MSG_TEMPLATE);
        Set<String> sendMsgUserIdSet = Sets.newLinkedHashSet();
        for (String toUserId : toUserIdSet) {
            if (toUserId.startsWith(IdPrefix.EXTERNAL.getValue())) {
                if (configEntity.getBusinessCategoryUuid() != null) {
                    List<BusinessRoleOrgUserEntity> roleOrgUserEntityList = businessRoleOrgUserService.findByOrgUuidAndRoleUuid(configEntity.getBusinessCategoryUuid(), toUserId, configEntity.getRecipientRoleUuid());
                    for (BusinessRoleOrgUserEntity businessRoleOrgUserEntity : roleOrgUserEntityList) {
                        for (String userId : businessRoleOrgUserEntity.getUsers().split(";")) {
                            sendMsgUserIdSet.add(userId);
                        }
                    }
                }
            } else {
                sendMsgUserIdSet.add(toUserId);
            }
        }

        this.messageNotify(notifyWayEnums,
                isDyformType ? dyFormActionData.getDyFormData().getFormDataOfMainform() : null, msgTemplateId
                , recordEntity.getCreator(),
                sendMsgUserIdSet, null, receiverRecordEntities.get(0).getSignTimeLimit(),
                receiverRecordEntities.get(0).getFeedbackTimeLimit(), documentTitle, "");

    }

    private void addExpire(DmsDocExchangeRecordEntity recordEntity, DmsDocExchangeConfigEntity configEntity) {
        if (recordEntity.getIsNeedSign() && recordEntity.getSignTimeLimit() != null) {
            if (configEntity.getSignTimeLimit() == 1 &&
                    configEntity.getSignBeforeNum() != null &&
                    configEntity.getSignBeforeUnit() != null &&
                    configEntity.getSignBeforeMsgUuid() != null) {
                Date signBeforeDate = null;
                if (isWorkingDateUnit(configEntity.getSignBeforeUnit())) {
                    signBeforeDate = workHourService.getWorkDate(recordEntity.getSignTimeLimit(), -Double.valueOf(configEntity.getSignBeforeNum() + ""), configEntity.getSignBeforeUnit());
                } else {
                    long repeatInterval = this.calculateRepeatInterval(configEntity.getSignBeforeNum(), configEntity.getSignBeforeUnit());
                    signBeforeDate = new Date(recordEntity.getSignTimeLimit().getTime() - repeatInterval);
                }
                dmsDocExchangeExpireService.add(1, configEntity.getSignBeforeMsgUuid(), recordEntity.getUuid(), signBeforeDate);
            }

            if (recordEntity.getIsNeedSign() && configEntity.getSignTimeLimit() == 1 &&
                    configEntity.getSignAfterNum() != null &&
                    configEntity.getSignAfterUnit() != null &&
                    configEntity.getSignAfterMsgUuid() != null &&
                    configEntity.getSignAfterFrequency() != null) {
                List<Date> signAfterDateList = null;
                if (isWorkingDateUnit(configEntity.getSignAfterUnit())) {
                    // 任务逾期处理工作时间计算
                    signAfterDateList = this.calculateWorkingTime(Double.valueOf(configEntity.getSignAfterNum() + ""), configEntity.getSignAfterUnit(), configEntity.getSignAfterFrequency(), recordEntity.getSignTimeLimit());
                } else {

                    long repeatInterval = this.calculateRepeatInterval(Double.valueOf(configEntity.getSignAfterNum() + ""), configEntity.getSignAfterUnit());
                    //开始时间
                    Date startTime = new Date(recordEntity.getSignTimeLimit().getTime() + repeatInterval);
                    //结束时间
                    Date endTime = new Date(recordEntity.getSignTimeLimit().getTime() + repeatInterval * configEntity.getSignAfterFrequency());
                    signAfterDateList = com.wellsoft.context.util.date.DateUtils.calculationInterval(startTime, endTime, configEntity.getSignAfterFrequency());
                }
                for (Date date : signAfterDateList) {
                    dmsDocExchangeExpireService.add(1, configEntity.getSignAfterMsgUuid(), recordEntity.getUuid(), date);
                }
            }
        }

        if (recordEntity.getIsNeedFeedback() && recordEntity.getFeedbackTimeLimit() != null) {
            if (configEntity.getFeedbackTimeLimit() == 1 &&
                    configEntity.getFeedbackBeforeNum() != null &&
                    configEntity.getFeedbackBeforeUnit() != null &&
                    configEntity.getFeedbackBeforeMsgUuid() != null) {
                Date feedbackBeforeDate = null;
                if (isWorkingDateUnit(configEntity.getFeedbackBeforeUnit())) {
                    feedbackBeforeDate = workHourService.getWorkDate(recordEntity.getFeedbackTimeLimit(), -Double.valueOf(configEntity.getFeedbackBeforeNum() + ""), configEntity.getFeedbackBeforeUnit());
                } else {
                    long repeatInterval = this.calculateRepeatInterval(configEntity.getFeedbackBeforeNum(), configEntity.getFeedbackBeforeUnit());
                    feedbackBeforeDate = new Date(recordEntity.getFeedbackTimeLimit().getTime() - repeatInterval);
                }
                dmsDocExchangeExpireService.add(2, configEntity.getFeedbackBeforeMsgUuid(), recordEntity.getUuid(), feedbackBeforeDate);
            }

            if (recordEntity.getIsNeedFeedback() && configEntity.getFeedbackTimeLimit() == 1 &&
                    configEntity.getFeedbackAfterNum() != null &&
                    configEntity.getFeedbackAfterUnit() != null &&
                    configEntity.getFeedbackAfterMsgUuid() != null &&
                    configEntity.getFeedbackAfterFrequency() != null) {
                List<Date> feedbackAfterDateList = null;
                if (isWorkingDateUnit(configEntity.getFeedbackAfterUnit())) {
                    // 任务逾期处理工作时间计算
                    feedbackAfterDateList = this.calculateWorkingTime(Double.valueOf(configEntity.getFeedbackAfterNum() + ""), configEntity.getFeedbackAfterUnit(), configEntity.getFeedbackAfterFrequency(), recordEntity.getFeedbackTimeLimit());
                } else {

                    long repeatInterval = this.calculateRepeatInterval(Double.valueOf(configEntity.getFeedbackAfterNum() + ""), configEntity.getFeedbackAfterUnit());
                    //开始时间
                    Date startTime = new Date(recordEntity.getFeedbackTimeLimit().getTime() + repeatInterval);
                    //结束时间
                    Date endTime = new Date(recordEntity.getFeedbackTimeLimit().getTime() + repeatInterval * configEntity.getFeedbackAfterFrequency());
                    feedbackAfterDateList = com.wellsoft.context.util.date.DateUtils.calculationInterval(startTime, endTime, configEntity.getFeedbackAfterFrequency());
                }
                for (Date date : feedbackAfterDateList) {
                    dmsDocExchangeExpireService.add(2, configEntity.getFeedbackAfterMsgUuid(), recordEntity.getUuid(), date);
                }
            }
        }

    }

    private long calculateRepeatInterval(Double dueRepeatInterval, Integer dueUnit) {
        long repeatInterval = 0;
        switch (dueUnit) {
            case TimerUnit.DAY:
                repeatInterval = Double.valueOf(dueRepeatInterval * dueUnit * 1000l).longValue();
                break;
            case TimerUnit.HOUR:
                repeatInterval = Double.valueOf(dueRepeatInterval * dueUnit * 1000l).longValue();
                break;
            case TimerUnit.MINUTE:
                repeatInterval = Double.valueOf(dueRepeatInterval * dueUnit * 1000l).longValue();
                break;
            default:
                break;
        }
        return repeatInterval;
    }

    private List<Date> calculateWorkingTime(Double dueTime, Integer dueUnit, int dueTotalCount, Date taskDueTime) {
        List<Date> workingTimes = new ArrayList<Date>();
        for (int index = 1; index < dueTotalCount; index++) {
            Double amount = dueTime * index;
            switch (dueUnit) {
                case TimerUnit.WORKING_DAY:
                    workingTimes.add(workHourService.getWorkDate(taskDueTime, amount, WorkUnit.WorkingDay));
                    break;
                case TimerUnit.WORKING_HOUR:
                    workingTimes.add(workHourService.getWorkDate(taskDueTime, amount, WorkUnit.WorkingHour));
                    break;
                case TimerUnit.WORKING_MINUTE:
                    workingTimes.add(workHourService.getWorkDate(taskDueTime, amount, WorkUnit.WorkingMinute));
                    break;
                default:
                    break;
            }
        }
        return workingTimes;
    }

    private boolean isWorkingDateUnit(int unit) {
        if (unit == TimerUnit.WORKING_DAY
                || unit == TimerUnit.WORKING_HOUR
                || unit == TimerUnit.WORKING_MINUTE) {
            return true;
        }
        return false;
    }


    private String getMsgTemplateId(String id, String def) {
        if (StringUtils.isBlank(id)) {
            return def;
        }
        String msgTemplateId = def;
        MessageTemplate messageTemplate = messageTemplateApiFacade.getMessageTemplateByUuid(id);
        if (messageTemplate == null) {
            messageTemplate = messageTemplateApiFacade.getById(id);
        }
        if (messageTemplate != null) {
            msgTemplateId = messageTemplate.getId();
        }
        return msgTemplateId;
    }

    private long calculateRepeatInterval(int num, Integer unit) {
        long repeatInterval = 0;
        switch (unit) {
            case TimerUnit.DAY:
                repeatInterval = Long.valueOf(num * unit * 1000l);
                break;
            case TimerUnit.HOUR:
                repeatInterval = Long.valueOf(num * unit * 1000l);
                break;
            case TimerUnit.MINUTE:
                repeatInterval = Long.valueOf(num * unit * 1000l);
                break;
            default:
                break;
        }
        return repeatInterval;
    }


    @Override
    public void messageNotify(List<DocExchangeNotifyWayEnum> notifyWayEnums,
                              Map<String, Object> formData, String templateId, String fromUserId,
                              Set<String> toUserIds, String fileNames, Date siginTimeLimit,
                              Date feedbackTimeLimit, String documentTitle, String remark) {
        if (CollectionUtils.isEmpty(notifyWayEnums) || CollectionUtils.isEmpty(toUserIds)) {
            return;
        }

        String sendWay = "";
        //消息提示方式
        if (notifyWayEnums.contains(DocExchangeNotifyWayEnum.SMS)) {
            sendWay += Message.TYPE_SMS + Separator.COMMA.getValue();
        }
        if (notifyWayEnums.contains(DocExchangeNotifyWayEnum.MAIL)) {
            sendWay += Message.TYPE_EMAIL + Separator.COMMA.getValue();
        }
        if (notifyWayEnums.contains(DocExchangeNotifyWayEnum.IM)) {
            sendWay += Message.TYPE_ON_LINE + Separator.COMMA.getValue();
        }

        OrgUserVo fromUser = orgApiFacade.getUserVoById(fromUserId);
        String fromUserName = fromUser.getUserName();
        MultiOrgSystemUnit orgSystemUnit = orgApiFacade.getSystemUnitById(
                fromUser.getSystemUnitId());
        String fromUnitName = orgSystemUnit.getName();
        DmsDocExchangeMessageEntity messageEntity = new DmsDocExchangeMessageEntity(
                new Date(), fromUnitName, fromUserName, fileNames,
                formData, siginTimeLimit, feedbackTimeLimit
                , documentTitle, remark);

        messageClientApiFacade.send(templateId,
                sendWay.substring(0, sendWay.length() - 1), messageEntity, toUserIds, null, null);

    }

    @Override
    public DmsDocExchangeRecordDto getRecordDtoByFormUuidAndDataUuid(String formUuid,
                                                                     String dataUuid) {
        DmsDocExchangeRecordEntity entity = this.getByFormUuidAndDataUuid(formUuid, dataUuid);
        return this.buildEntity2Dto(entity);
    }

    private DmsDocExchangeRecordDto buildEntity2Dto(DmsDocExchangeRecordEntity entity) {
        DmsDocExchangeRecordDto recordDto = new DmsDocExchangeRecordDto();
        BeanUtils.copyProperties(entity, recordDto, "toUserIds", "toUserNames");
        try {
            if (entity.getToUserIds() != null) {
                recordDto.setToUserIds(IOUtil.toString(entity.getToUserIds().getCharacterStream()));
                recordDto.setToUserNames(
                        IOUtil.toString(entity.getToUserNames().getCharacterStream()));
            }

        } catch (Exception e) {
            logger.error("文档交换记录数据拷贝大字段异常：", e);
        }
        return recordDto;
    }

    @Override
    public DmsDocExchangeRecordDto getByUuid(String uuid) {
        DmsDocExchangeRecordEntity entity = getOne(uuid);
        if (entity != null) {
            return this.buildEntity2Dto(entity);
        }
        return null;
    }

    @Override
    @Transactional
    public void updateOvertimeLevelByUuid(String uuid) {
        try {
            DmsDocExchangeRecordEntity recordEntity = getOne(uuid);
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, recordEntity.getCreator());
            recordEntity.setOvertimeLevel(getOvertimeLevel(recordEntity).ordinal());
            logger.info("{}文档交换紧要性{}", uuid, recordEntity.getOvertimeLevel());
            save(recordEntity);
        } catch (Exception e) {
            logger.error("更新文档交换记录的紧要性异常：", e);
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();

        }
    }

    @Override
    public DocExchangeOvertimeLevelEnum getOvertimeLevel(DmsDocExchangeRecordEntity recordEntity) {
        DocExchangeOvertimeLevelEnum overtimeLevel = DocExchangeOvertimeLevelEnum.NONE;
        if (//需要反馈的，已反馈的情况下
                (recordEntity.getIsNeedFeedback() && DocExchangeRecordStatusEnum.FEEDBACK_DONE.ordinal() == recordEntity.getRecordStatus())
                        //需要签收，不需要反馈的，已签收的情况下
                        || (recordEntity.getIsNeedSign() && !recordEntity.getIsNeedFeedback() && DocExchangeRecordStatusEnum.SIGNED.ordinal() == recordEntity.getRecordStatus())) {
            return overtimeLevel;
        }

        //需要签收的，且在有签收时限的要求下，计算延期等级
        if (recordEntity.getIsNeedSign()
                && recordEntity.getSignTimeLimit() != null
                && recordEntity.getRecordStatus() == DocExchangeRecordStatusEnum.WAIT_SIGN.ordinal()) {
            overtimeLevel = overtimeLevelJudge(recordEntity.getSignTimeLimit());
        }

        //需要反馈的情况下，且在有反馈时限的要求下，计算延期等级
        if (recordEntity.getIsNeedFeedback()
                && recordEntity.getFeedbackTimeLimit() != null
                && recordEntity.getRecordStatus() == DocExchangeRecordStatusEnum.WAI_FEEDBACK.ordinal()) {
            DocExchangeOvertimeLevelEnum feedbackOvetimeLevel = overtimeLevelJudge(
                    recordEntity.getFeedbackTimeLimit());
            overtimeLevel = overtimeLevel.ordinal() > feedbackOvetimeLevel.ordinal() ? overtimeLevel : feedbackOvetimeLevel;
        }
        return overtimeLevel;
    }


    private DocExchangeOvertimeLevelEnum overtimeLevelJudge(Date judgeTime) {
        if (judgeTime == null) {
            return DocExchangeOvertimeLevelEnum.NONE;
        }
        WorkPeriod workPeriod = workHourFacadeService.getWorkPeriod(new Date(), judgeTime);
        double limitDay = workPeriod == null ? 0d : workPeriod.getWorkDay();
        if (limitDay <= 0) {//超过工作日，紧急
            return DocExchangeOvertimeLevelEnum.URGE;
        } else if (limitDay <= 1 && limitDay > 0) {//距离一天，重要
            return DocExchangeOvertimeLevelEnum.IMPORTANT;
        } else {
            return DocExchangeOvertimeLevelEnum.NORMAL;
        }
    }

    @Override
    public List<String> listUuidsByStatus(List<Integer> statusList) {
        return dao.listUuidsByStatus(statusList);
    }

    /**
     * 根据表单数据保存文档交换记录
     *
     * @param dyFormActionData
     * @return
     */
    private DmsDocExchangeRecordEntity saveRecordEntityByDyFormData(
            DyFormActionData dyFormActionData) {
        try {
            Map<String, Object> docExchangeConfig = (Map<String, Object>) dyFormActionData
                    .getExtra("docExchangeConfig");
            String dataType = docExchangeConfig.get("dataType").toString();
            DmsDocExchangeRecordEntity recordEntity = new DmsDocExchangeRecordEntity();
            if (docExchangeConfig.containsKey("uuid") && StringUtils.isNotBlank(
                    docExchangeConfig.get("uuid").toString())) {
                recordEntity = getOne(docExchangeConfig.get("uuid").toString());
            }

            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            recordEntity.setDyformUuid(dyFormActionData.getFormUuid());
            recordEntity.setDataUuid(dyFormActionData.getDataUuid());
            recordEntity.setUserId(userDetails.getUserId());
            recordEntity.setFromUserId(userDetails.getUserId());
            recordEntity.setRecordStatus(DocExchangeRecordStatusEnum.DRAFT.ordinal());
            Object isNeedFeedback = docExchangeConfig.get("isNeedFeedback");
            if (isNeedFeedback != null && (
                    "1".equals(isNeedFeedback.toString())
                            || BooleanUtils.toBoolean(isNeedFeedback.toString())
            )) {
                recordEntity.setIsNeedFeedback(true);
            } else {
                recordEntity.setIsNeedFeedback(false);
            }
            Object isNeedSign = docExchangeConfig.get("isNeedSign");
            if (isNeedSign != null && (
                    "1".equals(isNeedSign.toString())
                            || BooleanUtils.toBoolean(isNeedSign.toString())
            )) {
                recordEntity.setIsNeedSign(true);
            } else {
                recordEntity.setIsNeedSign(false);
            }


            recordEntity.setExchangeType(DocExchangeTypeEnum.DYFORM.ordinal());
            if (DataType.FILE.getId().equalsIgnoreCase(dataType)) {//文件流交换，保存文件ID，文件名称
                recordEntity.setExchangeType(DocExchangeTypeEnum.FILE.ordinal());
                ArrayList<Map<String, String>> fileList = (ArrayList<Map<String, String>>) dyFormActionData.getExtra(
                        "files");
                List<String> fileUuids = Lists.newArrayList();
                List<String> fileNames = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(fileList)) {
                    for (Map<String, String> fileMap : fileList) {
                        fileUuids.add(fileMap.get("fileID"));
                        fileNames.add(fileMap.get("fileName"));
                    }
                }
                recordEntity.setFileUuids(StringUtils.join(fileUuids, Separator.SLASH.getValue()));
                recordEntity.setFileNames(StringUtils.join(fileNames, Separator.SLASH.getValue()));
            }
            if (MapUtils.isNotEmpty(docExchangeConfig)) {
                recordEntity.setFromUnitId(docExchangeConfig.get("fromUnitId").toString());
                recordEntity.setConfigUuid(docExchangeConfig.get("configUuid").toString());
                recordEntity.setRefuseToView(docExchangeConfig.get("refuseToView") != null ? Integer.parseInt(docExchangeConfig.get("refuseToView").toString()) : 0);
                recordEntity.setNoReminders(docExchangeConfig.get("noReminders") != null ? Integer.parseInt(docExchangeConfig.get("noReminders").toString()) : 0);
                if (docExchangeConfig.containsKey("docEncryptionLevel") && StringUtils.isNotBlank(
                        docExchangeConfig.get("docEncryptionLevel").toString())) {
                    recordEntity.setDocEncryptionLevel(Integer.parseInt(
                            docExchangeConfig.get("docEncryptionLevel").toString()));
                }
                if (docExchangeConfig.containsKey("docUrgeLevel") && StringUtils.isNotBlank(
                        docExchangeConfig.get("docUrgeLevel").toString())) {
                    recordEntity.setDocUrgeLevel(
                            Integer.parseInt(docExchangeConfig.get("docUrgeLevel").toString()));
                }
                try {
                    if (docExchangeConfig.containsKey("signTimeLimit") && StringUtils.isNotBlank(
                            docExchangeConfig.get("signTimeLimit").toString())) {
                        recordEntity.setSignTimeLimit(DateUtils.parseDate(
                                docExchangeConfig.get("signTimeLimit").toString(),
                                "yyyy-MM-dd HH:mm"));
                    }
                    if (docExchangeConfig.containsKey(
                            "feedbackTimeLimit") && StringUtils.isNotBlank(
                            docExchangeConfig.get("feedbackTimeLimit").toString())) {
                        recordEntity.setFeedbackTimeLimit(DateUtils.parseDate(
                                docExchangeConfig.get("feedbackTimeLimit").toString(),
                                "yyyy-MM-dd HH:mm"));
                    }
                } catch (Exception e) {
                    logger.error("文档交换保存数据，转换时间异常：", e);
                }


                recordEntity.setToUserIds(new SerialClob(
                        docExchangeConfig.get("selectUserIds").toString().toCharArray()));
                recordEntity.setToUserNames(new SerialClob(
                        docExchangeConfig.get("selectUserNames").toString().toCharArray()));
//                recordEntity.setConfigurationJson(new SerialClob(
//                        docExchangeConfig.get("configurationJson").toString().toCharArray()));

                recordEntity.setFlowUuid((String) docExchangeConfig.get("flowUuid"));
                recordEntity.setDocTitle((String) docExchangeConfig.get("documentTitle"));

                if (docExchangeConfig.containsKey("isSmsNotify")) {
                    recordEntity.setIsSmsNotify(Boolean.valueOf(docExchangeConfig.get("isSmsNotify").toString()));
                }
                if (docExchangeConfig.containsKey("isImNotify")) {
                    recordEntity.setIsImNotify(Boolean.valueOf(docExchangeConfig.get("isImNotify").toString()));
                }
                if (docExchangeConfig.containsKey("isMailNotify")) {
                    recordEntity.setIsMailNotify(Boolean.valueOf(docExchangeConfig.get("isMailNotify").toString()));
                }


            }
            this.dao.save(recordEntity);
            return recordEntity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
