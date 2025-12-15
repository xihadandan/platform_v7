package org.bouncycastle2.pqc.crypto.newhope;

import org.bouncycastle2.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle2.util.Arrays;

public class NHPublicKeyParameters
    extends AsymmetricKeyParameter
{
    final byte[] pubData;

    public NHPublicKeyParameters(byte[] pubData)
    {
        super(false);
        this.pubData = Arrays.clone(pubData);
    }

    /**
     * Return the public key data.
     *
     * @return the public key values.
     */
    public byte[] getPubData()
    {
        return Arrays.clone(pubData);
    }
}
