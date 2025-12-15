package com.wellsoft.pt.webmail.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tebie.applib.api.APIContext;
import tebie.applib.api.IClient;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Description:
 *
 * @author chenq
 * @date 2020/1/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/1/15    chenq		2020/1/15		Create
 * </pre>
 */
public class SyncCoremailUserCallable implements Callable<Boolean> {

    private String mailConfUuid;
    private String defaultPwd;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SyncCoremailUserCallable(String mailConfUuid, String defaultPwd) {
        this.mailConfUuid = mailConfUuid;
        this.defaultPwd = defaultPwd;
    }

    @Override
    public Boolean call() throws Exception {
        WmMailConfigService wmMailConfigService = ApplicationContextHolder.getBean(
                WmMailConfigService.class);
        WmMailConfigEntity wmMailConfig = wmMailConfigService.getOne(this.mailConfUuid);
        if (wmMailConfig != null) {
            //FIXME:添加、同步邮件用户涉及到邮件密码，如果一方修改则可能导致邮件无法通过javamail发送，需要通过手动修改用户的客户端密码
            IClient client = null;
            APIContext ret = null;
            try {
                Socket socket = new Socket(wmMailConfig.getPop3Server(),
                        wmMailConfig.getApiPort());
                client = APIContext.getClient(socket);
                List<MultiOrgUserAccount> list = ApplicationContextHolder.getBean(
                        OrgApiFacade.class)
                        .queryAllAccountOfUnit(wmMailConfig.getSystemUnitId());
                String defaultAttrs = "domain_name=" + wmMailConfig.getDomain() + "&user_status=0&cos_id=1&quota_delta=0&password=" + this.defaultPwd;
                for (MultiOrgUserAccount user : list) {
                    try {
                        ret = client.userExist(
                                user.getLoginNameLowerCase() + "@" + wmMailConfig.getDomain());
                        if (APIContext.RC_USER_NOT_FOUND == ret.getRetCode()) {
                            ret = client.createUser("1", wmMailConfig.getSystemUnitId(),
                                    user.getLoginNameLowerCase(),
                                    defaultAttrs);
                            logger.info("创建Coremail邮件用户结果反馈 -> code={}, msg={}", ret.getRetCode(),
                                    ret.getResult());
                        }
                    } catch (Exception e) {
                        logger.error("对接coremail客户端，创建邮件用户{}异常：", user.getLoginName(), e);
                    }
                }


            } catch (Exception e) {
                logger.error("对接coremail客户端，创建邮件用户异常：", e);
            } finally {
                if (client != null)
                    client.close();
            }
        }
        return null;
    }
}
