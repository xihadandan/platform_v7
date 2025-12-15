package com.wellsoft.context.util;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Description：网络工具类
 *
 * @author chenq
 * @date 2018/7/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/12    chenq		2018/7/12		Create
 * </pre>
 */
public class NetUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetUtils.class);


    /**
     * 获取本机主机名
     *
     * @return
     */
    public static String getLocalHost() {

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            LOGGER.error("获取主机名称异常：", e);
        }
        return "";
    }


    /**
     * 获取本机ip地址
     *
     * @return
     */
    public static String getLocalAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr != null && addr instanceof Inet4Address && !addr.isLinkLocalAddress() && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            LOGGER.error("获取主机IP地址异常：", e);
        }
        return "";

    }


    public static String getRequestIp() {

        try {

            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes().getAttribute(
                    RequestContextListener.class.getName()
                            + ".REQUEST_ATTRIBUTES", 0);
            return getRequestIp(servletRequestAttributes.getRequest());
        } catch (Exception e) {
            LOGGER.warn("获取ip异常");
        }
        return "";
    }


    public static String getRequestIp(HttpServletRequest request) {
        String ip = null;
        ip = request.getHeader("x-forwarded-for");

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (StringUtils.isNotBlank(ip) && ip.indexOf(',') != -1) {
            String[] ips = ip.split(",");
            for (int i = 0; i < ips.length; i++) {
                if (null != ips[i] && !"unknown".equalsIgnoreCase(ips[i])) {
                    ip = ips[i];
                    break;
                }
            }
        }

        if (StringUtils.isNotBlank(ip) && ("0:0:0:0:0:0:1".equals(ip) || "0:0:0:0:0:0:0:1".equals(
                ip))) {//客户端访问地址使用localhost
            ip = NetUtils.getLocalAddress();
        }

        if ("unknown".equalsIgnoreCase(ip)) {
            LOGGER.warn(
                    "由于客户端通过Squid反向代理软件访问，无法获取客户端真实IP地址");
        }


        return ip;
    }

    public static int getServerPort() {
        int port = 80;
        try {
            MBeanServer server = null;
            if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
                server = MBeanServerFactory.findMBeanServer(null).get(0);
            }

            Set names = server.queryNames(new ObjectName("Catalina:type=Connector,*"), null);

            Iterator iterator = names.iterator();
            ObjectName name = null;
            while (iterator.hasNext()) {
                name = (ObjectName) iterator.next();
                String protocol = server.getAttribute(name, "protocol").toString();
                if (protocol.equals("HTTP/1.1")) {
                    return Integer.parseInt(server.getAttribute(name, "port").toString());
                }
            }
        } catch (Exception e) {
            LOGGER.warn("获取tomcat应用容器的服务端口失败：", e);
        }
        return port;
    }

    public static boolean ping(String ip, int port, Integer timeout) {
        boolean isConnect = false;
        try {
            TelnetClient client = new TelnetClient();
            client.setConnectTimeout(timeout == null ? 3000 : timeout);
            client.connect(ip, port);
            isConnect = true;
        } catch (Exception e) {
        }
        return isConnect;


    }


    public static String getMACAddress() {
        List<String> mac = getAllMACAddress();
        return CollectionUtils.isNotEmpty(mac) ? mac.get(0) : null;
    }

    public static List<String> getAllMACAddress() {
        List<String> macAddresses = Lists.newArrayList();
        Set<String> macAddressSet = Sets.newHashSet();
        try {
            java.lang.String os = System.getProperty("os.name");
            if (!os.toLowerCase().startsWith("win")) {
                java.lang.String[] cmd = {"ifconfig"};
                try {
                    Process process = Runtime.getRuntime().exec(cmd);
                    process.waitFor();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    java.lang.String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    java.lang.String str1 = sb.toString();
                    java.lang.String str2 = str1.split("ether")[1].trim();
                    String macAddress = str2.split("txqueuelen")[0].trim();
                    br.close();
                    // LOGGER.error("Linux macAddress=[{}]", macAddress);
                    System.out.println("ifconfig >> Linux MAC : " + macAddress);
                    macAddressSet.add(macAddress);
                } catch (Exception e) {
                    LOGGER.error("通过 ifconfig 命令获取 MAC 地址异常: ", e);
                    Process process = Runtime.getRuntime().exec("ip link show");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().startsWith("link/ether")) {
                            // 提取MAC地址
                            String[] parts = line.trim().split("\\s+");
                            if (parts.length >= 2) {
                                macAddressSet.add(parts[1]);
                                System.out.println("ip link show >> Linux MAC : " + parts[1]);
                                break;
                            }
                        }
                    }
                }

            } else {
                Process process = Runtime.getRuntime().exec("ipconfig /all");
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "GBK"));

                java.lang.String line;
                int index = -1;
                while ((line = br.readLine()) != null) {
                    index = line.toLowerCase().indexOf("物理地址");
                    if (index >= 0) {// 找到了
                        index = line.indexOf(":");
                        if (index >= 0) {
                            macAddressSet.add(line.substring(index + 1).trim());
                        }
                    }
                }
                br.close();
            }
        } catch (Exception e) {
            LOGGER.error("通过命令获取MAC地址失败：", e);
        }

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            StringBuilder sb = new StringBuilder();
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
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                    }
                    String macAddress = sb.toString();
                    if (StringUtils.isBlank(macAddress)) {
                        continue;
                    }
                    macAddressSet.add(macAddress);
                }
            }
        } catch (Exception e) {
            LOGGER.error("NetworkInterface.getNetworkInterfaces 获取MAC地址失败：", e);
        }
        macAddresses.addAll(macAddressSet);
        return macAddresses;
    }

}
