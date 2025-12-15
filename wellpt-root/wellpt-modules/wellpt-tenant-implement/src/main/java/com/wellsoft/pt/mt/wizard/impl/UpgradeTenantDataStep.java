package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.pt.basicdata.iexport.service.IexportService;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantUpgradeProcessLog;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.mt.wizard.Step;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Transactional
@Service
public class UpgradeTenantDataStep extends AbstractStep implements Step {

    // 文件类型 sql def
    private static final String FILE_IS_SQL = ".sql";
    private static final String FILE_IS_DEF = ".def";
    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private IexportService iexportService;

    @Autowired
    private TenantManagerService tenantManagerService;

    @Override
    public String process(WizardContext context) {
        // TODO Auto-generated method stub
        try {
            MongoFileEntity mongoFileEntity = null;
            mongoFileEntity = mongoFileService.getFile(context.getRepoFileUuid());
            // sql调用执行SQL的方法
            if (FILE_IS_SQL.equals(context.getDataType())) {
                if (mongoFileEntity != null) {
                    InputStream inputStream = mongoFileEntity.getInputstream();
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    boolean firstLine = true;
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (!firstLine) {
                            stringBuilder.append(System.getProperty("line.separator"));
                        } else {
                            firstLine = false;
                        }
                        stringBuilder.append(line);
                    }
                    tenantManagerService.executeTenantSql(context.getUuid(), stringBuilder.toString(),
                            super.createLog(-1, context));
                } else {
                    throw new RuntimeException("文件" + context.getRepoFileName() + "丢失！执行失败");
                }
            }
            // 定义执行定义的方法
            if (FILE_IS_DEF.equals(context.getDataType())) {
                Tenant tenant = tenantManagerService.get(context.getUuid());
                if (mongoFileEntity != null) {
                    iexportService.importData(mongoFileEntity.getInputstream(), true, null, false);
                } else {
                    throw new RuntimeException("文件" + context.getRepoFileName() + "丢失！执行失败");
                }
                super.createLog(-1, context).onExecuted(null, tenant, TenantUpgradeProcessLog.SUCCESS,
                        context.getRepoFileName() + "文件执行成功！");
            }
        } catch (Exception e) {
            throw new RuntimeException("文件：" + context.getRepoFileName() + "执行失败！", e);
        }
        return context.getRepoFileName() + "文件执行成功！";
    }
}
