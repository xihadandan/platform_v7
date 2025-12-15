package com.wellsoft.pt.repository.entity;

/**
 * Description:
 * 金格file转换状态
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/8   Create
 * </pre>
 */
public enum ConvertStatus {
    NOT(0, "未开始"),
    ING(1, "进行中"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败");

    private int value;

    private String desc;

    ConvertStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
