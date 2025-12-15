package com.wellsoft.pt.repository.support;

import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Properties;

class TrackerIpPortTransformClient extends TrackerClient {

    private final static Logger logger = LoggerFactory.getLogger(TrackerIpPortTransformClient.class);

    final String S_IP_MAPPING_KEY = "fastdfs.storage_server.ip.mapping";
    final String S_PORT_MAPPING_KEY = "fastdfs.storage_server.port.mapping";

    Properties properties;

    public TrackerIpPortTransformClient(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected ServerInfo[] getStorages(TrackerServer trackerServer, byte cmd, String groupName, String filename)
            throws IOException {
        byte[] header;
        byte[] bFileName;
        byte[] bGroupName;
        byte[] bs;
        int len;
        String ip_addr;
        int port;
        boolean bNewConnection;
        Socket trackerSocket;

        if (trackerServer == null) {
            trackerServer = getConnection();
            if (trackerServer == null) {
                return null;
            }
            bNewConnection = true;
        } else {
            bNewConnection = false;
        }
        trackerSocket = trackerServer.getSocket();
        OutputStream out = trackerSocket.getOutputStream();

        try {
            bs = groupName.getBytes(ClientGlobal.g_charset);
            bGroupName = new byte[ProtoCommon.FDFS_GROUP_NAME_MAX_LEN];
            bFileName = filename.getBytes(ClientGlobal.g_charset);

            if (bs.length <= ProtoCommon.FDFS_GROUP_NAME_MAX_LEN) {
                len = bs.length;
            } else {
                len = ProtoCommon.FDFS_GROUP_NAME_MAX_LEN;
            }
            Arrays.fill(bGroupName, (byte) 0);
            System.arraycopy(bs, 0, bGroupName, 0, len);

            header = ProtoCommon.packHeader(cmd, ProtoCommon.FDFS_GROUP_NAME_MAX_LEN + bFileName.length, (byte) 0);
            byte[] wholePkg = new byte[header.length + bGroupName.length + bFileName.length];
            System.arraycopy(header, 0, wholePkg, 0, header.length);
            System.arraycopy(bGroupName, 0, wholePkg, header.length, bGroupName.length);
            System.arraycopy(bFileName, 0, wholePkg, header.length + bGroupName.length, bFileName.length);
            out.write(wholePkg);

            ProtoCommon.RecvPackageInfo pkgInfo = ProtoCommon.recvPackage(trackerSocket.getInputStream(),
                    ProtoCommon.TRACKER_PROTO_CMD_RESP, -1);
            this.errno = pkgInfo.errno;
            if (pkgInfo.errno != 0) {
                return null;
            }

            if (pkgInfo.body.length < ProtoCommon.TRACKER_QUERY_STORAGE_FETCH_BODY_LEN) {
                throw new IOException("Invalid body length: " + pkgInfo.body.length);
            }

            if ((pkgInfo.body.length - ProtoCommon.TRACKER_QUERY_STORAGE_FETCH_BODY_LEN)
                    % (ProtoCommon.FDFS_IPADDR_SIZE - 1) != 0) {
                throw new IOException("Invalid body length: " + pkgInfo.body.length);
            }

            int server_count = 1 + (pkgInfo.body.length - ProtoCommon.TRACKER_QUERY_STORAGE_FETCH_BODY_LEN)
                    / (ProtoCommon.FDFS_IPADDR_SIZE - 1);

            ip_addr = new String(pkgInfo.body, ProtoCommon.FDFS_GROUP_NAME_MAX_LEN, ProtoCommon.FDFS_IPADDR_SIZE - 1)
                    .trim();
            ip_addr = ipInOutTransform(ip_addr);

            int offset = ProtoCommon.FDFS_GROUP_NAME_MAX_LEN + ProtoCommon.FDFS_IPADDR_SIZE - 1;

            port = (int) ProtoCommon.buff2long(pkgInfo.body, offset);
            port = portInOutTransform(port);
            offset += ProtoCommon.FDFS_PROTO_PKG_LEN_SIZE;

            ServerInfo[] servers = new ServerInfo[server_count];
            servers[0] = new ServerInfo(ip_addr, port);
            for (int i = 1; i < server_count; i++) {
                servers[i] = new ServerInfo(new String(pkgInfo.body, offset, ProtoCommon.FDFS_IPADDR_SIZE - 1).trim(),
                        port);
                offset += ProtoCommon.FDFS_IPADDR_SIZE - 1;
            }

            return servers;
        } catch (IOException ex) {
            if (!bNewConnection) {
                try {
                    trackerServer.close();
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                }
            }

            throw ex;
        } finally {
            if (bNewConnection) {
                try {
                    trackerServer.close();
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                }
            }
        }
    }

    @Override
    public StorageServer getStoreStorage(TrackerServer trackerServer, String groupName) throws IOException {
        byte[] header;
        String ip_addr = null;
        int port = 0;
        byte cmd;
        int out_len;
        boolean bNewConnection;
        byte store_path;
        Socket trackerSocket;

        if (trackerServer == null) {
            trackerServer = getConnection();
            if (trackerServer == null) {
                return null;
            }
            bNewConnection = true;
        } else {
            bNewConnection = false;
        }

        trackerSocket = trackerServer.getSocket();
        OutputStream out = trackerSocket.getOutputStream();

        try {
            if (groupName == null || groupName.length() == 0) {
                cmd = ProtoCommon.TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITHOUT_GROUP_ONE;
                out_len = 0;
            } else {
                cmd = ProtoCommon.TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITH_GROUP_ONE;
                out_len = ProtoCommon.FDFS_GROUP_NAME_MAX_LEN;
            }
            header = ProtoCommon.packHeader(cmd, out_len, (byte) 0);
            out.write(header);

            if (groupName != null && groupName.length() > 0) {
                byte[] bGroupName;
                byte[] bs;
                int group_len;

                bs = groupName.getBytes(ClientGlobal.g_charset);
                bGroupName = new byte[ProtoCommon.FDFS_GROUP_NAME_MAX_LEN];

                if (bs.length <= ProtoCommon.FDFS_GROUP_NAME_MAX_LEN) {
                    group_len = bs.length;
                } else {
                    group_len = ProtoCommon.FDFS_GROUP_NAME_MAX_LEN;
                }
                Arrays.fill(bGroupName, (byte) 0);
                System.arraycopy(bs, 0, bGroupName, 0, group_len);
                out.write(bGroupName);
            }

            ProtoCommon.RecvPackageInfo pkgInfo = ProtoCommon.recvPackage(trackerSocket.getInputStream(),
                    ProtoCommon.TRACKER_PROTO_CMD_RESP, ProtoCommon.TRACKER_QUERY_STORAGE_STORE_BODY_LEN);
            this.errno = pkgInfo.errno;
            if (pkgInfo.errno != 0) {
                return null;
            }

            ip_addr = new String(pkgInfo.body, ProtoCommon.FDFS_GROUP_NAME_MAX_LEN, ProtoCommon.FDFS_IPADDR_SIZE - 1)
                    .trim();
            ip_addr = ipInOutTransform(ip_addr);
            port = (int) ProtoCommon.buff2long(pkgInfo.body, ProtoCommon.FDFS_GROUP_NAME_MAX_LEN
                    + ProtoCommon.FDFS_IPADDR_SIZE - 1);
            port = portInOutTransform(port);
            store_path = pkgInfo.body[ProtoCommon.TRACKER_QUERY_STORAGE_STORE_BODY_LEN - 1];

            return new StorageServer(ip_addr, port, store_path);
        } catch (SocketTimeoutException ex) {
            logger.error("SocketTimeoutException ip_addr[" + ip_addr + "]port[" + port + "]");
            throw ex;
        } catch (IOException ex) {
            if (!bNewConnection) {
                try {
                    trackerServer.close();
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                }
            }
            throw ex;
        } finally {
            if (bNewConnection) {
                try {
                    trackerServer.close();
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                }
            }
        }
    }

    private int portInOutTransform(int port) {

        //处理内外端口映射转换
        if (StringUtils.isNotBlank(this.properties.getProperty(S_PORT_MAPPING_KEY))) {
            String[] portouts = this.properties.getProperty(S_PORT_MAPPING_KEY).split(",");
            for (String ports : portouts) {
                String[] mappings = ports.split(":");
                if (mappings.length == 2 && Integer.parseInt(mappings[0]) == port) {
                    return Integer.parseInt(mappings[1]);
                }
            }
        }

        return port;
    }

    private String ipInOutTransform(String ipAddr) {
        //处理内外Ip映射转换
        if (StringUtils.isNotBlank(this.properties.getProperty(S_IP_MAPPING_KEY))) {
            String[] ipouts = this.properties.getProperty(S_IP_MAPPING_KEY).split(",");
            for (String ips : ipouts) {
                String[] mappings = ips.split(":");
                if (mappings.length == 2 && ipAddr.indexOf(mappings[0]) != -1) {
                    return mappings[1];
                }
            }
        }

        return ipAddr;
    }

}