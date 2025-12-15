package com.wellsoft.pt.dyform.implement.definition.util.dyform;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class TableConfig {
    private static final Logger log = LoggerFactory.getLogger(TableConfig.class);
    //这里利用hbmdll来简化部分处理
    //	private SchemaExport export;
    //	private SchemaUpdate update;
    protected SessionFactory sessionFactory;
    private Configuration config;
    private Dialect dialect;
    private SqlStatementLogger sqlStatementLogger;
    private Mapping mapping;
    private String uuid = "";//表单uuid

    public TableConfig(Configuration cfg, String uuid) {
        this.config = cfg;
        dialect = ((SessionFactoryImpl) getSessionFactory()).getDialect();
        sqlStatementLogger = ((SessionFactoryImpl) getSessionFactory()).getServiceRegistry()
                .getService(JdbcServices.class).getSqlStatementLogger();
        initMapping();
        this.uuid = uuid;
    }

    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = ApplicationContextHolder.getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME,
                    SessionFactory.class);
        }
        return sessionFactory;
    }

    /**
     * @param 设定文件
     * @return void    返回类型
     * @throws
     * @Title: initMapping
     * @Description:将mapping初始化
     */
    private void initMapping() {
        Field mappingField;
        try {
            mappingField = config.getClass().getDeclaredField("mapping");
            mappingField.setAccessible(true);
            mapping = (Mapping) (mappingField.get(config));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    public void addTable() {
        //生成数据库表，这里只创建数据表
        CustomSchemaExport customSchemaExport = new CustomSchemaExport(
                ((SessionFactoryImpl) getSessionFactory()).getServiceRegistry(), config, this.uuid);
        customSchemaExport.execute(true, true, false, true);
        //		SchemaExport export = new SchemaExport(((SessionFactoryImpl) getSessionFactory()).getServiceRegistry(), config);
        //		export.execute(true, true, false, true);
    }

    /**
     * @param 设定文件
     * @return void    返回类型
     * @throws SQLException
     * @throws
     * @Title: updateTable
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public void updateTable() {
        CustomSchemaUpdate schemaUpdate = new CustomSchemaUpdate(
                ((SessionFactoryImpl) getSessionFactory()).getServiceRegistry(), config, this.uuid);
        schemaUpdate.execute(true, true);
    }

    /**
     * @param 设定文件
     * @return void    返回类型
     * @throws SQLException
     * @throws
     * @Title: updateTable
     * @Description: 删除表
     */
    public void dropTable() {
        //删除表
        CustomSchemaExport customSchemaExport = new CustomSchemaExport(
                ((SessionFactoryImpl) getSessionFactory()).getServiceRegistry(), config, this.uuid);
        customSchemaExport.execute(true, true, true, false);
        //		SchemaExport export = new SchemaExport(((SessionFactoryImpl) getSessionFactory()).getServiceRegistry(), config);
        //		export.execute(true, true, false, true);

    }

}
