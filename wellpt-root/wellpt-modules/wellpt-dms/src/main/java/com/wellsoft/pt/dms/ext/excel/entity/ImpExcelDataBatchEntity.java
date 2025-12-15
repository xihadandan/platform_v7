package com.wellsoft.pt.dms.ext.excel.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

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
@Table(name = "IMP_EXCEL_DATA_BATCH")
@DynamicUpdate
@DynamicInsert
public class ImpExcelDataBatchEntity extends SysEntity {

    private String batchNo;

    private String fileId;

    private String fileName;

    private String code;// 业务代码

    private int successCount;

    private int failCount;

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private List<ImpExcelDataBatchDetailEntity> details;

    @Transient
    public List<ImpExcelDataBatchDetailEntity> getDetails() {
        return details;
    }

    public void setDetails(List<ImpExcelDataBatchDetailEntity> details) {
        this.details = details;
    }
}
