package com.wellsoft.pt.servlet;

import com.wellsoft.context.util.NetUtils;
import com.wellsoft.context.util.ObfuscatedString;
import com.wellsoft.context.util.json.JsonUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
import java.net.URL;
import java.util.List;
import java.util.Map;

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

    // 对称加密密钥对生成算法"AES"
    public static String ENCRYPTED_KEY_ALGORITHM = new ObfuscatedString(
            new long[]{0x73973A78D53F193CL,
                    0xFB4B1D5274762D3BL}).toString();
    // 密码器对称加密密钥加密解密算法"AES/CBC/ISO10126Padding"
    public static String ENCRYPTED_KEY_CIPHER_ALGORITHM = new ObfuscatedString(
            new long[]{0x5D6DBE12B11F2A5DL,
                    0xB412D2C3F0681267L, 0x8A84BCD1D07C90B2L, 0xBE649DFE9B8C883DL}).toString();

    public License() {
        this.verify();
    }

    private String decrypt(byte[] keyEncoded, byte[] cipherData) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
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
            String certAlias = "publiccert";
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
                    System.err.println(macAddress);
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
                if (!allowed) {
                    System.err.println("启动中止，许可认证失败");
                    System.exit(0);
                }
            }
        } catch (UnsupportedOperationException ex) {
            System.err.println(ex.getMessage());
            System.err.println("启动中止，许可认证失败");
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("启动中止，许可认证失败");
            System.exit(0);
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
            byte[] licenseData = IOUtils.toByteArray(ClassUtils.getDefaultClassLoader().getResource("../../license.lic"));
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
