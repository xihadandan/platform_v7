/*
 * @(#)2016年3月31日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dbmigrate;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.zip.CRC32;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月31日.1	zhongzh		2016年3月31日		Create
 * </pre>
 * @date 2016年3月31日
 */
public class CheckSumUtils {
    public static void main(String[] args) throws IOException {
        args = new String[]{"D:/wep/workspace/wellpt-web/src/main/resources/dbmigrate/Oracle11g/basicdata/dml/V20160311_3__dict.sql"};
        if (args.length < 1) {
            CharsetUtils.err("usage: CheckSumUtils {file} \nexample:CheckSumUtils xxxx.sql");
            return;
        }
        System.out.println(calculateChecksum(FileUtils.readFileToByteArray(new File(args[0]))));
    }

    /**
     * Calculates the checksum of these bytes.
     *
     * @param bytes The bytes to calculate the checksum for.
     * @return The crc-32 checksum of the bytes.
     */
    private static int calculateChecksum(byte[] bytes) {
        final CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return (int) crc32.getValue();
    }

}
