package com.wellsoft.pt.security.service.impl;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.bean.KeyStoreBean;
import com.wellsoft.pt.security.entity.KeyStoreEntity;
import com.wellsoft.pt.security.service.KeyStoreService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author zhongzh
 */
@Service
@Transactional
public class KeyStoreServiceImpl extends BaseServiceImpl implements
        KeyStoreService {

    @Override
    public Collection<KeyStoreBean> query(QueryInfo queryInfo) {
        String currentUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<KeyStoreEntity> ssoDetails = dao.findByExample(
                new KeyStoreEntity(), queryInfo.getPropertyFilters(),
                queryInfo.getOrderBy(), queryInfo.getPagingInfo());

        return null;
    }

    @Override
    public void addKeyStore(String file, String filename, String password,
                            String provider, String type, String pvtkeyPass) {

    }

    @Override
    public void addTrustStore(String file, String filename, String password,
                              String provider, String type) {

    }

    @Override
    public void remove(String uuid) {

    }

    @Override
    public void removeStore(String keyStoreName) {

    }

    @Override
    public void importCertToStore(String fileName, String fileData,
                                  String keyStoreName) {

    }

    @Override
    public List<String> getStoreEntries(String keyStoreName) {
        return null;
    }

    @Override
    public KeyStoreBean getKeystoreInfo(String keyStoreName) {
        return null;
    }

    @Override
    public void removeCertFromStore(String alias, String keyStoreName) {

    }

}
