package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.service.Facade;

/**
 * Description: CMS应用APP对外统一的接口封装
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
public interface AppFacadeService extends Facade {

    void appTipNoLongerRemind(String tipCode, String userId);

    boolean existAppTipNoLongerRemind(String tipCode, String userId);

    String getAppSystemParam(String key, String system);

}
