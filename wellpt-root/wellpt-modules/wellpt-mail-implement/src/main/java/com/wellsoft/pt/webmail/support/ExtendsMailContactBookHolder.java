package com.wellsoft.pt.webmail.support;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 扩展邮件联系人
 *
 * @author chenq
 * @date 2020/3/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/3/18    chenq		2020/3/18		Create
 * </pre>
 */
@Component
public class ExtendsMailContactBookHolder {
    @Autowired
    List<AbstractMailContackBookTreeDialogDataProvider> contackBookTreeDialogDataProviders;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<javax.mail.Address> getMailAddress(String id) {
        try {
            for (AbstractMailContackBookTreeDialogDataProvider provider : contackBookTreeDialogDataProviders) {
                if (provider.match(id)) {
                    return provider.explainMailAddress(id);
                }
            }
        } catch (Exception e) {
            logger.error("扩展邮箱通讯录解析地址异常：{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException("邮件地址解析异常");
        }
        return null;
    }

    public String getLocalId(String toSendId, String id) {
        if (id.contains(WmWebmailConstants.MAIL_SEPARATOR)) {
            //邮件地址
            return id;
        }

        try {
            for (AbstractMailContackBookTreeDialogDataProvider provider : contackBookTreeDialogDataProviders) {
                if (provider.match(toSendId)) {
                    return provider.getLocalId(id);
                }
            }
        } catch (Exception e) {
            logger.error("扩展邮箱通讯录解析地址异常：{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException("邮件地址解析异常");
        }
        return null;
    }

    public boolean isExtendsMailAddressId(String id) {
        try {
            for (AbstractMailContackBookTreeDialogDataProvider provider : contackBookTreeDialogDataProviders) {
                if (provider.match(id)) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("判断是否是外部邮件地址ID异常：{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException("判断是否是外部邮件地址ID异常");
        }
        return false;
    }

    public List<String> getLocalIds(String toSendId, String[] ids) {
        List<String> results = Lists.newArrayList();
        try {
            for (String i : ids) {
                String temp = getLocalId(toSendId, i);
                if (StringUtils.isNotBlank(temp)) {
                    results.add(temp);
                }
            }
            return results;
        } catch (Exception e) {
            logger.error("扩展邮箱通讯录解析地址异常：{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException("邮件地址解析异常");
        }
    }
}
