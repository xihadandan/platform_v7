package com.wellsoft.pt.bpm.engine.dto;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/7/1.1	    zenghw		2021/7/1		    Create
 * </pre>
 * @date 2021/7/1
 */
public class SaveLogFlowDefinitionDto {

    private String name;
    private Double version;
    private String id;
    private String oldXml;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOldXml() {
        return oldXml;
    }

    public void setOldXml(String oldXml) {
        this.oldXml = oldXml;
    }
}
