package com.wellsoft.context.util.excel;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

//行分析结果
public class ExcelRowDataAnalysedResult implements Serializable {

    private boolean ok = true;
    private String msg;
    private int rowIndex = 0;
    private String dataJson;
    private Map<Integer, String> errorCellMsg = Maps.newHashMap();
    private Map<String, Object> dataAnalysed = Maps.newHashMap();


    public ExcelRowDataAnalysedResult() {
    }

    public ExcelRowDataAnalysedResult(boolean ok) {
        this.ok = ok;
    }

    public ExcelRowDataAnalysedResult(boolean ok, String msg) {
        this.ok = ok;
        this.msg = msg;
    }

    public ExcelRowDataAnalysedResult(boolean ok, String msg, int rowIndex) {
        this.ok = ok;
        this.msg = msg;
        this.rowIndex = rowIndex;
    }


    public boolean isOk() {
        return this.ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public ExcelRowDataAnalysedResult fail(String msg) {
        this.setOk(false);
        this.setMsg(msg);
        return this;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public Map<String, Object> getDataAnalysed() {
        return dataAnalysed;
    }

    public void setDataAnalysed(Map<String, Object> dataAnalysed) {
        this.dataAnalysed = dataAnalysed;
    }

    public Map<Integer, String> getErrorCellMsg() {
        return errorCellMsg;
    }

    public void setErrorCellMsg(Map<Integer, String> errorCellMsg) {
        this.errorCellMsg = errorCellMsg;
    }

    public ExcelRowDataAnalysedResult putErrorCellMsg(Integer cellIndex, String msg) {
        this.errorCellMsg.put(cellIndex, msg);
        return this;
    }
}