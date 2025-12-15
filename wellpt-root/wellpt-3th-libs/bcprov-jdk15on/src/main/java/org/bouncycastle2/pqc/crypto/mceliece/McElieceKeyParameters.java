package org.bouncycastle2.pqc.crypto.mceliece;

import org.bouncycastle2.crypto.params.AsymmetricKeyParameter;


public class McElieceKeyParameters
    extends AsymmetricKeyParameter
{
    private McElieceParameters params;

    public McElieceKeyParameters(
        boolean isPrivate,
        McElieceParameters params)
    {
        super(isPrivate);
        this.params = params;
    }


    public McElieceParameters getParameters()
    {
        return params;
    }

}
