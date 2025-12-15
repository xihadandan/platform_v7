package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.entity.ApiInvokeLogEntity;
import com.wellsoft.pt.api.facade.ApiLogFacadeService;
import com.wellsoft.pt.api.service.ApiInvokeLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年09月23日   chenq	 Create
 * </pre>
 */
@Service
public class ApiLogFacadeServiceImpl implements ApiLogFacadeService {
    @Autowired
    ApiInvokeLogService apiInvokeLogService;

    @Override
    public void saveApiInvokeLog(ApiInvokeLogEntity log) {
        if (log.getLatency() == null && log.getReqTime() != null && log.getResTime() != null) {
            log.setLatency((int) (log.getResTime().getTime() - log.getReqTime().getTime()));
        }
        if (StringUtils.isNotBlank(log.getReqMethod())) {
            log.setReqMethod(log.getReqMethod().toUpperCase());
        }
        apiInvokeLogService.save(log);
    }


    public static void main(String[] args) {
        ApiInvokeLogEntity log = new ApiInvokeLogEntity();
        log.setInvokeUrl("http:/111.111.111.111/testInvoke"); // 调用接口地址，如果是 soap 协议，则是 wsdl 地址，如: http://xxx/service?wsdl
        log.setPath("getGoods"); // 仅 soap 协议调用时候设置，代表调用的方法
        log.setProtocol("REST"); // REST / SOAP
        log.setReqBody(""); // 请求体： json 序列化
        log.setResBody(""); // 返回内容: json 序列化
        log.setReqMethod("GET"); // http 方法头: GET \ POST \ DELETE 等

    }
}
