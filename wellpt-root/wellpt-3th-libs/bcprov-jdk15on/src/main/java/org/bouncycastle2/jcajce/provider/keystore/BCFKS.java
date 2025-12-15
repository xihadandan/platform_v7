package org.bouncycastle2.jcajce.provider.keystore;

import org.bouncycastle2.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle2.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class BCFKS
{
    private static final String PREFIX = "org.bouncycastle2.jcajce.provider.keystore" + ".bcfks.";

    public static class Mappings
        extends AsymmetricAlgorithmProvider
    {
        public Mappings()
        {
        }

        public void configure(ConfigurableProvider provider)
        {
            provider.addAlgorithm("KeyStore.BCFKS", PREFIX + "BcFKSKeyStoreSpi$Std");
            provider.addAlgorithm("KeyStore.BCFKS-DEF", PREFIX + "BcFKSKeyStoreSpi$Def");

            provider.addAlgorithm("KeyStore.BCSFKS", PREFIX + "BcFKSKeyStoreSpi$StdShared");
            provider.addAlgorithm("KeyStore.BCSFKS-DEF", PREFIX + "BcFKSKeyStoreSpi$DefShared");
        }
    }
}
