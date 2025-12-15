package com.wellsoft.pt.repository.support.convert;

import com.wellsoft.pt.repository.entity.FileEntity;
import org.apache.log4j.Logger;

/**
 * Description: 文件上传转换成swf的工具类
 *
 * @author jackCheng
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-17.1	jackCheng		2013-4-17		Create
 * </pre>
 * @date 2013-4-17
 */
public class DocConverter {
    Logger logger = Logger.getLogger(DocConverter.class);
    private PDFConverter pdfConverter;
    private SWFConverter swfConverter;

    public DocConverter(PDFConverter pdfConverter, SWFConverter swfConverter) {
        super();
        this.pdfConverter = pdfConverter;
        this.swfConverter = swfConverter;
    }

    public void convert(String inputFile, String swfFile, String fileName) {
        this.pdfConverter.convert2PDF(inputFile);
        //		String pdfFile = FileUtil.getFilePrefix(inputFile) + ".pdf";
        String pdfFile = inputFile + ".pdf";
        this.swfConverter.convert2SWF(pdfFile, swfFile, fileName);
    }

    public FileEntity convert(String inputFile, String fileName) {
        this.pdfConverter.convert2PDF(inputFile);
        String pdfFile = FileUtil.getFilePrefix(inputFile) + ".pdf";
        //String pdfFile = inputFile + ".pdf";
        return this.swfConverter.convert2SWF(pdfFile, fileName);

    }

    public void convertPdf(String inputFile, String fileName) {
        this.pdfConverter.convert2PDF(inputFile);
    }

}
