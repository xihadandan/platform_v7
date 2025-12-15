package org.bouncycastle2.jcajce.provider.digest;

import org.bouncycastle2.crypto.CipherKeyGenerator;
import org.bouncycastle2.crypto.digests.RIPEMD320Digest;
import org.bouncycastle2.crypto.macs.HMac;
import org.bouncycastle2.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseMac;

public class RIPEMD320
{
    private RIPEMD320()
    {

    }

    static public class Digest
        extends BCMessageDigest
        implements Cloneable
    {
        public Digest()
        {
            super(new RIPEMD320Digest());
        }

        public Object clone()
            throws CloneNotSupportedException
        {
            Digest d = (Digest)super.clone();
            d.digest = new RIPEMD320Digest((RIPEMD320Digest)digest);

            return d;
        }
    }

    /**
     * RIPEMD320 HMac
     */
    public static class HashMac
        extends BaseMac
    {
        public HashMac()
        {
            super(new HMac(new RIPEMD320Digest()));
        }
    }

    public static class KeyGenerator
        extends BaseKeyGenerator
    {
        public KeyGenerator()
        {
            super("HMACRIPEMD320", 320, new CipherKeyGenerator());
        }
    }

    public static class Mappings
        extends DigestAlgorithmProvider
    {
        private static final String PREFIX = RIPEMD320.class.getName();

        public Mappings()
        {
        }

        public void configure(ConfigurableProvider provider)
        {
            provider.addAlgorithm("MessageDigest.RIPEMD320", PREFIX + "$Digest");

            addHMACAlgorithm(provider, "RIPEMD320", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
        }
    }
}
