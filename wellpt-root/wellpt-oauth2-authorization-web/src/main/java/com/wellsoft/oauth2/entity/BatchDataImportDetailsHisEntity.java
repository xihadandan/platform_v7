package com.wellsoft.oauth2.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/25    chenq		2019/9/25		Create
 * </pre>
 */
@Entity
@Table(name = "batch_data_import_details_his")
@DynamicInsert
@DynamicUpdate
public class BatchDataImportDetailsHisEntity extends BaseEntity {

    private Long batchDataImportUuid;

    private Integer rowIndex;

    private String importData;

    private Integer status;

    private String errorMsg;


    public Long getBatchDataImportUuid() {
        return batchDataImportUuid;
    }

    public void setBatchDataImportUuid(Long batchDataImportUuid) {
        this.batchDataImportUuid = batchDataImportUuid;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getImportData() {
        return importData;
    }

    public void setImportData(String importData) {
        this.importData = importData;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
