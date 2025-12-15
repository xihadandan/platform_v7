package com.wellsoft.pt.message.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数配置，全局变量
 *
 * @author 范华鹏
 * @version 1.0
 */
public class Config {

    /**
     * 指定串口为COM1
     */
    public final static String com = "COM1";
    /**
     * AT命令 "AT+CNMI=2,2,0,0,1\r"
     */
    public final static String cmdSMT = "AT+CNMI=2,2,0,0,1\r";
    /**
     * AT命令 "AT+CSCA? \r"
     */
    public final static String cmdCSCA = "AT+CSCA?\r";
    /**
     * AT命令 "AT+CSMP=17,167,0,240\r"
     */
    public final static String cmdCSMP = "AT+CSMP=17,167,0,240\r";

    //private ShortMessageService shortMessageService = null;
    /**
     * 短信发送超时时间，秒为单位。默认为30秒
     */
    public static final int TimeOut = 30;
    /**
     * Modem发送短信的模式，"0"为使用PDU编码
     */
    public static final String modemMode = "0";
    /**
     * 手机号码错误
     */
    public static final String MPERROR = "手机号码错误";
    /**
     * 指定波特率
     */
    final static int baud = 9600;
    final static int maxtry = 5;
    final static String SMS_TYPE = "";
    private static final Config instance = new Config();

    static {
        System.setSecurityManager(null); //不使用安全管理器
    }

    /**
     * 待发短信队列
     */
    public List<List<String>> Message = new ArrayList<List<String>>();
    /**
     * 支持的MODEM最大数量，默认为20
     */
    public int comNum = 20;
    /**
     * 初始化COM口的超时时间，秒为单位。默认为10秒
     */
    public int iniTime = 10;

    private Config() {

    }

    public static Config getInstance() {
        return instance;
    }

    /**
     * 清空待发短信队列
     */
    public static void clearMsg() {
        if (instance.Message != null) {
            instance.Message.clear();
        }
    }

    /**
     * 取一条待发消息
     *
     * @param id
     * @return 返回类型为List的消息（MP，Content）
     */
    public static List<String> getMsg() {
        List<String> temp = null;
        if (instance.Message != null && !instance.Message.isEmpty()) {
            temp = (List<String>) instance.Message.get(instance.Message.size() - 1);
            instance.Message.remove(instance.Message.size() - 1);
        }
        return temp;
    }

    /**
     * 添加短信到待发队列
     *
     * @param id      Modem 的 id
     * @param noteID  短消息文档的NotesID
     * @param MP      对方手机号
     * @param content 短消息内容
     * @return 如果添加成功则返回true
     */
    public static boolean addMsg(int id, String noteID, String MP, String content) {
        List<String> msg = new ArrayList<String>();
        if (noteID != null && !noteID.equals("")) {
            msg.add(noteID);
            if (MP != null && !MP.equals("")) {
                msg.add(MP);
                if (content != null && !content.equals("")) {
                    msg.add(content);
                    if (instance.Message == null) {
                        instance.Message = new ArrayList<List<String>>();
                    }
                    if (instance.Message.add(msg)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置初始化COM口的超时时间
     *
     * @param n 时间，秒为单位
     */
    public static void setInitime(int n) {
        instance.iniTime = n;
    }

    /**
     * 设置COM的最大数量
     *
     * @param n 预设置的数量
     */
    public static void setComnum(int n) {
        instance.comNum = n;
    }

}
