package com.wellsoft.pt.di.util;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.request.RequestWraper;
import com.wellsoft.pt.di.response.Response;
import com.wellsoft.pt.di.service.DiConfigService;
import org.apache.camel.ExchangePattern;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 数据交换调用工具类
 *
 * @author chenq
 * @date 2019/8/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/14    chenq		2019/8/14		Create
 * </pre>
 */
public class DIUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DIUtils.class);

    private static void requestValidate(RequestWraper requestWraper) {
        if (StringUtils.isBlank(requestWraper.getId())) {
            throw new RuntimeException("参数[id]必填，指定调用的数据交换规则");
        }
    }

    private static void exchangeRouteValidate(String routId) {
        if (!CamelContextUtils.existRoute(routId)) {
            throw new RuntimeException("数据交换路由不存在!");
        }

        if (!CamelContextUtils.isStarting(routId)) {
            throw new RuntimeException("路由未启用运行！");
        }

    }

    public static Response execute(RequestWraper requestWraper) {
        Response response = new Response();
        requestValidate(requestWraper);
        DiConfigService diConfigService = ApplicationContextHolder.getBean(DiConfigService.class);
        DiConfigEntity configEntity = diConfigService.getById(requestWraper.getId());
        if (configEntity == null) {
            response.setCode(-1);
            return response;
        }
        exchangeRouteValidate(configEntity.getUuid());
        try {
            response.setResponseBody(
                    CamelContextUtils.producer().sendBodyAndHeaders(
                            "direct:" + configEntity.getUuid(), ExchangePattern.InOut,
                            requestWraper.getRequest(), null));
        } catch (Exception e) {
            response.setCode(-1);
            response.setMsg(e.getMessage());
        }
        return response;
    }
}
