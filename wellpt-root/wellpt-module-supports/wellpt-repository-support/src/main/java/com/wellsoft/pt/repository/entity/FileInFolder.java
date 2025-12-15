package com.wellsoft.pt.repository.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: 文件夹中的文件
 *
 * @author hunt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-5.1	hunt		2014-1-5		Create
 * </pre>
 * @date 2014-1-5
 */
@Entity
@Table(name = "repo_file_in_folder")
@DynamicUpdate
@DynamicInsert
public class FileInFolder extends IdEntity {
    private static final long serialVersionUID = -6737427582159684073L;

    private String fileUuid;
    private String purpose;
    private Folder folder;
    private Integer seq;

    /*
     * private String uuid; private Date createTime; private String creator;
     * private Date modifyTime; private String modifier; private Integer recVer;
     */

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_UUID", unique = false, nullable = true, insertable = true, updatable = true)
    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public void doBindCreateTimeAsNow() {
        this.setCreateTime(new Date());
    }

    public void doBindModifyTimeAsNow() {
        this.setModifyTime(new Date());
    }

    public void doBindModifierAsCurrentUser() {
        this.setModifier(SpringSecurityUtils.getCurrentUserId());
    }

    public void doBindCreatorAsCurrentUser() {
        this.setCreator(SpringSecurityUtils.getCurrentUserId());
    }

    /*
     * public String getUuid() { return uuid; }
     *
     * public void setUuid(String uuid) { this.uuid = uuid; }
     */
    /*
     * public Date getCreateTime() { return createTime; }
     *
     * public void setCreateTime(Date createTime) { this.createTime =
     * createTime; }
     *
     * public String getCreator() { return creator; }
     *
     * public void setCreator(String creator) { this.creator = creator; }
     *
     * public Date getModifyTime() { return modifyTime; }
     *
     * public void setModifyTime(Date modifyTime) { this.modifyTime =
     * modifyTime; }
     *
     * public String getModifier() { return modifier; }
     *
     * public void setModifier(String modifier) { this.modifier = modifier; }
     *
     * public Integer getRecVer() { return recVer; }
     *
     * public void setRecVer(Integer recVer) { this.recVer = recVer; }
     */

}
