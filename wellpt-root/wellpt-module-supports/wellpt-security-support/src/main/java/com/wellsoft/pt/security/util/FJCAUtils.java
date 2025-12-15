/*
 * @(#)2014-1-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.util;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import fjca.FJCAApps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 福建CA证书证书工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-13.1	zhulh		2014-1-13		Create
 * </pre>
 * @date 2014-1-13
 */
public class FJCAUtils {
    public static final String KEY_LOGIN_TOKEN_ORIGIN_DATA = "FJCA_textOriginData";
    public static final String KEY_LOGIN_TOKEN_SIGN_DATA = "FJCA_textSignData";
    protected static Logger logger = LoggerFactory.getLogger(FJCAUtils.class);

    /**
     * CA验签，成功返回0，错误返回相应的错误代码
     *
     * @param textOriginData 原始数据
     * @param textSignData   签名数据
     * @param textCert       证书
     * @return
     */
    public static int verify(String textOriginData, String textSignData, String textCert) {
        FJCAApps ca = new fjca.FJCAApps();
        // 社保4000
        FJCAApps.setErrorBase(4000);
        String serverUrl = Config.getValue(UserDetailsServiceProvider.KEY_FJCA_SERVER_URL);
        FJCAApps.setServerURL(serverUrl);
        logger.info("ServerURL: " + serverUrl);

        int result = ca.FJCA_VerifyDataWithCert(textOriginData, textSignData, textCert);
        logger.info("FJCA_VerifyQY: " + result);

        int retCode = ca.getLastError();
        logger.info("getLastError: " + retCode);
        return retCode;
    }

    /**
     * CA验签，成功返回0，错误返回相应的错误代码
     *
     * @param textOriginData 原始数据
     * @param textSignData   签名数据
     * @param textCert       证书
     * @return
     */
    public static int verifyQY(String textOriginData, String textSignData, String textCert) {
        FJCAApps ca = new fjca.FJCAApps();
        // 社保4000
        FJCAApps.setErrorBase(4000);
        String serverUrl = Config.getValue(UserDetailsServiceProvider.KEY_FJCA_SERVER_URL);
        FJCAApps.setServerURL(serverUrl);
        logger.info("ServerURL: " + serverUrl);

        String result = ca.FJCA_VerifyQY(textOriginData, textSignData, textCert);
        logger.info("FJCA_VerifyQY: " + result);

        int retCode = ca.getLastError();
        logger.info("getLastError: " + retCode);
        return retCode;
    }

    /**
     * 根据错误代码返回错误信息
     *
     * @param retCode
     * @return
     */
    public static String getErrorData(int retCode) {
        String style;
        switch (retCode) {
            case 0:
                style = "证书验证成功!";
                break;
            case 1:
                style = "需要使用福建CA数字证书登录!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 9:
                style = "该证书中的单位在系统中不存在!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;

            case 4001:
                style = "内存分配失败或数据格式错误!错误代码为：4001!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201。";
                break;
            case 4002:
                style = "数据格式错误!错误代码为：4002!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4003:
                style = "访问数据库时，发生系统错误!<br>错误代码为：4003!如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4004:
                style = "通讯数据传输错误!错误代码为：4004!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4005:
                style = "无效参数!错误代码为：4005!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4006:
                style = "用户文件错误!错误代码为：4006!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4007:
                style = "证书错误!错误代码为：4007!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;

            case 4011:
                style = "您的数字证书非福建CA签发的数字证书!错误代码为：4011!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4012:
                style = "您的数字证书已过期，请及时更新!请检查您的数字证书是否已过期，" + "<br>或者客户本机电脑时间是否正确。错误代码为：4012!"
                        + "<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4013:
                style = "您的数字证书已被注销!错误代码为：4013!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4014:
                style = "您的数字证书暂时未开通或者尚未缴纳证书服务费!错误代码为：4014!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4015:
                style = "您的数字证书暂时未开通或者证书信息正在审核整理中!错误代码为：4015!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4016:
                style = "您的数字证书网上服务期限已到，请您及时缴费!错误代码为：4016!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4021:
                style = "验证签名失败，不是证书对应的KEY签名的!错误代码为：4021!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4022:
                style = "签名的证书与认证的证书不是同一张!错误代码为：4022!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4023:
                style = "加密失败!错误代码为：4023!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4024:
                style = "解密失败!错误代码为：4024!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4031:
                style = "时间戳服务器内部错误!请联错误代码为：4031!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4032:
                style = "签发时间戳失败!错误代码为：4032!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4033:
                style = "验证时间戳失败!错误代码为：4033!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4019:
                style = "需要使用福建CA数字证书登录!错误代码为：4019!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;

            case 4020:
                style = "您的数字证书网上社保服务期限已到，请您及时缴费 。" + "<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;

            case 4992:
                style = "打开证书失败.错误代码为：4992!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4993:
                style = "读证书失败.错误代码为：4993!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4994:
                style = "对数据签名失败.错误代码为：4994!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 4991:
                style = "请确认您的电脑上是否已安装“福建CA数字证书客户端软件3.1版本或以上版本，错误代码为：4991!"
                        + "<br>如果您还未安装，请您点击<a href='http://www.fjca.com.cn/download/downfile.asp?n=FJCAUserTool.exe'>下 载</a>最新版本的数字证书客户端软件."
                        + "<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 9013:
                style = "证书错误!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 9016:
                style = "验证签名失败!<br>如果您无法处理，请联系CA服务电话 :(0592)2031201";
                break;
            case 9000:
                style = "请确认用户是否已经更换key,如果更换证书请重新登陆后再操作!";
                break;
            case 9999:
                style = "网络故障，获取不到证书信息,请稍后再进行操作!";
                break;
            default:
                style = "网络故障，请稍后再进行操作!";
        }

        return style;
    }
}
