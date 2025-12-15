/*
 * @(#)2014-2-7 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-2-7.1	zhulh		2014-2-7		Create
 * </pre>
 * @date 2014-2-7
 */
public class SerializeUtils {

    /**
     * 对象序列化为字符串
     */
    public static String serialize(Object obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            String serStr = byteArrayOutputStream.toString("ISO-8859-1");//必须是ISO-8859-1
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");//编码后字符串不是乱码（不编也不影响功能）

            objectOutputStream.close();
            byteArrayOutputStream.close();
            return serStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串 反序列化为 对象
     */
    public static Object unSerialize(String serStr) {
        try {
            String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object obj = objectInputStream.readObject();

            objectInputStream.close();
            byteArrayInputStream.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
