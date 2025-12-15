package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.dao.impl.DmsDocExchangeReceiveDetailDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
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
public interface DmsDocExchangeRecordDetailService extends
        JpaService<DmsDocExchangeRecordDetailEntity, DmsDocExchangeReceiveDetailDaoImpl, String> {

    /**
     * 查询文档交换记录的接收者详情
     *
     * @param uuid
     * @return
     */
    List<DmsDocExchangeRecordDetailEntity> listByDocExchangeRecordUuid(String uuid);

    void deleteByDocExchangeRecordUuid(String uuid);

    /**
     * 保存文档交换记录的接收者详情
     *
     * @param recordEntity
     * @param allUserIdSet
     * @param type          0 正常发送 1 补充发送 2 转发
     * @param extraSendUuid 补充发送的UUID
     */
    List<DmsDocExchangeRecordDetailEntity> saveRecordDetails(
            DmsDocExchangeRecordEntity recordEntity, Set<String> allUserIdSet,
            Integer type, String extraSendUuid);

    /**
     * 撤回操作
     *
     * @param actionData
     */
    void revokeReceiveDetail(DocExchangeActionData actionData);

    /**
     * 查询文档交换记录的所有接收用户ID
     *
     * @param docExcRecordUuid
     * @return
     */
    List<String> listAllReceiverIdsByDocExcRecordUuid(String docExcRecordUuid);

    /**
     * 催办
     *
     * @param actionData
     */
    void urgeReceiveDetail(DocExchangeActionData actionData);

}
