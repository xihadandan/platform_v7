package com.wellsoft.pt.mt.dbmigrate.module;

import com.wellsoft.pt.mt.dbmigrate.AbstractMigrateModule;
import com.wellsoft.pt.mt.dbmigrate.IMigrateModule;
import com.wellsoft.pt.mt.dbmigrate.WellOrdered;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(WellOrdered.NORMAL_PRECEDENCE + 8)
@Component
public class CoreMigrateModule extends AbstractMigrateModule implements IMigrateModule {
    public static final String MODULE_NAME = "core";

    public CoreMigrateModule() {
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
}
