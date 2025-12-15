package com.wellsoft.pt.common.groovy.event;

import com.wellsoft.context.util.groovy.GroovyUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/12/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/19    chenq		2018/12/19		Create
 * </pre>
 */
@Component
public class GroovyUtilsLoadClassEvent implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        GroovyUtils.loadCommonUseClass();
    }
}
