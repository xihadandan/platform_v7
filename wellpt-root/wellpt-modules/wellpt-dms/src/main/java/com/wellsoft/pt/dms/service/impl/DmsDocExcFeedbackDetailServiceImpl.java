package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.impl.DmsDocExcFeedbackDetailDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcFeedbackDetailEntity;
import com.wellsoft.pt.dms.service.DmsDocExcFeedbackDetailService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 反馈详情服务
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
@Service
public class DmsDocExcFeedbackDetailServiceImpl extends AbstractJpaServiceImpl<DmsDocExcFeedbackDetailEntity, DmsDocExcFeedbackDetailDaoImpl, String> implements DmsDocExcFeedbackDetailService {


    @Override
    public List<DmsDocExcFeedbackDetailEntity> listFeedbackDetailsByDocExcRecordUuid(String recordUuid) {
        return this.dao.listFeedbackDetailsByDocExcRecordUuid(recordUuid);
    }

    @Override
    public List<DmsDocExcFeedbackDetailEntity> listByToFeedbackDetailUuid(
            String feedbackDetailUuid) {
        return this.dao.listByToFeedbackDetailUuid(feedbackDetailUuid);
    }
}
