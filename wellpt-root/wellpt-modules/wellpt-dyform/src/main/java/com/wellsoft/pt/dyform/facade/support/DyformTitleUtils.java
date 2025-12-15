/*
 * @(#)2021年1月18日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.facade.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.AbstractTitleGenerate;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.params.facade.SysParamItemConfigMgr;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月18日.1	zhongzh		2021年1月18日		Create
 * </pre>
 * @date 2021年1月18日
 */
public abstract class DyformTitleUtils extends AbstractTitleGenerate {

    private static final Logger LOG = LoggerFactory.getLogger(DyformTitleUtils.class);

    public static String getFinalTitleExpression(String titleExpression) {
        TitleExpression express = new TitleExpression(titleExpression);
        while (express.hasNext()) {
            String ch = express.next();
            if (StringUtils.isNotBlank(ch)) {
                int start = ch.indexOf("("), end = ch.lastIndexOf(")");
                //				int start = ch.indexOf("(dyform."), end = ch.lastIndexOf(")");
                //				if (start < 0) {
                //					start = ch.indexOf("(sys.");
                //				}
                if (start > -1 && end > start) {
                    String finalKey = ch.substring(start + 1, end);
                    titleExpression = titleExpression.replace(ch, finalKey);
                }
            }
        }
        return titleExpression;
    }

    /**
     * 生成表单实例标题
     *
     * @param dyformDefinition
     * @param flowInstance
     * @param taskData
     * @param dyFormData
     * @return
     */
    public static String generateDyformTitle(DyFormFormDefinition dyformDefinition, DyFormData dyFormData) {
        return generateDyformTitle(SpringSecurityUtils.getCurrentUserId(), dyformDefinition, dyFormData);
    }

    public static String generateDyformTitle(String starterUserId, DyFormFormDefinition dyformDefinition,
                                             DyFormData dyFormData, String titleExpression) {
        titleExpression = getFinalTitleExpression(titleExpression);
        // 表单内置变量
        Map<String, Object> allData = getDyformCommonVariables(starterUserId, dyformDefinition);
        // 表单变量
        titleExpression = addFormVariables(titleExpression, allData, dyFormData);
        // 系统参数
        titleExpression = addSysParamVariables(titleExpression, allData);

        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        String title = StringUtils.EMPTY;
        try {
            title = templateEngine.process(titleExpression, allData);
        } catch (Exception ex) {
            LOG.error(titleExpression);
            LOG.error(JsonUtils.object2Json(allData));
            LOG.error("表单标题解析错误：", ex);
        }
        return title;
    }

    public static String generateDyformTitle(String starterUserId, DyFormFormDefinition dyformDefinition,
                                             DyFormData dyFormData) {
        return generateDyformTitle(starterUserId, dyformDefinition, dyFormData, dyformDefinition.doGetTitleExpression());
    }

    /**
     * 表单内置变量
     *
     * @param starterUserId
     * @param flowDefinition
     * @return
     */
    private static Map<String, Object> getDyformCommonVariables(String starterUserId,
                                                                DyFormFormDefinition flowDefinition) {
        Map<String, Object> commonVariables = AbstractTitleGenerate.getCommonVariables();
        commonVariables.put("表单名称", flowDefinition.getName());
        commonVariables.put("表单ID", flowDefinition.getId());
        commonVariables.put("表单编号", flowDefinition.getCode());
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String starterUserName = userDetails.getUserName();
        String jobPath = null;
        String deptId = null;
        String deptPathName = null;
        OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
        if (StringUtils.equals(userDetails.getUserId(), starterUserId)) {
            UserSystemOrgDetails userSystemOrgDetails = userDetails.getUserSystemOrgDetails();
            UserSystemOrgDetails.OrgDetail orgDetail = userSystemOrgDetails.currentSystemOrgDetail();
            if (orgDetail != null) {
                OrgTreeNodeDto mainJob = orgDetail.getMainJob();
                jobPath = mainJob != null ? mainJob.getEleIdPath() : StringUtils.EMPTY;
                deptId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.DEPARTMENT.getValue());
                deptPathName = mainJob != null ? mainJob.getDeptNamePath() : StringUtils.EMPTY;
            } else {
                starterUserName = orgFacadeService.getNameByOrgEleIds(Lists.newArrayList(starterUserId)).get(starterUserId);
                List<OrgUserJobDto> userJobDtos = orgFacadeService.listUserJobs(starterUserId);
                OrgUserJobDto mainJob = userJobDtos.stream().filter(job -> job.isPrimary()).findFirst()
                        .orElse(CollectionUtils.isNotEmpty(userJobDtos) ? userJobDtos.get(0) : null);
                jobPath = mainJob != null ? mainJob.getJobIdPath() : StringUtils.EMPTY;
                deptId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.DEPARTMENT.getValue());
                deptPathName = getUserDepartmentPath(userJobDtos);
            }
        }

        commonVariables.put("当前登录用户姓名", starterUserName);
        if (StringUtils.isNotBlank(deptId)) {
            String deptName = orgFacadeService.getNameByOrgEleIds(Lists.newArrayList(deptId)).get(deptId);
            commonVariables.put("当前登录用户所在部门名称", deptName);
        } else {
            commonVariables.put("当前登录用户所在部门名称", "");
        }
        commonVariables.put("当前登录用户所在部门名称全路径", deptPathName);

//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        OrgUserVo userVo = orgApiFacade.getUserVoById(starterUserId);
//        commonVariables.put("当前登录用户姓名", starterUserName);
//        String jobPath = userVo.getMainJobIdPath();
//        String depId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.DEPARTMENT.getValue());
//        if (StringUtils.isNotBlank(depId)) {
//            MultiOrgElement depEle = orgApiFacade.getOrgElementById(depId);
//            commonVariables.put("当前登录用户所在部门名称", depEle == null ? "" : depEle.getName());
//        } else {
//            commonVariables.put("当前登录用户所在部门名称", "");
//        }
//        String departmentPath = orgApiFacade.getDepartmentNamePathByJobIdPath(userVo.getMainJobIdPath(), false);
//        commonVariables.put("当前登录用户所在部门名称全路径", departmentPath);
        return commonVariables;
    }

    /**
     * @param userJobDtos
     * @return
     */
    private static String getUserDepartmentPath(List<OrgUserJobDto> userJobDtos) {
        String deptName = StringUtils.EMPTY;
        if (CollectionUtils.isEmpty(userJobDtos)) {
            return deptName;
        }

        OrgUserJobDto userJob = userJobDtos.stream().filter(job -> job.isPrimary()).findFirst().orElse(userJobDtos.get(0));
        List<String> idPaths = Arrays.asList(StringUtils.split(userJob.getJobIdPath(), Separator.SLASH.getValue()));
        List<String> namePaths = Arrays.asList(StringUtils.split(userJob.getJobNamePath(), Separator.SLASH.getValue()));
        if (CollectionUtils.size(idPaths) != CollectionUtils.size(namePaths)) {
            return deptName;
        }

        for (int index = idPaths.size() - 1; index >= 0; index--) {
            String deptId = idPaths.get(index);
            if (StringUtils.startsWith(deptId, IdPrefix.DEPARTMENT.getValue())) {
                Object[] deptNamePaths = ArrayUtils.subarray(namePaths.toArray(new String[0]), 0, index + 1);
                return StringUtils.join(deptNamePaths, Separator.SLASH.getValue());
            }
        }
        return deptName;
    }

    /**
     * @param titleExpression
     * @param allData
     * @param dyFormData
     */
    public static String addFormVariables(String titleExpression, Map<String, Object> allData, DyFormData dyFormData) {
        Map<String, Object> form = Maps.newHashMap();
        Map<String, Object> dyformMainData = dyFormData.getFormDataOfMainform();
        if (dyformMainData != null) {
            form.putAll(dyformMainData);
            for (String f : dyformMainData.keySet()) {
                try {
                    if (dyFormData.isFileField(f) && dyformMainData.get(f) != null) {
                        Object files = dyformMainData.get(f);
                        if (files instanceof List && false == ((List) files).isEmpty()) {
                            form.put(f + "的所有文件名", dyFormData.getFileNamesOfField(files));
                        } else if (files instanceof Map && false == ((Map) files).isEmpty()) {
                            form.put(f + "的所有文件名", dyFormData.getFileNamesOfField(files));
                        }
                    }
                } catch (Exception e) {
                }

            }
        }
        TitleExpression express = new TitleExpression(titleExpression);
        String ch = null;
        while (express.hasNext()) {
            ch = express.next();
            if (ch.indexOf("#") == 0) {
                String ts = ch.substring(1);
                String[] t = ts.split("#");
                if (t.length == 2) {
                    String tt = fetchLetter(ch);
                    titleExpression = titleExpression.replace(ch, tt);
                    List<Map<String, Object>> subFormDatas = dyFormData.getFormDatasById(t[0]);
                    String value = "";
                    if (subFormDatas != null && subFormDatas.size() != 0) {
                        Object o = subFormDatas.get(0).get(t[1]);
                        if (o != null) {
                            value = String.valueOf(o);
                        }
                    }
                    form.put(tt, value);
                }
            }
        }
        allData.put("dyform", form);
        return titleExpression;
    }

    /**
     * @param titleExpression
     * @param allData
     */
    public static String addSysParamVariables(String titleExpression, Map<String, Object> allData) {
        Map<String, Object> sys = Maps.newHashMap();
        TitleExpression express = new TitleExpression(titleExpression);
        SysParamItemConfigMgr sysParamItemConfigMgr = ApplicationContextHolder.getBean(SysParamItemConfigMgr.class);
        while (express.hasNext()) {
            String ch = express.next(), key, value;
            if (StringUtils.startsWith(ch, "sys.")) {
                key = ch.replace("sys.", "");
                String finalKey = key.replaceAll("\\.", "_");// .为freemark关键字
                titleExpression = titleExpression.replace(key, finalKey);
                value = sysParamItemConfigMgr.getValueByKey(key);
                sys.put(finalKey, value);
            }
        }
        allData.put("sys", sys);
        return titleExpression;
    }
}
