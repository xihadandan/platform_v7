/*
 * @(#)2013-11-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.facade;

import com.wellsoft.pt.integration.request.*;
import com.wellsoft.pt.integration.response.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

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
public interface DataExchangeClientWebService {

    // b、数据交换平台上传执行后回调源系统上传结果回调接口
    ClientSendCallbackResponse clientSendCallback(ClientSendCallbackRequest message);

    // c、目标系统接收数据交换平台数据接收接口
    ReceiveResponse receive(ReceiveRequest request);

    // e、数据交换平台路由结束后回调源系统路由结果回调接口
    RouteCallbackResponse routeCallback(RouteCallbackRequest message);

    // g、数据交换平台收到目标系统的接收通知后调用源系统回复消息告知接口
    ReplyResponse replyMsg(ReplyRequest message);

    // i、源系统撤销调用数据交换平台撤销接口
    CancelResponse cancel(CancelRequest request);

    // k、数据交换平台收到目标系统的撤销通知后调用源系统撤销接口
    ClientCancelCallbackResponse clientCancelCallback(ClientCancelCallbackRequest request);

    // 1、数据交换平台上传执行后回调源系统上传结果回调接口(1.1版本)
    DXResponse dxCallback(DXCallbackRequest message);

    // 2、目标系统接收数据交换平台数据接收接口(1.1版本)
    DXResponse dxSend(DXRequest request);

    DXResponse dxCancel(DXCancelRequest dXCancelRequest);

    ShareResponse query(ShareRequest shareRequest);

    Boolean historyDataRequest(String zch, String unitId);

    //中央对接桥接同步接口
    Boolean BridgeSyn(String entityName, String unitId, String str);

    Response SendSerializeData(String typeId, String unitId, String serializeData, String para,
                               FilesRequest streamingDatas);

    /**
     * 发送测试数据
     *
     * @param sendData 数据内容
     * @return
     */
    String getTest(String sendData);

}
