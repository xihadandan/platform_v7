package com.wellsoft.context.util.io;

import com.wellsoft.context.enums.Encoding;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description: 输入流工具类【废弃】，请使用org.apache.commons.io.IOUtils
 *
 * @author Asus
 * @version 1.0
 * @date 2015年12月25日
 * @see org.apache.commons.io.IOUtils
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月23日.1	Asus		2015年12月23日		Create
 * </pre>
 */
@Deprecated
public class InputstreamUtils {
    static Logger logger = Logger.getLogger(InputstreamUtils.class);

    /**
     * 输入流转字符串
     *
     * @param is 输入流
     * @return 字符串
     */
    public static String Inpustream2String(InputStream is) {
        String reString = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int size = -1;
            byte[] buffer = new byte[1024];

            while ((size = is.read(buffer)) != -1) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
                baos.write(buffer, 0, size);
            }
            reString = baos.toString(Encoding.UTF8.getValue());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return reString;

    }
}
