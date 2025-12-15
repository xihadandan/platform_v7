package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.dms.bean.DmsDocExcExtraSendDto;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.dao.impl.DmsDocExcExtraSendDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcExtraSendEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeMessageEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.dms.enums.DocEchangeOperationEnum;
import com.wellsoft.pt.dms.enums.DocExchangeNotifyWayEnum;
import com.wellsoft.pt.dms.enums.DocExchangeTypeEnum;
import com.wellsoft.pt.dms.ext.dyform.service.DyFormActionService;
import com.wellsoft.pt.dms.service.*;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialClob;
import java.util.List;
import java.util.Set;

/**
 * Description:文档交换的补充发送详情服务
 *
 * @author chenq
 * @date 2018/5/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/18    chenq		2018/5/18		Create
 * </pre>
 */
@Service
public class DmsDocExcExtraSendServiceImpl extends
        AbstractJpaServiceImpl<DmsDocExcExtraSendEntity, DmsDocExcExtraSendDaoImpl, String> implements
        DmsDocExcExtraSendService {
    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DmsDocExchangeRecordDetailService dmsDocExchangeRecordDetailService;

    @Autowired
    private DmsDocExchangeRecordService dmsDocExchangeRecordService;

    @Autowired
    private DmsDocExchangeLogService dmsDocExchangeLogService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private DyFormActionService dyFormActionService;

    @Autowired
    private DmsDocExcContactBookService dmsDocExcContactBookService;

    @Override
    @Transactional
    public void saveExtraSendData(DocExchangeActionData actionData) {
        try {
            DocExchangeActionData.DocExcOperateData extraSendData = actionData.getExtraSendData();
            DmsDocExchangeRecordEntity recordEntity = dmsDocExchangeRecordService.getOne(
                    actionData.getDocExcRecordUuid());
            DocExchangeActionData.ToUserData userData = extraSendData.getToUserData().get(0);
            DmsDocExcExtraSendEntity excExtraSendEntity = new DmsDocExcExtraSendEntity(
                    actionData.getDocExcRecordUuid(),
                    new SerialClob(userData.getToUserId().toCharArray()),
                    new SerialClob(userData.getToUserName().toCharArray()),
                    userData.getFeedbackTimeLimit(), userData.getSignTimeLimit(), false, false,
                    false);

            if (CollectionUtils.isNotEmpty(userData.getNotifyWays())) {
                excExtraSendEntity.setIsImNotify(
                        userData.getNotifyWays().contains(DocExchangeNotifyWayEnum.IM));
                excExtraSendEntity.setIsSmsNotify(
                        userData.getNotifyWays().contains(DocExchangeNotifyWayEnum.SMS));
                excExtraSendEntity.setIsMailNotify(
                        userData.getNotifyWays().contains(DocExchangeNotifyWayEnum.MAIL));
            }

            this.dao.save(excExtraSendEntity);

            Set<String> allUserIds = dmsDocExcContactBookService.explainUserIdsBySelectIds(
                    userData.getToUserId());

            //保存接收者详情记录
            List<DmsDocExchangeRecordDetailEntity> recordDetailEntityList = dmsDocExchangeRecordDetailService.saveRecordDetails(
                    recordEntity, allUserIds, 1, excExtraSendEntity.getUuid());
            List<DmsDocExchangeRecordEntity> receiveRecordEntities = dmsDocExchangeRecordService.copyDocExchangeRecordToSender(
                    recordEntity, recordDetailEntityList, 1, excExtraSendEntity.getUuid());

            //保存接收者文档交换记录与表单数据
            DyFormData formData = null;
            if (DocExchangeTypeEnum.DYFORM.ordinal() == recordEntity.getExchangeType()) {
                formData = dyFormFacade.getDyFormData(recordEntity.getDyformUuid(),
                        recordEntity.getDataUuid());
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

                    dmsDocExchangeRecordService.save(entity);
                } catch (Exception e) {
                    logger.error("保存文档交换接收方的文档交换记录与表单数据异常：", e);
                } finally {
                    if (entity.getUserId().startsWith(IdPrefix.USER.getValue())) {
                        IgnoreLoginUtils.logout();
                    }
                }
            }

            //发送消息提示
            dmsDocExchangeRecordService.messageNotify(userData.getNotifyWays(),
                    formData != null ? formData.getFormDataOfMainform() : null,
                    DmsDocExchangeMessageEntity.DOC_EXCHANGE_RECEIVE_MSG_TEMPLATE,
                    recordEntity.getUserId(),
                    allUserIds, null, excExtraSendEntity.getSignTimeLimit(),
                    excExtraSendEntity.getFeedbackTimeLimit(), recordEntity.getDocTitle(),
                    "");

            this.dmsDocExchangeLogService.saveLog(actionData.getDocExcRecordUuid(),
                    DocEchangeOperationEnum.EXTRA_SEND.getName(),
                    SpringSecurityUtils.getCurrentUserName(), userData.getToUserName(), null, null,
                    null);

        } catch (Exception e) {
            logger.error("保存补充发送人员异常：", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<DmsDocExcExtraSendDto> listDocExcExtraSendDetail(String docExchageRecordUuid) {
        List<DmsDocExcExtraSendEntity> excExtraSendEntities = this.dao.listDocExcExtraSendDetailByRecordUuid(
                docExchageRecordUuid);
        List<DmsDocExcExtraSendDto> sendDtos = Lists.newArrayList();
        for (DmsDocExcExtraSendEntity sendEntity : excExtraSendEntities) {
            DmsDocExcExtraSendDto dto = new DmsDocExcExtraSendDto();
            BeanUtils.copyProperties(sendEntity, dto, "toUserIds", "toUserNames");
            try {
                dto.setToUserIds(IOUtils.toString(sendEntity.getToUserIds().getCharacterStream()));
                dto.setToUserNames(
                        IOUtils.toString(sendEntity.getToUserNames().getCharacterStream()));
            } catch (Exception e) {
                logger.error("查询补充发送详情，拷贝大字段数据异常：", e);
            }
            sendDtos.add(dto);
        }

        return sendDtos;
    }
}
