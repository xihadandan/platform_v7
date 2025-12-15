package com.wellsoft.pt.api.service;

import com.wellsoft.pt.api.dao.impl.ApiAuthroizeItemDaoImpl;
import com.wellsoft.pt.api.entity.ApiAuthorizeItemEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/10/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/25    chenq		2018/10/25		Create
 * </pre>
 */
public interface ApiAuthroizeItemService extends
        JpaService<ApiAuthorizeItemEntity, ApiAuthroizeItemDaoImpl, String> {


    void deleteBySytemUuids(List<String> systemUuids);

    List<ApiAuthorizeItemEntity> listBySystemUuid(String systemUuid);

    List<ApiAuthorizeItemEntity> listBySystemCode(String systemCode, boolean authorized);
}
