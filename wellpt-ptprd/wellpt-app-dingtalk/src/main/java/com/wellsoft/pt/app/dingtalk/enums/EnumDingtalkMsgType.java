package com.wellsoft.pt.app.dingtalk.enums;

/**
 * Description: 钉钉消息类型
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
public enum EnumDingtalkMsgType {

    text("text", "普通文本"), image("image", "带图片"), file("file", "带文件"), link("link", "带链接"), markdown("markdown",
            "markdown"), oa("oa", "OA"), action_card("action_card", "卡片"),

    ;

    private String value = "";
    private String remark;

    private EnumDingtalkMsgType(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static EnumDingtalkMsgType value2EnumObj(String value) {
        EnumDingtalkMsgType enumObj = null;
        for (EnumDingtalkMsgType status : EnumDingtalkMsgType.values()) {
            if (status.getValue().equals(value)) {
                enumObj = status;
            }
        }

        return enumObj;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }
}
