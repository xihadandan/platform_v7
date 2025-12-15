package com.wellsoft.context.util.excel;

import com.alibaba.excel.read.metadata.ReadSheet;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

//汇总报告
public class ExcelImportDataReport implements Serializable {

    private Long batchUuid;

    private String msg;

    private int code = 0;

    public ExcelImportDataReport() {
    }

    private List<SheetResult> sheetResults = Lists.newArrayList();

    public List<SheetResult> getSheetResults() {
        return sheetResults;
    }

    public static class SheetResult implements Serializable {
        private int sheetIndex;
        private String sheetName;
        private List<ExcelRowDataAnalysedResult> results = Lists.newArrayList();

        public SheetResult() {
        }

        public SheetResult(int sheetIndex, String sheetName) {
            this.sheetIndex = sheetIndex;
            this.sheetName = sheetName;
        }

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

        public List<ExcelRowDataAnalysedResult> getResults() {
            return results;
        }

        public void setResults(List<ExcelRowDataAnalysedResult> results) {
            this.results = results;
        }
    }


    private List<ExcelRowDataAnalysedResult> success = Lists.newArrayList();
    private List<ExcelRowDataAnalysedResult> fail = Lists.newArrayList();

    public List<ExcelRowDataAnalysedResult> getSuccess() {
        return success;
    }

    public List<ExcelRowDataAnalysedResult> getFail() {
        return fail;
    }


    public void addSheetResult(ReadSheet sheet, ExcelRowDataAnalysedResult result) {
        if (this.sheetResults.size() == sheet.getSheetNo()) {
            this.sheetResults.add(new SheetResult(sheet.getSheetNo(), sheet.getSheetName()));
        }
        this.sheetResults.get(sheet.getSheetNo()).getResults().add(result);

    }

    public Long getBatchUuid() {
        return batchUuid;
    }

    public void setBatchUuid(Long batchUuid) {
        this.batchUuid = batchUuid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}