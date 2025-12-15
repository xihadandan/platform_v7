package com.wellsoft.pt.repository.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年11月04日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "REPO_FILE_WAIT_UPLOAD")
@DynamicUpdate
@DynamicInsert
public class FileWaitUploadEntity extends IdEntity {

    private String md5;

    private String fileUuid;

    private String fileName;

    private String log;

    private int failCount;

    private int dataLock;

    private Date retryTime;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getDataLock() {
        return dataLock;
    }

    public void setDataLock(int dataLock) {
        this.dataLock = dataLock;
    }

    public Date getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(Date retryTime) {
        this.retryTime = retryTime;
    }
}
