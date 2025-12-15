package com.wellsoft.pt.workflow.dto;

/**
 * Description: 校验项输出类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/5/13.1	    zenghw		2021/5/13		    Create
 * </pre>
 * @date 2021/5/13
 */
public class WfOpinionRuleItemDto {

    // 校验条件 枚举：ItemConditionEnum
    // IC01:等于
    // IC02:不等于
    // IC03:大于
    // IC04:大于等于
    // IC05:小于
    // IC06:小于等于
    // IC07:包含
    // IC08:不包含
    private String itemCondition;

    private String itemValue;

    private String opinionRuleUuid;
    // 固定值：意见内容或意见长度
    private String itemName;

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getOpinionRuleUuid() {
        return opinionRuleUuid;
    }

    public void setOpinionRuleUuid(String opinionRuleUuid) {
        this.opinionRuleUuid = opinionRuleUuid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
