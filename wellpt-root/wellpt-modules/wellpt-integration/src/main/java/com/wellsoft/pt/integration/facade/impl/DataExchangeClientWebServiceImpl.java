/*
 * @(#)2013-11-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.facade.impl;

import com.wellsoft.pt.integration.facade.DataExchangeClientWebService;
import com.wellsoft.pt.integration.request.*;
import com.wellsoft.pt.integration.response.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-14.1	zhulh		2013-11-14		Create
 * </pre>
 * @date 2013-11-14
 */
@WebService
@SOAPBinding(style = Style.RPC)
public class DataExchangeClientWebServiceImpl implements DataExchangeClientWebService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeClientWebService#sendCallback(com.wellsoft.pt.integration.request.ClientSendCallbackRequest)
     */
    @Override
    public ClientSendCallbackResponse clientSendCallback(ClientSendCallbackRequest message) {
        System.out.println("clientSendCallback");
        ClientSendCallbackResponse clientSendCallbackResponse = new ClientSendCallbackResponse();
        clientSendCallbackResponse.setCode(1);
        clientSendCallbackResponse.setMsg("Success");
        return clientSendCallbackResponse;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeClientWebService#receive(com.wellsoft.pt.integration.request.SendRequest)
     */
    @Override
    public ReceiveResponse receive(ReceiveRequest request) {
        System.out.println("receive");
        ReceiveResponse receiveResponse = new ReceiveResponse();
        receiveResponse.setMsg("Success");
        receiveResponse.setBatchId("");
        receiveResponse.setCode(1);
        return receiveResponse;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeClientWebService#routeCallback(com.wellsoft.pt.integration.request.RouteCallbackRequest)
     */
    @Override
    public RouteCallbackResponse routeCallback(RouteCallbackRequest message) {
        System.out.println("routeCallback");
        RouteCallbackResponse routeCallbackResponse = new RouteCallbackResponse();
        routeCallbackResponse.setMsg("Success");
        routeCallbackResponse.setCode(1);
        return routeCallbackResponse;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeClientWebService#reply(com.wellsoft.pt.integration.request.ReplyRequest)
     */
    @Override
    public ReplyResponse replyMsg(ReplyRequest message) {
        System.out.println("replyMsg");
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setMsg("Success");
        replyResponse.setCode(1);
        return replyResponse;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeClientWebService#cancel(com.wellsoft.pt.integration.request.CancelRequest)
     */
    @Override
    public CancelResponse cancel(CancelRequest request) {
        return new CancelResponse();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeClientWebService#clientCancelCallback(com.wellsoft.pt.integration.request.ClientCancelCallbackRequest)
     */
    @Override
    public ClientCancelCallbackResponse clientCancelCallback(ClientCancelCallbackRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeClientWebService#sendCallback(com.wellsoft.pt.integration.request.ClientSendCallbackRequest)
     */
    @Override
    public DXResponse dxCallback(DXCallbackRequest message) {
        System.out.println("********************************************************************");
        System.out.println("dxCallback");
        DXResponse clientSendCallbackResponse = new DXResponse();
        clientSendCallbackResponse.setCode(1);
        clientSendCallbackResponse.setMsg("Success");
        return clientSendCallbackResponse;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeClientWebService#receive(com.wellsoft.pt.integration.request.SendRequest)
     */
    @Override
    public DXResponse dxSend(DXRequest request) {
        System.out.println("********************************************************************");
        System.out.println("dxSend");
        DXResponse receiveResponse = new DXResponse();
        receiveResponse.setMsg("Success");
        receiveResponse.setCode(1);
        return receiveResponse;
    }

    @Override
    public ShareResponse query(ShareRequest shareRequest) {
        System.out.println("queryShareData");
        ShareResponse shareResponse = new ShareResponse();
        shareResponse.setCode(1);
        shareResponse.setMsg("Success");
        shareResponse.setTotalRow(1);
        List<String> strList = new ArrayList<String>();
        String path = "D:\\dxtest\\test_ztdj.xml";
        try {
            FileInputStream input = new FileInputStream(path);
            String temp = IOUtils.toString(input);
            strList.add(temp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info(e.getMessage());
        }
        shareResponse.setRecords(strList);
        // 请求表单数据，以XML文本形式传输
        return shareResponse;
    }

    @Override
    public DXResponse dxCancel(DXCancelRequest dXCancelRequest) {
        System.out.println("********************************************************************");
        System.out.println("dxCancel");
        DXResponse receiveResponse = new DXResponse();
        receiveResponse.setMsg("Success");
        receiveResponse.setCode(1);
        return receiveResponse;
    }

    @Override
    public Boolean historyDataRequest(String zch, String unitId) {
        System.out.println("historyDataRequest");
        return true;
    }

    @Override
    public Boolean BridgeSyn(String entityName, String unitId, String str) {
        // TODO Auto-generated method stub
        System.out.println("BridgeSyn");
        return true;
    }

    @Override
    public String getTest(String sendData) {
        // TODO Auto-generated method stub
        System.out.println("sendTest:" + sendData);
        return "sendTest:" + sendData;
    }

    @Override
    public Response SendSerializeData(String typeId, String unitId, String serializeData, String para,
                                      FilesRequest streamingDatas) {
        // TODO Auto-generated method stub
        Response response = new Response();
        System.out.println("SendSerializeData");
        response.setCode(1);
        response.setMsg("SendSerializeData");
        return response;
    }
}
