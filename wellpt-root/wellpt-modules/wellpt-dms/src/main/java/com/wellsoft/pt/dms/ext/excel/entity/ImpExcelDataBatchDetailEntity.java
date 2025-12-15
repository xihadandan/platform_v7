package com.wellsoft.pt.dms.ext.excel.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年05月26日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "IMP_EXCEL_DATA_BATCH_DETAIL")
@DynamicUpdate
@DynamicInsert
public class ImpExcelDataBatchDetailEntity extends SysEntity {

    private Long batchUuid;

    private String sheet;

    private int sheetIndex;

    private int rowIndex;

    private Boolean success;

    private String remark;

    private String dataJson;

    public Long getBatchUuid() {
        return batchUuid;
    }

    public void setBatchUuid(Long batchUuid) {
        this.batchUuid = batchUuid;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
}
