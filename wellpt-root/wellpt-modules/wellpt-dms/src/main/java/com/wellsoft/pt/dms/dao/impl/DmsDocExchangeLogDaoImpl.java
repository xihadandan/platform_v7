package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExchangeLogEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description:
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
@Repository
public class DmsDocExchangeLogDaoImpl extends AbstractJpaDaoImpl<DmsDocExchangeLogEntity, String> {

    public List<DmsDocExchangeLogEntity> listLogByRecordUuid(String docExchangeRecordUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("ruuid", docExchangeRecordUuid);
        return this.listByHQL("from DmsDocExchangeLogEntity where docExchangeRecordUuid=:ruuid order by createTime desc", param);
    }
}
