/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.exceltemplate.service.ExcelImportRuleService;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: DepartmentUserController.java
 *
 * @author liuzq
 * @date 2013-9-23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-9-23.1	liuzq		2013-9-23		Create
 * </pre>
 */
@Controller
@RequestMapping("/org/department/user")
public class DepartmentUserController extends JqGridQueryController {

    private static final String ORG_DEPARTMENT_IMPORT_RULE = "org_department_001";
    private static final String ORG_JOB_IMPORT_RULE = "org_job_001";
    private static final String ORG_USER_IMPORT_RULE = "org_user_001";
    private static final String ORG_DUTY_IMPORT_RULE = "org_duty_001";
    private static final String ORG_USER_JOB_IMPORT_RULE = "org_user_job_001";
    private static final String ORG_EMPLOYEE_IMPORT_RULE = "org_employee_001";
    private static final String ORG_EMPLOYEE_JOB_IMPORT_RULE = "org_employee_job_001";
    private static final String USER = "用户";
    private static final String DEPT = "部门";
    private static final String JOB = "职位";
    private static final String DUTY = "职务";
    private static final String USERJOB = "用户职位";
    private static Logger logger = Logger.getLogger(DepartmentUserController.class);
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private ExcelImportRuleService excelImportRuleService;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentUserService departmentUserService;
    @Autowired
    private TenantManagerService tenantManagerService;
    @Autowired
    private DepartmentAdminService departmentAdminService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private JobService jobService;
    @Autowired
    private DutyService dutyService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SecurityApiFacade securityApiFacade;

    @RequestMapping(value = "")
    public String departmentList(Model model) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String userId = userDetails.getUserId();
        // boolean isAdmin = securityApiFacade.hasRole(userId,
        // RoleConstants.ROLE_ADMIN);
        // if (!isAdmin) {
        model.addAttribute("tenantList", tenantManagerService.getNormalTenants());
        return forward("/org/department_user");
		/*	} else if (departmentAdminService.isDepartmentAdmin(userId)) {
				List<String> departmentUuids = departmentAdminService.getDepartmentUuidListByUserId(userId);
				model.addAttribute("currentUserId", userId);
				if (departmentUuids.isEmpty()) {
					model.addAttribute("departmentUuid", departmentUuids.get(0));
				}
				model.addAttribute("tenantList", tenantManagerService.getNormalTenants());
				return forward("/org/departmentAdmin_user");
			} else {
				Department department = departmentService.getById(userDetails.getDepartmentId());
				model.addAttribute("currentUserId", userId);
				model.addAttribute("departmentUuid", department.getUuid());
				model.addAttribute("tenantList", tenantManagerService.getNormalTenants());
				return forward("/org/departmentAdmin_user");
			}*/
    }

    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(@RequestParam("departmentUuid") String departmentUuid,
                               HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        QueryData queryData = departmentUserService.queryByDepartment(departmentUuid, jqGridQueryInfo, queryInfo);
        return convertToJqGridQueryData(queryData);
    }

    @RequestMapping(value = "/importUser", method = RequestMethod.POST)
    public @ResponseBody
    void importUser(@RequestParam("ruleId") String ruleId,
                    @RequestParam(value = "upload") MultipartFile excelFile, HttpServletResponse response) throws IOException {
        String[] ruleids = ruleId.split(";");
        boolean isImportSuccess = true;
        String msg = "";
        HashMap<String, Object> deptRsMp = new HashMap<String, Object>();
        HashMap<String, Object> userRsMp = new HashMap<String, Object>();
        HashMap<String, Object> postRsMp = new HashMap<String, Object>();
        HashMap<String, Object> jobRsMp = new HashMap<String, Object>();
        HashMap<String, Object> userjobRsMp = new HashMap<String, Object>();
        HashMap<String, Object> EmployeeRsMp = new HashMap<String, Object>();
        HashMap<String, Object> EmployeejobRsMp = new HashMap<String, Object>();

        List<Map<String, Object>> userJobList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> employeeJobList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < ruleids.length; i++) {
            // 如果有一个报错 就不导入了
            if (!isImportSuccess) {
                break;
            }

            if (ruleids[i].equals(ORG_DEPARTMENT_IMPORT_RULE)) {// 部门
                try {
                    List localList = excelImportRuleService.getListFromExcel(ruleids[i], excelFile.getInputStream(),
                            DEPT);
                    deptRsMp = departmentService.saveDepartmentFromList(localList);
                    msg = msg + deptRsMp.get("msg");
                } catch (Exception e) {
                    isImportSuccess = false;
                    msg = "Error: 1 " + e.getMessage();
                    logger.error(msg, e);
                }
                logger.info("all deparemts imported.");
            } else if (ruleids[i].equals(ORG_DUTY_IMPORT_RULE)) {// 职务
                try {
                    List localList = excelImportRuleService.getListFromExcel(ruleids[i], excelFile.getInputStream(),
                            DUTY);
                    postRsMp = dutyService.saveDutyFromList(localList);
                    msg = msg + "\n" + postRsMp.get("msg");
                } catch (Exception e) {
                    isImportSuccess = false;
                    msg = msg + "\n" + "Error: 2 " + e.getMessage();
                    logger.error(msg, e);
                }
                logger.info("all duties imported.");
            } else if (ruleids[i].equals(ORG_JOB_IMPORT_RULE)) {// 职位
                try {
                    List localList = excelImportRuleService.getListFromExcel(ruleids[i], excelFile.getInputStream(),
                            JOB);
                    jobRsMp = jobService.saveJobFromList(localList);
                    msg = msg + "\n" + jobRsMp.get("msg");
                } catch (Exception e) {
                    isImportSuccess = false;
                    msg = msg + "\n" + "Error: 3 " + e.getMessage();
                    logger.error(msg, e);
                }
                logger.info("all jobs imported.");
            } else if (ruleids[i].equals(ORG_USER_IMPORT_RULE)) { // 用户
                try {
                    List localList = excelImportRuleService.getListFromExcel(ruleids[i], excelFile.getInputStream(),
                            USER);

                    userRsMp = userService.saveUserFromList(localList);
                    msg = msg + "\n" + userRsMp.get("msg");
                } catch (Exception e) {
                    isImportSuccess = false;
                    msg = msg + "\n" + "Error: 4 " + e.getMessage();
                    logger.error(msg, e);
                }
                logger.info("all users imported.");
            } else if (ruleids[i].equals(ORG_USER_JOB_IMPORT_RULE)) {// 用户职位关系
                try {
                    List localList = excelImportRuleService.getListFromExcel(ORG_USER_IMPORT_RULE,
                            excelFile.getInputStream(), USER);
                    userJobList = this.basicDataApiFacade.getListByExcel(excelFile.getInputStream(), ruleids[i],
                            USERJOB);

                    userjobRsMp = userService.saveUserJobRelactionFromList(localList, userJobList);
                    msg = msg + "\n" + userjobRsMp.get("msg");
                } catch (Exception e) {
                    isImportSuccess = false;
                    msg = msg + "\n" + "Error: 5 " + e.getMessage();
                    logger.error(msg, e);
                }
                logger.info("all user_jobs imported.");
            }
            // else if (ruleids[i].equals(ORG_EMPLOYEE_IMPORT_RULE)) {//人员表
            // try {
            // List localList = excelImportRuleService.getListFromExcel(
            // ruleids[i],excelFile.getInputStream(), USER);
            // EmployeeRsMp = employeeService.saveEmployeeFromList(localList);
            // EmployeeRsMp.put("employeeList", localList);
            // msg = msg + "\n" + EmployeeRsMp.get("msg");
            // } catch (Exception e) {
            // isImportSuccess = false;
            // msg = msg + "\n" + "Error: 6 " + e.getMessage();
            // logger.error(msg, e);
            // }
            // logger.info("all employee imported.");
            // } else if (ruleids[i].equals(ORG_EMPLOYEE_JOB_IMPORT_RULE))
            // {//人员职位关系
            // try {
            // List localList = (List) EmployeeRsMp.get("employeeList");
            // employeeJobList =
            // excelImportRuleService.getListFromExcel(ruleids[i],excelFile.getInputStream(),
            // USERJOB);
            // EmployeejobRsMp =
            // employeeService.saveEmployeeJobRelactionFromList(localList,
            // employeeJobList);
            // msg = msg + "\n" + EmployeejobRsMp.get("msg");
            // } catch (Exception e) {
            // isImportSuccess = false;
            // msg = msg + "\n" + "Error: 7 " + e.getMessage();
            // logger.error(msg, e);
            // }
            // logger.info("all employee_job imported.");
            // }
        }
        if (isImportSuccess) {
            try {
                // 更新部门负责人
                departmentService.updateDeptPrincipalAfterImport(deptRsMp);
                msg = msg + "\n" + "更新部门负责人成功！";
            } catch (Exception e) {
                isImportSuccess = false;
                msg = msg + "\n" + "Error: 6 " + e.getMessage();
                logger.error(msg, e);
            }
        }

        if (isImportSuccess) {
            try {
                // 更新职位汇报对象
                jobService.updateJobLeadersAfterImport(jobRsMp);
                msg = msg + "\n" + "更新职位汇报对象成功！";
            } catch (Exception e) {
                isImportSuccess = false;
                msg = msg + "\n" + "Error: 7 " + e.getMessage();
                logger.error(msg, e);
            }
        }
        // 更新用户的上级领导
        // userService.updateUserLeadersAfterImport(userRsMp);

        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(msg);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
