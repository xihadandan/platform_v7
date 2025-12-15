package com.wellsoft.pt.bot.support;

import com.google.common.base.Objects;
import com.wellsoft.pt.bot.entity.BotRuleObjMappingEntity;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/18    chenq		2018/9/18		Create
 * </pre>
 */
public class BotFieldMapping {

    private String fromField;

    private String fromObjId;

    private String toField;

    private String toObjId;

    private BotFieldValue fieldValue;

    private boolean isReverse = false;

    private BotFieldMapping() {
    }

    public static BotFieldMapping build() {
        return new BotFieldMapping();
    }

    public String getFromField() {
        return fromField;
    }

    public void setFromField(String fromField) {
        this.fromField = fromField;
    }

    public String getToField() {
        return toField;
    }

    public void setToField(String toField) {
        this.toField = toField;
    }

    public BotFieldValue getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(BotFieldValue value) {
        this.fieldValue = value;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public String getFromObjId() {
        return fromObjId;
    }

    public void setFromObjId(String fromObjId) {
        this.fromObjId = fromObjId;
    }

    public String getToObjId() {
        return toObjId;
    }

    public void setToObjId(String toObjId) {
        this.toObjId = toObjId;
    }

    /**
     * 字段值的转换
     *
     * @param type
     * @param formData
     * @param fromFieldValue
     * @param toFieldValue
     * @param script
     * @return
     */
    public BotFieldMapping fieldValue(Integer type, Map<String, ?> formData,
                                      Object fromFieldValue, Object toFieldValue,
                                      String script) {
        if (BotRuleObjMappingEntity.VAL_TYPE_FTL.equals(type)) {
            this.setFieldValue(
                    new BotFieldFreemarkerValue(formData, fromFieldValue, toFieldValue, script));
        } else if (BotRuleObjMappingEntity.VAL_TYPE_GROOVY.equals(type)) {
            this.setFieldValue(
                    new BotFieldGroovyValue(formData, fromFieldValue, toFieldValue, script));
        } else {
            this.setFieldValue(new BasicBotFieldValue(fromFieldValue));
        }
        return this;
    }

    public BotFieldMapping reverse(boolean reverse) {
        this.isReverse = reverse;
        return this;
    }

    public BotFieldMapping fromObjId(String fromObjId) {
        this.fromObjId = fromObjId;
        return this;
    }

    public BotFieldMapping toObjId(String toObjId) {
        this.toObjId = toObjId;
        return this;
    }

    public BotFieldMapping toField(String toField) {
        this.toField = toField;
        return this;
    }

    public BotFieldMapping fromField(String fromField) {
        this.fromField = fromField;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotFieldMapping that = (BotFieldMapping) o;
        return isReverse == that.isReverse &&
                Objects.equal(fromField, that.fromField) &&
                Objects.equal(fromObjId, that.fromObjId) &&
                Objects.equal(toField, that.toField) &&
                Objects.equal(toObjId, that.toObjId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fromField, fromObjId, toField, toObjId, isReverse);
    }
}
