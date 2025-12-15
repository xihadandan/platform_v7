package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.entity.ExchangeDataLog;
import com.wellsoft.pt.integration.request.*;
import com.wellsoft.pt.integration.response.*;
import com.wellsoft.pt.integration.support.WebServiceMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据交换数据流向
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-17.1	ruanhg		2013-11-17		Create
 * </pre>
 * @date 2013-11-17
 */
public interface ExchangeDataFlowService {

    public SendResponse send(SendRequest request, Integer operateSource);

    public String getTest(String getData);

    public String sendTest();

    public PlatformSendCallbackResponse sendCallback(PlatformSendCallbackRequest message);

    public ReplyResponse reply(ReplyRequest message);

    public void addExchangeDataLog(ExchangeDataLog exchangeDataLog);

    public void callbackClientAndreceiveClient(WebServiceMessage webServiceMessage);

    public void RouteCallBackClient(WebServiceMessage webServiceMessage);

    public void replyClient(WebServiceMessage webServiceMessage);

    public String reapply(Integer rel, String sendMonitorUuid, String unitIds);

    public List<Map<String, Object>> cancelMsg(String sendMonitorUuid);

    public String reapeat(Integer rel, String uuid, String unitIds);

    public CancelResponse cancel(CancelRequest request);

    public void receiveClient(WebServiceMessage webServiceMessage) throws FileNotFoundException, IOException;

    public void cancelClient(WebServiceMessage webServiceMessage);

    public PlatformCancelCallbackResponse platformCancelCallback(PlatformCancelCallbackRequest request);

    public void exchangeDataRepeatTask();

    public void exchangeDataRepeatTaskSSDJ();

    public void exchangeDataRepeatTaskGSDJ();

    public void exchangeDataRepeatTaskYZYM();

    public void runExchangeDataRepeat(WebServiceMessage webServiceMessage);

    public String examineExchange(String dataUuid, Integer code, String failMsg);

    public String delExamineExchange(String dataUuid);

    public String markCertificateStatus(String[] receiveMonitorUuid);

    /****************数据交换***********************/
    public DXResponse dxSend(DXRequest request, String source);

    public DXResponse dxCallback(DXCallbackRequest message);

    public Map<String, String> dxSendAsynchronous(WebServiceMessage webServiceMessage, String source);

    public ShareResponse query(ShareRequest shareRequest, String toId);

    public ShareResponse queryByToId(ShareRequest shareRequest, String toId);

    public Boolean historyDataRequest(String zch, String unitId);

    /**
     * 将DxResponse对象转为xml格式
     *
     * @return
     */
    public String turnDXResponseToXml(DXResponse dxResponse);

    public DXCallbackRequest turnXmlToDXCallbackRequest(String dXCallbackRequestXml);

    public ShareRequest turnXmlToShareRequest(String shareRequestXml);

    /**
     * 将ShareResponse对象转为xml格式
     *
     * @param shareResponse
     * @return
     */
    String turnShareResponseToXml(ShareResponse shareResponse);
}
