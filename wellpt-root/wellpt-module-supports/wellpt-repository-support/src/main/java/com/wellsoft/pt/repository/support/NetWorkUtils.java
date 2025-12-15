package com.wellsoft.pt.repository.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class NetWorkUtils {
    static Logger logger = LoggerFactory.getLogger(NetWorkUtils.class);

    /**
     * 通过本地的一个网络入口连接远程网络
     *
     * @param localInetAddr
     * @param remoteInetAddr
     * @param port
     * @param timeout
     * @return
     */
    public static boolean isReachable(InetAddress localInetAddr, InetAddress remoteInetAddr, int port, int timeout) {

        boolean isReachable = false;
        Socket socket = null;
        try {
            socket = new Socket();

            // 端口号设置为 0 表示在本地挑选一个可用端口进行连接

            SocketAddress localSocketAddr = new InetSocketAddress(localInetAddr, 0);
            socket.bind(localSocketAddr);
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(remoteInetAddr, port);
            socket.connect(endpointSocketAddr, timeout);
            logger.error("SUCCESS - connection established! Local: " + localInetAddr.getHostAddress() + " remote: "
                    + remoteInetAddr.getHostAddress() + " port" + port);
            isReachable = true;
        } catch (IOException e) {
            logger.error("FAILRE - CAN not connect! Local: " + localInetAddr.getHostAddress() + " remote: "
                    + remoteInetAddr.getHostAddress() + " port" + port, e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Error occurred while closing socket..", e);
                }
            }
        }
        return isReachable;
    }

    /**
     * 使用当前网络连接接远程网络
     *
     * @param remoteInetAddr
     * @param port
     * @param timeout
     * @return
     */
    public static boolean isReachable(InetAddress remoteInetAddr, int port, int timeout) {

        boolean isReachable = false;
        Socket socket = null;
        try {
            socket = new Socket();

            InetSocketAddress endpointSocketAddr = new InetSocketAddress(remoteInetAddr, port);
            socket.connect(endpointSocketAddr, timeout);
            logger.error("SUCCESS - connection established! " + " remote: " + remoteInetAddr.getHostAddress() + " port"
                    + port);
            isReachable = true;
        } catch (IOException e) {
            logger.error("FAILRE - CAN not connect!   remote: " + remoteInetAddr.getHostAddress() + " port" + port, e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Error occurred while closing socket..", e);
                }
            }
        }
        return isReachable;
    }
}
