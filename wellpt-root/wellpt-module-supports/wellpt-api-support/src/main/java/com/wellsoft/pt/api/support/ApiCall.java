package com.wellsoft.pt.api.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.api.adapter.WellptApiAdapter;
import com.wellsoft.pt.api.dto.ApiOutSysServiceConfigDto;
import com.wellsoft.pt.api.facade.ApiOutSystemFacadeService;
import com.wellsoft.pt.api.request.ApiAdapterRequest;
import com.wellsoft.pt.api.request.ApiRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 平台提供对接系统的api 调用工具
 *
 * @author chenq
 * @date 2018/11/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/9    chenq		2018/11/9		Create
 * </pre>
 */
public class ApiCall {


    public static Builder build() {
        return new Builder();
    }

    public static void main(String[] arrs) {

    }

    public static class Builder<T extends ApiRequest> {

        private String bizCorrelationKey;//业务关联值

        private String businessType;//业务类型描述

        private String idempotentKey;//幂等值

        private String systemCode; //外部系统编码

        private String serviceCode;//外部系统服务编码

        private boolean async = false;//异步调用

        private T request;//请求报文体

        protected Builder() {
        }

        /**
         * 请求外部服务的编码
         *
         * @param sysCode     系统编码
         * @param serviceCode 系统服务编码
         * @return
         */
        public Builder service(String sysCode, String serviceCode) {
            this.serviceCode = serviceCode;
            this.systemCode = sysCode;
            return this;
        }

        /**
         * 设置业务关联的参数
         *
         * @param bizCorrelationKey 业务关联值
         * @param businessType      业务类型
         * @return
         */
        public Builder businessCorrela(String bizCorrelationKey, String businessType) {
            this.bizCorrelationKey = bizCorrelationKey;
            this.businessType = businessType;
            return this;
        }

        /**
         * 设置幂等值，幂等值是能够全局确认唯一的值
         *
         * @param idempotentKey
         * @return
         */
        public Builder idempotentKey(String idempotentKey) {
            this.idempotentKey = idempotentKey;
            return this;
        }

        /**
         * 请求外部系统的参数体
         *
         * @param request
         * @return
         */
        public Builder requestBody(T request) {
            this.request = request;
            return this;
        }

        public Builder async(boolean async) {
            this.async = async;
            return this;
        }

        public ApiResponse call() throws Exception {
            //params参数校验
            if (StringUtils.isBlank(this.systemCode)) {
                throw new RuntimeException("系统编码参数systemCode为空");
            }
            if (StringUtils.isBlank(this.serviceCode)) {
                throw new RuntimeException("服务编码参数serviceCode为空");
            }
            if (StringUtils.isBlank(this.idempotentKey)) {
                throw new RuntimeException("幂等值参数为空");
            }


            //获取配置
            ApiOutSysServiceConfigDto serviceConfigDto = ApplicationContextHolder.getBean(
                    ApiOutSystemFacadeService.class).getServiceConfig(
                    this.systemCode, this.serviceCode);

            //调用适配器
            WellptApiAdapter adapter = null;
            try {
                adapter = (WellptApiAdapter) ApplicationContextHolder.getBean(
                        Class.forName(serviceConfigDto.getServiceAdapter()));
            } catch (Exception e) {
                throw new RuntimeException(
                        "无法解析的适配器[" + serviceConfigDto.getServiceAdapter() + "]");
            }

            ApiAdapterRequest adapterRequest = new ApiAdapterRequest();
            CallApiParams params = new CallApiParams(this.bizCorrelationKey, this.businessType,
                    this.idempotentKey, this.systemCode, this.serviceCode, this.request);
            adapterRequest.setParams(params);
            adapterRequest.setApiRequest(this.request);
            adapterRequest.setEndpoint(serviceConfigDto.getServiceUrl());
            adapterRequest.setConnectionLiveSeconds(
                    serviceConfigDto.getOvertimeLimit() != null ? serviceConfigDto.getOvertimeLimit().intValue() : 60);
            ApiResponse response = adapter.invoke(adapterRequest);
            return response;
        }

    }
}
