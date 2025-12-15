package com.wellsoft.pt.message.support;

import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * 用于生成手机短信PDU编码
 *
 * @author 范华鹏
 * @version 1.0
 * @since JDK 1.4.2
 */
public class PduPack {
    /**
     * 英文7BIT编码
     */
    public static final String MSGCODING_ENGLISH = "00";
    /**
     * 中文8BIT编码
     */
    public static final String MSGCODING_CHINESE = "08";
    static Logger logger = Logger.getLogger(PduPack.class);
    // 短消息中心号长度
    private static String smscLen = "08";
    private static String smscFormat = "91";
    private static int msgIndicator = 1;
    // 源地址长度
    private static int addrLen = 13;
    private static String addrFormat = "91";
    // 源地址
    private static String addr;
    // 短消息内容编码方式,tp_dcs
    private static String msgCoding;
    String m_pdu = "0003000101";
    int max = 0;
    int no = 0;
    int refno = 0;

    // 时间戳,tp_scts
    // private String timestamp;
    private String msg_sign = "";
    private int msg_total = 0;
    private int msg_index = 0;
    // 短消息中心号
    private String smsc;
    private int msgLen;
    private String msgTime = null;
    // 短消息内容,tp_ud
    private String msgContent;

    /**
     * 构建对象
     */
    public PduPack() {
        smscLen = "08";
        smscFormat = "91";
        addrLen = 13;
        addrFormat = "91";
        msgTime = null;

    }

    /**
     * 对PDU数据进行解码
     *
     * @param src 源数据
     * @param b   true为发送时的信息，false为接收时的数据
     */
    public PduPack(String src, boolean b) {

        int m = 0, n = 16;
        if (b) {
            m = 2;
            n = 4;
        }
        msg_sign = "";
        msg_total = 0;
        msg_index = 0;

        if (src != null && src.length() > 44) {
            String temp = src.substring(4, 18);
            smsc = GFString.interChange(temp);
            if (smsc != null && smsc.length() > 1) {
                smsc = smsc.substring(0, smsc.length() - 1);
                if (smsc.length() == 13)
                    smsc = smsc.substring(2);
            }

            temp = src.substring(20 + m, 22 + m);

            int addrLen = Integer.parseInt(temp, 16);

            if (addrLen % 2 == 0)
                temp = src.substring(24 + m, 24 + m + addrLen);
            else
                temp = src.substring(24 + m, 24 + m + addrLen + 1);

            addr = GFString.interChange(temp);

            // 去掉为补齐为偶数加上的那一位
            if (addr != null && addr.length() % 2 == 0) {
                addr = addr.substring(0, addr.length() - 1);
                if (addr.length() == 13)// 如果前面有86，去掉它
                    addr = addr.substring(2);
            }
            if (addrLen % 2 == 0) {
                msgCoding = src.substring(24 + m + addrLen + 2, 24 + m + addrLen + 4);

                if (!b && 24 + addrLen + 4 + 16 < src.length()) {// get time
                    String ttemp = src.substring(24 + addrLen + 4, 24 + addrLen + 4 + 14);
                    ttemp = GFString.interChange(ttemp);
                    msgTime = "20" + ttemp.substring(0, 2) + "-" + ttemp.substring(2, 4) + "-" + ttemp.substring(4, 6)
                            + " " + ttemp.substring(6, 8) + ":" + ttemp.substring(8, 10) + ":"
                            + ttemp.substring(10, 12);

                    temp = src.substring(24 + addrLen + 4 + 16);
                }

                temp = src.substring(24 + m + addrLen + 4 + n);
            } else {
                msgCoding = src.substring(24 + m + addrLen + 3, 24 + m + addrLen + 5);
                if (!b && 24 + addrLen + 5 + 16 < src.length()) {
                    String ttemp = src.substring(24 + addrLen + 5, 24 + addrLen + 5 + 14);
                    ttemp = GFString.interChange(ttemp);
                    msgTime = "20" + ttemp.substring(0, 2) + "-" + ttemp.substring(2, 4) + "-" + ttemp.substring(4, 6)
                            + " " + ttemp.substring(6, 8) + ":" + ttemp.substring(8, 10) + ":"
                            + ttemp.substring(10, 12);
                }
                temp = src.substring(24 + m + addrLen + 5 + n);
            }

            if (msgCoding.equals(MSGCODING_CHINESE))
                msgContent = GFString.unicode2gb(temp.trim());
            else if (msgCoding.equals("04"))
                msgContent = GFString.decode8bit(temp.trim());
            else {
                String con = GFString.decode7bit(temp.trim());
                byte[] conb = con.getBytes();
                ByteBuffer hb = ByteBuffer.wrap(conb);
                CharBuffer cb = null;
                try {
                    Charset cs = Charset.forName("CCGSM");
                    CharsetDecoder cd = cs.newDecoder();
                    cb = cd.decode(hb);
                } catch (CharacterCodingException ae) {
                    logger.error(ae.getMessage(), ae);
                }
                msgContent = cb.toString();
            }

            if (temp.substring(0, 4).equals("0500") || temp.substring(0, 4).equals("0608")) {
                String datahead = temp.substring(0, 2);
                int datahead_long = Integer.parseInt(datahead, 16) + 1;
                String head = temp.substring(0, datahead_long * 2);
                if (datahead_long == 6) {
                    msg_sign = head.substring(6, 8);
                    msg_total = Integer.parseInt(head.substring(8, 10), 16);
                    msg_index = Integer.parseInt(head.substring(10, 12), 16);
                } else if (datahead_long == 7) {
                    msg_sign = head.substring(6, 10);
                    msg_total = Integer.parseInt(head.substring(10, 12), 16);
                    msg_index = Integer.parseInt(head.substring(12, 14), 16);
                }

                if (msgCoding.equals(MSGCODING_CHINESE))
                    msgContent = msgContent.substring(datahead_long / 2);
                else
                    msgContent = msgContent.substring((datahead_long + 1));
            }
        }
    }

    /**
     * 对短信中心号码进行PDU编码（手机号码自动补全并进行换位，补位）
     *
     * @param s 短信中心号码
     * @return PDU编码后的短信中心号码
     */
    public static String getSmscPdu(String s) {
        String centerNo = null;
        if (s != null && !s.equals("")) {
            if (s.length() == 11 && s.substring(0, 2).equals("13")) {
                centerNo = "86" + s;
            } else if (s.length() == 13 && s.substring(0, 4).equals("8613")) {
                centerNo = s;
            } else if (s.length() == 14 && s.substring(0, 5).equals("+8613")) {
                centerNo = s.substring(1);
            }
        }
        return GFString.interChange(centerNo);
    }

    /**
     * 对手机号码进行PDU编码（手机号码自动补全并进行换位，补位）
     *
     * @param ad 手机号码
     * @return PDU编码后的手机号码
     */
    public static String getPhonePdu(String ad) {
        String centerNo = "";
        addrFormat = "91";
        addrLen = 13;
        if (ad != null) {
            if (ad.length() == 11 && ad.substring(0, 2).equals("13")) {
                centerNo = "86" + ad;
            } else if (ad.length() == 13 && ad.substring(0, 4).equals("8613")) {
                centerNo = ad;
            } else if (ad.length() == 14 && ad.substring(0, 5).equals("+8613")) {
                centerNo = ad.substring(1);
            } else if (ad.length() > 0) {// 特服号
                addrFormat = "A1";
                addrLen = ad.length();
                centerNo = ad;
            }
            centerNo = GFString.interChange(centerNo);
        }
        return centerNo;
    }

    /**
     * 对短信消息体进行PDU编码
     *
     * @param content      短信消息体
     * @param concacencted 是否支持长短信
     * @return PDU编码后的字符串
     */
    public static List<String> getMsgPdu(String content, boolean concacencted) {
        List<String> v = new ArrayList<String>();
        if (content != null) {
            if (msgCoding == MSGCODING_ENGLISH) {
                v = GFString.stringToHexString(content, concacencted);
            } else {
                v = GFString.gb2unicode(content, concacencted);
            }
        }
        return v;
    }

    /**
     * 获取手机短信的PDU编码，当生成多条短信时，自动拆分 纯英文超长短信的消息长度计算时对消息体需进行移位，再将数据头加上计算
     *
     * @param smsc         信息中心号码
     * @param mp           接收方手机号码
     * @param msg          短消息内容
     * @param concacencted 是否支持超长短信
     * @return PDU编码后的对象
     */
    public static List<String> getPdu(String smsc, String mp, String msg, boolean concacencted) {
        List<String> result = new ArrayList<String>();

        if (smsc != null && mp != null && msg != null) {
            if (GFString.isContainsChineseChar(msg)) {
                msgCoding = MSGCODING_CHINESE;
            } else {
                msgCoding = MSGCODING_ENGLISH;
            }

            List<String> msg_pdu = getMsgPdu(msg, true);
            int l = msg_pdu.size();
            String tmp = "";
            String head_pdu = "";
            String pdu_len = "";
            int indicator = getNextMsgIndicator();
            for (int i = 0; i < l; i++) {
                tmp = (String) msg_pdu.get(i);
                logger.debug("Tmp：" + tmp);
                if (l > 1 && concacencted) {
                    head_pdu = getDataHeader(l, i + 1, indicator);
                    if (msgCoding == MSGCODING_ENGLISH)
                        tmp = offsetHexString(tmp, 1);
                    tmp = head_pdu + tmp;
                    concacencted = true;
                } else
                    concacencted = false;

                pdu_len = getPduHexLength(tmp, concacencted);
                result.add(getSMSHeader(smsc, mp, concacencted) + pdu_len + tmp);
            }
        } else {
            System.out.println("参数错误。");
        }
        return result;
    }

    /**
     * 生成PDU短信的报头
     *
     * @param sc           信息中心号码
     * @param mp           接收方手机号码
     * @param concacencted 是否支持超长短信
     * @return PDU短信的报头
     */
    public static String getSMSHeader(String sc, String mp, boolean concacencted) {
        String tp_mti = "11";
        final String tp_mr = "00";
        final String tp_pid = "00";
        final String tp_vp = "00";
        if (concacencted)
            tp_mti = "51";

        String mp_pdu = getPhonePdu(mp);
        return smscLen + smscFormat + getSmscPdu(sc) + tp_mti + tp_mr + GFString.byte2hex((byte) addrLen) + addrFormat
                + mp_pdu + tp_pid + msgCoding + tp_vp;
    }

    private static int getNextMsgIndicator() {
        if (msgIndicator > 253)
            msgIndicator = 0;
        return ++msgIndicator;
    }

    /**
     * 生成超长短信的消息数据部分的报头
     *
     * @param max   短信拆分条数
     * @param no    但前为第几条
     * @param refno 该笔短信的短信的唯一标示（并不一定要唯一）
     * @return 短信数据的报头
     */
    public static String getDataHeader(int max, int no, int refno) {
        String m_pdu = "0003";
        m_pdu = m_pdu + intToHexString(refno);
        m_pdu = m_pdu + intToHexString(max);
        m_pdu = m_pdu + intToHexString(no);
        m_pdu = intToHexString(m_pdu.length() / 2) + m_pdu;
        return m_pdu;
    }

    /**
     * 主函数，用于测试
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
			/*			PduPack p = new PduPack();

						String a = "爱死你了让我如何凑够七十个字呢？好多啊，怎么打啊，靠打字会很累的耶，能不能"
								+ "用拷贝复制的啊那样的话会快很多的啊，我到底打了多少个字了？够";

						String smsc = "+8613800592500";
						String mp = "+8613559223329";

						String u = "0891683108502905F011000D91683155293223F90008008672316B7B4F604E868BA9621"
								+ "159824F5551D1591F4E0353414E2A5B575462FF1F597D591A554AFF0C600E4E486253554AF"
								+ "F0C976062535B574F1A5F887D2F76848036FF0C80FD4E0D80FD752862F78D1D590D523676"
								+ "84554A90A3683776848BDD4F1A5FEB5F88591A7684554AFF0C621152305E9562534E86591"
								+ "A5C114E2A5B574E86FF1F591F";

						System.out
								.println("---------------------------------------------------");
						String text = "Welcome to BEA, a leading provider of enterprise infrastructure "
								+ "software for reducing IT complexity and successfully deploying "
								+ "Service-Oriented Architecture (SOA). Our products include BEA WebLogic "
								+ "Server, the world's leading application server,";

						String text11 = "I love you not because of who you are, but because of who I am when"
								+ " I am with youI love you not because of who you are, but because of who I "
								+ "am when I am with youI love you not because of who you are, but because "
								+ "of who I am when I am with you";

						String text3 = "[Welcome to BEA]";

						String text4 = "Welcome to BEA, a leading provider of enterprise infrastructure software for reducing IT complexity and successfully deploying Service-Oriented Architec";

						String text2 = "爱死你了让我如何凑够七十个字呢？好多啊，怎么打啊，靠打字会很累的耶，能不能用拷贝复制的啊"
								+ "那样的话会快很多的啊，我到底打了多少个字了？够不够啊？可能不够，再多打几个吧，现在应该够了 ，打出字数来"
								+ "看下吧。";

						String text5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHI" +
						"JKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ" +
						"ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ";


						String pd = "AE65F6F8DD2E83E86F90B0186481C22076394C4EBBCF20B8FC6D4F93CB72D0DB0C2ABBE965395" +
								"C9E9E974169B7591E9ED3E5F531BD2E2F83E66F33FD1E969741E6B71C242F93EBE3B4FB0C4A5241E3" +
								"771BCE2EE3D3F43C28EC2683E6F5F1B83C9F9BEB6C761E442EC3D9EF7CDA7D064DCB727B7A5C6E3DE" +
								"5E9B29B5E268382F2313A4D2F8F01";

						String lll = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
						System.out.println(text5.length());
						Vector v = getPdu(smsc, mp, text5, true);
						for (int i = 0; i < v.size(); i++) {
							String coded = ((String) v.get(i)).toUpperCase();
							System.out.println(coded);
						}


						String c1 = "41E19058341E9149E592D9743EA151E9945AB55EB1596D503824168D476452B964369D4F68543AA556AD576C561B140E8945E31199542E994DE7131A954EA955EB159BD506854362D1784426954B66D3F98446A5536AD57AC566B541E19058341E9149E592D9743EA151E9945AB55EB1596D503824168D476452B964369D4F68543AA556AD";
						String c2 = "41E19058341E9149E592D9743EA151E9945AB55EB1596D503824168D476452B964369D4F68543AA556AD576C561B140E8945E31199542E994DE7131A954EA955EB159BD506854362D1784426954B66D3F98446A5536AD57AC566B541E19058341E9149E592D9743EA151E9945AB55EB1596D503824168D476452B964369D4F68543AA556AD57";

						System.out.println(offsetHexString(c1, 1).toUpperCase());
						System.out.println(offsetHexString(c2, 1).toUpperCase());*/

			/*


			String n1 = "0891683108502905F0240D91683155293223F900008030716192952" +
					"39941E19058341E9149E592D9743EA151E9945AB55EB1596D503824168D4764" +
					"52B964369D4F68543AA556AD576C561B140E8945E31199542E994DE7131A95" +
					"4EA955EB159BD506854362D1784426954B66D3F98446A5536AD57AC566B541E1" +
					"9058341E9149E592D9743EA151E9945AB55EB1596D503824168D476452B964369D" +
					"4F68543AA556AD57";

			String n2 = "0891683108502905F0240D91683155293223F90000803071612032231A41E19058341E9149E592D9743EA151E9945AB55EB1592D";
			PduPack pd2 = new PduPack(n1, false);
			System.out.println(pd2);

			String l1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVW";
			System.out.println(pd2.getMsgContent());
			System.out.println(l1);
			*/
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 将十六进制字符串转换为byte数组
     *
     * @param pdu
     * @return 十六进制字符串转换后得到的byte数组
     */
    public static byte[] hexStringToBytes(String pdu) {
        byte oldBytes[] = new byte[pdu.length() / 2];
        for (int i = 0; i < pdu.length() / 2; i++) {
            oldBytes[i] = (byte) (Integer.parseInt(pdu.substring(i * 2, i * 2 + 1), 16) * 16);
            oldBytes[i] += (byte) Integer.parseInt(pdu.substring(i * 2 + 1, i * 2 + 2), 16);
        }
        return oldBytes;
    }

    /**
     * 获取十六进制字符串进行数据偏移后的十六进制字符串
     *
     * @param pdu          源字符串
     * @param offsetNumber 偏移位数
     * @return 处理后的字符串
     */
    public static String offsetHexString(String pdu, int offsetNumber) {
        byte oldBytes[] = hexStringToBytes(pdu);
        BitSet bitSet = new BitSet(pdu.length() * 8 + offsetNumber);
        int value1 = 0 + offsetNumber;
        for (int i = 0; i < pdu.length() / 2; i++) {
            for (int j = 0; j < 8; j++) {
                value1 = i * 8 + j + offsetNumber;
                if ((oldBytes[i] & 1 << j) != 0)
                    bitSet.set(value1);
            }

        }

        int value2 = ++value1 / 8;
        if (value1 % 8 != 0)
            value2++;
        if (value2 == 0)
            value2++;
        byte newBytes[] = new byte[value2];
        for (int i = 0; i < value2; i++) {
            for (int j = 0; j < 8; j++)
                if (value1 + 1 > i * 8 + j && bitSet.get(i * 8 + j))
                    newBytes[i] |= (byte) (1 << j);

        }

        for (pdu = bytesToHexString(newBytes); pdu.length() > 0 && pdu.substring(pdu.length() - 2).equals("00"); pdu = pdu
                .substring(0, pdu.length() - 2))
            ;
        return pdu;
    }

    /**
     * byte数组转换为十六进制字符串
     *
     * @param bys
     * @return byte数组转换后的十六进制字符串
     */
    public static String bytesToHexString(byte bys[]) {
        int len = bys.length;
        String pdu = "";
        for (int i = 0; i < len; i++) {
            String str1 = Integer.toHexString(bys[i]);
            if (str1.length() != 2)
                str1 = "0" + str1;
            str1 = str1.substring(str1.length() - 2, str1.length());
            pdu = pdu + str1;
        }

        return pdu;
    }

    /**
     * 获取PDU编码的数据长度
     *
     * @param pdu
     * @return PDU编码长度值转换为十六进制后的字符串
     */
    public static String getPduHexLength(String pdu, boolean b) {
        if (msgCoding == MSGCODING_ENGLISH) {
            int len = pdu.length() / 2;
            len *= 8;
            int l = len / 7;
            return intToHexString(l);
        } else {
            return intToHexString(pdu.length() / 2);
        }
    }

    /**
     * 数值转换为长度为2的十六进制字符串，不足的左补0
     *
     * @param len
     * @return 转换后得到的字符串
     */
    public static String intToHexString(int len) {
        String str1 = Integer.toHexString(len);
        if (str1.length() < 2)
            str1 = "0" + str1;
        return str1;
    }

    /**
     * 获取手机号
     *
     * @return 手机号
     */
    public String getAddr() {
        return addr;
    }

    /**
     * 获取时间
     *
     * @return 时间
     */
    public String getTime() {
        return msgTime;
    }

    /**
     * 获取编码方式
     *
     * @return 编码方式，“00”为7BIT，“08”为8BIT
     */
    public String getMsgCoding() {
        return msgCoding;
    }

    /**
     * 设置编码方式
     *
     * @param encoding 0:表示7-BIT编码 4:表示8-BIT编码 8:表示UCS2编码
     */
    public static void setMsgCoding(int encoding) {
        if (encoding == 8)
            msgCoding = MSGCODING_CHINESE;
        else if (encoding == 4)
            msgCoding = "04";
        else
            msgCoding = MSGCODING_ENGLISH;

    }

    /**
     * 获取短信内容
     *
     * @return 短信内容
     */
    public String getMsgContent() {
        return msgContent;
    }

    /**
     * 获取短信PDU数据的长度
     *
     * @return 短信PDU数据的长度长度
     */
    public int getMsgLen() {
        return msgLen;
    }

    /**
     * 获取短信中心号码
     *
     * @return 短信中心号码
     */
    public String setSmsc() {
        return smsc;
    }

    /**
     * 获取该条短信的唯一标识
     *
     * @return 短信标识
     */
    public String getMsgSign() {
        return msg_sign;
    }

    /**
     * 获取短信发送的总数量
     *
     * @return 该条短信的总数量
     */
    public int getMsgTotal() {
        return msg_total;
    }

    /**
     * 获取当前短信的序号
     *
     * @return 该条短信在整条短信的位置
     */
    public int getMsgIndex() {
        return msg_index;
    }

    public String toString() {
        String str = "** GSM MESSAGE **\n";
        str = str + "  SMSC: " + smsc + "\n";
        str = str + "  Sign: " + msg_sign + "\n";
        str = str + "  Total: " + msg_total + "\n";
        str = str + "  Index: " + msg_index + "\n";
        str = str + "  Originator: " + addr + "\n";
        str = str + "  Date: " + msgTime + "\n";
        str = str + "  Text: 【" + msgContent + "】\n";
        str = str + "***\n";
        return str;
    }

}