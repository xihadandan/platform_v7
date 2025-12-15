package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.pt.basicdata.iexport.service.IexportService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantTemplateModule;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.mt.service.TenantTemplateModuleService;
import com.wellsoft.pt.mt.wizard.Step;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class InitDefinitionDataStep extends BaseServiceImpl implements Step {
    @Autowired
    TenantTemplateModuleService tenantTemplateModuleService;
    @Autowired
    MongoFileService mongoFileService;
    @Autowired
    IexportService iexportService;
    @Autowired
    TenantManagerService tenantManagerService;

    @Override
    public String process(WizardContext context) {
        // setup4执行业务模块包
        String moduleUuids = context.getModuleUuids();
        if (StringUtils.isNotBlank(context.getModuleUuids())) {
            String moduleUuidArray[] = moduleUuids.split(";");
            for (String moduleUuid : moduleUuidArray) {
                TenantTemplateModule tenantTemplateModule = tenantTemplateModuleService.get(moduleUuid);
                String repoFileUuids = tenantTemplateModule.getRepoFileUuids();
                if (StringUtils.isNotBlank(repoFileUuids)) {
                    String repoFileUuidsArray[] = moduleUuids.split(";");
                    String repoFileNamesArray[] = tenantTemplateModule.getRepoFileNames().split(";");
                    for (int i = 0; i < repoFileUuidsArray.length; i++) {
                        MongoFileEntity mongoFileEntity = null;
                        try {
                            mongoFileEntity = mongoFileService.getFile(repoFileUuidsArray[i]);
                        } catch (Exception e1) {
                            throw new RuntimeException("执行租户业务模块包:" + repoFileNamesArray[i] + "文件获取失败", e1);
                        }
                        if (mongoFileEntity == null) {
                            throw new RuntimeException("执行租户业务模块包:" + repoFileNamesArray[i] + "文件丢失");
                        }
                        try {
                            // 不切换租户无法在租户库执行
                            Tenant tenant = tenantManagerService.get(context.getUuid());
                            IgnoreLoginUtils.login(tenant.getId(), tenant.getId());
                            iexportService.importData(mongoFileEntity.getInputstream(), true, null, false);
                        } catch (Exception e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                            throw new RuntimeException("执行租户业务模块包出错:" + e.getMessage(), e);
                        }

                    }
                }
            }

        }
        return "业务模块执行成功！";
    }

}
