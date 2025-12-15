package com.wellsoft.pt.dms.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/5
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/5    chenq		2018/7/5		Create
 * </pre>
 */
public class DataActiveDyformParam implements Serializable {

    private String formUuid;
    private List<String> dataValues;
    private List<String> dataTexts;
    private String dataValueColumn;
    private String dataTextColumn;
    private String lastActiveTimeColumn;
    private String activeCntColumn;

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public List<String> getDataValues() {
        return dataValues;
    }

    public void setDataValues(List<String> dataValues) {
        this.dataValues = dataValues;
    }

    public List<String> getDataTexts() {
        return dataTexts;
    }

    public void setDataTexts(List<String> dataTexts) {
        this.dataTexts = dataTexts;
    }

    public String getDataValueColumn() {
        return dataValueColumn;
    }

    public void setDataValueColumn(String dataValueColumn) {
        this.dataValueColumn = dataValueColumn;
    }

    public String getDataTextColumn() {
        return dataTextColumn;
    }

    public void setDataTextColumn(String dataTextColumn) {
        this.dataTextColumn = dataTextColumn;
    }


    public String getLastActiveTimeColumn() {
        return lastActiveTimeColumn;
    }

    public void setLastActiveTimeColumn(String lastActiveTimeColumn) {
        this.lastActiveTimeColumn = lastActiveTimeColumn;
    }

    public String getActiveCntColumn() {
        return activeCntColumn;
    }

    public void setActiveCntColumn(String activeCntColumn) {
        this.activeCntColumn = activeCntColumn;
    }


}
