package com.wellsoft.pt.repository.entity;

import com.wellsoft.context.jdbc.entity.BaseEntity;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.repository.support.enums.EnumOperateType;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "repo_folder_operate_log")
@DynamicUpdate
@DynamicInsert
public class FolderOperateLog extends BaseEntity {

    private static final long serialVersionUID = 7183087787493388099L;

    private String operateType;
    private String fileJson;
    private Folder folder;

    private String uuid;
    private Date createTime;
    private String creator;
    private Date modifyTime;
    private String modifier;
    private Integer recVer;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getFileJson() {
        return fileJson;
    }

    public void setFileJson(String fileJson) {
        this.fileJson = fileJson;
    }

    public void setFileJson(List<FileInFolder> fileJson) {
        this.fileJson = JSONArray.fromObject(fileJson).toString();
    }

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_UUID", unique = false, nullable = true, insertable = true, updatable = true)
    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public void doBindOperateType(EnumOperateType optype) {
        this.operateType = optype.getValue();
    }

    public void doBindFiles(List<FileInFolder> files) {
        long time1 = System.currentTimeMillis();
        if (files != null) {
            JsonConfig cfg = new JsonConfig();
            cfg.setJsonPropertyFilter(new PropertyFilter() {
                public boolean apply(Object source, String name, Object value) {
                    if (name.equals("folder")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            this.fileJson = (JSONArray.fromObject(files, cfg).toString());
        }

        long time2 = System.currentTimeMillis();
        System.out.println("convert log to json spend " + (time2 - time1) / 1000.0 + "s");
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

    @Id
    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public Date getModifyTime() {
        return modifyTime;
    }

    @Override
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Version
    @Override
    public Integer getRecVer() {
        return recVer;
    }

    @Override
    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    public void doBindUuid() {
        this.uuid = UuidUtils.createUuid();

    }
}
