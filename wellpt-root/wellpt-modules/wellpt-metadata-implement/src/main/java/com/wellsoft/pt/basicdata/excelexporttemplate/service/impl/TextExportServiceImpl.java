package com.wellsoft.pt.basicdata.excelexporttemplate.service.impl;

import com.wellsoft.pt.basicdata.excelexporttemplate.service.TextExportService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
@Transactional
public class TextExportServiceImpl extends BaseServiceImpl implements TextExportService {
    /**
     * txt 传入要导到文本中的内容，拼接成一个字符串"\r\n"代表换行
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.TextExportService#generateTextFile(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String generateTextFile(String txt, HttpServletRequest request, HttpServletResponse response) {

        //将文件存到指定位置
        String path = request.getSession().getServletContext().getRealPath("/") + UUID.randomUUID() + ".txt";
        File file = new File(path);
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
            fout.write(txt.getBytes());
            fout.flush();
            fout.close();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        OutputStream os;
        try {
            os = response.getOutputStream();
            response.setContentType("text/plain;charset=GBK");// 定义输出类型
            byte[] b = path.getBytes();
            os.write(b);
            os.flush();
            os.close();
        } catch (IOException e1) {
            logger.error(ExceptionUtils.getStackTrace(e1));
        } // 取得输出流

        return path;
    }
}
