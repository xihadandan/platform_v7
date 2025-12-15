package com.wellsoft.pt.dm.jdbc;


import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.SysEntity;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年05月12日   chenq	 Create
 * </pre>
 */
public class Model extends SysEntity {

    private static final long serialVersionUID = 3026719445268359253L;

    private String table;
    // 其他属性
    private List<Prop> props = Lists.newArrayList();

    public Model() {
    }

    public Model(String table) {
        this.table = table;
    }


    public List<Prop> getProps() {
        return props;
    }

    public void setProps(List<Prop> props) {
        this.props = props;
    }

    public List<Prop> mergedProps() {
        List<Prop> props = Lists.newArrayList(this.props);
        props.add(new Prop("UUID", this.uuid));
        props.add(new Prop("CREATE_TIME", this.createTime));
        props.add(new Prop("CREATOR", this.creator));
        props.add(new Prop("MODIFIER", this.modifier));
        props.add(new Prop("MODIFY_TIME", this.modifyTime));
        props.add(new Prop("TENANT", this.tenant));
        props.add(new Prop("SYSTEM", this.system));
        props.add(new Prop("REC_VER", this.recVer));
        return props;
    }

    public List<String> propNames() {
        List<String> names = Lists.newArrayList("UUID", "CREATE_TIME", "CREATOR", "MODIFIER",
                "MODIFY_TIME", "TENANT", "SYSTEM", "REC_VER");
        for (Prop p : this.props) {
            names.add(p.code.toUpperCase());
        }
        return names;
    }

    public List<Object> propValues() {
        List<Object> values = Lists.newArrayList(this.uuid, this.createTime, this.creator, this.modifier,
                this.modifyTime, this.tenant, this.system, this.recVer);
        for (Prop p : this.props) {
            values.add(p.getTransformedValue());
        }
        return values;
    }

    public Prop setPropValue(String code, Object value) {
        for (Prop p : this.props) {
            if (code.equalsIgnoreCase(p.code)) {
                p.setValue((Serializable) value);
                return p;
            }
        }
        Prop p = new Prop(code, value);
        this.props.add(p);
        return p;
    }

    public Object getPropValue(String code) {
        for (Prop p : this.props) {
            if (code.equalsIgnoreCase(p.code)) {
                return p.getTransformedValue();
            }
        }
        return null;
    }


    public Prop getProp(String code) {
        for (Prop p : this.props) {
            if (code.equalsIgnoreCase(p.code)) {
                return p;
            }
        }
        return null;
    }

    @Transient
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    // 其他属性
    public static class Prop implements Serializable {

        private String code;
        private Object value;
        private String type;

        public Prop() {
        }

        public Prop(String code, Object value) {
            this.value = value;
            this.code = code;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getTransformedValue() {
            if ("timestamp".equalsIgnoreCase(this.type) && !Date.class.isInstance(this.value)) {
                try {// 日期转换
                    if (String.class.isInstance(this.value)) {
                        return DateUtils.parseDate(this.value.toString(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH", "yyyy-MM-dd");
                    } else if (Number.class.isInstance(this.value)) {
                        return new Date(Long.parseLong(this.value.toString()));
                    }
                } catch (Exception e) {
                }
            } else if ("number".equalsIgnoreCase(this.type) && Number.class.isInstance(this.value)) {
                if (String.class.isInstance(this.value)) {
                    return new BigDecimal(this.value.toString());
                }
            }
            return this.value;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


}
