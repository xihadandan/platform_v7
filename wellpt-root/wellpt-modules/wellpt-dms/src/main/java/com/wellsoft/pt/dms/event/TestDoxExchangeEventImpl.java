package com.wellsoft.pt.dms.event;

import com.wellsoft.pt.dms.dto.DmsDocExchangeRelatedDocDto;
import com.wellsoft.pt.dms.entity.DmsDocExcFeedbackDetailEntity;
import com.wellsoft.pt.dms.entity.DmsDocExchangeRecordEntity;
import com.wellsoft.pt.dms.facade.service.DmsDocExchangeRelatedDocFacadeService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: yt
 * @Date: 2021/7/15 15:22
 * @Description:
 */
@Service
public class TestDoxExchangeEventImpl extends BaseServiceImpl implements DocExchangeEvent {

    private static final long serialVersionUID = 4195042830802483026L;

    @Autowired
    private DmsDocExchangeRelatedDocFacadeService relatedDocFacadeService;

    @Override
    public String getId() {
        return "pt-test";
    }

    @Override
    public String getName() {
        return "平台测试事件";
    }

    @Override
    public void signEvent(DmsDocExchangeRecordEntity record) {
        logger.error("平台测试事件：签收事件：" + record.getDocTitle());
        DmsDocExchangeRelatedDocDto relatedDocDto = new DmsDocExchangeRelatedDocDto();
        relatedDocDto.setDocExchangeRecordUuid(record.getUuid());
        relatedDocDto.setFromRecordDetailUuid(record.getFromRecordDetailUuid());
        relatedDocDto.setProcessingMethod("签收测试");
        relatedDocDto.setDocTitle("签收测试文档标题");
        relatedDocDto.setDocLink("http://wwww.baidu.com");
        relatedDocFacadeService.addRelatedDoc(relatedDocDto);
    }

    @Override
    public void feedbackEvent(DmsDocExchangeRecordEntity record, DmsDocExcFeedbackDetailEntity feedbackDetail) {
        logger.error("平台测试事件：反馈事件" + record.getDocTitle() + ":" + feedbackDetail.getContent());
        DmsDocExchangeRelatedDocDto relatedDocDto = new DmsDocExchangeRelatedDocDto();
        relatedDocDto.setDocExchangeRecordUuid(record.getUuid());
        relatedDocDto.setFromRecordDetailUuid(record.getFromRecordDetailUuid());
        relatedDocDto.setProcessingMethod("反馈测试");
        relatedDocDto.setDocTitle("反馈测试文档标题");
        relatedDocDto.setDocLink("http://wwww.baidu.com");
        relatedDocFacadeService.addRelatedDoc(relatedDocDto);
    }
}
