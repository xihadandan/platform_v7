package com.wellsoft.pt.security.config.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.config.dto.AppLoginPageConfigSettingDto;
import com.wellsoft.pt.security.config.dto.MultiUserLoginSettingsDto;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;
import com.wellsoft.pt.security.config.service.AppLoginPageConfigService;
import com.wellsoft.pt.security.config.service.MultiUserLoginSettingsService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialClob;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "登录配置")
@RestController
@RequestMapping("/web/login/config")
public class AppLoginPageConfigController extends BaseController {

    @Autowired
    private AppLoginPageConfigService appLoginPageConfigService;

    @Autowired
    private MultiUserLoginSettingsService multiUserLoginSettingsService;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private MultiOrgService multiOrgService;

    @Value("${spring.security.pwd.encrypt.type:1}")
    private String passwordEncryptType; // 密码加密模式: 默认1=base64

    @ApiOperation(value = "获取登录页配置", notes = "获取登录页配置", tags = {"超管-->登录页配置"})
    @GetMapping("/getLoginPageConfig")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位ID", paramType = "query", dataType = "String", required = false)})
    @ResponseBody
    public AppLoginPageConfigEntity getLoginPageConfig(@RequestParam(required = false) String systemUnitId) {
        AppLoginPageConfigEntity entity = appLoginPageConfigService.getLoginConfigBySystemUnitId(
                systemUnitId = StringUtils.isBlank(systemUnitId) ? Config.DEFAULT_TENANT : systemUnitId, true);
        entity.setPasswordEncryptType(passwordEncryptType);
        String userNamePlaceholder = multiUserLoginSettingsService.getUserNamePlaceholder(systemUnitId);
        entity.setUserNamePlaceholder(userNamePlaceholder);
        return entity;
    }

    @GetMapping("/appLoginPageConfigSetting")
    @ApiOperation(value = "App登录页面配置设置", notes = "App登录页面配置设置", response = AppLoginPageConfigSettingDto.class, tags = {
            "超管-->登录页配置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位ID", paramType = "query", dataType = "String", required = false)})
    @ResponseBody
    public Map<String, Object> appLoginPageConfigSetting(@RequestParam(required = false) String systemUnitId) {
        Map<String, Object> model = Maps.newHashMap();
        systemUnitId = StringUtils.isBlank(systemUnitId) ? Config.DEFAULT_TENANT : systemUnitId;
        AppLoginPageConfigEntity pageConfig = appLoginPageConfigService.getBySystemUnitId(systemUnitId);
        if (pageConfig == null) {
            pageConfig = appLoginPageConfigService.saveInitPageConfig(systemUnitId);
        }
        model.put("po", pageConfig);
        model.put("ptId", MultiOrgSystemUnit.PT_ID);
        model.put("defaultId", Config.DEFAULT_TENANT);
        model.put("systemUnitId", systemUnitId);
        List<MultiOrgSystemUnit> systemUnits = multiOrgService.queryAllSystemUnitList();
        model.put("allSystemUnits", systemUnits);
        List<MultiUserLoginSettingsDto> loginTypes = multiUserLoginSettingsService.getBySystemUnitId(MultiOrgSystemUnit.PT_ID);
        model.put("loginTypes", loginTypes);
        // 背景图转为base64编码输出
        try {
            String imageBase64 = IOUtils.toString(pageConfig.getPageBackgroundImage().getCharacterStream());
            if (StringUtils.isNotBlank(imageBase64)) {
                model.put("backgroundImage",
                        (imageBase64.startsWith("data:image/png;base64,") ? "" : "data:image/png;base64,")
                                + imageBase64.replaceAll("\r|\n", ""));
            }

            imageBase64 = IOUtils.toString(pageConfig.getPageLogo().getCharacterStream());
            if (StringUtils.isNotBlank(imageBase64)) {
                model.put("headerImage",
                        (imageBase64.startsWith("data:image/png;base64,") ? "" : "data:image/png;base64,")
                                + imageBase64.replaceAll("\r|\n", ""));

            }
        } catch (Exception e) {

        }
        return model;
    }

    @GetMapping("/index")
    @ApiOperation(value = "返回登录页配置url", notes = "返回登录页配置url", tags = {"超管-->登录页配置"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "u", value = "系统单位ID", paramType = "query", dataType = "String", required = false)})
    public String index(@RequestParam(value = "u", required = false) String systemUnitId, Model model) {
        model.addAllAttributes(this.appLoginPageConfigSetting(systemUnitId));
        return "pt/security/app_login_page_config";
    }

    @GetMapping("/unitLoginPageSetting")
    @ResponseBody
    @ApiOperation(value = "获取单位登录页配置", notes = "获取单位登录页配置（用于在左导航菜单中获取登录页配置信息以及退出登录时获取登录的url）", response = AppLoginPageConfigEntity.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位id", paramType = "query", dataType = "String", required = false)})
    public ApiResult loginPageSettingByUid(@RequestParam(value = "systemUnitId", required = false) String systemUnitId) {
        AppLoginPageConfigEntity entity = new AppLoginPageConfigEntity();
        if (StringUtils.isNotBlank(systemUnitId)) {
            entity.setSystemUnitId(systemUnitId);
            List<AppLoginPageConfigEntity> list = appLoginPageConfigService.listByEntity(entity);
            if (null != list && !list.isEmpty()) {
                entity = list.get(0);
                // 背景图转为base64编码输出
                entity = readImageBase64Clob(entity);
                BeanUtils.copyProperties(appLoginPageConfigService.getBySystemUnitId("T001"), entity,
                        new String[]{"uuid", "pageTitle", "pageBackgroundImage", "pageBackgroundImageBase64", "pageStyle", "pageLogo", "pageLogoBase64", "footerContent", "unitLoginPageUri", "unitLoginPageSwitch", "systemUnitId"});
                entity.setUserNamePlaceholder(handlePlaceholder());
                return ApiResult.success(entity);
            }
        }
        BeanUtils.copyProperties(appLoginPageConfigService.getBySystemUnitId("T001"), entity, new String[]{"uuid"});
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        appLoginPageConfigService.save(entity);
        entity.setUserNamePlaceholder(handlePlaceholder());
        return ApiResult.success(readImageBase64Clob(entity));
    }

    private String handlePlaceholder() {
        List<MultiUserLoginSettingsDto> loginTypes = multiUserLoginSettingsService.getBySystemUnitId(MultiOrgSystemUnit.PT_ID);
        StringBuffer sb = new StringBuffer();
        loginTypes.forEach(tmp -> {
            if (1 == tmp.getEnable()) {
                if (sb.length() > 0) {
                    sb.append("/");
                }
                if (sb.length() == 0) {
                    sb.append("请输入");
                }
                sb.append(tmp.getLoginTypeAlias());
            }
        });
        return sb.toString();
    }

    @GetMapping("/loginPageSettings")
    @ResponseBody
    @ApiOperation(value = "根据登录url获取单位登录页配置", notes = "根据登录url获取单位登录页配置（用于在登录时，获取登录页配置信息）", response = AppLoginPageConfigEntity.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginUrl", value = "登录url", paramType = "query", dataType = "String", required = false)})
    public ApiResult loginPageSettings(@RequestParam(value = "loginUrl", required = false) String loginUrl) {
        if (StringUtils.isNotBlank(loginUrl)) {
            AppLoginPageConfigEntity entity = null;
            List<AppLoginPageConfigEntity> list = appLoginPageConfigService.listByLoginPageUrl(loginUrl);
            if (null != list && !list.isEmpty()) {
                return ApiResult.success(readImageBase64Clob(list.get(0)));
            } else {
                return ApiResult.success();
            }
        } else {
            return ApiResult.success();
        }
    }


    @GetMapping("/getAllLoginPageSettings")
    @ResponseBody
    @ApiOperation(value = "获取所有单位登录页配置", notes = "获取所有单位登录页配置")
    public ApiResult<List<AppLoginPageConfigEntity>> getAllLoginPageSettings() {
        try {
            List<AppLoginPageConfigEntity> list = appLoginPageConfigService.listAll();
            if (CollectionUtils.isNotEmpty(list)) {
                for (AppLoginPageConfigEntity entity : list) {
                    entity.setPasswordEncryptType(passwordEncryptType);
                    readImageBase64Clob(entity);
                }
                return ApiResult.success(list);
            }
        } catch (Exception e) {
            logger.error("获取所有的登录页配置异常：", e);
        }
        return ApiResult.success(null);
    }

    private AppLoginPageConfigEntity readImageBase64Clob(AppLoginPageConfigEntity entity) {
        if (null == entity) {
            return null;
        }
        try {
            String imageBase64 = IOUtils.toString(entity.getPageBackgroundImage().getCharacterStream());
            if (StringUtils.isNotBlank(imageBase64)) {
                entity.setPageBackgroundImageBase64(imageBase64);
            }

            imageBase64 = IOUtils.toString(entity.getPageLogo().getCharacterStream());
            if (StringUtils.isNotBlank(imageBase64)) {
                entity.setPageLogoBase64(imageBase64);
            }
        } catch (Exception e) {
            logger.error("读取图片base64 clob数据异常：", e);
        }
        return entity;
    }

    @PostMapping("/uploadFooterContentFile")
    @ResponseBody
    @ApiOperation(value = "上传页脚内容", notes = "上传页脚内容", tags = {"超管-->登录页配置"})
    public String uploadFooterContentFile(@RequestParam(value = "footerContentFile") MultipartFile footerContentFile,
                                          HttpServletRequest request) {

        try {
            MongoFileEntity mongoFileEntity = mongoFileService.saveFile(footerContentFile.getOriginalFilename(),
                    footerContentFile.getInputStream());
            String fileId = mongoFileEntity.getId();
            // mongoFileService.pushFileToFolder(UUID.randomUUID().toString(),
            // fileId, "footerContentFile");
            String url = request.getContextPath() + "/security/aid/downloadFooterContentFile?uuid=" + fileId;
            System.out.println(url);
            return "<a href=\"" + url + "\" target=\"_blank\">" + footerContentFile.getOriginalFilename() + "</a>";
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return "error";
    }

    @PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "保存", notes = "保存", tags = {"超管-->登录页配置"})
    public ApiResult save(@RequestParam(value = "pageBackgroundImage", required = false) MultipartFile pageBackgroundImage,
                          @RequestParam(value = "pageLogo", required = false) MultipartFile pageLogo,
                          String data, String loginType,
                          HttpServletRequest request) throws Exception {
        AppLoginPageConfigEntity po = (AppLoginPageConfigEntity) JSONObject.toBean(JSONObject.fromObject(data),
                AppLoginPageConfigEntity.class);
        if (StringUtils.isNotBlank(po.getUuid())) {
            AppLoginPageConfigEntity exist = appLoginPageConfigService.getOne(po.getUuid());
            BeanUtils.copyProperties(po, exist, IdEntity.BASE_FIELDS);
            po = exist;
        }
        String style = "def";
        if (StringUtils.isNotBlank(po.getPageStyle())) {
            style = po.getPageStyle().replaceAll("_", "");
        }
        BASE64Encoder encoder = new BASE64Encoder();
        if (pageBackgroundImage != null) {
            String encoderStr = encoder.encode(IOUtils.toByteArray(pageBackgroundImage.getInputStream()));
            po.setPageBackgroundImage(new SerialClob(encoderStr.toCharArray()));
        }
        if (pageLogo != null) {
            String encoderStr = encoder.encode(IOUtils.toByteArray(pageLogo.getInputStream()));
            po.setPageLogo(new SerialClob(encoderStr.toCharArray()));
        }

        appLoginPageConfigService.save(po);

        if ("1".equals(po.getEnableOauth())) {
            // 开启统一认证，单位登录页默认选中“默认登录页”
            appLoginPageConfigService.turnOffLoginPageSwitch();
        }

        Boolean flag = false;
        if (Config.DEFAULT_TENANT.equals(po.getSystemUnitId())) {//租户才需要配置
            MultiUserLoginSettingsEntity settingsEntity = (MultiUserLoginSettingsEntity) JSONObject.toBean(JSONObject.fromObject(loginType),
                    MultiUserLoginSettingsEntity.class);
            settingsEntity.setSystemUnitId(MultiOrgSystemUnit.PT_ID);
            flag = multiUserLoginSettingsService.saveLoginSettingsEntity(settingsEntity);
        }
        return ApiResult.success(flag);
    }

    @PostMapping("/saveUnitConf")
    @ResponseBody
    @ApiOperation(value = "保存", notes = "保存", tags = {"单位管理员-->登录页配置"})
    public ApiResult saveUnitConf(@RequestParam(value = "pageBackgroundImage", required = false) MultipartFile pageBackgroundImage,
                                  @RequestParam(value = "pageLogo", required = false) MultipartFile pageLogo,
                                  String data, String loginType,
                                  HttpServletRequest request) throws Exception {
        AppLoginPageConfigEntity po = (AppLoginPageConfigEntity) JSONObject.toBean(JSONObject.fromObject(data),
                AppLoginPageConfigEntity.class);
        if (StringUtils.isNotBlank(po.getUuid())) {
            AppLoginPageConfigEntity exist = appLoginPageConfigService.getOne(po.getUuid());
            if (StringUtils.isNotBlank(po.getUnitLoginPageSwitch()) && "1".equals(po.getUnitLoginPageSwitch())) {
                exist.setUnitLoginPageSwitch(po.getUnitLoginPageSwitch());
                exist.setUnitLoginPageUri(po.getUnitLoginPageUri());
                exist.setPageTitle(po.getPageTitle());
                exist.setPageStyle(po.getPageStyle());
                exist.setFooterContent(po.getFooterContent());
                po = exist;
            } else {
                exist.setUnitLoginPageSwitch(po.getUnitLoginPageSwitch());
                po = exist;
            }
        }

        BASE64Encoder encoder = new BASE64Encoder();
        if (pageBackgroundImage != null) {
            String encoderStr = encoder.encode(IOUtils.toByteArray(pageBackgroundImage.getInputStream()));
            po.setPageBackgroundImage(new SerialClob(encoderStr.toCharArray()));
        }
        if (pageLogo != null) {
            String encoderStr = encoder.encode(IOUtils.toByteArray(pageLogo.getInputStream()));
            po.setPageLogo(new SerialClob(encoderStr.toCharArray()));
        }

        appLoginPageConfigService.save(po);
        return ApiResult.success();
    }

    @GetMapping("/getLoginSettings")
    @ApiOperation(value = "获取登录配置", notes = "获取登录配置")
    public ApiResult getLoginSettings() {
        MultiUserLoginSettingsEntity loginSettings = multiUserLoginSettingsService.getLoginSettingsEntity();
        return ApiResult.success(loginSettings);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改", notes = "修改", tags = {"超管-->登录页配置"})
    public @ResponseBody
    ResultMessage update(@RequestBody AppLoginPageConfigEntity po) throws Exception {
        ResultMessage message = new ResultMessage();
        if (StringUtils.isNotBlank(po.getUuid())) {
            AppLoginPageConfigEntity exist = appLoginPageConfigService.getOne(po.getUuid());
            BeanUtils.copyProperties(po, exist, IdEntity.BASE_FIELDS);
            po = exist;
        }
        String style = "def";
        if (StringUtils.isNotBlank(po.getPageStyle())) {
            style = po.getPageStyle().replaceAll("_", "");
        }
        BASE64Encoder encoder = new BASE64Encoder();
        BASE64Decoder decoder = new BASE64Decoder();
        if (StringUtils.isNotBlank(po.getPageBackgroundImageBase64())) {
            po.setPageBackgroundImage(new SerialClob(po.getPageBackgroundImageBase64().toCharArray()));
        }
        if (StringUtils.isNotBlank(po.getPageLogoBase64())) {
            po.setPageLogo(new SerialClob(po.getPageLogoBase64().toCharArray()));
        }
        appLoginPageConfigService.save(po);
        return message;
    }

    @RequestMapping(value = "/preview", method = {RequestMethod.POST})
    @ApiOperation(value = "预览", notes = "跳转预览url", tags = {"超管-->登录页配置"})
    public String preview(@RequestBody Map<String, Object> pageConfig, HttpServletRequest request, Model model,
                          HttpServletResponse response) throws Exception {
        model.addAttribute("config", pageConfig);
        model.addAttribute("isPreview", true);

        // 背景图转为base64编码输出
        try {
            if (pageConfig.get("pageBackgroundImage") != null) {
                String imageBase64 = pageConfig.get("pageBackgroundImage").toString();
                if (StringUtils.isNotBlank(imageBase64)) {
                    model.addAttribute("backgroundImage", "url(" + imageBase64 + ")");
                }
            }

            if (pageConfig.get("headerImage") != null) {
                String imageBase64 = pageConfig.get("headerImage").toString();
                if (StringUtils.isNotBlank(imageBase64)) {
                    if ("_right".equalsIgnoreCase(pageConfig.get("pageStyle").toString())) {
                        model.addAttribute("headerImage", imageBase64);
                    } else {
                        model.addAttribute("headerImage", "url(" + imageBase64 + ")");
                    }
                }
            }
        } catch (Exception e) {
        }
        String style = "_right".equalsIgnoreCase(pageConfig.get("pageStyle").toString()) ? "_right" : "";
        // response.sendRedirect(forward("/user/domain_login3" + style));
        return forward("/user/domain_login3" + style);
    }

}
