/*
 * @(#)2017年10月20日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.personalinfo.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.personalinfo.facade.service.PersonalInfoService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.repository.support.ImageResizeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月20日.1	zhongzh		2017年10月20日		Create
 * </pre>
 * @date 2017年10月20日
 */
@Controller
@RequestMapping("/personalinfo")
public class PersonalInfoController extends BaseController {

    @Autowired
    private PersonalInfoService personalInfoService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @RequestMapping(value = "/show/info")
    public String showInfo(Model model) {
        model.addAttribute("user", personalInfoService.getCurrentUserInfo());
        return forward("/personalinfo/show-personal-info");
    }

    @RequestMapping(value = "/show/infoBody")
    @ResponseBody
    public OrgUserVo showInfoBody() {
        OrgUserVo user = personalInfoService.getCurrentUserInfo();
        // 密码不能传到前端接口
        user.setPassword("");
        return user;
    }

    @RequestMapping(value = "/modify/info")
    public String modifyInfo(Model model) {
        OrgUserVo orgUserVo = personalInfoService.getCurrentUserInfo();
        model.addAttribute("user", orgUserVo);
        model.addAttribute("userJson", JsonUtils.object2Json(orgUserVo));
        return forward("/personalinfo/modify-personal-info");
    }

    @RequestMapping(value = "/modify/password")
    public String modifyPwd(Model model) {
        model.addAttribute("user", personalInfoService.getCurrentUserInfo());
        return forward("/personalinfo/modify-personal-password");
    }


    @RequestMapping("userImg")
    public void userImg(@RequestParam("id") String userId,
                        @RequestParam(value = "preview", required = false) String preview, HttpServletResponse res,
                        HttpServletRequest request) throws IOException {
        String fileID = multiOrgUserService.getUserPhoto(userId);
        if (fileID == null) {
            return;
        }
        MongoFileEntity file = mongoFileService.getFile(fileID);
        String oFileName = file.getLogicFileInfo().getFileName();
        if (preview != null && "true".equals(preview) && file.getLength() > 400000) {// 预览
            // long fileSize = file.getLength();
            String previewPath = Config.APP_DATA_DIR + "/" + "preview";
            File dir = new File(previewPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = file.getId() + oFileName;
            File preFile = new File(previewPath + "/" + fileName);
            InputStream is = null;
            if (!preFile.exists()) {
                try {
                    is = file.getInputstream();
                    ImageResizeTool.createFixedBoundImg(is, preFile, 0.5f);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }

            is = new FileInputStream(preFile);
            FileDownloadUtils.download(request, res, is, oFileName);
            is.close();
        } else {
            InputStream is = file.getInputstream();
            FileDownloadUtils.download(request, res, is, oFileName);
            is.close();
        }

    }

}
