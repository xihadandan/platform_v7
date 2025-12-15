/*
 * @(#)2013-4-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;
import com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-22.1	zhulh		2013-4-22		Create
 * </pre>
 * @date 2013-4-22
 */
public class SQLServer2008TenantDatabaseBuilder implements TenantDatabaseBuilder {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.support.TenantDatabaseBuilder#build(com.wellsoft.pt.core.mt.entity.Tenant)
     */
    @Override
    public void build(Tenant tenant) {
        try {
            File inputFile = generateCreateDatabaseScript("init_tenant_database.ftl", tenant, "init");
            // // 生成附加数据库的数据文件
            // generateAttachDatabaseDataFile(tenant);
            // // 生成附加数据库的日志文件
            // generateAttachDatabaseLogFile(tenant);
            // // 生成附加数据库的脚本、创建用户、更改数据库所有者、用户权限授权、用户表更改唯一的管理员用户名及登录账号密码的脚本
            // File inputFile = generateDatabaseScriptFile(tenant);
            // 执行osql bat命令附加初始库
            DatabaseConfigFacadeService databaseConfigService = ApplicationContextHolder
                    .getBean(DatabaseConfigFacadeService.class);
            DatabaseConfig databaseConfig = databaseConfigService.getDatabaseConfigByUuid(tenant
                    .getDatabaseConfigUuid());
            String server = databaseConfig.getHost();
            String username = databaseConfig.getLoginName();
            String password = databaseConfig.getPassword();
            String osqlBatFilePath = Config.SQL_SERVER_2008_OSQL_BAT_FILE;
            String inputFilePath = inputFile.getAbsolutePath();
            String command = osqlBatFilePath + " " + server + " " + username + " " + password + " \"" + inputFilePath
                    + "\"";
            logger.warn("exexute commond " + command);
            Process process = Runtime.getRuntime().exec(command);
            String outputInfo = IOUtils.toString(process.getInputStream());
            logger.warn(IOUtils.toString(process.getInputStream()));
            int result = process.waitFor();
            if (result == 0) {
                logger.warn("tenant database build success!");
            } else {
                throw new RuntimeException("tenant database build failure, " + outputInfo);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));

            throw new RuntimeException(e);
        }
    }

    /**
     * @param string
     * @throws Exception
     */
    private File generateCreateDatabaseScript(String templateName, Tenant tenant, String fileNamePrefix)
            throws Exception {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration();
        // 加载模板
        loadTemplate(templateName, cfg);
        // 获取模板
        Template template = cfg.getTemplate(templateName);

        // 将对象转换成Map数据
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("databaseName", tenant.getJdbcDatabaseName());
        root.put("tenantDataDir", Config.SQL_SERVER_2008_TENANT_DATA_DIR);
        root.put("username", tenant.getJdbcUsername());
        root.put("password", tenant.getJdbcPassword());
        // 初化化用户表租户管理员数据
        root.put("uuid", UUID.randomUUID());
        root.put("account", tenant.getAccount());
        root.put("accountPassword", tenant.getPassword());
        // 合并数据输出
        StringWriter writer = new StringWriter();
        template.process(root, writer);

        // 生成的角本文件跟初始数据文件在同一目录下
        String databaseName = tenant.getJdbcDatabaseName();
        File dir = new File(Config.SQL_SERVER_2008_INITIAL_SCRIPT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File outputFile = new File(dir, databaseName + ".sql");
        FileOutputStream output = new FileOutputStream(outputFile);
        IOUtils.write(writer.toString(), output);
        IOUtils.closeQuietly(output);

        return outputFile;
    }

    /**
     * 如何描述该方法
     *
     * @param templateName
     * @param cfg
     * @throws IOException
     */
    private void loadTemplate(String templateName, freemarker.template.Configuration cfg) throws IOException {
        try {
            File templateDir = new File(TenantDatabaseBuilder.class.getResource("/").getFile(),
                    "com\\wellsoft\\pt\\core\\mt\\ftl");
            cfg.setDirectoryForTemplateLoading(templateDir);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));

            // 加载字符串模板
            StringTemplateLoader loader = new StringTemplateLoader();
            loader.putTemplate(
                    templateName,
                    IOUtils.toString(TenantDatabaseBuilder.class.getResourceAsStream("/com/wellsoft/pt/core/mt/ftl/"
                            + templateName)));
            cfg.setTemplateLoader(loader);
        }
    }

    /**
     * 生成指定数据库的附加数据库角本
     *
     * @param jdbcDatabaseName
     * @throws IOException
     */
    private File generateDatabaseScriptFile(Tenant tenant) throws IOException {
        String databaseName = tenant.getJdbcDatabaseName();
        String loginname = tenant.getJdbcUsername();
        String username = tenant.getJdbcUsername();
        String password = tenant.getJdbcPassword();
        StringBuilder sb = new StringBuilder();
        File dataFile = new File(Config.SQL_SERVER_2008_TENANT_DATA_DIR + File.separator + databaseName + ".mdf");
        File logFile = new File(Config.SQL_SERVER_2008_TENANT_DATA_DIR + File.separator + databaseName + "_log.ldf");
        // 附加数据库
        sb.append("USE [master]");
        sb.append(Separator.LINE.getValue());
        sb.append("GO");
        sb.append(Separator.LINE.getValue());
        sb.append("CREATE DATABASE [" + databaseName + "] ON ");
        sb.append(Separator.LINE.getValue());
        sb.append("( FILENAME = N'" + dataFile.getAbsolutePath() + "' ),");
        sb.append(Separator.LINE.getValue());
        sb.append("( FILENAME = N'" + logFile.getAbsolutePath() + "' )");
        sb.append(Separator.LINE.getValue());
        sb.append(" FOR ATTACH");
        sb.append(Separator.LINE.getValue());
        sb.append("GO");
        sb.append(Separator.LINE.getValue());

        // 创建用户、更改数据库所有者、用户权限授权
        sb.append("USE [master]");
        sb.append(Separator.LINE.getValue());
        sb.append("GO");
        sb.append(Separator.LINE.getValue());
        sb.append("CREATE LOGIN [" + username + "] WITH PASSWORD=N'" + password + "', DEFAULT_DATABASE=["
                + databaseName + "], CHECK_EXPIRATION=OFF, CHECK_POLICY=OFF");
        sb.append(Separator.LINE.getValue());
        sb.append("GO");
        sb.append(Separator.LINE.getValue());
        sb.append("USE [" + databaseName + "]");
        sb.append(Separator.LINE.getValue());
        sb.append("CREATE USER [" + username + "] FOR LOGIN [" + loginname + "]");
        sb.append(Separator.LINE.getValue());
        sb.append("GO");
        sb.append(Separator.LINE.getValue());

        // 用户表更改唯一的管理员用户名及登录账号密码的脚本
        sb.append("USE [" + databaseName + "]");
        sb.append(Separator.LINE.getValue());
        sb.append("UPDATE [" + databaseName + "].[dbo].[org_user]");
        sb.append(Separator.LINE.getValue());
        sb.append("SET [account_non_expired] = 0");
        sb.append(Separator.LINE.getValue());
        sb.append(",[account_non_locked] = 0");
        sb.append(Separator.LINE.getValue());
        sb.append(",[credentials_non_expired] = 0");
        sb.append(Separator.LINE.getValue());
        sb.append(",[enabled] = 1");
        sb.append(Separator.LINE.getValue());
        sb.append(",[issys] = 1");
        sb.append(Separator.LINE.getValue());
        sb.append(",[login_name] = '" + loginname + "'");
        sb.append(Separator.LINE.getValue());
        sb.append(",[password] = '" + password + "'");
        sb.append(Separator.LINE.getValue());
        sb.append(",[sex] = 1");
        sb.append(Separator.LINE.getValue());
        sb.append(",[user_name] = '" + username + "'");
        sb.append(Separator.LINE.getValue());
        sb.append("WHERE login_name = 'admin' and user_name = 'admin'");
        sb.append(Separator.LINE.getValue());

        // 生成的角本文件跟初始数据文件在同一目录下
        File dir = new File(Config.SQL_SERVER_2008_INITIAL_SCRIPT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File outputFile = new File(dir, databaseName + ".sql");
        FileOutputStream output = new FileOutputStream(outputFile);
        IOUtils.write(sb, output);
        IOUtils.closeQuietly(output);

        return outputFile;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.support.TenantDatabaseBuilder#drop(com.wellsoft.pt.core.mt.entity.Tenant)
     */
    @Override
    public void drop(Tenant tenant) {
        try {
            // 生成删除用户及分离数据库脚本
            File inputFile = generateCreateDatabaseScript("detach_tenant_database.ftl", tenant, "detach");
            DatabaseConfigFacadeService databaseConfigService = ApplicationContextHolder
                    .getBean(DatabaseConfigFacadeService.class);
            DatabaseConfig databaseConfig = databaseConfigService.getDatabaseConfigByUuid(tenant
                    .getDatabaseConfigUuid());
            String server = databaseConfig.getHost();
            String username = databaseConfig.getLoginName();
            String password = databaseConfig.getPassword();
            String osqlBatFilePath = Config.SQL_SERVER_2008_OSQL_BAT_FILE;
            String inputFilePath = inputFile.getAbsolutePath();
            String command = osqlBatFilePath + " " + server + " " + username + " " + password + " \"" + inputFilePath
                    + "\"";
            logger.warn("exexute commond " + command);
            Process process = Runtime.getRuntime().exec(command);
            String outputInfo = IOUtils.toString(process.getInputStream());
            logger.warn(IOUtils.toString(process.getInputStream()));
            int result = process.waitFor();
            if (result == 0) {
                logger.warn("tenant database detach success!");
            } else {
                throw new RuntimeException("tenant database detach failure, " + outputInfo);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }
}
