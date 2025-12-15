package com.wellsoft.oauth2.dto;

import com.wellsoft.oauth2.enums.GenderEnum;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/23    chenq		2019/9/23		Create
 * </pre>
 */
public class UserDto implements Serializable {

    private String accountNumber;

    private String password;

    private String userName;

    private GenderEnum gender;

    private String identifiedCode;

    private String cellphoneNumber;


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getIdentifiedCode() {
        return identifiedCode;
    }

    public void setIdentifiedCode(String identifiedCode) {
        this.identifiedCode = identifiedCode;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
