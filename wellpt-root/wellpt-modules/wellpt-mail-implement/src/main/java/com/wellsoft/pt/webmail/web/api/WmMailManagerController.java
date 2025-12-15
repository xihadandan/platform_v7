package com.wellsoft.pt.webmail.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.facade.service.WmMailRevocationFacadeService;
import com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 邮件管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/10/19.1	    zenghw		2021/10/19		    Create
 * </pre>
 * @date 2021/10/19
 */
@Api(tags = "邮件管理接口")
@RestController
@RequestMapping("/api/mail/manager/")
public class WmMailManagerController extends BaseController {

    @Autowired
    private WmMailRevocationFacadeService wmMailRevocationFacadeService;
    @Autowired
    private WmWebmailOutboxService wmWebmailOutboxService;
    @Autowired
    private WmWebmailService wmWebmailService;
    @Autowired
    private WmMailConfigService wmMailConfigService;
    @Autowired
    private WmMailboxInfoUserService wmMailboxInfoUserService;


    @GetMapping("/oldToNew")
    public ApiResult oldToNew(@RequestParam(name = "pageSize", required = false) Integer pageSize) {
        if (pageSize == null) {
            pageSize = 200;
        }
        wmWebmailService.oldToNew(pageSize);
        return ApiResult.success();
    }

    @ApiOperation(value = "邮件撤回", notes = "邮件撤回 返回每个收件人的撤回结果")
    @PostMapping("/revokeMail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mailUuid", value = "邮件uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Map<String, Boolean>> revokeMail(
            @RequestParam(name = "mailUuid", required = true) String mailUuid) {
        return ApiResult.success(wmMailRevocationFacadeService.revokeMail(mailUuid));
    }

    @ApiOperation(value = "邮件删除", notes = "邮件删除")
    @PostMapping("/delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mailboxUuids", value = "邮件uuid集合", paramType = "query", dataType = "String", allowMultiple = true, required = false)})
    public ApiResult delete(@RequestParam(name = "mailboxUuids", required = false) Collection<String> mailboxUuids) {
        wmWebmailOutboxService.delete(mailboxUuids);
        return ApiResult.success();
    }

    @ApiOperation(value = "更新已读/未读状态", notes = "更新已读/未读状态")
    @PostMapping("/updateMailReadStatus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mailboxUuids", value = "邮件uuid集合", paramType = "query", dataType = "Array", required = false),
            @ApiImplicitParam(name = "readStatus", value = "1 已读   0 未读", paramType = "query", dataType = "Array", required = true)})
    public ApiResult updateMailReadStatus(
            @RequestParam(name = "mailboxUuids", required = false) List<String> mailboxUuids,
            @RequestParam(name = "readStatus", required = true) String readStatus) {
        wmWebmailService.updateMailReadStatus(mailboxUuids, readStatus);
        return ApiResult.success();
    }

    @ApiOperation(value = "获取邮件详情", notes = "获取邮件详情")
    @GetMapping("/get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mailboxUuid", value = "邮件uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WmWebmailBean> getWebMail(
            @RequestParam(name = "mailboxUuid", required = true) String mailboxUuid) {
        WmWebmailBean webmailBean = wmWebmailOutboxService.get(mailboxUuid);
        if (webmailBean.getSendStatus() == null && webmailBean.getRevokeStatus() != null && webmailBean.getRevokeStatus() == 1) {
            webmailBean.setContent("该邮件已经被发送者撤回");
            webmailBean.setSubject(webmailBean.getSubject());
        } else if (webmailBean.getSendStatus() == null && webmailBean.getReadReceiptStatus() != null && webmailBean.getReadReceiptStatus() == 1) {
            //邮箱配置
            WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
            //自动发送回执
            if (configEntity.getSendReceipt() != null && configEntity.getSendReceipt()) {
                wmWebmailService.receipt(mailboxUuid, 2);
                webmailBean.setReadReceiptStatus(2);
            }
        }
        return ApiResult.success(webmailBean);
    }

    @ApiOperation(value = "保存待发送", notes = "保存待发送")
    @PostMapping("/send")
    public ApiResult<Map<String, String>> send(@RequestBody WmWebmailBean webmailBean) {
        return ApiResult.success(wmWebmailService.send(webmailBean));
    }

    @ApiOperation(value = "重新发送", notes = "重新发送")
    @PostMapping("/resend")
    public ApiResult resend(
            @RequestParam(name = "mailboxUuid", required = true) String mailboxUuid) {
        wmMailboxInfoUserService.resend(mailboxUuid);
        return ApiResult.success();
    }

}
