package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author lilin
 * @ClassName: UserProperty
 * @Description: 用key，value来保存一些用户的扩展信息，方便扩展
 */
//@Entity
//@Table(name = "org_user_property")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class UserProperty extends IdEntity {
    public static final String KEY_ONLY_LOGON_CERTIFICATE = "only.logon.width.certificate";
    public static final String KEY_CERTIFICATE_SUBJECT_DN = "certificate.subject";
    public static final String KEY_RECEIVE_SMS_MESSAGE = "receive.sms.message";
    public static final String KEY_SHOW_CONTACT = "show.contact";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FORM_SPLIT_PAGE = "form.show.split";
    private static final long serialVersionUID = -6688333608257379754L;
    private String userUuid;
    private String name;
    private String value;
    private String clobValue;

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the userUuid
     */
    @Column(nullable = false)
    public String getUserUuid() {
        return userUuid;
    }

    /**
     * @param userUuid 要设置的userUuid
     */
    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the clobValue
     */
    public String getClobValue() {
        return clobValue;
    }

    /**
     * @param clobValue 要设置的clobValue
     */
    public void setClobValue(String clobValue) {
        this.clobValue = clobValue;
    }
}
