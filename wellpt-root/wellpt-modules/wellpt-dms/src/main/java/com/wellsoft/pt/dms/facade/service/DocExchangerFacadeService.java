package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.bean.*;
import com.wellsoft.pt.dms.dto.DmsDocExchangeRelatedDocDto;
import com.wellsoft.pt.dms.enums.DocExchangeFeedbackTypeEnum;

import java.util.List;

/**
 * Description: 文档交换-门面服务
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
public interface DocExchangerFacadeService extends BaseService {

    /**
     * 审批通过发送
     *
     * @param uuid
     * @param isAgree
     */
    public void processApprovalResult(String uuid, boolean isAgree);


    /**
     * 获取文档交换记录数据
     *
     * @param uuid       文档交换记录UUID
     * @param dyformUuid 表单定义UUID
     * @param dataUuid   文档交换记录UUID
     * @return
     */
    DmsDocExchangeRecordDto getDocExchangeRecord(String uuid, String dyformUuid, String dataUuid);


    /**
     * 获取文档交换记录的接收者详情
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExchangeRecordDetailDto> listDocExchangeReceiverDetail(String docExchageRecordUuid);


    /**
     * 获取文档交换记录的转发接收者详情
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExchangeForwardOrgDto> listDmsDocExchangeForwardOrgDto(String docExchageRecordUuid);

    /**
     * 获取文档交换记录的接收者反馈详情
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExcFeedbackDetailDto> listDocExcFeedbackDetail(String docExchageRecordUuid);

    List<DmsDocExcFeedbackOrgDetailDto> listDocExcFeedbackOrgDetail(String docExchageRecordUuid);

    /**
     * 获取文档交换记录的催办详情
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExcUrgeDetailDto> listDocExcUrgeDetail(String docExchageRecordUuid);

    /**
     * 获取文档交换记录的操作日志详情
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExchangeLogDto> listDocExchangecLogs(String docExchageRecordUuid);

    /**
     * 签收文档交换记录
     *
     * @param docExchageRecordUuid
     * @param isReturn             是否退回
     * @return
     */
    boolean signDocExchangeRecord(String docExchageRecordUuid, boolean isReturn,
                                  String returnReason);


    /**
     * 回执收文人的反馈信息
     *
     * @param docEchangeFeedbackDetailUuid 反馈明细UUID
     * @param feedbackType
     * @return
     */
    boolean answerFeedbackDetail(String docEchangeFeedbackDetailUuid, String content,
                                 DocExchangeFeedbackTypeEnum feedbackType);

    /**
     * 获取转发情况
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExchangeForwardDto> listDocExchangeForwardDetail(String docExchageRecordUuid);

    /**
     * 获取补充发送详情
     *
     * @param docExchageRecordUuid
     * @return
     */
    List<DmsDocExcExtraSendDto> listDocExcExtraSendDetail(String docExchageRecordUuid);


    /**
     * 查看相关文档
     *
     * @param recordDetailUuid
     * @return
     */
    List<DmsDocExchangeRelatedDocDto> listRelatedDoc(String recordDetailUuid);

    /**
     * 查看相关文档
     *
     * @param docExchangeRecordUuid
     * @return
     */
    List<DmsDocExchangeRelatedDocDto> listRelatedDocByRecordUuid(String docExchangeRecordUuid);

    /**
     * 添加文档交换通讯录
     *
     * @param contactBookDto
     */
    void saveContactBook(DmsDocExcContactBookDto contactBookDto);

    /**
     * 添加文档交换通讯录单位
     *
     * @param contactBookUnitDto
     */
    void saveContactBookUnit(DmsDocExcContactBookUnitDto contactBookUnitDto);

    /**
     * 删除文档交换通讯录
     *
     * @param uuids
     */
    void deleteContactBook(List<String> uuids);

    /**
     * 删除文档交换通讯录单位
     *
     * @param uuids
     */
    void deleteContactBookUnit(List<String> uuids);

}
