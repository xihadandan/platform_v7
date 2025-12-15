/*
 * @(#)4/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.dingtalk.entity.*;
import com.wellsoft.pt.app.dingtalk.service.DingtalkSyncLogService;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.entity.OrganizationEntity;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;

import java.util.Calendar;
import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/23/25.1	    zhulh		4/23/25		    Create
 * </pre>
 * @date 4/23/25
 */
public class DingtalkSyncLoggerHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DingtalkSyncLoggerHolder.class);

    private static final ThreadLocal<DingtalkSyncLogger> dingtalkSyncLoggerHolder = new NamedThreadLocal<>("DingtalkSyncLogger");

    public static void create(DingtalkConfigVo dingtalkConfigVo) {
        DingtalkSyncLogger syncLogger = new DingtalkSyncLogger();
        DingtalkSyncLogEntity syncLogEntity = syncLogger.getDingtalkSyncLog();
        syncLogEntity.setConfigUuid(dingtalkConfigVo.getUuid());
        syncLogEntity.setSyncContent(getSyncContent(dingtalkConfigVo));
        syncLogEntity.setSyncType("1");
        syncLogEntity.setSyncTime(Calendar.getInstance().getTime());
        syncLogEntity.setSyncStatus(1);
        syncLogEntity.setSystem(dingtalkConfigVo.getSystem());
        syncLogEntity.setTenant(dingtalkConfigVo.getTenant());
        dingtalkSyncLoggerHolder.set(syncLogger);
    }

    private static String getSyncContent(DingtalkConfigVo dingtalkConfigVo) {
        DingtalkConfigVo.DingtalkOrgSyncOption orgSyncOption = dingtalkConfigVo.getConfiguration().getOrgSyncOption();
        List<String> contents = Lists.newArrayList();
        if (orgSyncOption.isDept()) {
            contents.add("部门");
        }
        if (orgSyncOption.isUserName()) {
            contents.add("姓名");
        }
        if (orgSyncOption.isUserAvatar()) {
            contents.add("头像");
        }
        if (orgSyncOption.isUserMobile()) {
            contents.add("手机号码");
        }
        if (orgSyncOption.isUserEmail()) {
            contents.add("邮箱");
        }
        if (orgSyncOption.isUserNo()) {
            contents.add("员工编号");
        }
        if (orgSyncOption.isUserRemark()) {
            contents.add("备注");
        }
        if (orgSyncOption.isJob()) {
            contents.add("职位");
        }
        return StringUtils.join(contents, ";");
    }

    public static void orgVersion(OrgVersionEntity orgVersionEntity) {
        OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
        OrganizationEntity organizationEntity = orgFacadeService.getOrganizationByOrgVersionId(orgVersionEntity.getId());
        DingtalkSyncLogEntity dingtalkSyncLog = dingtalkSyncLoggerHolder.get().getDingtalkSyncLog();

        dingtalkSyncLog.setOrgVersionUuid(orgVersionEntity.getUuid());
        dingtalkSyncLog.setOrgUuid(orgVersionEntity.getOrgUuid());
        dingtalkSyncLog.setOrgName(organizationEntity.getName());
    }

    public static void addDept(DingtalkDeptEntity dingtalkDeptEntity) {
        DingtalkSyncLogDetailEntity detailEntity = new DingtalkSyncLogDetailEntity();
        detailEntity.setTargetOperationType("add");
        detailEntity.setTargetTable("dingtalk_dept");
        detailEntity.setTargetUuid(dingtalkDeptEntity.getUuid());
        detailEntity.setTargetName("钉钉部门表");
        detailEntity.setTargetData(JsonUtils.object2Json(dingtalkDeptEntity));
        detailEntity.setSyncStatus(1);

        dingtalkSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void updateDept(DingtalkDeptEntity dingtalkDeptEntity) {
        DingtalkSyncLogDetailEntity detailEntity = new DingtalkSyncLogDetailEntity();
        detailEntity.setTargetOperationType("update");
        detailEntity.setTargetTable("dingtalk_dept");
        detailEntity.setTargetUuid(dingtalkDeptEntity.getUuid());
        detailEntity.setTargetName("钉钉部门表");
        detailEntity.setTargetData(JsonUtils.object2Json(dingtalkDeptEntity));
        detailEntity.setSyncStatus(1);

        dingtalkSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void addJob(DingtalkJobEntity dingtalkJobEntity) {
        DingtalkSyncLogDetailEntity detailEntity = new DingtalkSyncLogDetailEntity();
        detailEntity.setTargetOperationType("add");
        detailEntity.setTargetTable("dingtalk_job");
        detailEntity.setTargetUuid(dingtalkJobEntity.getUuid());
        detailEntity.setTargetName("钉钉职位表");
        detailEntity.setTargetData(JsonUtils.object2Json(dingtalkJobEntity));
        detailEntity.setSyncStatus(1);

        dingtalkSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void updateJob(DingtalkJobEntity dingtalkJobEntity) {
        DingtalkSyncLogDetailEntity detailEntity = new DingtalkSyncLogDetailEntity();
        detailEntity.setTargetOperationType("update");
        detailEntity.setTargetTable("dingtalk_job");
        detailEntity.setTargetUuid(dingtalkJobEntity.getUuid());
        detailEntity.setTargetName("钉钉职位表");
        detailEntity.setTargetData(JsonUtils.object2Json(dingtalkJobEntity));
        detailEntity.setSyncStatus(1);

        dingtalkSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void addUser(DingtalkUserEntity dingtalkUserEntity) {
        DingtalkSyncLogDetailEntity detailEntity = new DingtalkSyncLogDetailEntity();
        detailEntity.setTargetOperationType("add");
        detailEntity.setTargetTable("dingtalk_user");
        detailEntity.setTargetUuid(dingtalkUserEntity.getUuid());
        detailEntity.setTargetName("钉钉用户表");
        detailEntity.setTargetData(JsonUtils.object2Json(dingtalkUserEntity));
        detailEntity.setSyncStatus(1);

        dingtalkSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void updateUser(DingtalkUserEntity dingtalkUserEntity) {
        DingtalkSyncLogDetailEntity detailEntity = new DingtalkSyncLogDetailEntity();
        detailEntity.setTargetOperationType("update");
        detailEntity.setTargetTable("dingtalk_user");
        detailEntity.setTargetUuid(dingtalkUserEntity.getUuid());
        detailEntity.setTargetName("钉钉用户表");
        detailEntity.setTargetData(JsonUtils.object2Json(dingtalkUserEntity));
        detailEntity.setSyncStatus(1);

        dingtalkSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void error(String errorMsg) {
        DingtalkSyncLogEntity syncLogEntity = dingtalkSyncLoggerHolder.get().getDingtalkSyncLog();
        syncLogEntity.setErrorMessage(errorMsg);
        syncLogEntity.setSyncStatus(0);
    }

    public static boolean commit() {
        boolean success = true;
        try {
            DingtalkSyncLogService dingtalkSyncLogService = ApplicationContextHolder.getBean(DingtalkSyncLogService.class);
            DingtalkSyncLogger syncLogger = dingtalkSyncLoggerHolder.get();
            dingtalkSyncLogService.saveLog(syncLogger.getDingtalkSyncLog(), syncLogger.getDetails());
        } catch (Exception e) {
            LOGGER.error("FeishuSyncLoggerHolder commit error", e);
            success = false;
        }
        dingtalkSyncLoggerHolder.remove();
        return success;
    }

    private static final class DingtalkSyncLogger extends BaseObject {
        private DingtalkSyncLogEntity dingtalkSyncLog = new DingtalkSyncLogEntity();

        private List<DingtalkSyncLogDetailEntity> details = Lists.newArrayList();

        /**
         * @return the dingtalkSyncLog
         */
        public DingtalkSyncLogEntity getDingtalkSyncLog() {
            return dingtalkSyncLog;
        }

        /**
         * @param dingtalkSyncLog 要设置的dingtalkSyncLog
         */
        public void setDingtalkSyncLog(DingtalkSyncLogEntity dingtalkSyncLog) {
            this.dingtalkSyncLog = dingtalkSyncLog;
        }

        /**
         * @return the details
         */
        public List<DingtalkSyncLogDetailEntity> getDetails() {
            return details;
        }

        /**
         * @param details 要设置的details
         */
        public void setDetails(List<DingtalkSyncLogDetailEntity> details) {
            this.details = details;
        }

        public void addDetail(DingtalkSyncLogDetailEntity detailEntity) {
            this.details.add(detailEntity);
        }
    }

}
