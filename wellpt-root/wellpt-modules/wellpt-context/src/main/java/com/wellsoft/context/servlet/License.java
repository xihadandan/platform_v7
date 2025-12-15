package com.wellsoft.context.servlet;

import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.NetUtils;
import com.wellsoft.context.util.ObfuscatedString;
import com.wellsoft.context.util.json.JsonUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.ClassUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年06月16日   chenq	 Create
 * </pre>
 */
public class License {

    public License() {
        this.verify();
    }

    private String decrypt(byte[] keyEncoded, byte[] cipherData) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        // 对称加密密钥对生成算法"AES"
        String ENCRYPTED_KEY_ALGORITHM = new ObfuscatedString(
                new long[]{0x73973A78D53F193CL,
                        0xFB4B1D5274762D3BL}).toString();
        // 密码器对称加密密钥加密解密算法"AES/CBC/ISO10126Padding"
        String ENCRYPTED_KEY_CIPHER_ALGORITHM = new ObfuscatedString(
                new long[]{0x5D6DBE12B11F2A5DL,
                        0xB412D2C3F0681267L, 0x8A84BCD1D07C90B2L, 0xBE649DFE9B8C883DL}).toString();
        // AES专用密钥
        SecretKeySpec key = new SecretKeySpec(keyEncoded, ENCRYPTED_KEY_ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(ENCRYPTED_KEY_CIPHER_ALGORITHM);
        // 初始化向量
        int ivLen = cipher.getBlockSize();
        byte[] ivBytes = new byte[ivLen];
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        // 初始化为加密模式的密码器
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        // 解密
        byte[] result = cipher.doFinal(cipherData);
        String licenseContent = new String(result);
        return licenseContent;
    }

    private PublicKey getPublicKey() {
        PublicKey publicKey = null;
        InputStream input = null;
        try {
            // JKS密钥库路径
            String jksPath = "/ca/publicCerts.store";
            // JKS密钥库密码
            String jksPassword = "ws123456";
            // 公钥别名
            String certAlias = "publiccert7";
            KeyStore keyStore = KeyStore.getInstance("JKS");
            URL url = License.class.getResource(jksPath);
            input = url.openStream();
            keyStore.load(input, jksPassword.toCharArray());
            publicKey = keyStore.getCertificate(certAlias).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
        return publicKey;
    }

    private void verify() {
        System.out.println("<<< 证书校验 >>>");
        try {
            // 读取许可证信息
            LicenseCertificate licenseCertificate = readLicenseCertificate();
            // 解密许可证内容
            String licenseContent = decrypt(licenseCertificate.getKeyEncoded(),
                    licenseCertificate.getCipherData());
            PublicKey publicKey = getPublicKey();
            String signatureAlgorithm = new String(
                    Base64.decodeBase64(licenseCertificate.getSignatureAlgorithmData()));
            byte[] signatureData = licenseCertificate.getSignedData();
            // 许可证验证
            boolean isVerify = verifySignature(licenseContent, signatureData, signatureAlgorithm, publicKey);
            if (isVerify) {
                Map<String, Object> licenseContentMap = JsonUtils.json2Object(licenseContent,
                        Map.class);
                String subject = licenseContentMap.get("subject").toString();
                boolean allowed = false;
                if (StringUtils.isNotBlank(subject)) {
                    String[] subjects = subject.split(",");
                    List<String> macAddress = NetUtils.getAllMACAddress();
                    for (String sj : subjects) {
                        for (String m : macAddress) {
                            if (sj.equalsIgnoreCase(m)) {
                                allowed = true;
                                break;
                            }
                        }
                        if (allowed) {
                            break;
                        }
                    }
                }

                // 认证服务
                Map<String, Object> extra = (Map<String, Object>) licenseContentMap.get("extra");
                String configServerUrl = Config.getValue("license.server.url");
                String serviceUrl = extra != null ? Objects.toString(extra.get("serviceUrl"), StringUtils.EMPTY) : StringUtils.EMPTY;
                if (!allowed && (StringUtils.isNotBlank(configServerUrl) || StringUtils.isNotBlank(serviceUrl))) {
                    try {
                        HttpClient httpClient = HttpClients.createDefault();
                        String serverUrl = StringUtils.defaultIfBlank(configServerUrl, serviceUrl) + "/license/verify/signature";
                        HttpPost post = new HttpPost(serverUrl);
                        post.setConfig(RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build());
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        String content = UUID.randomUUID().toString();
                        params.add(new BasicNameValuePair("content", content));
                        HttpEntity httpEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
                        post.setEntity(httpEntity);
                        HttpResponse response = httpClient.execute(post);
                        String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                        if (StringUtils.isNotBlank(responseBody)) {
                            Map<String, Object> results = JsonUtils.json2Object(responseBody, Map.class);
                            String signature = Objects.toString(results.get("signature"), StringUtils.EMPTY);
                            byte[] signedData = java.util.Base64.getDecoder().decode(signature.getBytes(StandardCharsets.UTF_8));
                            if (StringUtils.isNotBlank(signature)) {
                                allowed = verifySignature(content, signedData, signatureAlgorithm, publicKey);
                            }
                        }
                    } catch (Exception e) {
                        //  e.printStackTrace();
                    }
                }

                // 开发版本
                if (!allowed && extra != null && Config.ENV_DEV.equalsIgnoreCase(Objects.toString(extra.get("profile")))) {
                    verifyDevProfile(licenseContentMap);
                } else if (!allowed) {
                    System.err.println("启动中止，许可认证失败");
                    System.exit(0);
                } else {
                    licenseContentMap.put("validate", allowed);
                }
                Preferences preferences = Preferences.userNodeForPackage(License.class);
                preferences.putByteArray("content", JsonUtils.object2Json(licenseContentMap).getBytes(StandardCharsets.UTF_8));
            } else {
                System.err.println("启动中止，许可认证失败");
                System.exit(0);
            }
        } catch (UnsupportedOperationException ex) {
            System.err.println(ex.getMessage());
            System.err.println("启动中止，许可认证失败");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("启动中止，许可认证失败");
            System.exit(0);
        }

    }

    private void verifyDevProfile(Map<String, Object> licenseContentMap) throws UnsupportedEncodingException {
        Preferences preferences = Preferences.userNodeForPackage(License.class);
        String serialNumber = (String) licenseContentMap.get("serialNumber");
        Calendar calendar = Calendar.getInstance();
        Calendar installCalendar = Calendar.getInstance();
        byte[] profileBytes = preferences.getByteArray(serialNumber, null);
        Map<String, Object> installInfo = Maps.newHashMap();
        if (profileBytes == null) {
            long installTime = installCalendar.getTimeInMillis();
            licenseContentMap.put("installTime", installTime);
            installInfo.put("installTime", installTime);
        } else {
            installInfo = JsonUtils.json2Object(new String(profileBytes, StandardCharsets.UTF_8), Map.class);
            installCalendar.setTimeInMillis(Long.valueOf(Objects.toString(installInfo.get("installTime"))));
        }
        installCalendar.add(Calendar.DAY_OF_YEAR, 180);
        // 开发版半年有效期到期
        boolean expire = false;
        if (installCalendar.getTimeInMillis() < calendar.getTimeInMillis()) {
            installInfo.put("expire", true);
            expire = true;
        }
        preferences.put("serialNumber", serialNumber);
        preferences.putByteArray(serialNumber, JsonUtils.object2Json(installInfo).getBytes(StandardCharsets.UTF_8));

        // 开发版半年有效期到期
        if (expire) {
            System.err.println("开发版半年有效期到期，请从新申请许可证！");
            System.exit(0);
        } else {
            // 启动监控计时，有效运行期随机10~15天,提前2天提醒
            Random random = new Random();
            int runningDay = 10 + random.nextInt(10) % 6;
            Calendar timerCalendar = Calendar.getInstance();
            timerCalendar.add(Calendar.DAY_OF_YEAR, runningDay);
            Timer timer = new Timer();
            timer.schedule(new DevProfileTimerTask(runningDay), timerCalendar.getTime());
            preferences.putInt("runningDay", runningDay);
            preferences.putLong("startTime", Calendar.getInstance().getTimeInMillis());
        }
    }

    private boolean verifySignature(String licenseContent, byte[] signatureData, String signatureAlgorithm,
                                    PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(signatureAlgorithm);
        signature.initVerify(publicKey);
        signature.update(licenseContent.getBytes());
        boolean isVerify = signature.verify(signatureData);
        return isVerify;
    }

    private LicenseCertificate readLicenseCertificate() {
        LicenseCertificate licenseCertificate = new LicenseCertificate();
        DataInputStream input = null;
        try {
            URL licenseResource = ClassUtils.getDefaultClassLoader().getResource("../../license.lic");
            if (licenseResource == null) {
                licenseResource = ClassUtils.getDefaultClassLoader().getResource("/license.lic");
            }
            byte[] licenseData = IOUtils.toByteArray(licenseResource);
            if (licenseData == null) {
                throw new RuntimeException("许可授权信息不存在");
            }
            input = new DataInputStream(new ByteArrayInputStream(licenseData));
            // AES密钥数据
            int keyLength = input.readInt();
            byte[] keyEncoded = new byte[keyLength];
            input.readFully(keyEncoded);
            // license内容加密数据
            int cipherDataLength = input.readInt();
            byte[] cipherData = new byte[cipherDataLength];
            input.readFully(cipherData);
            // license内容签名算法base64编码数据
            int signatureAlgorithmLength = input.readInt();
            byte[] signatureAlgorithmData = new byte[signatureAlgorithmLength];
            input.readFully(signatureAlgorithmData);
            // license内容签名数据
            int signatureLength = input.readInt();
            byte[] signedData = new byte[signatureLength];
            input.readFully(signedData);

            licenseCertificate.setKeyEncoded(keyEncoded);
            licenseCertificate.setCipherData(cipherData);
            licenseCertificate.setSignatureAlgorithmData(signatureAlgorithmData);
            licenseCertificate.setSignedData(signedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
        return licenseCertificate;
    }


}
