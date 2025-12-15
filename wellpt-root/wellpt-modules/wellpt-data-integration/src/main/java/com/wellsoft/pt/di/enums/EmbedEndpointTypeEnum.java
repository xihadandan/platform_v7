package com.wellsoft.pt.di.enums;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/24
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/24    chenq		2019/7/24		Create
 * </pre>
 */
public enum EmbedEndpointTypeEnum {

    FILE("file", "本地服务器文件"),

    FTP("ftp", "FTP服务器文件"),

    DIRECT("direct", "同步调用");

    private String type;

    private String name;

    EmbedEndpointTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
