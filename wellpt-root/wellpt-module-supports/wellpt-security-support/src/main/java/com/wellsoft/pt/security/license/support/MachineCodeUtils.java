/*
 * @(#)2019年5月15日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.license.support;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月15日.1	zhulh		2019年5月15日		Create
 * </pre>
 * @date 2019年5月15日
 */
public class MachineCodeUtils {

    private static String MACHINE_CODE;

    /**
     * @return
     * @throws Exception
     */
    public static String getMachineCode() throws Exception {
        if (StringUtils.isBlank(MACHINE_CODE)) {
            MACHINE_CODE = generateMachineCode();
        }
        return MACHINE_CODE;
    }

    /**
     * @return
     */
    private static String generateMachineCode() throws Exception {
        Set<String> result = new LinkedHashSet<String>();
        Set<String> macAddresses = getMacAddresses();
        result.addAll(macAddresses);
        Properties props = System.getProperties();
        String javaVersion = props.getProperty("java.version");
        result.add(javaVersion);
        String osName = props.getProperty("os.name");
        result.add(osName);
        String osArch = props.getProperty("os.arch");
        result.add(osArch);
        String osVersion = props.getProperty("os.version");
        result.add(osVersion);
        String userName = props.getProperty("user.name");
        result.add(userName);
        return insertSplitString(DigestUtils.md5Hex(result.toString()), "-", 4);
    }

    /**
     * @param string
     * @param spliter
     * @param interval
     * @return
     */
    private static String insertSplitString(String string, String spliter, int interval) {
        StringBuilder sb = new StringBuilder();
        int len = string.length();
        for (int index = 0; index < len; index++) {
            sb.append(string.charAt(index));
            if ((index + 1) != len && (index + 1) % interval == 0) {
                sb.append(spliter);
            }
        }
        return sb.toString();
    }

    /**
     * @return
     * @throws Exception
     */
    private static Set<String> getMacAddresses() throws Exception {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        StringBuilder sb = new StringBuilder();
        Set<String> macAddressSet = new LinkedHashSet<String>();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                InetAddress ip = interfaceAddress.getAddress();
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                if (network == null) {
                    continue;
                }
                byte[] mac = network.getHardwareAddress();
                if (mac == null) {
                    continue;
                }
                sb.delete(0, sb.length());
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                }
                String macAddress = sb.toString();
                if (StringUtils.isBlank(macAddress)) {
                    continue;
                }
                macAddressSet.add(macAddress);
            }
        }
        return macAddressSet;
    }

}
