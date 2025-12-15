package com.wellsoft.pt.ops.server.listener;

import com.wellsoft.pt.ops.server.service.ServerRegisterCenterService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/30    chenq		2019/7/30		Create
 * </pre>
 */
//@Component
public class OpsRegisterServerListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            contextRefreshedEvent.getApplicationContext().getBean(
                    ServerRegisterCenterService.class).registCurrentServer();

        }

    }
}
