package com.wellsoft.context.util.barcode;

import java.awt.image.BufferedImage;

/**
 * Description: 文本绘画接口
 *
 * @author FashionSUN
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-3.1	FashionSUN		2014-10-3		Create
 * </pre>
 * @date 2014-10-3
 */
public interface WellTextPainter {

    /**
     * 如何描述该方法
     *
     * @param barcode
     * @param text     办件单号
     * @param barWidth
     */
    public void paintText(BufferedImage barcode, String text, int barWidth);

    /**
     * 自定义文本打印
     *
     * @param barcode
     * @param projectIdText   项目编号 reservedText5 reservedText8
     * @param projectNameText 项目名称
     * @param text            办件单号
     * @param itemNameText    事项名称 reservedText4
     * @param barWidth
     */
    public void paintText(BufferedImage barcode, String projectIdText, String projectNameText, String text,
                          String itemNameText, int barWidth);

}