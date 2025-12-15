package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年05月13日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "API_OPERATION")
@DynamicUpdate
@DynamicInsert
public class ApiOperationEntity extends SysEntity {

    private Long apiLinkUuid;

    private String code;

    private String name;

    private String remark;

    private String path;

    private String method;

    private String reqFormatType;

    private String resFormatType;

    private Long timeout;


    private List<ApiOperationParamEntity> parameters;

    private List<ApiOperationBodySchemaEntity> bodySchema;

    private ApiLinkEntity apiLink;

    public Long getApiLinkUuid() {
        return apiLinkUuid;
    }

    public void setApiLinkUuid(Long apiLinkUuid) {
        this.apiLinkUuid = apiLinkUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReqFormatType() {
        return reqFormatType;
    }

    public void setReqFormatType(String reqFormatType) {
        this.reqFormatType = reqFormatType;
    }

    public String getResFormatType() {
        return resFormatType;
    }

    public void setResFormatType(String resFormatType) {
        this.resFormatType = resFormatType;
    }

    @Transient
    public List<ApiOperationParamEntity> getParameters() {
        return parameters;
    }

    public void setParameters(List<ApiOperationParamEntity> parameters) {
        this.parameters = parameters;
    }

    @Transient
    public List<ApiOperationBodySchemaEntity> getBodySchema() {
        return bodySchema;
    }

    public void setBodySchema(List<ApiOperationBodySchemaEntity> bodySchema) {
        this.bodySchema = bodySchema;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Transient
    public ApiLinkEntity getApiLink() {
        return apiLink;
    }

    public void setApiLink(ApiLinkEntity apiLink) {
        this.apiLink = apiLink;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
