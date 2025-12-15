package com.wellsoft.pt.di.callback;

import com.wellsoft.context.util.xml.XmlConverUtils;
import com.wellsoft.pt.di.processor.AbstractReadFileDocument2SaveProcessor;
import com.wellsoft.pt.di.util.CamelContextUtils;
import org.apache.camel.Exchange;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.util.Date;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/29
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/29    chenq		2019/10/29		Create
 * </pre>
 */
public class ReadFileDocument2SaveCallback extends AbstractDiCallback {
    @Override
    public void callback(Object responseObj, Object request) {
        //输出反馈xml文件
        String fileName = "response_" + EXCHANGE_LOCAL.get().getIn().getHeader(
                Exchange.FILE_NAME, String.class);
        String filePath = EXCHANGE_LOCAL.get().getIn().getHeader(Exchange.FILE_PARENT).toString();
        String fileuri = "file://" + filePath + "/response?fileName=" + fileName;
        CamelContextUtils.producer().asyncSendBody(fileuri,
                XmlConverUtils.formateDocumentOutStrig(constructFeedbackDocument(), true));

    }

    protected Document constructFeedbackDocument() {
        try {
            AbstractReadFileDocument2SaveProcessor.ReadNodesResponse response = EXCHANGE_LOCAL.get().getProperty(
                    AbstractReadFileDocument2SaveProcessor.NODES_RESPONSE_KEY,
                    AbstractReadFileDocument2SaveProcessor.ReadNodesResponse.class);
            Document document = DocumentFactory.getInstance().createDocument();
            document.setXMLEncoding(Charsets.UTF_8.name());
            Element root = document.addElement("root");
            Element info = root.addElement("info");
            Date now = new Date();
            info.addElement("date").setText(
                    DateFormatUtils.format(now, "yyyy-MM-dd HH:mm:ss"));
            info.addElement("code").setText(DateFormatUtils.format(now, "yyyyMMddHHmmss"));
            info.addElement("r_code");
            info.addElement("total").setText(response.getTotal() + "");
            info.addElement("success").setText(response.getSuccess() + "");

            Element docEle = root.addElement("document");
            docEle.addElement("i_sn");
            docEle.addElement("action");
            docEle.addElement("i_status").setText("0");
            docEle.addElement("i_info").setText("生成新文档");
            return document;
        } catch (Exception e) {
            logger.error("构建反馈报文document结构异常：", e);
        }
        return null;
    }

    @Override
    public String name() {
        return "读取xml文档保存的反馈";
    }
}
