package com.wellsoft.pt.basicdata.serialnumber.bean;

import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;

/**
 * Description: 流水号定义VO类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-7.1	zhouyq		2013-3-7		Create
 * </pre>
 * @date 2013-3-7
 */
public class SerialNumberBean extends SerialNumber {

    private static final long serialVersionUID = 1L;

    /**
     * 流水号定义使用人，从组织机构中选择直接作为ACL中的SID
     */
    private String ownerNames;

    private String ownerIds;

    //setter getter
    public String getOwnerNames() {
        return ownerNames;
    }

    public void setOwnerNames(String ownerNames) {
        this.ownerNames = ownerNames;
    }

    public String getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(String ownerIds) {
        this.ownerIds = ownerIds;
    }

}
