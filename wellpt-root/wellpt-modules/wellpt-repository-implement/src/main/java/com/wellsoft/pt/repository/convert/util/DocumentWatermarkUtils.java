package com.wellsoft.pt.repository.convert.util;

import com.spire.doc.Document;
import com.spire.doc.PictureWatermark;
import com.spire.doc.TextWatermark;
import com.spire.doc.documents.WatermarkLayout;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.*;
import com.spire.presentation.FileFormat;
import com.spire.presentation.ISlide;
import com.spire.presentation.Presentation;
import com.spire.presentation.drawing.BackgroundType;
import com.spire.presentation.drawing.FillFormatType;
import com.spire.presentation.drawing.IImageData;
import com.spire.presentation.drawing.PictureFillType;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.repository.entity.WatermarkStyle;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年07月17日   chenq	 Create
 * </pre>
 */
public class DocumentWatermarkUtils {

    private static Logger logger = LoggerFactory.getLogger(DocumentWatermarkUtils.class);
    public static Font jFont = null;

    static {
        SpireLicenseInstall.installLicense();
        try {
            InputStream fontStream = DocumentWatermarkUtils.class.getClassLoader().getResourceAsStream("fonts/AlibabaPuHuiTi-3-55-Regular.ttf");
            jFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (Exception e) {
            logger.error("水印工具静态初始化字体异常: ", e);
        }
    }

    public static void main(String[] args) throws Exception {
        WatermarkStyle style = new WatermarkStyle();
        style.setType(WatermarkStyle.WatermarkType.text);
        style.setText("威尔信息计算有限公司\n2025\n产品研发中心");
        style.setFontSize(24);
        style.setOpacity(new BigDecimal("1"));
//        style.setImage(ImageIO.read(new FileInputStream(new File("C:\\Users\\15959\\Desktop\\平台管理后台240304\\1.2威搭.png"))));
        style.setLayout(WatermarkStyle.WatermarkLayout.diagonal);
        style.setVerticalAlign(WatermarkStyle.Align.bottom);
        style.setHorizontalAlign(WatermarkStyle.Align.left);
        addDocWatermark(new FileInputStream(new File("C:\\Users\\15959\\Desktop\\新建文件夹\\泛微和宜搭的优势和劣势.docx"))
                , new FileOutputStream(new File("C:\\Users\\15959\\Desktop\\新建文件夹\\泛微和宜搭的优势和劣势-水印.docx")), style);
//        addPdfBoxWatermark(new FileInputStream(new File("C:\\Users\\15959\\Desktop\\新建文件夹\\郭柏雅个人介绍.pdf"))
//                , new FileOutputStream(new File("C:\\Users\\15959\\Desktop\\新建文件夹\\郭柏雅个人介绍-水印.pdf")), style);
//        addPptWatermark(new FileInputStream(new File("C:\\Users\\15959\\Desktop\\新建文件夹\\汇报模板.pptx"))
//                , new FileOutputStream(new File("C:\\Users\\15959\\Desktop\\新建文件夹\\汇报模板-水印.pptx")), style);
//        createTextImage("威尔信息", style.getFontFamily(), Font.PLAIN, style.getFontSize(), style.getColor(), "C:\\Users\\15959\\Desktop\\新建文件夹\\222.png", "png");
//        createTextImage("第一行\n第二行了呀\n第三行", jFont.deriveFont(Font.BOLD).deriveFont(48f)
//                , Color.RED, "center", 45d);
    }

    public static void addPdfWatermark(InputStream inputStream, OutputStream outputStream
            , String watermarkText) throws Exception {
        addPdfWatermark(inputStream, outputStream, new WatermarkStyle(watermarkText));
    }

    public static void addPdfBoxWatermark(InputStream inputStream, OutputStream outputStream
            , WatermarkStyle watermarkStyle) throws Exception {
        PDDocument document = PDDocument.load(inputStream);
        InputStream fontStream = DocumentWatermarkUtils.class.getClassLoader().getResourceAsStream("fonts/AlibabaPuHuiTi-3-55-Regular.ttf");
        PDType0Font pdfFont = PDType0Font.load(document, fontStream);
        IOUtils.closeQuietly(fontStream);
        int pageWidth = (int) document.getPage(0).getMediaBox().getWidth();
        int pageHeight = (int) document.getPage(0).getMediaBox().getHeight();
        int offsetX = 0, offsetY = 0;
        BufferedImage watermarkPic = watermarkStyle.getImage();
        boolean isRotated = WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout());
        if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
            if (StringUtils.isNotBlank(watermarkStyle.getImageFileId())) {
                MongoFileEntity mongoFileEntity = ApplicationContextHolder.getBean(MongoFileService.class).getFile(watermarkStyle.getImageFileId());
                if (mongoFileEntity != null) {
                    watermarkPic = ImageIO.read(mongoFileEntity.getInputstream());
                }
            }

            if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
                watermarkPic = rotateImage(watermarkPic, -45);
            }
            int imageWidth = watermarkPic.getWidth();
            int imageHeight = watermarkPic.getHeight();
            // 计算图片坐标，PDF坐标系原点(0,0)在页面左下角
            if (WatermarkStyle.Align.top.equals(watermarkStyle.getVerticalAlign())) {
                offsetY = pageHeight - imageHeight;
            } else if (WatermarkStyle.Align.bottom.equals(watermarkStyle.getVerticalAlign())) {
                offsetY = 0;
            } else {
                offsetY = pageHeight / 2 - imageHeight / 2;
            }
            if (WatermarkStyle.Align.left.equals(watermarkStyle.getHorizontalAlign())) {
                offsetX = 0;
            } else if (WatermarkStyle.Align.right.equals(watermarkStyle.getHorizontalAlign())) {
                offsetX = pageWidth - imageWidth;
            } else {
                offsetX = pageWidth / 2 - imageWidth / 2;
            }
        }


        Iterator<PDPage> pageIterator = document.getPages().iterator();
        PDImageXObject pdImage = null;
        if (watermarkPic != null) {
            pdImage = LosslessFactory.createFromImage(document, watermarkPic);
        }
        while ((pageIterator.hasNext())) {
            PDPage page = pageIterator.next();
            if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND,  // 在现有内容上追加
                        true,   // 压缩内容流
                        true    // 重置图形状态x
                )) {
                    if (watermarkStyle.getOpacity() != null) {
                        // 设置透明度
                        PDExtendedGraphicsState state = new PDExtendedGraphicsState();
                        state.setNonStrokingAlphaConstant(watermarkStyle.getOpacity().floatValue());
                        contentStream.setGraphicsStateParameters(state);
                    }
                    contentStream.drawImage(pdImage, offsetX, offsetY, watermarkPic.getWidth(), watermarkPic.getHeight());
                }
            } else {

                try (PDPageContentStream contentStream = new PDPageContentStream(
                        document, page,
                        PDPageContentStream.AppendMode.APPEND,
                        true, true)) {
                    if (watermarkStyle.getOpacity() != null) {
                        // 设置透明度
                        PDExtendedGraphicsState state = new PDExtendedGraphicsState();
                        state.setNonStrokingAlphaConstant(watermarkStyle.getOpacity().floatValue());
                        contentStream.setGraphicsStateParameters(state);
                    }

                    contentStream.setFont(pdfFont, watermarkStyle.getFontSize());
                    contentStream.setNonStrokingColor(watermarkStyle.getColor());
                    String[] texts = watermarkStyle.getText().split("\\n");
                    // 计算文本宽度和高度
                    float lineHeight = watermarkStyle.getFontSize() * 1.2f;
                    int maxTextWidth = 0;
                    int[] textWidths = new int[texts.length];
                    for (int i = 0, len = texts.length; i < len; i++) {
                        String text = texts[i];
                        textWidths[i] = (int) (pdfFont.getStringWidth(text) / 1000 * watermarkStyle.getFontSize());
                        maxTextWidth = Math.max(textWidths[i], maxTextWidth);
                    }
                    float totalTextHeight = lineHeight * texts.length;
                    float textY = 0;
                    // 保存当前图形状态
                    contentStream.saveGraphicsState();
                    int transformX = 0, transformY = 0;
                    if (isRotated) {
                        if (WatermarkStyle.Align.top.equals(watermarkStyle.getVerticalAlign())) {
                            transformY = (int) (pageHeight - maxTextWidth / 2 - lineHeight / 2);
                        } else if (WatermarkStyle.Align.bottom.equals(watermarkStyle.getVerticalAlign())) {
                            transformY = (int) ((maxTextWidth / 2 + lineHeight / 2) * 0.707);
                        } else {
                            transformY = pageHeight / 2;
                            textY = totalTextHeight / 2;
                        }
                        if (WatermarkStyle.Align.left.equals(watermarkStyle.getHorizontalAlign())) {
                            transformX = maxTextWidth / 2;
                        } else if (WatermarkStyle.Align.right.equals(watermarkStyle.getHorizontalAlign())) {
                            transformX = pageWidth - maxTextWidth / 2;
                        } else {
                            transformX = pageWidth / 2;
                            textY = totalTextHeight / 2;
                        }
                        contentStream.transform(Matrix.getRotateInstance(Math.toRadians(45), transformX, transformY));
                    } else {
                        if (WatermarkStyle.Align.top.equals(watermarkStyle.getVerticalAlign())) {
                            transformY = (int) (pageHeight - totalTextHeight / 2 + lineHeight / 2);
                        } else if (WatermarkStyle.Align.bottom.equals(watermarkStyle.getVerticalAlign())) {
                            transformY = (int) (totalTextHeight - lineHeight / 2);
                        } else {
                            transformY = pageHeight / 2;
                            textY = totalTextHeight / 2;
                        }
                        if (WatermarkStyle.Align.left.equals(watermarkStyle.getHorizontalAlign())) {
                            transformX = 10 /* 间距 */ + maxTextWidth / 2;
                        } else if (WatermarkStyle.Align.right.equals(watermarkStyle.getHorizontalAlign())) {
                            transformX = pageWidth - maxTextWidth / 2 - 10 /*间距*/;
                        } else {
                            transformX = pageWidth / 2;
                        }
                        contentStream.transform(Matrix.getTranslateInstance(transformX, transformY));
                    }

                    for (int i = 0, len = texts.length; i < len; i++) {
                        String text = texts[i];
                        contentStream.beginText();
                        contentStream.newLineAtOffset(-textWidths[i] / 2, textY);
                        contentStream.showText(text);
                        contentStream.endText();
                        textY -= lineHeight;
                    }
                }
            }

        }
        document.save(outputStream);
    }


    public static void addPdfWatermark(InputStream inputStream, OutputStream outputStream
            , WatermarkStyle watermarkStyle) throws Exception {
        // 创建 PdfDocument 对象
        PdfDocument doc = new PdfDocument();
        // 加载 PDF 文档
        doc.loadFromStream(inputStream);
        Font f = null;
        PdfTrueTypeFont font = null;
        PdfBrush brush = null;
        boolean isRotated = WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout());
        BufferedImage watermarkPic = watermarkStyle.getImage();
        if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
            if (StringUtils.isNotBlank(watermarkStyle.getImageFileId())) {
                MongoFileEntity mongoFileEntity = ApplicationContextHolder.getBean(MongoFileService.class).getFile(watermarkStyle.getImageFileId());
                if (mongoFileEntity != null) {
                    watermarkPic = ImageIO.read(mongoFileEntity.getInputstream());
                }
            }

            if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
//                watermarkPic = rotateImage(watermarkPic, -45);
            }
        } else {
            // 创建字体和画刷
            f = new Font(StringUtils.isBlank(watermarkStyle.getFontFamily()) ? "微软雅黑"
                    : watermarkStyle.getFontFamily(), Font.PLAIN, watermarkStyle.getFontSize());
            font = new PdfTrueTypeFont(f, true);
            Color color = watermarkStyle.getColor();
            brush = new PdfSolidBrush(new PdfRGBColor(color));
        }
        // 遍历页面
        for (int i = 0; i < doc.getPages().getCount(); i++) {
            PdfPageBase page = doc.getPages().get(i);
            // 获取页面的宽度和高度
            int pageWidth = (int) page.getActualSize().getWidth();
            int pageHeight = (int) page.getActualSize().getHeight();
            int margin = (int) doc.getPageSettings().getMargins().getRight();
//            pageWidth += margin * 2;
//            pageHeight += margin * 2;
            // 设置水印的透明度
            page.getCanvas().setTransparency(watermarkStyle.getOpacity() != null ? watermarkStyle.getOpacity().floatValue() : 0f);
            if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
                int imageWidth = watermarkPic.getWidth();
                int imageHeight = watermarkPic.getHeight();
                int offsetX = 0, offsetY = 0;
                if (WatermarkStyle.Align.top.equals(watermarkStyle.getVerticalAlign())) {
                    offsetY = 0;
                } else if (WatermarkStyle.Align.bottom.equals(watermarkStyle.getVerticalAlign())) {
                    offsetY = pageHeight - imageHeight;
                } else {
                    offsetY = pageHeight / 2 - imageHeight / 2;
                }
                if (WatermarkStyle.Align.left.equals(watermarkStyle.getHorizontalAlign())) {
                    offsetX = 0;
                } else if (WatermarkStyle.Align.right.equals(watermarkStyle.getHorizontalAlign())) {
                    offsetX = pageWidth - imageWidth + margin / 2; // FIXME: spire canvas 有问题?
                } else {
                    offsetX = pageWidth / 2 - imageWidth / 2;
                }


                System.out.println(pageWidth + "," + pageHeight + " ->" + imageWidth + ',' + imageHeight + " " + offsetX + " , " + offsetY);
                page.getCanvas().translateTransform(offsetX, offsetY);
                page.getCanvas().drawImage(PdfImage.fromImage(watermarkPic), 0, 0);

            } else {
                // 计算文本尺寸
                PdfStringFormat format = new PdfStringFormat();
                Dimension2D textSize = font.measureString(watermarkStyle.getText(), format);
                int textHeight = (int) textSize.getHeight();
                int textWidth = (int) textSize.getWidth();
                int rotatedTextHeight = 0, rotatedTextWidth = 0;
                if (isRotated) {
                    Rectangle2D rectangle2D = getRotatedTextRect(f, watermarkStyle.getText());
                    rotatedTextHeight = (int) rectangle2D.getHeight();
                    rotatedTextWidth = (int) rectangle2D.getWidth();
                }
                int offsetX = 0, offsetY = 0;
                if (WatermarkStyle.Align.top.equals(watermarkStyle.getVerticalAlign())) {
                    offsetY = isRotated ? rotatedTextHeight : 0;
                } else if (WatermarkStyle.Align.bottom.equals(watermarkStyle.getVerticalAlign())) {
                    offsetY = pageHeight - textHeight;
                } else {
                    offsetY = pageHeight / 2 - (isRotated ? -(int) (rotatedTextHeight / 2) : textHeight / 2);
                }
                if (WatermarkStyle.Align.left.equals(watermarkStyle.getHorizontalAlign())) {
                    offsetX = 0;
                } else if (WatermarkStyle.Align.right.equals(watermarkStyle.getHorizontalAlign())) {
                    offsetX = pageWidth - (isRotated ? rotatedTextWidth + textHeight : textWidth);
                } else {
                    offsetX = pageWidth / 2 - (isRotated ? rotatedTextWidth : textWidth) / 2;
                }

                page.getCanvas().translateTransform(offsetX, offsetY);
                // 应用旋转
                if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
                    page.getCanvas().rotateTransform(-45);
                }
                page.getCanvas().drawString(
                        watermarkStyle.getText(),
                        font,
                        brush, 0, 0,
                        format
                );
            }
        }
        // 将更改保存到另一个文件
        doc.saveToStream(outputStream);
        // 释放资源
        doc.dispose();
    }

    public static BufferedImage rotateImage(BufferedImage original, double angle) {
        // 计算旋转后的新尺寸
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        int newWidth = (int) Math.round(originalWidth * cos + originalHeight * sin);
        int newHeight = (int) Math.round(originalWidth * sin + originalHeight * cos);

        // 创建新的BufferedImage
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, original.getType());
        Graphics2D g2d = rotated.createGraphics();

        // 计算旋转中心点并执行旋转
        AffineTransform at = new AffineTransform();
        at.translate(newWidth / 2, newHeight / 2);
        at.rotate(radians);
        at.translate(-originalWidth / 2, -originalHeight / 2);

        g2d.setTransform(at);
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public static Rectangle2D getRotatedTextRect(Font font, String text) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        TextLayout layout = new TextLayout(text, font, frc);

        // 获取未旋转时的边界
        Shape outline = layout.getOutline(null);
        Rectangle2D bounds = outline.getBounds2D();

        // 创建45度旋转变换
        AffineTransform rotate = AffineTransform.getRotateInstance(
                Math.toRadians(-45),
                bounds.getCenterX(),
                bounds.getCenterY()
        );

        // 应用旋转并获取新的边界
        Shape rotatedOutline = rotate.createTransformedShape(outline);
        Rectangle2D rotatedBounds = rotatedOutline.getBounds2D();

        return rotatedBounds;
    }

    public static void addExcelWatermark(InputStream inputStream, OutputStream outputStream
            , String watermarkText) throws Exception {
        addExcelWatermark(inputStream, outputStream, new WatermarkStyle(watermarkText));
    }

//

    public static void addExcelWatermark(InputStream inputStream, OutputStream outputStream
            , WatermarkStyle watermarkStyle) throws Exception {
        if ((WatermarkStyle.WatermarkType.text.equals(watermarkStyle.getType()) && StringUtils.isBlank(watermarkStyle.getText()))
                || (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) && StringUtils.isBlank(watermarkStyle.getImageFileId())) {
            return;
        }
        Workbook workbook = new Workbook();
        workbook.loadFromStream(inputStream);
        int width = (int) workbook.getWorksheets().get(0).getPageSetup().getPageWidth();
        int height = (int) workbook.getWorksheets().get(0).getPageSetup().getPageHeight();
        int screenDpi = Toolkit.getDefaultToolkit().getScreenResolution();
//        width = (width / 72) * screenDpi;
//        height = (height / 72) * screenDpi;
        BufferedImage image = null;
        Font font = new Font(watermarkStyle.getFontFamily(), Font.PLAIN, watermarkStyle.getFontSize());
        if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
            if (StringUtils.isNotBlank(watermarkStyle.getImageFileId())) {
                MongoFileEntity picture = ApplicationContextHolder.getBean(MongoFileService.class).getFile(watermarkStyle.getImageFileId());
                if (picture == null) {
                    return;
                }
                image = ImageIO.read(picture.getInputstream());
            } else if (watermarkStyle.getImage() != null) {
                image = createWatermarkImage(width, height, watermarkStyle.getImage(), null, null
                        , WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout()) ? -45d : null, convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign()));
            }
        } else {
            image = createWatermarkImage(width, height, watermarkStyle.getText(), watermarkStyle.getFontFamily(), watermarkStyle.getFontSize()
                    , watermarkStyle.getColor(), watermarkStyle.getOpacity() == null ? null : watermarkStyle.getOpacity().floatValue(),
                    WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout()) ? -45d : null, convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign()));
//            image = createTextImage(watermarkStyle.getText(), watermarkStyle.getFontFamily(), Font.PLAIN, watermarkStyle.getFontSize(), watermarkStyle.getColor(),
//                    WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout()) ? -45d : null);
        }

        for (Worksheet sheet : (Iterable<Worksheet>) workbook.getWorksheets()) {

           /* if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
                MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
                MongoFileEntity picture = mongoFileService.getFile(watermarkStyle.getImageFileId());
                if (picture == null) {
                    return;
                }
                Thumbnails.Builder builder = null;
                if (BooleanUtils.isTrue(watermarkStyle.getWashout())) {
//                    builder = Thumbnails.of(washoutImage(picture));
                } else {
                    builder = Thumbnails.of(picture.getInputstream());
                }
                if (watermarkStyle.getScale() != null) {
                    builder.scale(watermarkStyle.getScale().divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP).floatValue());
                }
                if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
                    builder.rotate(-45d);
                }
                image = builder.asBufferedImage();
            } else {
                //定义图片宽度和高度
                image = new BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D loGraphic = image.createGraphics();

                //获取文本size
                FontMetrics loFontMetrics = loGraphic.getFontMetrics(font);
                int liStrWidth = loFontMetrics.stringWidth(watermarkStyle.getText());
                int liStrHeight = loFontMetrics.getHeight();

                //文本显示样式及位置
                loGraphic.setColor(Color.white);
                loGraphic.fillRect(0, 0, (int) width, (int) height);
//                loGraphic.translate(((int) width - liStrWidth) / 2, ((int) height - liStrHeight) / 2);
//                if (WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout())) {
//                    loGraphic.rotate(Math.toRadians(-45));
//                }

//                loGraphic.translate(-((int) width - liStrWidth) / 2, -((int) height - liStrHeight) / 2);
                loGraphic.translate(0, 0);
                loGraphic.setFont(font);
                loGraphic.setColor(StringUtils.isBlank(watermarkStyle.getFontColor()) ? Color.GRAY : convertHexColor(watermarkStyle.getFontColor()));
                loGraphic.drawString(watermarkStyle.getText(), ((int) width - liStrWidth) / 2, ((int) height - liStrHeight) / 2);
                loGraphic.dispose();
            }*/

            //将图片设置为页眉
            sheet.getPageSetup().setLeftHeaderImage(image);
            sheet.getPageSetup().setLeftHeader("&G");

            //将显示模式设置为Layout
//            sheet.setViewMode(ViewMode.Layout);
        }

        workbook.saveToStream(outputStream);

    }

    public static void addDocWatermark(InputStream inputStream, OutputStream outputStream
            , TextWatermark textWatermark) throws Exception {
        Document doc = new Document();
        doc.loadFromStream(inputStream, com.spire.doc.FileFormat.Auto);
        doc.setWatermark(textWatermark);
        //保存文档
        doc.saveToFile(outputStream, com.spire.doc.FileFormat.Auto);
    }

    public static void addDocWatermark(InputStream inputStream, OutputStream outputStream
            , PictureWatermark pictureWatermark) throws Exception {
        Document doc = new Document();
        doc.loadFromStream(inputStream, com.spire.doc.FileFormat.Auto);
        doc.setWatermark(pictureWatermark);
        //保存文档
        doc.saveToFile(outputStream, com.spire.doc.FileFormat.Auto);
    }


    public static void addDocWatermark(InputStream inputStream, OutputStream outputStream
            , WatermarkStyle watermarkStyle) throws Exception {
        if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
            if (StringUtils.isBlank(watermarkStyle.getImageFileId())) {
                return;
            }
            MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
            MongoFileEntity picture = mongoFileService.getFile(watermarkStyle.getImageFileId());
            if (picture == null) {
                return;
            }

            Document doc = new Document();
            doc.loadFromStream(inputStream, com.spire.doc.FileFormat.Auto);
            int width = (int) doc.getSections().get(0).getPageSetup().getPageSize().getWidth();
            int height = (int) doc.getSections().get(0).getPageSetup().getPageSize().getHeight() + (int) doc.getSections().get(0).getPageSetup().getHeaderDistance() + (int) doc.getSections().get(0).getPageSetup().getFooterDistance();
            BufferedImage bufferedImage = createWatermarkImage(width, height, ImageIO.read(picture.getInputstream())
                    , watermarkStyle.getScale() == null ? null : watermarkStyle.getScale().doubleValue(), watermarkStyle.getOpacity() == null ? null : watermarkStyle.getOpacity().floatValue()
                    , WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout()) ? -45d : null, convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign()));
            PictureWatermark watermark = new PictureWatermark();
            watermark.setPicture(bufferedImage);
            watermark.isWashout(false);
            doc.setWatermark(watermark);
            //保存文档
            doc.saveToFile(outputStream, com.spire.doc.FileFormat.Auto);
        } else {
            if (StringUtils.isBlank(watermarkStyle.getText())) {
                return;
            }

            Document doc = new Document();
            doc.loadFromStream(inputStream, com.spire.doc.FileFormat.Auto);
            int width = (int) doc.getSections().get(0).getPageSetup().getPageSize().getWidth();
            int height = (int) doc.getSections().get(0).getPageSetup().getPageSize().getHeight() + (int) doc.getSections().get(0).getPageSetup().getHeaderDistance() + (int) doc.getSections().get(0).getPageSetup().getFooterDistance();
            BufferedImage bufferedImage = createWatermarkImage(width, height
                    , watermarkStyle.getText(), watermarkStyle.getFontFamily(), watermarkStyle.getFontSize(),
                    convertHexColor(watermarkStyle.getFontColor()), watermarkStyle.getOpacity() == null ? null : watermarkStyle.getOpacity().floatValue(),
                    WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout()) ? -45d : null, convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign()));
            PictureWatermark watermark = new PictureWatermark();
            watermark.setPicture(bufferedImage);
            watermark.isWashout(false);
            doc.setWatermark(watermark);
            //保存文档
            doc.saveToFile(outputStream, com.spire.doc.FileFormat.Auto);
        }

    }

    public static Positions convertPositions(WatermarkStyle.Align verticalAlign, WatermarkStyle.Align horizontalAlign) {
        if (verticalAlign != null && horizontalAlign != null) {
            if (WatermarkStyle.Align.center.equals(verticalAlign) && verticalAlign.equals(horizontalAlign)) {
                return Positions.CENTER;
            }
            return Positions.valueOf((verticalAlign == null ? WatermarkStyle.Align.bottom.name().toUpperCase() : verticalAlign.name().toUpperCase()) + "_" + (horizontalAlign == null ? WatermarkStyle.Align.left.name().toUpperCase() : horizontalAlign.name().toUpperCase()));

        }
        return Positions.CENTER;
    }


    private static Color convertHexColor(String hex) {
        if (StringUtils.isNotBlank(hex)) {
            if (hex.startsWith("#")) {
                hex = hex.substring(1); // 去掉 #
            }

            // 解析 Alpha、Red、Green、Blue 分量
            int a, r, g, b;

            if (hex.length() == 8) { // #AARRGGBB 格式（带 Alpha）
                a = Integer.parseInt(hex.substring(0, 2), 16);
                r = Integer.parseInt(hex.substring(2, 4), 16);
                g = Integer.parseInt(hex.substring(4, 6), 16);
                b = Integer.parseInt(hex.substring(6, 8), 16);
            } else if (hex.length() == 6) { // #RRGGBB 格式（不带 Alpha，默认不透明）
                a = 255; // 完全不透明
                r = Integer.parseInt(hex.substring(0, 2), 16);
                g = Integer.parseInt(hex.substring(2, 4), 16);
                b = Integer.parseInt(hex.substring(4, 6), 16);
            } else {
                throw new IllegalArgumentException("Invalid hex color format: " + hex);
            }
            return new Color(r, g, b, a);
        }
        return null;
    }

    public static void addDocWatermark(InputStream inputStream, OutputStream outputStream
            , String watermarkText) throws Exception {

        // 创建文字水印
        TextWatermark watermark = new TextWatermark();
        watermark.setText(watermarkText);
        watermark.setFontName("宋体");
        watermark.setFontSize(120);
        watermark.setColor(Color.GRAY);
        watermark.setLayout(WatermarkLayout.Diagonal); // Diagonal或Horizontal
        addDocWatermark(inputStream, outputStream, watermark);
    }

    public static void addDocWatermark(InputStream inputStream, OutputStream outputStream
            , InputStream image) throws Exception {

        PictureWatermark pictureWatermark = new PictureWatermark();
        //设置图像水印格式
        pictureWatermark.setScaling(100);
        pictureWatermark.setPicture(image);
        pictureWatermark.isWashout(true);


        addDocWatermark(inputStream, outputStream, pictureWatermark);
    }


    public static void addPptWatermark(InputStream inputStream, OutputStream outputStream
            , WatermarkStyle watermarkStyle) throws Exception {
        if ((WatermarkStyle.WatermarkType.text.equals(watermarkStyle.getType()) && StringUtils.isBlank(watermarkStyle.getText()))
                || (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType()) && StringUtils.isBlank(watermarkStyle.getImageFileId()) && watermarkStyle.getImage() == null)) {
            return;
        }
        Presentation presentation = new Presentation();
        presentation.loadFromStream(inputStream, FileFormat.AUTO);
        Dimension2D slideSize = presentation.getSlideSize().getSize();
        int slideWidth = (int) slideSize.getWidth();
        int slideHeight = (int) slideSize.getHeight();
        Iterator<ISlide> iterator = presentation.getSlides().iterator();
        IImageData image = null;
        if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
            if (StringUtils.isNotBlank(watermarkStyle.getImageFileId())) {
                MongoFileEntity picture = ApplicationContextHolder.getBean(MongoFileService.class).getFile(watermarkStyle.getImageFileId());
                if (picture == null) {
                    return;
                }
                image = presentation.getImages().append(ImageIO.read(picture.getInputstream()));
            } else if (watermarkStyle.getImage() != null) {
                image = presentation.getImages().append(createWatermarkImage(slideWidth, slideHeight, watermarkStyle.getImage(), null, null
                        , WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout()) ? -45d : null, convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign())));
            }
        } else {
            image = presentation.getImages().append(createWatermarkImage(slideWidth, slideHeight, watermarkStyle.getText(), watermarkStyle.getFontFamily(), watermarkStyle.getFontSize()
                    , watermarkStyle.getColor(), watermarkStyle.getOpacity() == null ? null : watermarkStyle.getOpacity().floatValue(),
                    WatermarkStyle.WatermarkLayout.diagonal.equals(watermarkStyle.getLayout()) ? -45d : null, convertPositions(watermarkStyle.getVerticalAlign(), watermarkStyle.getHorizontalAlign())));
        }
        while (iterator.hasNext()) {
            ISlide slide = iterator.next();
            if (WatermarkStyle.WatermarkType.picture.equals(watermarkStyle.getType())) {
                //获取幻灯片背景属性，设置图片填充
                slide.getSlideBackground().setType(BackgroundType.CUSTOM);
                slide.getSlideBackground().getFill().setFillType(FillFormatType.PICTURE);
                slide.getSlideBackground().getFill().getPictureFill().setFillType(PictureFillType.STRETCH);
                slide.getSlideBackground().getFill().getPictureFill().getPicture().setEmbedImage(image);
            } else {
                //获取幻灯片背景属性，设置图片填充
                slide.getSlideBackground().setType(BackgroundType.CUSTOM);
                slide.getSlideBackground().getFill().setFillType(FillFormatType.PICTURE);
                slide.getSlideBackground().getFill().getPictureFill().setFillType(PictureFillType.STRETCH);
                slide.getSlideBackground().getFill().getPictureFill().getPicture().setEmbedImage(image);
            }
        }
        //保存文档
        presentation.saveToFile(outputStream, FileFormat.AUTO);

    }

    public static void addPptWatermark(InputStream inputStream, OutputStream outputStream
            , String watermarkText) throws Exception {
        addPptWatermark(inputStream, outputStream, new WatermarkStyle(watermarkText));

    }


    public static BufferedImage createWatermarkImage(int width, int height, BufferedImage watermarkPic, Double scale, Float opacity, Double rotate, Positions positions) throws Exception {
        BufferedImage container = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        File tempFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + ".png");
        ImageIO.write(container, "png", tempFile);
        // 计算缩放比例：当图片超过页面尺寸时候，进行适当缩放
        double widthRatio = (double) width / watermarkPic.getWidth();
        double heightRatio = (double) height / watermarkPic.getHeight();
        double ratio = Math.min(widthRatio, heightRatio);
        double s = scale == null ? 1d : scale;
        if (widthRatio < 1 || heightRatio < 1) {
            s = ratio;
        }
        try {
            return Thumbnails.of(tempFile).scale(1d)
                    .watermark(positions == null ? Positions.CENTER : positions,
                            Thumbnails.of(watermarkPic).scale(s).rotate(rotate == null ? 0d : rotate).asBufferedImage()
                            , opacity == null ? 0f : opacity).asBufferedImage();

        } catch (Exception e) {
            logger.error("创建水印图片异常", e);
        } finally {
            FileUtils.forceDelete(tempFile);
        }
        return null;

    }


    public static BufferedImage createWatermarkImage(int width, int height, String text,
                                                     String fontFamily, Integer fontSize,
                                                     Color color, Float opacity, Double rotate,
                                                     Positions positions) throws Exception {
        File tempFile = null;
        try {
//            Font jFont = Font.createFont(Font.TRUETYPE_FONT, DocumentWatermarkUtils.class.getClassLoader().getResourceAsStream("fonts/AlibabaPuHuiTi-3-55-Regular.ttf"));
            Font font = jFont.deriveFont(fontSize == null ? 24f : (float) fontSize);// new Font(StringUtils.defaultString(fontFamily, "SansSerif"), Font.PLAIN, fontSize == null ? 18 : fontSize); // 字体、样式、大小
            BufferedImage image = createTextImage(text, font, color);
            if (rotate != null) {
                image = rotateImage(image, rotate.doubleValue());
            }
            // 生成透明背景图，用于定位打印水印图在上面
            BufferedImage container = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            BufferedImage markedBufferedImage = Thumbnails.of(container).scale(1d)
                    .watermark(positions == null ? Positions.CENTER : positions,
                            Thumbnails.of(image).scale(1d).asBufferedImage()
                            , opacity == null ? 0f : opacity).outputQuality(1f).asBufferedImage();
            tempFile = new File(System.getProperty("java.io.tmpdir"), "watermark-image-" + UUID.randomUUID().toString() + ".png");
//            System.out.println(tempFile.getAbsoluteFile());
            ImageIO.write(markedBufferedImage, "png", tempFile);
            return ImageIO.read(tempFile);
        } catch (Exception e) {
            logger.error("创建文字图片异常", e);
            throw new RuntimeException(e);
        } finally {
            FileUtils.forceDelete(tempFile);
        }
    }

    public static BufferedImage createTextImage(String text, Font f, Color color) throws Exception {
        File tempFile = null;
        try {
            // 配置参数
            Color textColor = color == null ? new Color(72, 72, 72) : color;
            int padding = 0; // 内边距
            // 1. 创建字体f
            Font font = f == null ? new Font("Microsoft YaHei", Font.PLAIN, 28) : f;
//        int fontSize = font.getSize();
            // 2. 创建临时图形上下文用于测量文本
            BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempG = tempImg.createGraphics();
            FontMetrics fm = tempG.getFontMetrics(font);

            // 3. 分割文本为多行
            List<String> lines = new ArrayList<>();
            for (String line : text.split("\\n")) {
                lines.add(line);
            }

            // 4. 计算最大行宽和总高度
            int maxLineWidth = 0;
            for (String line : lines) {
                int lineWidth = fm.stringWidth(line);
                if (lineWidth > maxLineWidth) {
                    maxLineWidth = lineWidth;
                }
            }

            int lineHeight = fm.getHeight();
            int textBlockHeight = lineHeight * lines.size();

            // 5. 计算图片最终尺寸（根据文本内容自适应）
            int imageWidth = maxLineWidth + padding * 2;
            int imageHeight = textBlockHeight + padding * 2;

            // 6. 创建最终图像
            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            setupHighQualityRendering(g2d);


            // 8. 绘制文本（居中对齐）
            g2d.setColor(textColor);
            g2d.setFont(font);

            int y = padding + fm.getAscent(); // 初始Y坐标
            for (String line : lines) {
                int lineWidth = fm.stringWidth(line);
                // 计算居中的X坐标
                int x = (imageWidth - lineWidth) / 2;
                // 绘制文本
                g2d.drawString(line, x, y);
                y += lineHeight; // 移动到下一行
            }

            g2d.dispose();
            tempG.dispose();

            // 9. 保存图片
            tempFile = new File(System.getProperty("java.io.tmpdir"), "text-image-" + UUID.randomUUID().toString() + ".png");
            ImageIO.write(image, "png", tempFile);
//        System.out.println("自适应宽度的多行文本图片生成成功！" + tempFile.getAbsolutePath());
//        System.out.println("生成图片尺寸: " + imageWidth + "x" + imageHeight);
            return ImageIO.read(tempFile);
        } catch (Exception e) {
            logger.error("生成文字图片异常: ", e);
            throw new RuntimeException(e);
        } finally {
            FileUtils.forceDelete(tempFile);
        }
    }

    private static void setupHighQualityRendering(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }
}
