package com.wellsoft.pt.message.sms.impl;

import com.wellsoft.pt.message.sms.MoType;
import com.wellsoft.pt.message.sms.RptType;
import com.wellsoft.pt.message.sms.SMasAbstractClient;
import com.wellsoft.pt.message.sms.SmsClientApi;
import com.wellsoft.pt.message.sms.skmas.Rpt;
import com.wellsoft.pt.message.sms.skmas.Sms;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SmasSmsClientApiImpl extends SmsClientApi {


    public SmasSmsClientApiImpl() {
    }

    public SmasSmsClientApiImpl(String service, String apiCode, String username, String password, String dbname,
                                String clientBean) {
        super(service, apiCode, username, password, dbname, clientBean);
    }

    @Override
    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url, String sendTime) {
        logger.info(
                "sendSM(String[] mobiles={}, String content={}, long smID={}, long srcID={}, String url={}, String sendTime={}) - start",
                new Object[]{mobiles, content, smID, srcID, url, sendTime});
        SMasAbstractClient<Sms, Rpt> aMasClient = getServiceClient();//new SKMasClient();
        int sendResult = IM_SEND_FAIL;
        if (aMasClient.init(service, apiCode, username, password) == SMasAbstractClient.IM_CONN_SUCC) {
            sendResult = aMasClient.sendSM(mobiles, content, smID, srcID, url, sendTime) == mobiles.length ? IM_SEND_SUCC
                    : IM_SEND_FAIL;
        }
        aMasClient.release();
        logger.info("sendSM(String[], String, long, long, String, String) - end - return value={}", sendResult);
        return sendResult;
    }

    @Override
    public MoType[] receiveSM(long srcID, int amount) {
        logger.info("receiveSM(long srcID={}, int amount={}) - start", srcID, amount);
        List<MoType> moResult = new ArrayList<MoType>();
        SMasAbstractClient<Sms, Rpt> aMasClient = getServiceClient();//new SKMasClient();
        if (aMasClient.init(service, apiCode, username, password) == SMasAbstractClient.IM_CONN_SUCC) {
            Sms[] mos = aMasClient.receiveSM(srcID, amount);
            for (int i = 0; i < mos.length; i++) {
                moResult.add(new SmasMO(mos[i]));
            }
        }
        aMasClient.release();
        MoType[] moType = new MoType[0];
        MoType[] returnMoTypeArray = moResult.toArray(moType);
        logger.info("receiveSM(long, int) - end - return value.length={}", returnMoTypeArray.length);
        return returnMoTypeArray;
    }

    @Override
    public RptType[] receiveRPT(long smID, int amount) {
        logger.info("receiveRPT(long smID={}, int amount={}) - start", smID, amount);
        List<RptType> rptResult = new ArrayList<RptType>();
        SMasAbstractClient<Sms, Rpt> aMasClient = getServiceClient();//new SKMasClient();
        if (aMasClient.init(service, apiCode, username, password) == SMasAbstractClient.IM_CONN_SUCC) {
            Rpt[] rpt = aMasClient.receiveRPT(smID, amount);
            for (int i = 0; i < rpt.length; i++) {
                rptResult.add(new SmasRpt(rpt[i]));
            }
        }
        aMasClient.release();
        RptType[] rptType = new RptType[0];
        RptType[] returnRptTypeArray = rptResult.toArray(rptType);
        logger.info("receiveRPT(long, int) - end - return value.length={}", returnRptTypeArray.length);
        return returnRptTypeArray;
    }

    @Override
    public int getType() {
        return MSG_TYPE_SMAS;
    }
}
