package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryOrgService;
import com.wellsoft.pt.basicdata.business.service.BusinessRoleOrgUserService;
import com.wellsoft.pt.dms.bean.*;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.dto.DmsDocExchangeRelatedDocDto;
import com.wellsoft.pt.dms.entity.*;
import com.wellsoft.pt.dms.enums.*;
import com.wellsoft.pt.dms.event.DocExchangeEvent;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.facade.service.DocExchangerFacadeService;
import com.wellsoft.pt.dms.service.*;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:文档交换-门面服务实现类
 *
 * @author chenq
 * @date 2018/5/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/16    chenq		2018/5/16		Create
 * </pre>
 */
@Service
public class DocExchangerFacadeServiceImpl implements DocExchangerFacadeService, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;

    @Autowired
    private DmsDocExchangeConfigService dmsDocExchangeConfigService;

    @Autowired
    private DmsDocExchangeRecordDetailService dmsDocExchangeRecordDetailService;

    @Autowired
    private DmsDocExcFeedbackDetailService dmsDocExcFeedbackDetailService;

    @Autowired
    private DmsDocExcUrgeDetailService dmsDocExcUrgeDetailService;

    @Autowired
    private DmsDocExchangeLogService dmsDocExchangeLogService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;

    @Autowired
    private DyFormActionService dyFormActionService;

    @Autowired
    private DmsDocExchangeForwardService dmsDocExchangeForwardService;

    @Autowired
    private DmsDocExcExtraSendService dmsDocExcExtraSendService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private DmsDocExcContactBookService dmsDocExcContactBookService;

    @Autowired
    private DmsDocExcContactBookUnitService dmsDocExcContactBookUnitService;

    @Autowired
    private BusinessCategoryOrgService businessCategoryOrgService;

    @Autowired
    private List<DocExchangeEvent> docExchangeEvents;

    @Autowired
    private DmsDocExchangeRelatedDocService dmsDocExchangeRelatedDocService;

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
    public void processApprovalResult(String uuid, boolean isAgree) {
        DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(uuid);
        if (isAgree) {
            DyFormData formData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(),
                    recordEntity.getDataUuid());
            DyFormActionData dyFormActionData = new DyFormActionData();
            dyFormActionData.setDyFormData(formData);
            Map<String, Object> docExchangeConfig = Maps.newHashMap();
            try {
                if (recordEntity.getToUserIds() != null) {
                    docExchangeConfig.put("selectUserIds", IOUtils.toString(recordEntity.getToUserIds().getCharacterStream()));
                }
            } catch (Exception e) {
                logger.error("获取文档交换记录，大字段转换异常：", e);
                throw new RuntimeException(e);
            }
            docExchangeConfig.put("documentTitle", recordEntity.getDocTitle() == null ? "" : recordEntity.getDocTitle());
            dmsDocExchangeRecordService.sendDoc(dyFormActionData, docExchangeConfig, recordEntity);
        } else {
            recordEntity.setRecordStatus(DocExchangeRecordStatusEnum.REJECTED.ordinal());
            dmsDocExchangeRecordService.update(recordEntity);
        }
    }

    @Override
    public DmsDocExchangeRecordDto getDocExchangeRecord(String uuid, String dyformUuid,
                                                        String dataUuid) {
        if (StringUtils.isNotBlank(uuid)) {
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(uuid);
            return buildDmsDocExchangeRecordDto(recordEntity);
        }

        if (StringUtils.isNotBlank(dyformUuid) && StringUtils.isNotBlank(dataUuid)) {
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getByFormUuidAndDataUuid(
                    dyformUuid, dataUuid);
            return buildDmsDocExchangeRecordDto(recordEntity);
        }

        return null;

    }


    private DmsDocExchangeRecordDto buildDmsDocExchangeRecordDto(
            DmsDocExchangeRecordEntity recordEntity) {
        DmsDocExchangeRecordDto dto = new DmsDocExchangeRecordDto();
        BeanUtils.copyProperties(recordEntity, dto, "toUserIds", "toUserNames",
                "configurationJson");
        String userId = StringUtils.isNotBlank(
                recordEntity.getFromUserId()) ? recordEntity.getFromUserId() : dto.getUserId();
        OrgUserVo userVo = multiOrgApiFacade.getUserById(userId);
        dto.setUserName(userVo.getUserName());
        if (StringUtils.isNotBlank(recordEntity.getFromUnitId())) {
            BusinessCategoryOrgEntity orgEntity = businessCategoryOrgService.getBusinessById(recordEntity.getFromUnitId());
            if (orgEntity != null) {
                dto.setUserUnitName(orgEntity.getName());
            }
        }
        if (StringUtils.isBlank(dto.getUserUnitName())) {
            MultiOrgSystemUnit orgSystemUnit = orgApiFacade.getSystemUnitById(userVo.getSystemUnitId());
            dto.setUserUnitName(orgSystemUnit.getName());
        }
        try {
            if (recordEntity.getToUserIds() != null) {
                dto.setToUserIds(
                        IOUtils.toString(recordEntity.getToUserIds().getCharacterStream()));
                dto.setToUserNames(
                        IOUtils.toString(recordEntity.getToUserNames().getCharacterStream()));
            }
            if (recordEntity.getConfigurationJson() != null) {
                dto.setConfigurationJson(
                        IOUtils.toString(recordEntity.getConfigurationJson().getCharacterStream()));
            }
        } catch (Exception e) {
            logger.error("获取文档交换记录，大字段转换异常：", e);
        }

        DmsDocExchangeForwardDto forwardDto = dmsDocExchangeForwardService.getSignedForwardByRecordUuid(
                dto.getUuid());
        dto.setForwardDto(forwardDto);
        return dto;
    }


    @Override
    public List<DmsDocExchangeRecordDetailDto> listDocExchangeReceiverDetail(
            String docExchageRecordUuid) {
        List<DmsDocExchangeRecordDetailEntity> docExchangeReceiveDetailEntities = dmsDocExchangeRecordDetailService.listByDocExchangeRecordUuid(
                docExchageRecordUuid);
        List<DmsDocExchangeRecordDetailDto> detailDtoList = Lists.newArrayList();
        DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(
                docExchageRecordUuid);
        for (DmsDocExchangeRecordDetailEntity detailEntity : docExchangeReceiveDetailEntities) {
            DmsDocExchangeRecordDetailDto detailDto = new DmsDocExchangeRecordDetailDto();
            BeanUtils.copyProperties(detailEntity, detailDto);
            detailDto.setToUserName(this.getUserNameByUserId(detailDto.getToUserId()));
            if (StringUtils.isNotBlank(detailDto.getSignUserId())) {
                detailDto.setSignUserName(getUserNameByUserId(detailDto.getSignUserId()));
            }
            detailDto.setSignTimeLimit(recordEntity.getSignTimeLimit());
            detailDto.setFeedbackTimeLimit(recordEntity.getFeedbackTimeLimit());
            if (detailEntity.getType() == 1 && StringUtils.isNotBlank(detailEntity.getExtraSendUuid())) {
                DmsDocExcExtraSendEntity dmsDocExcExtraSendEntity = dmsDocExcExtraSendService.getOne(detailEntity.getExtraSendUuid());
                detailDto.setSignTimeLimit(dmsDocExcExtraSendEntity.getSignTimeLimit());
                detailDto.setFeedbackTimeLimit(dmsDocExcExtraSendEntity.getFeedbackTimeLimit());
            }
            if (detailEntity.getType() == 2 && StringUtils.isNotBlank(detailEntity.getExtraSendUuid())) {
                DmsDocExchangeForwardEntity forwardEntity = dmsDocExchangeForwardService.getOne(detailEntity.getExtraSendUuid());
                detailDto.setSignTimeLimit(forwardEntity.getSignTimeLimit());
                detailDto.setFeedbackTimeLimit(forwardEntity.getFeedbackTimeLimit());
            }
            if (detailDto.getSignTimeLimit() != null) {
                //距离签收时限仅一天的情况下，需要提醒
                detailDto.setSignNearDeadline(DateUtils.addDays(new Date(), -1).compareTo(
                        detailDto.getSignTimeLimit()) <= 0);
                //逾期
                if (detailDto.getSignTime() != null && detailDto.getSignTime().after(detailDto.getSignTimeLimit())) {
                    detailDto.setOverdue(true);
                }
            }
            detailDtoList.add(detailDto);
        }
        return detailDtoList;
    }


    @Override
    public List<DmsDocExchangeForwardOrgDto> listDmsDocExchangeForwardOrgDto(String docExchageRecordUuid) {
        List<DmsDocExchangeRecordDetailEntity> docExchangeReceiveDetailEntities = dmsDocExchangeRecordDetailService.listByDocExchangeRecordUuid(docExchageRecordUuid);
        List<String> recordDetailUuidList = new ArrayList<>();
        for (DmsDocExchangeRecordDetailEntity docExchangeReceiveDetailEntity : docExchangeReceiveDetailEntities) {
            recordDetailUuidList.add(docExchangeReceiveDetailEntity.getUuid());
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("fromRecordDetailUuids", recordDetailUuidList);
        List<DmsDocExchangeRecordEntity> recordEntityList = dmsDocExchangeRecordService.listByHQL("from DmsDocExchangeRecordEntity where fromRecordDetailUuid in (:fromRecordDetailUuids) ", params);
        List<DmsDocExchangeForwardOrgDto> forwardOrgDtoList = new ArrayList<>();
        for (DmsDocExchangeRecordEntity recordEntity : recordEntityList) {
            Map<String, Object> forwardParms = Maps.newHashMap();
            forwardParms.put("docExchangeRecordUuid", recordEntity.getUuid());
            List<DmsDocExchangeForwardEntity> forwardEntityList = dmsDocExchangeForwardService.listByHQL("from DmsDocExchangeForwardEntity where docExchangeRecordUuid=:docExchangeRecordUuid order by createTime asc ", forwardParms);
            if (!forwardEntityList.isEmpty()) {
                DmsDocExchangeForwardOrgDto forwardOrgDto = new DmsDocExchangeForwardOrgDto();
                forwardOrgDto.setForwardUnitId(recordEntity.getUserId());
                forwardOrgDto.setForwardUnitName(this.getUserNameByUserId(recordEntity.getUserId()));
                forwardOrgDto.setForwardDate(forwardEntityList.get(0).getCreateTime());
                List<DmsDocExchangeRecordDetailDto> forwardRecordDetailDtoList = new ArrayList<>();
                List<DmsDocExchangeRecordDetailDto> recordDetailDtoList = this.listDocExchangeReceiverDetail(recordEntity.getUuid());
                for (DmsDocExchangeRecordDetailDto recordDetailDto : recordDetailDtoList) {
                    if (recordDetailDto.getType() == 2) {
                        forwardRecordDetailDtoList.add(recordDetailDto);
                    }
                }
                forwardOrgDto.setForwardList(forwardRecordDetailDtoList);
                forwardOrgDtoList.add(forwardOrgDto);
            }
        }
        return forwardOrgDtoList;
    }


    private String getUserNameByUserId(String userId) {
        if (userId.startsWith(
                DocExcContactBookIdPrefixEnum.CONTACT_ID.getId())) {
            DmsDocExcContactBookEntity bookEntity = dmsDocExcContactBookService.getByContactId(
                    userId);
            return bookEntity != null ? bookEntity.getContactName() : "";
        }

        if (userId.startsWith(
                DocExcContactBookIdPrefixEnum.CONTACT_UNIT_ID.getId())) {
            DmsDocExcContactBookUnitEntity unitEntity = dmsDocExcContactBookUnitService.getByUnitId(
                    userId);
            return unitEntity != null ? unitEntity.getUnitName() : "";

        }
        if (userId.startsWith(
                IdPrefix.EXTERNAL.getValue())) { //外部单位
            BusinessCategoryOrgEntity orgEntity = businessCategoryOrgService.getBusinessById(userId);
            return orgEntity != null ? orgEntity.getName() : "";
        }

        OrgUserVo userVo = multiOrgApiFacade.getUserById(userId);
        return userVo != null ? userVo.getUserName() : "";
    }

    @Override
    public List<DmsDocExcFeedbackDetailDto> listDocExcFeedbackDetail(String docExchageRecordUuid) {
        List<DmsDocExcFeedbackDetailEntity> feedbackDetailEntities = dmsDocExcFeedbackDetailService.listFeedbackDetailsByDocExcRecordUuid(
                docExchageRecordUuid);
        DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(docExchageRecordUuid);
        List<DmsDocExcFeedbackDetailDto> feedbackDetailDtoList = Lists.newArrayList();
        for (DmsDocExcFeedbackDetailEntity feedbackDetailEntity : feedbackDetailEntities) {
            if (feedbackDetailEntity.getFeedbackType().equals(DocExchangeFeedbackTypeEnum.RECEIVER_FEEDBACK)) {
                DmsDocExcFeedbackDetailDto dto = new DmsDocExcFeedbackDetailDto();
                BeanUtils.copyProperties(feedbackDetailEntity, dto);
                OrgUserVo userVo = multiOrgApiFacade.getUserById(dto.getFromUserId());
                dto.setFromUserName(userVo.getUserName());
                dto.setFromUserUnitName(this.getUserNameByUserId(dto.getFromUnitId()));
                if (recordEntity.getFeedbackTimeLimit() != null) {
                    //逾期
                    if (dto.getFeedbackTime().after(recordEntity.getFeedbackTimeLimit())) {
                        dto.setOverdue(true);
                    }
                }
                dto.setAnswerContent(this.getAnswerFeedbackContent(dto.getUuid()));
                feedbackDetailDtoList.add(dto);
            }
        }
        return feedbackDetailDtoList;
    }

    @Override
    public List<DmsDocExcFeedbackOrgDetailDto> listDocExcFeedbackOrgDetail(String docExchageRecordUuid) {
        DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(docExchageRecordUuid);
        List<DmsDocExchangeRecordDetailEntity> docExchangeReceiveDetailEntities = dmsDocExchangeRecordDetailService.listByDocExchangeRecordUuid(docExchageRecordUuid);
        Boolean isFeedback = null;
        if (recordEntity.getFromRecordDetailUuid() != null) {
            DmsDocExchangeRecordDetailEntity detailEntity = dmsDocExchangeRecordDetailService.getOne(recordEntity.getFromRecordDetailUuid());
            isFeedback = detailEntity.getIsFeedback();
            //没有签收记录 添加明细
            if (docExchangeReceiveDetailEntities.size() == 0) {
                docExchangeReceiveDetailEntities.add(detailEntity);
            }
        }

        List<DmsDocExcFeedbackOrgDetailDto> feedbackOrgDetailDtoList = new ArrayList<>();
        for (DmsDocExchangeRecordDetailEntity recordDetailEntity : docExchangeReceiveDetailEntities) {
            if (recordDetailEntity.getSignStatus().equals(DocExchangeRecordStatusEnum.RETURNED)) {
                continue;
            }
            DmsDocExcFeedbackOrgDetailDto feedbackOrgDetailDto = new DmsDocExcFeedbackOrgDetailDto();
            if (recordDetailEntity.getType() == 2) {
                feedbackOrgDetailDto.setFeedback(recordDetailEntity.getIsFeedback());
            } else {
                feedbackOrgDetailDto.setFeedback(isFeedback != null ? isFeedback : recordDetailEntity.getIsFeedback());
            }

            if (recordEntity.getFeedbackTimeLimit() != null) {
                feedbackOrgDetailDto.setFeedbackTimeLimit(recordEntity.getFeedbackTimeLimit());
                //距离反馈时限仅一天的情况下，需要提醒
                feedbackOrgDetailDto.setFeedbackNearDeadline(DateUtils.addDays(new Date(), -1).compareTo(
                        recordEntity.getFeedbackTimeLimit()) <= 0);
            }
            feedbackOrgDetailDto.setUnitId(recordDetailEntity.getToUserId());
            feedbackOrgDetailDto.setUnitName(this.getUserNameByUserId(recordDetailEntity.getToUserId()));
            Map<String, Object> parms = Maps.newHashMap();
            parms.put("fromUnitId", recordDetailEntity.getToUserId());
            parms.put("docExchageRecordUuid", docExchageRecordUuid);
            parms.put("feedbackType", DocExchangeFeedbackTypeEnum.RECEIVER_FEEDBACK);
            List<DmsDocExcFeedbackDetailEntity> feedbackDetailEntityList = dmsDocExcFeedbackDetailService.listByHQL("from DmsDocExcFeedbackDetailEntity where " +
                    "docExchangeRecordUuid=:docExchageRecordUuid and fromUnitId=:fromUnitId and feedbackType=:feedbackType  " +
                    "order by feedbackTime desc", parms);
            if (StringUtils.isNotBlank(recordEntity.getFromRecordDetailUuid())) {
                feedbackOrgDetailDto.setRecordDetailUuid(recordEntity.getFromRecordDetailUuid());
            } else {
                feedbackOrgDetailDto.setRecordDetailUuid(recordDetailEntity.getUuid());
            }
            if (feedbackDetailEntityList.isEmpty()) {
                feedbackOrgDetailDtoList.add(feedbackOrgDetailDto);
            } else {
                for (DmsDocExcFeedbackDetailEntity feedbackDetailEntity : feedbackDetailEntityList) {
                    DmsDocExcFeedbackOrgDetailDto sub = new DmsDocExcFeedbackOrgDetailDto();
                    BeanUtils.copyProperties(feedbackOrgDetailDto, sub);
                    BeanUtils.copyProperties(feedbackDetailEntity, sub);
                    sub.setFromUserName(this.getUserNameByUserId(feedbackDetailEntity.getFromUserId()));
                    sub.setFromUserUnitName(this.getUserNameByUserId(feedbackDetailEntity.getFromUnitId()));
                    if (recordEntity.getFeedbackTimeLimit() != null) {
                        //逾期
                        if (feedbackDetailEntity.getFeedbackTime().after(recordEntity.getFeedbackTimeLimit())) {
                            sub.setOverdue(true);
                        }
                    }
                    sub.setAnswerContent(this.getAnswerFeedbackContent(sub.getUuid()));
                    feedbackOrgDetailDtoList.add(sub);
                }
            }
        }
        return feedbackOrgDetailDtoList;
    }

    private String getAnswerFeedbackContent(String feedbackDetailUuid) {
        StringBuilder answerBuilder = new StringBuilder();
        Map<String, OrgUserVo> orgUserMap = Maps.newHashMap();
        //加载回执内容
        if (StringUtils.isNotBlank(feedbackDetailUuid)) {
            List<DmsDocExcFeedbackDetailEntity> answerFeedbackDetailEntities = dmsDocExcFeedbackDetailService.listByToFeedbackDetailUuid(
                    feedbackDetailUuid);
            for (DmsDocExcFeedbackDetailEntity ans : answerFeedbackDetailEntities) {
                String actionName = "";
                if (DocExchangeFeedbackTypeEnum.SENDER_ANSWER.equals(ans.getFeedbackType())) {
                    actionName = "回执";
                } else if (DocExchangeFeedbackTypeEnum.SENDER_REQUEST_FEEDBACK_AGAIN.equals(
                        ans.getFeedbackType())) {
                    actionName = "要求再次反馈";
                }
                OrgUserVo user = orgUserMap.containsKey(ans.getFromUserId()) ? orgUserMap.get(
                        ans.getFromUserId()) : multiOrgApiFacade.getUserById(
                        ans.getFromUserId());
                answerBuilder.append(user.getUserName()).append(" [" + actionName + "]：").append(
                        ans.getContent()).append(
                        DateFormatUtils.format(ans.getFeedbackTime(),
                                " (yyyy-MM-dd HH:mm:ss)")).append("<br>");
            }
        }
        return answerBuilder.toString();
    }


    @Override
    public List<DmsDocExcUrgeDetailDto> listDocExcUrgeDetail(String docExchageRecordUuid) {
        List<DmsDocExcUrgeDetailEntity> urgeDetailEntities = dmsDocExcUrgeDetailService.listDocExcUrgeDetail(
                docExchageRecordUuid);
        List<DmsDocExcUrgeDetailDto> urgeDetailDtos = Lists.newArrayList();
        for (DmsDocExcUrgeDetailEntity entity : urgeDetailEntities) {
            DmsDocExcUrgeDetailDto dto = new DmsDocExcUrgeDetailDto();
            BeanUtils.copyProperties(entity, dto);
            dto.setToUserName(getUserNameByUserId(dto.getToUserId()));
            String[] urgeWayArr = dto.getUrgeWay().split(Separator.SEMICOLON.getValue());
            String wayNames = "";
            for (String way : urgeWayArr) {
                wayNames += DocExchangeNotifyWayEnum.getByCode(
                        Integer.parseInt(way)).getName() + Separator.SPACE.getValue();
            }
            dto.setUrgeWayName(wayNames);
            urgeDetailDtos.add(dto);
        }
        return urgeDetailDtos;
    }

    @Override
    public List<DmsDocExchangeLogDto> listDocExchangecLogs(String docExchageRecordUuid) {
        List<DmsDocExchangeLogEntity> logEntities = dmsDocExchangeLogService.listLogByRecordUuid(
                docExchageRecordUuid);
        List<DmsDocExchangeLogDto> logDtoList = Lists.newArrayList();
        for (DmsDocExchangeLogEntity entity : logEntities) {
            DmsDocExchangeLogDto dto = new DmsDocExchangeLogDto();
            BeanUtils.copyProperties(entity, dto, "target");
            try {
                if (entity.getTarget() != null)
                    dto.setTarget(IOUtils.toString(entity.getTarget().getCharacterStream()));
            } catch (Exception e) {
                logger.error("查询收文操作日志大字段读取异常：", e);
            }

            logDtoList.add(dto);
        }
        return logDtoList;
    }


    @Override
    @Transactional
    public boolean signDocExchangeRecord(String docExchageRecordUuid, boolean isReturn,
                                         String returnReason) {
        DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(docExchageRecordUuid);
        if (recordEntity == null) {
            return true;
        }
        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(recordEntity.getConfigUuid());
        if (!isReturn && configEntity.getBusinessCategoryUuid() != null) {
            if (!recordEntity.getUserId().equals(SpringSecurityUtils.getCurrentUserId())) {
                List<BusinessRoleOrgUserEntity> roleOrgUserEntityList = businessRoleOrgUserService.findByOrgUuidAndRoleUuid(configEntity.getBusinessCategoryUuid(), null, configEntity.getRecipientRoleUuid());
                boolean flg = false;
                for (BusinessRoleOrgUserEntity businessRoleOrgUserEntity : roleOrgUserEntityList) {
                    if (businessRoleOrgUserEntity.getUsers().indexOf(SpringSecurityUtils.getCurrentUserId()) > -1) {
                        flg = true;
                        break;
                    }
                }
                if (!flg) {
                    throw new RuntimeException("您当前无权限收件，请联系管理员");
                }
            }
        }
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String fromRecordDetailUuid = recordEntity.getFromRecordDetailUuid();
        if (isReturn) {
            //退回操作，删除操作
            if (recordEntity.getExchangeType() == DocExchangeTypeEnum.DYFORM.ordinal()) {
                dyFormActionService.delete(recordEntity.getDyformUuid(),
                        recordEntity.getDataUuid(), false);
            }
            dmsDocExchangeRecordService.delete(docExchageRecordUuid);
        } else {
            //签收操作
            recordEntity.setRecordStatus(
                    recordEntity.getIsNeedFeedback() ? DocExchangeRecordStatusEnum.WAI_FEEDBACK.ordinal() : DocExchangeRecordStatusEnum.SIGNED.ordinal());
            //接收用户签收，记录一条文档交换记录的明细
            DmsDocExchangeRecordDetailEntity receiveDetailEntity = new DmsDocExchangeRecordDetailEntity();
            receiveDetailEntity.setSignUserId(userDetails.getUserId());
            receiveDetailEntity.setSignTime(new Date());
            receiveDetailEntity.setSignStatus(DocExchangeRecordStatusEnum.SIGNED);
            receiveDetailEntity.setDocExchangeRecordUuid(docExchageRecordUuid);
            receiveDetailEntity.setToUserId(recordEntity.getUserId());
            dmsDocExchangeRecordService.save(recordEntity);
            dmsDocExchangeRecordDetailService.save(receiveDetailEntity);
            //签收，更新超时等级
            dmsDocExchangeRecordService.updateOvertimeLevelByUuid(docExchageRecordUuid);
            //签收事件
            if (StringUtils.isNotBlank(configEntity.getSignEvent())) {
                DocExchangeEvent docExchangeEvent = exchangeEventMap.get(configEntity.getSignEvent());
                docExchangeEvent.signEvent(recordEntity);
            }
            //记录日志
            dmsDocExchangeLogService.saveLog(docExchageRecordUuid,
                    DocEchangeOperationEnum.SIGN.getName(),
                    userDetails.getUserName(), null, null, null, null);
        }


        if (fromRecordDetailUuid != null) {
            //通知发文方的接收详情做对应的修改
            try {
                DmsDocExchangeRecordDetailEntity recordDetailEntity = dmsDocExchangeRecordDetailService.getOne(
                        fromRecordDetailUuid);
                IgnoreLoginUtils.login(Config.DEFAULT_TENANT, recordDetailEntity.getCreator());
                if (recordDetailEntity != null) {
                    recordDetailEntity.setSignStatus(
                            isReturn ? DocExchangeRecordStatusEnum.RETURNED : DocExchangeRecordStatusEnum.SIGNED);
                    recordDetailEntity.setSignTime(new Date());
                    recordDetailEntity.setSignUserId(userDetails.getUserId());
                    recordDetailEntity.setReturnReason(returnReason);
                    dmsDocExchangeRecordDetailService.save(recordDetailEntity);
                    dmsDocExchangeRecordDetailService.flushSession();
                    if (recordEntity.getIsNeedSign() && !recordEntity.getIsNeedFeedback()) {
                        if (configEntity.getAutoFinish() != null && configEntity.getAutoFinish() == 1) {
                            //需要反馈的情况下，如果发送方的收文对象都反馈结束，那么发送方自动更新为已办结
                            dmsDocExchangeRecordService.updateDocExchangeSignFinishedWhenDetailDone(recordDetailEntity.getDocExchangeRecordUuid());
                        }
                    }

                }


            } catch (Exception e) {
                logger.error("收文签收或者退回操作通知发文处理异常：", e);
                throw new RuntimeException("操作异常");
            } finally {
                IgnoreLoginUtils.logout();
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean answerFeedbackDetail(String docEchangeFeedbackDetailUuid, String content,
                                        DocExchangeFeedbackTypeEnum feedbackType) {
        DmsDocExcFeedbackDetailEntity feedbackDetailEntity = dmsDocExcFeedbackDetailService.getOne(
                docEchangeFeedbackDetailUuid);
        String fromFeedbackDetailUuid = feedbackDetailEntity.getFromFeedbackDetailUuid();
        DmsDocExcFeedbackDetailEntity fromFeedbackDetailEntity = dmsDocExcFeedbackDetailService.getOne(
                fromFeedbackDetailUuid);
        DmsDocExchangeRecordEntity fromDocExcRecordEntity = dmsDocExchangeRecordService.getOne(
                fromFeedbackDetailEntity.getDocExchangeRecordUuid());
        //1.保存回执信息
        DmsDocExcFeedbackDetailEntity answerFeedbackEntity = new DmsDocExcFeedbackDetailEntity();
        answerFeedbackEntity.setFeedbackTime(new Date());
        answerFeedbackEntity.setToUserId(feedbackDetailEntity.getFromUserId());
        answerFeedbackEntity.setFromUserId(SpringSecurityUtils.getCurrentUserId());
        answerFeedbackEntity.setDocExchangeRecordUuid(
                feedbackDetailEntity.getDocExchangeRecordUuid());
        answerFeedbackEntity.setContent(content);
        answerFeedbackEntity.setToFeedbackDetailUuid(docEchangeFeedbackDetailUuid);
        answerFeedbackEntity.setFeedbackType(feedbackType);
        dmsDocExcFeedbackDetailService.save(answerFeedbackEntity);

        DocEchangeOperationEnum operationEnum = DocEchangeOperationEnum.ANSWER;
        //要求再次反馈的，重置文档交换记录收文者为未反馈
        if (DocExchangeFeedbackTypeEnum.SENDER_REQUEST_FEEDBACK_AGAIN.equals(feedbackType)) {
            DmsDocExchangeRecordDetailEntity recordDetailEntity = dmsDocExchangeRecordDetailService.getOne(
                    fromDocExcRecordEntity.getFromRecordDetailUuid());
            recordDetailEntity.setIsFeedback(false);//重置明细表示未反馈
            dmsDocExchangeRecordDetailService.save(recordDetailEntity);
            operationEnum = DocEchangeOperationEnum.REQUEST_FEEDBACK_AGAIN;
        }


        //2.回执发送给收文人
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, feedbackDetailEntity.getFromUserId());
            DmsDocExcFeedbackDetailEntity answer2FeedbakEntity = new DmsDocExcFeedbackDetailEntity();
            answer2FeedbakEntity.setFeedbackTime(answerFeedbackEntity.getFeedbackTime());
            answer2FeedbakEntity.setToUserId(answerFeedbackEntity.getToUserId());
            answer2FeedbakEntity.setFromUserId(SpringSecurityUtils.getCurrentUserId());
            answer2FeedbakEntity.setDocExchangeRecordUuid(
                    fromFeedbackDetailEntity.getDocExchangeRecordUuid());
            answer2FeedbakEntity.setContent(content);
            answer2FeedbakEntity.setToFeedbackDetailUuid(fromFeedbackDetailUuid);
            answer2FeedbakEntity.setFeedbackType(feedbackType);
            dmsDocExcFeedbackDetailService.save(answer2FeedbakEntity);
            //要求再次反馈的，重置状态为待签收
            if (DocExchangeFeedbackTypeEnum.SENDER_REQUEST_FEEDBACK_AGAIN.equals(feedbackType)) {
                fromDocExcRecordEntity.setRecordStatus(
                        DocExchangeRecordStatusEnum.WAI_FEEDBACK.ordinal());
                dmsDocExchangeRecordService.save(fromDocExcRecordEntity);
            }

        } catch (Exception e) {
            logger.error("回执信息发送给收文人异常：", e);
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }

        OrgUserVo user = multiOrgApiFacade.getSimpleUserInfoById(answerFeedbackEntity.getToUserId());
        OrgUserVo createUser = multiOrgApiFacade.getSimpleUserInfoById(answerFeedbackEntity.getCreator());
        //3.保存日志
        dmsDocExchangeLogService.saveLog(feedbackDetailEntity.getDocExchangeRecordUuid(),
                operationEnum.getName(), createUser != null ? createUser.getUserName() : answerFeedbackEntity.getCreator(),
                user != null ? user.getUserName() : answerFeedbackEntity.getToUserId(), answerFeedbackEntity.getContent(), null, null);
        //要求再次反馈的，需要发送消息提示
        if (DocExchangeFeedbackTypeEnum.SENDER_REQUEST_FEEDBACK_AGAIN.equals(feedbackType)) {
            //获取表单数据
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(
                    fromDocExcRecordEntity.getUuid());
            Map<String, Object> formData = null;
            if (recordEntity.getExchangeType() == DocExchangeTypeEnum.DYFORM.ordinal()) {
                DyFormData dyFormData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(),
                        recordEntity.getDataUuid());
                formData = dyFormData.getFormDataOfMainform();
            }

            //发送消息
            dmsDocExchangeRecordService.messageNotify(
                    Lists.<DocExchangeNotifyWayEnum>newArrayList(DocExchangeNotifyWayEnum.IM),
                    formData,
                    DmsDocExchangeMessageEntity.DOC_EXCHANGER_REQUEST_FEEDBACK_AGAIN_MSG_TEMPLATE,
                    SpringSecurityUtils.getCurrentUserId(),
                    Sets.<String>newHashSet(feedbackDetailEntity.getFromUserId()), null, null, null,
                    "", content);
        }

        return true;
    }

    @Override
    public List<DmsDocExchangeForwardDto> listDocExchangeForwardDetail(
            String docExchageRecordUuid) {
        return dmsDocExchangeForwardService.listSendedForwardByRecordUuid(docExchageRecordUuid);
    }

    @Override
    public List<DmsDocExcExtraSendDto> listDocExcExtraSendDetail(String docExchageRecordUuid) {
        return dmsDocExcExtraSendService.listDocExcExtraSendDetail(docExchageRecordUuid);
    }

    @Override
    public List<DmsDocExchangeRelatedDocDto> listRelatedDoc(String recordDetailUuid) {
        List<DmsDocExchangeRelatedDocDto> relatedDocDtoList = new ArrayList<>();
        DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getByFromRecordDetailUuid(recordDetailUuid);
        //是发件方需要判断权限
        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(recordEntity.getConfigUuid());
        //办理过程查看设置（发件单位查看收件单位办理过程相关文档）
        if (configEntity.getProcessView() == null || configEntity.getProcessView() == 0) {
            return relatedDocDtoList;
        }
        // 收件单位已拒绝查看
        if (recordEntity.getRefuseToView() != null && recordEntity.getRefuseToView() == 1) {
            return relatedDocDtoList;
        }
        DmsDocExchangeRelatedDocEntity relatedDocEntity = new DmsDocExchangeRelatedDocEntity();
        relatedDocEntity.setFromRecordDetailUuid(recordDetailUuid);
        List<DmsDocExchangeRelatedDocEntity> relatedDocEntityList = dmsDocExchangeRelatedDocService.listByEntity(relatedDocEntity);
        for (DmsDocExchangeRelatedDocEntity docEntity : relatedDocEntityList) {
            DmsDocExchangeRelatedDocDto docDto = new DmsDocExchangeRelatedDocDto();
            BeanUtils.copyProperties(docEntity, docDto);
            relatedDocDtoList.add(docDto);
        }
        return relatedDocDtoList;
    }

    @Override
    public List<DmsDocExchangeRelatedDocDto> listRelatedDocByRecordUuid(String docExchangeRecordUuid) {
        List<DmsDocExchangeRelatedDocDto> relatedDocDtoList = new ArrayList<>();
        if (docExchangeRecordUuid == null) {
            return relatedDocDtoList;
        }
        DmsDocExchangeRelatedDocEntity relatedDocEntity = new DmsDocExchangeRelatedDocEntity();
        relatedDocEntity.setDocExchangeRecordUuid(docExchangeRecordUuid);
        List<DmsDocExchangeRelatedDocEntity> relatedDocEntityList = dmsDocExchangeRelatedDocService.listByEntity(relatedDocEntity);
        for (DmsDocExchangeRelatedDocEntity docEntity : relatedDocEntityList) {
            DmsDocExchangeRelatedDocDto docDto = new DmsDocExchangeRelatedDocDto();
            BeanUtils.copyProperties(docEntity, docDto);
            relatedDocDtoList.add(docDto);
        }
        return relatedDocDtoList;
    }

    @Override
    public void saveContactBook(DmsDocExcContactBookDto contactBookDto) {
        dmsDocExcContactBookService.saveContactBook(contactBookDto);
    }

    @Override
    public void saveContactBookUnit(DmsDocExcContactBookUnitDto contactBookUnitDto) {
        dmsDocExcContactBookUnitService.saveContactBookUnit(contactBookUnitDto);
    }

    @Override
    public void deleteContactBook(List<String> uuids) {
        dmsDocExcContactBookService.deleteByUuids(uuids);
    }

    @Override
    public void deleteContactBookUnit(List<String> uuids) {
        dmsDocExcContactBookUnitService.deleteByUuids(uuids);
    }


}
