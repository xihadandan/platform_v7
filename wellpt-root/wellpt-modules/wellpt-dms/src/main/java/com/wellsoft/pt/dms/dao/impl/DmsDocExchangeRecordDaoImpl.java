package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
@Repository
public class DmsDocExchangeRecordDaoImpl extends
        AbstractJpaDaoImpl<DmsDocExchangeRecordEntity, String> {
    public DmsDocExchangeRecordEntity getByFromRecordDetailUuid(String uuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("detailUuid", uuid);
        return this.getOneByHQL(
                "from DmsDocExchangeRecordEntity where fromRecordDetailUuid=:detailUuid", param);

    }

    public void updateDocExchangeFinishedWhenDetailDone(String docExchangeRecordUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("uuid", docExchangeRecordUuid);
        this.updateByNamedSQL("updateDocExchangeFinishedWhenDetailDone", param);
    }

    public void updateDocExchangeSignFinishedWhenDetailDone(String docExchangeRecordUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("uuid", docExchangeRecordUuid);
        this.updateByNamedSQL("updateDocExchangeSignFinishedWhenDetailDone", param);
    }

    public List<String> listUuidsByStatus(List<Integer> statusList) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("status", statusList);
        return this.listCharSequenceByHQL(
                "select uuid from DmsDocExchangeRecordEntity where recordStatus in(:status) ", param);
    }
}
