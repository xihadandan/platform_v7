/*
 * @(#)2020年3月5日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.dms.core.support.DyFormActionData;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
import com.wellsoft.pt.dms.ext.dyform.web.action.DyFormGetAction;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月5日.1	zhulh		2020年3月5日		Create
 * </pre>
 * @date 2020年3月5日
 */
public class ActionDataCustomizer {

    /**
     * @param dyFormActionData
     * @return
     */
    public static boolean isClose(DyFormActionData dyFormActionData) {
        String closeStr = null;
        //弹窗内容，默认自动关闭
        if ("_dialog".equalsIgnoreCase(dyFormActionData.getExtraString("ep_target"))) {
            closeStr = "true";
        }
        if (StringUtils.isNotBlank(dyFormActionData.getExtraString("ep_autoClose"))
                || StringUtils.isNotBlank(dyFormActionData.getExtraString("autoClose"))) {
            // 获取autoClose参数，设置是否关闭当前窗口
            closeStr = StringUtils.defaultIfBlank(dyFormActionData.getExtraString("ep_autoClose"),
                    dyFormActionData.getExtraString("autoClose"));
        }
        return "true".equalsIgnoreCase(closeStr);
    }

    /**
     * @param dyFormActionData
     * @return
     */
    public static List<String> getTriggerEvents(DyFormActionData dyFormActionData) {
        // 触发的事件
        if (StringUtils.isNotBlank(dyFormActionData.getExtraString("ep_triggerEvents"))) {
            return Lists.newArrayList(StringUtils.split(dyFormActionData.getExtraString("ep_triggerEvents"),
                    Separator.SEMICOLON.getValue()));
        }
        return null;
    }

    /**
     * @param dyFormActionData
     * @param string
     * @return
     */
    public static String getSuccessMsg(DyFormActionData dyFormActionData, String defaultSuccessMsg) {
        // 操作成功提示信息
        if (StringUtils.isNotBlank(dyFormActionData.getExtraString("successMsg"))) {
            return dyFormActionData.getExtraString("successMsg");
        }
        return defaultSuccessMsg;
    }

    /**
     * @param listViewSelection
     * @param string
     * @return
     */
    public static String getSuccessMsg(ListViewSelection listViewSelection, String defaultSuccessMsg) {
        // 操作成功提示信息
        if (StringUtils.isNotBlank(listViewSelection.getExtraString("successMsg"))) {
            return listViewSelection.getExtraString("successMsg");
        }
        return defaultSuccessMsg;
    }

    /**
     * @param dyFormActionData
     * @param defaultSuccessMsgType
     * @return
     */
    public static String getSuccessMsgType(DyFormActionData dyFormActionData, String defaultSuccessMsgType) {
        // 操作成功提示信息类型
        if (StringUtils.isNotBlank(dyFormActionData.getExtraString("successMsgType"))) {
            return dyFormActionData.getExtraString("successMsgType");
        }
        return defaultSuccessMsgType;
    }

    /**
     * @param dyFormActionData
     * @param defaultSuccessMsgType
     * @return
     */
    public static String getSuccessMsgType(ListViewSelection listViewSelection, String defaultSuccessMsgType) {
        // 操作成功提示信息类型
        if (StringUtils.isNotBlank(listViewSelection.getExtraString("successMsgType"))) {
            return listViewSelection.getExtraString("successMsgType");
        }
        return defaultSuccessMsgType;
    }

    /**
     * @param dyFormActionData
     * @param defaultJsModule
     * @return
     */
    public static String getExecuteJsModule(DyFormActionData dyFormActionData, String defaultJsModule) {
        // 操作结果执行的JS模块
        if (StringUtils.isNotBlank(dyFormActionData.getExtraString("ep_executeJsModule"))) {
            return dyFormActionData.getExtraString("ep_executeJsModule");
        }
        return defaultJsModule;
    }

    /**
     * @param listViewSelection
     * @param defaultJsModule
     * @return
     */
    public static String getExecuteJsModule(ListViewSelection listViewSelection, String defaultJsModule) {
        // 操作结果执行的JS模块
        if (StringUtils.isNotBlank(listViewSelection.getExtraString("ep_executeJsModule"))) {
            return listViewSelection.getExtraString("ep_executeJsModule");
        }
        return defaultJsModule;
    }

    /**
     * @param dyFormActionData
     * @param defaultValue
     * @return
     */
    public static boolean isRefreshParent(DyFormActionData dyFormActionData, boolean defaultValue) {
        String refreshParent = dyFormActionData.getExtraString("ep_refreshParent");
        if (StringUtils.isBlank(refreshParent)) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(refreshParent);
    }

    /**
     * @param dyFormActionData
     * @return
     */
    public static boolean isDisplayAsLabel(DyFormActionData dyFormActionData) {
        return !StringUtils.equals(DyFormGetAction.VALUE_EDIT_MODE,
                dyFormActionData.getExtraString(DyFormGetAction.KEY_VIEW_MODE));
    }

}
