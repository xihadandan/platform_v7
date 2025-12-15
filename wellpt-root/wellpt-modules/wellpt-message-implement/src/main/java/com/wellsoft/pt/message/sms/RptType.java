package com.wellsoft.pt.message.sms;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-6.1	zhongzh		2015-2-6		Create
 * </pre>
 * @date 2015-2-6
 */
public interface RptType {
    /**
     * @return 短信ID
     */
    public String getSmId();

    /**
     * @return 手机号
     */
    public String getMobile();

    /**
     * @return 回执代码
     */
    public String getCode();

    /**
     * @return 回执描述
     */
    public String getDesc();

    /**
     * @return 回执时间
     */
    public String getMoTime();

    /**
     * @return 回去原始回执对象
     */
    public Object getRpt();

    /**
     * @return 回执成功
     */
    public boolean isRtpSUCC();

    /**
     * @return 回执失败
     */
    public boolean isRtpFAIL();
}
