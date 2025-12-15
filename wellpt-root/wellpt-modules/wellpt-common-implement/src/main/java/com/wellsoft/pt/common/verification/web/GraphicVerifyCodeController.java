/*
 * @(#)Jun 23, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.verification.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.common.verification.entity.VerifyCode;
import com.wellsoft.pt.common.verification.service.VerifyCodeService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jun 23, 2017.1	zhulh		Jun 23, 2017		Create
 * </pre>
 * @date Jun 23, 2017
 */
@Controller
@RequestMapping("/common/verification/graphic/verify/code")
public class GraphicVerifyCodeController extends BaseController {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 35;
    private static final String VERIFY_CODE = "verifycode";
    private static final String REQUIRED_SMS_VERIFY_CODE = "requiredSmsVerifyCode";
    @Autowired
    private VerifyCodeService verifyCodeService;

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

            // 取随机产生的认证码(4位数字)
            VerifyCode verifyCode = verifyCodeService.createGraphicVerifyCode();
            session.setAttribute("verifycodeId", verifyCode.getId());
            // 在内存中创建图象
            BufferedImage image = createVerifyCodeImage(verifyCode.getCode());
            if (image == null) {
                return;
            }

            // 将认证码存入SESSION
            session.setAttribute(VERIFY_CODE, new String(verifyCode.getCode()));
            // 使用JPEG编码，输出到response的输出流
            ImageIO.write(image, "jpeg", response.getOutputStream());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @RequestMapping(value = "/show")
    public void show(HttpServletRequest request, HttpServletResponse response) {
        try {
            String verifyCode = request.getParameter(VERIFY_CODE);
            if (StringUtils.isBlank(verifyCode)) {
                return;
            }
            response.setContentType("image/jpeg"); // 必须设置ContentType为image/jpeg
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            // 在内存中创建图象
            BufferedImage image = createVerifyCodeImage(verifyCode);
            if (image == null) {
                return;
            }
            // 使用JPEG编码，输出到response的输出流
            ImageIO.write(image, "jpeg", response.getOutputStream());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    static Color getRandColor(final int fc, final int bc) {
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

    private static BufferedImage createVerifyCodeImage(String verifyCode) {
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

        if (verifyCode == null || StringUtils.isBlank(verifyCode)) {
            return null;
        }
        char[] sRand = new char[4];
        for (int i = 0; i < 4; i++) {
            sRand[i] = verifyCode.charAt(i);
            // 将认证码显示到图象中
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString("" + sRand[i], 15 * i + 3, 16 + (int) (Math.random() * (HEIGHT - 16)));
        }
        return image;
    }
}
