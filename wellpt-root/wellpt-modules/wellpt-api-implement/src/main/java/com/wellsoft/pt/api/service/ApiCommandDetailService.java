package com.wellsoft.pt.api.service;

import com.wellsoft.pt.api.dao.impl.ApiCommandDetailDaoImpl;
import com.wellsoft.pt.api.entity.ApiCommandDetailEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
public interface ApiCommandDetailService extends
        JpaService<ApiCommandDetailEntity, ApiCommandDetailDaoImpl, String> {

    ApiCommandDetailEntity getByCommandUuid(String commandUuid);
}
