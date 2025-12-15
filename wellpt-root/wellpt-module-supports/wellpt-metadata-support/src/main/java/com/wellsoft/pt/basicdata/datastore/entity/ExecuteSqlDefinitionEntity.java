package com.wellsoft.pt.basicdata.datastore.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Clob;

/**
 * Description: 可执行SQL 定义配置
 *
 * @author chenq
 * @date 2018/9/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/4    chenq		2018/9/4		Create
 * </pre>
 */
@Entity
@Table(name = "EXEC_SQL_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class ExecuteSqlDefinitionEntity extends TenantEntity {

    private static final long serialVersionUID = -1798252106265668976L;

    @JsonIgnore
    private Clob sqlContent;

    @NotBlank
    private String id;

    @NotBlank
    private String name;
    private String sqlText;

    public Clob getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(Clob sqlContent) {
        this.sqlContent = sqlContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Transient
    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
