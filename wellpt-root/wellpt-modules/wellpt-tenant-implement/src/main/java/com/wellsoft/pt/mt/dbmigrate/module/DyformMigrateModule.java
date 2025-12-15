package com.wellsoft.pt.mt.dbmigrate.module;

import com.wellsoft.pt.mt.dbmigrate.AbstractMigrateModule;
import com.wellsoft.pt.mt.dbmigrate.IMigrateModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DyformMigrateModule extends AbstractMigrateModule implements IMigrateModule {
    private static final String MODULE_NAME = "dyform";

    public DyformMigrateModule() {
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
