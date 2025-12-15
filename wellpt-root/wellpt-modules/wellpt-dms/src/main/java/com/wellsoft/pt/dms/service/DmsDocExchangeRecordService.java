package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.bean.DmsDocExchangeRecordDto;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.dao.impl.DmsDocExchangeRecordDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.dms.enums.DocExchangeNotifyWayEnum;
import com.wellsoft.pt.dms.enums.DocExchangeOvertimeLevelEnum;
import com.wellsoft.pt.jpa.service.JpaService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public interface DmsDocExchangeRecordService extends
        JpaService<DmsDocExchangeRecordEntity, DmsDocExchangeRecordDaoImpl, String> {


    /**
     * 保存文档交换记录，文档交换内容为某表单数据
     *
     * @param data
     */
    DmsDocExchangeRecordEntity saveDocExchangeRecordWithDyformData(DyFormActionData data,
                                                                   boolean isSend);

    /**
     * 发送
     *
     * @param dyFormActionData
     * @param docExchangeConfig
     * @param recordEntity
     */
    void sendDoc(DyFormActionData dyFormActionData, Map<String, Object> docExchangeConfig, DmsDocExchangeRecordEntity recordEntity);

    @Transactional
    void sendDocExchange2Reciver(DmsDocExchangeRecordEntity recordEntity);

    /**
     * 拷贝文档交换记录为发送数据
     *
     * @param source           发文记录
     * @param detailEntityList 收文用户明细
     * @param type             0 正常发送 1 补充发送 2 转发
     * @param extraSendUuid    补充发送的UUID 或  转发的UUID
     * @return
     */
    List<DmsDocExchangeRecordEntity> copyDocExchangeRecordToSender(
            DmsDocExchangeRecordEntity source,
            List<DmsDocExchangeRecordDetailEntity> detailEntityList, Integer type, String extraSendUuid);

    /**
     * 根据表单定义UUID，数据UUID获取文档交换记录
     *
     * @param formUuid
     * @param dataUuid
     * @return
     */
    DmsDocExchangeRecordEntity getByFormUuidAndDataUuid(String formUuid, String dataUuid);

    /**
     * 更新文档交换单的
     * 拒绝查看
     *
     * @param docExcRecordUuid
     * @param refuseToView
     */
    void updateRefuseToView(String docExcRecordUuid, int refuseToView);

    /**
     * 更新文档交换单的 不再提醒
     *
     * @param docExcRecordUuid
     * @param noReminders
     */
    void updateNoReminders(String docExcRecordUuid, int noReminders);

    /**
     * 更新文档交换单的状态
     *
     * @param docExcRecordUuid
     * @param recordStatus
     */
    void updateRecordStatus(String docExcRecordUuid, int recordStatus);

    /**
     * 更新文档交换为已办结
     *
     * @param docExcRecordUuid
     */
    void updateRecordFinished(String docExcRecordUuid);

    /**
     * 反馈意见
     *
     * @param actionData
     */
    void feedbackDocExchange(DocExchangeActionData actionData);

    void feedbackDocExchange(String docExcRecordUuid, DocExchangeActionData.DocExcOperateData feedbackData);

    /**
     * 当明细都已完结的情况下，更新文档交换记录为已完结
     *
     * @param docExchangeRecordUuid
     */
    void updateDocExchangeFinishedWhenDetailDone(String docExchangeRecordUuid);

    void updateDocExchangeSignFinishedWhenDetailDone(String docExchangeRecordUuid);

    /**
     * 转发
     *
     * @param actionData
     */
    void forwardDocExchange(DocExchangeActionData actionData);

    /**
     * 根据来源文档交换明细UUID获取文档交换记录
     *
     * @param uuid
     * @return
     */
    DmsDocExchangeRecordEntity getByFromRecordDetailUuid(String uuid);

    void messageNotify(List<DocExchangeNotifyWayEnum> notifyWayEnums,
                       Map<String, Object> formData, String templateId, String fromUserId,
                       Set<String> toUserIds, String fileNames, Date siginTimeLimit,
                       Date feedbackTimeLimit, String documentTitle, String remark);

    DmsDocExchangeRecordDto getRecordDtoByFormUuidAndDataUuid(String formUuid, String dataUuid);

    DmsDocExchangeRecordDto getByUuid(String uuid);

    /**
     * 更新文档交换的超时等级
     *
     * @param uuid
     */
    void updateOvertimeLevelByUuid(String uuid);

    /**
     * 判断获取文档交换的超时等级（紧要性）
     *
     * @param recordEntity
     * @return
     */
    DocExchangeOvertimeLevelEnum getOvertimeLevel(DmsDocExchangeRecordEntity recordEntity);

    /**
     * 根据状态查询文档交换uuid集合
     *
     * @param statusList
     * @return
     */
    List<String> listUuidsByStatus(List<Integer> statusList);
}
