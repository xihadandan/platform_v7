package com.wellsoft.pt.dms.enums;

/**
 * Description: 文档交换操作枚举类
 *
 * @author chenq
 * @date 2018/5/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/19    chenq		2018/5/19		Create
 * </pre>
 */
public enum DocEchangeOperationEnum {

    SAVE("保存"), SEND("发送"), EXTRA_SEND("补充发送"), REVOKE("撤回"), URGE("催办"), SIGN("签收"), RETURN(
            "退回"), FORWARD("转发"), FINISH("办结"), FEEDBACK("反馈意见"), ANSWER(
            "回执"), REQUEST_FEEDBACK_AGAIN("要求再次反馈");

    private String name;

    DocEchangeOperationEnum(String name) {
        this.name = name;
    }

    public static DocEchangeOperationEnum get(Integer ordinal) {
        DocEchangeOperationEnum[] enums = DocEchangeOperationEnum.values();
        for (DocEchangeOperationEnum e : enums) {
            if (e.ordinal() == ordinal) {
                return e;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
