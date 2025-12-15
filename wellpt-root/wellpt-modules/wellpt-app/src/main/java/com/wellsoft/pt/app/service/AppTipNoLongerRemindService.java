package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.impl.AppTipNoLongerRemindDaoImpl;
import com.wellsoft.pt.app.entity.AppTipNoLongerRemindEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface AppTipNoLongerRemindService extends
        JpaService<AppTipNoLongerRemindEntity, AppTipNoLongerRemindDaoImpl, String> {


    boolean existAppTipNoLongerRemind(String tipCode, String userId);
}
