package com.wellsoft.pt.dms.enums;

/**
 * Description:文档交换记录的状态
 *
 * @author chenq
 * @date 2018/5/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/16    chenq		2018/5/16		Create
 * </pre>
 */
public enum DocExchangeRecordStatusEnum {

    DRAFT("草稿"), SENDED("已发送"), FINISH("已办结"), RETURNED("已退回"), SIGNED("已签收"), FEEDBACK_DONE(
            "已反馈"), WAIT_SIGN("待签收"), WAI_FEEDBACK("待反馈"), WAIT_APPROVAL("待审批"), REJECTED("审批不通过");

    private String name;

    DocExchangeRecordStatusEnum(String name) {
        this.name = name;
    }

    public static DocExchangeRecordStatusEnum get(Integer ordinal) {
        DocExchangeRecordStatusEnum[] enums = DocExchangeRecordStatusEnum.values();
        for (DocExchangeRecordStatusEnum e : enums) {
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
