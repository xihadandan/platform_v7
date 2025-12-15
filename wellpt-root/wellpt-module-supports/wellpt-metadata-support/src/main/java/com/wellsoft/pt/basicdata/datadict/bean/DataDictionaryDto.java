package com.wellsoft.pt.basicdata.datadict.bean;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/11    chenq		2019/10/11		Create
 * </pre>
 */
public class DataDictionaryDto implements Serializable {

    private String uuid;

    private String name;

    private String code;

    private String type;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
