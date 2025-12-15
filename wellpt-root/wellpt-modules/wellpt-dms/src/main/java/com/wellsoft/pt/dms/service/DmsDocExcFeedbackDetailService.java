package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.impl.DmsDocExcFeedbackDetailDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcFeedbackDetailEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 反馈详情数据服务
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
public interface DmsDocExcFeedbackDetailService extends JpaService<DmsDocExcFeedbackDetailEntity, DmsDocExcFeedbackDetailDaoImpl, String> {

    /**
     * 查看文档交换记录下的所有接收者的反馈信息
     *
     * @param recordUuid
     * @return
     */
    List<DmsDocExcFeedbackDetailEntity> listFeedbackDetailsByDocExcRecordUuid(String recordUuid);

    /**
     * 获取回执指定反馈详情的回执详情
     *
     * @param feedbackDetailUuid
     * @return
     */
    List<DmsDocExcFeedbackDetailEntity> listByToFeedbackDetailUuid(String feedbackDetailUuid);
}
