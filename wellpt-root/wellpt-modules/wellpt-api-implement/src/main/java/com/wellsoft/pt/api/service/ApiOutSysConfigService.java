package com.wellsoft.pt.api.service;

import com.wellsoft.pt.api.dao.impl.ApiOutSystemConfigDaoImpl;
import com.wellsoft.pt.api.dto.ApiOutSysServiceConfigDto;
import com.wellsoft.pt.api.dto.ApiOutSystemCofigDto;
import com.wellsoft.pt.api.entity.ApiOutSystemConfigEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

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
public interface ApiOutSysConfigService extends
        JpaService<ApiOutSystemConfigEntity, ApiOutSystemConfigDaoImpl, String> {
    void deleteConfig(List<String> uuids);

    byte[] tokenKey();

    ApiOutSystemCofigDto getByUuid(String uuid, boolean fetchService);

    ApiOutSysServiceConfigDto getServiceConfig(String systemCode, String serviceCode);


    ApiOutSystemConfigEntity getBySystemCode(String systemCode);
}
