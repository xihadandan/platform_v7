package com.wellsoft.pt.api.aspect;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.api.exception.ApiBusinessException;
import com.wellsoft.pt.api.exception.ApiInterceptException;
import com.wellsoft.pt.api.facade.ApiCommandFacadeService;
import com.wellsoft.pt.api.request.ApiAdapterRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.api.support.ApiContextHolder;
import com.wellsoft.pt.api.support.CallApiParams;
import com.wellsoft.pt.api.support.TokenClaims;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Description: api被调用
 *
 * @author chenq
 * @date 2018/8/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/10    chenq		2018/8/10		Create
 * </pre>
 */
@Aspect
@Component
public class ApiCallInAspect implements Ordered {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Before("execution( * com.wellsoft.pt.api.WellptApi.handleRequest(..))")
    public void before() {
        LOGGER.info("### 开始处理api访问服务 ###");
        ApiContextHolder.timer("IN_0");
        ApiAdapterRequest adapterRequest = new ApiAdapterRequest();
        adapterRequest.setApiMode(ApiAdapterRequest.API_MODE_IN);
        adapterRequest.setParams(new CallApiParams());
        TokenClaims claims = ApiContextHolder.getTokenClaims();
        adapterRequest.getParams().setSystemCode(claims.getSystemCode());
        adapterRequest.getParams().setIdempotentKey(ApiContextHolder.getRequestIdempotentKey());
        try {
            OrgUserVo adminUser = ApplicationContextHolder.getBean(OrgApiFacade.class).getUnitAdmin(
                    claims.getUnit());
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, adminUser.getId());
            //保存指令
            String commandUuid = ApplicationContextHolder.getBean(
                    ApiCommandFacadeService.class).saveAdapterRequestCommand(adapterRequest);
            ApiContextHolder.getContext().get().put(ApiContextHolder.COMMAND_KEY, commandUuid);
            IgnoreLoginUtils.logout();
        } catch (Exception e) {
            LOGGER.error("api被访问前，保存指令异常：", e);
            if (Throwables.getRootCause(e).getMessage().indexOf("UQ_IDEMP_KEY") != -1) {
                //幂等值异常
                throw new ApiInterceptException(ApiCodeEnum.REQUEST_OVER_FREQUENCE);
            }
            throw new ApiBusinessException(ApiCodeEnum.API_SYSTEM_ERROR.getCode(),
                    ApiCodeEnum.API_SYSTEM_ERROR.getDescription());

        }


    }

    @AfterReturning(pointcut = "execution( * com.wellsoft.pt.api.WellptApi.handleRequest(..))  ",
            returning = "response", argNames = "response")
    public void afterReturn(ApiResponse response) {

        TokenClaims claims = ApiContextHolder.getTokenClaims();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        try {
            OrgUserVo adminUser = ApplicationContextHolder.getBean(OrgApiFacade.class).getUnitAdmin(
                    claims.getUnit());
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, adminUser.getId());
            ApiCallOutAspect.commandUpgrade(null, gson.toJson(response), response.isSuccess());
            IgnoreLoginUtils.logout();
        } catch (Exception e) {
            LOGGER.error("api被访问后，保存指令异常：", e);
        }

    }

    @After("execution( * com.wellsoft.pt.api.WellptApi.handleRequest(..))")
    public void after() {
        Stopwatch time = ApiContextHolder.timer("IN_0");

        LOGGER.info("### 结束处理api访问服务，耗时：{}###", time.stop());
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
