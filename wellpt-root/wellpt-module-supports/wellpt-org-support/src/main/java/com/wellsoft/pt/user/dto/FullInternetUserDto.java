package com.wellsoft.pt.user.dto;

/**
 * Description: 完整的互联网用户对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/4/8.1	    zenghw		2021/4/8		    Create
 * </pre>
 * @date 2021/4/8
 */
public class FullInternetUserDto extends UserDto {

    private String accountUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
