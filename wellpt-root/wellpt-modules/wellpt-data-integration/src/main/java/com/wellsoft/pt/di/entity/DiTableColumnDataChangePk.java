package com.wellsoft.pt.di.entity;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/29
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/29    chenq		2019/8/29		Create
 * </pre>
 */
public class DiTableColumnDataChangePk implements Serializable {

    private static final long serialVersionUID = -3390770836675573246L;
    private String uuid;

    private String columnName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
