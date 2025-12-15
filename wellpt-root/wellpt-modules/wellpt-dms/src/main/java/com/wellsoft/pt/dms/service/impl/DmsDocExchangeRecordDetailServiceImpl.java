package com.wellsoft.pt.dms.service.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.dao.impl.DmsDocExchangeReceiveDetailDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcUrgeDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeMessageEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.dms.enums.*;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.service.DmsDocExcUrgeDetailService;
import com.wellsoft.pt.dms.service.DmsDocExchangeLogService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordDetailService;
import com.wellsoft.pt.dms.service.DmsDocExchangeRecordService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
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
public class DmsDocExchangeRecordDetailServiceImpl extends
        AbstractJpaServiceImpl<DmsDocExchangeRecordDetailEntity, DmsDocExchangeReceiveDetailDaoImpl, String> implements
        DmsDocExchangeRecordDetailService {

    @Autowired
    private DmsDocExchangeLogService dmsDocExchangeLogService;

    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private DyFormActionService dyFormActionService;

    @Autowired
    private DmsDocExcUrgeDetailService dmsDocExcUrgeDetailService;


    @Override
    public List<DmsDocExchangeRecordDetailEntity> listByDocExchangeRecordUuid(String uuid) {
        return this.dao.getByDocExchangeRecordUuid(uuid);
    }

    @Override
    @Transactional
    public void deleteByDocExchangeRecordUuid(String uuid) {
        this.dao.deleteByDocExchangeRecordUuid(uuid);
    }

    @Override
    @Transactional
    public List<DmsDocExchangeRecordDetailEntity> saveRecordDetails(
            DmsDocExchangeRecordEntity recordEntity,
            Set<String> allUserIdSet, Integer type, String extraSendUuid) {
        List<DmsDocExchangeRecordDetailEntity> receiveDetailEntities = Lists.newArrayList();
        for (String userId : allUserIdSet) {
            DmsDocExchangeRecordDetailEntity detail = new DmsDocExchangeRecordDetailEntity();
            detail.setDocExchangeRecordUuid(recordEntity.getUuid());
            detail.setToUserId(userId);
            detail.setSignStatus(
                    recordEntity.getIsNeedSign() ? DocExchangeRecordStatusEnum.WAIT_SIGN : DocExchangeRecordStatusEnum.SIGNED);
            detail.setType(type);
            detail.setExtraSendUuid(extraSendUuid);
            receiveDetailEntities.add(detail);
        }
        this.dao.saveAll(receiveDetailEntities);
        return receiveDetailEntities;
    }


    @Override
    @Transactional
    public void revokeReceiveDetail(DocExchangeActionData actionData) {
        Stopwatch revokeTimer = Stopwatch.createStarted();
        try {
            logger.info("撤回收文对象的数据开始");
            DocExchangeActionData.DocExcOperateData revokeOpData = actionData.getRevokeData();
            List<DocExchangeActionData.ToUserData> toUserDataList = revokeOpData.getToUserData();
            String toUserNames = "";
            List<String> messageUserList = Lists.newArrayList();
            List<String> revokeRecordDetailUuids = Lists.newArrayList();
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(
                    actionData.getDocExcRecordUuid());
            boolean allRevoke2Draft = StringUtils.isNotBlank(
                    revokeOpData.getOperationCode()) && revokeOpData.getOperationCode().indexOf(
                    "DRAFT") != -1;
            if (allRevoke2Draft) {
                try {
                    toUserNames = IOUtils.toString(
                            recordEntity.getToUserNames().getCharacterStream());
                } catch (Exception e) {
                }
                List<DmsDocExchangeRecordDetailEntity> detailEntityList = this.listByDocExchangeRecordUuid(
                        actionData.getDocExcRecordUuid());
                for (DmsDocExchangeRecordDetailEntity detailEntity : detailEntityList) {
                    if (BooleanUtils.isTrue(detailEntity.getIsRevoked())) {
                        continue;
                    }
                    detailEntity.setIsRevoked(true);
                    detailEntity.setRevokeReason(revokeOpData.getContent());
                    this.save(detailEntity);
                    revokeRecordDetailUuids.add(detailEntity.getUuid());
                }

            } else {

                for (DocExchangeActionData.ToUserData data : toUserDataList) {
                    DmsDocExchangeRecordDetailEntity detailEntity = getOne(data.getReceiverUuid());
                    detailEntity.setIsRevoked(true);
                    detailEntity.setRevokeReason(revokeOpData.getContent());
                    this.save(detailEntity);
                    toUserNames += data.getToUserName() + Separator.SEMICOLON.getValue();
                    if (detailEntity.getToUserId().startsWith(IdPrefix.USER.getValue())) {
                        messageUserList.add(detailEntity.getToUserId());
                    }
                    revokeRecordDetailUuids.add(detailEntity.getUuid());
                }
                toUserNames = toUserNames.substring(0, toUserNames.length() - 1);
            }

            dmsDocExchangeLogService.saveLog(actionData.getDocExcRecordUuid(),
                    DocEchangeOperationEnum.REVOKE.getName(),
                    SpringSecurityUtils.getCurrentUserName(),
                    toUserNames,
                    revokeOpData.getContent(),
                    null, null);

            //开始删除收文对象的数据
            deleteReciverDocExchangeData(revokeRecordDetailUuids,
                    revokeOpData.getOperationCode());


            //是否撤回并生成草稿
            if (allRevoke2Draft) {
                allRevokeToDraftRecord(recordEntity);
            }

            Map<String, Object> formData = null;
            if (recordEntity.getExchangeType() == DocExchangeTypeEnum.DYFORM.ordinal()) {
                DyFormData dyFormData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(),
                        recordEntity.getDataUuid());
                formData = dyFormData.getFormDataOfMainform();
            }


            //发送消息
            dmsDocExchangeRecordService.messageNotify(
                    Lists.<DocExchangeNotifyWayEnum>newArrayList(DocExchangeNotifyWayEnum.IM),
                    formData, DmsDocExchangeMessageEntity.DOC_EXCHANGE_REVOKE_MSG_TEMPLATE,
                    SpringSecurityUtils.getCurrentUserId(),
                    Sets.<String>newHashSet(messageUserList), null, null, null,
                    actionData.getDocumentTitle(), revokeOpData.getContent());

        } finally {
            logger.info("撤回收文对象的数据结束，耗时：{}", revokeTimer);
        }

    }

    /**
     * 撤回后新生成新的草稿
     *
     * @param recordEntity
     * @return
     */
    private DmsDocExchangeRecordEntity allRevokeToDraftRecord(
            DmsDocExchangeRecordEntity recordEntity) {
        if (recordEntity.getExchangeType() == DocExchangeTypeEnum.DYFORM.ordinal()) {
            DyFormData dyFormData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(),
                    recordEntity.getDataUuid());
            DmsDocExchangeRecordEntity copyRecord = new DmsDocExchangeRecordEntity();
            BeanUtils.copyProperties(recordEntity, copyRecord, "uuid");
            copyRecord.setRecordStatus(DocExchangeRecordStatusEnum.DRAFT.ordinal());
            copyRecord.setDataUuid(dyFormActionService.save(dyFormData.doCopy(true)));
            dmsDocExchangeRecordService.save(copyRecord);


            return copyRecord;
        }
        return null;
    }

    @Override
    public List<String> listAllReceiverIdsByDocExcRecordUuid(String docExcRecordUuid) {
        return this.dao.listAllReceiverIdsByDocExcRecordUuid(docExcRecordUuid);
    }

    @Override
    @Transactional
    public void urgeReceiveDetail(DocExchangeActionData actionData) {
        DocExchangeActionData.DocExcOperateData urgeData = actionData.getUrgeData();
        List<DocExchangeActionData.ToUserData> toUserDataList = urgeData.getToUserData();
        List<DmsDocExcUrgeDetailEntity> urgeDetailEntities = Lists.newArrayList();
        String toUserNames = "";
        for (DocExchangeActionData.ToUserData user : toUserDataList) {
            List<Integer> urgeWays = Lists.newArrayList();
            for (DocExchangeNotifyWayEnum way : user.getNotifyWays()) {
                urgeWays.add(way.ordinal());
            }
            toUserNames += user.getToUserName() + Separator.SEMICOLON.getValue();
            urgeDetailEntities.add(
                    new DmsDocExcUrgeDetailEntity(user.getToUserId(), urgeData.getContent(),
                            StringUtils.join(urgeWays, Separator.SEMICOLON.getValue()),
                            actionData.getDocExcRecordUuid()));
        }
        toUserNames = toUserNames.substring(0, toUserNames.length() - 1);
        dmsDocExcUrgeDetailService.saveAll(urgeDetailEntities);
        dmsDocExchangeLogService.saveLog(actionData.getDocExcRecordUuid(),
                DocEchangeOperationEnum.URGE.getName(), SpringSecurityUtils.getCurrentUserName(),
                toUserNames, urgeData.getContent(), null, null);

        //获取表单数据
        DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(
                actionData.getDocExcRecordUuid());
        Map<String, Object> formData = null;
        if (recordEntity.getExchangeType() == DocExchangeTypeEnum.DYFORM.ordinal()) {
            DyFormData dyFormData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(),
                    recordEntity.getDataUuid());
            formData = dyFormData.getFormDataOfMainform();
        }

        for (DocExchangeActionData.ToUserData user : toUserDataList) {
            if (user.getToUserId().startsWith(DocExcContactBookIdPrefixEnum.CONTACT_ID.getId())
                    || user.getToUserId().startsWith(
                    DocExcContactBookIdPrefixEnum.CONTACT_UNIT_ID.getId())) {
                continue;
            }

            //发送消息
            dmsDocExchangeRecordService.messageNotify(
                    user.getNotifyWays(),
                    formData, DmsDocExchangeMessageEntity.DOC_EXCHANGER_URGE_MSG_TEMPLATE,
                    SpringSecurityUtils.getCurrentUserId(),
                    Sets.<String>newHashSet(user.getToUserId()), null, null, null,
                    actionData.getDocumentTitle(), urgeData.getContent());

        }


    }


    private void deleteReciverDocExchangeData(List<String> revokeRecordDetailUuids,
                                              String operationCode) {

        for (String uuid : revokeRecordDetailUuids) {
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getByFromRecordDetailUuid(
                    uuid);
            if (recordEntity == null) {
                continue;
            }
            //表单数据删除
            if (DocExchangeTypeEnum.DYFORM.ordinal() == recordEntity.getExchangeType()) {
                dyFormFacade.delFullFormData(recordEntity.getDyformUuid(),
                        recordEntity.getDataUuid());
            }
            //级联撤回
            if (StringUtils.isNotBlank(operationCode) && operationCode.indexOf(
                    "REOVKE_CASCADE") != -1) {
                List<String> uuids = this.dao.listUuidByDocExchangeRecordUuid(
                        recordEntity.getUuid());

                deleteReciverDocExchangeData(uuids, operationCode);
            }


            dmsDocExchangeRecordService.delete(recordEntity);
        }

    }
}
