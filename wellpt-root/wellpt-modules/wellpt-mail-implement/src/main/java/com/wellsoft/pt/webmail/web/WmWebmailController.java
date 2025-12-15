/*
 * @(#)2016年6月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.web;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailConfigDto;
import com.wellsoft.pt.webmail.bean.WmMailRevocationDto;
import com.wellsoft.pt.webmail.bean.WmMailUserDto;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.entity.WmMailRevocationEntity;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.enums.WmMailBoxName;
import com.wellsoft.pt.webmail.enums.WmMailBoxStatus;
import com.wellsoft.pt.webmail.enums.WmMailRevokeStatus;
import com.wellsoft.pt.webmail.facade.service.WmWebmailOutboxService;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.job.DefaultWebmailClientSyncInboxMailJob;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import com.wellsoft.pt.webmail.service.WmMailRevocationService;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import com.wellsoft.pt.webmail.service.WmMailboxInfoUserService;
import com.wellsoft.pt.webmail.support.MailFetchHanlder;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import com.wellsoft.pt.webmail.support.WmWebmailOperation;
import com.wellsoft.pt.webmail.support.WmWebmailOperation.Button;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 邮件
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月3日.1	zhulh		2016年6月3日		Create
 * </pre>
 * @date 2016年6月3日
 */
@Controller
@RequestMapping(value = "/webmail")
public class WmWebmailController extends BaseController {

    private Lock lock = new ReentrantLock();

    @Autowired
    private WmMailUserService wmMailUserService;

    @Autowired
    private WmWebmailService wmWebmailService;

    @Resource
    private WmMailRevocationService wmMailRevocationService;

    @Resource
    private MailFetchHanlder mailFetchHanlder;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private WmMailConfigService wmMailConfigService;

    @Autowired
    private WmWebmailOutboxService wmWebmailOutboxService;

    @Autowired
    private WmMailboxInfoUserService wmMailboxInfoUserService;

    /**
     * 判断当前用户是否存在邮箱账号
     *
     * @return
     */
    @RequestMapping(value = "/exists/mail/account")
    @ResponseBody
    public String exists() {
        // 获取邮件发件人地址列表
        WmMailUserEntity example = new WmMailUserEntity();
        example.setUserId(SpringSecurityUtils.getCurrentUserId());
        List<WmMailUserEntity> wmMailUsers = wmMailUserService.findByExample(example);
        if (wmMailUsers.isEmpty()) {
            return "false";
        }
        return "true";
    }

    /**
     * 打开写信界面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/new")
    @Description("打开写邮件Web页面")
    public String newMail(Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmWebmailBean webmailBean = wmWebmailService.getWmWebmailBean(userId, null);
        webmailBean.setSubject("写信");
        model.addAttribute("webmailBean", webmailBean);
        setAllowOrgOptionsAttribute(model);
        model.addAttribute("buttons", WmWebmailOperation.getNewBtns(userId));
        return forward("/webmail/wm_mail_outbox");
    }

    private void setAllowOrgOptionsAttribute(Model model) {
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(
                SpringSecurityUtils.getCurrentUserUnitId());
        if (configEntity != null && StringUtils.isNotBlank(configEntity.getAllowOrgOptions())) {
            model.addAttribute("allowOrgOptions", JsonUtils.object2Gson(
                    orgApiFacade.getOrgOptionsByIds(configEntity.getAllowOrgOptions().split(";"))));
        }
    }

    /**
     * 获取邮件配置
     *
     * @return
     */
    @GetMapping("/getMailConfig")
    public @ResponseBody
    WmMailConfigDto getMailConfig() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(
                userDetails.getSystemUnitId());
        WmMailConfigDto configDto = new WmMailConfigDto();
        if (configEntity != null) {
            BeanUtils.copyProperties(configEntity, configDto);
            if (StringUtils.isNotBlank(configEntity.getAllowOrgOptions())) {
                configDto.setAllowOrgOptionList(orgApiFacade.getOrgOptionsByIds(configEntity.getAllowOrgOptions().split(";")));
            }
            // 获取邮件发件人地址列表
            WmMailUserEntity example = new WmMailUserEntity();
            example.setUserId(userDetails.getUserId());
            List<WmMailUserEntity> wmMailUsers = wmMailUserService.findByExample(example);
            List<WmMailUserDto> senter = new ArrayList<WmMailUserDto>();
            for (WmMailUserEntity wmMailUser : wmMailUsers) {
                WmMailUserDto wmMailUserDto = new WmMailUserDto();
                BeanUtils.copyProperties(wmMailUser, wmMailUserDto);
                if (wmMailUser.getIsInnerUser()) {
                    wmMailUserDto.setUserName(orgApiFacade.getUserNameById(
                            wmMailUser.getUserId()));
                }
                senter.add(wmMailUserDto);
            }
            configDto.setSenterMailAddresses(senter);
        }
        return configDto;
    }

    /**
     * 双击打开草稿邮件
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/draft")
    public String viewDraft(@RequestParam(value = "mailboxUuid") String mailboxUuid, Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmWebmailBean webmailBean = wmWebmailService.getWmWebmailBean(userId, mailboxUuid);
        webmailBean.setMailboxUuid(mailboxUuid);
        model.addAttribute("webmailBean", webmailBean);
        model.addAttribute("buttons", WmWebmailOperation.getDraftBtns(userId, mailboxUuid));
        setAllowOrgOptionsAttribute(model);
        return forward("/webmail/wm_mail_outbox");
    }

    /**
     * 再次编辑
     *
     * @param mailboxUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/editAgain")
    public String editAgain(@RequestParam(value = "mailboxUuid") String mailboxUuid, Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmWebmailBean webmailBean = wmWebmailService.getWmWebmailBean(userId, mailboxUuid);
        webmailBean.setEditAgain(true);
        model.addAttribute("webmailBean", webmailBean);
        setAllowOrgOptionsAttribute(model);
        model.addAttribute("buttons", WmWebmailOperation.getNewBtns(userId));
        return forward("/webmail/wm_mail_outbox");
    }

    @GetMapping("/get")
    public @ResponseBody
    WmWebmailBean getWebMail(@RequestParam(value = "mailboxUuid") String mailboxUuid) {
        WmWebmailBean webmailBean = wmWebmailOutboxService.get(mailboxUuid);
        if (webmailBean != null)
            tryRevokeMailFailSave(webmailBean);
        return webmailBean;
    }

    /**
     * 双击打开收件箱、已发送、删除箱的邮件
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/inbox")
    public String viewInbox(@RequestParam(value = "mailboxUuid") String mailboxUuid, Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmWebmailBean webmailBean = wmWebmailService.getWmWebmailBean(userId, mailboxUuid);
        webmailBean.setMailboxUuid(mailboxUuid);
        model.addAttribute("webmailBean", webmailBean);
        List<Button> buttons = WmWebmailOperation.getInboxBtns(userId, mailboxUuid);

        if (WmMailBoxName.OUT_BOX.getCode().equals(webmailBean.getMailboxName())) {
            if (WmMailBoxStatus.LOGICAL_DELETE.getCode() != webmailBean.getStatus()) {
                buttons.add(0, WmWebmailOperation.BTN_EDIT_AGAIN_STATUS);
            }
            if (webmailBean.getRevokeStatus() == null) {
                buttons.add(WmWebmailOperation.BTN_REVOKE);// 撤回按钮
            } else {
                model.addAttribute("revokeStatusName", "["
                        + (WmMailRevokeStatus.values()[webmailBean.getRevokeStatus()]).getName() + "]");
            }

        }
        model.addAttribute("buttons", buttons);
        tryRevokeMailFailSave(webmailBean);

        if (WmWebmailConstants.FLAG_UNREAD.equals(webmailBean.getIsRead())) {
            // 标记已阅
            wmWebmailService.markRead(mailboxUuid, userId);
        }
        try {
            Map<String, Object> sendStatus = wmWebmailOutboxService.querySendStatus(mailboxUuid, true);
            model.addAttribute("sendStatus", sendStatus);
        } catch (Exception ex) {
            logger.error("Webmail投递状态渲染,异常：", Throwables.getStackTraceAsString(ex));
        }
        return forward("/webmail/wm_mail_inbox");
    }

    /**
     * 对邮件进行撤回失败的记录
     */
    private void tryRevokeMailFailSave(WmWebmailBean webmailBean) {
        if (WmMailBoxName.IN_BOX.getCode().equals(webmailBean.getMailboxName())
                && StringUtils.isNotBlank(webmailBean.getMid())) {
            WmMailRevocationDto paramDto = new WmMailRevocationDto(webmailBean.getUserId(),
                    webmailBean.getFromMailUuid(), true);
            List<WmMailRevocationEntity> existRevocations = wmMailRevocationService.queryMailRevocation(
                    paramDto);
            boolean hasRevokeRecord = CollectionUtils.isNotEmpty(existRevocations);
            if (hasRevokeRecord) {// 有撤回记录，查看撤回记录的状态
                if (existRevocations.get(0).getIsRevokeSuccess()) {// 如果是撤回成功的记录，则邮件就不允许读取具体内容了
                    webmailBean.setRevokeStatus(WmMailRevokeStatus.REVOKE_SUCCESS.ordinal());
                    webmailBean.setContent("该邮件已经被发送者撤回");
                    webmailBean.setSubject(webmailBean.getSubject());
                }
            } else {
                // 如果没有撤回记录，则查看邮件的时候，需要写一条撤回失败的记录，防止发信方并发进行撤回操作，保证只有一方动作是成功的
                try {
                    // 保存一条撤回失败的记录，防止被撤回(针对并发情况)
                    wmMailRevocationService.saveMailRevocation(webmailBean.getFromMailUuid(),
                            webmailBean.getUserId(), false);
                } catch (Exception e) {
                    // 保存一条撤回失败的记录抛出数据库唯一性约束异常，说明数据库已经有一条撤回邮件的记录，此时有可能是并发撤回，或者并发读邮件，需要再次查询数据库的撤回记录状态
                    existRevocations = wmMailRevocationService.queryMailRevocation(paramDto);
                    if (CollectionUtils.isNotEmpty(existRevocations) && existRevocations.get(
                            0).getIsRevokeSuccess()) {// 如果是撤回成功的记录，则邮件就不允许读取具体内容了
                        webmailBean.setRevokeStatus(WmMailRevokeStatus.REVOKE_SUCCESS.ordinal());
                        webmailBean.setContent("<div class=\"red_tip\">该邮件已经被发送者撤回。</div>");
                        webmailBean.setSubject("发信方已撤回邮件：" + webmailBean.getSubject());
                    }
                }
            }
        }
    }

    private String explainCurrentUserMail(WmWebmailBean webmailBean) {
        String subMailAddress = null;
        if (StringUtils.isNotBlank(webmailBean.getToMailAddress())
                && webmailBean.getToMailAddress().indexOf(
                SpringSecurityUtils.getCurrentLoginName()) != -1) {
            subMailAddress = webmailBean.getToMailAddress().substring(
                    webmailBean.getToMailAddress().indexOf(
                            SpringSecurityUtils.getCurrentLoginName()));
        } else if (StringUtils.isNotBlank(webmailBean.getCcMailAddress())
                && webmailBean.getCcMailAddress().indexOf(
                SpringSecurityUtils.getCurrentLoginName()) != -1) {
            subMailAddress = webmailBean.getCcMailAddress().substring(
                    webmailBean.getCcMailAddress().indexOf(
                            SpringSecurityUtils.getCurrentLoginName()));
        } else if (StringUtils.isNotBlank(webmailBean.getBccMailAddress())
                && webmailBean.getBccMailAddress().indexOf(
                SpringSecurityUtils.getCurrentLoginName()) != -1) {
            subMailAddress = webmailBean.getBccMailAddress().substring(
                    webmailBean.getBccMailAddress().indexOf(
                            SpringSecurityUtils.getCurrentLoginName()));
        }

        if (StringUtils.isNotEmpty(subMailAddress)) {
            int endIndex = subMailAddress.indexOf(
                    ";") == -1 ? subMailAddress.length() : subMailAddress.indexOf(";");
            return subMailAddress.substring(0, endIndex);
        }
        return null;
    }

    /**
     * 转发
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/transfer")
    public String transfer(@RequestParam(value = "mailboxUuid") String mailboxUuid, Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmWebmailBean webmailBean = wmWebmailService.getWmWebmailBean(userId, mailboxUuid);
        tryRevokeMailFailSave(webmailBean);
        webmailBean.setSubject("转发: " + webmailBean.getSubject());
        webmailBean.setTransfer(true);
        model.addAttribute("webmailBean", webmailBean);
        setAllowOrgOptionsAttribute(model);
        model.addAttribute("buttons", WmWebmailOperation.getNewBtns(userId));
        return forward("/webmail/wm_mail_outbox");
    }

    /**
     * 回复
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/reply")
    public String reply(@RequestParam(value = "mailboxUuid") String mailboxUuid, Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmWebmailBean webmailBean = wmWebmailService.getWmWebmailBean(userId, mailboxUuid);
        tryRevokeMailFailSave(webmailBean);
        webmailBean.setSubject("回复: " + webmailBean.getSubject());
        webmailBean.setReply(true);
        model.addAttribute("webmailBean", webmailBean);
        setAllowOrgOptionsAttribute(model);
        model.addAttribute("buttons", WmWebmailOperation.getNewBtns(userId));
        return forward("/webmail/wm_mail_outbox");
    }

    /**
     * 回复全部
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/reply/all")
    public String replyAll(@RequestParam(value = "mailboxUuid") String mailboxUuid, Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmWebmailBean webmailBean = wmWebmailService.getWmWebmailBean(userId, mailboxUuid);
        tryRevokeMailFailSave(webmailBean);
        webmailBean.setSubject("回复: " + webmailBean.getSubject());
        webmailBean.setReplyAll(true);
        model.addAttribute("webmailBean", webmailBean);
        setAllowOrgOptionsAttribute(model);
        model.addAttribute("buttons", WmWebmailOperation.getNewBtns(userId));
        return forward("/webmail/wm_mail_outbox");
    }

    @RequestMapping(value = "/count")
    @ResponseBody
    public String count() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        return wmWebmailService.countInfo(userId);
    }

    @RequestMapping("/poll")
    @ResponseBody
    public String poll() {
        return "0";
    }

    @RequestMapping("/refush")
    @ResponseBody
    public ApiResult refush(HttpServletRequest request) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        Map<String, Integer> map = Maps.newHashMap();
        int count = wmMailboxInfoUserService.receiveFailMail(userId);
        if (count > 0) {
            map.put("receiveFailCount", count);
            return ApiResult.success(map);
        }
        DefaultWebmailClientSyncInboxMailJob.startSyncInboxMail(userId);
        Long unReadCount = wmMailboxInfoUserService.getUnreadCount(userId);
        WmMailConfigEntity wmMailConfigEntity = wmMailConfigService.getBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        map.put("unReadCount", unReadCount.intValue());
        map.put("receiveMailAction", wmMailConfigEntity.getReceiveMailAction());
        return ApiResult.success(map);
    }

    /**
     * 同步外部邮箱账号的收件箱邮件
     *
     * @param type 0 按目前已经同步到的邮件编号同步往后的
     *             1 最近七天的邮件
     *             2 最近一个月的邮件
     *             3 最近三个月的邮件
     *             4 最近半年的邮件
     *             -9 同步全量
     * @return
     */
    @RequestMapping(value = "/syncOtherMailAcountMessages", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Integer> syncOtherMailAccountMessages(Integer type, String mailAddress) {
        WmMailUserEntity mailUserEntity = wmMailUserService.getOuterMailUser(mailAddress, SpringSecurityUtils.getCurrentUserId());
        if (mailUserEntity != null) {
            Date truncateNow = DateUtils.truncate(new Date(), Calendar.DATE);
            Date beginTime = null;
            if (type == null) {
                type = 0;
            }
            switch (type) {
                case 1:
                    beginTime = DateUtils.addDays(truncateNow, -7);//最近一周
                    break;
                case 2:
                    beginTime = DateUtils.addMonths(truncateNow, -1);//最近一个月
                    break;
                case 3:
                    beginTime = DateUtils.addMonths(truncateNow, -3);//最近三个月
                    break;
                case 4:
                    beginTime = DateUtils.addMonths(truncateNow, -6);//最近半年
                    break;
                default:
                    beginTime = null;
            }
            return new ResponseEntity<Integer>(mailFetchHanlder.fetchMailsUsingPop3Protocol(mailUserEntity, beginTime), HttpStatus.OK);
        }


        return new ResponseEntity(0, HttpStatus.OK);
    }


}
