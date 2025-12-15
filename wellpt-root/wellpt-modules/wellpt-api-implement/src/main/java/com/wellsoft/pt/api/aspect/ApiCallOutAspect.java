package com.wellsoft.pt.api.aspect;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.api.entity.ApiCommandDetailEntity;
import com.wellsoft.pt.api.entity.ApiCommandEntity;
import com.wellsoft.pt.api.enums.CommandStatusEnum;
import com.wellsoft.pt.api.facade.ApiCommandFacadeService;
import com.wellsoft.pt.api.request.ApiAdapterRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.api.service.ApiCommandDetailService;
import com.wellsoft.pt.api.service.ApiCommandService;
import com.wellsoft.pt.api.support.ApiContextHolder;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialClob;
import java.util.Date;

/**
 * Description: api调用外部
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
public class ApiCallOutAspect implements Ordered {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiCallOutAspect.class);

    /**
     * 更新指令报文
     *
     * @param requestBody
     * @param responseBody
     * @param success
     */
    public static void commandUpgrade(String requestBody, String responseBody, boolean success) {
        String commandUuid = (String) (ApiContextHolder.getContext().get().get(
                ApiContextHolder.COMMAND_KEY));
        if (StringUtils.isNotBlank(commandUuid)) {
            ApiCommandService apiCommandService = ApplicationContextHolder.getBean(
                    ApiCommandService.class);
            ApiCommandEntity commandEntity = apiCommandService.getOne(commandUuid);
            commandEntity.setStatus(success ? CommandStatusEnum.SUCCESS.getValue() : CommandStatusEnum.FAIL.getValue());
            if (StringUtils.isNotBlank(requestBody) || StringUtils.isNotBlank(responseBody)) {
                ApiCommandDetailService detailService = ApplicationContextHolder.getBean(
                        ApiCommandDetailService.class);
                ApiCommandDetailEntity detailEntity = detailService.getByCommandUuid(commandUuid);
                if (detailEntity == null) {
                    detailEntity = new ApiCommandDetailEntity();
                }
                detailEntity.setCommandUuid(commandUuid);
                try {
                    if (StringUtils.isNotBlank(requestBody)) {
                        detailEntity.setRequestBody(new SerialClob(requestBody.toCharArray()));
                    }

                    if (StringUtils.isNotBlank(responseBody)) {
                        detailEntity.setResponseBody(new SerialClob(responseBody.toCharArray()));
                    }
                } catch (Exception e) {
                    LOGGER.error("保存指令报文异常", e);
                }
                detailService.save(detailEntity);
            }
            commandEntity.setResponseTime(new Date());
            apiCommandService.save(commandEntity);

        }

    }

    @Before("execution( * com.wellsoft.pt.api.adapter.WellptApiAdapter.invoke(..)) && args(request) ")
    public void before(JoinPoint jp, ApiAdapterRequest request) {
        LOGGER.info("### 开始处理api适配器 ###");
        ApiContextHolder.timer("OUT_0");
        request.setApiMode(ApiAdapterRequest.API_MODE_OUT);
        //保存指令
        String commandUuid = ApplicationContextHolder.getBean(
                ApiCommandFacadeService.class).saveAdapterRequestCommand(request);
        ApiContextHolder.getContext().get().put(ApiContextHolder.COMMAND_KEY, commandUuid);
    }

    @AfterReturning(pointcut = "execution( * com.wellsoft.pt.api.adapter.WellptApiAdapter.invoke(..))  ",
            returning = "response", argNames = "response")
    public void afterReturn(ApiResponse response) {
        commandUpgrade(null, null, response.isSuccess());
        ApiContextHolder.getContext().remove();

    }

    @After("execution( * com.wellsoft.pt.api.adapter.WellptApiAdapter.invoke(..)) && args(request) ")
    public void after(JoinPoint jp, ApiAdapterRequest request) {
        Stopwatch time = ApiContextHolder.timer("OUT_0");
        commandUpgrade(request.getRealRequestBody(), request.getRealResponseBody(), true);
        LOGGER.info("### 接收处理api适配器，耗时：{} ###", time.stop());
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
