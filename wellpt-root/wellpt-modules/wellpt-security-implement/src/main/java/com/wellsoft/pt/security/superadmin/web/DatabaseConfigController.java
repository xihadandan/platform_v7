/*
 * @(#)2013-4-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;
import com.wellsoft.pt.security.superadmin.service.DatabaseConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-24.1	rzhu		2013-4-24		Create
 * </pre>
 * @date 2013-4-24
 */
@Api(tags = "超管数据库配置")
@RestController
@RequestMapping("/superadmin/database/config")
public class DatabaseConfigController extends BaseController {

    public static final Map<String, String> databaseTypes = new LinkedHashMap<String, String>();

    static {
        databaseTypes.put("SQLServer2008", "Microsoft SQL Server 2008");
        databaseTypes.put("SQLServer2005", "Microsoft SQL Server 2005");
        databaseTypes.put("SQLServer2000", "Microsoft SQL Server 2000");
        databaseTypes.put("Oracle11g", "Oracle 11g");
        databaseTypes.put("Oracle10g", "Oracle 10g");
        databaseTypes.put("Oracle9i", "Oracle 9i");
        databaseTypes.put("Oracle", "Oracle (any version)");
        databaseTypes.put("MySQL5", "MySQL5");
        databaseTypes.put("MySQL5InnoDB", "MySQL5 with InnoDB");
        databaseTypes.put("MySQLMyISAM", "MySQL with MyISAM");
        databaseTypes.put("DB2", "DB2");
        databaseTypes.put("DB2400", "DB2 AS/400");
        databaseTypes.put("PostgreSQL81", "PostgreSQL 8.1");
        databaseTypes.put("PostgreSQL82", "PostgreSQL 8.2 and later");
        databaseTypes.put("SybaseASE15", "Sybase ASE 15.5");
        databaseTypes.put("SybaseASE157", "Sybase ASE 15.7");
        databaseTypes.put("SybaseAnywhere", "Sybase Anywhere");
        databaseTypes.put("SAPDB", "SAP DB");
        databaseTypes.put("Informix", "Informix");
        databaseTypes.put("HSQL", "HypersonicSQL");
        databaseTypes.put("H2", "H2 Database");
        databaseTypes.put("Ingres", "Ingres");
        databaseTypes.put("Progress", "Progress");
        databaseTypes.put("Mckoi", "Mckoi SQL");
        databaseTypes.put("Interbase", "Interbase");
        databaseTypes.put("Pointbase", "Pointbase");
        databaseTypes.put("FrontBase", "Frontbase");
        databaseTypes.put("Firebird", "Firebird");
    }

    @Autowired
    private DatabaseConfigService databaseConfigService;

    /**
     * 打开数据库管理员配置
     *
     * @return
     */
    @ApiIgnore
    @GetMapping("")
    @ApiOperation(value = "打开数据库管理员配置", notes = "打开数据库管理员配置", tags = {"暂时没用到"})
    public String databaseConfig(Model model) {
        model.addAttribute("databaseConfig", new DatabaseConfig());
        model.addAttribute("databaseTypes", databaseTypes);
        return forward("/superadmin/database/config");
    }

    /**
     * 打开数据库管理员配置
     *
     * @return
     */
    @ApiIgnore
    @GetMapping("/list")
    @ApiOperation(value = "跳转数据库管理员配置列表", notes = "跳转数据库管理员配置列表", tags = {"暂时没用到"})
    public String list(Model model) {
        model.addAttribute("databaseConfigs", databaseConfigService.getAll());
        return forward("/superadmin/database/config_list");
    }

    @ApiIgnore
    @GetMapping("/view/{databaseConfigUuid}")
    @ApiOperation(value = "跳转数据库管理员配置视图", notes = "跳转数据库管理员配置视图", tags = {"暂时没用到"})
    public String view(@PathVariable("databaseConfigUuid") String databaseConfigUuid, Model model) {
        DatabaseConfig databaseConfig = databaseConfigService.get(databaseConfigUuid);
        model.addAttribute("title", "数据库配置信息");
        model.addAttribute("databaseConfig", databaseConfig);
        model.addAttribute("databaseTypeName", databaseTypes.get(databaseConfig.getType()));
        return forward("/superadmin/database/config_view");
    }

    @ApiIgnore
    @GetMapping("/create")
    @ApiOperation(value = "创建数据库类型", notes = "创建数据库类型", tags = {"暂时没用到"})
    public String createForm(Model model) {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        model.addAttribute("title", "创建数据库类型");
        model.addAttribute("databaseConfig", databaseConfig);
        model.addAttribute("databaseTypes", databaseTypes);
        return forward("/superadmin/database/config_edit");
    }

    @ApiIgnore
    @GetMapping("/edit/{databaseConfigUuid}")
    @ApiOperation(value = "编辑数据库类型", notes = "编辑数据库类型", tags = {"暂时没用到"})
    public String editForm(@PathVariable("databaseConfigUuid") String databaseConfigUuid, Model model) {
        DatabaseConfig databaseConfig = databaseConfigService.get(databaseConfigUuid);
        model.addAttribute("title", "编辑数据库类型");
        model.addAttribute("databaseConfig", databaseConfig);
        model.addAttribute("databaseTypes", databaseTypes);
        return forward("/superadmin/database/config_edit");
    }

}
