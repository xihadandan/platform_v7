package com.wellsoft.pt.api.facade;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.api.dto.ApiOutSysServiceConfigDto;
import com.wellsoft.pt.api.dto.ApiOutSystemCofigDto;

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
public interface ApiOutSystemFacadeService extends Facade {

    ApiOutSystemCofigDto getByUuid(String uuid, boolean fetchService);

    void deleteConfig(List<String> uuids);

    /**
     * 生成token
     * （目前token不由用户通过api调用生成）
     *
     * @param uuid
     * @return
     */
    String generateToken(String uuid);

    void addSystemConfig(ApiOutSystemCofigDto dto);

    Select2QueryData queryApiAdapterClass(Select2QueryInfo queryInfo);

    ApiOutSysServiceConfigDto getServiceConfig(String systemCode, String serviceCode);


}
