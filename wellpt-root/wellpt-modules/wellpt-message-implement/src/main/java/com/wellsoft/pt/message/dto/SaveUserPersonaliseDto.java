package com.wellsoft.pt.message.dto;


/**
 * SaveUserPersonaliseDto
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 20-10-22.1	shenhb		20-10-22		Create
 * </pre>
 * @date 20-10-22
 */
public class SaveUserPersonaliseDto {

    private Integer mainSwitch;
    private String[] templateIds;
    private Integer[] isPopups;

    public Integer getMainSwitch() {
        return mainSwitch;
    }

    public void setMainSwitch(Integer mainSwitch) {
        this.mainSwitch = mainSwitch;
    }

    public String[] getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(String[] templateIds) {
        this.templateIds = templateIds;
    }

    public Integer[] getIsPopups() {
        return isPopups;
    }

    public void setIsPopups(Integer[] isPopups) {
        this.isPopups = isPopups;
    }
}
