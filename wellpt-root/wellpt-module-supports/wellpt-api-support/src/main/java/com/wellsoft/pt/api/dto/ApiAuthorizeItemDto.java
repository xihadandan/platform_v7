package com.wellsoft.pt.api.dto;

import java.io.Serializable;

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
public class ApiAuthorizeItemDto implements Serializable {
    private static final long serialVersionUID = 582726370693225462L;

    private String pattern;

    private Boolean isAuthorized;//是否授权

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Boolean getIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(Boolean authorized) {
        isAuthorized = authorized;
    }
}
