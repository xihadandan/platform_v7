package com.wellsoft.pt.basicdata.printtemplate.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 打印记录实体类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-3.1	zhouyq		2013-4-3		Create
 * </pre>
 * @date 2013-4-3
 */
@Entity
@Table(name = "cd_print_record")
@DynamicUpdate
@DynamicInsert
public class PrintRecord extends IdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 使用人
     */
    private String userName;
    /**
     * 模板编号
     */
    private String code;
    /**
     * 打印对象
     */
    private String printObject;
    /**
     * 打印对象类型
     */
    private String printObjectType;
    /**
     * 打印次数
     */
    private Integer printTimes;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the printTimes
     */
    public Integer getPrintTimes() {
        return printTimes;
    }

    /**
     * @param printTimes 要设置的printTimes
     */
    public void setPrintTimes(Integer printTimes) {
        this.printTimes = printTimes;
    }

    /**
     * @return the printObject
     */
    public String getPrintObject() {
        return printObject;
    }

    /**
     * @param printObject 要设置的printObject
     */
    public void setPrintObject(String printObject) {
        this.printObject = printObject;
    }

    /**
     * @return the printObjectType
     */
    public String getPrintObjectType() {
        return printObjectType;
    }

    /**
     * @param printObjectType 要设置的printObjectType
     */
    public void setPrintObjectType(String printObjectType) {
        this.printObjectType = printObjectType;
    }

}
