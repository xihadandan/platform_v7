package com.wellsoft.pt.di.request;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/15    chenq		2019/8/15		Create
 * </pre>
 */
public class RequestDataItemXmlConverter extends JavaBeanConverter {

    public RequestDataItemXmlConverter(Mapper mapper) {
        super(mapper, RequestDataItem.class);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        RequestDataItem item = (RequestDataItem) source;
        if (CollectionUtils.isNotEmpty(item.getItemList())) {
            writer.startNode("DATALIST");
            Iterator iterator = item.getItemList().iterator();
            while (iterator.hasNext()) {
                context.convertAnother(iterator.next());
            }
            writer.endNode();
        }

        List<RequestStream> streams = item.getStreamingDatas();
        if (CollectionUtils.isNotEmpty(streams)) {
            writer.startNode("STREAMINGDATAS");
            Iterator iterator = streams.iterator();
            while (iterator.hasNext()) {
                writer.startNode("ITEM");
                context.convertAnother(iterator.next());
                writer.endNode();
            }
            writer.endNode();
        }
    }
}
