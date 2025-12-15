package com.wellsoft.pt.ureport.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Clob;

/**
 * Description: 报表文件存储
 *
 * @author chenq
 * @date 2018/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/25    chenq		2018/9/25		Create
 * </pre>
 */
@Entity
@Table(name = "RP_FILE_REPOSITORY")
@DynamicInsert
@DynamicUpdate
public class RpFileRepositoryEntity extends IdEntity {

    private String fileId;

    private String fileName;

    @JsonIgnore
    private Clob content;

    private String contentStr;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Basic(fetch = FetchType.LAZY)
    public Clob getContent() {
        return content;
    }

    public void setContent(Clob content) {
        this.content = content;
    }

    @Transient
    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }
}
