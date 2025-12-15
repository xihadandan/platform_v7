package com.wellsoft.pt.basicdata.exceltemplate.bean;

import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelColumnDefinition;

/**
 * Description: Excel列对应VO类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-24.1	zhouyq		2013-4-22		Create
 * </pre>
 * @date 2013-4-22
 */
public class ExcelColumnDefinitionBean extends ExcelColumnDefinition {

    private static final long serialVersionUID = 1L;
    // jqGrid默认传过来的行标识
    private String id;

    private String guuid;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getGuuid() {
        return guuid;
    }

    public void setGuuid(String guuid) {
        this.guuid = guuid;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((guuid == null) ? 0 : guuid.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExcelColumnDefinitionBean other = (ExcelColumnDefinitionBean) obj;
        if (guuid == null) {
            if (other.guuid != null)
                return false;
        } else if (!guuid.equals(other.guuid))
            return false;
        return true;
    }
}
