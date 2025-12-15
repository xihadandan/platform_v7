package com.wellsoft.context.util.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

public class ClobUtils {
    private static Logger LOG = LoggerFactory.getLogger(ClobUtils.class);

    public static String ClobToString(Clob clob) {

        String reString = "";

        Reader is = null;
        BufferedReader br = null;
        try {
            is = clob.getCharacterStream();
            br = new BufferedReader(is);

            char[] buffer = new char[1024];
            int size = -1;
            StringBuilder sb = new StringBuilder("");

            while ((size = br.read(buffer)) != -1) {// 执行循环将字符串全部取出付值给StringBuilder由StringBuilder转成STRING
                sb.append(new String(buffer, 0, size));
            }
            reString = sb.toString();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }

        return reString;

    }

    /**
     * 把Blob类型转换为byte数组类型
     *
     * @param blob
     * @return
     */
    public static byte[] blobToBytes(Blob blob) {

        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(blob.getBinaryStream());
            byte[] bytes = new byte[(int) blob.length()];
            int len = bytes.length;
            int offset = 0;
            int read = 0;
            while (offset < len
                    && (read = is.read(bytes, offset, len - offset)) >= 0) {
                offset += read;
            }
            return bytes;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }

}
