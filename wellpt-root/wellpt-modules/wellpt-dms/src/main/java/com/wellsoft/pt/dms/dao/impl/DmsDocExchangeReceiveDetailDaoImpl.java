package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordDetailEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description:文档交换-接收人员详情服务
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
@Repository
public class DmsDocExchangeReceiveDetailDaoImpl extends
        AbstractJpaDaoImpl<DmsDocExchangeRecordDetailEntity, String> {

    private final String SQL_FROM_BY_DOC_EXCHANGE_RECORD_UUID = "from DmsDocExchangeRecordDetailEntity where docExchangeRecordUuid=:docRecordUuid";

    public List<DmsDocExchangeRecordDetailEntity> getByDocExchangeRecordUuid(String uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("docRecordUuid", uuid);
        return listByHQL(SQL_FROM_BY_DOC_EXCHANGE_RECORD_UUID + " order by toUserId asc", param);
    }

    public void deleteByDocExchangeRecordUuid(String uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("docRecordUuid", uuid);
        this.deleteByHQL("delete " + SQL_FROM_BY_DOC_EXCHANGE_RECORD_UUID, param);
    }

    public List<String> listAllReceiverIdsByDocExcRecordUuid(String docExcRecordUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("docRecordUuid", docExcRecordUuid);
        return this.listCharSequenceByHQL(
                "select distinct toUserId " + SQL_FROM_BY_DOC_EXCHANGE_RECORD_UUID, param);

    }

    public List<String> listUuidByDocExchangeRecordUuid(String uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("docRecordUuid", uuid);
        return this.listCharSequenceByHQL(
                "select uuid " + SQL_FROM_BY_DOC_EXCHANGE_RECORD_UUID, param);
    }
}
