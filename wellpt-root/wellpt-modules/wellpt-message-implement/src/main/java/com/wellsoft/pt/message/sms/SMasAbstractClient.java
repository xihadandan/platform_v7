package com.wellsoft.pt.message.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Description: WEB SERVICE短信机抽象接口(封装初始化方法)
 *
 * @param <MoType>
 * @param <RptType>
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-4.1	zhongzh		2015-2-4		Create
 * </pre>
 * @date 2015-2-4
 */
public abstract class SMasAbstractClient<moType, rptType> {

    public static final int IM_CONN_SUCC = 0;
    public static final int IM_CONN_FAIL = -1;
    public static final int IM_SEND_SUCC = 0;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * service地址 sWebService
     */
    protected String sWebService;
    /**
     * 接口名称 apiCode
     */
    protected String apiCode;
    /**
     * 用户名 username
     */
    protected String username;
    /**
     * 密码 password
     */
    protected String password;

    protected String returnStateCode;

    /**
     * 初始化Client,可以在这里初始化WebService
     *
     * @param sWebService
     * @param apiCode
     * @param username
     * @param password
     * @return
     */
    public int init(String sWebService, String apiCode, String username, String password) {
        logger.info("init(String sWebService={}, String apiCode={}, String username={}, String password={}) - start",
                new Object[]{sWebService, apiCode, username, password});
        /* 可以在这里初始化WebService */
        this.sWebService = sWebService;
        this.apiCode = apiCode;
        this.username = username;
        this.password = password;
        int returnint = testContect();
        logger.info("init(String, String, String, String) - end - return value={}", returnint);
        return returnint;
    }

    /**
     * webService 返回状态
     *
     * @return
     */
    public String getReturnStateCode() {
        return returnStateCode;
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
     * 默认接收100短信
     *
     * @return
     */
    public moType[] receiveSM() {
        return receiveSM(1L, 100);
    }

    /**
     * 默认接收100回执
     *
     * @return
     */
    public rptType[] receiveRPT() {
        return receiveRPT(1L, 100);
    }

    public abstract int sendSM(String[] mobiles, String content, long smID, long srcID, String url, String sendTime);

    public abstract moType[] receiveSM(long srcID, int amount);

    public abstract rptType[] receiveRPT(long smID, int amount);

    public abstract String getName();

    protected abstract int preContect();

    protected int testContect() {
        Assert.notNull(sWebService, "testContect param sWebService is not null");
        URL url;
        URLConnection urlcon;
        int rtn = IM_CONN_FAIL;
        try {
            url = new URL(sWebService);
            urlcon = url.openConnection();
            urlcon.setConnectTimeout(30 * 1000);/* 30秒超时 */
            rtn = urlcon.getInputStream() == null ? IM_CONN_FAIL : IM_CONN_SUCC;
        } catch (MalformedURLException ex) {
            rtn = IM_CONN_FAIL;
            logger.debug("testContect : {}", ex);
        } catch (IOException ex) {
            rtn = IM_CONN_FAIL;
            logger.debug("testContect : {}", ex);
        } finally {
        }

        return rtn;
    }

	/*
	public boolean isConnected()
	{
	    Socket checkSocket = null;
	    try
	    {
	        checkSocket = new Socket(hostUrl, hostPort);
	        return true;
	    }
	    catch (UnknownHostException e1)
	    {
	        log.error("Unknown server: " + e1.toString());

	        return false;
	    }
	    catch (IOException e1)
	    {
	        log.error("IOException: " + e1.toString());

	        return false;
	    }
	    finally
	    {
	        try
	        {
	            // close the server check
	            if (checkSocket != null)
	            {
	                checkSocket.close();
	            }
	        }
	        catch (IOException e2)
	        {
	            // do nothing
	        }
	    }
	}
	 */

    public void release() {
        this.sWebService = null;
        this.apiCode = null;
        this.username = null;
        this.password = null;
    }

    protected String binary2Hex(byte[] bys) {
        if ((bys == null) || (bys.length < 1)) {
            return null;
        }
        StringBuffer sb = new StringBuffer(100);

        for (int i = 0; i < bys.length; ++i) {
            if (bys[i] >= 16)
                sb.append(Integer.toHexString(bys[i]));
            else if (bys[i] >= 0)
                sb.append("0" + Integer.toHexString(bys[i]));
            else {
                sb.append(Integer.toHexString(bys[i]).substring(6, 8));
            }
        }
        return sb.toString();
    }

    protected String iso2gbk(String str) {
        if (str == null) {
            return "";
        }
        String temp = "";
        try {
            byte[] buf = str.trim().getBytes("iso8859-1");
            temp = new String(buf, "GBK");
        } catch (UnsupportedEncodingException e) {
            temp = str;
        }
        return temp;
    }

    protected String replaceSpecilAlhpa(String content) {
        if ((content == null) || (content.trim().length() == 0)) {
            return "";
        }
        String spec_char = "\\'";
        String retStr = "";
        for (int i = 0; i < content.length(); ++i) {
            if (spec_char.indexOf(content.charAt(i)) >= 0) {
                retStr = retStr + "\\";
            }
            retStr = retStr + content.charAt(i);
        }
        return retStr;
    }

    protected boolean checkSmID(long smID) {
        return ((smID >= 0L) && (smID <= 99999999L));
    }

    protected String obj2str(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    protected String long2str(Long lng) {
        if (lng == null) {
            return String.valueOf(1L);
        }
        return String.valueOf(lng);
    }
}
