package com.wellsoft.pt.integration.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

public class FtpPool extends Pool<FTPClient> {

    public FtpPool(Config poolConfig, String host, int port, String user, String password, String passiveModeConf) {
        super(poolConfig, new FtpPoolableObjectFactory(host, port, user, password, passiveModeConf));
    }

    public static void main(String[] args) throws Exception {
        GenericObjectPool.Config config = new Config();
        //最大池容量
        config.maxActive = 5;
        //从池中取对象达到最大时,继续创建新对象.
        config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;//GenericObjectPool.WHEN_EXHAUSTED_GROW;
        //池为空时取对象等待的最大毫秒数.
        config.maxWait = 5 * 60 * 1000;
        //取出对象时验证(此处设置成验证ftp是否处于连接状态).
        config.testOnBorrow = true;
        //还回对象时验证(此处设置成验证ftp是否处于连接状态).
        config.testOnReturn = false;
        FtpPool pool = new FtpPool(config, "XXXXXX", 21, "xxxxxx", "xxxxxx", "true");
        System.out.println("borrowSize1:" + pool.borrowSize());
        System.out.println("inPoolSize1:" + pool.inPoolSize());
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 8; i++) {
            FTPClient ftpClient = pool.getResource();
            System.out.println("ftpClient" + (i + 1) + " isConnected:" + ftpClient.isConnected());
            //ftpClient.disconnect();
            //pool.returnResource(ftpClient);
            pool.returnResource(ftpClient);
        }
        System.out.println("time:" + (System.currentTimeMillis() - begin));
        System.out.println("borrowSize2:" + pool.borrowSize());
        System.out.println("inPoolSize2:" + pool.inPoolSize());
        pool.destroy();
    }
}