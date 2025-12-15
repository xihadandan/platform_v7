package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExcUrgeDetailEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 催办详情Dao服务
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
public class DmsDocExcUrgeDetailDaoImpl extends AbstractJpaDaoImpl<DmsDocExcUrgeDetailEntity, String> {


    public List<DmsDocExcUrgeDetailEntity> listDocExcUrgeDetail(String docExchageRecordUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("recordUuid", docExchageRecordUuid);
        return this.listByHQL("from DmsDocExcUrgeDetailEntity where docExchangeRecordUuid=:recordUuid order by createTime desc", param);
    }
}
