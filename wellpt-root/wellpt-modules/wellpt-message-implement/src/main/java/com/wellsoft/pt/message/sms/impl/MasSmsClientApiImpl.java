package com.wellsoft.pt.message.sms.impl;

import com.jasson.im.api.APIClient;
import com.jasson.im.api.MOItem;
import com.jasson.im.api.RPTItem;
import com.wellsoft.pt.message.sms.MoType;
import com.wellsoft.pt.message.sms.RptType;
import com.wellsoft.pt.message.sms.SmsClientApi;

import java.util.ArrayList;
import java.util.List;

public class MasSmsClientApiImpl extends SmsClientApi {

    public MasSmsClientApiImpl() {
    }

    public MasSmsClientApiImpl(String service, String apiCode, String username, String password, String dbname) {
        super(service, apiCode, username, password, dbname, "");
    }

    @Override
    public int sendSM(String[] mobiles, String content, long smID, long srcID, String url, String sendTime) {
        APIClient apiClient = null;
        int sendResult = IM_SEND_FAIL;
        try {
            apiClient = new APIClient();
            if (apiClient.init(service, username, password, apiCode, dbname) == APIClient.IMAPI_SUCC) {
                sendResult = apiClient.sendSM(mobiles, content, smID, srcID, url, sendTime) == 0 ? IM_SEND_SUCC
                        : IM_SEND_FAIL;
            }
        } finally {
            if (apiClient != null)
                apiClient.release();
        }
        return sendResult;
    }

    @Override
    public MoType[] receiveSM(long srcID, int amount) {
        APIClient apiClient = null;
        List<MoType> moResult = new ArrayList<MoType>();
        try {
            apiClient = new APIClient();
            if (apiClient.init(service, username, password, apiCode, dbname) == APIClient.IMAPI_SUCC) {
                MOItem[] rpts = apiClient.receiveSM(srcID, amount);
                for (int i = 0; i < rpts.length; i++) {
                    moResult.add(new MasMO(rpts[i]));
                }
            }
        } finally {
            if (apiClient != null)
                apiClient.release();
        }
        MoType[] moType = new MoType[0];
        return moResult.toArray(moType);
    }

    @Override
    public RptType[] receiveRPT(long smID, int amount) {
        APIClient apiClient = null;
        List<RptType> rptResult = new ArrayList<RptType>();
        try {
            apiClient = new APIClient();
            if (apiClient.init(service, username, password, apiCode, dbname) == APIClient.IMAPI_SUCC) {
                RPTItem[] rpts = apiClient.receiveRPT(smID, amount);
                for (int i = 0; i < rpts.length; i++) {
                    rptResult.add(new MasRpt(rpts[i]));
                }
            }
        } finally {
            if (apiClient != null)
                apiClient.release();
        }
        RptType[] rptType = new RptType[0];
        return rptResult.toArray(rptType);
    }

    @Override
    public int getType() {
        return MSG_TYPE_MAS;
    }
}
