package com.wellsoft.pt.dm.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月06日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "DATA_MODEL")
@DynamicUpdate
@DynamicInsert
public class DataModelEntity extends SysEntity {

    private String id;

    private String name;

    private String remark;

    private String module;

    private Type type;

    public DataModelEntity() {
    }

    public DataModelEntity(String id, String name, Type type, String remark, String module) {
        this.id = id;
        this.name = name;
        this.remark = remark;
        this.module = module;
        this.type = type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(final String module) {
        this.module = module;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public static enum Type {
        TABLE, VIEW, RELATION
    }


}
