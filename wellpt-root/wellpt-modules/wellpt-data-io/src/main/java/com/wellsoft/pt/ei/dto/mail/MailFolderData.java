package com.wellsoft.pt.ei.dto.mail;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/9/27 19:00
 * @Description:
 */
public class MailFolderData implements Serializable {
    private static final long serialVersionUID = 5941463813777120187L;

    @FieldType(desc = "主键uuid", required = true)
    private String uuid;

    @FieldType(desc = "用户id", required = true)
    private String userId;

    @FieldType(desc = "文件夹名称", required = true)
    private String folderName;

    @FieldType(desc = "文件夹编码：用于标注邮件归属", required = true)
    private String folderCode;

    @FieldType(desc = "排序号", type = ExportFieldTypeEnum.INTEGER)
    private Integer seq;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderCode() {
        return folderCode;
    }

    public void setFolderCode(String folderCode) {
        this.folderCode = folderCode;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
