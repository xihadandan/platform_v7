package com.wellsoft.pt.repository.support;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class ImageResizeTool {
    /**
     * 生成缩略图
     *
     * @param OriFilePath
     * @param TargetFilePath
     * @param height
     * @param width
     * @throws Exception
     */
    public static void createFixedBoundImg(String OriFilePath, String TargetFilePath, double Ratio) throws Exception {
        File f = new File(OriFilePath);
        Image src = ImageIO.read(f);
        int oriWidth = src.getWidth(null);
        int oriHeight = src.getHeight(null);
        int tagWidth, tagHeight;
        tagWidth = (int) (oriWidth * Ratio);
        tagHeight = (int) (oriHeight * Ratio);

        BufferedImage target = new BufferedImage(tagWidth, tagHeight, BufferedImage.TYPE_INT_RGB);
        target.getGraphics().drawImage(src, 0, 0, tagWidth, tagHeight, null);
        File file = new File(TargetFilePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        // jdk升级到1.7，不能使用JPEGImageEncoder这些了
        // FileOutputStream out = new FileOutputStream(TargetFilePath);
        // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        // encoder.encode(target);
        // out.close();

        String formatName = TargetFilePath.substring(TargetFilePath.lastIndexOf(".") + 1);
        ImageIO.write(target, /* "GIF" */formatName /* format desired */, new File(TargetFilePath) /* target */);
    }

    /**
     * 生成缩略图
     *
     * @param OriFilePath
     * @param TargetFilePath
     * @param height
     * @param width
     * @throws Exception
     */
    public static void createFixedBoundImg(InputStream srcis, File preFile, double Ratio) throws Exception {
        Image src = ImageIO.read(srcis);
        int oriWidth = src.getWidth(null);
        int oriHeight = src.getHeight(null);
        int tagWidth, tagHeight;
        tagWidth = (int) (oriWidth * Ratio);
        tagHeight = (int) (oriHeight * Ratio);

        BufferedImage target = new BufferedImage(tagWidth, tagHeight, BufferedImage.TYPE_INT_RGB);
        target.getGraphics().drawImage(src, 0, 0, tagWidth, tagHeight, null);
        File file = preFile;
        if (!file.exists()) {
            file.createNewFile();
        }

        // JDK升级到1.7，不能用JPEGCodec了
        // FileOutputStream out = new FileOutputStream(preFile);
        // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        // // JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(target);
        // // param.setQuality(0.000001f, false);
        // // encoder.setJPEGEncodeParam(param);
        // encoder.encode(target);
        // out.close();

        String preFilePath = preFile.getPath();
        String formatName = preFilePath.substring(preFilePath.lastIndexOf(".") + 1);
        ImageIO.write(target, /* "GIF" */formatName /* format desired */, new File(preFilePath) /* target */);

    }

    public static void main(String[] args) throws Exception {
        // ImageResizeTool.createFixedBoundImg("c:/EU.jpg", "c:/pre/EU.jpg",1);

        System.out.println(Float.MAX_VALUE);

        System.out.println(Math.pow(2, 32));
    }
}
