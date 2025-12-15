/*
 * @(#)2016年6月16日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.web;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.webmail.entity.WmMailbox;
import com.wellsoft.pt.webmail.facade.service.WmMailboxUpgradeService;
import com.wellsoft.pt.webmail.service.WmMailboxService;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * Description: 邮箱升级控制器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月16日.1	zhulh		2016年6月16日		Create
 * </pre>
 * @date 2016年6月16日
 */
// @Controller
// @RequestMapping(value = "/webmail/mailbox")
public class WmMailboxUpgradeController extends BaseController {

    @Autowired
    private WmMailboxUpgradeService wmMailboxUpgradeService;

    @Autowired
    private WmMailboxService wmMailboxService;

    @Autowired
    private MongoFileService mongoFileService;

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "/outbox/upgrade")
    @ResponseBody
    public String openNewDialog(Model model) {
        wmMailboxUpgradeService.upgradeOutbox();
        return "success";
    }

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "/inbox/file")
    @ResponseBody
    public String inboxFiles(Model model) {
        WmMailbox example = new WmMailbox();
        example.setMailboxName(WmWebmailConstants.INBOX);
        example.setStatus(WmWebmailConstants.STATUS_RECEIVE_SUCCESS);
        List<WmMailbox> wmMailboxs = wmMailboxService.findByExample(example);
        for (WmMailbox wmMailbox : wmMailboxs) {
            String folderUuid = wmMailbox.getUuid();
            String repoFiles = wmMailbox.getRepoFileUuids();
            if (StringUtils.isNotBlank(repoFiles)) {
                List<String> fileIds = Arrays.asList(StringUtils.split(repoFiles, Separator.SEMICOLON.getValue()));
                for (String fileId : fileIds) {
                    if (!mongoFileService.isFileInFolder(folderUuid, fileId)) {
                        logger.error("folderUuid: " + folderUuid + ", fileId: " + fileId);
                        mongoFileService.pushFileToFolder(folderUuid, fileId, null);
                    }
                }
            }
        }
        return "success";
    }
}
