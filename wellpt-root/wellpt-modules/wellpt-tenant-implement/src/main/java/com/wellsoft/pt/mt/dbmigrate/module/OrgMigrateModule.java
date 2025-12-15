package com.wellsoft.pt.mt.dbmigrate.module;

import com.wellsoft.pt.mt.dbmigrate.AbstractMigrateModule;
import com.wellsoft.pt.mt.dbmigrate.IMigrateCommon;
import com.wellsoft.pt.mt.dbmigrate.IMigrateModule;
import com.wellsoft.pt.mt.dbmigrate.WellOrdered;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Properties;

@Order(WellOrdered.NORMAL_PRECEDENCE + 2)
@Component
public class OrgMigrateModule extends AbstractMigrateModule implements IMigrateModule, IMigrateCommon {
    public static final String MODULE_NAME = "org";
    public static final String JDBC_PREFIX = "multi.tenancy.org.";
    private Properties properties;

    public OrgMigrateModule() {
        super(MODULE_NAME);
    }

    @Override
    @Value("${" + MODULE_NAME + ".dbmigrate.schema}")
    public void setSchemaEnabled(boolean enabled) {
        super.setSchemaEnabled(enabled);
    }

    @Override
    @Value("${" + MODULE_NAME + ".dbmigrate.data}")
    public void setDataEnabled(boolean enabled) {
        super.setDataEnabled(enabled);
    }

    @Override
    @Value("${" + MODULE_NAME + ".dbmigrate.repair}")
    public void setRepairEnabled(boolean enabled) {
        super.setRepairEnabled(enabled);
    }

    /**
     * 获取组织模块数据源配置
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#getProperties()
     */
    @Override
    public Properties getProperties() {
        if (properties == null) {
            properties = super.getProperties(JDBC_PREFIX);
        }
        return properties;
    }

    /**
     * 获取组织模块数据源
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#getDataSource()
     */
    @Override
    public DataSource getDataSource() {
        return super.getDataSource(this.getProperties());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#beforeBuildDb(javax.sql.DataSource, org.springframework.jdbc.core.JdbcTemplate, java.lang.String)
     */
    @Override
    public boolean beforeBuildDb(DataSource dataSource, JdbcTemplate jdbcTemplate, String dbName) {
        return true;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#afterBuildDb()
     */
    @Override
    public String afterBuildDb(String jdbcType) {
        return getModuleSql(DB_BUILD_AFTER);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#beforeDropDb(javax.sql.DataSource, org.springframework.jdbc.core.JdbcTemplate, java.lang.String)
     */
    @Override
    public boolean beforeDropDb(DataSource dbaDataSource, JdbcTemplate jdbcTemplate, String dbName) {
        return true;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.dbmigrate.IMigrateCommon#afterBuildDrop()
     */
    @Override
    public String afterDropDb(String jdbcType) {
        return getModuleSql(DB_DROP_AFTER);
    }
}
