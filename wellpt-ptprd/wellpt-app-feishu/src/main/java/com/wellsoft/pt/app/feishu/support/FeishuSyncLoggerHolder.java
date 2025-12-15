/*
 * @(#)3/28/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.feishu.entity.FeishuDeptEntity;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogDetailEntity;
import com.wellsoft.pt.app.feishu.entity.FeishuSyncLogEntity;
import com.wellsoft.pt.app.feishu.entity.FeishuUserEntity;
import com.wellsoft.pt.app.feishu.service.FeishuSyncLogService;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
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
 * 3/28/25.1	    zhulh		3/28/25		    Create
 * </pre>
 * @date 3/28/25
 */
public class FeishuSyncLoggerHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeishuEventHoler.class);

    private static final ThreadLocal<FeishuSyncLogger> feishuSyncLoggerHolder = new NamedThreadLocal<>("FeishuSyncLogger");

    public static void create(FeishuConfigVo feishuConfigVo) {
        FeishuSyncLogger syncLogger = new FeishuSyncLogger();
        FeishuSyncLogEntity syncLogEntity = syncLogger.getFeishuSyncLog();
        syncLogEntity.setConfigUuid(feishuConfigVo.getUuid());
        syncLogEntity.setSyncContent(getSyncContent(feishuConfigVo));
        syncLogEntity.setSyncType("1");
        syncLogEntity.setSyncTime(Calendar.getInstance().getTime());
        syncLogEntity.setSyncStatus(1);
        syncLogEntity.setSystem(feishuConfigVo.getSystem());
        syncLogEntity.setTenant(feishuConfigVo.getTenant());
        feishuSyncLoggerHolder.set(syncLogger);
    }

    private static String getSyncContent(FeishuConfigVo feishuConfigVo) {
        FeishuConfigVo.FeishuOrgSyncOption orgSyncOption = feishuConfigVo.getConfiguration().getOrgSyncOption();
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
        if (orgSyncOption.isUserGender()) {
            contents.add("性别");
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
        return StringUtils.join(contents, ";");
    }

    public static void orgVersion(OrgVersionEntity orgVersionEntity) {
        OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
        OrganizationEntity organizationEntity = orgFacadeService.getOrganizationByOrgVersionId(orgVersionEntity.getId());
        FeishuSyncLogEntity feishuSyncLog = feishuSyncLoggerHolder.get().getFeishuSyncLog();

        feishuSyncLog.setOrgVersionUuid(orgVersionEntity.getUuid());
        feishuSyncLog.setOrgUuid(orgVersionEntity.getOrgUuid());
        feishuSyncLog.setOrgName(organizationEntity.getName());
    }

    public static void addDept(FeishuDeptEntity feishuDeptEntity) {
        FeishuSyncLogDetailEntity detailEntity = new FeishuSyncLogDetailEntity();
        detailEntity.setTargetOperationType("add");
        detailEntity.setTargetTable("feishu_dept");
        detailEntity.setTargetUuid(feishuDeptEntity.getUuid());
        detailEntity.setTargetName("飞书部门表");
        detailEntity.setTargetData(JsonUtils.object2Json(feishuDeptEntity));
        detailEntity.setSyncStatus(1);

        feishuSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void updateDept(FeishuDeptEntity feishuDeptEntity) {
        FeishuSyncLogDetailEntity detailEntity = new FeishuSyncLogDetailEntity();
        detailEntity.setTargetOperationType("update");
        detailEntity.setTargetTable("feishu_dept");
        detailEntity.setTargetUuid(feishuDeptEntity.getUuid());
        detailEntity.setTargetName("飞书部门表");
        detailEntity.setTargetData(JsonUtils.object2Json(feishuDeptEntity));
        detailEntity.setSyncStatus(1);

        feishuSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void addUser(FeishuUserEntity feishuUserEntity) {
        FeishuSyncLogDetailEntity detailEntity = new FeishuSyncLogDetailEntity();
        detailEntity.setTargetOperationType("add");
        detailEntity.setTargetTable("feishu_user");
        detailEntity.setTargetUuid(feishuUserEntity.getUuid());
        detailEntity.setTargetName("飞书用户表");
        detailEntity.setTargetData(JsonUtils.object2Json(feishuUserEntity));
        detailEntity.setSyncStatus(1);

        feishuSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void updateUser(FeishuUserEntity feishuUserEntity) {
        FeishuSyncLogDetailEntity detailEntity = new FeishuSyncLogDetailEntity();
        detailEntity.setTargetOperationType("update");
        detailEntity.setTargetTable("feishu_user");
        detailEntity.setTargetUuid(feishuUserEntity.getUuid());
        detailEntity.setTargetName("飞书用户表");
        detailEntity.setTargetData(JsonUtils.object2Json(feishuUserEntity));
        detailEntity.setSyncStatus(1);

        feishuSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void error(String errorMsg) {
        FeishuSyncLogEntity syncLogEntity = feishuSyncLoggerHolder.get().getFeishuSyncLog();
        syncLogEntity.setErrorMessage(errorMsg);
        syncLogEntity.setSyncStatus(0);
    }

    public static boolean commit() {
        boolean success = true;
        try {
            FeishuSyncLogService feishuSyncLogService = ApplicationContextHolder.getBean(FeishuSyncLogService.class);
            FeishuSyncLogger syncLogger = feishuSyncLoggerHolder.get();
            feishuSyncLogService.saveLog(syncLogger.getFeishuSyncLog(), syncLogger.getDetails());
        } catch (Exception e) {
            LOGGER.error("FeishuSyncLoggerHolder commit error", e);
            success = false;
        }
        feishuSyncLoggerHolder.remove();
        return success;
    }

    private static final class FeishuSyncLogger extends BaseObject {
        private FeishuSyncLogEntity feishuSyncLog = new FeishuSyncLogEntity();

        private List<FeishuSyncLogDetailEntity> details = Lists.newArrayList();

        /**
         * @return the feishuSyncLog
         */
        public FeishuSyncLogEntity getFeishuSyncLog() {
            return feishuSyncLog;
        }

        /**
         * @param feishuSyncLog 要设置的feishuSyncLog
         */
        public void setFeishuSyncLog(FeishuSyncLogEntity feishuSyncLog) {
            this.feishuSyncLog = feishuSyncLog;
        }

        /**
         * @return the details
         */
        public List<FeishuSyncLogDetailEntity> getDetails() {
            return details;
        }

        /**
         * @param details 要设置的details
         */
        public void setDetails(List<FeishuSyncLogDetailEntity> details) {
            this.details = details;
        }

        public void addDetail(FeishuSyncLogDetailEntity detailEntity) {
            this.details.add(detailEntity);
        }
    }
}
