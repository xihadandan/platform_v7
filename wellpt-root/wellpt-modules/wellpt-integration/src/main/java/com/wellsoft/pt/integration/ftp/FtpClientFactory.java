package com.wellsoft.pt.integration.ftp;

import org.apache.commons.net.ftp.FTPClient;

public class FtpClientFactory {
    private FtpConfig readConfig = new FtpConfig();// read config
    private FtpConfig writeConfig = new FtpConfig();// write config

    public FtpClientFactory() {
    }

    public FTPClient getRead() {
        return new FTPClient();
    }

    public FTPClient getRead(FtpConfig readConfig) {
        if (this.readConfig == null) {
            synchronized (this.readConfig) {
                if (this.readConfig == null && readConfig != null) {
                    this.readConfig = readConfig;
                }
            }
        }
        return getRead();
    }

    public FTPClient getRead(String server, String username, String password) {
        if (readConfig == null) {
            synchronized (readConfig) {
                if (readConfig == null || readConfig.getServer() == null) {
                    readConfig.setServer(server);
                    readConfig.setUsername(username);
                    readConfig.setPassword(password);
                }
            }
        }
        return getRead();
    }

    public FTPClient getRead(String server, int port, String username, String password) {
        if (readConfig == null) {
            synchronized (readConfig) {
                if (readConfig == null || readConfig.getServer() == null) {
                    readConfig.setPort(port);
                    readConfig.setServer(server);
                    readConfig.setUsername(username);
                    readConfig.setPassword(password);
                }
            }
        }
        return getRead();
    }

    public FTPClient getWrite() {
        return new FTPClient();
    }

    public FTPClient getWrite(FtpConfig writeConfig) {
        if (this.writeConfig == null) {
            synchronized (this.writeConfig) {
                if (this.writeConfig == null && writeConfig != null) {
                    this.writeConfig = writeConfig;
                }
            }
        }
        return getWrite();
    }

    public FTPClient getWrite(String server, String username, String password) {
        if (writeConfig == null) {
            synchronized (writeConfig) {
                if (writeConfig == null || writeConfig.getServer() == null) {
                    writeConfig.setServer(server);
                    writeConfig.setUsername(username);
                    writeConfig.setPassword(password);
                }
            }
        }
        return getWrite();
    }

    public FTPClient getWrite(String server, int port, String username, String password) {
        if (writeConfig == null) {
            synchronized (writeConfig) {
                if (writeConfig == null || writeConfig.getServer() == null) {
                    writeConfig.setPort(port);
                    writeConfig.setServer(server);
                    writeConfig.setUsername(username);
                    writeConfig.setPassword(password);
                }
            }
        }
        return getWrite();
    }
}
