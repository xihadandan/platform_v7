package com.wellsoft.pt.basicdata.datastore.support.export;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年06月14日   chenq	 Create
 * </pre>
 */
public class ExportRows implements Serializable {

    private String fileName;
    private List<Map<String, String>> dataList;
    private List<HeaderTitle> titles;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }

    public List<HeaderTitle> getTitles() {
        return titles;
    }

    public void setTitles(List<HeaderTitle> titles) {
        this.titles = titles;
    }

    public static class HeaderTitle implements Serializable {
        private String title;
        private String code;
        private DataType dataType;
        private String format;
        private Boolean exportAttachment = false; // 同时导出附件


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }


        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public DataType getDataType() {
            return dataType;
        }

        public void setDataType(DataType dataType) {
            this.dataType = dataType;
        }

        public static enum DataType {
            string, number, date, file;
        }

        public Boolean getExportAttachment() {
            return exportAttachment;
        }

        public void setExportAttachment(Boolean exportAttachment) {
            this.exportAttachment = exportAttachment;
        }
    }
}
