package com.wellsoft.pt.api.response;

import com.wellsoft.context.enums.ApiCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/8    chenq		2018/8/8		Create
 * </pre>
 */
@ApiModel(value = "返回请求实体")
public class ApiResponse implements Serializable {
    @ApiModelProperty(value = "状态值")
    private int code = 0;
    @ApiModelProperty(value = "返回信息")
    private String msg;
    @ApiModelProperty(value = "返回数据对象")
    private Object data;

    public ApiResponse() {

    }

    public ApiResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ApiResponse build() {
        return new ApiResponse();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ApiResponse code(int code) {
        this.code = code;
        return this;
    }

    public ApiResponse msg(String msg) {
        this.msg = msg;
        return this;
    }

    public ApiResponse code(ApiCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getDescription();
        return this;
    }

    public ApiResponse data(Object data) {
        this.data = data;
        return this;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == 0;
    }

}
