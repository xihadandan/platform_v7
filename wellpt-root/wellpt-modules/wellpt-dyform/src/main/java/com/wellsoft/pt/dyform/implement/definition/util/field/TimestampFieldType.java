package com.wellsoft.pt.dyform.implement.definition.util.field;

import com.wellsoft.pt.dyform.implement.definition.enums.FieldTypeEnum;

/**
 * Description: 日期类型
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
public class TimestampFieldType implements BaseFieldType {
    private String pattern;

    @Override
    public final FieldTypeEnum getType() {
        return FieldTypeEnum.TIMESTAMP;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
