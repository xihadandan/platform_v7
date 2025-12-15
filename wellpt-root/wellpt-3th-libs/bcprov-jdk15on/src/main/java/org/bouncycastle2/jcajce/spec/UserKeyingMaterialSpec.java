package org.bouncycastle2.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

import org.bouncycastle2.util.Arrays;

public class UserKeyingMaterialSpec
    implements AlgorithmParameterSpec
{
    private final byte[] userKeyingMaterial;

    public UserKeyingMaterialSpec(byte[] userKeyingMaterial)
    {
        this.userKeyingMaterial = Arrays.clone(userKeyingMaterial);
    }

    public byte[] getUserKeyingMaterial()
    {
        return Arrays.clone(userKeyingMaterial);
    }
}
