package com.wellsoft.context.util.excel;

import com.alibaba.excel.annotation.ExcelProperty;

import java.util.Date;

/**
 * 导入数据接收样例
 */
public class ExampleExcelImportDo implements ExcelDo {

    @ExcelProperty("文本")
    private String text;

    @ExcelProperty("数字")
    private Integer number;

    @ExcelProperty("时间")
    private Date time;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}