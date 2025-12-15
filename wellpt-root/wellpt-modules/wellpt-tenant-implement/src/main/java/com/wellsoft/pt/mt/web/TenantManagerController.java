/*
 * @(#)2013-4-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.web;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.mt.entity.*;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.mt.service.TenantService;
import com.wellsoft.pt.mt.service.TenantUpgradeBatchService;
import com.wellsoft.pt.mt.service.TenantUpgradeLogService;
import com.wellsoft.pt.mt.wizard.Wizard;
import com.wellsoft.pt.mt.wizard.WizardContext;
import com.wellsoft.pt.mt.wizard.impl.CreateWizardStepLog;
import com.wellsoft.pt.mt.wizard.impl.OracleWizardGenerator;
import com.wellsoft.pt.mt.wizard.impl.UpdateWizardStepLog;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-19.1	rzhu		2013-4-19		Create
 * </pre>
 * @date 2013-4-19
 */
@Controller
@RequestMapping("/superadmin/tenant")
public class TenantManagerController extends BaseController {

    // 文件类型 sql def
    private static final String FILE_IS_SQL = ".sql";
    private static final String FILE_IS_DEF = ".def";
    @Autowired
    private TenantManagerService tenantManagerService;
    @Autowired
    private DatabaseConfigFacadeService databaseConfigFacadeService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private TenantUpgradeBatchService tenantUpgradeBatchService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private CreateWizardStepLog createWizardStepLog;
    @Autowired
    private UpdateWizardStepLog updateWizardStepLog;
    @Autowired
    private TenantUpgradeLogService tenantUpgradeLogService;

    @RequestMapping("/active")
    public String active(Model model) {
        model.addAttribute("tenant", new Tenant());
        model.addAttribute("databaseConfigs", databaseConfigFacadeService.getAll());
        return forward("/superadmin/tenant/tenant_active");
    }

    @RequestMapping("/list")
    public String list(Model model) {
        return forward("/superadmin/tenant/tenant_list");
    }

    @RequestMapping("/setup")
    public String setup(Model model, HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        if (StringUtils.isNotBlank(uuid)) {
            Tenant tenant = tenantService.get(uuid);
            model.addAttribute("tenant", tenant);
        } else {
            model.addAttribute("tenant", new Tenant());
        }
        return forward("/superadmin/tenant/tenant_setup");
    }

    @RequestMapping("/update/setup")
    public String updateSetup(Model model, HttpServletRequest request) {
        return forward("/superadmin/tenant/tenant_update_setup");
    }

    @RequestMapping("/update")
    public String update(Model model, HttpServletRequest request) {
        return forward("/superadmin/tenant/tenant_update");
    }

    @RequestMapping("/deactive")
    public String deactive(Model model) {
        model.addAttribute("tenant", new Tenant());
        model.addAttribute("databaseConfigs", databaseConfigFacadeService.getAll());
        return forward("/superadmin/tenant/tenant_deactive");
    }

    @RequestMapping("/reject")
    public String reject(Model model) {
        model.addAttribute("tenant", new Tenant());
        model.addAttribute("databaseConfigs", databaseConfigFacadeService.getAll());
        return forward("/superadmin/tenant/tenant_reject");
    }

    @RequestMapping("/normal")
    public String normal(Model model) {
        model.addAttribute("tenant", new Tenant());
        model.addAttribute("databaseConfigs", databaseConfigFacadeService.getAll());
        return forward("/superadmin/tenant/tenant_normal");
    }

    @RequestMapping("/normal/list")
    public String normalList(Model model) {
        model.addAttribute("tenants", tenantManagerService.getNormalTenants());
        return forward("/superadmin/tenant/normal_list");
    }

    @RequestMapping("/review")
    public String review(Model model) {
        model.addAttribute("tenant", new Tenant());
        model.addAttribute("databaseConfigs", databaseConfigFacadeService.getAll());
        return forward("/superadmin/tenant/tenant_review");
    }

    @RequestMapping("/review/list")
    public String reviewList(Model model) {
        model.addAttribute("tenants", tenantManagerService.getReviewTenants());
        return forward("/superadmin/tenant/review_list");
    }

    @RequestMapping("/view/{tenantUuid}")
    public String view(@PathVariable("tenantUuid") String tenantUuid, Model model) {
        model.addAttribute("tenant", tenantManagerService.get(tenantUuid));
        return forward("/superadmin/tenant/view");
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createForm(Model model) {
        // 创建租户、初始化数据，设为已审核过
        Tenant tenant = new Tenant();
        // tenant.setReviewed(true);
        model.addAttribute("tenant", tenant);
        model.addAttribute("title", "创建租户");
        model.addAttribute("databaseConfigs", databaseConfigFacadeService.getAll());
        return forward("/superadmin/tenant/edit");
    }

    @RequestMapping(value = "/edit/{tenantUuid}", method = RequestMethod.GET)
    public String editForm(@PathVariable("tenantUuid") String tenantUuid, Model model) {
        Tenant tenant = tenantManagerService.get(tenantUuid);
        model.addAttribute("tenant", tenant);
        model.addAttribute("title", "编辑租户");
        model.addAttribute("databaseConfigName",
                databaseConfigFacadeService.getDatabaseConfigByUuid(tenant.getDatabaseConfigUuid()).getName());
        return forward("/superadmin/tenant/edit");
    }

    @RequestMapping(value = "/review/{tenantUuid}", method = RequestMethod.GET)
    public String reviewForm(@PathVariable("tenantUuid") String tenantUuid, Model model) {
        model.addAttribute("tenant", tenantManagerService.get(tenantUuid));
        model.addAttribute("databaseConfigs", databaseConfigFacadeService.getAll());
        return forward("/superadmin/tenant/review");
    }

    @RequestMapping(value = "/activeList", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage getActiveTenantList() {
        ResultMessage resultMessage = new ResultMessage();
        try {
            List<Tenant> tenantList = tenantManagerService.getNormalTenants();
            resultMessage.setData(tenantList);
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    @RequestMapping(value = "/created/tenant")
    @ResponseBody
    public void createdTenant(Model model, HttpServletRequest request) {
        try {
            String createBean = request.getParameter("createBean");
            WizardContext wizardContext = (WizardContext) JsonUtils.toBean(createBean, WizardContext.class);
            wizardContext.setJdbcPort(1521);
            OracleWizardGenerator oracleWizardGenerator = new OracleWizardGenerator();
            Wizard wizard = oracleWizardGenerator.getCreateWizard("Oracle11g");
            wizard.start(wizardContext);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @RequestMapping(value = "/update/tenant")
    @ResponseBody
    public void updateTenant(Model model, HttpServletRequest request) {
        TenantUpgradeLog tenantUpgradeLog = new TenantUpgradeLog();
        try {
            String updateBean = request.getParameter("updateBean");
            WizardContext wizardContext = (WizardContext) JsonUtils.toBean(updateBean, WizardContext.class);
            Tenant tenant = this.tenantService.get(wizardContext.getUuid());
            IgnoreLoginUtils.login(tenant.getId(), tenant.getId());
            wizardContext.setJdbcPort(1521);
            OracleWizardGenerator oracleWizardGenerator = new OracleWizardGenerator();
            Wizard wizard = oracleWizardGenerator.getUpgradeWizard("Oracle11g");
            String repoFileUuids = wizardContext.getRepoFileUuids();
            String repoFileNames = wizardContext.getRepoFileNames();
            tenantUpgradeLog.setTenant(tenant);
            TenantUpgradeBatch tenantUpgradeBatch = this.tenantUpgradeBatchService.get(wizardContext
                    .getUpgradeBatchUuid());
            tenantUpgradeLog.setTenantUpgradeBatch(tenantUpgradeBatch);
            tenantUpgradeLog.setRemark("执行开始！");
            tenantUpgradeLogService.save(tenantUpgradeLog);
            if (StringUtils.isNotBlank(repoFileUuids)) {
                String fileUuids[] = repoFileUuids.split(";");
                String fileNames[] = repoFileNames.split(";");
                List<String> fileList = new ArrayList<String>();
                for (String fileUuid : fileUuids) {
                    fileList.add(fileUuid);
                }
                // 将文件移入文件夹
                mongoFileService.pushFilesToFolder(wizardContext.getUpgradeBatchUuid(), fileList, null);
                wizardContext.setIsUpdate(true);
                for (int i = 0; i < fileUuids.length; i++) {
                    String fileName = fileNames[i];
                    String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());

                    wizardContext.setDataType(fileType.toLowerCase());
                    wizardContext.setRepoFileUuid(fileUuids[i]);
                    wizardContext.setRepoFileName(fileNames[i]);
                    wizard.start(wizardContext);
                }
                updateWizardStepLog.updateStepLog(null, tenant, "租户更新执行完成！", TenantUpgradeProcessLog.COMPLETE, -1,
                        wizardContext);
                tenantUpgradeLog.setResult(1);
                tenantUpgradeLog.setRemark("租户更新执行成功");
                tenantUpgradeLogService.save(tenantUpgradeLog);
                IgnoreLoginUtils.logout();
            }
            // 保存升级成功批次
        } catch (Exception e) {
            IgnoreLoginUtils.logout();
            // 保存升级失败批次
            tenantUpgradeLog.setResult(1);
            tenantUpgradeLog.setRemark("租户更新执行失败" + e.getMessage());
            tenantUpgradeLogService.save(tenantUpgradeLog);
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @SuppressWarnings("finally")
    @RequestMapping(value = "/created/tenant/log")
    @ResponseBody
    public String createdTenantLog(HttpServletRequest request) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        try {
            int setup = Integer.parseInt(request.getParameter("setup"));
            int batchNo = Integer.parseInt(request.getParameter("batchNo"));
            String tenantCode = request.getParameter("tenantCode");
            String tenantUuid = request.getParameter("tenantUuid");
            if (StringUtils.isNotBlank(tenantUuid)) {
                List<TenantCreateProcessLog> tenantCreateProcessLogs = tenantManagerService.getSetupLogByTenantUuid(
                        setup, batchNo, tenantUuid);
                if (tenantCreateProcessLogs != null && tenantCreateProcessLogs.size() > 0) {
                    String json = JsonUtils.object2Json(tenantCreateProcessLogs.get(0));
                    jsonObject.put("data", json);
                }
            } else if (StringUtils.isNotBlank(tenantCode)) {
                List<TenantCreateProcessLog> tenantCreateProcessLogs = tenantManagerService.getSetupLogByTenantCode(
                        setup, batchNo, tenantCode);
                if (tenantCreateProcessLogs != null && tenantCreateProcessLogs.size() > 0) {
                    String json = JsonUtils.object2Json(tenantCreateProcessLogs.get(0));
                    jsonObject.put("data", json);
                } else if (tenantCreateProcessLogs == null) {
                    jsonObject.put("data", "");
                }
            }
            jsonObject.put("status", "success");
        } catch (Exception e) {
            jsonObject.put("status", "fail");
            jsonObject.put("msg", e.getMessage());
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {

        }
        return jsonObject.toString();
    }

    @SuppressWarnings("finally")
    @RequestMapping(value = "/update/tenant/log")
    @ResponseBody
    public String updateTenantLog(HttpServletRequest request) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        try {
            int setup = Integer.parseInt(request.getParameter("setup"));
            String tenantUuid = request.getParameter("tenantUuid");
            String upgradeBatchUuid = request.getParameter("upgradeBatchUuid");
            List<TenantUpgradeProcessLog> tenantUpgradeProcessLogs = tenantManagerService
                    .getUpdateSetupLogByTenantUuid(setup, upgradeBatchUuid, tenantUuid);
            if (tenantUpgradeProcessLogs != null && tenantUpgradeProcessLogs.size() > 0) {
                String json = JsonUtils.object2Json(tenantUpgradeProcessLogs.get(0));
                jsonObject.put("data", json);
            }
            jsonObject.put("status", "success");
        } catch (Exception e) {
            jsonObject.put("status", "fail");
            jsonObject.put("msg", e.getMessage());
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {

        }
        return jsonObject.toString();
    }
}
