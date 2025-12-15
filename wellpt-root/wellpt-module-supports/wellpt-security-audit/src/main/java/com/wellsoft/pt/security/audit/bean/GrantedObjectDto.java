package com.wellsoft.pt.security.audit.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月12日   chenq	 Create
 * </pre>
 */
public class GrantedObjectDto implements Serializable {

    private Object object;

    private String functionType;

    private boolean md5hex = false;

    private boolean granted = false;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public boolean getMd5hex() {
        return md5hex;
    }

    public void setMd5hex(boolean md5hex) {
        this.md5hex = md5hex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrantedObjectDto that = (GrantedObjectDto) o;
        return md5hex == that.md5hex &&
                Objects.equals(object, that.object) &&
                Objects.equals(functionType, that.functionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, functionType, md5hex);
    }
}
