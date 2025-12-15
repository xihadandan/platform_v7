package com.wellsoft.pt;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Description:
 * FIXME:代码化配置前解决移动模块的代码依赖解耦
 *
 * @author chenq
 * @date 2019/11/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/15    chenq		2019/11/15		Create
 * </pre>
 */
@Configuration
@ImportResource(value = {"classpath:config/applicationContext-mobile.xml"})
public class MobileConfiguration {
}
