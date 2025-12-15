package com.wellsoft.pt.org.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.wellsoft.context.util.excel.ExcelDo;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月27日   chenq	 Create
 * </pre>
 */
public class OrgElementExcelDo implements ExcelDo {
    private static final long serialVersionUID = 1738147372939661962L;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("简称")
    private String shortName;

    @ExcelProperty("编号")
    private String code;

    @ExcelProperty("上级")
    private String parent;

    @ExcelProperty("备注")
    private String remark;

    private String type;

    private Long uuid;

    private Result result = new Result();


    private List<OrgElementExcelDo> children;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(final String parent) {
        this.parent = parent;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public List<OrgElementExcelDo> getChildren() {
        return this.children;
    }

    public void setChildren(final List<OrgElementExcelDo> children) {
        this.children = children;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Long getUuid() {
        return this.uuid;
    }

    public void setUuid(final Long uuid) {
        this.uuid = uuid;
    }

    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }


    public static class Result implements Serializable {
        private boolean success = true;
        private String msg;
        private int row;
        private String sheetName;

        public Result() {
        }

        public Result(final int row, final String sheetName) {
            this.row = row;
            this.sheetName = sheetName;
        }

        public Result success(boolean success) {
            this.success = success;
            return this;
        }

        public Result msg(String msg) {
            this.msg = msg;
            return this;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public void setSuccess(final boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return this.msg;
        }

        public void setMsg(final String msg) {
            this.msg = msg;
        }

        public int getRow() {
            return this.row;
        }

        public void setRow(final int row) {
            this.row = row;
        }

        public String getSheetName() {
            return this.sheetName;
        }

        public void setSheetName(final String sheetName) {
            this.sheetName = sheetName;
        }


    }
}
