package com.wellsoft.pt.app.dingtalk.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.context.web.process.Process;
import com.wellsoft.pt.app.dingtalk.constants.DingtalkInfo;
import com.wellsoft.pt.app.dingtalk.enums.EnumCallBackEventType;
import com.wellsoft.pt.app.dingtalk.facade.DingtalkOrgSyncApi;
import com.wellsoft.pt.app.dingtalk.service.EventCallBackService;
import com.wellsoft.pt.app.dingtalk.service.MultiDeptUserAuditService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingUserService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgSyncLogService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiUtils;
import com.wellsoft.pt.app.dingtalk.vo.MultiDeptUserAuditVo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description: 钉钉接口controller
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
@Api(tags = "钉钉接入模块")
@Controller
@RequestMapping("pt/dingtalk")
@Deprecated
public class DingtalkMgrController extends BaseController {

    private static AtomicBoolean doing = new AtomicBoolean(false);

    @Autowired
    private DingtalkConfig dingtalkConfig;

    @Autowired
    private MultiOrgDingUserService multiOrgDingUserService;

    @Autowired
    private MultiOrgSyncLogService multiOrgSyncLogService;

    @Autowired
    private EventCallBackService eventCallBackService;

    @Autowired
    private MultiDeptUserAuditService multiDeptUserAuditService;

    /**
     * 部门同步
     *
     * @param request
     * @param response
     * @return
     */
    @Process
    @ResponseBody
    @RequestMapping(value = "/syncOrg")
    public ResultMessage syncOrg(HttpServletRequest request, HttpServletResponse response) {
        ResultMessage resultMsg = new ResultMessage("");
        if (StringUtils.equals(SpringSecurityUtils.getCurrentUserUnitId(), dingtalkConfig.getSystemUnitId())) {
            Map<String, Object> resultObj = Maps.newHashMap();
            if (doing.compareAndSet(false, true)) {
                try {
                    Integer state = ApplicationContextHolder.getBean(DingtalkOrgSyncApi.class).syncOrgFromDingtalk();
                    if (state.intValue() == DingtalkInfo.SYNC_STATUS_SUCCESS) {
                        resultObj.put("code", 1);// 同步成功
                    } else {
                        resultObj.put("code", 0);// 同步异常
                    }
                } catch (Exception ex) {
                    resultObj.put("code", 0);
                    resultObj.put("errmsg", ex.getMessage());
                    logger.error(ex.getMessage(), ex);
                    resultMsg.setSuccess(false);
                    resultMsg.addMessage(ex.getMessage());
                } finally {
                    resultObj.put("time", DateUtils.formatDateTimeMin(new Date()));
                    dingtalkConfig.saveValue("org.sync_last_message", JsonUtils.object2Json(resultObj));
                    doing.set(false);
                }
            } else {
                resultMsg.addMessage("还在同步中...");
            }
        } else {
            resultMsg.setSuccess(false);
            resultMsg.addMessage("[系统单位]请选择本单位进行同步");
        }
        return resultMsg;
    }

    /**
     * 重新同步单条异常的数据
     *
     * @param logId 日志主键id
     * @param type  类型：1 部门 2 人员 3 人员和职位关系
     * @return
     */
    @ApiOperation("钉钉接入/同步日志/重新同步")
    @ResponseBody
    @RequestMapping(value = "/syncOneData")
    public ResultMessage syncOneData(@RequestParam(value = "logId", required = true) String logId, @RequestParam(value = "type", required = true) String type) {
        ResultMessage resultMessage = new ResultMessage();
        Integer state = ApplicationContextHolder.getBean(DingtalkOrgSyncApi.class).syncOneData(logId, type);
        Map<String, Object> resultObj = Maps.newHashMap();
        if (state.intValue() == DingtalkInfo.SYNC_STATUS_SUCCESS) {
            resultObj.put("code", 1);
        } else {
            resultObj.put("code", 0);
        }
        resultMessage.setSuccess(true);
        resultMessage.setData(resultObj);
        return resultMessage;
    }

    /**
     * 重新同步 业务事件 数据
     *
     * @param logId 日志主键id
     * @return
     */
    @ApiOperation("钉钉接入/业务事件同步日志/重新同步")
    @ResponseBody
    @RequestMapping(value = "/syncEventData")
    public ResultMessage syncEventData(@RequestParam(value = "logId", required = true) String logId) {
        ResultMessage resultMessage = new ResultMessage();
        Integer state = ApplicationContextHolder.getBean(DingtalkOrgSyncApi.class).syncEventData(logId);
        Map<String, Object> resultObj = Maps.newHashMap();
        if (state.intValue() == DingtalkInfo.SYNC_STATUS_SUCCESS) {
            resultObj.put("code", 1);
        } else {
            resultObj.put("code", 0);
        }
        resultMessage.setSuccess(true);
        resultMessage.setData(resultObj);
        return resultMessage;
    }

    @ResponseBody
    @RequestMapping(value = "/syncOrgStatue")
    public ResultMessage syncOrgStatue(HttpServletRequest request, HttpServletResponse response) {
        ResultMessage resultMsg = new ResultMessage();
        // 0 同步异常 1 同步成功 2 同步中
        resultMsg.setData(doing.get() ? "{\"code\":2}" : dingtalkConfig.getValue("org.sync_last_message"));
        return resultMsg;
    }

    /**
     * 执行回调事件
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/executeCallBack")
    public ResultMessage executeCallBack(HttpServletRequest request, HttpServletResponse response) {
        ResultMessage resultMsg = new ResultMessage();
        try {
            ApplicationContextHolder.getBean(DingtalkOrgSyncApi.class).executeCallBack();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            resultMsg.setSuccess(false);
            resultMsg.addMessage(ex.getMessage());
        }
        return resultMsg;
    }

    /**
     * 执行待办推送
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/executeWorkRecord")
    public ResultMessage executeWorkRecord(HttpServletRequest request, HttpServletResponse response) {
        ResultMessage resultMsg = new ResultMessage();
        try {
            DingtalkApiUtils.executeWorkRecord();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            resultMsg.setSuccess(false);
            resultMsg.addMessage(ex.getMessage());
        }
        return resultMsg;
    }

    /**
     * @param
     */
    private List<String> getDefaultCallBackTags() {
        List<String> backTags = new ArrayList<>();
        backTags.add(EnumCallBackEventType.user_active_org.getValue());
        backTags.add(EnumCallBackEventType.user_add_org.getValue());
        backTags.add(EnumCallBackEventType.user_leave_org.getValue());
        backTags.add(EnumCallBackEventType.user_modify_org.getValue());
        // backTags.add(EnumCallBackEventType.org_admin_add.getValue());
        // backTags.add(EnumCallBackEventType.org_admin_remove.getValue());
        backTags.add(EnumCallBackEventType.org_dept_create.getValue());
        backTags.add(EnumCallBackEventType.org_dept_modify.getValue());
        backTags.add(EnumCallBackEventType.org_dept_remove.getValue());
//		backTags.add(EnumCallBackEventType.label_user_change.getValue());
//		backTags.add(EnumCallBackEventType.label_conf_add.getValue());
//		backTags.add(EnumCallBackEventType.label_conf_del.getValue());
//		backTags.add(EnumCallBackEventType.label_conf_modify.getValue());
        return backTags;
    }

    /**
     * 注册业务事件回调接口
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/registerCallBack")
    public ResultMessage registerCallBack(HttpServletRequest request, HttpServletResponse response) {
        String callBackTag = request.getParameter("call_back_tag");
        ResultMessage resultMsg = new ResultMessage();
        try {
            List<String> backTags;
            if (StringUtils.isBlank(callBackTag)) {
                backTags = getDefaultCallBackTags();
            } else {
                backTags = JSONArray.fromObject(callBackTag);
            }
            resultMsg.setData(DingtalkApiUtils.registerCallBack(backTags, DingtalkApiUtils.getAccessToken()));
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            resultMsg.setSuccess(false);
            resultMsg.addMessage(ex.getMessage());
        }
        return resultMsg;
    }

    @ResponseBody
    @RequestMapping(value = "/updateCallBack")
    public ResultMessage updateCallBack(HttpServletRequest request, HttpServletResponse response) {
        String callBackTag = request.getParameter("call_back_tag");
        ResultMessage resultMsg = new ResultMessage();
        try {
            List<String> backTags;
            if (StringUtils.isBlank(callBackTag)) {
                backTags = getDefaultCallBackTags();
            } else {
                backTags = JSONArray.fromObject(callBackTag);
            }
            resultMsg.setData(DingtalkApiUtils.updateCallBack(backTags, DingtalkApiUtils.getAccessToken()));
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            resultMsg.setSuccess(false);
            resultMsg.addMessage(ex.getMessage());
        }
        return resultMsg;
    }

    /**
     * 查询事件回调接口
     *
     * @param request
     * @param response
     * @return
     */
    @Process
    @ResponseBody
    @RequestMapping(value = "/getCallBack")
    public ResultMessage getCallBack(HttpServletRequest request, HttpServletResponse response) {
        ResultMessage resultMsg = new ResultMessage();
        try {
            resultMsg.setData(DingtalkApiUtils.getCallBack(DingtalkApiUtils.getAccessToken()));
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            resultMsg.setSuccess(false);
            resultMsg.addMessage(ex.getMessage());
        }
        return resultMsg;
    }

    /**
     * 删除事件回调接口
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteCallBack")
    public ResultMessage deleteCallBack(HttpServletRequest request, HttpServletResponse response) {
        ResultMessage resultMsg = new ResultMessage();
        try {
            resultMsg.setData(DingtalkApiUtils.deleteCallBack(DingtalkApiUtils.getAccessToken()));
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            resultMsg.setSuccess(false);
            resultMsg.addMessage(ex.getMessage());
        }
        return resultMsg;
    }

    /**
     * 获取回调失败的结果
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/syncFailedList")
    public ResultMessage syncFailedList(HttpServletRequest request, HttpServletResponse response) {
        ResultMessage resultMsg = new ResultMessage();
        try {
            resultMsg.setData(ApplicationContextHolder.getBean(DingtalkOrgSyncApi.class).syncFailedList());
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            resultMsg.setSuccess(false);
            resultMsg.addMessage(ex.getMessage());
        }
        return resultMsg;
    }

    @ApiOperation("获取钉钉接入配置/组织同步配置")
    @RequestMapping(value = "/getConfig")
    public @ResponseBody
    ResultMessage getModuleConfig() {
        return new ResultMessage(null, true, dingtalkConfig.getConfig());
    }

    @ApiOperation("更新钉钉接入配置/组织同步配置")
    @RequestMapping(value = "/saveConfig")
    public @ResponseBody
    ResultMessage saveModuleConfig(@RequestParam(value = "jsonParams") String jsonParams,
                                   HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = JSONObject.fromObject(jsonParams);
        ResultMessage resultMessage = new ResultMessage(null, true, dingtalkConfig.saveConfig(params));
        DingtalkApiUtils.invalidateAllToken();// 清理与配置相关的key
        // return registerCallBack(request, response);
        return resultMessage;
    }

    @RequestMapping(value = "/getDingIdsByPtIds")
    public @ResponseBody
    ResultMessage getDingIdsByPtIds(@RequestParam(value = "userIds") String userIds) {
        JSONArray userIdArray = JSONArray.fromObject(userIds);
        return new ResultMessage(null, true, multiOrgDingUserService.getDingIdsByPtIds(userIdArray));
    }

    @ApiOperation(value = "钉钉接入/组织同步日志/详情", notes = "根据日志uuid获取组织同步日志详情")
    @RequestMapping(value = "/getOrgSyncLogDetail")
    public @ResponseBody
    ResultMessage getOrgSyncLogDetail(@RequestParam(value = "uuid", required = true) String uuid) {
        return new ResultMessage(null, true, multiOrgSyncLogService.getMultiOrgSyncLogDetail(uuid));
    }

    @ApiOperation(value = "钉钉接入/业务事件同步日志/详情")
    @RequestMapping(value = "/getEventCallBackDetail")
    public @ResponseBody
    ResultMessage getEventCallBackDetail(@RequestParam(value = "uuid", required = true) String uuid) {
        return new ResultMessage(null, true, eventCallBackService.getEventCallBackDetail(uuid));
    }

    @ApiOperation(value = "钉钉接入/多部门人员审核列表/详情")
    @RequestMapping(value = "/getMultiDeptUserAudit")
    public @ResponseBody
    ResultMessage getMultiDeptUserAudit(@RequestParam(value = "uuid", required = true) String uuid) {
        return new ResultMessage(null, true, multiDeptUserAuditService.getMultiDeptUserAuditDetail(uuid));
    }

    @ApiOperation(value = "钉钉接入/多部门人员审核列表/详情/审核")
    @RequestMapping(value = "/multiDeptUserAudit")
    public @ResponseBody
    ResultMessage multiDeptUserAudit(@RequestParam(value = "jsonStr") String jsonStr) {
        MultiDeptUserAuditVo vo = JsonUtils.json2Object(jsonStr, MultiDeptUserAuditVo.class);
        multiDeptUserAuditService.auditRecord(vo);
        return new ResultMessage();
    }

    @ApiOperation(value = "钉钉接入/多部门人员审核列表/钉钉职位下拉列表")
    @RequestMapping(value = "/dingJobList", method = RequestMethod.GET)
    public @ResponseBody
    List<String> dingJobList() {
        return multiDeptUserAuditService.getDingJobList();
    }

    @RequestMapping(value = "/invalidateAccessToken")
    public @ResponseBody
    ResultMessage invalidateAccessToken(HttpServletRequest request, HttpServletResponse response) {
        DingtalkApiUtils.invalidateAccessToken();
        return new ResultMessage();
    }

    @RequestMapping(value = "/invalidateAllToken")
    public @ResponseBody
    ResultMessage invalidateAllToken(HttpServletRequest request, HttpServletResponse response) {
        DingtalkApiUtils.invalidateAllToken();
        return new ResultMessage();
    }

}
