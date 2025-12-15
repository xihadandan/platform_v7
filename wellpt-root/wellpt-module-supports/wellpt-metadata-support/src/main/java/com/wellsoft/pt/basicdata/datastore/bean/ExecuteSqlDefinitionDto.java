package com.wellsoft.pt.basicdata.datastore.bean;

import java.io.Serializable;

/**
 * Description: 可执行SQL 定义配置
 *
 * @author chenq
 * @date 2018/9/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/4    chenq		2018/9/4		Create
 * </pre>
 */

public class ExecuteSqlDefinitionDto implements Serializable {

    private static final long serialVersionUID = -1798252106265668976L;

    private String uuid;

    private String sqlContent;

    private String id;

    private String name;

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
