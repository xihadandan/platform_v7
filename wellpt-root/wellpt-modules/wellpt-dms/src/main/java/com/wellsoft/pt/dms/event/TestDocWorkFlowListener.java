package com.wellsoft.pt.dms.event;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.impl.DirectionListenerAdapter;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.dms.facade.service.DocExchangerFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: yt
 * @Date: 2021/7/20 15:31
 * @Description:
 */
@Component
public class TestDocWorkFlowListener extends DirectionListenerAdapter {

    @Autowired
    private DocExchangerFacadeService docExchangerFacadeService;

    @Override
    public void transition(Event event) throws WorkFlowException {
        String value = event.getTaskOpinionValue();
        Boolean isAgree = false;
        if ("1".equals(value)) {//同意
            isAgree = true;
        }
        docExchangerFacadeService.processApprovalResult(event.getReservedText3(), isAgree);
    }
}
