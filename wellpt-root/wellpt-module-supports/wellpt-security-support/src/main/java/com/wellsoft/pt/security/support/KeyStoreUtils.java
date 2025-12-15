package com.wellsoft.pt.security.support;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.security.bean.KeyStoreBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.List;

/**
 * @author zhongzh
 */
public abstract class KeyStoreUtils {
    protected static Logger LOG = LoggerFactory.getLogger(KeyStoreUtils.class);

    protected int tenantId;
    protected boolean includeCert = false;

    public KeyStoreUtils(int tenantId) {
        super();
        this.tenantId = tenantId;
    }

    public boolean isIncludeCert() {
        return includeCert;
    }

    public void setIncludeCert(boolean includeCert) {
        this.includeCert = includeCert;
    }

    public List<KeyStoreBean> getKeyStores(boolean isSuperTenant) {

        return null;
    }

    public void addKeyStoreWithFilePath(String filePath, String filename,
                                        String password, String provider, String type, String pvtkeyPass) {

    }

    public void addKeyStore(String fileData, String filename, String password,
                            String provider, String type, String pvtkeyPass) {

    }

    public void addKeyStore(byte[] content, String filename, String password,
                            String provider, String type, String pvtkeyPass) {

    }

    public void addTrustStore(String fileData, String filename,
                              String password, String provider, String type) {

    }

    public void addTrustStore(byte[] content, String filename, String password,
                              String provider, String type) {

    }

    public void deleteStore(String keyStoreName) {

    }

    public void importCertToStore(String fileName, String certData,
                                  String keyStoreName) {

    }

    public String importCertToStore(String certData, String keyStoreName) {

        return null;
    }

    public void removeCertFromStore(String alias, String keyStoreName) {

    }

    public List<String> getStoreEntries(String keyStoreName) {

        return null;
    }

    public KeyStoreBean getKeystoreInfo(String keyStoreName) {

        return null;
    }

    public Key getPrivateKey(String alias, boolean isSuperTenant) {

        return null;
    }

    public List<KeyStoreBean> query(QueryInfo paramQueryInfo) {

        return null;
    }

}
