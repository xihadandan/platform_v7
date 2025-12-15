package com.wellsoft.pt.ureport.dto;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/5/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/10    chenq		2019/5/10		Create
 * </pre>
 */
public class RpFileRepositoryDto implements Serializable {
    private static final long serialVersionUID = 7209271168259214641L;

    private String uuid;

    private String fileId;

    private String fileName;

    private String content;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
