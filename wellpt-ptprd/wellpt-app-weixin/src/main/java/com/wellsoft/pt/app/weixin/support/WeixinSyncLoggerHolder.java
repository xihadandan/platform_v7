/*
 * @(#)5/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.weixin.entity.WeixinDeptEntity;
import com.wellsoft.pt.app.weixin.entity.WeixinSyncLogDetailEntity;
import com.wellsoft.pt.app.weixin.entity.WeixinSyncLogEntity;
import com.wellsoft.pt.app.weixin.entity.WeixinUserEntity;
import com.wellsoft.pt.app.weixin.service.WeixinSyncLogService;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
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
 * 5/23/25.1	    zhulh		5/23/25		    Create
 * </pre>
 * @date 5/23/25
 */
public class WeixinSyncLoggerHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinSyncLoggerHolder.class);

    private static final ThreadLocal<WeixinSyncLogger> weixinSyncLoggerHolder = new NamedThreadLocal<>("WeixinSyncLogger");

    public static void create(WeixinConfigVo weixinConfigVo) {
        WeixinSyncLogger syncLogger = new WeixinSyncLogger();
        WeixinSyncLogEntity syncLogEntity = syncLogger.getWeixinSyncLog();
        syncLogEntity.setConfigUuid(weixinConfigVo.getUuid());
        syncLogEntity.setSyncContent(getSyncContent(weixinConfigVo));
        syncLogEntity.setSyncType("1");
        syncLogEntity.setSyncTime(Calendar.getInstance().getTime());
        syncLogEntity.setSyncStatus(1);
        syncLogEntity.setSystem(weixinConfigVo.getSystem());
        syncLogEntity.setTenant(weixinConfigVo.getTenant());
        weixinSyncLoggerHolder.set(syncLogger);
    }

    private static String getSyncContent(WeixinConfigVo weixinConfigVo) {
        List<String> contents = Lists.newArrayList();
        contents.add("部门");
        contents.add("用户");
        return StringUtils.join(contents, ";");
    }

    public static void orgVersion(OrgVersionEntity orgVersionEntity) {
        OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
        OrganizationEntity organizationEntity = orgFacadeService.getOrganizationByOrgVersionId(orgVersionEntity.getId());
        WeixinSyncLogEntity weixinSyncLog = weixinSyncLoggerHolder.get().getWeixinSyncLog();

        weixinSyncLog.setOrgVersionUuid(orgVersionEntity.getUuid());
        weixinSyncLog.setOrgUuid(orgVersionEntity.getOrgUuid());
        weixinSyncLog.setOrgName(organizationEntity.getName());
    }

    public static void addDept(WeixinDeptEntity weixinDeptEntity) {
        WeixinSyncLogDetailEntity detailEntity = new WeixinSyncLogDetailEntity();
        detailEntity.setTargetOperationType("add");
        detailEntity.setTargetTable("weixin_dept");
        detailEntity.setTargetUuid(weixinDeptEntity.getUuid());
        detailEntity.setTargetName("企业微信部门表");
        detailEntity.setTargetData(JsonUtils.object2Json(weixinDeptEntity));
        detailEntity.setSyncStatus(1);

        weixinSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void updateDept(WeixinDeptEntity weixinDeptEntity) {
        WeixinSyncLogDetailEntity detailEntity = new WeixinSyncLogDetailEntity();
        detailEntity.setTargetOperationType("update");
        detailEntity.setTargetTable("weixin_dept");
        detailEntity.setTargetUuid(weixinDeptEntity.getUuid());
        detailEntity.setTargetName("企业微信部门表");
        detailEntity.setTargetData(JsonUtils.object2Json(weixinDeptEntity));
        detailEntity.setSyncStatus(1);

        weixinSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void addUser(WeixinUserEntity weixinUserEntity) {
        WeixinSyncLogDetailEntity detailEntity = new WeixinSyncLogDetailEntity();
        detailEntity.setTargetOperationType("add");
        detailEntity.setTargetTable("weixin_user");
        detailEntity.setTargetUuid(weixinUserEntity.getUuid());
        detailEntity.setTargetName("企业微信用户表");
        detailEntity.setTargetData(JsonUtils.object2Json(weixinUserEntity));
        detailEntity.setSyncStatus(1);

        weixinSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void updateUser(WeixinUserEntity weixinUserEntity) {
        WeixinSyncLogDetailEntity detailEntity = new WeixinSyncLogDetailEntity();
        detailEntity.setTargetOperationType("update");
        detailEntity.setTargetTable("weixin_user");
        detailEntity.setTargetUuid(weixinUserEntity.getUuid());
        detailEntity.setTargetName("企业微信用户表");
        detailEntity.setTargetData(JsonUtils.object2Json(weixinUserEntity));
        detailEntity.setSyncStatus(1);

        weixinSyncLoggerHolder.get().addDetail(detailEntity);
    }

    public static void error(String errorMsg) {
        WeixinSyncLogEntity syncLogEntity = weixinSyncLoggerHolder.get().getWeixinSyncLog();
        syncLogEntity.setErrorMessage(errorMsg);
        syncLogEntity.setSyncStatus(0);
    }

    public static Boolean commit() {
        boolean success = true;
        try {
            WeixinSyncLogService dingtalkSyncLogService = ApplicationContextHolder.getBean(WeixinSyncLogService.class);
            WeixinSyncLogger syncLogger = weixinSyncLoggerHolder.get();
            dingtalkSyncLogService.saveLog(syncLogger.getWeixinSyncLog(), syncLogger.getDetails());
        } catch (Exception e) {
            LOGGER.error("WeixinSyncLoggerHolder commit error", e);
            success = false;
        }
        weixinSyncLoggerHolder.remove();
        return success;
    }

    private static final class WeixinSyncLogger extends BaseObject {
        private WeixinSyncLogEntity weixinSyncLog = new WeixinSyncLogEntity();

        private List<WeixinSyncLogDetailEntity> details = Lists.newArrayList();

        /**
         * @return the weixinSyncLog
         */
        public WeixinSyncLogEntity getWeixinSyncLog() {
            return weixinSyncLog;
        }

        /**
         * @param weixinSyncLog 要设置的weixinSyncLog
         */
        public void setWeixinSyncLog(WeixinSyncLogEntity weixinSyncLog) {
            this.weixinSyncLog = weixinSyncLog;
        }

        /**
         * @return the details
         */
        public List<WeixinSyncLogDetailEntity> getDetails() {
            return details;
        }

        /**
         * @param details 要设置的details
         */
        public void setDetails(List<WeixinSyncLogDetailEntity> details) {
            this.details = details;
        }

        public void addDetail(WeixinSyncLogDetailEntity detailEntity) {
            this.details.add(detailEntity);
        }
    }
}
