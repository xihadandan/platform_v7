package com.wellsoft.pt.api.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.api.entity.ApiAuthorizeItemEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
@Repository
public class ApiAuthroizeItemDaoImpl extends AbstractJpaDaoImpl<ApiAuthorizeItemEntity, String> {

    public void deleteBySytemUuids(List<String> systemUuids) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("suuids", systemUuids);
        this.deleteByHQL(
                "delete from ApiAuthorizeItemEntity t where t.systemConfig.uuid in (:suuids)",
                param);

    }

    public List<ApiAuthorizeItemEntity> listBySystemUuid(String systemUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("suuid", systemUuid);
        return listByHQL(" from ApiAuthorizeItemEntity t where t.systemConfig.uuid =:suuid ",
                param);
    }

    public List<ApiAuthorizeItemEntity> listBySystemCode(String systemCode, boolean authorized) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("systemCode", systemCode);
        param.put("authorized", authorized);
        return listByHQL(
                "  from ApiAuthorizeItemEntity t where t.systemConfig.systemCode =:systemCode and t.isAuthorized=:authorized  ",
                param);

    }
}
