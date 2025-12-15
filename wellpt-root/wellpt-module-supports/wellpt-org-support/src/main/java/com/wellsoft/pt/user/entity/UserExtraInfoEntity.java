/*
 * @(#)2021-04-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.user.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 用户信息扩展表
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-04-08.1	zenghw		2021-04-08		Create
 * </pre>
 * @date 2021-04-08
 */
@Entity
@Table(name = "USER_EXTRA_INFO")
@DynamicUpdate
@DynamicInsert
public class UserExtraInfoEntity extends IdEntity {

    private static final long serialVersionUID = 1617867222539L;

    private String valueString;

    private Integer valueInt;

    private Date valueDate;

    /**
     * //静态变量，枚举类：EnumDataType
     * // string date int
     **/
    private String dataType;
    /**
     * // 对应USER_ACCOUNT的uuid
     **/
    private String accountUuid;
    /**
     * // 字段名
     **/
    private String keyName;

    /**
     * @return the valueString
     */
    public String getValueString() {
        return this.valueString;
    }

    /**
     * @param valueString
     */
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    /**
     * @return the valueInt
     */
    public Integer getValueInt() {
        return this.valueInt;
    }

    /**
     * @param valueInt
     */
    public void setValueInt(Integer valueInt) {
        this.valueInt = valueInt;
    }

    /**
     * @return the valueDate
     */
    public Date getValueDate() {
        return this.valueDate;
    }

    /**
     * @param valueDate
     */
    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return this.dataType;
    }

    /**
     * @param dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the accountUuid
     */
    public String getAccountUuid() {
        return this.accountUuid;
    }

    /**
     * @param accountUuid
     */
    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    /**
     * @return the keyName
     */
    public String getKeyName() {
        return this.keyName;
    }

    /**
     * @param keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

}
