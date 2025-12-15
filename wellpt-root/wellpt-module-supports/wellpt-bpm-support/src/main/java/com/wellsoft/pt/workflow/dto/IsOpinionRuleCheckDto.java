package com.wellsoft.pt.workflow.dto;

/**
 * Description: 校验签署意见是否符合规则输出对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/5/18.1	    zenghw		2021/5/18		    Create
 * </pre>
 * @date 2021/5/18
 */
public class IsOpinionRuleCheckDto {

    private Boolean isSuccess = true;

    private String message;

    private Boolean isAlertAutoClose = true;

    public Boolean getAlertAutoClose() {
        return isAlertAutoClose;
    }

    public void setAlertAutoClose(Boolean alertAutoClose) {
        isAlertAutoClose = alertAutoClose;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
