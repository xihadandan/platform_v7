/*
 * @(#)2013-4-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.provider;

import com.wellsoft.pt.integration.request.DXCallbackRequest;
import com.wellsoft.pt.integration.request.DXRequest;
import com.wellsoft.pt.integration.request.ShareRequest;
import com.wellsoft.pt.integration.response.ShareResponse;
import com.wellsoft.pt.integration.support.DXExchangeSupport;

import java.util.Map;

/**
 * Description: 数据交换触发模块
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-30.1	ruanhg		2014-4-30		Create
 * </pre>
 * @date 2014-4-30
 */
public interface BusinessHandleSource {

    /**
     * 用于进入业务实现前，验证数据是否满足业务需求
     * 0-成功
     * 1-xml格式错误
     * 2-业务数据不满足
     * 3-系统异常
     * 4-数据重复发送
     *
     * @param xml
     */
    public Map<Integer, String> correctnessData(String xml, DXRequest dxRequest);

    /**
     * 数据交换请求需要实现的业务
     *
     * @param batchId
     * @return
     */
    public Map<String, String> exchangeDataRequest(DXExchangeSupport dXExchangeSupport);

    /**
     * 数据交换返回需要实现的业务
     *
     * @param batchId
     * @return
     */
    public Boolean exchangeDataResponse(DXExchangeSupport dXExchangeSupport);

    /**
     * 数据交换回调需要实现的业务
     *
     * @param dXCallbackRequest
     * @return
     */
    public Boolean exchangeDataCallBack(DXCallbackRequest dXCallbackRequest);

    /**
     * 验证数据
     *
     * @param batchId
     * @return key:dataId
     * value:验证不通过的原因，成功时返回success
     */
    public Map<String, String> verificationData(DXExchangeSupport dXExchangeSupport);

    /**
     * 查询数据
     *
     * @param typeId
     * @param params
     * @param unitId
     * @return
     */
    public ShareResponse queryShareData(ShareRequest shareRequest);

    /**
     * 获取模块ID
     *
     * @return
     */
    public String getBusinessId();

    /**
     * 获取模块的名字
     *
     * @return
     */
    public String getBusinessName();

    /**
     * @param batchId 源批次号
     * @param unitId
     * @param status
     */
    public void repestCall(String batchId, String unitId, String status);
}
