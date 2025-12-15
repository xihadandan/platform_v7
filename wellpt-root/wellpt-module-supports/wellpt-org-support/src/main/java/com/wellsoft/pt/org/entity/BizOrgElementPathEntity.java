package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 业务组织节点路径
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_ELEMENT_PATH")
@DynamicUpdate
@DynamicInsert
public class BizOrgElementPathEntity extends SysEntity {


    private static final long serialVersionUID = -5597563431400156709L;

    private String bizOrgElementId;
    private String cnPath;
    private String pinYinPath;
    private String idPath;
    private Boolean leaf;
    private Long bizOrgUuid;
    private String localPath;

    public BizOrgElementPathEntity() {
    }

    public BizOrgElementPathEntity(String bizOrgElementId, String cnPath, String pinYinPath, String idPath, Boolean leaf) {
        this.bizOrgElementId = bizOrgElementId;
        this.cnPath = cnPath;
        this.pinYinPath = pinYinPath;
        this.idPath = idPath;
        this.leaf = leaf;
    }

    public String getBizOrgElementId() {
        return bizOrgElementId;
    }

    public void setBizOrgElementId(String bizOrgElementId) {
        this.bizOrgElementId = bizOrgElementId;
    }

    public String getCnPath() {
        return cnPath;
    }

    public void setCnPath(String cnPath) {
        this.cnPath = cnPath;
    }

    public String getPinYinPath() {
        return pinYinPath;
    }

    public void setPinYinPath(String pinYinPath) {
        this.pinYinPath = pinYinPath;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }

    @Transient
    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
