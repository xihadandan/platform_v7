package com.wellsoft.pt.org.entity;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.SysEntity;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 组织单元实例路径
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_PATH")
@DynamicUpdate
@DynamicInsert
public class OrgElementPathEntity extends SysEntity {


    private static final long serialVersionUID = -6958546192756491003L;
    private String orgElementId;
    private Long orgElementUuid;
    private Long orgVersionUuid;
    private String cnPath;
    private String pinYinPath;
    private String idPath;
    private Boolean leaf; // 是否末级

    private String localPath;

    public String getOrgElementId() {
        return orgElementId;
    }

    public void setOrgElementId(String orgElementId) {
        this.orgElementId = orgElementId;
    }

    public Long getOrgElementUuid() {
        return this.orgElementUuid;
    }

    public void setOrgElementUuid(final Long orgElementUuid) {
        this.orgElementUuid = orgElementUuid;
    }

    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
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
        return this.leaf;
    }

    public void setLeaf(final Boolean leaf) {
        this.leaf = leaf;
    }


    /**
     * 获取最近指定前缀的id段
     *
     * @param path
     * @param prefix
     * @return
     */
    public static String nearestIdByPrefix(String path, String prefix, boolean fullPath) {
        if (StringUtils.isNotBlank(path)) {
            String[] parts = path.split(Separator.SLASH.getValue());
            for (int i = parts.length - 1; i >= 0; i--) {
                if (parts[i].startsWith(prefix)) {
                    return !fullPath ? parts[i] : StringUtils.join(ArrayUtils.subarray(parts, 0, i + 1), Separator.SLASH.getValue());
                }
            }
        }
        return null;
    }

    @Transient
    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
