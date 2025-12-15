package com.wellsoft.context.util.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
 * 2024年05月25日   chenq	 Create
 * </pre>
 */
public class SheetImportRule implements Serializable {
    private static final long serialVersionUID = 4505026413903967630L;
    private int sheetIndex = 0;
    private String sheetName = "sheet1";
    private DuplicateStrategy duplicateStrategy;
    private List<HeaderRule> header = Lists.newArrayList();
    private Map<String, Object> params = Maps.newHashMap();
    private String table;
    private List<String> duplicateDataHeader;
    private Join join;
    private Boolean importLog = false;
    private Integer headerRowIndex;


    public int getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public DuplicateStrategy getDuplicateStrategy() {
        return duplicateStrategy;
    }

    public void setDuplicateStrategy(DuplicateStrategy duplicateStrategy) {
        this.duplicateStrategy = duplicateStrategy;
    }

    public List<HeaderRule> getHeader() {
        return header;
    }

    public void setHeader(List<HeaderRule> header) {
        this.header = header;
    }


    public Integer getHeaderRowIndex() {
        return headerRowIndex;
    }

    public void setHeaderRowIndex(Integer headerRowIndex) {
        this.headerRowIndex = headerRowIndex;
    }

    public List<String> getDuplicateDataHeader() {
        return duplicateDataHeader;
    }

    public void setDuplicateDataHeader(List<String> duplicateDataHeader) {
        this.duplicateDataHeader = duplicateDataHeader;
    }

    public Boolean getImportLog() {
        return importLog;
    }

    public void setImportLog(Boolean importLog) {
        this.importLog = importLog;
    }

    public HeaderRule getHeaderRuleByTitle(String title) {
        for (HeaderRule r : this.getHeader()) {
            if (r.getTitle().equalsIgnoreCase(title)) {
                return r;
            }
        }
        return null;
    }

    public static enum DuplicateStrategy {
        update, ignore;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Join getJoin() {
        return join;
    }

    public void setJoin(Join join) {
        this.join = join;
    }

    public static class Join implements Serializable {
        private static final long serialVersionUID = 4626498837986788702L;
        private String table;
        private String formUuid;
        private String joinHeader;
        private String joinColumn;

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public String getFormUuid() {
            return formUuid;
        }

        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }

        public String getJoinHeader() {
            return joinHeader;
        }

        public void setJoinHeader(String joinHeader) {
            this.joinHeader = joinHeader;
        }

        public String getJoinColumn() {
            return joinColumn;
        }

        public void setJoinColumn(String joinColumn) {
            this.joinColumn = joinColumn;
        }
    }

    public static class HeaderRule implements Serializable {
        private int index = 0;
        private String title;
        private String code;
        private String format;
        private DataType dataType;
        private Boolean required = false;
        private String regExp = null;
        private List<Map<String, String>> transformValueOption = Lists.newArrayList();
        private TransformValueType transformValueType;
        private String transformValueDataDictCode;
        private String transformValueOrgUuid;

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

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

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public List<Map<String, String>> getTransformValueOption() {
            return transformValueOption;
        }

        public void setTransformValueOption(List<Map<String, String>> transformValueOption) {
            this.transformValueOption = transformValueOption;
        }

        public String getRegExp() {
            return regExp;
        }

        public void setRegExp(String regExp) {
            this.regExp = regExp;
        }

        public TransformValueType getTransformValueType() {
            return transformValueType;
        }

        public void setTransformValueType(TransformValueType transformValueType) {
            this.transformValueType = transformValueType;
        }

        public DataType getDataType() {
            return dataType;
        }

        public void setDataType(DataType dataType) {
            this.dataType = dataType;
        }

        public static enum TransformValueType {
            dataDict, define, orgElement;
        }

        public static enum DataType {
            string, number, date;
        }

        public String getTransformValueDataDictCode() {
            return transformValueDataDictCode;
        }

        public void setTransformValueDataDictCode(String transformValueDataDictCode) {
            this.transformValueDataDictCode = transformValueDataDictCode;
        }

        public String getTransformValueOrgUuid() {
            return transformValueOrgUuid;
        }

        public void setTransformValueOrgUuid(String transformValueOrgUuid) {
            this.transformValueOrgUuid = transformValueOrgUuid;
        }
    }


}
