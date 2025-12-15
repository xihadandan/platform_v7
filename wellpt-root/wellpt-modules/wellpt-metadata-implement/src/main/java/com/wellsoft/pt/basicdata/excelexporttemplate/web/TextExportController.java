package com.wellsoft.pt.basicdata.excelexporttemplate.web;

import bitronix.tm.utils.ExceptionUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Description: 如何描述该类
 * 导出成文本格式
 *
 * @author yuyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-9.1	yuyq		2015-7-9		Create
 * </pre>
 * @date 2015-7-9
 */
@Controller
@RequestMapping("/basicdata/textexportrule")
public class TextExportController {
    protected static Logger logger = LoggerFactory.getLogger(TextExportController.class);

    @RequestMapping(value = "/getTextFile")
    public void getExcelFile(@RequestParam("path") String path, HttpServletRequest request, HttpServletResponse response) {
        File file = new File(path);
        try {
            InputStream is = new FileInputStream(file);
            FileDownloadUtils.download(request, response, is, "text.txt"); // WorkForm Def
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(ExceptionUtils.getStackTrace(e)
                    .getBytes()), "error.txt");
        }
    }

}
