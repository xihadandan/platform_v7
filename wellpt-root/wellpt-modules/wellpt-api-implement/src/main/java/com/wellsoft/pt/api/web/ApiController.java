package com.wellsoft.pt.api.web;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.api.WellptApi;
import com.wellsoft.pt.api.annotation.ApiComponent;
import com.wellsoft.pt.api.entity.ApiOutSystemConfigEntity;
import com.wellsoft.pt.api.exception.ApiArgumentErrorException;
import com.wellsoft.pt.api.exception.ApiBusinessException;
import com.wellsoft.pt.api.exception.ApiInterceptException;
import com.wellsoft.pt.api.facade.ApiOutSystemFacadeService;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.api.service.ApiOutSysConfigService;
import com.wellsoft.pt.api.support.ApiContextHolder;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Description:对外api统一接口服务
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
//@Controller
//@RequestMapping("/api")
@Deprecated
public class ApiController extends BaseController implements InitializingBean {


    private final Map<String, WellptApi> apiMap = Maps.newHashMap();
    @Autowired
    MongoFileService mongoFileService;
    @Autowired
    private List<WellptApi> wellptApiList;

    /**
     * api统一请求入口
     *
     * @param apiClass
     * @param invokeMethod
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws Exception
     */
    @Description("api统一请求web服务")
    @RequestMapping(value = "/{apiClass}/{invokeMethod}", method = RequestMethod.POST)
    public void handleRequest(@PathVariable String apiClass,
                              @PathVariable String invokeMethod,
                              HttpServletRequest httpServletRequest,
                              HttpServletResponse httpServletResponse) throws Exception {
        logger.info("apiClass={},invokeMethod={} ",
                new String[]{apiClass, invokeMethod});

        try {
            WellptApi wellptApi = apiMap.get(apiClass);
            if (wellptApi == null) {
                outResponse(httpServletResponse, ApiResponse.build().code(
                        ApiCodeEnum.API_NOT_FOUND));

                return;
            }
            ApiResponse response = wellptApi.handleRequest(httpServletRequest, httpServletResponse,
                    invokeMethod);
            outResponse(httpServletResponse, response);
        } catch (ApiArgumentErrorException ape) {
            logger.error("请求api参数异常：", ape);
            outResponse(httpServletResponse, ApiResponse.build().code(ApiCodeEnum.ARGUMENTS_ERROR));
        } catch (ApiBusinessException abe) {
            logger.error("请求api业务处理异常：", abe);
            outResponse(httpServletResponse,
                    ApiResponse.build().code(abe.getErrorCode()).msg(abe.getErrorMsg()));
        } catch (ApiInterceptException interceptException) {
            logger.error("请求api拦截异常：", interceptException);
            outResponse(httpServletResponse,
                    ApiResponse.build().code(interceptException.getErrorCode()).msg(
                            interceptException.getErrorMsg()));
        } catch (Exception e) {
            logger.error("请求api异常：", e);
            outResponse(httpServletResponse,
                    ApiResponse.build().code(ApiCodeEnum.API_SYSTEM_ERROR));
        } finally {
            ApiContextHolder.getContext().remove();
        }
    }


    private void outResponse(HttpServletResponse httpServletResponse,
                             ApiResponse response) throws Exception {

        PrintWriter pw = httpServletResponse.getWriter();
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpStatus.ACCEPTED.value());
        pw.write(new Gson().toJson(response));
    }

    /**
     * 获取文件流api
     *
     * @param fileId
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws Exception
     */
    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public void handleFileRequest(@RequestParam String fileId,
                                  HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse) throws Exception {
        try {
            MongoFileEntity fileEntity = mongoFileService.getFile(fileId);
            if (fileEntity == null) {//文件不存在
                outResponse(httpServletResponse,
                        ApiResponse.build().code(ApiCodeEnum.API_RESOURCE_NOT_EXIST));
                return;
            }

            FileDownloadUtils.download(httpServletRequest, httpServletResponse,
                    fileEntity.getInputstream(), fileEntity.getFileName(),
                    fileEntity.getContentType());
        } catch (Exception e) {
            logger.error("api下载文件流异常：", e);
            outResponse(httpServletResponse,
                    ApiResponse.build().code(ApiCodeEnum.API_SYSTEM_ERROR));
        }

    }

    /**
     * 刷新token
     *
     * @param systemCode
     * @return
     */
    @RequestMapping(value = "/token/refresh/{systemCode}", method = RequestMethod.GET)
    public @ResponseBody
    ApiResponse refreshToken(@PathVariable String systemCode) {
        try {
            ApiOutSystemConfigEntity configEntity = ApplicationContextHolder.getBean(
                    ApiOutSysConfigService.class).getBySystemCode(systemCode);
            if (configEntity == null) {
                return ApiResponse.build().code(ApiCodeEnum.REFRESH_TOKEN_ERROR_NOT_EXIST_SYS);
            }
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT,
                    ApplicationContextHolder.getBean(OrgApiFacade.class).getUnitAdmin(
                            configEntity.getSystemUnitId()).getId());
            String token = ApplicationContextHolder.getBean(
                    ApiOutSystemFacadeService.class).generateToken(
                    configEntity.getUuid());
            IgnoreLoginUtils.logout();
            return ApiResponse.build().data(token);
        } catch (Exception e) {
            return ApiResponse.build().code(ApiCodeEnum.API_SYSTEM_ERROR);
        }
    }


    /**
     * 加载所有api组件服务
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (apiMap.isEmpty()) {
            for (WellptApi wellptApi : wellptApiList) {
                Type apiClass = wellptApi.getClass().getGenericSuperclass();
                String name = AnnotationUtils.getValue(
                        ((Class) apiClass).getAnnotation(ApiComponent.class),
                        "name").toString();
                apiMap.put(name, wellptApi);
            }
        }


    }
}
