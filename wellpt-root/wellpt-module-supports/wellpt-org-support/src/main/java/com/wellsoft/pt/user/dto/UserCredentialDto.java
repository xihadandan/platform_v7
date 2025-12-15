package com.wellsoft.pt.user.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月22日   chenq	 Create
 * </pre>
 */
public class UserCredentialDto implements Serializable {

    private String type;

    private String code;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCredentialDto that = (UserCredentialDto) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, code);
    }
}
