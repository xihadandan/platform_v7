/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.org.adsycn.service.ADDeptService;
import com.wellsoft.pt.org.bean.UserBean;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.DepartmentAdminService;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Description: UserController.java
 *
 * @author Administrator
 * @date 2012-12-23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-23.1	zhulh		2012-12-23		Create
 * </pre>
 */
@Controller
@RequestMapping("/org/user")
public class UserController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    private DepartmentAdminService departmentAdminService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ADDeptService aDDeptService;

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @RequestMapping(value = "/list")
    public String user(Model model) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String currentUserId = userDetails.getUserId();
        Boolean isDepartmentAdmin = departmentAdminService.isDepartmentAdmin(currentUserId);
        if (userDetails.isAdmin()) {
            model.addAttribute("isDepartmentAdmin", "0");
        } else if (isDepartmentAdmin) {
            List<String> departmentUuidList = departmentAdminService.getDepartmentUuidListByUserId(currentUserId);
            model.addAttribute("currentUserId", currentUserId);
            model.addAttribute("departmentUuid", departmentUuidList.get(0));
            model.addAttribute("isDepartmentAdmin", "1");
        } else {
            Department department = departmentService.getById(userDetails.getMainDepartmentId());
            model.addAttribute("currentUserId", currentUserId);
            model.addAttribute("departmentUuid", department.getUuid());
            model.addAttribute("isDepartmentAdmin", "1");
        }
        return forward("/org/user");
    }

    // @RequestMapping(value = "/list")
    // public @ResponseBody
    // JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
    // JqGridQueryData queryData = userService.query(queryInfo);
    // return queryData;
    // }

    // @RequestMapping(value = "/get")
    // public @ResponseBody
    // ResultMessage get(@RequestBody UserBean user) {
    // ResultMessage resultMessage = new ResultMessage();
    // UserBean bean = userService.getBeanById(user.getId());
    // resultMessage.setData(bean);
    // return resultMessage;
    // }

    // @RequestMapping(value = "/save", method = RequestMethod.POST)
    // public @ResponseBody
    // ResultMessage update(@RequestBody @Valid UserBean bean) {
    // ResultMessage message = new ResultMessage();
    // userService.saveBean(bean);
    // message.setData(bean);
    // return message;
    // }

    @RequestMapping(value = "/delete")
    public @ResponseBody
    ResultMessage delete(@RequestBody UserBean user) {
        ResultMessage resultMessage = new ResultMessage();
        userService.deleteById(user.getId());
        return resultMessage;
    }

    /**
     * 使用@ModelAttribute, 实现Struts2
     * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
     */
    // @ModelAttribute("preloadUser")
    // public User getUser(@RequestBody User user) {
    // if (user != null && StringUtils.isNotBlank(user.getUuid())) {
    // return userService.getUserByUUID(user.getUuid());
    // }
    // return null;
    // }
    @RequestMapping(value = "/upload/photo", method = RequestMethod.POST)
    @ResponseBody
    public void uploadPhoto(@RequestParam(value = "upload") MultipartFile upload, HttpServletResponse response)
            throws IOException {
        // 上传处理
        MongoFileEntity fileEntity = mongoFileService.saveFile(upload.getOriginalFilename(), upload.getInputStream());

        BufferedImage image = ImageIO.read(fileEntity.getInputstream());
        if (image == null) { // 如果不是图片格式
            response.setContentType(MediaType.TEXT_HTML_VALUE);
            response.getWriter().write("");
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            response.setContentType(MediaType.TEXT_HTML_VALUE);
            response.getWriter().write(fileEntity.getId());
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    @RequestMapping(value = "/view/photo/{nodeUuid}")
    @ResponseBody
    public void viewPhoto(@PathVariable(value = "nodeUuid") String nodeUuid, HttpServletResponse response) {
        response.setContentType("image/jpeg"); // 必须设置ContentType为image/jpeg
        // response.setHeader("Pragma", "No-cache");
        // response.setHeader("Cache-Control", "no-cache");
        // response.setDateHeader("Expires", 0);
        InputStream is = null;
        OutputStream os = null;
        try {
            if (StringUtils.isNotBlank(nodeUuid) && !"null".equals(nodeUuid)) {
                if (nodeUuid.startsWith(IdPrefix.USER.getValue())) {
                    String avatar = userInfoService.getUserAvatar(nodeUuid);
                    if (StringUtils.isNotBlank(avatar)) {
                        nodeUuid = avatar;
                    }
                }
                // 上传处理
                MongoFileEntity fileEntity = mongoFileService.getFile(nodeUuid);
                if (fileEntity != null) {
                    is = fileEntity.getInputstream();
                    os = response.getOutputStream();
                    if (is != null) {
                        IOUtils.write(IOUtils.toByteArray(is), os);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
            if (os != null) {
                IOUtils.closeQuietly(os);
            }
        }
    }


    @GetMapping(value = "/view/photo/user/{userId}")
    public void viewPhotoByUserId(@PathVariable(value = "userId") String userId, HttpServletResponse response) {
        response.setContentType("image/jpeg"); // 必须设置ContentType为image/jpeg
        InputStream is = null;
        OutputStream os = null;
        try {
            String fileID = multiOrgUserService.getUserPhoto(userId);
            if (fileID == null) {
                return;
            }
            // 上传处理
            MongoFileEntity fileEntity = mongoFileService.getFile(fileID);
            if (fileEntity != null) {
                is = fileEntity.getInputstream();
                os = response.getOutputStream();
                if (is != null) {
                    IOUtils.write(IOUtils.toByteArray(is), os);
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
            if (os != null) {
                IOUtils.closeQuietly(os);
            }
        }
    }


    @RequestMapping(value = "/saveUserSet", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage saveUserSet(@RequestParam("uuid") String uuid, UserBean userBean, HttpServletResponse response)
            throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        try {
            userService.saveUserSet(uuid, userBean);
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getUser(@RequestParam("uuid") String uuid, HttpServletResponse response) throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        try {
            UserBean user = userService.getBean(uuid);
            resultMessage.setData(user);
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    @RequestMapping(value = "/getDataDictionariesByType", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage getDataDictionariesByType(@RequestParam("type") String type, HttpServletResponse response)
            throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        try {
            List<DataDictionary> traceList = dataDictionaryService.getDataDictionariesByType(type);
            resultMessage.setData(traceList);
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    @RequestMapping(value = "/saveCommonUsers", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage saveCommonUsers(@RequestParam("commonUserNames") String commonUserNames,
                                         @RequestParam("commonUserIds") String commonUserIds, HttpServletResponse response) throws Exception {
        ResultMessage resultMessage = new ResultMessage();
        try {
            this.userService.saveCommonUsers(commonUserNames, commonUserIds);
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    @RequestMapping(value = "/viewUserContacts")
    public String viewUserContacts(@RequestParam("uuid") String uuid, Model model) {
        model.addAttribute("uuid", uuid);
        return forward("/org/user_contact");
    }

    @RequestMapping(value = "/setAdSync", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage setAdSync(HttpServletResponse response) {
        ResultMessage resultMessage = new ResultMessage();
        try {
            aDDeptService.adSync();
            resultMessage.setSuccess(true);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            resultMessage.setSuccess(false);
        }
        return resultMessage;
    }

    /**
     * 作者： yuyq
     * 创建时间：2015-2-4 下午3:54:33
     * 类描述：TODO
     * 备注：通过字符串ids找到用户的主部门
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/getDepartmentsByIds")
    @ResponseBody
    public String getDepartmentsByIds(@RequestParam(value = "ids") String ids) {
        String departments = "";
        if (StringUtils.isNotBlank(ids)) {
            String[] idsString = ids.split(Separator.SEMICOLON.getValue());
            for (String id : idsString) {
                User user = userService.getById(id);
                String dapartmentname = "";
                String[] dapartmentnames = user.getDepartmentName().split(Separator.SEMICOLON.getValue());
                dapartmentname = dapartmentnames[0];
                departments += ";" + dapartmentname;
            }
            departments = departments.replaceFirst(";", "");
        }
        return departments;

    }
}