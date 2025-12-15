/*
 * @(#)2013-11-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.facade;

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
public interface DataExchangeClientWebServiceWithoutCA {

    // 1、数据交换平台上传执行后回调源系统上传结果回调接口(1.1版本)
    String dxCallback(String dXCallbackRequestXml);

    // 2、目标系统接收数据交换平台数据接收接口(1.1版本)
    String dxSend(String dxRequestXml);

}
