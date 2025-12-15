package com.wellsoft.pt.message.sms.skmas;

import com.wellsoft.pt.message.sms.SMasAbstractClient;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 尚科S-MAS客户端
 *
 * @author FashionSUN
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-4.1	FashionSUN		2015-2-4		Create
 * </pre>
 * @date 2015-2-4
 */
@Component
public class SKMasClient extends SMasAbstractClient<Sms, Rpt> {

    public static final String IM_RTP_FAIL = "SUCCESS";
    public static final String IM_RTP_SUCC = "ERROR";
    /**
     * sMas状态与解释
     */
    public static Map<Integer, String> sMasStatusKeyValue = new HashMap<Integer, String>();

    static {
        sMasStatusKeyValue.put(-100, "短信提交成功");
        sMasStatusKeyValue.put(-101, "接口账号不能为空");
        sMasStatusKeyValue.put(-102, "接口密码不能为空");
        sMasStatusKeyValue.put(-103, "接口授权验证不通过");
        sMasStatusKeyValue.put(-104, "接口没有发送权限");
        sMasStatusKeyValue.put(-105, "短信内容不能为空");
        sMasStatusKeyValue.put(-106, "短信字数不能超过 n 个");
        sMasStatusKeyValue.put(-107, "手机号码不正确");
        sMasStatusKeyValue.put(-108, "部分手机号码不正确");
        sMasStatusKeyValue.put(-109, "数据库插入错误");
        sMasStatusKeyValue.put(-110, "上行短信接收成功");
        sMasStatusKeyValue.put(-111, "接口没有接收权限");
        sMasStatusKeyValue.put(-120, "短信回执接收成功");
        sMasStatusKeyValue.put(-121, "接口没有接收回执权限");

        sMasStatusKeyValue.put(-500, "目标网络不通^_^发送失败");
    }

    private SmsWebServiceService smsWebServiceService = null;

    @Override
    public int init(String sWebService, String apiCode, String username, String password) {
        /* 可以在这里初始化WebService */
        return super.init(sWebService, apiCode, username, password);
    }

    @Override
    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url, String sendTime) {
        logger.info("pre ksmas send mobiles : " + mobiles + " content : " + content + " smID : " + smID + " srcID : "
                + srcID + " url : " + url + " sendTime : " + sendTime);
        if (username == null) {
            return -101;
        }
        if (password == null) {
            return -102;
        }
        if (sWebService == null) {
            return -103;
        }
        if ((mobiles == null) || (mobiles.length == 0)) {
            return -107;
        }
        StringBuffer mobileBuf = new StringBuffer();
        for (int i = 0; i < mobiles.length; ++i) {
            mobileBuf.append(",").append(mobiles[i]);
        }
        if (mobileBuf.length() > 1)
            mobileBuf.delete(0, 1);
        else {
            return -107;
        }
        String contenttmp = replaceSpecilAlhpa(content);
        if (contenttmp.length() < 1) {
            return -105;
        }
        if ((!(checkSmID(smID))) || (!(checkSmID(srcID)))) {
            return -105;
        }
        int ret = preContect();
        if (ret != IM_CONN_SUCC) {
            return ret;
        }
        if (sendTime == null || sendTime.equalsIgnoreCase("null")) {
            sendTime = "";
        }
        SmsWebService smsWebService = smsWebServiceService.getSmsWebServicePort();
        logger.info("ksmas sending username : " + username + " password : " + password + " mobileBuf : "
                + obj2str(mobileBuf) + " content : " + content + " smID : " + long2str(smID) + " sendTime : "
                + sendTime);
        MtResult mtResult = smsWebService.sendMt(username, password, obj2str(mobileBuf), content, long2str(smID),
                sendTime);
        logger.info("ksmas sended result returnMtCount : " + mtResult.getReturnMtCount() + " returnStateCode : "
                + mtResult.getReturnStateCode() + " returnStateIntro : " + mtResult.getReturnStateIntro());
        returnStateCode = mtResult.getReturnStateCode();
        return Integer.valueOf(mtResult.getReturnMtCount());
    }

    @Override
    public Sms[] receiveSM(long srcID, int amount) {
        logger.info("receiveSM(long srcID={}, int amount={}) - start", srcID, amount);
        if (username == null) {
            return null;
        }
        if (password == null) {
            return null;
        }
        if (sWebService == null) {
            return null;
        }
        int ret = preContect();
        if (ret != IM_CONN_SUCC) {
            return null;
        }
        SmsWebService smsWebService = smsWebServiceService.getSmsWebServicePort();
        logger.info("ksmas receiveSM username : " + username + " password : " + password + " amount : " + amount);
        MoResult moResulttmp = smsWebService.recvMo(username, password, String.valueOf(amount));
        logger.info("ksmas receiveSM result returnMoCount : " + moResulttmp.getReturnMoCount() + " returnStateCode : "
                + moResulttmp.getReturnStateCode() + " returnStateIntro : " + moResulttmp.getReturnStateIntro());
        returnStateCode = moResulttmp.getReturnStateCode();
        List<Sms> moList = moResulttmp.getReturnMoList();
        Sms[] moType = new Sms[0];
        Sms[] returnSmsArray = moList.toArray(moType);
        logger.info("receiveSM(long, int) - end - return value={}", returnSmsArray);
        return returnSmsArray;
    }

    @Override
    public Rpt[] receiveRPT(long smID, int amount) {
        logger.info("receiveRPT(long smID={}, int amount={}) - start", smID, amount);
        if (username == null) {
            return null;
        }
        if (password == null) {
            return null;
        }
        if (sWebService == null) {
            return null;
        }
        int ret = preContect();
        if (ret != IM_CONN_SUCC) {
            return null;
        }
        SmsWebService smsWebService = smsWebServiceService.getSmsWebServicePort();
        logger.info("ksmas receiveRPT username : " + username + " password : " + password + " amount : " + amount);
        RptResult moResulttmp = smsWebService.recvRpt(username, password, String.valueOf(amount));
        logger.info("ksmas receiveRPT result returnRptCount : " + moResulttmp.getReturnRptCount()
                + " returnStateCode : " + moResulttmp.getReturnStateCode() + " returnStateIntro : "
                + moResulttmp.getReturnStateIntro());
        returnStateCode = moResulttmp.getReturnStateCode();
        List<Rpt> rptList = moResulttmp.getReturnRptList();
        Rpt[] rptType = new Rpt[0];
        Rpt[] returnRptArray = rptList.toArray(rptType);
        logger.info("receiveRPT(long, int) - end - return value={}", returnRptArray);
        return returnRptArray;
    }

    @Override
    protected int preContect() {
        int rtn = IM_CONN_SUCC;
        if (smsWebServiceService == null) {
            URL wsdlLocation;
            try {
                wsdlLocation = new URL(sWebService);
                smsWebServiceService = new SmsWebServiceService(wsdlLocation);
            } catch (MalformedURLException ex) {
                rtn = IM_CONN_FAIL;
                logger.debug("preContect : {}", ex);
                smsWebServiceService = new SmsWebServiceService();
            }
        }
        return rtn;
    }

    @Override
    public String getName() {
        return "尚科S-MAS客户端";
    }
}
