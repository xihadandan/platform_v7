package com.wellsoft.pt.workflow.dto;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * Description: 保存流程签署意见规则输入类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/6/3.1	    zenghw		2021/6/3		    Create
 * </pre>
 * @date 2021/6/3
 */
public class SaveOpinionRuleDto extends BaseObject {
    private static final long serialVersionUID = -8250579814716964855L;

    private String uuid;
    // 满足条件
    private String satisfyCondition;
    // 提示3s自动关闭
    private String isAlertAutoClose;
    // 提示语
    private String cueWords;
    // 规则名称
    private String opinionRuleName;

    private Map<String, Map<String, String>> i18n = Maps.newHashMap();

    //校验项 json串
    private String opinionRuleItems;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getSatisfyCondition() {
        return this.satisfyCondition;
    }

    public void setSatisfyCondition(final String satisfyCondition) {
        this.satisfyCondition = satisfyCondition;
    }

    public String getIsAlertAutoClose() {
        return this.isAlertAutoClose;
    }

    public void setIsAlertAutoClose(final String isAlertAutoClose) {
        this.isAlertAutoClose = isAlertAutoClose;
    }

    public String getCueWords() {
        return this.cueWords;
    }

    public void setCueWords(final String cueWords) {
        this.cueWords = cueWords;
    }

    public String getOpinionRuleName() {
        return this.opinionRuleName;
    }

    public void setOpinionRuleName(final String opinionRuleName) {
        this.opinionRuleName = opinionRuleName;
    }

    public String getOpinionRuleItems() {
        return this.opinionRuleItems;
    }

    public void setOpinionRuleItems(final String opinionRuleItems) {
        this.opinionRuleItems = opinionRuleItems;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Map<String, Map<String, String>> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }
}
