/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.facade;

import com.wellsoft.pt.integration.request.*;
import com.wellsoft.pt.integration.response.*;

import javax.jws.WebService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-8.1	zhulh		2013-11-8		Create
 * </pre>
 * @date 2013-11-8
 */
@WebService
public interface DataExchangeWebService {

    // a、源系统调用数据交换平台上传接口
    SendResponse send(SendRequest request);

    // d、目标系统在接收执行后回调数据交换平台分发结果回调接口
    PlatformSendCallbackResponse platformSendCallback(PlatformSendCallbackRequest request);

    // f、目标系统在确认接收/拒收后调用数据交换平台回复消息接口
    ReplyResponse replyMsg(ReplyRequest request);

    // h、源系统撤销调用数据交换平台撤销接口
    CancelResponse cancel(CancelRequest request);

    // j、目标系统在撤销后回调数据交换平台撤销回调接口
    PlatformCancelCallbackResponse platformCancelCallback(PlatformCancelCallbackRequest request);

    // 1、源系统调用数据交换平台上传接口(1.1版本)
    DXResponse dxSend(DXRequest request);

    // 2、目标系统在接收执行后回调数据交换平台分发结果回调接口(1.1版本)
    DXResponse dxCallback(DXCallbackRequest request);

    DXResponse dxCancel(DXCancelRequest dXCancelRequest);

    ShareResponse query(ShareRequest shareRequest);

    Boolean historyDataRequest(String zch, String unitId);

    //中央对接桥接同步接口
    Boolean BridgeSyn(String entityName, String unitId, String str);

    Response SendSerializeData(String typeId, String unitId, String serializeData, String para,
                               FilesRequest streamingDatas);

    String getTest(String getData);

    //标识项目已经获取过
    Boolean xmflag(String xmbh, String unitId);
}
