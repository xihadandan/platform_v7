package org.bouncycastle2.jcajce.provider.symmetric;

import org.bouncycastle2.crypto.CipherKeyGenerator;
import org.bouncycastle2.crypto.engines.VMPCKSA3Engine;
import org.bouncycastle2.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle2.jcajce.provider.util.AlgorithmProvider;

public final class VMPCKSA3
{
    private VMPCKSA3()
    {
    }
    
    public static class Base
        extends BaseStreamCipher
    {
        public Base()
        {
            super(new VMPCKSA3Engine(), 16);
        }
    }

    public static class KeyGen
        extends BaseKeyGenerator
    {
        public KeyGen()
        {
            super("VMPC-KSA3", 128, new CipherKeyGenerator());
        }
    }

    public static class Mappings
        extends AlgorithmProvider
    {
        private static final String PREFIX = VMPCKSA3.class.getName();

        public Mappings()
        {
        }

        public void configure(ConfigurableProvider provider)
        {

            provider.addAlgorithm("Cipher.VMPC-KSA3", PREFIX + "$Base");
            provider.addAlgorithm("KeyGenerator.VMPC-KSA3", PREFIX + "$KeyGen");

        }
    }
}
