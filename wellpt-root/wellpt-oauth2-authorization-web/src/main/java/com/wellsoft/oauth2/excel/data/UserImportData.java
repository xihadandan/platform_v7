package com.wellsoft.oauth2.excel.data;

import com.alibaba.excel.annotation.ExcelProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/25    chenq		2019/9/25		Create
 * </pre>
 */

public class UserImportData {

    @ExcelProperty("用户账号")
    @SerializedName("用户账号")
    private String accountNumber;

    @ExcelProperty("用户姓名")
    @SerializedName("用户姓名")
    private String userName;

    @ExcelProperty("密码")
    @SerializedName("密码")
    private String password;

    @ExcelProperty("手机号码")
    @SerializedName("手机号码")
    private String cellphoneNumber;

    public static void main(String[] arrs) {
        UserImportData data = new UserImportData();
        data.setAccountNumber("1111");
        System.out.println(new Gson().toJson(data));
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }
}
