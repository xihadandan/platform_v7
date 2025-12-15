package com.wellsoft.pt.api.api;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.api.AbstractWellptApi;
import com.wellsoft.pt.api.annotation.ApiComponent;
import com.wellsoft.pt.api.annotation.AsyncMethod;
import com.wellsoft.pt.api.request.AsyncRequestWrapper;
import com.wellsoft.pt.api.request.DemoApiRequest;
import com.wellsoft.pt.api.request.DemoSoapApiRequest;
import com.wellsoft.pt.api.request.SoapApiRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.api.support.ApiCall;
import com.wellsoft.pt.api.support.ApiContextHolder;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.axiom.om.OMElement;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Description: 样例api
 *
 * @author chenq
 * @date 2018/8/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/8    chenq		2018/8/8		Create
 * </pre>
 */
@ApiComponent(name = "demo")
public class DemoApi extends AbstractWellptApi {


    public ApiResponse getSomething(DemoApiRequest request) {

        LOGGER.info("getSomething invoke...........");
        //测试
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, "adm_pt");
            ApiResponse apiResponse = ApiCall.build().requestBody(request).idempotentKey(
                    new Date().getTime() + "").service(
                    "test_inner", "test_ml").businessCorrela("dddddd112", "测试业务类型").call();
            LOGGER.info("调用api测试，反馈：{}", new Gson().toJson(apiResponse));
            return apiResponse;
        } catch (Exception e) {
            LOGGER.error("测试调用api异常：", e);
        } finally {
            IgnoreLoginUtils.logout();
        }
        return new ApiResponse().msg("调用getSomething成功");
    }

    public ApiResponse getSomethingFile(Map<String, String[]> getParameters,
                                        Map<String, MultipartFile> fileMap) {
        LOGGER.info("getSomething invoke...........");
        if (MapUtils.isNotEmpty(fileMap)) {
            Iterator<MultipartFile> fileIterator = fileMap.values().iterator();
            while (fileIterator.hasNext()) {
                MultipartFile file = fileIterator.next();
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(
                            new File("D:\\下载区\\" + file.getOriginalFilename()));
                    IOUtils.copy(file.getInputStream(), fileOutputStream);
                } catch (Exception e) {
                    LOGGER.error("文件流读取异常：", e);
                }


            }
        }

        return new ApiResponse();
    }


    @AsyncMethod
    public ApiResponse getSomeMark(DemoApiRequest request) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("requestId", ApiContextHolder.getCommandUuid());
        return ApiResponse.build().msg("异步请求接收成功").data(data);
    }

    public void getSomeMarkAsync(AsyncRequestWrapper<DemoApiRequest> requestWrapper) {


        //测试
        try {
            Thread.sleep(4000L);
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, "adm_pt");
            DemoSoapApiRequest soapApiRequest = new DemoSoapApiRequest(
                    new SoapApiRequest.Operation("http://service.cxf.example.com/",
                            "getCustomer"));
            soapApiRequest.setName("soapApiRequest 请求");

            ApiResponse apiResponse = ApiCall.build().requestBody(soapApiRequest).idempotentKey(
                    new Date().getTime() + "").service(
                    "test_inner", "test_m2").businessCorrela("dddddd3333", "测试业务类型").call();
            OMElement element = (OMElement) apiResponse.getData();
            LOGGER.info("调用api测试，反馈：{}", element.getText());

        } catch (Exception e) {
            LOGGER.error("测试调用api异常：", e);
        } finally {
            IgnoreLoginUtils.logout();
        }


        LOGGER.info("############# 异步测试成功 ###############");
    }

}
