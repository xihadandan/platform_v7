package com.wellsoft.pt.basicdata.iexport.suport;

import org.apache.commons.compress.utils.Lists;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月09日   chenq	 Create
 * </pre>
 */
public class IExportEntityStream implements Serializable {

    @JsonIgnore
    private static ObjectMapper objectMapper;


    private List<IExportEntityStream> children = Lists.newArrayList();

    private String name;

    private Metadata metadata;

    private List<File> files = Lists.newArrayList();


    static {
        objectMapper = IExportJsonObjectMapperFactory.objectMapper();
    }


    public IExportEntityStream() {

    }

    public static IExportEntityStream fromJSON(String str) {
        try {
            return objectMapper.readValue(str, IExportEntityStream.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toJSON() {
        try {
            StringWriter writer = new StringWriter();
            objectMapper.writeValue(writer, this);
            return writer.toString();
        } catch (Exception e) {
        }
        return null;
    }

    public IExportEntityStream(String name, Metadata metadata) {
        this();
        this.name = name;
        this.metadata = metadata;
    }


    public List<IExportEntityStream> getChildren() {
        return children;
    }

    public void setChildren(List<IExportEntityStream> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public static class Metadata implements Serializable {

        private String data;
        private String type;
        private String uuid;
        private Integer recVer;

        public Metadata() {
        }

        public Metadata(String data, String type, String uuid, Integer recVer) {
            this.data = data;
            this.type = type;
            this.uuid = uuid;
            this.recVer = recVer;
        }


        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }


        public Integer getRecVer() {
            return recVer;
        }

        public void setRecVer(Integer recVer) {
            this.recVer = recVer;
        }
    }

    public static class File {
        private String name;
        private String filePhysicalId;

        @JsonIgnore
        private InputStream input;

        public File() {
        }


        public File(String name, InputStream input) {
            this.name = name;
            this.input = input;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public InputStream getInput() {
            return input;
        }

        public void setInput(InputStream input) {
            this.input = input;
        }

        public String getFilePhysicalId() {
            return filePhysicalId;
        }

        public void setFilePhysicalId(String filePhysicalId) {
            this.filePhysicalId = filePhysicalId;
        }
    }
}
