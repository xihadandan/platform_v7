package com.wellsoft.pt.di.util;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ServiceStatus;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Description: camel上下文实例的操作
 *
 * @author chenq
 * @date 2018/11/22
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/22    chenq		2018/11/22		Create
 * </pre>
 */
public class CamelContextUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelContextUtils.class);


    /**
     * 获取camel上下文实例
     *
     * @return
     */
    public static CamelContext context() {
        return ApplicationContextHolder.getBean(CamelContext.class);
    }

    /**
     * 停止路由
     *
     * @param routeId
     */
    public static void stopRoute(String routeId) {
        try {
            if (existRoute(routeId))
                context().stopRoute(routeId);
        } catch (Exception e) {
            LOGGER.error("停止路由{}异常：", routeId, e);
        }
    }

    /**
     * 删除路由
     *
     * @param routeId
     */
    public static void removeRoute(String routeId) {
        try {
            if (existRoute(routeId)) {
                context().removeRouteDefinition(context().getRouteDefinition(routeId));
            }
        } catch (Exception e) {
            LOGGER.error("删除路由{}异常：", routeId, e);
        }
    }

    /**
     * 按组删除路由
     *
     * @param group
     */
    public static void removeRouteGroup(String group) {
        try {
            List<RouteDefinition> routeList = context().getRouteDefinitions();
            if (CollectionUtils.isNotEmpty(routeList)) {
                for (RouteDefinition route : routeList) {
                    if (group.equals(route.getGroup())) {
                        context().removeRoute(route.getId());
                        context().removeRouteDefinition(route);
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    /**
     * 判断是否存在路由
     *
     * @param routeId
     * @return
     */
    public static boolean existRoute(String routeId) {
        return context().getRouteDefinition(routeId) != null;
    }


    /**
     * 查询路由状态
     *
     * @param routeId
     * @return
     */
    public static ServiceStatus getRouteStatus(String routeId) {
        try {
            if (existRoute(routeId))
                return context().getRouteStatus(routeId);
        } catch (Exception e) {
            LOGGER.error("查询路由{}状态异常：", routeId, e);
        }
        return null;
    }

    /**
     * 启动路由
     *
     * @param routeId
     */
    public static void startRoute(String routeId) {
        try {
            if (existRoute(routeId))
                context().startRoute(routeId);

        } catch (Exception e) {
            LOGGER.error("启动路由{}异常：", routeId, e);
        }
    }

    /**
     * 挂起路由
     *
     * @param routeId
     */
    public static void suspendRoute(String routeId) {
        try {
            if (existRoute(routeId))
                context().suspendRoute(routeId);
        } catch (Exception e) {
            LOGGER.error("挂起路由{}异常：", routeId, e);
        }
    }


    /**
     * 挂起路由
     *
     * @param routeId
     * @param time
     * @param timeUnit
     */
    public static void suspendRoute(String routeId, long time, TimeUnit timeUnit) {
        try {
            if (existRoute(routeId))
                context().suspendRoute(routeId, time, timeUnit);
        } catch (Exception e) {
            LOGGER.error("挂起路由{}异常：", routeId, e);
        }
    }


    public static boolean isStarting(String routId) {
        try {
            if (existRoute(routId))
                return ServiceStatus.Started.equals(getRouteStatus(routId));
        } catch (Exception e) {
            LOGGER.error("判断路由{}是否启用异常：", routId, e);
        }
        return false;

    }


    public static ProducerTemplate producer() {
        return ApplicationContextHolder.getBean("defaultProducerTemplate", ProducerTemplate.class);
    }

    public static ConsumerTemplate consumer() {
        return ApplicationContextHolder.getBean("defaultConsumerTemplate", ConsumerTemplate.class);
    }

}
