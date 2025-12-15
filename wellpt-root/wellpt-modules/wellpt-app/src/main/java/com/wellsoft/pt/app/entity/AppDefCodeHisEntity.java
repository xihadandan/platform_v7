package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;

/**
 * Description: 自定义代码的历史数据
 *
 * @author chenq
 * @date 2018/12/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/7    chenq		2018/12/7		Create
 * </pre>
 */
@Entity
@Table(name = "APP_DEFINE_CODE_HIS")
@DynamicUpdate
@DynamicInsert
public class AppDefCodeHisEntity extends TenantEntity {

    private String relaBusizUuid;

    private Clob script;

    private String scriptType;

    private String author;

    private String remark;

    public String getRelaBusizUuid() {
        return relaBusizUuid;
    }

    public void setRelaBusizUuid(String relaBusizUuid) {
        this.relaBusizUuid = relaBusizUuid;
    }

    public Clob getScript() {
        return script;
    }

    public void setScript(Clob script) {
        this.script = script;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
