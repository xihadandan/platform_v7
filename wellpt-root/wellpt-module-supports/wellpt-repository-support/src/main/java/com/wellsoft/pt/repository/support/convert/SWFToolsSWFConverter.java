package com.wellsoft.pt.repository.support.convert;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.repository.entity.FileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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
public class SWFToolsSWFConverter implements SWFConverter {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public FileEntity convert2SWF(String inputFile, String swfFile, String fileName) {
        FileEntity fileEntity = new FileEntity();
        File pdfFile = new File(inputFile);
        File outFile = new File(swfFile);
        if (!inputFile.endsWith(".pdf")) {
            logger.debug("文件格式非PDF！");
            return null;
        }
        if (!pdfFile.exists()) {
            logger.debug("PDF文件不存在！");
            return null;
        }
		/*if (outFile.exists()) {
			InputStream isr = null;
			try {
				isr = new FileInputStream(outFile);
			} catch (FileNotFoundException e) {

			}

			fileEntity.setFilename(outFile.getName());
			fileEntity.setFile(isr);
			return fileEntity;
		}else{

		}*/
        if (!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        String command = Config.getValue("SWFTools_pdf2swf.dir") + " \"" + inputFile + "\" -o \"" + swfFile
                + "\" -T 9 -f";
        try {
            logger.debug("开始转换文档: " + inputFile);
            Process pro = null;
            pro = Runtime.getRuntime().exec(command);

            //来清空process.getInputStream()的缓冲区

            InputStream is_ = pro.getErrorStream();

            BufferedReader br_ = new BufferedReader(new InputStreamReader(is_));

            StringBuilder buf = new StringBuilder(); // 保存输出结果流

            String line = null;

            while ((line = br_.readLine()) != null) {

                buf.append(line); // 循环等待ffmpeg进程结束
            }
            try {
                pro.waitFor();
                InputStream isr = new FileInputStream(outFile);
                //				String swfFiles = swfFile.substring(11);
                //				String swfFiles = fileName + ".swf";
                fileEntity.setFilename(outFile.getName());
                fileEntity.setFile(isr);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }

            // logger.debug("输出结果为：" + buf);
            logger.debug("成功转换为SWF文件！");
        } catch (IOException e) {
            logger.error("转换文档为swf文件失败！", e);
        }
        return fileEntity;

    }

    @Override
    public FileEntity convert2SWF(String inputFile, String fileName) {
        String swfFile = FileUtil.getFilePrefix(inputFile) + ".swf";
        return convert2SWF(inputFile, swfFile, fileName);
    }

}
