package com.wellsoft.pt.repository.convert.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.convert.FileConvertService;
import com.wellsoft.pt.repository.convert.util.DocumentToPdfUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/7.1	liuyz		2021/9/7		Create
 * </pre>
 * @date 2021/9/7
 */
@Component("spireConvertService")
public class SpireConvertServiceImpl extends BaseServiceImpl implements FileConvertService {
    @Override
    public void officeToOFD(File srcFile, OutputStream out, Map<String, String> metas) {

    }

    @Override
    public void officeToPDF(File srcFile, OutputStream out, Map<String, String> metas) {
        try {
            DocumentToPdfUtils.wordToPdf(srcFile, (FileOutputStream) out);
        } catch (Exception ex) {
            logger.warn("officeToPDF srcFile[" + srcFile.getAbsolutePath() + "]:" + ex.getMessage(),
                    ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /*@Override
    public void officeToPDF(File srcFile, String targetPath) {
        try {
            DocumentToPdfUtils.wordToPdf(srcFile, targetPath);
        } catch (Exception ex) {
            logger.warn("officeToPDF srcFile[" + srcFile.getAbsolutePath() + "]:" + ex.getMessage(),
                    ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }*/
}
