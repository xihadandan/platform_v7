package org.bouncycastle2.pqc.crypto.sphincs;

import org.bouncycastle2.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle2.util.Arrays;

public class SPHINCSPublicKeyParameters
    extends AsymmetricKeyParameter
{
    private final byte[] keyData;

    public SPHINCSPublicKeyParameters(byte[] keyData)
    {
        super(false);
        this.keyData = Arrays.clone(keyData);
    }

    public byte[] getKeyData()
    {
        return Arrays.clone(keyData);
    }
}
