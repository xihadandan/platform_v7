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
@Table(name = "DATA_MARK_TYPE")
@DynamicUpdate
@DynamicInsert
public class DataMarkTypeEntity extends SysEntity {

    private String dataUuid;

    private Type type;

    public String getDataUuid() {
        return this.dataUuid;
    }

    public void setDataUuid(final String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public static enum Type {
        FIXED, DEMO
    }


}
