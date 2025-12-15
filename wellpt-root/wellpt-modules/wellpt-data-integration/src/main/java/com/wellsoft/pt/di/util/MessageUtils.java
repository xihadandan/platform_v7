package com.wellsoft.pt.di.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.math.BigDecimal;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/16    chenq		2019/8/16		Create
 * </pre>
 */
public class MessageUtils {

    private static final XStream jsonXStream = new XStream(
            new JettisonMappedXmlDriver());//转为json化的对象字符串

    private static final XStream xmlXStream = new XStream(new StaxDriver());//转为xml化的对象字符串


    /**
     * 对象转为可读性的json字符串
     *
     * @param object
     * @return
     */
    public static String object2ReadableJSON(Object object) {
        return object != null ? jsonXStream.toXML(object) : "";
    }

    /**
     * 对象的json字符串转为对象
     *
     * @param objectJson
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJSON(String objectJson, Class<T> clazz) {
        return (T) jsonXStream.fromXML(objectJson);
    }

    public static Object fromJSON(String objectJson) {
        return jsonXStream.fromXML(objectJson);
    }

    /**
     * 对象的xml字符串转为对象
     *
     * @param objectXml
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromXML(String objectXml, Class<T> clazz) {
        return (T) xmlXStream.fromXML(objectXml);
    }


    /**
     * 对象转为可读性的xml字符串
     *
     * @param object
     * @return
     */
    public static String object2ReadableXML(Object object) {
        return object != null ? xmlXStream.toXML(object) : "";
    }

    public static void main(String[] arrs) {
        System.out.println(MessageUtils.object2ReadableJSON(new BigDecimal("1")));
    }
}
