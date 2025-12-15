package com.wellsoft.context.util.barcode;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-3.1	Administrator		2014-10-3		Create
 * </pre>
 * @date 2014-10-3
 */
public class WellBaseLineTextPainter implements WellTextPainter {

    private static WellTextPainter instance;

    private WellBaseLineTextPainter() {

    }

    public static WellTextPainter getInstance() {
        if (instance == null) {
            instance = new WellBaseLineTextPainter();
        }
        return instance;
    }

    @Override
    public void paintText(BufferedImage barcode, String text, int nWidth) {
        Graphics g2d = barcode.getGraphics();

        Font f = new Font("monospace", Font.PLAIN, 10 * nWidth); // 设置字体样式和大小
        g2d.setFont(f);// 设置图片字体
        FontMetrics fm = g2d.getFontMetrics();// 获取图片字体信息
        int h = fm.getHeight();
        int center = (barcode.getWidth() - fm.stringWidth(text)) / 2;

        g2d.setColor(Color.WHITE);// 设置背景颜色
        //passa uma linha limpando em cima
        g2d.fillRect(0, 0, barcode.getWidth(), barcode.getHeight() * 1 / 20);// 取图片高度的20之一为上部白色空隙（条形码与上部边沿空隙）
        //passa uma linha limpando em baixo
        g2d.fillRect(0, barcode.getHeight() - (h * 9 / 10), barcode.getWidth(), (h * 9 / 10));// 取图片字体相关的(h * 9 / 10)作为下部文本信息,(h * 1 / 10)是空隙2*(h * 1 / 20)

        //coloca o texto
        g2d.setColor(Color.BLACK);
        //texto primeiro quadrante
        g2d.drawString(text, center, barcode.getHeight() - (h / 10));// 条形码与文本空隙(h / 10)
    }

    @Override
    public void paintText(BufferedImage barcode, String projectIdText, String projectNameText, String text,
                          String itemNameText, int barWidth) {
        Graphics g2d = barcode.getGraphics();
        int fontTimes = 10, topOffSet = 8, buttomOffSet = 2, metrTimes = 7;
        Font f = new Font("monospace", Font.PLAIN, fontTimes * barWidth);
        g2d.setFont(f);
        FontMetrics fm = g2d.getFontMetrics();
        int h = fm.getHeight();
        int center = 0;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, barcode.getWidth(), h * metrTimes * 2 / 10);// 1.4
        g2d.fillRect(0, barcode.getHeight() - (h * metrTimes * 2 / 10), barcode.getWidth(), (h * metrTimes * 2 / 10));// 1.4

        g2d.setColor(Color.BLACK);
        center = (barcode.getWidth() - fm.stringWidth(projectIdText)) / 2;
        g2d.drawString(projectIdText, center, topOffSet);
        center = (barcode.getWidth() - fm.stringWidth(projectNameText)) / 2;
        g2d.drawString(projectNameText, center, topOffSet + (h * metrTimes) / 10);
        center = (barcode.getWidth() - fm.stringWidth(text)) / 2;
        g2d.drawString(text, center, barcode.getHeight() - buttomOffSet - (h * metrTimes) / 10);
        center = (barcode.getWidth() - fm.stringWidth(itemNameText)) / 2;
        g2d.drawString(itemNameText, center, barcode.getHeight() - buttomOffSet);
    }
}
