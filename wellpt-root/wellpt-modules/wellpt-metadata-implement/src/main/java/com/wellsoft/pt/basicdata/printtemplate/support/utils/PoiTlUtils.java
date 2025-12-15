package com.wellsoft.pt.basicdata.printtemplate.support.utils;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public abstract class PoiTlUtils {

    private static Logger logger = Logger.getLogger(PoiTlUtils.class);

    public static File renderDocxFile(InputStream inputStream, final Map<String, Object> model, File destFile) {
        Configure config = Configure.builder().useSpringEL(false).setRootModel(model).build();
        XWPFTemplate template = null;
        try {
            ZipSecureFile.setMinInflateRatio(0.001);
            // ZipSecureFile.setMinInflateRatio(-1.0d);
            template = XWPFTemplate.compile(inputStream, config).render(model);
        } catch (Exception e) {
            if (e.getMessage().contains("Render template {{+bodyFile")) {
                throw new BusinessException("存在附件格式不符合套打模板要求，模板类型为Poi-tl模板", e);
            } else if (e.getMessage().contains("Compile template failed")) {
                throw new BusinessException("模板格式不正确！", e);
            } else {
                throw new BusinessException("套打模板有误", e);
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destFile);
            template.write(out);
            out.flush();
            out.close();
            template.close();
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return destFile;
    }

    public static File renderDocxFile(InputStream inputStream, Map<String, Object> model) {
        String tempFileName = new Date().getTime() + "-temp.docx";
        File destFile = new File(Config.HOME_DIR + File.separator + tempFileName);
        return renderDocxFile(inputStream, model, destFile);
    }

    public static File mergeDocx(Collection<File> files, boolean pageBreak) {
        String tempFileName = new Date().getTime() + "-temp.docx";
        File destFile = new File(Config.HOME_DIR + File.separator + tempFileName);

        NiceXWPFDocument source = null;
        try {
            for (File file : files) {
                if (source == null) {
                    source = new NiceXWPFDocument(new FileInputStream(file));
                } else {
                    if (pageBreak) {
                        XWPFParagraph paragraph = source.createParagraph();
                        paragraph.setPageBreak(true);
                    }
                    NiceXWPFDocument target = new NiceXWPFDocument(new FileInputStream(file));
                    source = source.merge(target);

                    target.close();
                }
            }

            if (source != null) {
                source.write(new FileOutputStream(destFile));
                source.close();
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return destFile;
    }

    public static void deleteFiles(Collection<File> files) {
        for (File file : files) {
            FileUtils.deleteQuietly(file);
        }
    }
}
