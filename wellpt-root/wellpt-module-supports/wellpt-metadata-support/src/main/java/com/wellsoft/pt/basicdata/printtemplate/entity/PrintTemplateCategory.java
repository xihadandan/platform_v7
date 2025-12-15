package com.wellsoft.pt.basicdata.printtemplate.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CD_PRINT_TEMPLATE_CATEGORY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
@DynamicUpdate
@DynamicInsert
@ApiModel("打印模版分类")
public class PrintTemplateCategory extends TenantEntity {
    private static final long serialVersionUID = -1341572156858573480L;
    // 名称
    @NotBlank
    @ApiModelProperty("名称")
    private String name;

    // 编号
    @NotBlank
    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("图标颜色")
    private String iconColor;

    @ApiModelProperty("备注")
    private String remark;

    // 父结点UUID
    @ApiModelProperty("父结点UUID")
    private String parentUuid;

//	// 自关联，父编号
//	@UnCloneable
//	@ApiModelProperty("上级分类")
//	private PrintTemplateCategory parent;
//
//	// 子结点
//	@UnCloneable
//	@ApiModelProperty("子分类")
//	private Set<PrintTemplateCategory> children;

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

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    /**
     * //	 * @return the parent
     * //
     */
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "parent_uuid")
//	public PrintTemplateCategory getParent() {
//		return parent;
//	}
//
//	/**
//	 * @param parent
//	 *            要设置的parent
//	 */
//	public void setParent(PrintTemplateCategory parent) {
//		this.parent = parent;
//	}
//
//	/**
//	 * @return the children
//	 */
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
//	@Cascade(CascadeType.ALL)
//	@OrderBy("code asc")
//	public Set<PrintTemplateCategory> getChildren() {
//		return children;
//	}
//
//	/**
//	 * @param children
//	 *            要设置的children
//	 */
//	public void setChildren(Set<PrintTemplateCategory> children) {
//		this.children = children;
//	}
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
}
