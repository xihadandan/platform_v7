package com.wellsoft.pt.dyform.implement.definition.util.field;

import com.wellsoft.pt.dyform.implement.definition.enums.FieldTypeEnum;

/**
 * Description: 数值类型
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月28日.1	hongjz		2018年3月28日		Create
 * </pre>
 * @date 2018年3月28日
 */
public class NumberFieldType implements BaseFieldType {
    private int precision;// 所有有效数字的位数, 值的范围是1-38，即最大38位
    private int scale;// 小数位的位数

    @Override
    public final FieldTypeEnum getType() {
        return FieldTypeEnum.NUMBER;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

}
