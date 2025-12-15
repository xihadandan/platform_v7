package com.wellsoft.context.util.barcode;

import org.jbarcode.JBarcode;
import org.jbarcode.encode.*;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.EAN13TextPainter;
import org.jbarcode.paint.WideRatioCodedPainter;
import org.jbarcode.paint.WidthCodedPainter;
import org.jbarcode.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Description: 条形码处理工具类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月22日.1	Asus		2015年12月22日		Create
 * </pre>
 * @date 2015年12月22日
 */
public class JBarcodeUtils {
    public static final BarcodeEncoder JBARCODE_CODE39 = Code39Encoder.getInstance();
    public static final BarcodeEncoder JBARCODE_CODE128 = Code128Encoder.getInstance();
    public static final BarcodeEncoder JBARCODE_EAN13 = EAN13Encoder.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(JBarcodeUtils.class);

    // /**
    // * 进行条码的生成，获得输入流
    // *
    // * @author wujx createtime 2015-4-17上午11:04:09
    // * @param barcode
    // * @return
    // */
    // @Deprecated
    // public static InputStream generalJBarcode(String barcode, BarcodeEncoder
    // barcodeEncoder) {
    // // FileOutputStream os = null;
    // InputStream is = null;
    // WellJBarcode localJBarcode = new WellJBarcode(
    // barcodeEncoder,
    // WideRatioCodedPainter.getInstance(),
    // WellBaseLineTextPainter.getInstance());
    // localJBarcode.setShowCheckDigit(false);
    // localJBarcode.setCheckDigit(false);
    // BufferedImage bufferedImage;
    // try {
    // bufferedImage = localJBarcode.createBarcode("", "",
    // barcode, "");
    // ByteArrayOutputStream bs = new ByteArrayOutputStream();
    // ImageOutputStream imOut;
    // imOut = ImageIO.createImageOutputStream(bs);
    //
    // ImageIO.write(bufferedImage, "JPG", imOut);
    //
    // is = new ByteArrayInputStream(bs.toByteArray());
    //
    // } catch (InvalidAtributeException e1) {
    // e1.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // // try{
    // // os = new FileOutputStream("快捷链接条形码" + barcode);
    // // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
    // // JPEGEncodeParam param =
    // // encoder.getDefaultJPEGEncodeParam(bufferedImage);
    // // param.setQuality(1.0f, false);
    // // encoder.setJPEGEncodeParam(param);
    // // encoder.encode(bufferedImage);
    // // } catch (FileNotFoundException e) {
    // // e.printStackTrace();
    // // } catch (InvalidAtributeException e) {
    // // e.printStackTrace();
    // // } catch (ImageFormatException e) {
    // // e.printStackTrace();
    // // } catch (IOException e) {
    // // e.printStackTrace();
    // // }
    // return is;
    // }

    /**
     * 进行条码的生成，获得输入流-自己定义的条码常用格式
     *
     * @param barcode
     * @return
     * @author wujx createtime 2015-4-17上午11:04:09
     */
    public static BufferedImage getJBBufferImage(String barcode, BarcodeEncoder barcodeEncoder) {
        JBarcode jBarcode = getJBarcode(barcodeEncoder);
        return getJBBufferImage(barcode, jBarcode);
    }

    /**
     * 进行条码的生成，获得输入流-通过调用JBarcodeFactory.getInstance().createXXX格式
     *
     * @param barcode
     * @param jBarcode
     * @return
     * @author wujx createtime 2015-4-29上午8:48:28
     */
    public static BufferedImage getJBBufferImage(String barcode, JBarcode jBarcode) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = jBarcode.createBarcode(barcode);
        } catch (InvalidAtributeException e) {
            LOG.error(e.getMessage(), e);
        }
        return bufferedImage;
    }

    /**
     * BufferedImage 转换为 ByteArrayOutputStream
     *
     * @param bufferedImage 图片数据流
     * @param fileType      文件格式,参考 {@link ImageUtil}
     * @return ByteArrayOutputStream
     */
    public static ByteArrayOutputStream bufferedImage2ByteArrayOutputStream(BufferedImage bufferedImage,
                                                                            String fileType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageUtil.encodeAndWrite(bufferedImage, fileType, baos);
            //			ImageOutputStream imOut;
            //			imOut = ImageIO.createImageOutputStream(baos);
            //			ImageIO.write(bufferedImage, fileType, imOut);

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return baos;
    }

    /**
     * BufferedImage 转换为 ByteArrayOutputStream
     * 默认JPEG格式
     *
     * @param bufferedImage 图片数据流
     * @return ByteArrayInputStream
     */
    public static ByteArrayInputStream bufferedImage2JpegByteArrayInputStream(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = bufferedImage2ByteArrayOutputStream(bufferedImage, ImageUtil.JPEG);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return bais;
    }

    /**
     * BufferedImage 转化为 ByteArrayInputStream
     *
     * @param bufferedImage 图片数据流
     * @param fileType      文件格式,参考 {@link ImageUtil}
     * @return ByteArrayInputStream
     */
    public static ByteArrayInputStream bufferedImage2ByteArrayInputStream(BufferedImage bufferedImage,
                                                                          String fileType) {
        ByteArrayOutputStream baos = bufferedImage2ByteArrayOutputStream(bufferedImage, fileType);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return bais;
    }

    /**
     * 将BufferedImage以JPG方式，存入路径为filePath的硬盘中
     *
     * @param bufferedImage 图片数据流
     * @param filePath      存储地址（D:\\temp\\image.jpg）
     * @author wujx createtime 2015-4-28上午10:31:43
     */
    public static void saveToJPG(BufferedImage bufferedImage, String filePath) {
        saveToImageFile(bufferedImage, filePath, ImageUtil.JPEG);
    }

    /**
     * 将BufferedImage以fileType作为后缀名，存入路径为filePath的硬盘中
     *
     * @param bufferedImage 图片数据流
     * @param filePath      存储地址（D:\\temp\\image.jpg）
     * @param fileType      文件格式,参考 {@link ImageUtil}
     * @author wujx createtime 2015-4-28上午10:28:21
     */
    public static void saveToImageFile(BufferedImage bufferedImage, String filePath, String fileType) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filePath);
            ImageUtil.encodeAndWrite(bufferedImage, fileType, fos);
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

    }

    /**
     * 根据不同的条码类型，获取自己定义的对应类型的条码配置
     *
     * @param barcodeEncoder
     * @return
     * @author wujx createtime 2015-4-28上午10:23:16
     */
    public static JBarcode getJBarcode(BarcodeEncoder barcodeEncoder) {
        // TODO 有待完善更多类型
        JBarcode jBarcode = null;
        if (barcodeEncoder instanceof Code39Encoder) {
            jBarcode = new JBarcode(barcodeEncoder, WideRatioCodedPainter.getInstance(),
                    BaseLineTextPainter.getInstance());
            jBarcode.setShowCheckDigit(false);
            jBarcode.setCheckDigit(false);
        } else if (barcodeEncoder instanceof Code128Encoder) {
            jBarcode = new JBarcode(barcodeEncoder, WidthCodedPainter.getInstance(), BaseLineTextPainter.getInstance());
        } else if (barcodeEncoder instanceof EAN13Encoder) {
            jBarcode = new JBarcode(barcodeEncoder, WidthCodedPainter.getInstance(), EAN13TextPainter.getInstance());
        } else {
            try {
                throw new Exception("找不到合适的条码编码规则！");
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return jBarcode;
    }
}
