package com.wellsoft.pt.message.web.api;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.message.service.MessageOutboxService;
import com.wellsoft.pt.message.service.OnlineMessageService;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageBean;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 消息模块
 *
 * @Auther: yt
 * @Date: 2021/10/21 14:28
 * @Description:
 */
@Api(tags = "消息模块")
@RestController
@RequestMapping("/api/message/content")
public class ApiMessageContentController extends BaseController {

    @Autowired
    private MessageInboxService messageInboxService;
    @Autowired
    private MessageOutboxService messageOutboxService;
    @Autowired
    private OnlineMessageService onlineMessageService;

    /**
     * 设置已读
     *
     * @param uuids
     * @return
     */
    @ApiOperation(value = "设置已读", notes = "设置已读")
    @PostMapping(value = "/read")
    public ApiResult readMessageContent(@ApiParam(value = "uuid集合", required = true) @RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            System.out.println("当前未读消息uuid为：" + uuid);
            messageInboxService.saveRead(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 设置未读
     *
     * @param uuids
     * @return
     */
    @ApiOperation(value = "设置未读", notes = "设置未读")
    @PostMapping(value = "/unread")
    public ApiResult unreadMessageContent(@ApiParam(value = "uuid集合", required = true) @RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            System.out.println("当前已读消息uuid为：" + uuid);
            messageInboxService.saveUnread(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 撤回消息
     *
     * @param uuids
     * @return
     */
    @ApiOperation(value = "撤回消息", notes = "撤回消息")
    @PostMapping(value = "/retractMessage")
    public ApiResult retractMessageContent(@ApiParam(value = "uuid集合", required = true) @RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            System.out.println("当前已读消息uuid为：" + uuid);
            messageOutboxService.retractMessage(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 删除发件箱数据
     *
     * @param uuids
     * @return
     */
    @ApiOperation(value = "删除发件箱数据", notes = "删除发件箱数据")
    @PostMapping(value = "/deleteOutboxMessage")
    public ApiResult deleteOutboxMessage(@ApiParam(value = "uuid集合", required = true) @RequestParam("uuids") String[] uuids) {
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
    @ApiOperation(value = "删除收件箱数据", notes = "删除收件箱数据")
    @PostMapping(value = "/deleteInboxMessage")
    public ApiResult deleteInboxMessage(@ApiParam(value = "uuid集合", required = true) @RequestParam("uuids") String[] uuids) {
        for (String uuid : uuids) {
            System.out.println("当前删除发件箱消息uuid为：" + uuid);
            messageInboxService.deleteMessage(uuid);
        }
        return ApiResult.success();
    }


    /**
     * 发送消息
     *
     * @param messageBean
     * @return
     */
    @ApiOperation(value = "发送消息", notes = "发送消息")
    @PostMapping(value = "/submitmessage")
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
}
