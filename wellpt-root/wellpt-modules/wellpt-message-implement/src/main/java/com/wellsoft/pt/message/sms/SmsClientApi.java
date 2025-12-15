package com.wellsoft.pt.message.sms;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.message.sms.skmas.Rpt;
import com.wellsoft.pt.message.sms.skmas.Sms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 平台统一接口
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
public abstract class SmsClientApi {

    // 字段常量
    public static final int IM_CONN_SUCC = 0;
    public static final int IM_CONN_FAIL = -1;
    public static final int IM_SEND_SUCC = 0;
    public static final int IM_SEND_FAIL = -1;
    public static final int MSG_TYPE_MODEM = 0;
    public static final int MSG_TYPE_MAS = 1;
    public static final int MSG_TYPE_SMAS = 2;
    public static final int MSG_TYPE_CLOUDMAS = 3;
    public static final int MSG_TYPE_NONE = MSG_TYPE_MAS;// -1;默认是MAS短信机
    protected Logger logger = LoggerFactory.getLogger(getClass());
    // 配置变量
    protected String service;
    protected String apiCode;
    protected String username;
    protected String password;
    protected String dbname;
    protected String clientBean;

    public SmsClientApi() {
    }

    public SmsClientApi(String service, String apiCode, String username, String password,
                        String dbname,
                        String clientBean) {
        this.service = service;
        this.apiCode = apiCode;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
        this.clientBean = clientBean;
    }

    public int sendSM(String mobile, String content, long smID) {
        return sendSM(new String[]{mobile}, content, smID, smID);
    }

    public int sendSM(String[] mobiles, String content, long smID) {
        return sendSM(mobiles, content, smID, smID);
    }

    public int sendSM(String[] mobiles, String content, long smID, long srcID) {
        return sendSM(mobiles, content, smID, srcID, "");
    }

    public int sendSM(String[] mobiles, String content, String sendTime, long smID, long srcID) {
        return sendSM(mobiles, content, smID, srcID, "", sendTime);
    }

    public int sendSM(String mobile, String content, long smID, String url) {
        return sendSM(new String[]{mobile}, content, smID, url);
    }

    public int sendSM(String[] mobiles, String content, long smID, String url) {
        return sendSM(mobiles, content, smID, smID, url);
    }

    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url) {
        return sendSM(mobiles, content, smID, srcID, url, null);
    }

    /**
     * 如何描述该方法
     *
     * @param mobiles  手机号
     * @param content  发送内容
     * @param smID     短信ID
     * @param srcID
     * @param url      短信连接地址(短信内容)
     * @param sendTime 发送时间
     * @return 是否发送成功，是否需要重发：SmsClientApiFacade.NEED_RESEND
     */
    public abstract int sendSM(String[] mobiles, String content, long smID, long srcID, String url,
                               String sendTime);

    /**
     * 默认接收100短信
     *
     * @return
     */
    public MoType[] receiveSM() {
        return receiveSM(1L, 100);
    }

    /**
     * 如何描述该方法
     *
     * @param srcID
     * @param amount 接收短信条数
     * @return 短信内容
     */
    public abstract MoType[] receiveSM(long srcID, int amount);

    /**
     * 默认接收100回执
     *
     * @return
     */
    public RptType[] receiveRPT() {
        return receiveRPT(1L, 100);
    }

    /**
     * 如何描述该方法
     *
     * @param smID
     * @param amount 接收回执条数
     * @return 回执内容
     */
    public abstract RptType[] receiveRPT(long smID, int amount);

    /**
     * 获取短信机消息类型(0 modem; 1 mas机; 2 smas机; 3 云mas)
     */
    public abstract int getType();

    @Override
    public String toString() {
        return "SmsClientApi [service=" + service + ", apiCode=" + apiCode + ", username=" + username + ", password="
                + password + ", dbname=" + dbname + "]";
    }

    public SMasAbstractClient<Sms, Rpt> getServiceClient() {
        // TODO 根据配置获取实现类
        return ApplicationContextHolder.getBean(clientBean, SMasAbstractClient.class);
    }
}
