package com.wellsoft.context.util.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.util.groovy.GroovyUseable;

/**
 * Description: xml转换工具，以下有简单调用样例
 *
 * @author chenq
 * @date 2018/11/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/9    chenq		2018/11/9		Create
 * </pre>
 */
@GroovyUseable
public class XmlBuilder {

    XStream xStream = new XStream(new DomDriver());


    /**
     * 设置类的别名
     *
     * @param alias
     * @param clazz
     * @return
     */
    public XmlBuilder alias(String alias, Class clazz) {
        this.xStream.alias(alias, clazz);
        return this;
    }

    /**
     * 设置类字段的别名
     *
     * @param alias
     * @param fieldClazz
     * @param fieldName
     * @return
     */
    public XmlBuilder aliasField(String alias, Class fieldClazz, String fieldName) {
        this.xStream.aliasField(alias, fieldClazz, fieldName);
        return this;
    }

    /**
     * xml转对象忽略未知字段
     *
     * @return
     */
    public XmlBuilder ignoreUnknownElements() {
        this.xStream.ignoreUnknownElements();
        return this;
    }

    /**
     * 注册转换器
     *
     * @param converter
     * @return
     */
    public XmlBuilder registerConverter(Converter converter) {
        this.xStream.registerConverter(converter);
        return this;
    }

    /**
     * 注册转换器
     *
     * @param converter
     * @return
     */
    public XmlBuilder registerConverter(SingleValueConverter converter) {
        this.xStream.registerConverter(converter);
        return this;
    }

    /**
     * 实例对象转xml字符串
     *
     * @param instance
     * @return
     */
    public String toXml(Object instance) {
        return this.xStream.toXML(instance);
    }

    /**
     * xml字符串转对象
     *
     * @param xml
     * @return
     */
    public Object toBean(String xml) {
        return this.xStream.fromXML(xml);
    }


    //调用样例
    private static class Snippent {
        public static void main(String[] arrs) {
            //1.对象转xml字符串
            DataItem dataItem = new DataItem();
            dataItem.setAceId("111");
            dataItem.setLabel("测试");
            String xml = new XmlBuilder().alias("demo", DataItem.class).toXml(dataItem);
            System.out.println("解析成字符串：\n\r" + xml);
            //2.xml字符串转对象

            DataItem resut = (DataItem) new XmlBuilder().alias("demo",
                    DataItem.class).registerConverter(new Converter() {

                //bean转xml字符串的解析
                @Override
                public void marshal(Object source, HierarchicalStreamWriter writer,
                                    MarshallingContext context) {

                }

                //xml字符串反转回bean的解析
                @Override
                public Object unmarshal(HierarchicalStreamReader reader,
                                        UnmarshallingContext context) {
                    DataItem data = new DataItem();

                    //每解析一层都需要movedown/moveup
                    reader.moveDown();
                    data.setAceId(reader.getValue());
                    reader.moveUp();

                    reader.moveDown();
                    data.setLabel(reader.getValue());
                    reader.moveUp();

                    reader.close();

                    return data;
                }

                //是否可以转换的类型
                @Override
                public boolean canConvert(Class type) {
                    return type.equals(DataItem.class);
                }
            }).toBean(xml);

            System.out.println("反解析回来：label=" + resut.getLabel() + " , aceId=" + resut.getAceId());

        }
    }


}
