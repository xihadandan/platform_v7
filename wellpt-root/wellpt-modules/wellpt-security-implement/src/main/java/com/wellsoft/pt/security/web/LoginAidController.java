package com.wellsoft.pt.security.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.regex.RegexUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.common.verification.entity.VerifyCode;
import com.wellsoft.pt.common.verification.service.VerifyCodeService;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;
import com.wellsoft.pt.security.config.service.AppLoginPageConfigService;
import com.wellsoft.pt.security.core.authentication.CustomDaoAuthenticationProvider;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping(value = "/security/aid/")
public class LoginAidController extends BaseController {

    public static final int WIDTH = 64;
    public static final int HEIGHT = 24;
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private AppLoginPageConfigService appLoginPageConfigService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Autowired
    private VerifyCodeService verifyCodeService;

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @RequestMapping(value = "/image")
    public void createJPG(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();

            response.setContentType("image/jpeg"); // 必须设置ContentType为image/jpeg
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            // 在内存中创建图象
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            // 获取图形上下文
            Graphics g = image.getGraphics();
            Random random = new Random();
            // 设定背景色
            g.setColor(getRandColor(200, 250));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            // 设定字体
            g.setFont(new Font("Times New Roman", Font.ITALIC | Font.BOLD | Font.PLAIN, 18));
            // 画边框
            g.setColor(getRandColor(160, 200));
            // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
            for (int i = 0; i < 155; i++) {
                int x = random.nextInt(WIDTH);
                int y = random.nextInt(HEIGHT);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                g.drawLine(x, y, x + xl, y + yl);
            }
            // drawBackground(g);
            // 取随机产生的认证码(4位数字)
            char[] sRand = new char[4];
            for (int i = 0; i < 4; i++) {
                sRand[i] = CHARS.charAt(random.nextInt(CHARS.length()));
                // 将认证码显示到图象中
                g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
                g.drawString("" + sRand[i], 15 * i + 3, 16 + (int) (Math.random() * (HEIGHT - 16)));
            }
            // 将认证码存入SESSION
            session.setAttribute(CustomDaoAuthenticationProvider.IMAGE_CODE, new String(sRand));
            int timeout = 3600;
            AppLoginPageConfigEntity po = appLoginPageConfigService.getBySystemUnitId(Config.DEFAULT_TENANT);//FIXME:获取单位的登录页配置
            if (po != null && po.getLoginBoxAccountCodeTimeout() != null) {
                timeout = po.getLoginBoxAccountCodeTimeout();
            }
            session.setAttribute(CustomDaoAuthenticationProvider.IMAGE_CODE_TIMEOUT, System.currentTimeMillis()
                    + (timeout * 1000));

            // 使用JPEG编码，输出到response的输出流
            ImageIO.write(image, "jpeg", response.getOutputStream());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @RequestMapping(value = "/downloadFooterContentFile")
    @ResponseBody
    public void downloadFooterContentFile(@RequestParam("uuid") String uuid, HttpServletResponse response,
                                          HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            MongoFileEntity mongoFileEntity = mongoFileService.getFile(uuid);
            inputStream = mongoFileEntity.getInputstream();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("content-disposition",
                    "attachment;filename=" + URLEncoder.encode(mongoFileEntity.getFileName(), "UTF-8"));
            outputStream = response.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private Color getRandColor(final int fc, final int bc) {
        // 给定范围获得随机颜色
        Random random = new Random();
        int ffc = fc;
        int bbc = bc;
        if (ffc > 255) {
            ffc = 255;
        }
        if (bbc > 255) {
            bbc = 255;
        }
        int r = ffc + random.nextInt(bbc - ffc);
        int g = ffc + random.nextInt(bbc - ffc);
        int b = ffc + random.nextInt(bbc - ffc);
        return new Color(r, g, b);
    }

    @RequestMapping(value = "/checkImageCode", method = RequestMethod.POST)
    @ResponseBody
    public Map<Integer, String> checkImageCode(String code, HttpServletRequest request) {

        String imageCode = (String) request.getSession().getAttribute(CustomDaoAuthenticationProvider.IMAGE_CODE);

        Object timeout = request.getSession().getAttribute(CustomDaoAuthenticationProvider.IMAGE_CODE_TIMEOUT);
        if (imageCode == null || timeout == null || System.currentTimeMillis() > Long.parseLong(timeout.toString())) {
            return initMsg(1, "验证码超时!");
        }

        if (code == null || "".equals(code) || !imageCode.toUpperCase().equals(code.toUpperCase())) {
            return initMsg(1, "验证码错误");
        }

        return initMsg(0, "");
    }

    @RequestMapping(value = "/checkSmsCode", method = RequestMethod.POST)
    @ResponseBody
    public Map<Integer, String> checkSmsCode(String code, String username, HttpServletRequest request) {

        String smsCode = (String) request.getSession().getAttribute(CustomDaoAuthenticationProvider.SMS_CODE);
        Object timeout = request.getSession().getAttribute(CustomDaoAuthenticationProvider.SMS_CODE_TIMEOUT);
        Object smsUsername = request.getSession().getAttribute(CustomDaoAuthenticationProvider.SMS_USERNAME);

        if (smsCode == null || timeout == null || smsUsername == null
                || System.currentTimeMillis() > Long.parseLong(timeout.toString())) {
            return initMsg(1, "验证码超时!");
        }

        if (!smsUsername.equals(username) || !smsCode.equals(code)) {
            return initMsg(1, "验证码错误");
        }
        return initMsg(0, "");
    }

    /**
     * 创建图形验证码
     *
     * @return
     */
    @RequestMapping(value = "/createImageVerifyCode", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<VerifyCode> createImageVerifyCode() {
        return ApiResult.success(verifyCodeService.createGraphicVerifyCode());
    }

    /**
     * 发送手机短信
     *
     * @param mobilePhone
     * @param deviceId
     * @return
     */
    @RequestMapping(value = "/sendMobileSms", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<VerifyCode> sendMobileSms(@RequestParam(value = "mobilePhone") String mobilePhone,
                                               @RequestParam(value = "deviceId") String deviceId) {
        logger.info(String.format("Send mobile sms to [%s] from device: %s", mobilePhone, deviceId));
        if (!RegexUtils.isMobilePhone(mobilePhone)) {
            throw new BusinessException("手机号码填写错误");
        }
        long userCount = multiOrgUserService.countUserByMobilePhone(mobilePhone);
        if (userCount == 0) {
            throw new BusinessException("不存在手机号码对应的用户信息");
        } else if (userCount > 1) {
            throw new BusinessException("手机号码存在多个对应的用户信息");
        }
        return ApiResult.success(verifyCodeService.sendSmsVerifyCode(mobilePhone));
    }

    @RequestMapping(value = "/sendSms", method = RequestMethod.POST)
    @ResponseBody
    public Map<Integer, String> sendSms(@RequestParam(value = "username") String username, HttpServletRequest request) {

        MultiOrgUserAccount user = orgApiFacade.getAccountByLoginName(username);
        if (user == null) {
            return initMsg(1, "发送短信失败，输入账号错误！");
        }
        OrgUserVo userVo = orgApiFacade.getUserVoById(user.getId());
        if (userVo == null || userVo.getMobilePhone() == null) {
            return initMsg(1, "发送短信失败，账号没有关联手机");
        }
        String mobilePhone = userVo.getMobilePhone();

        String smsCode = String.valueOf(1 + (int) (Math.random() * 999999));

        int timeout = 3600;
        AppLoginPageConfigEntity po = appLoginPageConfigService.getBySystemUnitId(userVo.getSystemUnitId());
        if (po == null) {
            po = appLoginPageConfigService.getBySystemUnitId(Config.DEFAULT_TENANT);
        }
        if (po != null && po.getLoginBoxAccountSmsTimeout() != null) {
            timeout = po.getLoginBoxAccountSmsTimeout();
        }
        HttpSession session = request.getSession();
        session.setAttribute(CustomDaoAuthenticationProvider.SMS_CODE_TIMEOUT, System.currentTimeMillis()
                + (timeout * 1000));
        session.setAttribute(CustomDaoAuthenticationProvider.SMS_CODE, smsCode);
        session.setAttribute(CustomDaoAuthenticationProvider.SMS_USERNAME, username);

        System.out.println("短信内容为：" + smsCode);
        // 发送短信
        messageClientApiFacade.sendSmsMessages(userVo.getId(), mobilePhone, "登录验证码: " + smsCode, null, null, null);

        return initMsg(0, "");

    }

    private Map<Integer, String> initMsg(int key, String value) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(key, value);
        return map;
    }

}
