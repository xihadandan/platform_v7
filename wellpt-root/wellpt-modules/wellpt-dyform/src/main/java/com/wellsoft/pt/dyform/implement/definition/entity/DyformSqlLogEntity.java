package com.wellsoft.pt.dyform.implement.definition.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/3/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/3/8    chenq		2019/3/8		Create
 * </pre>
 */
@Entity
@Table(name = "dyform_sql_log")
@DynamicInsert
@DynamicUpdate
public class DyformSqlLogEntity extends IdEntity {

    private static final long serialVersionUID = 2054118699506298294L;

    private String sqlScript;

    private String formUuid;

    private String tableName;

    public String getSqlScript() {
        return sqlScript;
    }

    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
