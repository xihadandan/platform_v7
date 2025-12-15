package com.wellsoft.pt.dm.hibernate;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class PropertyBuilder {

    Property property = null;

    public PropertyBuilder() {
        property = new Property();
    }

    public PropertyBuilder name(String name) {
        this.property.setName(name);
        return this;
    }

    public PropertyBuilder type(String type) {
        // 类型转换
        if ("varchar".equalsIgnoreCase(type)) {
            type = "string";
        } else if ("number".equalsIgnoreCase(type)) {
            type = "java.math.BigDecimal";
        }
        this.property.setType(type);
        return this;
    }

    public PropertyBuilder length(Integer length) {
        if (length != null) {
            this.property.setLength(length);
        }
        return this;
    }

    public PropertyBuilder precision(Integer precision) {
        this.property.setPrecision(precision);
        return this;
    }

    public PropertyBuilder scale(Integer scale) {
        if (scale != null) {
            this.property.setScale(scale);
        }
        return this;
    }

    public PropertyBuilder unique(boolean unique) {
        this.property.setUnique(unique);
        return this;
    }

    public PropertyBuilder notNull(boolean notNull) {
        this.property.setNotNull(notNull);
        return this;
    }

    public PropertyBuilder comment(String comment) {
        this.property.setComment(comment);
        return this;
    }

    public PropertyBuilder defaultValue(String defaultValue) {
        this.property.setDefaultValue(defaultValue);
        return this;
    }

    public PropertyBuilder rename(String rename) {
        this.property.setRename(rename);
        return this;
    }

    public PropertyBuilder uniqueKey(String uniqueKey) {
        this.property.setUniqueKey(uniqueKey);
        return this;
    }

    public Property toProperty() {
        if (this.property.getType().equalsIgnoreCase("java.math.BigDecimal")) {
            // 数字型长度转精度
            if (this.property.getPrecision() == null && this.property.getLength() != null) {
                this.property.setPrecision(this.property.getLength());
            }
        }
        if (StringUtils.isNotBlank(this.property.getDefaultValue()) && "string".equalsIgnoreCase(this.property.getType())) {
            this.property.setDefaultValue("'" + this.property.getDefaultValue() + "'");
        }
        return this.property;
    }


    public List<Property> copy(List<Property> propertyList) {
        if (propertyList != null) {
            List<Property> out = Lists.newArrayListWithCapacity(propertyList.size());
            for (Property p : propertyList) {
                Property newProp = new Property();
                BeanUtils.copyProperties(p, newProp);
                out.add(newProp);
            }
            return out;
        }
        return null;
    }
}