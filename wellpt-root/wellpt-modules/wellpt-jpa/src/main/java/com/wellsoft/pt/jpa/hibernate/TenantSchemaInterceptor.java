package com.wellsoft.pt.jpa.hibernate;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.hibernate.EmptyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * Description: 租户 schema 拦截处理
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年10月10日   chenq	 Create
 * </pre>
 */
public class TenantSchemaInterceptor extends EmptyInterceptor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String defaultSchema = null;

    public TenantSchemaInterceptor() {
    }

    public TenantSchemaInterceptor(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    @Override
    public String onPrepareStatement(String sql) {

        try {
            //        if (sql.toLowerCase().indexOf(" uf_") != -1) { // 只针对带有表单前置的表sql处理
            String tenant = SpringSecurityUtils.getCurrentTenantId();
            StopWatch stopWatch = new StopWatch("租户 SQL Explain & Replace");
            stopWatch.start("租户 SQL Explain");
            DbType dbType = DbType.oracle;
            String databaseType = Config.getValue("database.type");
            SQLASTVisitorAdapter sqlastVisitorAdapter = new TenantOracleASTVisitorAdapter(defaultSchema, tenant, new TablePrefixMathASTTableSource("UF_"));
            if (DatabaseType.MySQL5.getName().equalsIgnoreCase(databaseType)) {
                sqlastVisitorAdapter = new TenantMysqlASTVisitorAdapter(defaultSchema, tenant, new TablePrefixMathASTTableSource("UF_"));
                dbType = DbType.mysql;
            } else if (DatabaseType.DM.getName().equalsIgnoreCase(databaseType)) {
                dbType = DbType.dm;
            } else if (DatabaseType.KB.getName().equalsIgnoreCase(databaseType)) {
                dbType = DbType.kingbase;

            }
            SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
            sqlStatement.accept(sqlastVisitorAdapter);
            stopWatch.stop();
            logger.info("租户 SQL Explain & Replace , Time : {}", stopWatch.prettyPrint());
            sql = sqlStatement.toString();
            logger.info("租户 SQL Explain & Replace , 最终SQL: {}", sql);
//        }
        } catch (Exception e) {
            logger.error("租户 SQL Explain & Replace 异常: ", e);
        }
        return sql;
    }


}
