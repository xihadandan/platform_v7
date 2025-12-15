package com.wellsoft.pt.dms.event;

import com.wellsoft.pt.dms.entity.DmsDocExcFeedbackDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/7/15 14:51
 * @Description:
 */
public interface DocExchangeEvent extends Serializable {

    /**
     * Id
     *
     * @return
     */
    String getId();

    /**
     * 名称
     *
     * @return
     */
    String getName();


    /**
     * 签收事件
     *
     * @param record 交换记录
     */
    void signEvent(DmsDocExchangeRecordEntity record);

    /**
     * 反馈事件
     *
     * @param record         交换记录
     * @param feedbackDetail 反馈信息
     */
    void feedbackEvent(DmsDocExchangeRecordEntity record, DmsDocExcFeedbackDetailEntity feedbackDetail);


}
