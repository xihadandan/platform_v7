package com.wellsoft.pt.basicdata.exceltemplate.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Description: Excel列对应实体类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-22.1	zhouyq		2013-4-24		Create
 * </pre>
 * @date 2013-4-24
 */
@Entity
@Table(name = "cd_excel_column_definition")
@DynamicUpdate
@DynamicInsert
public class ExcelColumnDefinition extends IdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 第几列
     */
    private Integer columnNum;
    /**
     * 属性名（域名）
     */
    private String attributeName;
    /**
     * 数据类型（域类型）
     */
    private String attributeType;

    @UnCloneable
    private ExcelImportRule excelImportRule;

    /**
     * @return the columnNum
     */
    public Integer getColumnNum() {
        return columnNum;
    }

    /**
     * @param columnNum 要设置的columnNum
     */
    public void setColumnNum(Integer columnNum) {
        this.columnNum = columnNum;
    }

    /**
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param attributeName 要设置的attributeName
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return the attributeType
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * @param attributeType 要设置的attributeType
     */
    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    /**
     * @return the excelImportRule
     */
    @ManyToOne
    @JoinColumn(name = "excel_import_rule_uuid")
    public ExcelImportRule getExcelImportRule() {
        return excelImportRule;
    }

    /**
     * @param excelImportRule 要设置的excelImportRule
     */
    public void setExcelImportRule(ExcelImportRule excelImportRule) {
        this.excelImportRule = excelImportRule;
    }

}
