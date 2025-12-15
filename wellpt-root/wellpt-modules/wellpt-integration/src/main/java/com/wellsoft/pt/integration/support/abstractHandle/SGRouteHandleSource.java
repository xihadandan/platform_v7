/*
 * @(#)2013-6-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support.abstractHandle;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.pt.integration.provider.BusinessHandleSource;
import com.wellsoft.pt.integration.request.DXCallbackRequest;
import com.wellsoft.pt.integration.request.DXRequest;
import com.wellsoft.pt.integration.request.ShareRequest;
import com.wellsoft.pt.integration.response.ShareResponse;
import com.wellsoft.pt.integration.support.DXExchangeSupport;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 批次信息
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-28.1	Administrator		2013-11-28		Create
 * </pre>
 * @date 2013-11-28
 */
@Component
public class SGRouteHandleSource implements BusinessHandleSource {

    @Override
    public Map<String, String> exchangeDataRequest(DXExchangeSupport dXExchangeSupport) {
        // TODO Auto-generated method stub
        System.out.println("********************************************************************");
        System.out.println(dXExchangeSupport.getBatchId() + "，执行之定义模块返回事件exchangeDataRequest");
        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        return map;
    }

    @Override
    public Boolean exchangeDataResponse(DXExchangeSupport dXExchangeSupport) {
        // TODO Auto-generated method stub
        System.out.println("********************************************************************");
        System.out.println(dXExchangeSupport.getBatchId() + "，执行之定义模块返回事件exchangeDataResponse");
        return true;
    }

    @Override
    public Boolean exchangeDataCallBack(DXCallbackRequest dXCallbackRequest) {
        // TODO Auto-generated method stub
        System.out.println("********************************************************************");
        System.out.println(dXCallbackRequest.getBatchId() + "，执行之定义模块返回事件exchangeDataCallBack");
        return true;
    }

    @Override
    public Map<String, String> verificationData(DXExchangeSupport dXExchangeSupport) {
        // TODO Auto-generated method stub
        Map<String, String> map = new HashMap<String, String>();
        map.put("dataId", "不满足条件的原因");
        System.out.println("********************************************************************");
        System.out.println(dXExchangeSupport.getBatchId() + "，自定义模块验证通过verificationData");
        return null;
    }

    @Override
    public ShareResponse queryShareData(ShareRequest shareRequest) {
        // TODO Auto-generated method stub
        System.out.println("********************************************************************");
        System.out.println("执行之定义模块返回事件queryShareData");
        return null;
    }

    @Override
    public String getBusinessId() {
        // TODO Auto-generated method stub
        return ModuleID.DATA_EXCHANGE.getValue();
    }

    @Override
    public String getBusinessName() {
        // TODO Auto-generated method stub
        return "测试模块验证";
    }

    @Override
    public Map<Integer, String> correctnessData(String xml, DXRequest dxRequest) {
        Map<Integer, String> responseMap = new HashMap<Integer, String>();
        responseMap.put(0, "成功");
        return responseMap;
    }

    @Override
    public void repestCall(String batchId, String unitId, String status) {
        // TODO Auto-generated method stub

    }

}
