package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织单元模型定义
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_MODEL_DEF")
@DynamicUpdate
@DynamicInsert
public class OrgElementModelDefEntity extends SysEntity {

    private static final long serialVersionUID = -5790509465827213407L;


    private String orgElementModelId; // 组织模型id

    private String defJson;

    public String getOrgElementModelId() {
        return orgElementModelId;
    }

    public void setOrgElementModelId(String orgElementModelId) {
        this.orgElementModelId = orgElementModelId;
    }

    public String getDefJson() {
        return defJson;
    }

    public void setDefJson(String defJson) {
        this.defJson = defJson;
    }
}
