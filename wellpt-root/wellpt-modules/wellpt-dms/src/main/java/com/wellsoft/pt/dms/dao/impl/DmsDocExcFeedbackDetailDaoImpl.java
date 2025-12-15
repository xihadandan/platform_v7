package com.wellsoft.pt.dms.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dms.entity.DmsDocExcFeedbackDetailEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 反馈详情DAO
 *
 * @author chenq
 * @date 2018/5/17
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/17    chenq		2018/5/17		Create
 * </pre>
 */
@Repository
public class DmsDocExcFeedbackDetailDaoImpl extends
        AbstractJpaDaoImpl<DmsDocExcFeedbackDetailEntity, String> {


    public List<DmsDocExcFeedbackDetailEntity> listFeedbackDetailsByDocExcRecordUuid(
            String recordUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("recordUuid", recordUuid);
        return this.listByHQL(
                "from DmsDocExcFeedbackDetailEntity where docExchangeRecordUuid=:recordUuid order by feedbackTime asc",
                param);

    }

    public List<DmsDocExcFeedbackDetailEntity> listByToFeedbackDetailUuid(
            String feedbackDetailUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("feedbackDetailUuid", feedbackDetailUuid);
        return this.listByHQL(
                "from DmsDocExcFeedbackDetailEntity where toFeedbackDetailUuid=:feedbackDetailUuid order by feedbackTime asc",
                param);

    }
}
