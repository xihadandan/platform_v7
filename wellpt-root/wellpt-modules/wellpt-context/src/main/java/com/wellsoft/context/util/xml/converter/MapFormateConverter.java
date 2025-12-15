package com.wellsoft.context.util.xml.converter;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/14    chenq		2019/8/14		Create
 * </pre>
 */
public class MapFormateConverter extends MapConverter {
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    private boolean showListValueAttr = false;


    private boolean upperCaseFormateTag = false;//大写

    private String listValueAttrName = "isList";

    private String listValueAttrValue = "1";

    public MapFormateConverter(Mapper mapper, boolean showListValueAttr,
                               boolean upperCaseFormateTag) {
        super(mapper);
        this.showListValueAttr = showListValueAttr;
        this.upperCaseFormateTag = upperCaseFormateTag;
    }


    public MapFormateConverter(Mapper mapper, boolean showListValueAttr,
                               String listValueAttrName,
                               String listValueAttrValue) {
        super(mapper);
        this.showListValueAttr = showListValueAttr;
        this.listValueAttrName = listValueAttrName;
        this.listValueAttrValue = listValueAttrValue;
    }

    public MapFormateConverter(Mapper mapper) {
        super(mapper);
    }


    public MapFormateConverter(Mapper mapper, Class type) {
        super(mapper, type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        Map map = (Map) source;
        Set<String> keys = map.keySet();
        for (String k : keys) {
            writer.startNode(tagName(k));
            Object x = map.get(k);
            if (!(x instanceof String)
                    && !(x instanceof Number)
                    && !(x instanceof Boolean)
                    && !(x instanceof Character)) {//非基础类型
                if (x instanceof Collection && showListValueAttr) {
                    writer.addAttribute(listValueAttrName, listValueAttrValue);
                }
                context.convertAnother(x);
            } else {
                writer.setValue(map.get(k).toString());
            }

            writer.endNode();
        }
    }

    private String tagName(String key) {
        String v = key;
        return upperCaseFormateTag ? v.toUpperCase() : v;
    }
}
