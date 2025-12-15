package com.wellsoft.pt.message.web;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.message.entity.MessageInbox;
import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.processor.MessageProcessor;
import com.wellsoft.pt.message.server.impl.JmsMessageConsumer;
import com.wellsoft.pt.message.service.*;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageBean;
import com.wellsoft.pt.message.websocket.service.MsgHeaders;
import com.wellsoft.pt.message.websocket.service.MsgPayLoad;
import com.wellsoft.pt.message.websocket.service.MsgTypeEnum;
import com.wellsoft.pt.message.websocket.service.WebSocketService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-25.1	Administrator		2013-5-25		Create
 * </pre>
 * @date 2013-5-25
 */
@Controller
@RequestMapping("/message/content")
public class MessageContentController extends BaseController {
    @Autowired
    JmsMessageConsumer JmsMessageConsumer;
    @Autowired
    private MessageInboxService messageInboxService;
    @Autowired
    private MessageOutboxService messageOutboxService;
    @Autowired
    private MessageEventService messageEventService;
    @Autowired
    private OnlineMessageService onlineMessageService;
    @Autowired
    private ShortMessageService shortMessageService;

    /**
     * @return
     */
    @RequestMapping(value = "/count")
    @ResponseBody
    public String count() {
        Map<String, String> pollMap = new HashMap<String, String>();
        String userId = SpringSecurityUtils.getCurrentUserId();
        Long unreadCount = messageInboxService.getOnlineMessageCount("receive", userId, "false");
        // 收件箱未读邮件数/收件箱邮件数
        pollMap.put("unread", unreadCount + "");
        return JSONObject.fromObject(pollMap).toString();
    }

    /**
     * 打开消息详情
     *
     * @param uuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/openmessage")
    public String openMessageContent(@RequestParam("uuid") String uuid, Model model) {
        MessageInbox mc = messageInboxService.openMessageInbox(uuid);
        // if (mc.getSender().equals(SpringSecurityUtils.getCurrentUserId())) {
        // model.addAttribute("type", "send");
        // } else if
        // (mc.getRecipient().equals(SpringSecurityUtils.getCurrentUserId())) {
        model.addAttribute("type", "receive");
        // }
        model.addAttribute("userId_cur", mc.getRecipient());
        model.addAttribute("userName_cur", mc.getRecipientName());
        model.addAttribute("mc", mc);
        try {
            if (mc.getMessageParm() != null) {
                model.addAttribute("messageParm",
                        IOUtils.toString(mc.getMessageParm().getCharacterStream()));
            } else {
                model.addAttribute("messageParm", "");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return forward("/message/messageDetail");
    }

    /**
     * 打开消息详情
     *
     * @param uuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/openOutboxMessage")
    public String openOutboxMessage(@RequestParam("uuid") String uuid, Model model) {
        MessageOutbox mc = messageOutboxService.openMessageOutbox(uuid);
        // if (mc.getSender().equals(SpringSecurityUtils.getCurrentUserId())) {
        model.addAttribute("type", "send");
        model.addAttribute("userId_cur", mc.getSender());
        model.addAttribute("userName_cur", mc.getSenderName());
        // } else if
        // (mc.getRecipient().equals(SpringSecurityUtils.getCurrentUserId())) {
        // model.addAttribute("type", "receive");
        // }
        model.addAttribute("mc", mc);
        return forward("/message/messageDetail");
    }

    /**
     * 全部置已读
     *
     * @param uuids
     * @return
     */
    @RequestMapping(value = "/read")
    @ResponseBody
    public ApiResult readMessageContent(@RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            System.out.println("当前未读消息uuid为：" + uuid);
            messageInboxService.saveRead(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 全部置未读
     *
     * @param uuids
     * @return
     */
    @RequestMapping(value = "/unread")
    @ResponseBody
    public ApiResult unreadMessageContent(@RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            System.out.println("当前已读消息uuid为：" + uuid);
            messageInboxService.saveUnread(uuid);
        }
        return ApiResult.success();
    }

    @RequestMapping(value = "/retractMessage")
    @ResponseBody
    public ApiResult retractMessageContent(@RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            System.out.println("当前已读消息uuid为：" + uuid);
            messageOutboxService.retractMessage(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 全部置未读
     *
     * @param uuids
     * @return
     */
    @RequestMapping(value = "/markoutboxflag")
    @ResponseBody
    public ApiResult markOutboxflag(@RequestParam("uuids") String[] uuids,
                                    @RequestParam("markflag") String markflag) {
        for (String uuid : uuids) {
            System.out.println("当前消息uuid为：" + uuid);
            messageOutboxService.updateMarkFlag(uuid, markflag);
        }
        return ApiResult.success();
    }

    /**
     * 全部置未读
     *
     * @param uuids
     * @return
     */
    @RequestMapping(value = "/markInboxflag")
    @ResponseBody
    public ApiResult markinboxflag(@RequestParam("uuids") String[] uuids,
                                   @RequestParam("markflag") String markflag) {
        for (String uuid : uuids) {
            System.out.println("当前消息uuid为：" + uuid);
            messageInboxService.updateMarkFlag(uuid, markflag);
        }
        return ApiResult.success();
    }

    /**
     * 删除发件箱数据
     *
     * @param uuids
     * @return
     */
    @RequestMapping(value = "/deleteOutboxMessage")
    @ResponseBody
    public ApiResult deleteOutboxMessage(@RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            System.out.println("当前删除发件箱消息uuid为：" + uuid);
            messageOutboxService.deleteMessage(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 删除收件箱数据
     *
     * @param uuids
     * @return
     */
    @RequestMapping(value = "/deleteInboxMessage")
    @ResponseBody
    public ApiResult deleteInboxMessage(@RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            //System.out.println("当前删除发件箱消息uuid为：" + uuid);
            messageInboxService.deleteMessage(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 打开发送消息页面
     *
     * @return
     */
    @RequestMapping(value = "/sendmessage")
    public String sendMessageContent() {
        return forward("/message/sendMessage");
    }

    /**
     * 提交立场
     *
     * @param message
     * @param viewpoint
     * @param uuid
     */
    @RequestMapping(value = "/submitViewPoint")
    @ResponseBody
    public ApiResult submitViewPoint(@RequestParam("message") String message,
                                     @RequestParam("viewpoint") String viewpoint,
                                     @RequestParam("msgnote") String msgnote,
                                     @RequestParam("uuid") String uuid,
                                     @RequestParam("viewpointText") String viewpointText) {
        MessageInbox mc = messageInboxService.getMessageInbox(uuid);
        mc.setViewpoint(viewpointText);
        mc.setNote(msgnote);
        messageInboxService.saveMessageInbox(mc);
        messageEventService.exeServerEventInstance(message, viewpoint, msgnote);
        return ApiResult.success();
    }

    /**
     * 发送消息
     *
     * @param messageBean
     */
    @RequestMapping(value = "/submitmessage")
    @ResponseBody
    public ApiResult submitMessage(@RequestBody MessageBean messageBean) {
        String userId = messageBean.getUserId();
        String type[] = messageBean.getType();
        String body = messageBean.getBody();
        String subject = messageBean.getSubject();
        String markflag = messageBean.getMarkflag();
        String[] messageAttach = messageBean.getMessageAttach();
        String relatedUrl = messageBean.getRelatedUrl();
        String relatedTitle = messageBean.getRelatedTitle();
        String showUser = messageBean.getShowUser();
        String[] uid = userId.split(";");
        String[] uname = null;
        if (showUser != null) {
            uname = showUser.split(";");
        }
        List<String> list = Arrays.asList(uid);// 转化为id
        List<String> uNamelist = Arrays.asList(uname);// 转化为id
        Message msg = new Message();
        msg.setBody(body);
        msg.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        msg.setRecipientNames(uNamelist);
        msg.setSubject(subject);
        msg.setMessageLevel(markflag);
        msg.setRelatedUrl(relatedUrl);
        msg.setRelatedUrl(relatedTitle);
        msg.setSystem(RequestSystemContextPathResolver.system());
        msg.setTenant(SpringSecurityUtils.getCurrentTenantId());
        StringBuilder fieldsBuilder = new StringBuilder();
        for (String field : messageAttach) {
            fieldsBuilder.append(field).append(Separator.COMMA.getValue());
        }
        if (StringUtils.isNotEmpty(fieldsBuilder.toString())) {
            msg.setAttach(fieldsBuilder.substring(0, fieldsBuilder.length() - 1));
        }
        onlineMessageService.send(MessageTemplate.SYSTEM_DEFAULT_MESSAGE, msg, list, msg);
        return ApiResult.success();
    }

    private void doProcess(Class<? extends MessageProcessor> processor, Message message) {
        try {
            ApplicationContextHolder.getBean(processor).doProcessor(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 查看短信收发情况
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/lookmsg")
    public String doLookMsg(Model model) {
        return forward("/pt/message/lookmsg");
    }

    /**
     * mas设置
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/mas")
    public String maxConfig(Model model) {
        return forward("/pt/message/mas");
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test(Model model) {
        // JmsMessageConsumer.receiveMessage();
//        MessageClientApiFacadeImpl messageClientApiFacade = ApplicationContextHolder
//                .getBean(MessageClientApiFacadeImpl.class);
//        messageClientApiFacade.sendSmsMessages("1", "13950065425", "ccewcew", null, null, null,
//                false);


        MsgPayLoad payLoad = new MsgPayLoad(MsgTypeEnum.inboxOffLine, "【测试消息接口】");
        MsgHeaders msgHeaders = new MsgHeaders(new MsgPayLoad(MsgTypeEnum.inboxOffLine, "测试"));

        ApplicationContextHolder.getBean(WebSocketService.class).sendToUser("U_165841090203615232", payLoad, msgHeaders);
        return "执行完毕！";
    }

    /**
     * 接受云mas推送状态报告
     */
    @RequestMapping(value = "/cloudMasReport")
    @ResponseBody
    public ApiResult cloudMasReport(HttpServletRequest request) {
        // 读取请求内容
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String reqBody = sb.toString();
            shortMessageService.cloudMasReport(reqBody);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
        return ApiResult.success();
    }
}
