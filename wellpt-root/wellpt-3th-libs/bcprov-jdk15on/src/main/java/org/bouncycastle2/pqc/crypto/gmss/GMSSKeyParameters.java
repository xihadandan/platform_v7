package org.bouncycastle2.pqc.crypto.gmss;

import org.bouncycastle2.crypto.params.AsymmetricKeyParameter;

public class GMSSKeyParameters
    extends AsymmetricKeyParameter
{
    private GMSSParameters params;

    public GMSSKeyParameters(
        boolean isPrivate,
        GMSSParameters params)
    {
        super(isPrivate);
        this.params = params;
    }

    public GMSSParameters getParameters()
    {
        return params;
    }
}