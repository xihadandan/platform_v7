package com.wellsoft.pt.message.sms;

import com.wellsoft.context.config.service.SystemParamsFacadeService;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.message.entity.MasConfig;
import com.wellsoft.pt.message.service.ShortMessageService;
import com.wellsoft.pt.message.sms.impl.CloudMasSmsClientApiImpl;
import com.wellsoft.pt.message.sms.impl.MasSmsClientApiImpl;
import com.wellsoft.pt.message.sms.impl.NoOpeSmsClientApiImpl;
import com.wellsoft.pt.message.sms.impl.SmasSmsClientApiImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsClientApiFactory {
    private static final Logger logger = LoggerFactory.getLogger(SmsClientApiFactory.class);

    private static final int FETCH_COUNT = 128;
    private static int countor = -FETCH_COUNT;
    private static SmsClientApi smsClientApi = null;
    private static SmsClientApi noOpeSmsClientApi = new NoOpeSmsClientApiImpl(null, null, null, null, null);

    public synchronized static void resetCountor() {
        smsClientApi = null;
        System.out.println("SmsClientApiFactory.resetCountor VOID");
    }

    public static SmsClientApi createSmsClientApi() {
        logger.info("createSmsClientApi() - start");
        if (countor > FETCH_COUNT || smsClientApi == null) {
            countor = -FETCH_COUNT;// 从-FETCH_COUNT开始计数
            MasConfig masConfig = ApplicationContextHolder.getBean(ShortMessageService.class).getBean();
            if (masConfig == null || StringUtils.isBlank(masConfig.getUuid())) {
                throw new WellException("createSmsClientApi() for masConfig is not null");
            }
            boolean isOpen = masConfig.getIsOpen() == null ? false : masConfig.getIsOpen();
            boolean sIsOpen = masConfig.getsIsOpen() == null ? false : masConfig.getsIsOpen();
            boolean cloudIsOpen = masConfig.getCloudIsOpen() == null ? false : masConfig.getCloudIsOpen();
            if (isOpen && !sIsOpen && !cloudIsOpen) {
                smsClientApi = new MasSmsClientApiImpl(masConfig.getImIp(), masConfig.getApiCode(),
                        masConfig.getLoginName(), masConfig.getLoginPassword(), masConfig.getDbName());
            } else if (sIsOpen && !isOpen && !cloudIsOpen) {
                smsClientApi = new SmasSmsClientApiImpl(masConfig.getsWebService(), masConfig.getApiCode(),
                        masConfig.getsLoginName(), masConfig.getsLoginPassword(), masConfig.getDbName(),
                        masConfig.getClientBean());
            } else if (cloudIsOpen && !isOpen && !sIsOpen) {
                smsClientApi = new CloudMasSmsClientApiImpl(masConfig.getCloudMasHttp(), masConfig.getEcName(),
                        masConfig.getApId(), masConfig.getSecretKey(), masConfig.getCloudSign(),
                        masConfig.getAddSerial());
            } else {
                smsClientApi = getSmsClientApi();
                if (smsClientApi == null) {
                    smsClientApi = noOpeSmsClientApi;
                }
            }
        } else {
            countor++;
        }
        logger.info("createSmsClientApi() - end - return value={}", smsClientApi);
        return smsClientApi;
    }

    public static SmsClientApi createSmsClientApi(MasConfig masConfig) {
        logger.info("createSmsClientApi(MasConfig masConfig={}) - start", masConfig);
        if (masConfig == null) {
            SmsClientApi returnSmsClientApi = createSmsClientApi();
            logger.debug("createSmsClientApi(MasConfig) - end - return value={}", returnSmsClientApi);
            return returnSmsClientApi;
        }
        SmsClientApi smsClientApi;
        boolean isOpen = masConfig.getIsOpen() == null ? false : masConfig.getIsOpen();
        boolean sIsOpen = masConfig.getsIsOpen() == null ? false : masConfig.getsIsOpen();
        boolean cloudIsOpen = masConfig.getCloudIsOpen() == null ? false : masConfig.getCloudIsOpen();
        if (isOpen && !sIsOpen && !cloudIsOpen) {
            smsClientApi = new MasSmsClientApiImpl(masConfig.getImIp(), masConfig.getApiCode(),
                    masConfig.getLoginName(), masConfig.getLoginPassword(), masConfig.getDbName());
        } else if (sIsOpen && !isOpen && !cloudIsOpen) {
            smsClientApi = new SmasSmsClientApiImpl(masConfig.getsWebService(), masConfig.getApiCode(),
                    masConfig.getsLoginName(), masConfig.getsLoginPassword(), masConfig.getDbName(),
                    masConfig.getClientBean());
        } else if (cloudIsOpen && !isOpen && !sIsOpen) {
            smsClientApi = new CloudMasSmsClientApiImpl(masConfig.getCloudMasHttp(), masConfig.getEcName(),
                    masConfig.getApId(), masConfig.getSecretKey(), masConfig.getCloudSign(),
                    masConfig.getAddSerial());
        } else {
            smsClientApi = getSmsClientApi();
            if (smsClientApi == null) {
                smsClientApi = noOpeSmsClientApi;
            }
        }
        logger.info("createSmsClientApi(MasConfig) - end - return value={}", smsClientApi);
        return smsClientApi;
    }

    private static SmsClientApi getSmsClientApi() {
        SystemParamsFacadeService systemParamsFacadeService = ApplicationContextHolder.getBean(SystemParamsFacadeService.class);
        String name = systemParamsFacadeService.getValue("sms.client.api.name");
        if (StringUtils.isNotBlank(name)) {
            SmsClientApi smsClientApi = ApplicationContextHolder.getBean(name, SmsClientApi.class);
            return smsClientApi;
        }
        return null;
    }
}
