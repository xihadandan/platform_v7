package com.wellsoft.pt.api.test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class LoginCasTest {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_ENCODING);
    final String server = "https://localhost:8443/cas/login";
    public String username = "ky";
    public String password = "111111";
    private HttpClient httpClient = HttpClients.createDefault();

    public static void main(String[] args) {
        try {
            new LoginCasTest().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String execute() throws Exception {
        Cookie cookie = getTicketGrantingTicket(server, username, password);
        convertToServletCookie(cookie);
        if (cookie != null) {
            return "SUCCESS";
        }
        return "SUCCESS";
    }

    private Cookie getTicketGrantingTicket(final String server, final String username, final String password)
            throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(server);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", username));
        nvps.add(new BasicNameValuePair("password", password));

        String[] params = doCasLoginRequest(client, server);
        nvps.add(new BasicNameValuePair("lt", params[0]));
        nvps.add(new BasicNameValuePair("execution", params[1]));
        nvps.add(new BasicNameValuePair("_eventId", "submit"));
        post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        try {
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                Cookie cookie = getCookieValue(client, "CASTGC");
                entity.consumeContent();
                return cookie;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Cookie getCookieValue(DefaultHttpClient httpclient, String name) {
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
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

    private String[] doCasLoginRequest(DefaultHttpClient httpclient, String url) throws IOException {
        String[] result = new String[2];
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
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

        if (entity != null) {
            entity.consumeContent();
        }
        return result;
    }

    private javax.servlet.http.Cookie convertToServletCookie(Cookie cookie) {
        javax.servlet.http.Cookie retCookie = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
        retCookie.setComment(cookie.getComment());
        retCookie.setDomain(cookie.getDomain());
        retCookie.setHttpOnly(false);
        retCookie.setSecure(false);
        retCookie.setPath(cookie.getPath());
        retCookie.setVersion(cookie.getVersion());
        retCookie.setValue(cookie.getValue());
        //retCookie.setMaxAge((int) ((cookie.getExpiryDate().getTime() - System.currentTimeMillis()) / 1000));
        return retCookie;
    }
}
