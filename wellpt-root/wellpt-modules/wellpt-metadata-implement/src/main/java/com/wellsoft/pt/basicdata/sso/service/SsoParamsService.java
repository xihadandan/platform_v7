package com.wellsoft.pt.basicdata.sso.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.sso.entity.SsoParams;

import java.util.Collection;
import java.util.List;

public abstract interface SsoParamsService {

    public abstract SsoParams getBean(String uuid);

    public abstract void save(SsoParams paramSsoParams);

    public abstract void saveBean(SsoParams paramSsoParams);

    public abstract List<SsoParams> query(QueryInfo queryInfo);

    public List<SsoParams> queryBySYSID(String sysId);

    public abstract void remove(String paramString);

    public abstract void removeAll(Collection<String> paramCollection);
}
