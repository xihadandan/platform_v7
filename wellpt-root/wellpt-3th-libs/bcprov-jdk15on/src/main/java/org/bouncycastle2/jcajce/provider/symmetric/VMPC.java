package org.bouncycastle2.jcajce.provider.symmetric;

import org.bouncycastle2.crypto.CipherKeyGenerator;
import org.bouncycastle2.crypto.engines.VMPCEngine;
import org.bouncycastle2.crypto.macs.VMPCMac;
import org.bouncycastle2.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle2.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle2.jcajce.provider.util.AlgorithmProvider;

public final class VMPC
{
    private VMPC()
    {
    }
    
    public static class Base
        extends BaseStreamCipher
    {
        public Base()
        {
            super(new VMPCEngine(), 16);
        }
    }

    public static class KeyGen
        extends BaseKeyGenerator
    {
        public KeyGen()
        {
            super("VMPC", 128, new CipherKeyGenerator());
        }
    }

    public static class Mac
        extends BaseMac
    {
        public Mac()
        {
            super(new VMPCMac());
        }
    }

    public static class Mappings
        extends AlgorithmProvider
    {
        private static final String PREFIX = VMPC.class.getName();

        public Mappings()
        {
        }

        public void configure(ConfigurableProvider provider)
        {

            provider.addAlgorithm("Cipher.VMPC", PREFIX + "$Base");
            provider.addAlgorithm("KeyGenerator.VMPC", PREFIX + "$KeyGen");
            provider.addAlgorithm("Mac.VMPCMAC", PREFIX + "$Mac");
            provider.addAlgorithm("Alg.Alias.Mac.VMPC", "VMPCMAC");
            provider.addAlgorithm("Alg.Alias.Mac.VMPC-MAC", "VMPCMAC");

        }
    }
}
