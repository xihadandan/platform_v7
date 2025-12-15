package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Description: 流程分类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
@Entity
@Table(name = "WF_DEF_CATEGORY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
@DynamicUpdate
@DynamicInsert
@ApiModel("流程分类")
public class FlowCategory extends TenantEntity {
    private static final long serialVersionUID = -1341572156858573480L;
    // 名称
    @NotBlank
    @ApiModelProperty("名称")
    private String name;

    // 编号
    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("图标颜色")
    private String iconColor;

    @ApiModelProperty("备注")
    private String remark;

    // 自关联，父编号
    @UnCloneable
    @ApiModelProperty("上级分类")
    private FlowCategory parent;

    // 子结点
    @UnCloneable
    @ApiModelProperty("子分类")
    private Set<FlowCategory> children;

    private String moduleId;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

    private List<AppDefElementI18nEntity> i18ns;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid")
    public FlowCategory getParent() {
        return parent;
    }

    /**
     * @param parent 要设置的parent
     */
    public void setParent(FlowCategory parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade(CascadeType.ALL)
    @OrderBy("code asc")
    public Set<FlowCategory> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(Set<FlowCategory> children) {
        this.children = children;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconColor() {
        return iconColor;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Transient
    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
