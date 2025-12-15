package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Clob;

/**
 * Description: api日志
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
@Table(name = "API_LOG")
@Entity
@DynamicInsert
@DynamicUpdate
public class ApiLogEntity extends TenantEntity {
    private static final long serialVersionUID = 2625994428493060553L;

    private String requestIp;

    private String target;

    private String operator;

    private Clob requestBody;

    private String description;

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Clob getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Clob requestBody) {
        this.requestBody = requestBody;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
