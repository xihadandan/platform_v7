package com.wellsoft.pt.api.support;

import io.jsonwebtoken.Claims;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/10/27
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/27    chenq		2018/10/27		Create
 * </pre>
 */
public class TokenClaims extends CaseInsensitiveMap<String, Object> {

    private static final String UNIT_KEY = "unit";

    private static final String SYS_CODE_KEY = "systemCode";

    private static final String SYS_UUID_KEY = "systemUuid";

    private static final String SYS_NAME_KEY = "systemName";

    private static final String AUTHORIZE_API_KEY = "authorizeApis";

    private static final String UNAUTHORIZE_API_KEY = "unauthorizeApis";

    public List<String> getAuthorizeApis() {
        return (List<String>) this.get(AUTHORIZE_API_KEY);
    }

    public void setAuthorizeApis(List<String> apis) {
        this.put(AUTHORIZE_API_KEY, apis);
    }

    public List<String> getUnauthorizeApis() {
        return (List<String>) this.get(UNAUTHORIZE_API_KEY);
    }

    public void setUnauthorizeApis(List<String> apis) {
        this.put(UNAUTHORIZE_API_KEY, apis);
    }

    public String getAudience() {
        return (String) this.get(Claims.AUDIENCE);
    }

    public void setSystemName(String systemName) {
        this.put(SYS_NAME_KEY, systemName);
    }

    public String getSystemCode() {
        return (String) this.get(SYS_CODE_KEY);
    }

    public void setSystemCode(String systemCode) {
        this.put(SYS_CODE_KEY, systemCode);
    }

    public String getSysteName() {
        return (String) this.get(SYS_NAME_KEY);
    }

    public String getUnit() {
        return (String) this.get(UNIT_KEY);
    }

    public void setUnit(String unidId) {
        this.put(UNIT_KEY, unidId);
    }


}
