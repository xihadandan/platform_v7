package com.wellsoft.pt.message.support;

import com.wellsoft.pt.message.entity.ShortMessage;
import com.wellsoft.pt.message.service.ShortMessageService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.comm.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.regex.Pattern;

/**
 * 无线MODEM适配器。 用来从MODEM发送短消息，以及从MODEM接收短消息
 *
 * @author 范华鹏
 * @version 1.0
 */
public class ModemAdapter extends Thread implements SerialPortEventListener {

    static Logger logger = Logger.getLogger(ModemAdapter.class);
    private static ModemAdapter modem;
    protected boolean init = true;
    protected boolean open = false;
    // 发送是否已成功完成
    private boolean sendOKFlag;
    //是否支持长短信
    private boolean supportLongMsg = true;
    private int errCount;
    // 发送模式是否是PDU方式
    private boolean isPduMode;
    private String smContent;
    private int msgCount = 1;
    private String NotesID = null;
    private String mobile = null; //调试用的
    private List<String> msg_pdu = new ArrayList<String>();
    private List<String> failedList = new ArrayList<String>();
    private int index = 0;
    private int MaxTry = 0;
    private String cmd1 = null;
    private boolean lock = false;
    private String smsc = null;
    private String cmdCMGF = "AT+CMGF=";
    private boolean iniFail = false;

    // private ArrayList<SubmitPack> sendBuffer;
    private String com = null; // 串口名称
    private int baud = 0; // 波特率
    // 要打开使用的串口
    private SerialPort sPort;
    private CommPortIdentifier portID;
    private ShortMessageService shortMessageService = null;

    /**
     * 构造一个ModemAdapter对象
     *
     * @param id     当前id
     * @param com    COM端口名称
     * @param baud   波特率
     * @param maxtry 发送失败后尝试重发次数
     */
    private ModemAdapter(String com, int baud, int maxtry, ShortMessageService shortMessageService) {
        this.com = com;
        this.MaxTry = maxtry;
        if (baud != 0)
            this.baud = baud;
        this.shortMessageService = shortMessageService;
        isPduMode = false;
        errCount = 0;
        logger.debug(".....Modem Start......");
        start();
    }

    public static ModemAdapter getModemAdapter(ShortMessageService shortMessageService) {
        if (modem == null)
            modem = new ModemAdapter(Config.com.toUpperCase(), Config.baud, Config.maxtry, shortMessageService);
        return modem;
    }

    /**
     * 获取当前COM端口
     *
     * @return 返回当前串口SerialPort对象
     */
    public SerialPort getPort() {
        return this.sPort;
    }

    /**
     * 是否支持长短信
     *
     * @return 如果支持则返回true
     */
    public boolean getLongMsgSupport() {
        return this.supportLongMsg;
    }

    /**
     * 设置是否支持长短信
     *
     * @param b
     */
    public void setLongMsgSupport(boolean b) {
        this.supportLongMsg = b;
    }

    // 得到计算机的串口
    private SerialPort getSerialPort() {
        SerialPort sPort = null;
        String owner = new String("modemn");
        int keeptime = 5000;
        Enumeration portList;
        portList = CommPortIdentifier.getPortIdentifiers();

        try {
            System.out.println(System.class.getClassLoader());
            //logger.debug("Win32Com Library Loaded");
            String driverName = "com.sun.comm.Win32Driver";
            CommDriver driver = (CommDriver) Class.forName(driverName).newInstance();
            driver.initialize();
            //logger.debug("Win32Driver Initialized");
        } catch (InstantiationException e1) {
            logger.error(e1.getMessage(), e1);
        } catch (IllegalAccessException e1) {
            logger.error(e1.getMessage(), e1);
        } catch (ClassNotFoundException e1) {
            logger.error(e1.getMessage(), e1);
        }

        if (portList == null) {
            System.out.println("找不到串口");
            logger.debug("找不到串口");
            return null;
        }
        // 如果有多个端口
        while (portList.hasMoreElements()) {
            portID = (CommPortIdentifier) portList.nextElement();

            if (portID.getName().equals(com))
                try {
                    sPort = (SerialPort) portID.open(owner, keeptime);
                    break;
                } // 打开一个串口
                catch (PortInUseException e) {
                    logger.error(this.com + "已被占用。", e);
                }
        } // while

        if (sPort != null) {
            this.open = true;
            logger.debug("serial name is :" + sPort.getName());
            try {
                // 设置串口的参数
                sPort.setSerialPortParams(this.baud, // 波特率
                        SerialPort.DATABITS_8, // 数据位数
                        SerialPort.STOPBITS_1, // 停止位
                        SerialPort.PARITY_NONE);// 奇偶位
            } catch (UnsupportedCommOperationException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return sPort;
    }

    private boolean init() {
        boolean result = false;
        if (this.com != null && !this.com.equals(""))
            sPort = getSerialPort();
        else
            return result;
        if (sPort != null) {
            listenSerialPort(sPort); //添加串口监听
            // 用配置参数初始化MODEM
            if (Config.modemMode != null && Config.modemMode.equals("0"))
                isPduMode = true;

            if (isPduMode)
                cmdCMGF += "0\r";
            else
                cmdCMGF += "1\r";
            sendMsg(cmdCMGF, sPort);
            sendOKFlag = true;

        } else {
            System.out.println("无法找到串口：" + this.com);
            return result;
        }

        for (int i = 0; i < Config.getInstance().iniTime; i++) {
            try {
                Thread.sleep(1000);
                if (iniFail)
                    break;
                if (sendOKFlag && !init) {
                    logger.debug("初始化" + sPort.getName() + "成功！");
                    System.out.println("初始化" + sPort.getName() + "完成");
                    return true;
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return result;
    }

    protected void closePort() {
        if (this.open) {
            sPort.removeEventListener();
            sPort.close();
            sPort = null;
            this.open = false;
        }
    }

    // 把短消息通过数据猫发送出去
    private void sendMsg(String msg, SerialPort sPort) {

        PrintWriter pw;
        if (msg != null && sPort != null)
            try {

                pw = new PrintWriter(sPort.getOutputStream());
                pw.println(msg);

                pw.flush();
                pw.close();
                //logger.debug("msg has been send from Modemn:");
                //logger.debug("发送数据" + msg);
                //System.out.println("发送数据:" + msg);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
    }

    // 把短消息通过数据猫发送出去
    private void sendMsg(byte[] msg, SerialPort sPort) {

        DataOutputStream pw;
        if (msg != null && sPort != null)
            try {

                pw = new DataOutputStream(sPort.getOutputStream());
                pw.write(msg);

                pw.flush();
                pw.close();
                //logger.debug("msg has been send from Modemn:");
                //logger.debug("发出代码：【" + new String(msg) + "】");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
    }

    private void listenSerialPort(SerialPort sPort) {

        if (sPort != null)
            try {
                sPort.addEventListener(this);
                sPort.notifyOnDataAvailable(true);
            } catch (TooManyListenersException e) {
                logger.error(e.getMessage(), e);
            }

    }

    /**
     * 初始化modem
     */
    public void run() {
        int waitCount = 0;
        if (init()) {
            while (true) {
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                while (true) {
                    if (sendOKFlag == true) {
                        waitCount = 0;
                        if (isPduMode) {
                            cmd1 = this.iniMsg();
                            if (cmd1 != null) {
                                //logger.debug("Cmd:" + cmd1);
                                sendMsg(cmd1.getBytes(), sPort);
                                lock = true;
                                sendOKFlag = false;
                                //logger.debug("isSendOK=false");
                            }
                            break;

                        }
                    } else
                        try {
                            sleep(1000);
                            if (waitCount > Config.TimeOut) {
                                if (NotesID != null && msg_pdu.isEmpty()) {
                                    //System.out.println("发送失败：" + NotesID);
                                    //SMS.setResult(NotesID, false, this.com, failedList, msgCount);
                                    NotesID = null;
                                    mobile = null;
                                    msgCount = 1;
                                } else {
                                    failedList.add(index + "");
                                }
                                sendOKFlag = true;
                                waitCount = 0;
                            } else
                                waitCount++;
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage(), e);
                        }

                }
            }
        } else {
			/*//测试
			ShortMessage shortsms = new ShortMessage();
			shortsms.setBody("测试");
			shortsms.setIsread(true);
			shortsms.setSmid(222);
			List<ShortMessage> list = new ArrayList<ShortMessage>();
			list.add(shortsms);
			shortMessageService.saveShortMessage(list);*/
            System.out.println("初始化" + this.com + "失败，请检查设备");
            logger.fatal("初始化" + this.com + "失败，请检查设备");
            this.closePort();
            iniFail = true;

        }
    }

    /**
     * 返回初始化结果
     *
     * @return 如果初始化失败则返回True
     */
    public boolean getIniResult() {
        return this.iniFail;
    }

    private String iniMsg() {
        String cmd = null;
        if (msg_pdu.isEmpty()) {
            NotesID = null;
            smContent = null;
            mobile = null;
            index = 0;
            List<String> temp = Config.getMsg(); //temp格式{0：接收方手机号，1：内容}
            if (temp != null) {
                //NotesID = (String) temp.get(0);
                String mp = (String) temp.get(0);
                mobile = mp;
                String content = (String) temp.get(1);
                logger.debug(com + " 准备发送：【" + mp + "】" + content);
                msg_pdu = PduPack.getPdu(smsc, mp, content, supportLongMsg);
                msgCount = msg_pdu.size();
            }
        }

        if (!msg_pdu.isEmpty()) {
			/*
			if (msg_pdu.size() >= 2) {
				logger.debug("★★★删除第二条短信。★★★");
				msg_pdu.remove(1);
			}
			*/
            String coded = (String) msg_pdu.get(0); //获取手机短信的PDU编码
            index++;
            msg_pdu.remove(0);
            if (coded != null && coded.length() > 18)
                cmd = "AT+CMGS=" + (coded.length() - 18) / 2 + "\r";
            smContent = coded + (char) Integer.parseInt("1A", 16);
            logger.debug("PDU【" + index + "】" + smContent);
        }
        return cmd;
    }

    /**
     * 处理侦听到的串口事件
     *
     * @param ev
     */
    public synchronized void serialEvent(SerialPortEvent ev) {

        DataInputStream in = null;
        int c = 0;
        StringBuilder sb = null;
        // 如果有串口事件发生
        if (ev.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                in = new DataInputStream(sPort.getInputStream());
                sb = new StringBuilder();
                while ((c = in.read()) != -1) {
                    sb.append((char) c);

                    // System.out.println(sb);
                    if (handleRecData(sb)) {
                        //logger.debug("从Modem接收到的数据【" + sb + "】");
                        sb = new StringBuilder();
                    }
                }
            } // try
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
    }

    /**
     * 获取短信中心号码
     *
     * @param dat
     * @return 返回短信中心号码
     */
    public String getCSCA(StringBuilder dat) {
        String data = dat.toString();
        //logger.debug("in getcsca ");

        int index0 = data.indexOf("+smsc:");
        if (index0 >= 0 && index0 < data.length()) {
            data = data.substring(index0);
            // logger.debug("data: "+data);
            if (data.length() > 22) {
                index0 = data.indexOf("\"");
                //logger.debug("index0=" + index0);
                if (index0 != -1) {
                    data = data.substring(index0 + 1);
                    // int index1 = data.lastIndexOf((char)0X0D);
                    int index1 = data.indexOf("\"");
                    // logger.debug(dat);
                    //logger.debug("index0=" + index0 + "index1=" + index1);
                    if (index1 != -1 && index1 < data.length()) {
                        String rt = data.substring(1, index1);
                        return rt;

                    }
                }

            }
        }
        return null;

    }

    /**
     * 判断接收到的数据是否最后是以"OK"结束的
     *
     * @param data
     * @return 如果数据以“OK”结束则返回True
     */
    private boolean isRecOK(String data) {
        final String OK_FLAG = "OK";
        int index1 = 0;

        if (data != null) {
            index1 = data.indexOf(OK_FLAG);

            if (index1 >= 0 && index1 + 4 <= data.length()) {
                String t = data.substring(index1 + 2);
                byte[] b = t.getBytes();
                if (b.length >= 2 && b[0] == 0x0D && b[1] == 0x0A) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断接收到的数据是否最后是以"/r/n"结束
     *
     * @param data
     * @return 如果数据以以"/r/n"结束，则返回True
     */
    public boolean isRecOver(String data) {
        // final String OVER_FLAG = "/r";
        int index1 = 0;
        int index2 = 0;
        // logger.debug("in isReceOver");
        if (data != null) {
            index1 = data.lastIndexOf((char) 0x0D);
            index2 = data.lastIndexOf((char) 0x0A);
            // logger.debug("index1="+index1+"index2="+index2);
            if (index1 >= 0 && index2 >= 0 && index2 - index1 == 1)
                return true;

        }
        return false;
    }

    /**
     * 发送短消息是否成功.
     * <p>
     * 判断依据: 收到回应的消息中有+CMGS:<space><number>,紧接着是两个换行回车(0x0D,0x0A,0x0D,0x0A),
     * 然后是OK,最后是一个回车换行(0x0D,0x0A)
     *
     * @param data
     * @return 如果短信发送成功则返回True
     */
    private boolean isSendOK(String data) {
        final String FLAG = "+CMGS:";
        int index = -1;
        int index2 = -1;

        if (data != null) {
            index = data.indexOf(FLAG);
            if (index > 0) {
                index += 6;
                if (index < data.length()) {
                    String temp = data.substring(index);
                    index = 0;
                    byte[] b = temp.getBytes();
                    for (int i = 0; i < b.length; i++) {
                        if (b[i] == 0x0D) {
                            index2 = i;
                            break;
                        }
                    }

                    if (index2 < temp.length() && index2 > index + 1) {
                        //String t1 = temp.substring(index + 1, index2);

                        try {
                            // 两个回车换行符 // OK // 一个回车换行
                            if (index2 + 8 == temp.length() && b[index2] == 0x0D && b[index2 + 1] == 0x0A
                                    && b[index2 + 2] == 0x0D && b[index2 + 3] == 0x0A && b[index2 + 4] == 0x4F
                                    && b[index2 + 5] == 0x4B && b[index2 + 6] == 0x0D && b[index2 + 7] == 0x0A) {
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            logger.error(e.getMessage(), e);
                            return false;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * 判断接收到的字符串最后是否是以"ERROR"结束的
     *
     * @param data
     * @return 如果接收到的字符串以"ERROR"结束则返回True
     */
    private boolean isRecError(String data) {

        final String FLAG = "ERROR";

        int index1 = 0;

        if (data != null) {
            index1 = data.indexOf(FLAG);

            if (index1 >= 0 && index1 + 7 <= data.length()) {
                String t = data.substring(index1 + 5);
                byte[] b = t.getBytes();
                if (b.length >= 2 && b[0] == 0x0D && b[1] == 0x0A) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 是否接收到手机发来的完整数据,上传的数据是以"+CMT:"开头
     *
     * @param data
     * @return 如果数据是以"+CMT:"开头，则返回True
     */
    private boolean isRecData(String data) {
        final String BEGIN_FLAG = "+CMT:";
        int index0 = -1;

        if (data != null) {
            index0 = data.indexOf(BEGIN_FLAG);
            if (index0 != -1 && data.length() > 20) {
                String t = data.substring(data.length() - 2);
                byte[] b = t.getBytes();
                if (b.length == 2 && b[0] == 0x0D && b[1] == 0x0A) {
                    return true;
                }
            }
        }
        return false;
    }

    //处理短信发送后
    private boolean handleRecData(StringBuilder sb) {
        String data = null;

        if (sb != null) {
            data = sb.toString();
            //System.out.println(data);
            if (isRecOK(data)) {
                //logger.debug("收到代码：【" + data + "】");
                if (isSendOK(data)) { //发送成功  收到了系统回执
                    // 改写任务状态
                    if (msg_pdu.isEmpty()) {
                        logger.debug("【" + mobile + "】发送成功。ID=" + NotesID);

                        NotesID = null;
                        mobile = null;
                        failedList.clear();
                    }
                } else if (init && data.indexOf(Config.cmdSMT) != -1) {
                    logger.debug(this.com + " SMT参数设置成功");
                    sendMsg(Config.cmdCSCA.getBytes(), sPort);
                } else if (init && data.indexOf(cmdCMGF) != -1) {
                    logger.debug(this.com + " CMGF参数设置成功");
                    sendMsg(Config.cmdSMT.getBytes(), sPort);
                } else if (init && data.indexOf(Config.cmdCSCA) != -1) {
                    smsc = data.substring(data.indexOf("\"") + 1);
                    smsc = smsc.substring(0, smsc.indexOf("\""));
                    logger.debug(this.com + " 消息服务中心号：" + smsc);
                    init = false;
                } else if (init && data.indexOf(Config.cmdCSMP) != -1) {
                    logger.debug(this.com + " 成功设置" + Config.cmdCSMP);
                    init = false;
                }

                smContent = null;
                sendOKFlag = true;
                return true;
            } else if (isRecError(data)) { //发送失败
                logger.debug("错误信息：" + data);
                if (init && (data.indexOf(cmdCMGF) != -1 || data.indexOf(Config.cmdCSCA) != -1)
                        || data.indexOf(Config.cmdSMT) != -1 || data.indexOf(Config.cmdCSMP) != -1)
                    iniFail = true;
                errCount++;
                if (data.indexOf("AT+CMGS=") != -1 && errCount > MaxTry) {
                    if (msg_pdu.isEmpty()) {
                        //SMS.setResult(NotesID, false, this.com, failedList, msgCount);
                        logger.debug("【" + mobile + "】发送失败.ID=" + smContent);
                        NotesID = null;
                        mobile = null;
                        failedList.clear();
                    } else {
                        failedList.add(index + "");
                    }
                    smContent = null;
                    sendOKFlag = true;
                    errCount = 0;
                } else {
                    logger.debug("重新发送：" + errCount);
                    if (cmd1 != null) {
                        sendMsg(cmd1.getBytes(), sPort);
                        lock = true;
                    } else {
                        sendOKFlag = true;
                    }
                }
                return true;
            } else if (data.indexOf(">") != -1 && lock) {
                if (smContent != null)
                    sendMsg(smContent.getBytes(), sPort);
                lock = false;
            } else if (isRecData(data)) {
                // 接收短信
                String temp = data;
                int index0 = data.lastIndexOf("+CMT:");
                if (index0 >= 0 && index0 < data.length()) {
                    data = data.substring(index0);
                    data = data.substring(data.indexOf("\n") + 1);
                    PduPack pack = new PduPack(data, false);
                    String srcAddr = pack.getAddr(); //发送方号码
                    String content = pack.getMsgContent(); //短信内容
                    String time = pack.getTime(); //接收时间
                    String sign = pack.getMsgSign(); // 超长短信的唯一标识
                    int total = pack.getMsgTotal(); //该短信共分几条
                    int index = pack.getMsgIndex(); //当前为第几条

                    logger.debug("PDU：" + temp);
                    if (srcAddr != null && content != null && content.indexOf("#") != -1) {
                        String smidstr = content.substring(0, content.indexOf("#"));
                        if (smidstr.length() == 3) {
                            Pattern p = Pattern.compile("[0-9]{3}");
                            if (p.matcher(smidstr).matches()) {
                                String[] cons = content.split("#");

                                //找到发信人信息
                                List<ShortMessage> smlist = shortMessageService.findBySmid(cons[0]);
                                if (smlist != null && smlist.size() > 0) {
                                    for (ShortMessage object : smlist) {
                                        if (object.getRecipientMobilePhone().equals(srcAddr)) {
                                            List<ShortMessage> sms = new ArrayList<ShortMessage>();
                                            ShortMessage s = new ShortMessage();
                                            s.setSmid(Long.valueOf(cons[0]));
                                            s.setBody(cons[1]);
                                            s.setIsread(false); //备用
                                            s.setRecipientMobilePhone(object.getSendMobilePhone());
                                            s.setRecipientName(object.getSenderName());
                                            s.setSendMobilePhone(srcAddr);
                                            s.setSenderName(object.getRecipientName());
                                            //s.setSendTime(new SimpleDateFormat("").parse(time));  不能确定发送时间的格式
                                            s.setType(0);
                                            s.setSend(object.getReceived());
                                            s.setReceived(object.getSend());
                                            s.setSendStatus(1);
                                            sms.add(s);
                                            shortMessageService.saveShortMessage(sms);
                                            sendOKFlag = true;
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}