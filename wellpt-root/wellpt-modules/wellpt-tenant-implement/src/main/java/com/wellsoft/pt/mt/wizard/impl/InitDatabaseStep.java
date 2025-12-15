package com.wellsoft.pt.mt.wizard.impl;

import com.wellsoft.pt.mt.entity.TenantTemplate;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.mt.service.TenantTemplateService;
import com.wellsoft.pt.mt.wizard.Step;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.mt.wizard.WizardStepLog;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
@Transactional
public class InitDatabaseStep extends AbstractStep implements Step {
    @Autowired
    TenantTemplateService tenantTemplateService;
    @Autowired
    private WizardStepLog WizardStepLog;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private TenantManagerService tenantManagerService;

    @SuppressWarnings("resource")
    @Override
    public String process(WizardContext context) {
        if (StringUtils.isBlank(context.getTempleUuid())) {
            return "租户模板生成成功！";
        }
        final int batchNo = context.getBatchNo();
        TenantTemplate tenantTemplate = this.tenantTemplateService.get(context.getTempleUuid());

        String ddlFileUuids = tenantTemplate.getDdlFileUuids();
        if (StringUtils.isNotBlank(ddlFileUuids)) {
            String ddlFileUuidsArray[] = ddlFileUuids.split(";");
            String ddlFileNamesArray[] = tenantTemplate.getDdlFileNames().split(";");
            for (int i = 0; i < ddlFileUuidsArray.length; i++) {
                MongoFileEntity mongoFileEntity = null;
                try {
                    mongoFileEntity = mongoFileService.getFile(ddlFileUuidsArray[i]);
                } catch (Exception e) {
                    throw new RuntimeException("执行租户模板ddl模板:" + ddlFileNamesArray[i] + "文件获取异常", e);
                }
                if (mongoFileEntity == null) {
                    throw new RuntimeException("执行租户模板ddl模板:" + ddlFileNamesArray[i] + "文件丢失");
                }
                InputStream inputStream = mongoFileEntity.getInputstream();
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                boolean firstLine = true;
                String line = null;
                ;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        if (!firstLine) {
                            stringBuilder.append(System.getProperty("line.separator"));
                        } else {
                            firstLine = false;
                        }
                        stringBuilder.append(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("执行租户模板ddl模板:" + ddlFileNamesArray[i] + "文件内容获取失败", e);
                }
                tenantManagerService.executeTenantSql(context.getUuid(), stringBuilder.toString(),
                        super.createLog(batchNo, null));
            }
        }
        String dmlFileUuids = tenantTemplate.getDmlFileUuids();
        if (StringUtils.isNotBlank(dmlFileUuids)) {
            String dmlFileUuidsArray[] = dmlFileUuids.split(";");
            String dmlFileNamesArray[] = tenantTemplate.getDmlFileNames().split(";");
            for (int i = 0; i < dmlFileUuidsArray.length; i++) {

                MongoFileEntity mongoFileEntity = null;
                try {
                    mongoFileEntity = mongoFileService.getFile(dmlFileUuidsArray[i]);
                } catch (Exception e) {
                    throw new RuntimeException("执行租户模板dml模板:" + dmlFileNamesArray[i] + "文件获取异常", e);
                }
                if (mongoFileEntity == null) {
                    throw new RuntimeException("执行租户模板dml模板:" + dmlFileNamesArray[i] + "文件丢失");
                }

                InputStream inputStream = mongoFileEntity.getInputstream();
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                boolean firstLine = true;
                String line = null;
                ;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        if (!firstLine) {
                            stringBuilder.append(System.getProperty("line.separator"));
                        } else {
                            firstLine = false;
                        }
                        stringBuilder.append(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("执行租户模板dml模板:" + dmlFileNamesArray[i] + "文件内容获取失败", e);
                }
                tenantManagerService.executeTenantSql(context.getUuid(), stringBuilder.toString(),
                        super.createLog(batchNo, null));
            }
        }
        return "租户模板执行成功！";
    }
}
