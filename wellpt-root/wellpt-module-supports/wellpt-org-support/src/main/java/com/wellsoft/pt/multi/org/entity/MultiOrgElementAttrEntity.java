package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/26    chenq		2019/10/26		Create
 * </pre>
 */
@Entity
@Table(name = "MULTI_ORG_ELEMENT_ATTR")
@DynamicInsert
@DynamicUpdate
public class MultiOrgElementAttrEntity extends IdEntity {
    public static final String KEY_CERTIFICATE_SUBJECT_DN = "certificate.subject";
    public static final String KEY_RECEIVE_SMS_MESSAGE = "receive.sms.message";
    private static final long serialVersionUID = -8591898415860033888L;
    private String attrType;

    private String attrDisplay;

    private String attrValue;

    private String code;

    private String name;

    private String elementUuid;


    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElementUuid() {
        return elementUuid;
    }

    public void setElementUuid(String elementUuid) {
        this.elementUuid = elementUuid;
    }

    public String getAttrDisplay() {
        return attrDisplay;
    }

    public void setAttrDisplay(String attrDisplay) {
        this.attrDisplay = attrDisplay;
    }
}
