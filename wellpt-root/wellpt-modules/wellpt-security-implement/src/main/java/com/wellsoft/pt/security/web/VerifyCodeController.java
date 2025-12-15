/*
 * @(#)2013-4-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.web;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Description: 验证码
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-29.1	zhulh		2013-4-29		Create
 * </pre>
 * @date 2013-4-29
 */
@Controller
@RequestMapping(value = "/security/verifycode/")
public class VerifyCodeController extends BaseController {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 24;
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String VERIFY_CODE = "verifycode";
    private static final String SMS_VERIFY_CODE = "smsverifycode";
    private static final String REQUIRED_SMS_VERIFY_CODE = "requiredSmsVerifyCode";
    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @RequestMapping(value = "/image")
    public void createJPG(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            String rand = (String) session.getAttribute(VERIFY_CODE);
            String input = request.getParameter("rand");
            if (rand != null && input != null) {
                response.setContentType("text/html;charset=gb2312");
                PrintWriter out = response.getWriter();
                if (rand.equalsIgnoreCase(input)) {
                    out.println("SUCCESS");
                } else {
                    out.println("FAILURE");
                }
                session.setAttribute(VERIFY_CODE, "");
                return;
            }

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
            session.setAttribute(VERIFY_CODE, new String(sRand));

            // 使用JPEG编码，输出到response的输出流
            ImageIO.write(image, "jpeg", response.getOutputStream());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @RequestMapping(value = "/sms/send", method = RequestMethod.POST)
    public void sendSMS(@RequestParam(value = "tenantId") String tenantId,
                        @RequestParam(value = "j_username") String loginName,
                        @RequestParam(value = "mobilePhone") String mobilePhone, HttpServletRequest request,
                        HttpServletResponse response) {
        try {
            // 忽略登录
            ignorgLogin(tenantId, loginName);

            // 生成6位随机短信验证码
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int index = 0; index < 6; index++) {
                sb.append(random.nextInt(10));
            }
            String smsVerifyCode = sb.toString();
            HttpSession session = request.getSession();
            session.setAttribute(SMS_VERIFY_CODE, smsVerifyCode);
            session.setAttribute(REQUIRED_SMS_VERIFY_CODE, true);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("verifyCode", "");
            // 获取短信验证码超时时间
            int timeout = securityApiFacade.getSmsVerifyCodeTimeOut();
            map.put("timeout", timeout == 0 ? 60 : timeout);
            response.getOutputStream().write(JsonUtils.object2Json(map).getBytes());
            // 发送短信
            messageClientApiFacade.sendSmsMessages(SpringSecurityUtils.getCurrentUserId(), mobilePhone, "登录验证码: "
                    + smsVerifyCode, null, null, null);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            // 忽略登出
            ignorgLogout();
        }
    }

    @RequestMapping(value = "/sms/timeout")
    public void smsTimeout(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getSession().removeAttribute(SMS_VERIFY_CODE);

            response.setContentType(MediaType.TEXT_HTML_VALUE);
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            response.getOutputStream().write("success".getBytes());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    Color getRandColor(final int fc, final int bc) {
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

    public void drawBackground(Graphics g) {
        // 画背景
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // 随机产生120个干扰点
        for (int i = 0; i < 120; i++) {
            int x = (int) (Math.random() * WIDTH);
            int y = (int) (Math.random() * HEIGHT);
            int red = (int) (Math.random() * 255);
            int green = (int) (Math.random() * 255);
            int blue = (int) (Math.random() * 255);
            g.setColor(new Color(red, green, blue));
            g.drawOval(x, y, 1, 0);
        }
        // 画4条直线
        for (int i = 0; i < 4; i++) {
            int red = (int) (Math.random() * 255);
            int green = (int) (Math.random() * 255);
            int blue = (int) (Math.random() * 255);
            g.setColor(new Color(red, green, blue));
            g.drawLine((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT), (int) (Math.random() * WIDTH),
                    (int) (Math.random() * HEIGHT));
        }
    }

    @RequestMapping(value = "/sms/is/require", method = RequestMethod.POST)
    public void isRequiredSmsVerifyCode(@RequestParam(value = "tenantId") String tenantId,
                                        @RequestParam(value = "j_username") String loginName, HttpServletRequest request,
                                        HttpServletResponse response) {
        try {
            // 忽略登录
            ignorgLogin(tenantId, loginName);

            MultiOrgUserAccount user = orgApiFacade.getAccountByLoginName(loginName);
            if (user == null) {
                return;
            }
            OrgUserVo userVo = orgApiFacade.getUserVoById(user.getId());
            String userId = user.getId();
            boolean isRequiredSmsVerifyCode = securityApiFacade.isRequiredSmsVerifyCode(userId,
                    ServletUtils.getRemoteAddr(request));
            String mobilePhone = userVo.getMobilePhone();
            int len = mobilePhone.length();
            String rawMobilePhone = mobilePhone.substring(0, 2) + "*****" + mobilePhone.substring(len - 4, len - 1);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("isRequiredSmsVerifyCode", isRequiredSmsVerifyCode);
            data.put("mobilePhone", mobilePhone);
            data.put("rawMobilePhone", rawMobilePhone);
            response.getOutputStream().write(JsonUtils.object2Json(data).getBytes());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            // 忽略登出
            ignorgLogout();
        }
    }

    @RequestMapping(value = "/sms/check/verifycode")
    @ResponseBody
    public void checkSmsVerifyCode(@RequestParam(value = "j_sms_verify_code") String inputSmsVerifyCode,
                                   HttpServletRequest request) {
        // 登录验证码
        Object smsVerifyCode = request.getSession().getAttribute(SMS_VERIFY_CODE);
        if (smsVerifyCode == null || inputSmsVerifyCode == null
                || !smsVerifyCode.toString().equals(inputSmsVerifyCode.toString())) {
            throw new RuntimeException("请输入正确的短信验证码!");
        } else {
            request.getSession().setAttribute("checkedSmsVerifyCode", true);
        }
    }

    /**
     * @param tenantId
     * @param loginName
     */
    private void ignorgLogin(String tenantId, String loginName) {
        try {
            IgnoreLoginUtils.login(tenantId, loginName);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     *
     */
    private void ignorgLogout() {
        IgnoreLoginUtils.logout();
    }
}
