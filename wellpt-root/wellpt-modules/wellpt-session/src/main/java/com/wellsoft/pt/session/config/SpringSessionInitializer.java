package com.wellsoft.pt.session.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * Description: 添加springSessionFilter到过滤器栈顶
 *
 * @author chenq
 * @date 2019-02-18
 * <p/>
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019-02-18    chenq		2019-02-18		Create
 * </pre>
 */
public class SpringSessionInitializer extends AbstractHttpSessionApplicationInitializer {
    private static Logger logger = LoggerFactory.getLogger(SpringSessionInitializer.class);

    /*static {
        try {
            EnableRedisHttpSession httpSession = SpringSessionConfig.class.getAnnotation(
                    EnableRedisHttpSession.class);
            InvocationHandler handler = Proxy.getInvocationHandler(httpSession);
            Field field = handler.getClass().getDeclaredField("memberValues");
            field.setAccessible(true);
            // 获取实例的属性map
            Map<String, Object> memberValues = (Map<String, Object>) field.get(handler);
            // 修改属性值
            memberValues.put("maxInactiveIntervalInSeconds", Config.getSessionTimeout());
            logger.info("设置redis http session超时时间：{}", httpSession.maxInactiveIntervalInSeconds());
        } catch (Exception e) {
            logger.warn("修改redis session缓存过期时间失败：", e);
        }
    }*/


}
