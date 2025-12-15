package com.wellsoft.pt.jpa.datasource;

import com.kingbase8.Driver;
import com.kingbase8.xa.KBXADataSource;
import com.wellsoft.pt.jpa.support.CustomKingbase8Dialect;
import com.wellsoft.pt.mt.entity.Tenant;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/4/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/4/8    chenq		2019/4/8		Create
 * </pre>
 */
public class KingbaseXADataSource extends AbstractXADataSource {

    static {
        //驱动注册
        try {
            DriverManager.registerDriver(new Driver());
        } catch (Exception var0) {
            throw new RuntimeException("Can not load Driver class dm.jdbc.driver.DmDriver");
        }
    }

    private String url;
    private String user;
    private String password;

    @Override
    public String getDriverClass() {
        return Driver.class.getCanonicalName();
    }


    @Override
    public String getXaDatasourceClass() {
        return KBXADataSource.class.getCanonicalName();
    }


    @Override
    public String getDialect() {
        return CustomKingbase8Dialect.class.getCanonicalName();
    }

    @Override
    public String getType() {
        return DatabaseType.KB.getName();
    }


    @Override
    public void extractDriverProperties(Properties properties) {
        driverProperties.put(XADriverProperty.URL, properties.get(XADriverProperty.URL));
        driverProperties.put("user", properties.get(XADriverProperty.USER));
        driverProperties.put("password", properties.get(XADriverProperty.PASSWORD));
    }


    @Override
    public XADataSource buildInternal(Tenant tenant) {
        String server = tenant.getJdbcServer();
        String port = tenant.getJdbcPort();
        String databaseName = tenant.getJdbcDatabaseName();
        this.user = tenant.getJdbcUsername();
        this.password = tenant.getJdbcPassword();
        this.jndiName = tenant.getId();
        if (StringUtils.isNotBlank(tenant.getJdbcUrlFormat())) {
            url = tenant.getJdbcUrlFormat();
            url = url.replace("${jdbcPort}", port);
            url = url.replace("${jdbcServer}", server);
            url = url.replace("${jdbcDatabaseName}", databaseName);
        } else {
            url = "jdbc:kingbase8://" + server + ":" + port + "/" + databaseName; // 兼容旧方式
        }
        driverProperties.put(XADriverProperty.URL, url);
        driverProperties.put("user", user);
        driverProperties.put("password", password);
        return this;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public boolean isEnableJdbc4ConnectionTest() {
        return false;
    }

    @Override
    public boolean isTestQueryEnable() {
        return true;
    }

    @Override
    public String getTestQuery() {
        return "select 1 from dual";
    }

    @Override
    public int getMaxIdleTime() {
        return 30 * 60;
    }
}
