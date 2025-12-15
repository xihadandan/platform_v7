package com.wellsoft.pt.di.component.sql;

import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.enums.DIParameterDomType;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/23    chenq		2019/8/23		Create
 * </pre>
 */
public class SqlEndpoint extends
        AbstractEndpoint<SqlDIComponent, SqlProducer, SqlConsumer> {

    @EndpointParameter(domType = DIParameterDomType.TEXTAREA, name = "SQL")
    private String sql;

    @EndpointParameter(domType = DIParameterDomType.INPUT, name = "JDBC URL")
    private String jdbcUrl;

    @EndpointParameter(domType = DIParameterDomType.INPUT, name = "用户名")
    private String user;

    @EndpointParameter(domType = DIParameterDomType.INPUT, name = "密码")
    private String password;


    @Override
    public String endpointPrefix() {
        return "sql";
    }

    @Override
    public String endpointName() {
        return "数据交换-SQL端点";
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
