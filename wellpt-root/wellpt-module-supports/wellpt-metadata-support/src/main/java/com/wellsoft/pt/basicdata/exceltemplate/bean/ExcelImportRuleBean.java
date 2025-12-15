package com.wellsoft.pt.basicdata.exceltemplate.bean;

import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: Excel导入规则VO类
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
public class ExcelImportRuleBean extends ExcelImportRule {

    private static final long serialVersionUID = 1L;
    private String fileName;
    private Set<ExcelColumnDefinitionBean> changeColumnDefinitions = new HashSet<ExcelColumnDefinitionBean>();
    private Set<ExcelColumnDefinitionBean> deletedExcelRows = new HashSet<ExcelColumnDefinitionBean>();

    /**
     * @return the changeColumnDefinitions
     */
    public Set<ExcelColumnDefinitionBean> getChangeColumnDefinitions() {
        return changeColumnDefinitions;
    }

    /**
     * @param changeColumnDefinitions 要设置的changeColumnDefinitions
     */
    public void setChangeColumnDefinitions(Set<ExcelColumnDefinitionBean> changeColumnDefinitions) {
        this.changeColumnDefinitions = changeColumnDefinitions;
    }

    public Set<ExcelColumnDefinitionBean> getDeletedExcelRows() {
        return deletedExcelRows;
    }

    public void setDeletedExcelRows(Set<ExcelColumnDefinitionBean> deletedExcelRows) {
        this.deletedExcelRows = deletedExcelRows;
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
        result = prime * result + ((changeColumnDefinitions == null) ? 0 : changeColumnDefinitions.hashCode());
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
        ExcelImportRuleBean other = (ExcelImportRuleBean) obj;
        if (changeColumnDefinitions == null) {
            if (other.changeColumnDefinitions != null)
                return false;
        } else if (!changeColumnDefinitions.equals(other.changeColumnDefinitions))
            return false;
        return true;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
