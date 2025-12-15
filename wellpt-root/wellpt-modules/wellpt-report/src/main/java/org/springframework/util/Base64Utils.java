package org.springframework.util;

import org.springframework.security.crypto.codec.Base64;

import java.nio.charset.Charset;

/**
 * Description:Base64Utils在spring-core 4.x 才有，ureport2又需要改类，故直接引入该类
 *
 * @author chenq
 * @date 2018/10/20
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/20    chenq		2018/10/20		Create
 * </pre>
 */
public class Base64Utils {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final Base64Delegate delegate;

    static {
        Base64Delegate delegateToUse = null;

        if (ClassUtils.isPresent("java.util.Base64", Base64Utils.class.getClassLoader())) {
            delegateToUse = new JdkBase64Delegate();
        } else if (ClassUtils.isPresent("org.apache.commons.codec.binary.Base64",
                Base64Utils.class.getClassLoader()))
            delegateToUse = new CommonsCodecBase64Delegate();

        delegate = delegateToUse;
    }

    private static void assertSupported() {
        Assert.state(delegate != null,
                "Neither Java 8 nor Apache Commons Codec found - Base64 encoding not supported");
    }

    public static byte[] encode(byte[] src) {
        assertSupported();
        return delegate.encode(src);
    }

    public static String encodeToString(byte[] src) {
        assertSupported();
        if (src == null)
            return null;

        if (src.length == 0)
            return "";

        return new String(delegate.encode(src), DEFAULT_CHARSET);
    }

    public static byte[] decode(byte[] src) {
        assertSupported();
        return delegate.decode(src);
    }

    public static byte[] decodeFromString(String src) {
        assertSupported();
        if (src == null)
            return null;

        if (src.length() == 0)
            return new byte[0];

        return delegate.decode(src.getBytes(DEFAULT_CHARSET));
    }

    private static abstract interface Base64Delegate {
        public abstract byte[] encode(byte[] paramArrayOfByte);

        public abstract byte[] decode(byte[] paramArrayOfByte);
    }

    private static class CommonsCodecBase64Delegate
            implements Base64Utils.Base64Delegate {
        private final org.apache.commons.codec.binary.Base64 base64;

        private CommonsCodecBase64Delegate() {
            this.base64 = new org.apache.commons.codec.binary.Base64();
        }

        public byte[] encode(byte[] src) {
            return this.base64.encode(src);
        }

        public byte[] decode(byte[] src) {
            return this.base64.decode(src);
        }
    }

    private static class JdkBase64Delegate
            implements Base64Utils.Base64Delegate {

        public byte[] encode(byte[] src) {
            if ((src == null) || (src.length == 0))
                return src;


            return Base64.encode(src);
        }

        public byte[] decode(byte[] src) {
            if ((src == null) || (src.length == 0))
                return src;

            return Base64.decode(src);
        }
    }

}
