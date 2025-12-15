package org.bouncycastle2.pqc.jcajce.provider;

import org.bouncycastle2.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle2.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle2.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle2.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle2.pqc.jcajce.provider.newhope.NHKeyFactorySpi;

public class NH
{
    private static final String PREFIX = "org.bouncycastle2.pqc.jcajce.provider" + ".newhope.";

    public static class Mappings
        extends AsymmetricAlgorithmProvider
    {
        public Mappings()
        {
        }

        public void configure(ConfigurableProvider provider)
        {
            provider.addAlgorithm("KeyFactory.NH", PREFIX + "NHKeyFactorySpi");
            provider.addAlgorithm("KeyPairGenerator.NH", PREFIX + "NHKeyPairGeneratorSpi");

            provider.addAlgorithm("KeyAgreement.NH", PREFIX + "KeyAgreementSpi");

            AsymmetricKeyInfoConverter keyFact = new NHKeyFactorySpi();

            registerOid(provider, PQCObjectIdentifiers.newHope, "NH", keyFact);
        }
    }
}
