package com.wellsoft.pt.api.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.api.entity.ApiOutSysServiceConfigEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
@Repository
public class ApiOutSystemServiceConfigDaoImpl extends
        AbstractJpaDaoImpl<ApiOutSysServiceConfigEntity, String> {

    public void deleteBySystemUuids(List<String> uuids) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("systemUuid", uuids);
        this.deleteByHQL(
                "delete from ApiOutSysServiceConfigEntity where systemConfig.uuid in (:systemUuid) ",
                map);
    }

    public ApiOutSysServiceConfigEntity getServiceConfig(String systemCode, String serviceCode) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sysCode", systemCode);
        params.put("serviceCode", serviceCode);
        //params.put("unitId", SpringSecurityUtils.getCurrentUserUnitId());
        return this.getOneByHQL(
                " from ApiOutSysServiceConfigEntity t where t.systemConfig.systemCode=:sysCode " +
                        "and t.serviceCode=:serviceCode  ", params);

    }
}
