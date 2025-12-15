package com.wellsoft.pt.basicdata.ldap.test;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DistinguishedName;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

public class LdapSSLTest {
    private static final Logger logger = LoggerFactory.getLogger(LdapSSLTest.class);

    public static void main(String[] args) {

        String keystore = "C:/Program Files/Java/jdk1.6.0_35/jre/lib/security/cacerts";
        System.setProperty("javax.net.ssl.trustStore", keystore);

        String userName = "oatest"; //用户名称

        String password = "oa0.123"; //密码

        //String host = "172.16.26.210";  //AD服务器

        String host = "tsrvlcpdc01";

        String port = "636"; //端口

        String domain = "@lcp.cn"; //邮箱的后缀名

        String url = new String("ldap://" + host + ":" + port);

        String user = userName.indexOf(domain) > -1 ? userName : userName + domain;

        Hashtable<String, String> env = new Hashtable<String, String>();

        DirContext ctx;

        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        env.put(Context.SECURITY_PRINCIPAL, user); //不带邮箱后缀名的话，会报错，具体原因还未探究。高手可以解释分享。

        env.put(Context.SECURITY_CREDENTIALS, password);

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

        env.put(Context.PROVIDER_URL, url);

        env.put(Context.SECURITY_PROTOCOL, "ssl");

        try {

            ctx = new InitialDirContext(env);

            //SSl协议修改密码 success
            String newPassword = "oa0.515";
            ModificationItem[] mods = new ModificationItem[1];
            String newQuotedPassword = "\"" + newPassword + "\"";
            byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd",
                    newUnicodePassword));
            ctx.modifyAttributes("cn=xzsp,ou=ou,dc=lcp,dc=cn", mods);
            //System.out.println("Reset Password for: " + userName);
            ctx.close();

            System.out.println("验证成功！");

        } catch (NamingException err) {
            logger.error(ExceptionUtils.getStackTrace(err));
        } catch (UnsupportedEncodingException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

		/*	//创建objectclass属性
		    Attribute objclass = new BasicAttribute("objectclass");
		    objclass.add("top");
		    objclass.add("organizationalunit");
		    //创建cn属性
		    Attribute cn = new BasicAttribute("ou", "Employee");
		    //创建Attributes，并添加objectclass和cn属性
		    Attributes attrs = new BasicAttributes();
		    attrs.put(objclass);
		    attrs.put(cn);
		    //将属性绑定到新的条目上，创建该条目
		    //dirContext.bind(ldapGroupDN, null, attrs);
		*/

    }

    /**
     * 得到dn
     *
     * @param cn
     * @return
     */
    private DistinguishedName getDn(String cn) {
        //得到根目录，也就是配置文件中配置的ldap的根目录
        DistinguishedName newContactDN = new DistinguishedName();
        // 添加cn，即使得该条记录的dn为"cn=cn,根目录",例如"cn=abc,dc=testdc,dc=com"
        //顺序很关键.
        newContactDN.add("dc", "cn");
        newContactDN.add("dc", "lcp");
        newContactDN.add("ou", "OU");
        //newContactDN.add("uid", "li");
        newContactDN.add("cn", cn);

        return newContactDN;
    }

}
