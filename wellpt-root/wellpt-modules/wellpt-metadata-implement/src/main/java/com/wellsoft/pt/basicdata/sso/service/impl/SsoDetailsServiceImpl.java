package com.wellsoft.pt.basicdata.sso.service.impl;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.sso.bean.MsgBean;
import com.wellsoft.pt.basicdata.sso.dao.AccountsDao;
import com.wellsoft.pt.basicdata.sso.dao.SsoDetailsDao;
import com.wellsoft.pt.basicdata.sso.dao.SsoParamsDao;
import com.wellsoft.pt.basicdata.sso.entity.Accounts;
import com.wellsoft.pt.basicdata.sso.entity.SsoDetails;
import com.wellsoft.pt.basicdata.sso.service.SsoDetailsService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class SsoDetailsServiceImpl extends BaseServiceImpl implements SsoDetailsService {
    @Autowired
    private SsoDetailsDao ssoDetailsDao;

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private SsoParamsDao ssoParamsDao;

    @Override
    public SsoDetails getBean(String uuid) {
        SsoDetails ssoDetails = ssoDetailsDao.get(uuid);
        SsoDetails bean = new SsoDetails();
        BeanUtils.copyProperties(ssoDetails, bean);
        return bean;
    }

    @Override
    public SsoDetails getBySYSID(String sysId) {
        SsoDetails ssoDetails = ssoDetailsDao.getBySYSID(sysId);
        SsoDetails bean = new SsoDetails();
        BeanUtils.copyProperties(ssoDetails, bean);
        return bean;
    }

    @Override
    public void save(SsoDetails ssoDetails) {
        ssoDetailsDao.save(ssoDetails);
    }

    @Override
    public String saveBean(SsoDetails bean) {
        SsoDetails ssoDetails = new SsoDetails();
        MsgBean msgBean = new MsgBean();
        if (StringUtils.isNotBlank(bean.getUuid())) {// 更新操作
            ssoDetails = ssoDetailsDao.get(bean.getUuid());
            if (ssoDetailsDao.exist(bean.getUuid(), bean.getSysId())) {// 更新检测SYSID
                msgBean.setCode(-1);
                msgBean.setMessage("SYSID已存在");
                return JsonUtils.object2Json(msgBean);
            }
            // TODO 更新关联SYSID
            if (!bean.getSysId().equalsIgnoreCase(ssoDetails.getSysId())) {//SYSID有更新，则删除之前的关联数据
                accountsDao.deleteBySYSID(bean.getSysId());
                ssoParamsDao.deleteBySYSID(bean.getSysId());
            }
        } else { // 新增操作
            if (ssoDetailsDao.existSysId(bean.getSysId())) { // 新增检测SYSID
                msgBean.setCode(-1);
                msgBean.setMessage("SYSID已存在");
                return JsonUtils.object2Json(msgBean);
            }
        }
        BeanUtils.copyProperties(bean, ssoDetails);// source target
        ssoDetailsDao.save(ssoDetails);
        msgBean.setCode(0);
        msgBean.setMessage("配置保存成功");
        return JsonUtils.object2Json(msgBean);
    }

    @Override
    public List<SsoDetails> query(QueryInfo queryInfo) {
        List<SsoDetails> ssoDetails = this.ssoDetailsDao.findByExample(new SsoDetails(),
                queryInfo.getPropertyFilters(), queryInfo.getOrderBy(), queryInfo.getPagingInfo());
        return ssoDetails;
    }

    @Override
    public void remove(String uuid) {
        SsoDetails ssoDetails = ssoDetailsDao.get(uuid);
        // 删除账户相关信息，保持数据一致性
        accountsDao.deleteBySYSID(ssoDetails.getSysId());
        ssoParamsDao.deleteBySYSID(ssoDetails.getSysId());
        ssoDetailsDao.delete(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            SsoDetails ssoDetails = ssoDetailsDao.get(uuid);
            // 删除账户相关信息，保持数据一致性
            accountsDao.deleteBySYSID(ssoDetails.getSysId());
            ssoParamsDao.deleteBySYSID(ssoDetails.getSysId());
            ssoDetailsDao.delete(uuid);
        }
    }

    /**
     * 获取快速登录系统列表
     *
     * @see com.wellsoft.pt.basicdata.sso.service.SsoDetailsService#querySsos(java.lang.String)
     */
    @Override
    public List<SsoDetails> querySsos() {
        // UserDetails user = SpringSecurityUtils.getCurrentUser();
        // String userId = user.getUserId();
        return ssoDetailsDao.getAll();
    }

    /**
     * 判断该用户是否具有进入单点登录的权限
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.app.xzsp.service.XZSPService#isGoInOldSystem()
     */
    @Override
    public Accounts ssoAuth(String sysId) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        Accounts accounts = accountsDao.findAccount(userId, sysId);
        return accounts;
    }

    /**
     * 如何描述该方法
     */
    @Override
    public void saveAccount(String sysId, String userName, String passWord) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        Accounts accounts = accountsDao.findAccount(userId, sysId);
        if (accounts == null || accounts.getUuid() == null) {
            accounts = new Accounts();
            accounts.setUserId(userId);
            accounts.setSysId(sysId);
            accounts.setUserName(userName);
            accounts.setPassWord(passWord);
        } else {
            accounts.setUserName(userName);
            accounts.setPassWord(passWord);
        }
        accountsDao.save(accounts);
    }
}
