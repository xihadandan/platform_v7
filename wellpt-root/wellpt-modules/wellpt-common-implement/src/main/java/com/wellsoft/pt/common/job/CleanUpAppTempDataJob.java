/*
 * @(#)2016年3月21日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.job;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.log.entity.UserOperationLog;
import com.wellsoft.pt.log.service.UserOperationLogService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.util.Calendar;

/**
 * Description: 清理应用临时文件任务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月21日.1	zhulh		2016年3月21日		Create
 * </pre>
 * @date 2016年3月21日
 */
public class CleanUpAppTempDataJob {
    private static Logger LOG = LoggerFactory.getLogger(CleanUpAppTempDataJob.class);

    private static String KEY_CLEAN_UP = "clean.up.app.temp.data";
    private static String KEY_DIRS = "app.temp.data.dirs";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.task.job.Job#execute(org.quartz.JobExecutionContext, com.wellsoft.pt.task.job.JobData)
     */
    public static void execute() {
        // 不启用删除临时文件，直接返回
        if (!Config.TRUE.equalsIgnoreCase(Config.getValue(KEY_CLEAN_UP, Config.FALSE))) {
            return;
        }
        // 获取最近一天的时间点
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long lastModified = calendar.getTimeInMillis();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String[] dirs = StringUtils.split(Config.getValue(KEY_DIRS, ""), Separator.COMMA.getValue());
        for (String dir : dirs) {
            String tempDir = StringUtils.trim(dir);
            if (StringUtils.isBlank(tempDir)) {
                continue;
            }
            try {
                File file = new File(tempDir);
                String pattern = file.getAbsolutePath();
                File parent = file.getParentFile();
                for (File tmpFile : parent.listFiles()) {
                    // 删除最近一天的文件
                    if (lastModified < tmpFile.lastModified()) {
                        continue;
                    }

                    String path = tmpFile.getAbsolutePath();
                    if (antPathMatcher.match(pattern, path)) {
                        deleteFile(tmpFile);
                    }
                }
            } catch (Exception e) {
                LOG.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    /**
     * @param file
     */
    private static void deleteFile(File file) {
        // 删除目录
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tmp : files) {
                deleteFile(tmp);
            }
            log("Delete app temp data directory: " + file.getAbsolutePath());
            file.delete();
        }
        // 删除文件
        if (file.isFile()) {
            log("Delete app temp data file: " + file.getAbsolutePath());
            file.delete();
        }
    }

    /**
     * @param message
     */
    private static void log(String message) {
        // 记录日志
        LOG.error(message);
        // 保存数据库
        UserOperationLog log = new UserOperationLog();
        log.setModuleId(ModuleID.BASIC_DATA.getValue());
        log.setModuleName("基本数据");
        log.setContent(message);
        log.setOperation("清理应用临时文件");
        log.setUserName(SpringSecurityUtils.getCurrentUserName());
        log.setDetails(message);
        UserOperationLogService userOperationLogService = ApplicationContextHolder
                .getBean(UserOperationLogService.class);
        userOperationLogService.save(log);
    }

}
