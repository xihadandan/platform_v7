package com.wellsoft.pt.basicdata.sso.service.impl;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.sso.dao.SsoParamsDao;
import com.wellsoft.pt.basicdata.sso.entity.SsoParams;
import com.wellsoft.pt.basicdata.sso.service.SsoParamsService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class SsoParamsServiceImpl extends BaseServiceImpl implements SsoParamsService {

    @Autowired
    private SsoParamsDao ssoParamsDao;

    @Override
    public SsoParams getBean(String uuid) {
        SsoParams ssoParam = ssoParamsDao.get(uuid);
        SsoParams bean = new SsoParams();
        BeanUtils.copyProperties(ssoParam, bean);
        return bean;
    }

    @Override
    public void save(SsoParams bean) {
        ssoParamsDao.save(bean);
    }

    @Override
    public void saveBean(SsoParams bean) {
        SsoParams ssoParam = new SsoParams();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            ssoParam = ssoParamsDao.get(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, ssoParam);
        ssoParamsDao.save(ssoParam);
    }

    @Override
    public List<SsoParams> query(QueryInfo queryInfo) {
        List<SsoParams> ssoParams = this.ssoParamsDao.findByExample(new SsoParams(), queryInfo.getPropertyFilters(),
                queryInfo.getOrderBy(), queryInfo.getPagingInfo());
        return ssoParams;
    }

    @Override
    public List<SsoParams> queryBySYSID(String sysId) {
        List<SsoParams> ssoParams = ssoParamsDao.queryBySYSID(sysId);
        return ssoParams;
    }

    @Override
    public void remove(String uuid) {
        ssoParamsDao.delete(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            ssoParamsDao.delete(uuid);
        }
    }
}
