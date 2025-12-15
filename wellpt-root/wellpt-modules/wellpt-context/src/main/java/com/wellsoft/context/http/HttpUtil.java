package com.wellsoft.context.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpUtil {

    // private static Logger logInf = Logger.getLogger("info");
    // private static Logger logErr = Logger.getLogger("error");
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private static final String HTTPS = "https";

    @SuppressWarnings("cast")
    public static String getUrlRetrunString(String p_url) {
        URL url = null;
        URLConnection conn = null;
        BufferedReader buffered = null;
        StringBuilder checkReturns = null;
        String checkNameReturn = "网络超时,或者丢失连接!";
        try {
            url = new URL(p_url);
            if (HTTPS.equals(url.getProtocol())) {
                SSLContext ctx = null;
                try {
                    ctx = SSLContext.getInstance("TLS");
                    ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
                } catch (Exception e) {
                    throw new IOException(e);
                }
                HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
                connHttps.setSSLSocketFactory(ctx.getSocketFactory());
                connHttps.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;// 默认都认证通过
                    }
                });
                conn = connHttps;
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn = url.openConnection();
            conn.setConnectTimeout(20000);
            buffered = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            checkReturns = new StringBuilder();
            String s = buffered.readLine();
            while (s != null) {
                checkReturns.append(s + "\n");
                s = buffered.readLine();
            }
            checkNameReturn = checkReturns.toString();
            // logInf.info("通过url:" + p_url + ",获取到得内容为:" + checkNameReturn);
        } catch (Exception e) {
            LOGGER.error("Could not open url: " + p_url, e);
        } finally {
            try {
                if (buffered != null) {
                    buffered.close();
                }
                if (conn != null) {
                    conn = null;
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return checkNameReturn;
    }

    public static File getUrlReturnFile(String p_url, String path) {
        URL url;
        OutputStream out = null;
        InputStream in = null;
        OutputStream os = null;
        HttpURLConnection httpconn = null;
        File file = null;
        try {
            url = new URL(p_url);
            httpconn = (HttpURLConnection) url.openConnection();
            httpconn.setRequestMethod("POST");
            httpconn.setDoOutput(true);
            out = httpconn.getOutputStream();
            out.write(p_url.getBytes());
            out.flush();
            httpconn.connect();

            in = httpconn.getInputStream();
            file = new File(path);
            os = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) > 0) {
                os.write(b, 0, len);
            }
            os.flush();
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (os != null) {
                    os.close();
                    os = null;
                }
                if (httpconn != null) {
                    httpconn.disconnect();
                    httpconn = null;
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return file;
    }

    public static String getInputStreamString(String p_url, String path) {
        URL url = null;
        URLConnection conn = null;
        BufferedReader buffered = null;
        StringBuilder checkReturns = null;
        String checkNameReturn = null;
        FileOutputStream os = null;
        InputStream in = null;
        try {
            url = new URL(p_url);
            conn = url.openConnection();
            in = conn.getInputStream();
            int streamSize = in.available();
            if (streamSize > 2) {
                File file = new File(path);
                os = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int len;
                while ((len = in.read(b)) > 0) {
                    os.write(b, 0, len);
                }
                os.flush();
                return path;
            } else {
                buffered = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                checkReturns = new StringBuilder();
                String s = buffered.readLine();
                while (s != null) {
                    checkReturns.append(s);
                    s = buffered.readLine();
                }
                checkNameReturn = checkReturns.toString();
                // logInf.info("通过url:" + p_url + ",获取到得内容为:" +
                // checkNameReturn);
                return checkNameReturn;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (buffered != null) {
                    buffered.close();
                }
                if (os != null) {
                    os.close();
                    os = null;
                }
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (conn != null) {
                    conn = null;
                }

            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return checkNameReturn;
    }

    public static String httpPost(String path, Map<String, String> param) throws IOException {
        /**
         * 首先要和URL下的URLConnection对话。 URLConnection可以很容易的从URL得到。比如： // Using
         *  java.net.URL and //java.net.URLConnection
         *
         *  使用页面发送请求的正常流程：在页面http://www.faircanton.com/message/loginlytebox.asp中输入用户名和密码，然后按登录，
         *  跳转到页面http://www.faircanton.com/message/check.asp进行验证
         *  验证的的结果返回到另一个页面
         *
         *  使用java程序发送请求的流程：使用URLConnection向http://www.faircanton.com/message/check.asp发送请求
         *  并传递两个参数：用户名和密码
         *  然后用程序获取验证结果
         */
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        /**
         * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
         * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
         */
        connection.setDoOutput(true);
        /**
         * 最后，为了得到OutputStream，简单起见，把它约束在Writer并且放入POST信息中，例如： ...
         */
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
        String paramStr = "";
        for (String key : param.keySet()) {
            paramStr += "&" + key + "=" + param.get(key);
        }
        paramStr = paramStr.replaceFirst("&", "");
        out.write(paramStr); //向页面传递数据。post的关键所在！
        // remember to clean up
        out.flush();
        out.close();
        /**
         * 这样就可以发送一个看起来象这样的POST：
         * POST /jobsearch/jobsearch.cgi HTTP 1.0 ACCEPT:
         * text/plain Content-type: application/x-www-form-urlencoded
         * Content-length: 99 username=bob password=someword
         */
        // 一旦发送成功，用以下方法就可以得到服务器的回应：
        String sCurrentLine;
        String sTotalString;
        sCurrentLine = "";
        sTotalString = "";
        InputStream l_urlStream;
        l_urlStream = connection.getInputStream();
        // 传说中的三层包装阿！
        BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
        while ((sCurrentLine = l_reader.readLine()) != null) {
            sTotalString += sCurrentLine + "/r/n";

        }
        return sTotalString;
    }

    /**
     * 获取网络图片的输入流
     *
     * @param urlPath
     * @return
     */
    public static InputStream getInputStream(String urlPath) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            // 创建url
            URL url = new URL(urlPath);
            if (null != url) {
                // 打开连接
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // 设置网络超时时间
                httpURLConnection.setConnectTimeout(10000);
                // 打开输入流
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                int responseCode = httpURLConnection.getResponseCode();
                if (200 == responseCode) {
                    inputStream = httpURLConnection.getInputStream();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    private static class DefaultTrustManager implements X509TrustManager {
        /**
         * 如何描述该构造方法
         */
        public DefaultTrustManager() {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

}
