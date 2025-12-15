package com.wellsoft.pt.security.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.security.bean.KeyStoreBean;

import java.util.Collection;
import java.util.List;

public interface KeyStoreService {

    Collection<KeyStoreBean> query(QueryInfo paramQueryInfo);

    void addKeyStore(String file, String filename, String password,
                     String provider, String type, String pvtkeyPass);

    void addTrustStore(String file, String filename, String password,
                       String provider, String type);

    void remove(String uuid);

    void removeStore(String keyStoreName);

    void importCertToStore(String fileName, String fileData, String keyStoreName);

    List<String> getStoreEntries(String keyStoreName);

    KeyStoreBean getKeystoreInfo(String keyStoreName);

    void removeCertFromStore(String alias, String keyStoreName);
}
