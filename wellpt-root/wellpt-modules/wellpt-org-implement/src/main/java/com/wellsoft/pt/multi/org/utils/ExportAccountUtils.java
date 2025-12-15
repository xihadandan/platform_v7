package com.wellsoft.pt.multi.org.utils;

import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.excel.ExcelUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/4/7.1	    zenghw		2021/4/7		    Create
 * </pre>
 * @date 2021/4/7
 */
public class ExportAccountUtils {

    public static final String[] pwdTitles = new String[]{"单位", "部门路径", "职位", "账号", "姓名", "密码", "性别", "手机号", "电子邮件",
            "员工编号", "家庭电话", "办公电话", "身份证号", "英文名"};
    private static Logger logger = LoggerFactory.getLogger(ExportAccountUtils.class);

    /**
     * // 随机密码模式要导出用户和密码
     *
     * @param titles   列字段名
     * @param dataList 每列的数据
     * @param request
     * @param response
     * @return void
     **/
    public static void exportPwdAccounts(String[] titles, List<String[]> dataList, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            HSSFWorkbook excel = ExcelUtils.generateExcelBook(dataList, titles);
            excel.write(os);
            String fileName = "用户密码_" + DateUtil.getFormatDate(new Date(), "yyyy-MM-dd") + ".xls";
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(os.toByteArray()), fileName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            os.close();
        }
    }
}
