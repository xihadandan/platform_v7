package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExchangeForwardEntity;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/21    chenq		2018/5/21		Create
 * </pre>
 */
@Repository
public class DmsDocExchangeForwardDaoImpl extends
        AbstractJpaDaoImpl<DmsDocExchangeForwardEntity, String> {


    public List<DmsDocExchangeForwardEntity> listForwardByRecordUuidAndStatus(String uuid,
                                                                              DocExchangeRecordStatusEnum status) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("dcoExcRecordUuid", uuid);
        param.put("status", status);
        List<DmsDocExchangeForwardEntity> forwardEntities = listByHQL(
                "from DmsDocExchangeForwardEntity where docExchangeRecordUuid=:dcoExcRecordUuid and forwardStatus=:status order by createTime asc",
                param);
        return forwardEntities;
    }
}
