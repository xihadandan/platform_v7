package com.wellsoft.context.exception;

/**
 * @author lilin
 * @ClassName: WellException
 * @Description: 所有oa的异常超类 各个异常可以继承实现,可以考虑绑定资源信息处理
 * @看struts 的异常处理 建立oa的异常处理体制和方式
 */
public class WellException extends RuntimeException {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public WellException() {
        super();
    }

    public WellException(String arg0) {
        super(arg0);
    }

    public WellException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public WellException(Throwable arg0) {
        super(arg0);
    }
}
