package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExcExtraSendEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description:文档交换的补充发送详情DAO
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
public class DmsDocExcExtraSendDaoImpl extends
        AbstractJpaDaoImpl<DmsDocExcExtraSendEntity, String> {

    public List<DmsDocExcExtraSendEntity> listDocExcExtraSendDetailByRecordUuid(
            String docExchageRecordUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("recordUuid", docExchageRecordUuid);
        return this.listByHQL(
                "from DmsDocExcExtraSendEntity where docExchangeRecordUuid=:recordUuid order by createTime asc",
                param);
    }
}
