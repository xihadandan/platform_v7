/*
 * @(#)Dec 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 15, 2017.1	zhulh		Dec 15, 2017		Create
 * </pre>
 * @date Dec 15, 2017
 */
@Entity
@Table(name = "DMS_FOLDER_CONFIGURATION")
@DynamicUpdate
@DynamicInsert
public class DmsFolderConfigurationEntity extends IdEntity implements UUIDGeneratorIndicate {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1559811218726578323L;

    // 夹UUID
    private String folderUuid;
    // 阅读人员字段
    private String readFileField;
    // 编辑人员字段
    private String editFileField;
    // 是否继承夹的权限配置，1是，0否
    private Integer isInheritFolderRole;
    // 引用的授权对象UUID
    private String refObjectIdentityUuid;
    // 配置JSON信息
    private String configuration;

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

    /**
     * @return the readFileField
     */
    public String getReadFileField() {
        return readFileField;
    }

    /**
     * @param readFileField 要设置的readFileField
     */
    public void setReadFileField(String readFileField) {
        this.readFileField = readFileField;
    }

    /**
     * @return the editFileField
     */
    public String getEditFileField() {
        return editFileField;
    }

    /**
     * @param editFileField 要设置的editFileField
     */
    public void setEditFileField(String editFileField) {
        this.editFileField = editFileField;
    }

    /**
     * @return the isInheritFolderRole
     */
    public Integer getIsInheritFolderRole() {
        return isInheritFolderRole;
    }

    /**
     * @param isInheritFolderRole 要设置的isInheritFolderRole
     */
    public void setIsInheritFolderRole(Integer isInheritFolderRole) {
        this.isInheritFolderRole = isInheritFolderRole;
    }

    /**
     * @return the refObjectIdentityUuid
     */
    public String getRefObjectIdentityUuid() {
        return refObjectIdentityUuid;
    }

    /**
     * @param refObjectIdentityUuid 要设置的refObjectIdentityUuid
     */
    public void setRefObjectIdentityUuid(String refObjectIdentityUuid) {
        this.refObjectIdentityUuid = refObjectIdentityUuid;
    }

    /**
     * @return the configuration
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration 要设置的configuration
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

}
