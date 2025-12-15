package com.wellsoft.pt.app.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.bean.AppDefCodeHisDto;
import com.wellsoft.pt.app.dao.impl.AppDefCodeHisDaoImpl;
import com.wellsoft.pt.app.entity.AppDefCodeHisEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/12/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/7    chenq		2018/12/7		Create
 * </pre>
 */
public interface AppDefCodeHisService extends
        JpaService<AppDefCodeHisEntity, AppDefCodeHisDaoImpl, String> {
    String getScriptContent(String uuid);

    List<AppDefCodeHisDto> listByPage(String relaBusizUuid, String scriptType, PagingInfo page);
}
