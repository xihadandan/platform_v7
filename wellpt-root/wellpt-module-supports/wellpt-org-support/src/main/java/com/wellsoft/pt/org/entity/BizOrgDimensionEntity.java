package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description: 业务组织维度
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_DIMENSION")
@DynamicUpdate
@DynamicInsert
public class BizOrgDimensionEntity extends SysEntity {

    public static final String ELEMENT_TYPE = "bizOrgDimension";

    private static final long serialVersionUID = -6439733567657406085L;
    private String name;
    private String icon;
    private String remark;
    private String id;

    private List<OrgElementI18nEntity> i18ns;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Transient
    public List<OrgElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<OrgElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
