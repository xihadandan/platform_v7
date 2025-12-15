/*
 * @(#)2014-1-5 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.entity;

import com.mongodb.DBObject;
import com.wellsoft.context.jdbc.entity.BaseEntity;
import com.wellsoft.pt.repository.support.enums.EnumOperateType;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONObject;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.*;

/**
 * Description: 文件夹
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
@Table(name = "repo_folder")
@DynamicUpdate
@DynamicInsert
public class Folder extends BaseEntity {
    private static final long serialVersionUID = -7058336091441625677L;

    private List<FileInFolder> files = new ArrayList<FileInFolder>(0);

    private List<FolderOperateLog> logs = new ArrayList<FolderOperateLog>(0);

    private String _id;

    private String remark;

    private String uuid;
    private Date createTime;
    private String creator;
    private Date modifyTime;
    private String modifier;
    private Integer recVer;

    private String dbName;

    public static Folder doParse(String json) {
        @SuppressWarnings("rawtypes")
        Map<String, Class> classMap = new HashMap<String, Class>();
        classMap.put("files", FileInFolder.class);
        classMap.put("logs", FolderOperateLog.class);

        Folder logicFileInfo = (Folder) JSONObject.toBean(JSONObject.fromObject(json), Folder.class, classMap);

        return logicFileInfo;
    }

    public static Folder doParse(DBObject dbObj) {
        return doParse(dbObj.toString());
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_UUID")
    @OrderBy("CREATE_TIME")
    public List<FileInFolder> getFiles() {
        if (this.files == null) {
            this.files = new ArrayList<FileInFolder>();
        }
        return files;
    }

    public void setFiles(List<FileInFolder> files) {
        this.files = files;
    }

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_UUID")
    @OrderBy("CREATE_TIME")
    public List<FolderOperateLog> getLogs() {
        if (this.logs == null) {
            this.logs = new ArrayList<FolderOperateLog>();
        }
        return logs;
    }

    public void setLogs(List<FolderOperateLog> logs) {
        this.logs = logs;
    }

    /**
     * @param opType
     * @param files
     */
    public void doAddLog(EnumOperateType opType, List<FileInFolder> files) {
        FolderOperateLog log = new FolderOperateLog();
        log.doBindUuid();
        log.doBindOperateType(opType);
        log.doBindCreateTimeAsNow();
        log.doBindCreatorAsCurrentUser();
        log.doBindModifierAsCurrentUser();
        log.doBindModifyTimeAsNow();
        log.doBindFiles(files);
        this.logs.add(log);
    }

    public void doAddLog(EnumOperateType opType, FileInFolder file) {
        List<FileInFolder> files = new ArrayList<FileInFolder>();
        files.add(file);
        this.doAddLog(opType, files);
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Transient
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Version
    public Integer getRecVer() {
        return recVer;
    }

    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    /**
     * 设置租户ID为当前租户的ID
     */
    public void doBindDbNameAsCurrentTenantId() {
        this.setDbName(SpringSecurityUtils.getCurrentTenantId());
    }

}
