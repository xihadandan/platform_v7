package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 单位代码
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_UNIT_CODE")
@DynamicUpdate
@DynamicInsert
public class OrgUnitCodeEntity extends SysEntity {

    private static final long serialVersionUID = -1636257520309266272L;
    private Long orgUnitUuid;
    private String orgUnitId;
    private CodeType codeType;
    private String code;

    public Long getOrgUnitUuid() {
        return orgUnitUuid;
    }

    public void setOrgUnitUuid(Long orgUnitUuid) {
        this.orgUnitUuid = orgUnitUuid;
    }

    public CodeType getCodeType() {
        return codeType;
    }

    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrgUnitId() {
        return this.orgUnitId;
    }

    public void setOrgUnitId(final String orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    public static enum CodeType {
        UNIFIED_SOCIAL_CREDIT_CODE/*统一社会信用代码*/, BUSINESS_LICENSE/*营业执照*/, TAX_REGISTRATION_NO/*税务登记号*/
    }
}
