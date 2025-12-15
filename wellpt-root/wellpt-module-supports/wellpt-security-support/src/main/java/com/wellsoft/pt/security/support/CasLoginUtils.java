package com.wellsoft.pt.security.support;

import com.wellsoft.context.config.Config;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: cas后台登录工具类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-10-3.1  zhengky	2014-10-3	  Create
 * </pre>
 * @date 2014-10-3
 */
@SuppressWarnings("all")
public class CasLoginUtils {

    public static final String KEY_CASTGC = "CASTGC";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String serverUrlConfig = "security.cas.url";
    private static final String isUseConfig = "security.cas.isuse";
    private static Logger LOG = LoggerFactory.getLogger(CasLoginUtils.class);

    /**
     * 是否启用cas
     *
     * @return
     */
    public static boolean isUseCas() {
        String iscas = Config.getValue(isUseConfig);
        if ("true".equals(iscas)) {
            return true;
        } else {
            return false;
        }
    }

    public static CasLoginResult login(String server, String username, String password) throws Exception {
        Cookie cookie = getTicketGrantingTicket(server, username, password);
        if (cookie != null) {
            return new CasLoginResult(cookie.getValue(), true);
        }

        return new CasLoginResult(null, false);
    }

    public static CasLoginResult login(String username, String password) throws Exception {
        if (isUseCas()) {
            String server = Config.getValue(serverUrlConfig) + "/cas/login";
            Cookie cookie = getTicketGrantingTicket(server, username, password);
            if (cookie != null) {
                return new CasLoginResult(cookie.getValue(), true);
            }
        }

        return new CasLoginResult(null, false);
    }

    private static Cookie getTicketGrantingTicket(final String server, final String username, final String password)
            throws IOException {
        CookieStore cookieStore = new BasicCookieStore();
        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpPost post = new HttpPost(server);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", username));
        nvps.add(new BasicNameValuePair("password", password));

        String[] params = doCasLoginRequest(client, server);
        nvps.add(new BasicNameValuePair("lt", params[0]));
        nvps.add(new BasicNameValuePair("execution", params[1]));
        nvps.add(new BasicNameValuePair("_eventId", "submit"));
        nvps.add(new BasicNameValuePair("submit", "LOGIN"));
        post.setEntity(new UrlEncodedFormEntity(nvps, DEFAULT_ENCODING));
        try {
            HttpResponse response = client.execute(post);
            Cookie cookie = getCookieValue(cookieStore, KEY_CASTGC);
            return cookie;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private static Cookie getCookieValue(CookieStore cookieStore, String name) {
        List<Cookie> cookies = cookieStore.getCookies();
        if (cookies.isEmpty()) {
            return null;
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                Cookie cookie = cookies.get(i);
                if (cookie.getName().equalsIgnoreCase(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private static String[] doCasLoginRequest(HttpClient httpclient, String url) throws IOException {
        String[] result = new String[2];
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStreamReader reader = new InputStreamReader(entity.getContent(), "UTF-8");
        BufferedReader rd = new BufferedReader(reader);
        String tempLine = rd.readLine();
        String slt = "<input type=\"hidden\" name=\"lt\" value=\"";
        String sexecution = "<input type=\"hidden\" name=\"execution\" value=\"";
        while (tempLine != null) {
            int index = tempLine.indexOf(slt);
            if (index != -1) {
                String s1 = tempLine.substring(index + slt.length());
                int index1 = s1.indexOf("\"");
                if (index1 != -1)
                    result[0] = s1.substring(0, index1);
            }
            int indexexecution = tempLine.indexOf(sexecution);
            if (indexexecution != -1) {
                String s2 = tempLine.substring(indexexecution + sexecution.length());
                int index2 = s2.indexOf("\"");
                if (index2 != -1)
                    result[1] = s2.substring(0, index2);
            }
            tempLine = rd.readLine();
        }
        IOUtils.closeQuietly(rd);
        IOUtils.closeQuietly(reader);
        return result;
    }

    public static final class CasLoginResult {
        private String ticket;
        private boolean success;

        private CasLoginResult(String ticket, boolean success) {
            this.ticket = ticket;
            this.success = success;
        }

        /**
         * @return the ticket
         */
        public String getTicket() {
            return ticket;
        }

        /**
         * @return the success
         */
        public boolean isSuccess() {
            return success;
        }

    }

}
