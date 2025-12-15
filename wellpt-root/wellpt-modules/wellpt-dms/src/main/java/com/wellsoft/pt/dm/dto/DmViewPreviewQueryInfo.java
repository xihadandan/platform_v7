package com.wellsoft.pt.dm.dto;

import com.wellsoft.context.jdbc.support.PagingInfo;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月27日   chenq	 Create
 * </pre>
 */
public class DmViewPreviewQueryInfo implements Serializable {
    private String sql;
    private String sqlParameter;
    private PagingInfo pagingInfo;
    private String sqlObj;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlParameter() {
        return sqlParameter;
    }

    public void setSqlParameter(String sqlParameter) {
        this.sqlParameter = sqlParameter;
    }

    public PagingInfo getPagingInfo() {
        return pagingInfo;
    }

    public void setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

    public String getSqlObj() {
        return sqlObj;
    }

    public void setSqlObj(String sqlObj) {
        this.sqlObj = sqlObj;
    }
}
