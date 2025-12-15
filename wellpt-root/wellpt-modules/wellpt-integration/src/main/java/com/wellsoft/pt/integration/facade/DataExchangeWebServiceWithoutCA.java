/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.facade;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Description: 无CA数据交换接口
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
public interface DataExchangeWebServiceWithoutCA {

    //源系统调用数据交换平台上传接口：
    @WebMethod
    String dxSend(@WebParam(name = "dXRequestXml") String dXRequestXml);

    //目标系统在接收执行后回调数据交换平台分发结果回调接口：
    @WebMethod
    String dxCallback(@WebParam(name = "dXCallbackRequestXml") String dXCallbackRequestXml);

    //源系统调用数据交换平台注销接口：
    @WebMethod
    String dxCancel(@WebParam(name = "dXCancelRequestXml") String dXCancelRequestXml);

    //共享查询接口:
    @WebMethod
    String query(@WebParam(name = "shareRequestXml") String shareRequestXml);

}
