package com.wellsoft.context.util.xml.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.io.IOUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.io.FileOutputStream;

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
public class DataHandlerBase64Converter implements Converter {

    private String tagName = "STREAMDATA";

    private String fileTagName = "FILENAME";

    public DataHandlerBase64Converter() {

    }

    public DataHandlerBase64Converter(String tagName, String fileTagName) {
        this.tagName = tagName;
        this.fileTagName = fileTagName;
    }

    public static void main(String[] arrs) throws Exception {
        DataHandler dataHandler = new DataHandler(new FileDataSource("D:/test/bing/bing.html"));
        BASE64Encoder encoder = new BASE64Encoder();
        String code = encoder.encode(IOUtils.toByteArray(dataHandler.getInputStream()));
        System.out.println(code);
        BASE64Decoder decoder = new BASE64Decoder();
        FileOutputStream out = new FileOutputStream(new File("D:/test/bing/write.html"));
        out.write(decoder.decodeBuffer(code));
        out.flush();

    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        DataHandler dataHandler = (DataHandler) source;

        try {
            writer.startNode(tagName);
            BASE64Encoder encoder = new BASE64Encoder();
            writer.setValue(encoder.encode(IOUtils.toByteArray(dataHandler.getInputStream())));
            writer.endNode();
            writer.startNode(fileTagName);
            writer.setValue(dataHandler.getName());
            writer.endNode();
        } catch (Exception e) {
        }


    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

    @Override
    public boolean canConvert(Class type) {
        return DataHandler.class.isAssignableFrom(type);
    }
}
