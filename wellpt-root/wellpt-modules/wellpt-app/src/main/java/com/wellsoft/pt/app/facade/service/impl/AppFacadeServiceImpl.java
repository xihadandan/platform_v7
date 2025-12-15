package com.wellsoft.pt.app.facade.service.impl;

import com.wellsoft.pt.app.entity.AppTipNoLongerRemindEntity;
import com.wellsoft.pt.app.facade.service.AppFacadeService;
import com.wellsoft.pt.app.service.AppSystemInfoService;
import com.wellsoft.pt.app.service.AppTipNoLongerRemindService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/16    chenq		2019/10/16		Create
 * </pre>
 */
@Service
public class AppFacadeServiceImpl implements AppFacadeService {
    @Resource
    AppTipNoLongerRemindService appTipNoLongerRemindService;

    @Resource
    AppSystemInfoService appSystemInfoService;

    @Override
    public void appTipNoLongerRemind(String tipCode, String userId) {
        AppTipNoLongerRemindEntity remindEntity = new AppTipNoLongerRemindEntity(tipCode,
                StringUtils.isBlank(userId) ? SpringSecurityUtils.getCurrentUserId() : userId
        );
        appTipNoLongerRemindService.save(remindEntity);
    }

    @Override
    public boolean existAppTipNoLongerRemind(String tipCode, String userId) {
        return appTipNoLongerRemindService.existAppTipNoLongerRemind(tipCode,
                StringUtils.isBlank(userId) ? SpringSecurityUtils.getCurrentUserId() : userId);
    }

    @Override
    public String getAppSystemParam(String key, String system) {
        return appSystemInfoService.getAppSystemParam(key, system);
    }
}
