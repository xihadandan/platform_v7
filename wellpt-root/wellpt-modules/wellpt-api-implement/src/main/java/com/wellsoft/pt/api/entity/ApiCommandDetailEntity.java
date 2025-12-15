package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;
import java.sql.Clob;

/**
 * Description: 指令明细-报文
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
@Table(name = "API_COMMAND_DETAIL")
@DynamicUpdate
@DynamicInsert
@Entity
public class ApiCommandDetailEntity extends TenantEntity {

    private static final long serialVersionUID = 1474818634492278422L;

    private Clob requestBody;

    private Blob adapterRequest;

    private Clob responseBody;

    private String commandUuid;

    public Clob getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Clob requestBody) {
        this.requestBody = requestBody;
    }

    public Clob getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Clob responseBody) {
        this.responseBody = responseBody;
    }

    public String getCommandUuid() {
        return commandUuid;
    }

    public void setCommandUuid(String commandUuid) {
        this.commandUuid = commandUuid;
    }


    public Blob getAdapterRequest() {
        return adapterRequest;
    }

    public void setAdapterRequest(Blob adapterRequest) {
        this.adapterRequest = adapterRequest;
    }
}
