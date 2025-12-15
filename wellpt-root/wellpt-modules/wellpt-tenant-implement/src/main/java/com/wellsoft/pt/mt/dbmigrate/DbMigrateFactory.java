/*
 * @(#)2016年3月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月4日.1	zhongzh		2016年3月4日		Create
 * </pre>
 * @date 2016年3月4日
 */
public class DbMigrateFactory {
    private final static DbMigrateFactory instance = new DbMigrateFactory();
    protected Logger logger = LoggerFactory.getLogger(DbMigrateFactory.class);
    private boolean enabled;
    private boolean clean;
    private boolean repair = true;
    private Properties commonProps;

    private DbMigrateFactory() {
    }

    public static AbstractFlywayMigrator createDbMigrator(String tenantJdbcType) {
        String beanName = StringUtils.uncapitalize(tenantJdbcType + "Migrate");
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        AbstractFlywayMigrator dbMigrator = applicationContext.getBean(beanName, AbstractFlywayMigrator.class);
        dbMigrator.setClean(instance.clean);
        dbMigrator.setRepair(instance.repair);
        dbMigrator.setCommonProps(instance.commonProps);
        return dbMigrator;
    }

    @PostConstruct
    private void init() {
        if (enabled == false) {
            logger.info("migrate disabled,skip dbmigrate");
            return;
        }
        String jdbcType = Config.getValue("database.type");
        DbMigrateFactory.createDbMigrator(jdbcType).init();
    }

    public void setCommonProps(Properties commonProps) {
        instance.commonProps = commonProps;
    }

    public void setEnabled(boolean enabled) {
        instance.enabled = enabled;
    }

    public void setClean(boolean clean) {
        instance.clean = clean;
    }

    public void setRepair(boolean repair) {
        instance.repair = repair;
    }
}
