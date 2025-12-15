package com.wellsoft.pt.dm.sql;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月17日   chenq	 Create
 * </pre>
 */
public class Table {

    private String tableName;

    private String alias;


    public Table(final String tableName, final String alias) {
        this.tableName = tableName;
        this.alias = alias;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }


    public Table innerJoin(Table b) {
        return this;
    }

    public Table leftJoin(Table b) {
        return this;
    }

    public Table on(String onCondition) {
        return this;
    }

}
