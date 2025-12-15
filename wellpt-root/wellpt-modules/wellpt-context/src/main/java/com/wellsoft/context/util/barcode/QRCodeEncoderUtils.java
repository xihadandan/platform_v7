package com.wellsoft.context.util.barcode;

import com.swetake.util.Qrcode;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Description: 生成二维码
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年2月17日.1	zhulh		2016年2月17日		Create
 * </pre>
 * @date 2016年2月17日
 */
public class QRCodeEncoderUtils {

    public static void QRCodeEncoder(HttpServletRequest request, HttpServletResponse response, String content,
                                     int width, int height, int version) throws Exception {
        Qrcode qrcode = new Qrcode();
        qrcode.setQrcodeErrorCorrect('M');
        qrcode.setQrcodeEncodeMode('B');
        qrcode.setQrcodeVersion(version);
        byte[] d = content.getBytes("UTF-8");
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // createGraphics
        Graphics2D g = bi.createGraphics();
        // set background
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);
        g.setColor(Color.BLACK);

        if (d.length > 0) {
            boolean[][] b = qrcode.calQrcode(d);
            //System.out.println(b.length);
            for (int i = 0; i < b.length; i++) {
                for (int j = 0; j < b.length; j++) {
                    if (b[j][i]) {
                        g.fillRect(j * 3 + 2, i * 3 + 2, 3, 3);
                    }
                }
            }
        }

        g.dispose();
        bi.flush();

        response.setHeader("Prama", "no-cache");
        response.setHeader("Coche-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        ImageIO.write(bi, "png", response.getOutputStream());
    }

    /**
     * 生成二维码
     *
     * @param request
     * @param response
     * @param content
     * @throws Exception
     */
    public static void QRCodeEncoder(HttpServletRequest request, HttpServletResponse response, String content)
            throws Exception {
        int height = 0;
        int width = 0;
        int version = 0;
        //版本号增加，容量增加，相应的图像大小也要增加。在页面中的图片也要进行相应的增加
        String imageType = request.getParameter("imageType");
        if ("1".equals(imageType)) {
            width = 240;
            height = 240;
            version = 15;
        } else {
            width = 300;
            height = 300;
            version = 20;
        }

        QRCodeEncoder(request, response, content, width, height, version);
    }

}
