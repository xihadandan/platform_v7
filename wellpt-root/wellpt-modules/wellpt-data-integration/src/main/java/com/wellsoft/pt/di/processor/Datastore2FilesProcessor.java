package com.wellsoft.pt.di.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.xml.XmlConverUtils;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.di.anotation.ProcessorParameter;
import com.wellsoft.pt.di.enums.DIParameterDomType;
import com.wellsoft.pt.di.util.CamelContextUtils;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class Datastore2FilesProcessor extends AbstractDIProcessor<DataStoreData> {

    @ProcessorParameter(name = "文本模板", domType = DIParameterDomType.TEXTAREA)
    private String templateContent;
    @ProcessorParameter(name = "富文本字段", domType = DIParameterDomType.INPUT)
    private String richTextField;
    @ProcessorParameter(name = "附件字段", domType = DIParameterDomType.INPUT)
    private String attachmentField;
    @ProcessorParameter(name = "富文本文件名", domType = DIParameterDomType.INPUT)
    private String richTextFileName;
    @ProcessorParameter(name = "文件保存路径", domType = DIParameterDomType.INPUT)
    private String path;
    @ProcessorParameter(name = "数据唯一值字段", domType = DIParameterDomType.INPUT)
    private String dataUuidField;


    @Override
    void action(DataStoreData dataStoreData) throws Exception {
        try {
            outputRichTextAndAttachment2File(dataStoreData);
            Map params = Maps.newHashMap();
            params.put("DATA", dataStoreData);
            params.put("EXCHANGE_ID", getExchangeId());
            EXCHANGE.get().getIn().setBody(
                    TemplateEngineFactory.getDefaultTemplateEngine().process(templateContent,
                            params));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void outputRichTextAndAttachment2File(DataStoreData dataStoreData) {
        if (StringUtils.isBlank(getRichTextField()) && StringUtils.isBlank(getAttachmentField())) {
            return;
        }

        MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                MongoFileService.class);
        List<Map<String, Object>> dataList = dataStoreData.getData();
        for (Map<String, Object> data : dataList) {
            if (StringUtils.isNotBlank(getRichTextField())) {
                Object value = data.get(getRichTextField());
                if (value != null) {
                    Document document = DocumentHelper.createDocument();
                    document.setXMLEncoding(Charsets.UTF_8.name());
                    Element rootElement = document.addElement("root");
                    rootElement.addElement("body").addCDATA(value.toString());
                    CamelContextUtils.producer().asyncSendBody(getFileOutputPath(data, "body.xml"),
                            XmlConverUtils.formateDocumentOutStrig(document, false));

                }
            }

            if (StringUtils.isNotBlank(getAttachmentField())) {
                Object value = data.get(getAttachmentField());
                if (value != null) {
                    String[] fileIDs = value.toString().split(";");
                    List<HashMap<String, String>> attachments = Lists.newArrayList();
                    for (String fid : fileIDs) {
                        MongoFileEntity fileEntity = mongoFileService.getFile(fid);
                        if (fileEntity != null) {
                            HashMap<String, String> amap = Maps.newHashMap();
                            amap.put("_file_name", fileEntity.getFileName());
                            amap.put("_file_absolute_dir",
                                    absoluteDirectory(data, fileEntity.getFileName()));
                            amap.put("_file_relative_dir",
                                    relativeDirectory(data, fileEntity.getFileName()));
                            attachments.add(amap);
                            CamelContextUtils.producer().asyncSendBody(
                                    getFileOutputPath(data, fileEntity.getFileName()),
                                    fileEntity.getInputstream());
                        }
                    }

                    data.put("_attachment", attachments);
                }
            }
        }
    }

    private String relativeDirectory(Map<String, Object> data, String fileName) {
        return data.get(dataUuidField != null ? dataUuidField : getProperty("dataUuidColumn",
                String.class)).toString() + "/" + fileName;
    }

    private String absoluteDirectory(Map<String, Object> data, String fileName) {
        return getPath() + "/" + data.get(dataUuidField != null ? dataUuidField : getProperty("dataUuidColumn",
                String.class)).toString() + "/" + fileName;
    }


    private String getFileOutputPath(Map<String, Object> data,
                                     String fileName) {
        return "file://" + getPath() + "/" + data.get(dataUuidField != null ? dataUuidField : getProperty("dataUuidColumn",
                String.class)).toString() + "?fileName=" + fileName;
    }


    @Override
    public String name() {
        return "数据仓库数据处理输出文件转换器";
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getRichTextField() {
        return richTextField;
    }

    public void setRichTextField(String richTextField) {
        this.richTextField = richTextField;
    }

    public String getAttachmentField() {
        return attachmentField;
    }

    public void setAttachmentField(String attachmentField) {
        this.attachmentField = attachmentField;
    }

    public String getRichTextFileName() {
        return richTextFileName;
    }

    public void setRichTextFileName(String richTextFileName) {
        this.richTextFileName = richTextFileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDataUuidField() {
        return dataUuidField;
    }

    public void setDataUuidField(String dataUuidField) {
        this.dataUuidField = dataUuidField;
    }
}
