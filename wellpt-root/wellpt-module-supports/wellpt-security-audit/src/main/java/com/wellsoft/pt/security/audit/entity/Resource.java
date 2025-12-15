package com.wellsoft.pt.security.audit.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: 权限资源实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
@Entity
@Table(name = "AUDIT_RESOURCE")
@DynamicUpdate
@DynamicInsert
public class Resource extends IdEntity {

    private static final long serialVersionUID = 5782279931514872804L;
    @ApiModelProperty("资源名称")
    @NotBlank
    private String name;
    @ApiModelProperty("编码")
    @NotBlank
    private String code;
    @ApiModelProperty("资源类型")
    private String type;
    @ApiModelProperty("target")
    private String target;
    @ApiModelProperty("URL")
    private String url;
    @ApiModelProperty("是否激活")
    private Boolean enabled;
    @ApiModelProperty("是否为动态资源")
    private Boolean dynamic;
    @ApiModelProperty("是否默认显示，只对动态资源有效")
    private Boolean isDefault;
    @ApiModelProperty("是否默认显示，只对动态资源有效")
    private String applyTo;
    @ApiModelProperty("是否系统内置资源")
    private Boolean issys;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("CLASS样式")
    private String className;
    @ApiModelProperty("父节点")
    @UnCloneable
    private Resource parent;
    @ApiModelProperty("父节点uuid")
    private String parentUuid;

    @ApiModelProperty("子结点")
    @UnCloneable
    private Set<Resource> children = new HashSet<Resource>();

    @ApiModelProperty("资源所属的权限")
    @UnCloneable
    private Set<Privilege> privileges = new HashSet<Privilege>();

    @ApiModelProperty("模块ID")
    private String moduleId;

    /**
     *
     */
    public Resource() {
        super();
    }

    /**
     * @param uuid
     */
    public Resource(String uuid) {
        super(uuid);
    }

    /**
     * @return the name
     */
    @Column(nullable = false)
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target 要设置的target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the type
     */
    @Column(nullable = false)
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 要设置的url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the dynamic
     */
    public Boolean getDynamic() {
        return dynamic;
    }

    /**
     * @param dynamic 要设置的dynamic
     */
    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the applyTo
     */
    public String getApplyTo() {
        return applyTo;
    }

    /**
     * @param applyTo 要设置的applyTo
     */
    public void setApplyTo(String applyTo) {
        this.applyTo = applyTo;
    }

    /**
     * @return the issys
     */
    public Boolean getIssys() {
        return issys;
    }

    /**
     * @param issys 要设置的issys
     */
    public void setIssys(Boolean issys) {
        this.issys = issys;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className 要设置的className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public Resource getParent() {
        return parent;
    }

    /**
     * @param parent 要设置的parent
     */
    public void setParent(Resource parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("code asc")
    public Set<Resource> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(Set<Resource> children) {
        this.children = children;
    }

    /**
     * @return the privileges
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "resources")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    /**
     * @param privileges 要设置的privileges
     */
    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Resource other = (Resource) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Transient
    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

}
