package com.wellsoft.pt.di.enums;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/16    chenq		2019/7/16		Create
 * </pre>
 */
public enum CallbackStatusEnum {

    WAIT_CALLBACK("等待反馈"), WAIT_DEAL("反馈等待处理"), DONE("反馈正常处理"), EXCEPTION_DONE("反馈异常处理");

    private String name;

    CallbackStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
