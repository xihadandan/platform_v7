package com.wellsoft.pt.basicdata.sso.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.json.JsonUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "xzsp_sso_params")
@DynamicUpdate
@DynamicInsert
public class SsoParams extends IdEntity {

    private static final long serialVersionUID = -3045616969303300943L;
    // 系统ID
    private String sysId;
    // 参数字段
    private String paramName;
    // 参数提示文本
    private String paramTipText;
    // 参数类型(hidden,text,select)
    private String paramType;
    // 参数值
    private String paramValue;
    // 参数默认值
    private String defaultValue;

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String obtParamName() {
        if (paramName == null) {
            paramName = "paramName";
        }
        return paramName;
    }

    public String getParamTipText() {
        return paramTipText;
    }

    public void setParamTipText(String paramTipText) {
        this.paramTipText = paramTipText;
    }

    public String obtParamTipText() {
        if (obtParamType().equalsIgnoreCase("hidden")) {
            return "";
        }
        if (paramTipText == null) {
            paramTipText = "paramTip";
        }
        return paramTipText;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String obtParamType() {
        if (paramType == null) {
            paramType = "hidden";
        }
        return paramType;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @SuppressWarnings("all")
    public Map<String, String> obtParamMap() {
        Map<String, String> params = JsonUtils.toMap(getParamValue() == null ? "" : getParamValue());
        if (params == null) {
            params = new HashMap<String, String>();
        }
        return params;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String obtDefaultValue() {
        if (defaultValue == null) {
            defaultValue = "";
        }
        return defaultValue;
    }

    public String parseToHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<td><label for='").append(obtParamName()).append("'").append(">");//生成标签
        sb.append(obtParamTipText()).append("</lable></td>");
        if (obtParamType().equalsIgnoreCase("select")) {
            sb.append("<td><select id='").append(obtParamName()).append("'");
            sb.append(" name='").append(obtParamName()).append("'");
            sb.append(" style='width:86%' value='").append(obtDefaultValue()).append("'").append(">");
            for (String key : obtParamMap().keySet()) {
                sb.append("<option value='").append(key).append("'>").append(obtParamMap().get(key))
                        .append("</option>");
            }
            sb.append("</select></td>");
        } else if (obtParamType().equalsIgnoreCase("text") || obtParamType().equalsIgnoreCase("hidden")) {
            sb.append("<td><input type='").append(obtParamType()).append("'");
            sb.append(" id='").append(obtParamName()).append("'");
            sb.append(" name='").append(obtParamName()).append("'");
            sb.append(" style='width:86%' value='").append(obtDefaultValue()).append("'").append("></td>");
        }
        return sb.toString();
    }
}