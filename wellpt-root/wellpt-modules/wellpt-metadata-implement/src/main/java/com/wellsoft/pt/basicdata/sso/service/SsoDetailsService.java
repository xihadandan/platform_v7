package com.wellsoft.pt.basicdata.sso.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.sso.entity.Accounts;
import com.wellsoft.pt.basicdata.sso.entity.SsoDetails;

import java.util.Collection;
import java.util.List;

public abstract interface SsoDetailsService {
    public abstract SsoDetails getBean(String uuid);

    public SsoDetails getBySYSID(String sysId);

    public abstract void save(SsoDetails paramSsoDetails);

    public abstract String saveBean(SsoDetails paramSsoDetails);

    public abstract List<SsoDetails> query(QueryInfo paramQueryInfo);

    public abstract void remove(String paramString);

    public abstract void removeAll(Collection<String> paramCollection);

    public List<SsoDetails> querySsos();

    public Accounts ssoAuth(String sysId);

    public void saveAccount(String sysId, String userName, String passWord);
}
