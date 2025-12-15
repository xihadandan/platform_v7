package org.bouncycastle2.jcajce.provider.symmetric;

import org.bouncycastle2.crypto.CipherKeyGenerator;
import org.bouncycastle2.crypto.engines.XSalsa20Engine;
import org.bouncycastle2.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle2.jcajce.provider.util.AlgorithmProvider;

public final class XSalsa20
{
    private XSalsa20()
    {
    }
    
    public static class Base
        extends BaseStreamCipher
    {
        public Base()
        {
            super(new XSalsa20Engine(), 24);
        }
    }

    public static class KeyGen
        extends BaseKeyGenerator
    {
        public KeyGen()
        {
            super("XSalsa20", 256, new CipherKeyGenerator());
        }
    }

    public static class Mappings
        extends AlgorithmProvider
    {
        private static final String PREFIX = XSalsa20.class.getName();

        public Mappings()
        {
        }

        public void configure(ConfigurableProvider provider)
        {

            provider.addAlgorithm("Cipher.XSALSA20", PREFIX + "$Base");
            provider.addAlgorithm("KeyGenerator.XSALSA20", PREFIX + "$KeyGen");

        }
    }
}
